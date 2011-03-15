package org.codehaus.mojo.dbupgrade.sqlexec;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
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
 * Executes SQL against a database. Extracted from sql-maven-plugin 1.3
 */
public class DefaultSQLExec
    implements SQLExec
{
    private SQLExecConfig config;
    
    private PrintStream outLog = System.out;

    public DefaultSQLExec( SQLExecConfig config )
    {
        this.config = config;
    }

    // //////////////////////////////// Internal properties//////////////////////

    private int successfulStatements = 0;

    private int totalStatements = 0;

    /**
     * Database connection
     */
    private Connection conn = null;

    /**
     * SQL statement
     */
    private Statement statement = null;

    /**
     * Add a SQL transaction to execute
     */
    private Transaction createTransaction()
    {
        Transaction t = new Transaction();
        // transactions.add( t );
        return t;
    }

    /**
     * Add sql command to transactions list.
     *
     */
    private List<Transaction> addCommandToTransactions( List<Transaction> transactions, String sqlCommand )
    {
        Transaction t = createTransaction();

        t.addText( sqlCommand );

        transactions.add( t );

        return transactions;

    }

    /**
     * Add user sql fileset to transation list
     *
     */
    private List<Transaction> addFileSetToTransactions( List<Transaction> transactions, FileSet fileset )
    {
        String[] includedFiles;
        if ( fileset != null )
        {
            fileset.scan();
            includedFiles = fileset.getIncludedFiles();
        }
        else
        {
            includedFiles = new String[0];
        }

        for ( int j = 0; j < includedFiles.length; j++ )
        {
            Transaction t = createTransaction();
            t.setSrc( new File( fileset.getBasedir(), includedFiles[j] ) );
            transactions.add( t );
        }

        return transactions;
    }

    /**
     * Add user input of srcFiles to transaction list.
     *
     * @throws SQLException
     */
    private List<Transaction> addFilesToTransactions( List<Transaction> transactions, File[] files )
        throws SQLException
    {
        for ( int i = 0; files != null && i < files.length; ++i )
        {
            if ( files[i] != null && !files[i].exists() )
            {
                throw new SQLException( files[i].getPath() + " not found." );
            }

            Transaction t = createTransaction();
            t.setSrc( files[i] );
            transactions.add( t );
        }

        return transactions;
    }

    /**
     * Sort the transaction list.
     */
    private void sortTransactions( List<Transaction> transactions )
    {
        if ( SQLExecConfig.FILE_SORTING_ASC.equalsIgnoreCase( this.config.getOrderFile() ) )
        {
            Collections.sort( transactions );
        }
        else if ( SQLExecConfig.FILE_SORTING_DSC.equalsIgnoreCase( this.config.getOrderFile() ) )
        {
            Collections.sort( transactions, Collections.reverseOrder() );
        }
    }


    
    private void handleWindowsDomainUser( Properties driverProperties )
    {
        if ( "net.sourceforge.jtds.jdbc.Driver".equals( config.getDriver() ) )
        {
            String[] tokens = StringUtils.split( config.getUsername(), "\\" );

            if ( tokens != null && tokens.length == 2 )
            {
                driverProperties.put( "user", tokens[1] );
                driverProperties.put( "domain", tokens[0] );
            }
        }
    }
    
    private Driver createJDBCDriver()
    {
        Driver driverInstance = null;

        try
        {
            Class<?> dc = Class.forName( config.getDriver() );
            driverInstance = (Driver) dc.newInstance();
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( "Driver class not found: " + config.getDriver(), e );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failure loading driver: " + config.getDriver(), e );
        }
        
        return driverInstance;
    }

    private Connection createConnection( Driver driverInstance, Properties driverProperties )
        throws SQLException
    {
        Connection connection = null;
        
        for ( int i = 0; i < config.getConnectionRetries(); ++i )
        {
            try
            {
                connection = driverInstance.connect( config.getUrl(), driverProperties );
                if ( connection == null )
                {
                    // Driver doesn't understand the URL
                    throw new RuntimeException( "No suitable Driver for " + config.getUrl() );
                }

                break;

            }
            catch ( SQLException e )
            {
                if ( i < config.getConnectionRetries() )
                {
                    try
                    {
                        Thread.sleep( config.getConnectionRetryDelay() );
                    }
                    catch ( Exception iex )
                    {
                        throw new SQLException( "Unable to connect to " + config.getUrl(), iex );
                    }
                    continue;
                }

                throw new SQLException( "Unable to connect to " + config.getUrl(), e );
            }

        }

        return connection;

    }

    /**
     * parse driverProperties into Properties set
     *
     * @return
     * @throws SQLException
     */
    protected Properties getDriverProperties()
    {
        //set as protected scopy for unit test purpose
        Properties properties = new Properties();

        if ( !StringUtils.isEmpty( this.config.getDriverProperties() ) )
        {
            String[] tokens = StringUtils.split( this.config.getDriverProperties(), "," );
            for ( int i = 0; i < tokens.length; ++i )
            {
                String[] keyValueTokens = StringUtils.split( tokens[i].trim(), "=" );
                if ( keyValueTokens.length != 2 )
                {
                    throw new RuntimeException( "Invalid JDBC Driver properties: " + this.config.getDriverProperties() );
                }

                properties.setProperty( keyValueTokens[0], keyValueTokens[1] );

            }
        }

        return properties;
    }

    /**
     * read in lines and execute them
     */
    private void runStatements( Reader reader, PrintStream out )
        throws SQLException, IOException
    {
        String line;

        StringBuffer sql = new StringBuffer();

        BufferedReader in = new BufferedReader( reader );

        while ( ( line = in.readLine() ) != null )
        {
            if ( !config.isKeepFormat() )
            {
                line = line.trim();
            }

            if ( !config.isKeepFormat() )
            {
                if ( line.startsWith( "#" ) )
                {
                    continue;
                }
                if ( line.startsWith( "//" ) )
                {
                    continue;
                }
                if ( line.startsWith( "--" ) )
                {
                    continue;
                }
                StringTokenizer st = new StringTokenizer( line );
                if ( st.hasMoreTokens() )
                {
                    String token = st.nextToken();
                    if ( "REM".equalsIgnoreCase( token ) )
                    {
                        continue;
                    }
                }
            }

            if ( !config.isKeepFormat() )
            {
                sql.append( " " ).append( line );
            }
            else
            {
                sql.append( "\n" ).append( line );
            }

            // SQL defines "--" as a comment to EOL
            // and in Oracle it may contain a hint
            // so we cannot just remove it, instead we must end it
            if ( !config.isKeepFormat() )
            {
                if ( SqlSplitter.containsSqlEnd( line, config.getDelimiter() ) == SqlSplitter.NO_END )
                {
                    sql.append( "\n" );
                }
            }
            
            DelimiterType delimiterType = this.config.getDelimiterType();
            String delimiter = this.config.getDelimiter();
            
            if ( ( delimiterType.equals( DelimiterType.NORMAL ) && SqlSplitter.containsSqlEnd( line, delimiter ) > 0 )
                || ( delimiterType.equals( DelimiterType.ROW ) && line.trim().equals( delimiter ) ) )
            {
                execSQL( sql.substring( 0, sql.length() - delimiter.length() ), out );
                sql.setLength( 0 ); // clean buffer
            }
        }

        // Catch any statements not followed by ;
        if ( !sql.toString().equals( "" ) )
        {
            execSQL( sql.toString(), out );
        }
    }

    /**
     * Exec the sql statement.
     */
    private void execSQL( String sql, PrintStream out )
        throws SQLException
    {
        // Check and ignore empty statements
        if ( "".equals( sql.trim() ) )
        {
            return;
        }

        if ( config.isVerbose() ) {
            out.append( sql ).append( "\n" );
        }
        
        ResultSet resultSet = null;
        try
        {
            totalStatements++;

            boolean ret;
            int updateCount, updateCountTotal = 0;

            ret = statement.execute( sql );
            updateCount = statement.getUpdateCount();
            resultSet = statement.getResultSet();
            do
            {
                if ( !ret )
                {
                    if ( updateCount != -1 )
                    {
                        updateCountTotal += updateCount;
                    }
                }
                else
                {
                    if ( config.isPrintResultSet() )
                    {
                        printResultSet( resultSet, out );
                    }
                }
                ret = statement.getMoreResults();
                if ( ret )
                {
                    updateCount = statement.getUpdateCount();
                    resultSet = statement.getResultSet();
                }
            }
            while ( ret );

            if ( config.isPrintResultSet() )
            {
                StringBuffer line = new StringBuffer();
                line.append( updateCountTotal ).append( " rows affected" );
                out.println( line );
            }

            SQLWarning warning = conn.getWarnings();
            while ( warning != null )
            {
                warning = warning.getNextWarning();
            }
            conn.clearWarnings();
            successfulStatements++;
        }
        catch ( SQLException e )
        {
            if ( SQLExecConfig.ON_ERROR_ABORT.equalsIgnoreCase( config.getOnError() ) )
            {
                throw new SQLException( "Unable to execute: " + sql, e );
            }
        }
        finally
        {
            if ( resultSet != null )
            {
                resultSet.close();
            }
        }
    }

    /**
     * print any results in the result set.
     *
     * @param rs the resultset to print information about
     * @param out the place to print results
     * @throws SQLException on SQL problems.
     */
    private void printResultSet( ResultSet rs, PrintStream out )
        throws SQLException
    {
        if ( rs != null )
        {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            StringBuffer line = new StringBuffer();
            if ( config.isShowheaders() )
            {
                for ( int col = 1; col < columnCount; col++ )
                {
                    line.append( md.getColumnName( col ) );
                    line.append( "," );
                }
                line.append( md.getColumnName( columnCount ) );
                out.println( line );
                line = new StringBuffer();
            }
            while ( rs.next() )
            {
                boolean first = true;
                for ( int col = 1; col <= columnCount; col++ )
                {
                    String columnValue = rs.getString( col );
                    if ( columnValue != null )
                    {
                        columnValue = columnValue.trim();
                    }

                    if ( first )
                    {
                        first = false;
                    }
                    else
                    {
                        line.append( "," );
                    }
                    line.append( columnValue );
                }
                out.println( line );
                line = new StringBuffer();
            }
        }
        out.println();
    }

    /**
     * Contains the definition of a new transaction element. Transactions allow several files or
     * blocks of statements to be executed using the same JDBC connection and commit operation in
     * between.
     */
    private class Transaction
        implements Comparable<Object>
    {
        private File tSrcFile = null;

        private String tSqlCommand = "";

        /**
         *
         */
        public void setSrc( File src )
        {
            this.tSrcFile = src;
        }

        /**
         *
         */
        public void addText( String sql )
        {
            if ( !StringUtils.isBlank( sql ) )
            {
                this.tSqlCommand += sql;
            }
        }

        /**
         *
         */
        private void runTransaction( PrintStream out )
            throws IOException, SQLException
        {
            if ( tSqlCommand.length() != 0 )
            {
                runStatements( new StringReader( tSqlCommand ), out );
            }

            if ( tSrcFile != null )
            {
                Reader reader = null;

                if ( StringUtils.isEmpty( config.getEncoding() ) )
                {
                    reader = new FileReader( tSrcFile );
                }
                else
                {
                    reader = new InputStreamReader( new FileInputStream( tSrcFile ), config.getEncoding() );
                }

                try
                {
                    runStatements( reader, out );
                }
                finally
                {
                    reader.close();
                }
            }
        }

        public int compareTo( Object object )
        {
            Transaction transaction = (Transaction) object;

            if ( transaction.tSrcFile == null )
            {
                if ( this.tSrcFile == null )
                {
                    return 0;
                }
                else
                {
                    return Integer.MAX_VALUE;
                }
            }
            else
            {
                if ( this.tSrcFile == null )
                {
                    return Integer.MIN_VALUE;
                }
                else
                {
                    return this.tSrcFile.compareTo( transaction.tSrcFile );
                }
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load the sql file and then it
     */
    private void execute( Connection conn, List<Transaction> transactions )
        throws SQLException
    {

        try
        {
            statement = conn.createStatement();
            statement.setEscapeProcessing( config.isEscapeProcessing() );

            PrintStream out = System.out;
            try
            {
                if ( config.getOutputFile() != null )
                {
                    out = new PrintStream( new BufferedOutputStream( new FileOutputStream( config.getOutputFile()
                        .getAbsolutePath(), config.isAppend() ) ) );
                }

                // Process all transactions
                for ( Transaction t : transactions )
                {
                    t.runTransaction( out );

                    if ( !config.isAutocommit() )
                    {
                        conn.commit();
                    }
                }

            }
            finally
            {
                if ( out != null && out != System.out )
                {
                    out.close();
                }
            }
        }
        catch ( IOException e )
        {
            throw new SQLException( e.getMessage(), e );
        }
        catch ( SQLException e )
        {
            if ( !config.isAutocommit() && conn != null
                && SQLExecConfig.ON_ERROR_ABORT.equalsIgnoreCase( config.getOnError() ) )
            {
                this.rollbackQuietly();
            }
            throw new SQLException( e.getMessage(), e );
        }
        finally
        {
            DbUtils.closeQuietly( statement );
        }

        if ( SQLExecConfig.ON_ERROR_ABORT_AFTER.equalsIgnoreCase( config.getOnError() )
            && totalStatements != successfulStatements )
        {
            throw new SQLException( "Some SQL statements failed to execute" );
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new Connection as using the driver, url, userid and password specified.
     *
     * The calling method is responsible for closing the connection.
     *
     * @return Connection the newly created connection.
     * @throws SQLException if the UserId/Password/Url is not set or there is no suitable driver
     *             or the driver fails to load.
     * @throws SQLException if there is problem getting connection with valid url
     *
     */
    public Connection getConnection()
        throws SQLException
    {
        if ( conn != null )
        {
            return conn;
        }

        Properties driverProperties = new Properties();

        driverProperties.put( "user", config.getUsername() );

        handleWindowsDomainUser( driverProperties );

        if ( !config.isEnableAnonymousPassword() )
        {
            if ( !StringUtils.isBlank( this.config.getPassword() ) )
            {
                driverProperties.put( "password", this.config.getPassword() );
            }
        }

        driverProperties.putAll( this.getDriverProperties() );

        Driver driverInstance = this.createJDBCDriver();

        conn = this.createConnection( driverInstance, driverProperties );

        conn.setAutoCommit( config.isAutocommit() );

        return conn;
    }    
    
    public void execute( String sqlCommand )
        throws SQLException
    {
        execute( sqlCommand, new File[0], null );
    }

    public void execute( File[] srcFiles )
        throws SQLException
    {
        execute( null, srcFiles, null );
    }

    public void execute( FileSet fileset )
        throws SQLException
    {
        execute( null, null, fileset );
    }

    public void execute( String sqlCommand, File[] srcFiles, FileSet fileset )
        throws SQLException
    {

        List<Transaction> transactions = new ArrayList<Transaction>();

        successfulStatements = 0;

        totalStatements = 0;

        if ( !StringUtils.isBlank( sqlCommand ) )
        {
            addCommandToTransactions( transactions, sqlCommand );
        }

        if ( srcFiles != null )
        {
            addFilesToTransactions( transactions, srcFiles );
        }

        if ( fileset != null )
        {
            addFileSetToTransactions( transactions, fileset );
        }

        sortTransactions( transactions );

        for ( int i = 0; i < config.getRepeats() / config.getTransactionsPerConnection(); ++i )
        {
            Connection connection = null;
            try
            {
                connection = getConnection();
            }
            catch ( SQLException e )
            {
                if ( !config.isSkipOnConnectionError() )
                {
                    throw new SQLException( e.getMessage(), e );
                }
                else
                {
                    // error on get connection and user asked to skip the rest
                    break;
                }
            }

            for ( int j = 0; j < config.getTransactionsPerConnection(); ++j )
            {
                try
                {
                    this.execute( connection, transactions );
                    Thread.sleep( this.config.getSleepTimeBetweenRepeats() );
                }
                catch ( InterruptedException e )
                {
                }
            }
        }

    }

    public void execute( Reader reader )
        throws SQLException
    {
        try
        {
            statement = this.getConnection().createStatement();
            statement.setEscapeProcessing( config.isEscapeProcessing() );

            this.runStatements( reader, outLog );
        }
        catch ( IOException e )
        {
            throw new SQLException( "Error reading SQL stream: " + e.getMessage(), e );
        }
        finally
        {
            DbUtils.closeQuietly( statement );
            statement = null;
        }

    }

    public void execute( File sqlFile )
        throws SQLException
    {
        Reader reader = null;
        try
        {
            reader = new FileReader( sqlFile );
            execute( reader );
        }
        catch ( IOException e )
        {
            throw new SQLException( "Error reading SQL stream: " + e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( reader );
        }

    }
    
    public void execute( File sqlFile, boolean disableSQLParser )
        throws SQLException, IOException
    {
        if ( ! disableSQLParser )
        {
            this.execute( sqlFile );
        }
        else
        {
            InputStream is = null;
            Statement statement = null;
            String sql = null;

            try
            {
                is = new FileInputStream( sqlFile );
                sql = IOUtils.toString( is );
                
                statement = getConnection().createStatement();
                statement.setEscapeProcessing( false );
                
                if ( statement.execute( sql ) )
                {
                    //we expect a false return since the execution has no result set
                    throw new SQLException( "Unable execute SQL Statement:" + sql );
                }

            }
            finally
            {
                DbUtils.closeQuietly( statement );
                IOUtils.closeQuietly( is );
            }        
        } 
    
    }

    public void commit()
        throws SQLException
    {
        if ( ! this.config.isAutocommit() ) 
        {
            this.getConnection().commit();
        }
    }

    public void rollback()
    {
        try
        {
            if ( ! this.config.isAutocommit() ) 
            {
                this.getConnection().rollback();
            }
        }
        catch ( SQLException e )
        {
            //unexpected exception, throw runtime to get more attention
            throw new RuntimeException( "Unable to rollback." );
        }
    }

    public void rollbackQuietly()
    {
        try
        {
            this.getConnection().rollback();
        }
        catch ( SQLException e )
        {

        }
    }

    public void close()
    {
        DbUtils.closeQuietly( this.conn );
        this.conn = null;
    }

    public void execute( InputStream istream )
        throws SQLException
    {
        Reader reader = new InputStreamReader( istream );
        this.execute( reader );
    }
    
    /**
     * Number of SQL statements executed so far that caused errors.
     *
     * @return the number
     */
    public int getSuccessfulStatements()
    {
        return successfulStatements;
    }

    /**
     * Number of SQL statements executed so far, including the ones that caused errors.
     *
     * @return the number
     */
    public int getTotalStatements()
    {
        return totalStatements;
    }    

}
