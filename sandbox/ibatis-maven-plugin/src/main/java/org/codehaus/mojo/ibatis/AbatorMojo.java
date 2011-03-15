package org.codehaus.mojo.ibatis;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.ibatis.abator.api.Abator;
import org.apache.ibatis.abator.config.AbatorConfiguration;
import org.apache.ibatis.abator.config.AbatorContext;
import org.apache.ibatis.abator.config.DAOGeneratorConfiguration;
import org.apache.ibatis.abator.config.JDBCConnectionConfiguration;
import org.apache.ibatis.abator.config.JavaModelGeneratorConfiguration;
import org.apache.ibatis.abator.config.JavaTypeResolverConfiguration;
import org.apache.ibatis.abator.config.ModelType;
import org.apache.ibatis.abator.config.SqlMapGeneratorConfiguration;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.exception.InvalidConfigurationException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AbatorMojo 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 *
 * @goal abator
 * @phase generate-sources
 */
public class AbatorMojo
    extends AbstractMojo
{
    private static final String CONTEXT_ID = "IbatisPluginId";

    private static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";

    private static final String JDBC_URL = "jdbc:hsqldb:mem:abator";

    private static final String JDBC_USERNAME = "sa";

    private static final String JDBC_PASSWORD = "";

    /**
     * @parameter expression="${ibatis.sqlFile}" 
     *      default-value="${basedir}/src/main/ibatis/abator.sql"
     */
    private File sqlFile;

    /**
     * @parameter expression="${ibatis.abator.target.package}" default-value="${project.groupId}"
     */
    private String targetPackage;

    /**
     * @parameter expression="${ibatis.abator.output.directory}" default-value="${project.build.directory}/generated-sources/ibatis/src"
     */
    private File generatedSourceDir;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        String sqlCommands = JDBC_PASSWORD;
        
        if( !generatedSourceDir.exists() )
        {
            generatedSourceDir.mkdirs();
        }

        try
        {
            sqlCommands = FileUtils.fileRead( sqlFile );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to read sql command file " + sqlFile.getAbsolutePath(), e );
        }

        // Create HsqlDB
        try
        {
            Class.forName( JDBC_DRIVER ).newInstance();
            Connection conn = DriverManager.getConnection( JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD );
            Statement statement = conn.createStatement();

            statement.execute( sqlCommands );

            getLog().info( "SQL Update Count: " + statement.getUpdateCount() );

            SQLWarning warning = statement.getWarnings();
            if ( warning != null )
            {
                getLog().warn( "Warnings ... " );
                while ( warning != null )
                {
                    getLog().warn( "SQL State: " + warning.getSQLState() );
                    getLog().warn( "SQL Error Code: " + warning.getErrorCode() );
                    getLog().warn( warning.getMessage(), warning );

                    warning = warning.getNextWarning();
                }
            }

            List tableNames = new ArrayList();
            DatabaseMetaData dbMetaData = conn.getMetaData();

            ResultSet rs = dbMetaData.getTables( conn.getCatalog(), null, null, null );

            // check if the index database exists in the database
            while ( rs.next() )
            {
                String dbTableName = rs.getString( "TABLE_NAME" );
                
                if( !dbTableName.toLowerCase().startsWith( "system_") )
                {
                    tableNames.add( dbTableName );
                }
            }

            AbatorConfiguration abatorConfig = createAbatorConfiguration( tableNames );

            List abatorWarnings = new ArrayList();
            Abator abator = new Abator( abatorConfig, null, abatorWarnings );

            abator.generate( null );

            for ( Iterator itwarnings = abatorWarnings.iterator(); itwarnings.hasNext(); )
            {
                String warningMsg = (String) itwarnings.next();
                getLog().warn( warningMsg );
            }
        }
        catch ( InstantiationException e )
        {
            throw new MojoExecutionException( "Unable to create SQL Database.", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new MojoExecutionException( "Unable to create SQL Database.", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to create SQL Database.", e );
        }
        catch ( SQLException e )
        {
            throw new MojoExecutionException( "Unable to create SQL Database.", e );
        }
        catch ( InvalidConfigurationException e )
        {
            throw new MojoExecutionException( "Unable to generate ibatis sqlmap files: " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to generate ibatis sqlmap files.", e );
        }
        catch ( InterruptedException e )
        {
            throw new MojoExecutionException( "Unable to generate ibatis sqlmap files.", e );
        }
    }

    private AbatorConfiguration createAbatorConfiguration( List tableNames )
    {
        /* <abatorConfiguration> */

        AbatorConfiguration config = new AbatorConfiguration();

        /* <abatorContext id="DB2Tables" generatorSet="Java2"> */

        AbatorContext context = new AbatorContext( "Java2", ModelType.HIERARCHICAL );
        context.setId( CONTEXT_ID );

        /*
         * <jdbcConnection driverClass="COM.ibm.db2.jdbc.app.DB2Driver"
         *   connectionURL="jdbc:db2:TEST"
         *   userId="db2admin"
         *   password="db2admin">
         *   <classPathEntry location="/Program Files/IBM/SQLLIB/java/db2java.zip" />
         * </jdbcConnection>
         */

        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.setDriverClass( JDBC_DRIVER );
        jdbcConfig.setConnectionURL( JDBC_URL );
        jdbcConfig.setUserId( JDBC_USERNAME );
        jdbcConfig.setPassword( JDBC_PASSWORD );

        //        Map pluginArtifactMap = ArtifactUtils.artifactMapByVersionlessId( pluginClasspathList );
        //        Artifact sqlArtifact = (Artifact) pluginArtifactMap.get( "hsqldb:hsqldb" );
        //        
        //        jdbcConfig.addClasspathEntry( sqlArtifact.getFile().getAbsolutePath() );

        context.setJdbcConnectionConfiguration( jdbcConfig );

        /*
         * <javaTypeResolver >
         *   <property name="forceBigDecimals" value="false" />
         * </javaTypeResolver>
         */

        JavaTypeResolverConfiguration javaTypeConfig = new JavaTypeResolverConfiguration();

        javaTypeConfig.addProperty( "forceBigDecimals", "false" );

        context.setJavaTypeResolverConfiguration( javaTypeConfig );

        /*
         * <javaModelGenerator targetPackage="test.model" targetProject="\AbatorTestProject\src">
         *   <property name="enableSubPackages" value="true" />
         *   <property name="trimStrings" value="true" />
         * </javaModelGenerator>
         */

        JavaModelGeneratorConfiguration javaModelConfig = new JavaModelGeneratorConfiguration();
        
        javaModelConfig.setTargetPackage( targetPackage );
        javaModelConfig.setTargetProject( generatedSourceDir.getAbsolutePath() );
        javaModelConfig.addProperty( "enableSubPackages", "true" );
        javaModelConfig.addProperty( "trimStrings", "true" );

        context.setJavaModelGeneratorConfiguration( javaModelConfig );

        /*
         * <sqlMapGenerator targetPackage="test.xml"  targetProject="\AbatorTestProject\src">
         *   <property name="enableSubPackages" value="true" />
         * </sqlMapGenerator>
         */

        SqlMapGeneratorConfiguration sqlMapConfig = new SqlMapGeneratorConfiguration();

        sqlMapConfig.setTargetPackage( targetPackage );
        sqlMapConfig.setTargetProject( generatedSourceDir.getAbsolutePath() );
        sqlMapConfig.addProperty( "enableSubPackage", "true" );

        context.setSqlMapGeneratorConfiguration( sqlMapConfig );

        /*
         * <daoGenerator type="IBATIS" targetPackage="test.dao"  targetProject="\AbatorTestProject\src">
         *   <property name="enableSubPackages" value="true" />
         * </daoGenerator> 
         */

        DAOGeneratorConfiguration daoConfig = new DAOGeneratorConfiguration();

        daoConfig.setConfigurationType( "IBATIS" );
        daoConfig.setTargetPackage( targetPackage );
        daoConfig.setTargetProject( generatedSourceDir.getAbsolutePath() );

        context.setDaoGeneratorConfiguration( daoConfig );

        /*
         * <table schema="DB2ADMIN" tableName="ALLTYPES" domainObjectName="Customer" >
         *   <property name="useActualColumnNames" value="true"/>
         *   <generatedKey column="ID" sqlStatement="DB2" identity="true" />
         *   <columnOverride column="DATE_FIELD" property="startDate" />
         *   <ignoreColumn column="FRED" />
         *   <columnOverride column="LONG_VARCHAR_FIELD" jdbcType="VARCHAR" />
         * </table>
         */

        for ( Iterator itTableNames = tableNames.iterator(); itTableNames.hasNext(); )
        {
            String tableName = (String) itTableNames.next();

            TableConfiguration tableConfig = new TableConfiguration( context );
            tableConfig.setTableName( tableName );
            tableConfig.setDomainObjectName( StringUtils.capitalise( tableName.toLowerCase() ) );
            tableConfig.addProperty( "useActualColumnNames", "true" );

            context.addTableConfiguration( tableConfig );
        }

        List errors = new ArrayList();
        context.validate( errors );

        for ( Iterator iter = errors.iterator(); iter.hasNext(); )
        {
            String errorMsg = (String) iter.next();
            getLog().error( errorMsg );
        }

        /* </abatorContext> */

        config.addAbatorContext( context );

        /* </abatorConfiguration> */

        return config;
    }
}
