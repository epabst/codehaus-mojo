package org.codehaus.mojo.dbupgrade.generic;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.mojo.dbupgrade.DBUpgradeException;
import org.codehaus.mojo.dbupgrade.DBUpgradeLifecycle;
import org.codehaus.mojo.dbupgrade.sqlexec.DefaultSQLExec;

/*
 * Copyright 2000-2010 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * This class hooks up your global pre-upgrade, incremental upgrades, and finally global post-upgrade using both java and SQL 
 * files through java resources. Each incremental upgrade has an associate version number to be stored in a configurable
 * database version table. DBUpgrade uses database version's value to pickup the next upgrade in your java resource, if any.
 *   
 * Original source is from http://code.google.com/p/dbmigrate
 */
public class GenericDBUpgradeLifecycle
    implements DBUpgradeLifecycle
{

    private final Log log = LogFactory.getLog( GenericDBUpgradeLifecycle.class );

    private DefaultSQLExec sqlexec;

    private GenericDBUpgradeConfiguration config;

    public GenericDBUpgradeLifecycle( GenericDBUpgradeConfiguration config )
        throws DBUpgradeException
    {
        this.config = config;
        this.sqlexec = new DefaultSQLExec( config );
        this.initDBUpgrade();
    }

    /**
     * Execute DB Upgrade lifecycle phases
     */
    public int upgrade()
        throws DBUpgradeException
    {
        int upgradeCount = 0;
        try
        {
            runJavaUpgrader( "PreDBUpgrade" );

            upgradeCount = this.incrementalUpgrade();

            runJavaUpgrader( "PostDBUpgrade" );
        }
        catch ( SQLException e )
        {
            throw new DBUpgradeException( "Unable to upgrade", e );
        }

        return upgradeCount; 
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private Connection getConnection()
        throws DBUpgradeException
    {
        try
        {
            return sqlexec.getConnection();
        }
        catch ( SQLException e )
        {
            throw new DBUpgradeException( e.getMessage(), e );
        }
    }

    private void initDBUpgrade()
        throws DBUpgradeException
    {
        Statement stm = null;
        ResultSet rs = null;

        try
        {
            stm = sqlexec.getConnection().createStatement();
            rs = stm.executeQuery( "SELECT " + config.getVersionColumnName() + " FROM " + config.getVersionTableName() );
            if ( !rs.next() )
            {
                this.createInitialVersion();
            }
        }
        catch ( SQLException e )
        {
            try
            {
                this.createVersionTable();
                this.createInitialVersion();
            }
            catch ( SQLException ex )
            {
                throw new DBUpgradeException( "Unable to intialize version table.", ex );
            }
        }
        finally
        {
            DbUtils.closeQuietly( rs );
            DbUtils.closeQuietly( stm );
        }

    }

    private void createVersionTable()
        throws SQLException
    {
        sqlexec.execute( "create table " + config.getVersionTableName() + " ( " + config.getVersionColumnName()
            + " integer )" );
    }

    private void createInitialVersion()
        throws SQLException
    {
        sqlexec.execute( "insert into " + config.getVersionTableName() + " ( " + config.getVersionColumnName()
            + " ) values ( " + config.getInitialVersion() + " )" );
    }

    private int  incrementalUpgrade()
        throws DBUpgradeException
    {
        int latestVersion = this.getResourceVersion();

        int upgradeCount = 0;
        while ( !internalUpgrade( latestVersion ) )
        {
            upgradeCount++;
        }

        return upgradeCount;
    }
    
    private int getResourceVersion()
        throws DBUpgradeException
    {
        int version = 0;

        String packageName = config.getPackageName();
        String versionResourcePath = packageName.replace( '.', '/' ) + "/" + config.getVersionResourceName();

        InputStream versionResourceStream = this.getClass().getClassLoader().getResourceAsStream( versionResourcePath );
        if ( versionResourceStream == null )
        {
            throw new DBUpgradeException( "Could not find " + versionResourcePath + " resource in classpath" );
        }

        try
        {
            Properties properties = new Properties();
            properties.load( versionResourceStream );
            version = Integer.parseInt( properties.getProperty( "version" ) );
        }
        catch ( IOException e )
        {
            throw new DBUpgradeException( "Could not read " + versionResourcePath + " resource in classpath", e );
        }    
        
        return version;
    }

    /**
     * get version for DB and check
     * 
     * @param connection
     * @param latestVersion
     * @return
     * @throws DBUpgradeException
     */
    private int getVersion( int latestVersion )
        throws DBUpgradeException
    {
        Statement statement = null;
        int version;
        ResultSet rs = null;
        try
        {
            statement = sqlexec.getConnection().createStatement();

            // lock the table?

            try
            {
                rs = statement.executeQuery( "SELECT distinct(" + config.getVersionColumnName() + ") FROM "
                    + config.getVersionTableName() );
            }
            catch ( SQLException e )
            {
                //postgres requires this rollback
                sqlexec.rollback();

                //version table is not available ,assume version 0
                version = 0;
                return version;
            }

            if ( !rs.next() )
            {
                // if no version present, assume version = 0
                version = 0;
            }
            else
            {
                version = rs.getInt( 1 );

                if ( rs.next() )
                {
                    throw new DBUpgradeException( "Multiple versions found in " + config.getVersionTableName()
                        + " table" );
                }

                if ( latestVersion < version )
                {
                    throw new DBUpgradeException( "Downgrade your database from " + version + " to " + latestVersion
                        + " is not supported." );
                }
            }
        }
        catch ( SQLException e )
        {
            sqlexec.rollbackQuietly();
            throw new DBUpgradeException( "Could not execute version query", e );
        }
        finally
        {
            DbUtils.closeQuietly( rs );
            DbUtils.closeQuietly( statement );
        }

        return version;
    }

    /**
     * Upgrade to the next version
     * 
     * @param configuration
     * @return false when there are more upgrade to do
     * @throws DBUpgradeException
     */
    private boolean internalUpgrade( int latestVersion )
        throws DBUpgradeException
    {

        ResultSet rs = null;
        Statement statement = null;

        try
        {
            sqlexec.getConnection().setAutoCommit( false );

            int version = getVersion( latestVersion );
            int toVersion = version + 1;

            if ( version == latestVersion )
            {
                //no more upgrade to do
                return true;
            }

            DBUpgrade upgrade = this.getUpgrader( version, toVersion );

            try
            {
                log.info( "Database Upgrade: " + config.getDialect() + ":" + upgrade );
                upgrade.upgradeDB( sqlexec, config.getDialect() );
            }
            catch ( Exception e )
            {
                log.error( e );
                sqlexec.rollbackQuietly();
                throw new DBUpgradeException( "Failed to upgrade from version: " + version + " to " + toVersion, e );
            }

            statement = this.getConnection().createStatement();

            rs = statement.executeQuery( "SELECT distinct(" + config.getVersionColumnName() + ") FROM "
                + config.getVersionTableName() );

            if ( !rs.next() )
            {
                sqlexec.rollbackQuietly();
                throw new DBUpgradeException( "Unable to look up version info in database after upgrade" );
            }
            else
            {
                int currentDBVersion = rs.getInt( 1 );

                if ( currentDBVersion != toVersion )
                {
                    sqlexec.rollbackQuietly();
                    throw new DBUpgradeException( "Version in database is: " + currentDBVersion
                        + " which is not corrected incremented after upgrading from: " + currentDBVersion );
                }
            }

            //all good
            sqlexec.commit();
        }
        catch ( SQLException e )
        {
            //more likely version table was not created during the first upgrade at version 0
            sqlexec.rollbackQuietly();
            throw new DBUpgradeException( "Failed to upgrade due to database exception", e );
        }
        finally
        {
            DbUtils.closeQuietly( rs );
            DbUtils.closeQuietly( statement );
        }

        return false;
    }

    private String versionToString( int version )
    {
        String ret = "";

        if ( version < 0 )
        {
            ret = "_";
            version = ( -1 ) * version;
        }
        return ret + version;
    }

    private DBUpgrade getJavaUpgrader( int fromVer, int toVer )
    {
        String fromVersion = versionToString( fromVer );
        String toVersion = versionToString( toVer );

        String className = config.getPackageName() + "." + config.getUpgraderPrefix() + fromVersion + "to" + toVersion;

        DBUpgrade upgrader = null;

        upgrader = this.getJavaUpgrader( className );

        if ( upgrader == null )
        {
            String dialect = config.getDialect();
            className = config.getPackageName() + "." + dialect + "." + config.getUpgraderPrefix() + fromVersion + "to"
                + toVersion;

            upgrader = this.getJavaUpgrader( className );
        }

        return upgrader;
    }

    private DBUpgradeUsingSQL getSqlUpgrader( int fromVer, int toVer )
    {
        DBUpgradeUsingSQL upgrader = null;

        String fromVersion = versionToString( fromVer );
        String toVersion = versionToString( toVer );

        String sqlResourceName = config.getPackageNameSlashFormat() + "/" + config.getUpgraderPrefix() + fromVersion
            + "to" + toVersion + ".sql";
        upgrader = this.getSqlUpgrader( sqlResourceName );

        if ( upgrader == null )
        {
            sqlResourceName = config.getPackageNameSlashFormat() + "/" + config.getDialect() + "/"
                + config.getUpgraderPrefix() + fromVersion + "to" + toVersion + ".sql";
            upgrader = this.getSqlUpgrader( sqlResourceName );
        }

        return upgrader;
    }

    private DBUpgradeUsingSQL getSqlUpgrader( String resourceName )
    {
        DBUpgradeUsingSQL upgrader = null;

        InputStream test = this.getClass().getClassLoader().getResourceAsStream( resourceName );

        if ( test != null )
        {
            try
            {
                test.close();
            }
            catch ( IOException ioe )
            {
                // whatever
            }
            upgrader = new DBUpgradeUsingSQL( resourceName );
        }

        return upgrader;
    }

    /**
     * search of available upgrader
     * 
     * @param version
     * @param toVersion
     * @param config
     * @param connection
     * @return
     * @throws DBUpgradeException
     */
    private DBUpgrade getUpgrader( int version, int toVersion )
    {

        DBUpgrade upgrader = null;

        upgrader = this.getJavaUpgrader( version, toVersion );

        if ( upgrader == null )
        {
            upgrader = this.getSqlUpgrader( version, toVersion );
        }

        if ( upgrader == null )
        {
            throw new RuntimeException( "Unable to find a DBUpgrader capable of upgrading from version " + version
                + " to version " + toVersion );

        }

        return upgrader;
    }

    private void runJavaUpgrader( String upgraderName )
        throws DBUpgradeException, SQLException
    {
        String className = null;

        className = config.getPackageName() + "." + upgraderName;
        runUpgrade( this.getJavaUpgrader( className ) );

        className = config.getPackageName() + "." + config.getDialect() + "." + upgraderName;
        runUpgrade( this.getJavaUpgrader( className ) );
    }

    private void runUpgrade( DBUpgrade upgrader )
        throws DBUpgradeException
    {
        if ( upgrader != null )
        {
            try
            {
                upgrader.upgradeDB( sqlexec, config.getDialect() );
                sqlexec.commit();
            }
            catch ( Exception e )
            {
                sqlexec.rollbackQuietly();
                throw new DBUpgradeException( "Unable to perform update: ", e );
            }
        }
    }

    /**
     * return a DBUpgrade instance base on class name
     * 
     * @param config
     * @param className
     * @return
     * @throws DBUpgradeException
     */
    private DBUpgrade getJavaUpgrader( String className )
    {
        DBUpgrade upgrader = null;

        Class<?> clazz = null;

        try
        {
            clazz = Class.forName( className );
        }
        catch ( ClassNotFoundException e )
        {
            //user does not supply the upgrader, return null so that the upgrade lifecycle can safely ignore it
            return null;
        }

        try
        {
            upgrader = (DBUpgrade) clazz.newInstance();
        }
        catch ( Exception e )
        {
            //all bets are off, user supplies the unexpected class type
            throw new RuntimeException( e );
        }

        return upgrader;

    }
}
