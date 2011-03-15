package org.codehaus.mojo.fitnesse.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpException;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;

public class FitnesseServerManager
{

    // private static final Log LOG = LogFactory.getLog( IntegrationUtil.class );

    static FitnesseServerManager sInstance = new FitnesseServerManager();

    Process mFitnesseServer;

    public static void stopServer( Process pFitnessProcess )
    {
        if ( sInstance.mFitnesseServer != null )
        {
            System.out.println( "The FitNesse server isn't stopped" );
            sInstance.mFitnesseServer.destroy();
            System.out.println( "The FitNesse server was now stopped" );
        }
        else if ( pFitnessProcess != null )
        {
            System.out.println( "The Param FitNesse server isn't stopped" );
            pFitnessProcess.destroy();
            System.out.println( "The ParamFitNesse server was now stopped" );
        }
        else
        {
            System.out.println( "Nothing to stop !" );
        }
        sInstance.mFitnesseServer = null;
    }

    protected void finalize()
        throws Throwable
    {
        if ( mFitnesseServer != null )
        {
            System.out.println( "The FitNesse server wasn't stopped" );
            mFitnesseServer.destroy();
            System.out.println( "The FitNesse server wasn't stopped, but it is now." );
        }
        super.finalize();
    }

    public static Process startServer( File pBaseDir, String pVersion, String pPort )
        throws IOException, VerificationException
    {
        // File testDir = ResourceExtractor.simpleExtractResources( IntegrationUtil.class,
        // pBaseDir.getAbsolutePath() );
        // Use default maven location settings file
        Verifier verifier = new Verifier( pBaseDir.getAbsolutePath() );
        System.err.println( "Running " );
        String tPath = verifier.getArtifactPath( "org.fitnesse", "fitnesse", pVersion, "jar" );
        System.err.println( "Running 2" );
        copyFile( tPath, "target/lib/fitnesse.jar" );
        System.err.println( "Running 3 " );
        StringBuffer tCmd = new StringBuffer();
        tCmd.append( "java -cp " );
        tCmd.append( "target/lib/fitnesse.jar" );
        tCmd.append( " fitnesse.FitNesse " );
        tCmd.append( "-d src/it -p " + pPort );
        try
        {
            System.err.println( "Running " + tCmd );
            sInstance.mFitnesseServer = Runtime.getRuntime().exec( tCmd.toString() );
            Thread tThread = new Thread( new StreamConsummer( sInstance.mFitnesseServer ) );
            tThread.setDaemon( true );
            tThread.start();
        }
        catch ( IOException e )
        {
            System.err.println( "Unable to start fitnesse server" + e.getMessage() );
            throw new RuntimeException( e );
        }

        Runnable finalizer = new Runnable()
        {
            public void run()
            {
                try
                {
                    finalize();
                }
                catch ( Throwable e )
                {
                    // ignore
                }
            }
        };

        Runtime.getRuntime().addShutdownHook( new Thread( finalizer ) );

        waitForServer( pPort, 30 );

        return sInstance.mFitnesseServer;
    }

    private static void waitForServer( String pPort, long pTimeoutISeconds )
    {
        long tInitTime = System.currentTimeMillis();
        boolean isServerReady = false;
        while ( !isServerReady && ( System.currentTimeMillis() - tInitTime ) < ( pTimeoutISeconds * 1000 ) )
        {
            try
            {
                URL tUrl = new URL( "http://localhost:" + pPort );
                HttpURLConnection tCon = (HttpURLConnection) tUrl.openConnection();
                tCon.connect();
                isServerReady = ( 200 == tCon.getResponseCode() );
                System.err.println( tCon.getResponseCode() );
                tCon.disconnect();
            }
            catch ( HttpException e )
            {
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        if ( !isServerReady )
        {
            throw new RuntimeException( "The fitness server do not start" );
        }
    }

    private static void copyFile( String pSource, String pDest )
        throws FileNotFoundException, IOException
    {
        FileInputStream tSrcInputStream = new FileInputStream( new File( pSource ).getAbsolutePath() );
        File tDestFile = new File( pDest );
        File tDestDir = tDestFile.getParentFile();
        if ( !tDestDir.exists() )
        {
            assertTrue( tDestDir.mkdirs() );
        }
        assertTrue( tDestDir.exists() );
        tDestFile.createNewFile();
        FileOutputStream tDestOutStream = new FileOutputStream( tDestFile );
        byte[] tBuffer = new byte[5000];
        try
        {
            for ( int tRead = tSrcInputStream.read( tBuffer ); tRead > 0; tRead = tSrcInputStream.read( tBuffer ) )
            {
                tDestOutStream.write( tBuffer, 0, tRead );
            }
        }
        finally
        {
            try
            {
                tDestOutStream.close();
            }
            finally
            {
                tSrcInputStream.close();
            }
        }
    }

    private static void assertTrue( boolean pTrue )
    {
        if ( !pTrue )
        {
            throw new RuntimeException( "Assertion failed" );
        }
    }

    private static class StreamConsummer
        implements Runnable
    {
        private final Process mProcess;

        public StreamConsummer( Process pProcess )
        {
            mProcess = pProcess;
        }

        public void run()
        {
            boolean tContinue = true;
            while ( tContinue )
            {
                byte[] tBytes = new byte[100];
                try
                {
                    int i = mProcess.getInputStream().read( tBytes );
                    if ( i >= 0 )
                    {
                        String tString = new String( tBytes, 0, i );
                        System.out.print( tString );
                    }
                    else
                    {
                        tContinue = false;
                    }
                }
                catch ( IOException e )
                {
                    System.err.println( e.getMessage() );
                }
            }
        }

    }

}
