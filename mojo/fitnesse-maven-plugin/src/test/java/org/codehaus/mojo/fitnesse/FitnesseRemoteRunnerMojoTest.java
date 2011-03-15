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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ServerSocketFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Server;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class FitnesseRemoteRunnerMojoTest
    extends MockObjectTestCase
{
    protected String error;

    private String mRequest;

    private FitnesseRemoteRunnerMojo mMojo = null;

    private Mock mMockLog = null;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        mMojo = getMojo();
    }

    private FitnesseRemoteRunnerMojo getMojo()
    {
        FitnesseRemoteRunnerMojo tMojo = new FitnesseRemoteRunnerMojo();
        tMojo.setFailOnError( false );
        tMojo.setFailOnError( false );
        tMojo.setWorkingDir( "target/fitnesse" );

        List tFitnesses = new ArrayList();
        tFitnesses.add( new Fitnesse() );
        tMojo.setFitnesses( tFitnesses );

        mMockLog = mock( Log.class );
        mMockLog.stubs().method( "info" ).withAnyArguments();
        tMojo.setLog( (Log) mMockLog.proxy() );

        return tMojo;
    }

    public void testGetRemoteResourceWithoutCredential()
        throws IOException, MojoExecutionException
    {
        List tFitnesses = new ArrayList();
        Fitnesse tServer = new Fitnesse();
        tServer.setHostName( "localhost" );
        tServer.setPort( 8083 );
        tFitnesses.add( tServer );
        mMojo.setFitnesses( tFitnesses );

        startServer();
        ByteArrayOutputStream tOut = new ByteArrayOutputStream();
        try
        {
            mMojo.getRemoteResource( "http://localhost:8083/url", tOut, tServer );
            fail();
        }
        catch ( MojoExecutionException e )
        {
            assertNotNull( e.getCause() );
            assertEquals( "Connection reset", e.getCause().getMessage() );
        }

        assertEquals( "GET /url HTTP/1.1\r\n" + "User-Agent: Jakarta Commons-HttpClient/3.1\r\n"
            + "Host: localhost:8083\r\n\r", mRequest );

        tServer.setServerId( "TestId" );
        Server tMavenServer = new Server();
        tMavenServer.setId( "TestId" );
        tMavenServer.setUsername( "myLogin" );
        tMavenServer.setPassword( "myPassword" );
        mMojo.addServer( tMavenServer );
        startServer();
        tOut = new ByteArrayOutputStream();
        try
        {
            mMojo.getRemoteResource( "http://localhost:8083/url", tOut, tServer );
            fail();
        }
        catch ( MojoExecutionException e )
        {
            assertNotNull( e.getCause() );
        }

        assertEquals( "GET /url HTTP/1.1\r\n" + "Authorization: Basic bXlMb2dpbjpteVBhc3N3b3Jk\r\n"
            + "User-Agent: Jakarta Commons-HttpClient/3.1\r\n" + "Host: localhost:8083\r\n\r", mRequest );
        assertNull( error );
    }

    private void startServer()
        throws IOException
    {
        Runnable tRun = new Runnable()
        {
            public void run()
            {
                try
                {
                    ServerSocket tServerSock;
                    tServerSock = ServerSocketFactory.getDefault().createServerSocket( 8083 );
                    Socket tSocket = tServerSock.accept();
                    InputStream tIn = tSocket.getInputStream();
                    int tRead = tIn.read();
                    StringBuffer tBuf = new StringBuffer();
                    boolean tIsActive = true;
                    int[] tLastRead = new int[4];
                    while ( tRead != -1 && tIsActive )
                    {
                        tBuf.append( (char) tRead );
                        // System.out.print( (char) tRead );
                        // System.out.flush();
                        tRead = tIn.read();
                        tIsActive = checkActive( tRead, tLastRead );
                    }
                    mRequest = tBuf.toString();
                    OutputStreamWriter tWriter = new OutputStreamWriter( tSocket.getOutputStream() );
                    tWriter.write( "HTTP/1.1 404 Not Found\r\n" );
                    tWriter.write( "Content-Type: text/html; charset=utf-8\r\n" );
                    tWriter.write( "Content-Length: 0\r\n" );
                    tWriter.write( "Connection: close\r\n" );
                    tWriter.write( "Server: FitNesse-20050731\r\n\r" );
                    tSocket.close();
                    Thread.sleep( 100 );
                    tServerSock.close();
                }
                catch ( IOException e )
                {
                    error = e.getMessage();
                }
                catch ( InterruptedException e )
                {
                    error = e.getMessage();
                }
            }

            private boolean checkActive( int read, int[] lastRead )
            {
                lastRead[0] = lastRead[1];
                lastRead[1] = lastRead[2];
                lastRead[2] = lastRead[3];
                lastRead[3] = read;
                return !( lastRead[0] == 13 && lastRead[1] == 10 && lastRead[2] == 13 && lastRead[3] == 10 );
            }
        };
        Thread tThread = new Thread( tRun );
        tThread.setDaemon( true );
        tThread.start();
    }

    public void testCheckFailureWithFailureOn()
        throws FileNotFoundException, IOException, MojoFailureException, MojoExecutionException
    {
        checkReport( true, "remoteFailure/TestOk.html" );
        checkReport( true, "remoteFailure/SuiteOk.html" );
        checkReport( true, "remoteFailure/SuiteInfraOk.html" );
        try
        {
            checkReport( true, "remoteFailure/TestInvalid.html" );
            fail( "should not find result" );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().startsWith( "Unable to find failure result into FitNesse page, resultFile=[" ) );
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().endsWith( "remoteFailure/TestInvalid.html]." ) );
        }
        try
        {
            checkReport( true, "remoteFailure/TestInvalid2.html" );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue(
                        "Invalid message=" + e.getMessage(),
                        e.getMessage().startsWith( "Find both success and fail result into FitNesse page , resultFile=" ) );
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().endsWith( "remoteFailure/TestInvalid2.html]." ) );
        }
        try
        {
            checkReport( true, "remoteFailure/TestFail.html" );
            fail( "report TestFail.html should throw a Failure" );
        }
        catch ( MojoFailureException e )
        {
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().startsWith( "FitNesse page fail, resultFile=" ) );
            assertTrue( "Invalid message=" + e.getMessage(), e.getMessage().endsWith( "remoteFailure/TestFail.html]." ) );
        }
        try
        {
            checkReport( true, "remoteFailure/SuiteFail.html" );
            fail( "report SuiteFail.html should throw a Failure" );
        }
        catch ( MojoFailureException e )
        {
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().startsWith( "FitNesse page fail, resultFile=" ) );
            assertTrue( "Invalid message=" + e.getMessage(), e.getMessage().endsWith( "remoteFailure/SuiteFail.html]." ) );
        }
        try
        {
            checkReport( true, "remoteFailure/SuiteInfraFail.html" );
            fail( "report SuiteInfraFail.html should throw a Failure" );
        }
        catch ( MojoFailureException e )
        {
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().startsWith( "FitNesse page fail, resultFile=[" ) );
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().endsWith( "remoteFailure/SuiteInfraFail.html]." ) );
        }

        try
        {
            checkReport( true, "remoteFailure/SuiteException.html" );
            fail( "report SuiteException.html should throw a Failure" );
        }
        catch ( MojoFailureException e )
        {
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().startsWith( "FitNesse page fail, resultFile=[" ) );
            assertTrue( "Invalid message=" + e.getMessage(),
                        e.getMessage().endsWith( "remoteFailure/SuiteException.html]." ) );
        }

    }

    public void testCheckFailureWithFailureOff()
        throws FileNotFoundException, IOException, MojoFailureException, MojoExecutionException
    {
        checkReport( false, "remoteFailure/TestOk.html" );
        checkReport( false, "remoteFailure/SuiteOk.html" );
        checkReport( false, "remoteFailure/SuiteInfraOk.html" );
        checkReport( false, "remoteFailure/TestInvalid.html" );
        checkReport( false, "remoteFailure/TestInvalid2.html" );
        checkReport( false, "remoteFailure/TestFail.html" );
        checkReport( false, "remoteFailure/SuiteFail.html" );
        checkReport( false, "remoteFailure/SuiteInfraFail.html" );
        checkReport( false, "remoteFailure/SuiteException.html" );
    }

    private void checkReport( boolean pFailOnError, String pFileName )
        throws FileNotFoundException, IOException, MojoFailureException, MojoExecutionException
    {
        InputStream tSrcFile = getClass().getClassLoader().getResourceAsStream( pFileName );
        mMojo.setFailOnError( pFailOnError );

        mMojo.checkFailure( FileUtil.getString( tSrcFile ), pFileName );
    }

    public void testTransformOutputPage()
        throws IOException, MojoExecutionException, URISyntaxException
    {
        File tSrcFile =
            new File( getClass().getClassLoader().getResource( "remote/RemoteCallBrut_output.html" ).toURI() );
        File tExpectedFile =
            new File( getClass().getClassLoader().getResource( "remote/RemoteCallResult_output.html" ).toURI() );
        File tTmpFile = new File( "target/tmpOutput.html" );
        if ( tTmpFile.exists() )
        {
            assertTrue( tTmpFile.delete() );
        }
        Project antProject = new Project();
        antProject.init();
        Copy tCopy = (Copy) antProject.createTask( "copy" );
        tCopy.setFile( tSrcFile );
        tCopy.setOverwrite( true );
        tCopy.setTofile( tTmpFile );
        tCopy.execute();

        getMojo().transformOutputPage( tTmpFile );

        String tExpected = FileUtil.getString( tExpectedFile );
        String tTransform = FileUtil.getString( tTmpFile );

        StringTokenizer tTokExp = new StringTokenizer( tExpected, "\n" );
        StringTokenizer tTokRes = new StringTokenizer( tTransform.toString(), "\n" );
        while ( tTokExp.hasMoreElements() )
        {
            String tExpectToken = tTokExp.nextToken();
            String tResultToken = tTokRes.nextToken();
            assertEquals( tExpectToken, tResultToken );
        }
        assertFalse( tTokRes.hasMoreElements() );

    }

}
