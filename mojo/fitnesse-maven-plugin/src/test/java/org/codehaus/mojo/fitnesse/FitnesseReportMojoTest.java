package org.codehaus.mojo.fitnesse;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.MavenReportException;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class FitnesseReportMojoTest
    extends MockObjectTestCase
{
    private Log mMockLog = null;

    private static final String osAgnosticPath( String string )
    {
        StringBuffer buffer = new StringBuffer( string.length() );
        char ch;
        for ( int i = 0; i < string.length(); i++ )
        {
            ch = string.charAt( i );
            if ( ch == '\\' )
                ch = File.separatorChar;
            if ( ch == '/' )
                ch = File.separatorChar;
            buffer.append( ch );
        }
        return buffer.toString();
    }

    protected void setUp()
    {
        Mock tMockLog = mock( Log.class );
        tMockLog.stubs().method( "info" ).withAnyArguments();
        tMockLog.stubs().method( "debug" ).withAnyArguments();
        mMockLog = (Log) tMockLog.proxy();
    }

    public void testCopyFile()
        throws IOException, MavenReportException
    {
        File tInFile = null;
        File tOutFile = null;
        try
        {
            tInFile = new File( "tempIn.txt" );
            assertTrue( !tInFile.exists() );
            assertTrue( tInFile.createNewFile() );
            FileWriter tWriter = new FileWriter( tInFile );
            tWriter.write( "Chaine bidon" );
            tWriter.close();

            tOutFile = new File( "tempOut.txt" );
            assertFalse( tOutFile.exists() );
            FitnesseReportMojo.copyFile( mMockLog, new FileInputStream( tInFile ), tOutFile );
            assertTrue( tOutFile.exists() );
        }
        finally
        {
            tOutFile.delete();
            tInFile.delete();
        }
    }

    public void testCheckReport()
        throws MavenReportException
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( new File( "bidon" ) );
        try
        {
            tMojo.checkReport();
            fail( "Should not pass checkReport" );
        }
        catch ( MavenReportException e )
        {
            assertTrue( e.getMessage().startsWith( "Can't find any report in the following folder: " ) );
            assertTrue( e.getMessage().endsWith( osAgnosticPath( "\\bidon]" ) ) );
        }

        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/reportDir/Empty/fitnesse" ) );
        try
        {
            tMojo.checkReport();
            fail( "Should not pass checkReport" );
        }
        catch ( MavenReportException e )
        {
            assertTrue( e.getMessage().startsWith( "Can't find any report in the following folder: [" ) );
            assertTrue( e.getMessage().endsWith( osAgnosticPath( "\\target\\test-classes\\reportDir\\Empty\\fitnesse]" ) ) );
        }

        tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/onlyOneReport/fitnesse" ) );
        tMojo.checkReport();

    }

    public void testCreateIndex()
        throws MavenReportException, IOException
    {
        File tFile = new File( "target/test-classes/onlyOneReport/fitnesse/index.html" );
        if ( tFile.exists() )
        {
            tFile.delete();
        }
        tFile = new File( "target/test-classes/multiReport/fitnesse/index.html" );
        if ( tFile.exists() )
        {
            tFile.delete();
        }

        FitnesseReportMojo tMojo = getMojo();
        tMojo.setOutputDirectory( new File( "target/test-classes/onlyOneReport/fitnesse" ) );
        new File( "target/test-classes/onlyOneReportXml/fitnesse" ).mkdirs();
        tMojo.setXmlOutputDirectory( new File( "target/test-classes/onlyOneReportXml/fitnesse" ) );
        tMojo.createIndex();
        assertFalse( new File( "target/test-classes/onlyOneReportXml/fitnesse/index.xml" ).exists() );

        tMojo.setOutputDirectory( new File( "target/test-classes/multiReport/fitnesse" ) );
        new File( "target/test-classes/multiReportXml/fitnesse" ).mkdirs();
        tMojo.setXmlOutputDirectory( new File( "target/test-classes/multiReportXml/fitnesse" ) );
        tMojo.createIndex();
        File tIndexFile = new File( "target/test-classes/multiReportXml/fitnesse/index.xml" );
        assertTrue( tIndexFile.exists() );
        assertTrue( tIndexFile.length() > 30 );
        String tContent = getContent( tIndexFile );
        assertTrue( tContent.indexOf( "fitnesseResultSuiteCoverage.html" ) >= 0 );
        assertTrue( tContent.indexOf( "fitnesseResultSuiteCoverage2.html" ) >= 0 );
    }

    private String getContent( File pIndexFile )
        throws IOException
    {
        StringBuffer tBuffer = new StringBuffer();
        byte[] tBytes = new byte[100];
        int nbRead;
        FileInputStream tStream = new FileInputStream( pIndexFile );
        nbRead = tStream.read( tBytes );
        while ( nbRead > 0 )
        {
            tBuffer.append( new String( tBytes, 0, nbRead ) );
            nbRead = tStream.read( tBytes );
        }
        return tBuffer.toString();
    }

    private FitnesseReportMojo getMojo()
    {
        FitnesseReportMojo tMojo = new FitnesseReportMojo();
        tMojo.setWorkingDir( new File( "target/fitnesse" ) );
        tMojo.setLog( mMockLog );
        return tMojo;
    }

    public void testGetFitnesseReportDirWithReport()
        throws MavenReportException
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/onlyOneReport/fitnesse/" ) );
        tMojo.setWorkingDir( new File( "." ) );
        assertTrue( tMojo.getFitnesseReportDir().exists() );
    }

    public void testGetFitnesseReportDirWithBadExpliciteReport()
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/badfolder/fitnesse/" ) );
        tMojo.setWorkingDir( new File( "." ) );
        try
        {
            tMojo.getFitnesseReportDir();
            fail( "Report file shouldn't be found" );
        }
        catch ( MavenReportException e )
        {
            assertTrue( e.getMessage().startsWith( "Can't find any report in the following folder: [" ) );
            assertTrue( e.getMessage().endsWith( osAgnosticPath( "\\target\\test-classes\\badfolder\\fitnesse]" ) ) );
        }
    }

    public void testGetFitnesseReportDirWithoutReportAndWithoutClover()
        throws MavenReportException
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( null );
        tMojo.setWorkingDir( new File( "target/test-classes/reportDir/WithoutClover" ) );
        File tFile = tMojo.getFitnesseReportDir();
        assertEquals( osAgnosticPath( "target\\test-classes\\reportDir\\WithoutClover\\fitnesse" ), "" + tFile );
    }

    public void testGetFitnesseReportDirWithoutReportAndWithClover()
        throws MavenReportException
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( null );
        tMojo.setWorkingDir( new File( "target/test-classes/reportDir/WithClover" ) );
        File tFile = tMojo.getFitnesseReportDir();
        assertEquals( osAgnosticPath( "target\\test-classes\\reportDir\\WithClover\\clover\\fitnesse" ), "" + tFile );
    }

    public void testGetFitnesseReportDirWithEmptyReport()
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/reportDir/Empty/fitnesse" ) );
        tMojo.setWorkingDir( new File( "." ) );
        try
        {
            tMojo.getFitnesseReportDir();
            fail( "Report file shouldn't be found" );
        }
        catch ( MavenReportException e )
        {
            assertTrue( e.getMessage().startsWith( "Can't find any report in the following folder: [" ) );
            assertTrue( e.getMessage().endsWith( osAgnosticPath( "\\target\\test-classes\\reportDir\\Empty\\fitnesse]" ) ) );
        }
    }

    public void testGetOutputName()
    {
        FitnesseReportMojo tMojo = getMojo();
        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/onlyOneReport/fitnesse" ) );
        assertEquals( "fitnesse/fitnesseResult_localhost_SuiteCoverage3", tMojo.getOutputName() );

        tMojo.setFitnesseOutputDirectory( new File( "target/test-classes/multiReport/fitnesse" ) );
        assertEquals( "fitnesse/index", tMojo.getOutputName() );
    }

}
