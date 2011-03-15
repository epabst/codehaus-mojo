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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Server;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class FitnesseAbstractMojoTest
    extends MockObjectTestCase
{
    private static final String WILD_CART = "XXX";

    private Mock mMockLog;

    public static class MojoTest
        extends FitnesseAbstractMojo
    {

        public MojoTest( Log pLog )
        {
            setLog( pLog );
        }

        public void execute()
            throws MojoExecutionException, MojoFailureException
        {
        }

        String getOutputFileName( Fitnesse pServer )
        {
            return null;
        }

        String getOutputUrl( Fitnesse pServer )
        {
            return null;
        }

    }

    public void testGetCredentialOk()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( null );
        UsernamePasswordCredentials tCred = tMojo.getCredential( "Server0" );
        assertEquals( "Login0", tCred.getUserName() );
        assertEquals( "Password0", tCred.getPassword() );
        tCred = tMojo.getCredential( "Server1" );
        assertEquals( "Login1", tCred.getUserName() );
        assertEquals( "Password1", tCred.getPassword() );
        tCred = tMojo.getCredential( "Server2" );
        assertEquals( "Login2", tCred.getUserName() );
        assertEquals( "Password2", tCred.getPassword() );
    }

    public void testGetCredentialNotFound()
    {
        MojoTest tMojo = getMojo( null );
        try
        {
            tMojo.getCredential( "Server3" );
            fail( "Should not find credential" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Unable to find credential for ServerId=[Server3], "
                + "you must define a <Server> tag in your settings.xml for this Id.", e.getMessage() );
        }
    }

    private MojoTest getMojo( Log pLog )
    {
        MojoTest tMojo = new MojoTest( pLog );
        Server tServer = new Server();
        tServer.setId( "Server0" );
        tServer.setUsername( "Login0" );
        tServer.setPassword( "Password0" );
        tMojo.addServer( tServer );
        tServer = new Server();
        tServer.setId( "Server1" );
        tServer.setUsername( "Login1" );
        tServer.setPassword( "Password1" );
        tMojo.addServer( tServer );
        tServer = new Server();
        tServer.setId( "Server2" );
        tServer.setUsername( "Login2" );
        tServer.setPassword( "Password2" );
        tMojo.addServer( tServer );
        tMojo.setDateFormat( "dd/MM/yyyy HH:mm" );
        return tMojo;
    }

    public void testCheckConfigurationWithoutCommandLineNorConfig()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        mMockLog.stubs().method( "error" ).with(
                                                 eq( "Your should configure at least one Fitnesse "
                                                     + "server. Check your maven-fitnesse-plugin configuration." ) );
        try
        {
            tMojo.setFitnesses( null );
            tMojo.checkConfiguration();
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Your should configure at least one Fitnesse server. "
                + "Check your maven-fitnesse-plugin configuration.", e.getMessage() );
        }
        List tList = new ArrayList();
        tMojo.setFitnesses( tList );
        try
        {
            tMojo.checkConfiguration();
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Your should configure at least one Fitnesse server. "
                + "Check your maven-fitnesse-plugin configuration.", e.getMessage() );
        }
    }

    public void testCheckConfigurationWithoutCommandLineAndOneFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MaPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithoutCommandLineAndSeveralFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tList.add( new Fitnesse( "localhost2", 8080, "MaPage2", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 2, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MaPage", tMojo.getFitnesse( 0 ).getPageName() );
        assertEquals( "localhost2", tMojo.getFitnesse( 1 ).getHostName() );
        assertEquals( 8080, tMojo.getFitnesse( 1 ).getPort() );
        assertEquals( "MaPage2", tMojo.getFitnesse( 1 ).getPageName() );
    }

    public void testCheckConfigurationWithCommandLineAndWithoutConfig()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.setFitnesses( null );
        tMojo.cmdFitnessePage = "MaPage";
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MaPage", tMojo.getFitnesse( 0 ).getPageName() );

        tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnessePage = "MaPage";
        List tList = new ArrayList();
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MaPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithSimpleCommandLineAndOneFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnessePage = "MyPage";
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MyPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithFullCommandLineAndOneFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnessePage = "MyPage";
        tMojo.cmdFitnesseHostName = "myHost";
        tMojo.cmdFitnessePort = 8080;
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "myHost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 8080, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MyPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithCommandLineAndSeveralFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnessePage = "MyPage";
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tList.add( new Fitnesse( "localhost2", 8080, "MaPage2", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MyPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithFullCommandLineAndSeveralFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnessePage = "MyPage";
        tMojo.cmdFitnesseHostName = "myHost";
        tMojo.cmdFitnessePort = 8081;
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tList.add( new Fitnesse( "localhost2", 8080, "MaPage2", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "myHost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 8081, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MyPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithHostCommandLineAndSeveralFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnesseHostName = "myHost";
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tList.add( new Fitnesse( "localhost2", 8080, "MaPage2", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "myHost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 80, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MaPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    public void testCheckConfigurationWithPortCommandLineAndSeveralFitnesse()
        throws MojoExecutionException
    {
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.cmdFitnessePort = 8081;
        List tList = new ArrayList();
        tList.add( new Fitnesse( "localhost", 80, "MaPage", null ) );
        tList.add( new Fitnesse( "localhost2", 8080, "MaPage2", null ) );
        tMojo.setFitnesses( tList );
        tMojo.checkConfiguration();
        assertEquals( 1, tMojo.getFitnesseSize() );
        assertEquals( "localhost", tMojo.getFitnesse( 0 ).getHostName() );
        assertEquals( 8081, tMojo.getFitnesse( 0 ).getPort() );
        assertEquals( "MaPage", tMojo.getFitnesse( 0 ).getPageName() );
    }

    /**
     * @Override
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        mMockLog = mock( Log.class );
        mMockLog.stubs().method( "info" ).withAnyArguments();
    }

    public void testTransformHtmlForSimpleTest()
        throws IOException, MojoExecutionException
    {
        InputStream tSrcFile = getClass().getClassLoader().getResourceAsStream( "remote/TestBrut.html" );
        InputStream tExpectedFile = getClass().getClassLoader().getResourceAsStream( "remote/TestResult.html" );
        try
        {
            compareTransformFile( tSrcFile, tExpectedFile, "TestResult_output.txt", FitnessePage.STATUS_ERROR );
        }
        finally
        {
            tSrcFile.close();
            tExpectedFile.close();
        }
    }

    public void testTransformHtmlForSuite()
        throws IOException, MojoExecutionException
    {
        InputStream tSrcFile = getClass().getClassLoader().getResourceAsStream( "remote/SuiteBrut.html" );
        InputStream tExpectedFile = getClass().getClassLoader().getResourceAsStream( "remote/SuiteResult.html" );
        try
        {
            compareTransformFile( tSrcFile, tExpectedFile, "SuiteResult_output.txt", FitnessePage.STATUS_ERROR );
        }
        finally
        {
            tSrcFile.close();
            tExpectedFile.close();
        }
    }

    private void compareTransformFile( InputStream pSrcFile, InputStream pExpectedFile, String pOutputFileName,
                                       String pStatus )
        throws FileNotFoundException, IOException, MojoExecutionException
    {
        String tExpected = FileUtil.getString( pExpectedFile );
        ByteArrayOutputStream tTransform = new ByteArrayOutputStream();
        MojoTest tMojo = getMojo( (Log) mMockLog.proxy() );
        tMojo.transformHtml( pSrcFile, new OutputStreamWriter( tTransform ), pOutputFileName, pStatus );
        StringTokenizer tTokExp = new StringTokenizer( tExpected, "\n" );
        StringTokenizer tTokRes = new StringTokenizer( tTransform.toString(), "\n" );
        while ( tTokExp.hasMoreElements() )
        {
            String tExpectToken = tTokExp.nextToken();
            String tResultToken = tTokRes.nextToken();
            if ( tExpectToken.indexOf( WILD_CART ) >= 0 )
            {
                int tStartIndex = tExpectToken.indexOf( WILD_CART );
                assertEquals( tExpectToken.substring( 0, tStartIndex ), tResultToken.substring( 0, tStartIndex ) );
                int tEndIndex = tExpectToken.lastIndexOf( WILD_CART ) + WILD_CART.length();
                String tEndExpectected = tExpectToken.substring( tEndIndex, tExpectToken.length() );
                String tEndResult = tResultToken.substring( tEndIndex, tResultToken.length() );
                assertEquals( tEndExpectected, tEndResult );
            }
            else
            {
                assertEquals( tExpectToken, tResultToken );
            }

        }
        assertFalse( tTokRes.hasMoreElements() );
    }

}
