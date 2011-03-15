package org.codehaus.mojo.dbupgrade.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.codehaus.mojo.dbupgrade.DBUpgradeException;
import org.codehaus.mojo.dbupgrade.DBUpgradeLifecycle;
import org.codehaus.mojo.dbupgrade.sqlexec.DefaultSQLExec;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

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
 * This class hooks up user's sql upgrade script locations contained in a text file ( ie the text file contains a list of SQL script paths ). 
 * After a SQL script is executed, its names is stored in your configurable database version table. DBUpgrade uses
 * database version's value ( a SQL script name ) to pickup the next upgrade script, if any.
 */
public class FileDBUpgradeLifecycle
    implements DBUpgradeLifecycle
{

    private DefaultSQLExec sqlexec;

    private FileListDBUpgradeConfiguration config;

    private String initialDBVersion = null;

    public FileDBUpgradeLifecycle( FileListDBUpgradeConfiguration config )
        throws DBUpgradeException
    {
        this.config = config;
        this.sqlexec = new DefaultSQLExec( config );
        this.initializeDBVersion();
    }

    /**
     * Execute DB Upgrade lifecycle phases
     */
    public int upgrade()
        throws DBUpgradeException
    {
        int upgraderCount = 0;

        FileReader fileReader = null;

        try
        {
            fileReader = new FileReader( config.getUpgradeFile() );
            BufferedReader reader = new BufferedReader( fileReader );

            String line = null;

            //find where we left off last upgrade
            if ( !StringUtils.isBlank( this.initialDBVersion ) )
            {
                while ( ( line = this.readLine( reader ) ) != null )
                {
                    if ( !StringUtils.isBlank( line ) )
                    {
                        File upgradeFile = new File( config.getScriptDirectory(), line );
                        if ( !upgradeFile.exists() )
                        {
                            throw new DBUpgradeException( upgradeFile.getAbsolutePath() + " not found." );
                        }

                        if ( initialDBVersion.equals( line ) )
                        {
                            break; //so that we can continue with upgrade
                        }
                    }

                }

                if ( line == null )
                {
                    throw new DBUpgradeException( "Database version value: " + initialDBVersion
                        + " not found in the list. Are you upgrading the right database?" );
                }

            }

            //continue on with last upgrade
            while ( ( line = this.readLine( reader ) ) != null )
            {
                if ( !StringUtils.isBlank( line ) )
                {
                    upgrade( config.getScriptDirectory(), line.trim() );
                    upgraderCount++;
                }
            }

        }
        catch ( IOException e )
        {
            throw new DBUpgradeException( "Unable to perform file upgrade: " + this.config.getUpgradeFile(), e );
        }
        finally
        {
            IOUtil.close( fileReader );
        }

        return upgraderCount;
    }

    private String readLine( BufferedReader reader )
        throws IOException
    {
        String line = reader.readLine();
        if ( line != null )
        {
            line = line.trim();
            if ( StringUtils.isBlank( line ) || line.startsWith( "#" ) )
            {
                line = "";
            }
        }
        return line;
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

    private void initializeVersionTable()
        throws DBUpgradeException
    {
        try
        {
            this.createVersionTable();
            this.createInitialVersion();
        }
        catch ( SQLException e )
        {
            throw new DBUpgradeException( "Unable to create version table:" + config.getVersionTableName() + ".", e );
        }

    }

    private void initializeDBVersion()
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
            initializeVersionTable();
        }
        finally
        {
            DbUtils.closeQuietly( rs );
            DbUtils.closeQuietly( stm );
        }

        this.initialDBVersion = this.getDBVersion();

    }

    private void createVersionTable()
        throws SQLException
    {
        sqlexec.execute( "create table " + config.getVersionTableName() + " ( " + config.getVersionColumnName()
            + " varchar(256) )" );
        sqlexec.commit();
    }

    private void createInitialVersion()
        throws SQLException
    {
        sqlexec.execute( "insert into " + config.getVersionTableName() + " ( " + config.getVersionColumnName()
            + " ) values ( '' )" );
        sqlexec.commit();
    }

    private String getDBVersion()
        throws DBUpgradeException
    {
        Statement statement = null;
        String version;
        ResultSet rs = null;

        Connection connection = getConnection();

        try
        {
            statement = connection.createStatement();

            rs = statement.executeQuery( "SELECT distinct(" + config.getVersionColumnName() + ") FROM "
                + config.getVersionTableName() );

            if ( rs.next() )
            {
                version = rs.getString( 1 );
                if ( rs.next() )
                {
                    throw new DBUpgradeException( "Multiple versions found in " + config.getVersionTableName()
                        + " table." );
                }
            }
            else
            {
                throw new DBUpgradeException( "Version row not found in: " + config.getVersionTableName() + " table." );
            }
        }
        catch ( SQLException e )
        {
            sqlexec.rollbackQuietly();
            throw new DBUpgradeException( "Version row not found in: " + config.getVersionTableName() + " table." );
        }

        finally
        {
            DbUtils.closeQuietly( rs );
            DbUtils.closeQuietly( statement );
        }

        return version;
    }

    private void upgrade( File scriptDirectory, String upgradeFileName )
        throws DBUpgradeException
    {
        File upgradeFile = new File( scriptDirectory, upgradeFileName );
        
        if ( this.config.isVerbose() ) 
        {
            System.out.println( "Executing: " + upgradeFile + " ..." );
        }

        try
        {
            sqlexec.execute( upgradeFile, config.isDisableSQLParser() );
            if ( ! StringUtils.isBlank( config.getPostIncrementalStatement() ) )
            {
                sqlexec.execute( config.getPostIncrementalStatement() );
            }
            sqlexec.execute( "update " + this.config.getVersionTableName() + " set " + this.config.getVersionColumnName() +  " ='" + upgradeFileName + "'" );
            sqlexec.commit();
        }
        catch ( Exception e )
        {
            sqlexec.rollbackQuietly();
            throw new DBUpgradeException( "Unable to perform file upgrade: " + upgradeFile  + ".", e );
        }
    }
}
