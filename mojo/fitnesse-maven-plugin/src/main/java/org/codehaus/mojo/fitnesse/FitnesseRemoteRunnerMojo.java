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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReportException;

/**
 * This goal uses the <code>fitnesse.runner.TestRunner</code> class for getting result of a remote FitNesse web page
 * execution. It's possible to define several pages and/or servers.
 * 
 * @goal remotecall
 * @aggregator
 */
public class FitnesseRemoteRunnerMojo
    extends FitnesseAbstractMojo
{

    public static final String START_REPORT_TAG_KO = "document.getElementById(\"test-summary\").className = \"fail\"";

    public static final String START_REPORT_TAG_KO2 = "document.getElementById(\"test-summary\").className = \"error\"";

    public static final String START_REPORT_TAG_OK = "document.getElementById(\"test-summary\").className = \"pass\"";

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        new File( this.workingDir ).mkdirs();
        checkConfiguration();

        try
        {
            FitnesseReportMojo.copyAllResources( new File( this.workingDir ), getLog(), getClass().getClassLoader() );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException( "Unable to copy resources", e.getCause() );
        }

        getLog().info( "Found " + getFitnesseSize() + " Fitnesse configuration." );
        MojoFailureException tLastFailure = null;
        for ( int i = 0; i < getFitnesseSize(); i++ )
        {
            try
            {
                callFitnesse( i );
            }
            catch ( MojoFailureException e )
            {
                tLastFailure = e;
            }
        }
        if ( tLastFailure != null )
        {
            throw tLastFailure;
        }
    }

    /**
     * Call a Fitnesse server page.
     * 
     * @param pServerConfPosition The number of the Fitnesse configuration.
     * @throws MojoFailureException
     * @throws MojoExecutionException
     */
    void callFitnesse( int pServerConfPosition )
        throws MojoFailureException, MojoExecutionException
    {
        Fitnesse tServer = getFitnesse( pServerConfPosition );

        File tResultFile = new File( this.getFinalFileName( tServer ) );
        if ( tResultFile.exists() )
        {
            tResultFile.delete();
        }
        File tOutput = new File( this.getOutputFileName( tServer ) );
        if ( tOutput.exists() )
        {
            tOutput.delete();
        }
        try
        {
            FileOutputStream tOutputStream = new FileOutputStream( tOutput );

            tResultFile.createNewFile();
            ByteArrayOutputStream tOut = new ByteArrayOutputStream();
            String tUrl =
                "http://" + tServer.getHostName() + ":" + tServer.getPort() + "/" + tServer.getPageName()
                    + "?responder=" + tServer.getType();
            if ( tServer.getSuiteFilter() != null && tServer.getSuiteFilter().length() > 0 )
            {
                if ( Fitnesse.PAGE_TYPE_SUITE.equals( tServer.getType() ) )
                {
                    tUrl = tUrl + "&testFilter=" + tServer.getType();
                }
                else
                {
                    getLog().error(
                                    "The FitNesse parameter testFilter is only supported with Suite page (not Test page), it has be ignored..." );
                }
            }

            getRemoteResource( tUrl, tOut, tServer );

            String tOutAsString = tOut.toString();
            FitnessePage tFitnessePage = new FitnessePage( tOutAsString );
            transformHtml( new ByteArrayInputStream( tOut.toByteArray() ), new FileWriter( tResultFile ),
                           getOutputUrl( tServer ), tFitnessePage.getStatus() );

            getRemoteResource( "http://" + tServer.getHostName() + ":" + tServer.getPort() + "/ErrorLogs."
                + tServer.getPageName(), tOutputStream, tServer );
            transformOutputPage( new File( this.getOutputFileName( tServer ) ) );

            checkFailure( tOutAsString, tResultFile.getAbsolutePath() );

        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create File [" + tResultFile.getAbsolutePath() + "].", e );
        }

    }

    void transformOutputPage( File pFinalOutputName )
        throws IOException, MojoExecutionException
    {
        String tSrcAsString = FileUtil.getString( pFinalOutputName );
        tSrcAsString = tSrcAsString.replaceAll( "/files/css/", "" );
        tSrcAsString = tSrcAsString.replaceAll( "fitnesse.css", "fitnesse_base.css" );
        tSrcAsString = tSrcAsString.replaceAll( "/files/javascript/", "" );
        StringBuffer tFinal = new StringBuffer();

        int tStartIndex = tSrcAsString.indexOf( "<div class=\"sidebar\">" );
        if ( tStartIndex != -1 )
        {
            int tEndIndex = tSrcAsString.indexOf( "<div class=\"mainbar\">" );
            tFinal.append( tSrcAsString.substring( 0, tStartIndex ) );
            tFinal.append( tSrcAsString.substring( tEndIndex, tSrcAsString.length() ) );
            tSrcAsString = tFinal.toString();
        }

        tStartIndex = tSrcAsString.indexOf( "<div class=\"header\">" );
        if ( tStartIndex != -1 )
        {
            tFinal = new StringBuffer();
            int tEndIndex = tSrcAsString.indexOf( "</div>\r\n" ) + "</div>\r\n".length();
            tFinal.append( tSrcAsString.substring( 0, tStartIndex ) );
            tFinal.append( tSrcAsString.substring( tEndIndex, tSrcAsString.length() ) );
            tSrcAsString = tFinal.toString();
        }

        if ( !pFinalOutputName.delete() )
        {
            throw new MojoExecutionException( "Unable to delete output file" );
        }
        FileWriter tWriter = null;
        try
        {
            tWriter = new FileWriter( pFinalOutputName );
            tWriter.write( tSrcAsString );
        }
        finally
        {
            if ( tWriter != null )
            {
                tWriter.close();
            }
        }
    }

    void checkFailure( String pFileContent, String pFileName )
        throws MojoFailureException, MojoExecutionException
    {
        if ( isFailOnError() )
        {
            int tIndexOk = pFileContent.indexOf( START_REPORT_TAG_OK );
            int tIndexKo = pFileContent.indexOf( START_REPORT_TAG_KO );
            int tIndexKo2 = pFileContent.indexOf( START_REPORT_TAG_KO2 );
            if ( tIndexOk == -1 )
            {
                if ( ( tIndexKo == -1 ) && ( tIndexKo2 == -1 ) )
                {
                    throw new MojoExecutionException( "Unable to find failure result into FitNesse page, resultFile=["
                        + pFileName + "]." );
                }
                else
                {
                    throw new MojoFailureException( "FitNesse page fail, resultFile=[" + pFileName + "]." );
                }
            }
            else
            {
                if ( ( tIndexKo != -1 ) || ( tIndexKo2 != -1 ) )
                {
                    throw new MojoExecutionException(
                                                      "Find both success and fail result into FitNesse page , resultFile=["
                                                          + pFileName + "]." );
                }
            }
        }

    }

    void getRemoteResource( String pUrl, OutputStream pOutStream, Fitnesse pServer )
        throws MojoExecutionException
    {
        try
        {
            HttpClient tClient = new HttpClient();
            getLog().info( "Request resources from [" + pUrl + "]" );
            if ( pServer.getServerId() != null )
            {
                tClient.getParams().setAuthenticationPreemptive( true );
                Credentials defaultcreds = getCredential( pServer.getServerId() );
                AuthScope tAuthScope = new AuthScope( pServer.getHostName(), pServer.getPort(), AuthScope.ANY_REALM );
                tClient.getState().setCredentials( tAuthScope, defaultcreds );
                getLog().info( "Use credential for remote connection" );
            }
            HttpMethod tMethod = new GetMethod( pUrl );
            int tStatusCode = tClient.executeMethod( tMethod );
            if ( tStatusCode != 200 )
            {
                throw new MojoExecutionException( "Bad response code from resource [" + pUrl + "], return code=["
                    + tStatusCode + "]" );
            }

            InputStream tResponseStream = tMethod.getResponseBodyAsStream();
            byte[] tbytes = new byte[512];
            int tReadBytes = tResponseStream.read( tbytes );
            while ( tReadBytes >= 0 )
            {
                pOutStream.write( tbytes, 0, tReadBytes );
                tReadBytes = tResponseStream.read( tbytes );
            }
            pOutStream.flush();
            tMethod.releaseConnection();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to read FitNesse server response.", e );
        }
        finally
        {
            try
            {
                pOutStream.close();
            }
            catch ( IOException e )
            {
                getLog().error( "Unable to close Stream." );
            }
        }
    }

    public void setWorkingDir( String pWorkingDir )
    {
        workingDir = pWorkingDir;
    }

    String getOutputFileName( Fitnesse pServer )
    {
        return getResultFileName( pServer, FitnesseAbstractMojo.OUTPUT_EXTENSION, "html" );
    }

    String getOutputUrl( Fitnesse pServer )
    {
        return FITNESSE_RESULT_PREFIX + "_" + pServer.getHostName() + "_" + pServer.getPageName() + "_output.html";
    }

}
