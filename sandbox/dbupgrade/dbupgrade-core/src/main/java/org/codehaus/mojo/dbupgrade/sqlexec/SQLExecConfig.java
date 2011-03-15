package org.codehaus.mojo.dbupgrade.sqlexec;

import java.io.File;

import org.codehaus.mojo.dbupgrade.sqlexec.SQLExec.DelimiterType;

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


public class SQLExecConfig
{
    /**
     * Call {@link #setOnError(String)} with this value to abort SQL command execution if an error
     * is found.
     */
    public static final String ON_ERROR_ABORT = "abort";

    /**
     * Call {@link #setOnError(String)} with this value to continue SQL command execution until all
     * commands have been attempted, then abort the build if an SQL error occurred in any of the
     * commands.
     */
    public static final String ON_ERROR_ABORT_AFTER = "abortAfter";

    /**
     * Call {@link #setOnError(String)} with this value to continue SQL command execution if an
     * error is found.
     */
    public static final String ON_ERROR_CONTINUE = "continue";

    /**
     * Call {@link #setOrderFile(String)} with this value to sort in ascendant order the sql files.
     */
    public static final String FILE_SORTING_ASC = "ascending";

    /**
     * Call {@link #setOrderFile(String)} with this value to sort in descendant order the sql files.
     */
    public static final String FILE_SORTING_DSC = "descending";

    // ////////////////////////// User Info ///////////////////////////////////

    /**
     * Database username. If not given, it will be looked up through <code>settings.xml</code>'s
     * server with <code>${settingsKey}</code> as key.
     *
     * @since 1.0-beta-1
     */
    private String username;

    /**
     * Database password. If not given, it will be looked up through <code>settings.xml</code>'s
     * server with <code>${settingsKey}</code> as key.
     *
     * @since 1.0-beta-1
     */
    private String password;

    /**
     * Ignore the password and use anonymous access. This may be useful for databases like MySQL
     * which do not allow empty password parameters in the connection initialization.
     *
     * @since 1.0-beta-1
     */
    private boolean enableAnonymousPassword;

    /**
     * Additional key=value pairs separated by comma to be passed into JDBC driver.
     * @since 1.0-beta-1
     */
    private String driverProperties;

    /**
     * Skip execution when there is an error obtaining a connection. This is a special case to
     * support databases, such as embedded Derby, that can shutdown the database via the URL (i.e.
     * <code>shutdown=true</code>).
     *
     * @since 1.0-beta-1
     */
    private boolean skipOnConnectionError;

    // ////////////////////////////// Source info /////////////////////////////

    // //////////////////////////////// Database info /////////////////////////
    /**
     * Database URL.
     *
     * @since 1.0-beta-1
     */
    private String url;

    /**
     * Database driver classname.
     *
     * @since 1.0-beta-1
     */
    private String driver;

    // //////////////////////////// Operation Configuration ////////////////////
    /**
     * Set to <code>true</code> to execute none-transactional SQL.
     *
     * @since 1.0-beta-1
     */
    private boolean autocommit;

    /**
     * Action to perform if an error is found. Possible values are <code>abort</code> and
     * <code>continue</code>.
     *
     * @since 1.0-beta-1
     */
    private String onError = ON_ERROR_ABORT;

    // //////////////////////////// Parser Configuration ////////////////////

    /**
     * Set the delimiter that separates SQL statements.
     *
     * @since 1.0-beta-1
     */
    private String delimiter = ";";

    /**
     * <p>
     * The delimiter type takes two values - "normal" and "row". Normal means that any occurrence of
     * the delimiter terminate the SQL command whereas with row, only a line containing just the
     * delimiter is recognized as the end of the command.
     * </p>
     * <p>
     * For example, set this to "go" and delimiterType to "row" for Sybase ASE or MS SQL Server.
     * </p>
     *
     * @since 1.0-beta-1
     */
    private DelimiterType delimiterType = DelimiterType.NORMAL;

    /**
     * Set the order in which the SQL files will be executed. Possible values are
     * <code>ascending</code> and <code>descending</code>. Any other value means that no sorting
     * will be performed.
     *
     * @since 1.0-beta-1
     */
    private String orderFile = null;

    /**
     * Keep the format of an SQL block.
     *
     * @since 1.0-beta-1
     */
    private boolean keepFormat = false;

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * Print SQL results.
     *
     * @since 1.0-beta-1
     */
    private boolean printResultSet = false;

    /**
     * Print header columns.
     */
    private boolean showheaders = true;

    /**
     * Dump the SQL exection's output to a file. Default is stdout.
     *
     * @since 1.0-beta-1
     */
    private File outputFile;

    /**
     * Encoding to use when reading SQL statements from a file.
     *
     * @since 1.0-beta-1
     */
    private String encoding = "";

    /**
     * Append to an existing file or overwrite it?
     */
    private boolean append = false;

    /**
     * Argument to Statement.setEscapeProcessing If you want the driver to use regular SQL syntax
     * then set this to false.
     *
     * @since 1.0-beta-1
     */
    private boolean escapeProcessing = true;
    
    private boolean verbose = false;

    /**
     * Repeats the execution for stress test purpose
     * @since 1.0-beta-1
     */
    private int repeats = 1;

    /**
     * Pause between repeat for stress test purpose
     * @since 1.0-beta-1
     */
    private long sleepTimeBetweenRepeats = 0;

    /**
     * Transactions per connection execution for stress test purpose
     * @since 1.0-beta-1
     */
    private int transactionsPerConnection = 1;

    /**
     * Number of retries to open connection before giving up.
     * Mainly to handle the case where application and database colocated and
     * database is much slower to accept connection at system startup 
     * @since 1.0-beta-1
     */
    private int connectionRetries = 1;
    
    /**
     * Number of delay in msec between each connection retry
     * @since 1.0-beta-1
     */
    private int connectionRetryDelay = 10000; //msec
    
    // /////////////////////////////////////////////////////////////////////////////////////
    // Accessor
    // /////////////////////////////////////////////////////////////////////////////////////


    public void setRepeatesPerConnection( int num )
    {
        this.transactionsPerConnection = num;
    }

    public void setRepeats( int repeats )
    {
        this.repeats = repeats;
    }

    public void setSleepTimeBetweenRepeats( long sleepTimeBetweenRepeats )
    {
        this.sleepTimeBetweenRepeats = sleepTimeBetweenRepeats;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getDriver()
    {
        return this.driver;
    }

    public void setDriver( String driver )
    {
        this.driver = driver;
    }

    void setAutocommit( boolean autocommit )
    {
        this.autocommit = autocommit;
    }

    public String getOrderFile()
    {
        return this.orderFile;
    }

    public void setOrderFile( String orderFile )
    {
        if ( FILE_SORTING_ASC.equalsIgnoreCase( orderFile ) )
        {
            this.orderFile = FILE_SORTING_ASC;
        }
        else if ( FILE_SORTING_DSC.equalsIgnoreCase( orderFile ) )
        {
            this.orderFile = FILE_SORTING_DSC;
        }
        else
        {
            throw new IllegalArgumentException( orderFile + " is not a valid value for orderFile, only '"
                + FILE_SORTING_ASC + "' or '" + FILE_SORTING_DSC + "'." );
        }
    }

    public String getOnError()
    {
        return this.onError;
    }

    public void setOnError( String action )
    {
        if ( ON_ERROR_ABORT.equalsIgnoreCase( action ) )
        {
            this.onError = ON_ERROR_ABORT;
        }
        else if ( ON_ERROR_CONTINUE.equalsIgnoreCase( action ) )
        {
            this.onError = ON_ERROR_CONTINUE;
        }
        else if ( ON_ERROR_ABORT_AFTER.equalsIgnoreCase( action ) )
        {
            this.onError = ON_ERROR_ABORT_AFTER;
        }
        else
        {
            throw new IllegalArgumentException( action + " is not a valid value for onError, only '" + ON_ERROR_ABORT
                + "', '" + ON_ERROR_ABORT_AFTER + "', or '" + ON_ERROR_CONTINUE + "'." );
        }
    }

    public void setDriverProperties( String driverProperties )
    {
        this.driverProperties = driverProperties;
    }
    
    /**
     * Set the file encoding to use on the SQL files read in
     *
     * @param encoding the encoding to use on the files
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * Set the delimiter that separates SQL statements. Defaults to &quot;;&quot;;
     */
    public void setDelimiter( String delimiter )
    {
        this.delimiter = delimiter;
    }

    /**
     * Set the delimiter type: "normal" or "row" (default "normal").
     */
    public void setDelimiterType( DelimiterType delimiterType )
    {
        this.delimiterType = delimiterType;
    }

    /**
     * Print result sets from the statements; optional, default false
     */
    public void setPrintResutlSet( boolean print )
    {
        this.printResultSet = print;
    }

    /**
     * Print headers for result sets from the statements; optional, default true.
     */
    public void setShowheaders( boolean showheaders )
    {
        this.showheaders = showheaders;
    }

    /**
     * Set the output file;
     */
    public void setOutputFile( File output )
    {
        this.outputFile = output;
    }

    /**
     * whether output should be appended to or overwrite an existing file. Defaults to false.
     */
    public void setAppend( boolean append )
    {
        this.append = append;
    }

    /**
     * whether or not format should be preserved. Defaults to false.
     *
     * @param keepformat The keepformat to set
     */
    public void setKeepFormat( boolean keepformat )
    {
        this.keepFormat = keepformat;
    }

    /**
     * Set escape processing for statements.
     */
    public void setEscapeProcessing( boolean enable )
    {
        escapeProcessing = enable;
    }

    public boolean isEnableAnonymousPassword()
    {
        return enableAnonymousPassword;
    }

    public void setEnableAnonymousPassword( boolean enableAnonymousPassword )
    {
        this.enableAnonymousPassword = enableAnonymousPassword;
    }

    public String getDriverProperties()
    {
        return driverProperties;
    }

    public boolean isSkipOnConnectionError()
    {
        return skipOnConnectionError;
    }

    public void setSkipOnConnectionError( boolean skipOnConnectionError )
    {
        this.skipOnConnectionError = skipOnConnectionError;
    }

    public boolean isAutocommit()
    {
        return autocommit;
    }

    public boolean isPrintResultSet()
    {
        return printResultSet;
    }

    public void setPrintResultSet( boolean printResultSet )
    {
        this.printResultSet = printResultSet;
    }

    public int getTransactionsPerConnection()
    {
        return transactionsPerConnection;
    }

    public void setTransactionsPerConnection( int transactionsPerConnection )
    {
        this.transactionsPerConnection = transactionsPerConnection;
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public DelimiterType getDelimiterType()
    {
        return delimiterType;
    }

    public boolean isKeepFormat()
    {
        return keepFormat;
    }

    public boolean isShowheaders()
    {
        return showheaders;
    }

    public File getOutputFile()
    {
        return outputFile;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public boolean isAppend()
    {
        return append;
    }

    public boolean isEscapeProcessing()
    {
        return escapeProcessing;
    }

    public int getRepeats()
    {
        return repeats;
    }

    public long getSleepTimeBetweenRepeats()
    {
        return sleepTimeBetweenRepeats;
    }

    public int getConnectionRetries()
    {
        return connectionRetries;
    }

    public void setConnectionRetries( int connectionRetries )
    {
        this.connectionRetries = connectionRetries;
    }

    public int getConnectionRetryDelay()
    {
        return connectionRetryDelay;
    }

    public void setConnectionRetryDelay( int connectionRetryDelay )
    {
        this.connectionRetryDelay = connectionRetryDelay;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose( boolean verbose ) {
        this.verbose = verbose;
    }
    
}
