package org.codehaus.mojo.dbupgrade.sqlexec;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;


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
 * Unit test for simple SQLExec taken from sqlexec-maven-plugin 1.3
 */
public class SQLExecTest
    extends TestCase
{
    private DefaultSQLExec sqlexec;
    
    private SQLExecConfig config;

    private Properties p;

    public void setUp()
        throws Exception
    {
        super.setUp();
        p = new Properties();
        p.load( getClass().getResourceAsStream( "/test.properties" ) );

        config = new SQLExecConfig();
        config.setDriver( p.getProperty( "driver" ) );
        config.setUsername( p.getProperty( "user" ) );
        config.setPassword( p.getProperty( "password" ) );
        config.setUrl( p.getProperty( "url" ) );
        config.setDriverProperties( p.getProperty( "driverProperties" ) );
        
        sqlexec = new DefaultSQLExec( config );

        // populate parameters

    }

    /**
     * No error when there is no input
     */
    public void testNoCommandMojo()
        throws SQLException
    {
        sqlexec.execute( "" );

        assertEquals( 0, sqlexec.getSuccessfulStatements() );
    }

    public void testCreateCommandMojo()
        throws SQLException
    {
        String command = "create table PERSON ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";
        sqlexec.execute( command );

        assertEquals( 1, sqlexec.getSuccessfulStatements() );
    }

    public void testDropCommandMojo()
        throws SQLException
    {
        String command = "drop table PERSON";
        sqlexec.execute( command );
        assertEquals( 1, sqlexec.getSuccessfulStatements() );
    }

    public void testFileSetMojo()
        throws SQLException
    {

        FileSet ds = new FileSet();
        ds.setBasedir( "src/test" );
        ds.setIncludes( new String[] { "**/create*.sql" } );
        ds.scan();
        assert ( ds.getIncludedFiles().length == 1 );

        //sqlexec.setFileSet( ds );

        sqlexec.execute( ds );

        assertEquals( 3, sqlexec.getSuccessfulStatements() );

    }

    public void testFileArrayMojo()
        throws SQLException
    {
        File[] srcFiles = new File[1];
        srcFiles[0] = new File( "src/test/data/drop-test-tables.sql" );

        //sqlexec.setSrcFiles( srcFiles );
        sqlexec.execute( srcFiles );

        assertEquals( 3, sqlexec.getSuccessfulStatements() );

    }

    /**
     * Ensure srcFiles always execute first
     *
     */
    public void testAllMojo()
        throws SQLException
    {

        String command = "create table PERSON2 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";
        //sqlexec.addText( command );

        File[] srcFiles = new File[1];
        srcFiles[0] = new File( "src/test/data/create-test-tables.sql" );
        //sqlexec.setSrcFiles( srcFiles );

        FileSet ds = new FileSet();
        ds.setBasedir( "src/test" );
        ds.setIncludes( new String[] { "**/drop*.sql" } );
        ds.scan();
        //sqlexec.setFileSet( ds );
        sqlexec.execute( command, srcFiles, ds );

        assertEquals( 7, sqlexec.getSuccessfulStatements() );
    }

    public void distestOrderFile()
        throws SQLException
    {
        FileSet ds = new FileSet();
        ds.setBasedir( "src/test" );
        ds.setIncludes( new String[] { "**/drop*.sql", "**/create*.sql" } );
        ds.scan();
        //sqlexec.setFileSet( ds );

        config.setOrderFile( SQLExecConfig.FILE_SORTING_ASC );
        sqlexec.execute( ds );

        assertEquals( 6, sqlexec.getSuccessfulStatements() );

        try
        {
            config.setOrderFile( SQLExecConfig.FILE_SORTING_DSC );
            sqlexec.execute( ds );
            fail( "Execution is not aborted on error." );
        }
        catch ( SQLException e )
        {
        }
    }

    public void testOnErrorContinueMojo()
        throws SQLException
    {
        String command = "create table BOGUS"; //bad syntax
        config.setOnError( "continue" );
        sqlexec.execute( command );
        assertEquals( 0, sqlexec.getSuccessfulStatements() );
    }

    public void testOnErrorAbortMojo()
        throws SQLException
    {
        String command = "create table BOGUS"; //bad syntax

        try
        {
            sqlexec.execute( command );
            fail( "Execution is not aborted on error." );

        }
        catch ( SQLException e )
        {

        }

        assertEquals( 0, sqlexec.getSuccessfulStatements() );
    }

    public void testOnErrorAbortAfterMojo()
        throws SQLException
    {
        String commands = "create table BOGUS"; //bad syntax

        //        sqlexec.addText( commands );

        File[] srcFiles = new File[1];
        srcFiles[0] = new File( "src/test/data/invalid-syntax.sql" );

        assertTrue( srcFiles[0].exists() );

        //        sqlexec.setSrcFiles( srcFiles );
        config.setOnError( "abortAfter" );

        try
        {
            sqlexec.execute( commands, srcFiles, null );
            fail( "Execution is not aborted on error." );

        }
        catch ( SQLException e )
        {
            //expected
        }

        assertEquals( 0, sqlexec.getSuccessfulStatements() );
        assertEquals( 2, sqlexec.getTotalStatements() );
    }

    public void testBadDriver()
        throws SQLException
    {
        config.setDriver( "bad-driver" );
        try
        {
            sqlexec.execute( "" );

            fail( "Bad driver is not detected" );
        }
        catch ( RuntimeException e )
        {

        }
    }

    public void testBadUrl()
        throws SQLException
    {
        config.setUrl( "bad-url" );
        try
        {
            sqlexec.execute( "" );

            fail( "Bad URL is not detected" );
        }
        catch ( RuntimeException e )
        {

        }
    }

    public void testBadFile()
    {
        File[] srcFiles = new File[1];
        srcFiles[0] = new File( "a-every-bogus-file-that-does-not-exist" );

        try
        {
            sqlexec.execute( srcFiles );

            fail( "Bad files is not detected" );
        }
        catch ( SQLException e )
        {

        }
    }

    public void testOnError()
    {
        config.setOnError( "AbOrT" );
        assertEquals( SQLExecConfig.ON_ERROR_ABORT, config.getOnError() );
        config.setOnError( "cOnTiNuE" );
        assertEquals( SQLExecConfig.ON_ERROR_CONTINUE, config.getOnError() );
        try
        {
            config.setOnError( "bad" );
            fail( IllegalArgumentException.class.getName() + " was not thrown." );
        }
        catch ( IllegalArgumentException e )
        {
            //expected
        }
        try
        {
            config.setOnError( null );
            fail( IllegalArgumentException.class.getName() + " was not thrown." );
        }
        catch ( IllegalArgumentException e )
        {
            //expected
        }
    }

    public void testDriverProperties()
        throws SQLException
    {
        Properties driverProperties = this.sqlexec.getDriverProperties();
        assertEquals( 2, driverProperties.size() );
        assertEquals( "value1", driverProperties.get( "key1" ) );
        assertEquals( "value2", driverProperties.get( "key2" ) );
    }

    public void testBadDriverProperties()
        throws SQLException
    {
        try
        {
            config.setDriverProperties( "key1=value1,key2" );
            this.sqlexec.getDriverProperties();
            fail( "Unable to detect bad driver properties" );
        }
        catch ( RuntimeException e )
        {

        }
    }

    public void testKeepFormat()
        throws SQLException
    {
        // Normally a line starting in -- would be ignored, but with keepformat mode
        // on it will not.
        String command = "--create table PERSON ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";
        //sqlexec.addText( command );
        config.setKeepFormat( true );

        try
        {
            sqlexec.execute( command );
            fail( "-- at the start of the SQL command is ignored." );
        }
        catch ( SQLException e )
        {
        }

        assertEquals( 0, sqlexec.getSuccessfulStatements() );

    }

    public void testBadDelimiter()
        throws Exception
    {
        String command = "create table SEPARATOR ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar):"
            + "create table SEPARATOR2 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";

        //sqlexec.addText( command );
        config.setDelimiter( ":" );

        try
        {
            sqlexec.execute( command );
            fail( "Expected parser error." );
        }
        catch ( SQLException e )
        {
        }
    }

    public void testGoodDelimiter()
        throws Exception
    {
        String command = "create table SEPARATOR ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)\n:\n"
            + "create table SEPARATOR2 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";

        //sqlexec.addText( command );
        config.setDelimiter( ":" );

        sqlexec.execute( command );

        assertEquals( 2, sqlexec.getSuccessfulStatements() );
    }

    public void testBadDelimiterType()
        throws Exception
    {
        String command = "create table BADDELIMTYPE ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)" + "\n:"
            + "create table BADDELIMTYPE2 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";

        //sqlexec.addText( command );
        config.setDelimiter( ":" );
        config.setDelimiterType( DefaultSQLExec.DelimiterType.ROW );

        try
        {
            sqlexec.execute( command );
            fail( "Expected parser error." );
        }
        catch ( SQLException e )
        {
        }
    }

    public void testGoodDelimiterType()
        throws Exception
    {
        String command = "create table GOODDELIMTYPE ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)"
            + "\n:  \n" + "create table GOODDELIMTYPE2 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";

        //sqlexec.addText( command );
        config.setDelimiter( ":" );
        config.setDelimiterType( DefaultSQLExec.DelimiterType.ROW );

        sqlexec.execute( command );
        assertEquals( 2, sqlexec.getSuccessfulStatements() );
    }

    public void testOutputFile()
        throws Exception
    {
        String command = "create table GOODDELIMTYPE3 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)"
            + "\n:  \n" + "create table GOODDELIMTYPE4 ( PERSON_ID integer, FIRSTNAME varchar, LASTNAME varchar)";

        //sqlexec.addText( command );
        config.setDelimiter( ":" );
        config.setDelimiterType( DefaultSQLExec.DelimiterType.ROW );

        String basedir = System.getProperty( "basedir", "." );
        File outputFile = new File( basedir, "target/sql.out" );
        outputFile.delete();
        config.setOutputFile( outputFile );
        config.setPrintResutlSet( true );

        sqlexec.execute( command );

        assertTrue( "Output file: " + outputFile + " not found.", outputFile.exists() );

        assertTrue( "Unexpected empty output file. ", outputFile.length() > 0 );

        //makesure we can remote the file, it is not locked
        //assertTrue( outputFile.delete() );

    }

}
