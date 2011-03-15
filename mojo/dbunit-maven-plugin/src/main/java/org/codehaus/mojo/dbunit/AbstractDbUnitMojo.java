package org.codehaus.mojo.dbunit;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.dataset.datatype.IDataTypeFactory;

/**
 * Common configurations for all DBUnit operations
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @requiresDependencyResolution compile
 */
public abstract class AbstractDbUnitMojo
    extends AbstractMojo
{

    /**
     * The class name of the JDBC driver to be used.
     * 
     * @parameter expression="${driver}" 
     * @required
     */
    protected String driver;

    /**
     * Database username.  If not given, it will be looked up through 
     * settings.xml's server with ${settingsKey} as key
     * @parameter expression="${username}" 
     */
    protected String username;

    /**
     * Database password. If not given, it will be looked up through settings.xml's 
     * server with ${settingsKey} as key
     * @parameter expression="${password}" 
     */
    protected String password;

    /**
     * The JDBC URL for the database to access, e.g. jdbc:db2:SAMPLE.
     * 
     * @parameter
     * @required expression="${url}" 
     */
    protected String url;

    /**
     * The schema name that tables can be found under.
     * 
     * @parameter expression="${schema}" 
     */
    protected String schema;

    /**
     * Set the DataType factory to add support for non-standard database vendor data types.
     * 
     * @parameter expression="${dataTypeFactoryName}" default-value="org.dbunit.dataset.datatype.DefaultDataTypeFactory"
     */
    protected String dataTypeFactoryName = "org.dbunit.dataset.datatype.DefaultDataTypeFactory";

    /**
     * Enable or disable usage of JDBC batched statement by DbUnit
     * @parameter expression="${supportBatchStatement}" default-value="false"
     */
    protected boolean supportBatchStatement;

    /**
     * Enable or disable multiple schemas support by prefixing table names with the schema name.
     * 
     * @parameter expression="${useQualifiedTableNames}" default-value="false"
     */
    protected boolean useQualifiedTableNames;

    /**
     * Enable or disable the warning message displayed when DbUnit encounter an unsupported data type.
     * @parameter expression="${datatypeWarning}" default-value="false"
     */
    protected boolean datatypeWarning;

    /**
     * escapePattern
     * 
     * @parameter expression="${escapePattern}" 
     */
    protected String escapePattern;

    /**
     * skipOracleRecycleBinTables
     * 
     * @parameter expression="${escapePattern}" default-value="false"
     * @since 1.0-beta-2
     */
    protected boolean skipOracleRecycleBinTables;
    
    /**
     * Skip the execution when true, very handy when using together with maven.test.skip.
     * 
     * @parameter expression="${skip}" default-value="false"
     */
    protected boolean skip;
    
    /**
     * Access to hidding username/password
     * @parameter expression="${settings}"
     * @readonly
     */
    private Settings settings;

    /**
     * Server's id in settings.xml to look up username and password.
     * Default to ${url} if not given.
     * @parameter expression="${settingsKey}" 
     */
    private String settingsKey;

    /**
     * Class name of metadata handler.
     * @parameter expression="${metadataHandlerName}" default-value="org.dbunit.database.DefaultMetadataHandler"
     * @since 1.0-beta-3
     */
    protected String metadataHandlerName;

    /**
     * Be case sensitive when handling tables.
     * @see http://www.dbunit.org/properties.html#casesensitivetablenames
     * 
     * @parameter default-value="false"
     */
    private boolean caseSensitiveTableNames;


    ////////////////////////////////////////////////////////////////////


    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        loadUserInfoFromSettings();
    }

    IDatabaseConnection createConnection()
        throws Exception
    {

        // Instantiate JDBC driver
        Class dc = Class.forName( driver );
        Driver driverInstance = (Driver) dc.newInstance();
        Properties info = new Properties();
        info.put( "user", username );

        if ( password != null )
        {
            info.put( "password", password );
        }

        Connection conn = driverInstance.connect( url, info );

        if ( conn == null )
        {
            // Driver doesn't understand the URL
            throw new SQLException( "No suitable Driver for " + url );
        }
        conn.setAutoCommit( true );

        IDatabaseConnection connection = new DatabaseConnection( conn, schema );
        DatabaseConfig config = connection.getConfig();
        config.setFeature( DatabaseConfig.FEATURE_BATCHED_STATEMENTS, supportBatchStatement );
        config.setFeature( DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, useQualifiedTableNames );
        config.setFeature( DatabaseConfig.FEATURE_DATATYPE_WARNING, datatypeWarning );
        config.setFeature( DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, this.skipOracleRecycleBinTables );
        config.setFeature( DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, caseSensitiveTableNames );
        
        config.setProperty( DatabaseConfig.PROPERTY_ESCAPE_PATTERN, escapePattern );
        config.setProperty( DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY, new ForwardOnlyResultSetTableFactory() );

        // Setup data type factory
        IDataTypeFactory dataTypeFactory = (IDataTypeFactory) Class.forName( dataTypeFactoryName ).newInstance();
        config.setProperty( DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory );

        // Setup metadata handler
        IMetadataHandler metadataHandler = (IMetadataHandler) Class.forName( metadataHandlerName ).newInstance();
        config.setProperty( DatabaseConfig.PROPERTY_METADATA_HANDLER, metadataHandler );

        return connection;
    }

    /**
     * Load username password from settings if user has not set them in JVM properties
     */
    private void loadUserInfoFromSettings()
        throws MojoExecutionException
    {
        if ( this.settingsKey == null )
        {
            this.settingsKey = url;
        }

        if ( ( username == null || password == null ) && ( settings != null ) )
        {
            Server server = this.settings.getServer( this.settingsKey );

            if ( server != null )
            {
                if ( username == null )
                {
                    username = server.getUsername();
                }

                if ( password == null )
                {
                    password = server.getPassword();
                }
            }
        }

        if ( username == null )
        {
            //allow emtpy username
            username =  "" ;
        }

        if ( password == null )
        {
            //allow emtpy password
            password = "" ;
        }
    }


}
