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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;

/**
 * Common class for all FitNesse Mojo.
 * 
 * @author pke
 */
public abstract class FitnesseAbstractMojo
    extends AbstractMojo
{
    /** Prefix for all result saved page. */
    public static final String FITNESSE_RESULT_PREFIX = "fitnesseResult";

    /** Postfix for all saved file. */
    public static final String OUTPUT_EXTENSION = "_output";

    /**
     * This is the list of FitNesse server pages.<br/> A FitNesse tag is compose of the nested tags:<BR/> <code>
     * &lt;fitnesses&gt;<BR/>
     * &#160;&lt;fitnesse&gt;<BR/>
     * &#160;&#160;&lt;pageName&gt;This is the only required parameter, the name of 
     * the FitNesse page&lt;/pageName&gt;<BR/>
     * &#160;&#160;&lt;hostName&gt;default is <i>locahost</i>&lt;/hostName&gt;<BR/>
     * &#160;&#160;&lt;port&gt;: default is <i>80</i>;&lt;/port&gt;<BR/>
     * &#160;&#160;&lt;serverId&gt;ServerId defined in your settings.xml, this allows to use credentials 
     * (basic athentification) for calling your FitNesse pages&lt;/serverId&gt;<BR/>
     * &#160;&#160;&lt;type&gt;Override the default type of the page (Suite or Test).;&lt;/type&gt;<BR/>
     * &#160;&#160;&lt;suiteFilter&gt;Allow the use of Suite filtering ({@link http://wiki.agiletour.com/FitNesse.TestSuites} ;&lt;/suiteFilter&gt;<BR/>
     * &#160;&lt;/fitnesse&gt;<BR/>
     *     ... <BR/>
     * &lt;/fitnesses&gt;:<BR/>
     * </code>
     * 
     * @parameter
     * @required
     */
    private List fitnesses;

    /**
     * Fail the build if fitnesse pages have error.
     * 
     * @parameter default-value=false
     */
    private boolean failOnError;

    /**
     * Date format for FitNesse page timestamp.
     * 
     * @parameter default-value="dd/MM/yyyy HH:mm"
     */
    private String dateFormat;

    /**
     * List of the servers.
     * 
     * @parameter expression="${settings.servers}"
     * @required
     * @readonly
     */
    private List servers = new ArrayList();

    /**
     * @parameter expression="${project.build.directory}/fitnesse"
     * @readonly
     * @required
     */
    protected String workingDir;

    /**
     * Similar to the fitnesse configuration, but this parameter allow to override all the fitnesse by passing this
     * parameter in the command line.
     * {@link FitnesseAbstractMojo#fitnesses}
     * @parameter expression="${fitnesse.page}"
     * @see FitnesseAbstractMojo#fitnesses
     */
    String cmdFitnessePage;

    /**
     * Similar to the fitnesse configuration, but this parameter allow to override all the fitnesse by passing this
     * parameter in the command line.
     * {@link FitnesseAbstractMojo#fitnesses}
     * @parameter expression="${fitnesse.hostName}"
      * @see FitnesseAbstractMojo#fitnesses
    */
    String cmdFitnesseHostName;

    /**
     * Similar to the fitnesse configuration, but this parameter allow to override all the fitnesse by passing this
     * parameter in the command line.
     * {@link FitnesseAbstractMojo#fitnesses}
     * @parameter expression="${fitnesse.port}" default="-1"
     * @see FitnesseAbstractMojo#fitnesses
     */
    int cmdFitnessePort = -1;

    /**
     * Similar to the fitnesse configuration, but this parameter allow to override all the fitnesse by passing this
     * parameter in the command line. 
     * {@link FitnesseAbstractMojo#fitnesses}
     * @parameter expression="${fitnesse.suiteFilter}"
     * @see FitnesseAbstractMojo#fitnesses
     */
    String cmdFitnesseSuiteFilter;

    /**
     * Check the Mojo configuration.
     * 
     * @throws MojoExecutionException When the configuration is invalid.
     */
    void checkConfiguration()
        throws MojoExecutionException
    {
        changeConfigWithCmdLineParameters();

        if ( fitnesses == null || fitnesses.size() == 0 )
        {
            String errorMessage =
                "Your should configure at least one Fitnesse server. "
                    + "Check your maven-fitnesse-plugin configuration.";
            getLog().error( errorMessage );
            throw new MojoExecutionException( errorMessage );
        }
        else
        {
            for ( Iterator tIt = fitnesses.iterator(); tIt.hasNext(); )
            {
                ( (Fitnesse) tIt.next() ).checkConfiguration();
            }
        }
    }

    /**
     * Use command line argument for overriding the pom configuration.
     */
    private void changeConfigWithCmdLineParameters()
    {
        if ( cmdFitnesseHostName != null || cmdFitnessePort != -1 || cmdFitnessePage != null
            || cmdFitnesseSuiteFilter != null )
        {
            getLog().info( "Command line parameters detected, merging with pom configuration." );
            Fitnesse tFit =
                ( fitnesses != null && fitnesses.size() > 0 ? (Fitnesse) fitnesses.get( 0 )
                                : new Fitnesse( "localhost", Fitnesse.DEFAULT_FITNESSE_PORT, cmdFitnessePage,
                                                cmdFitnesseSuiteFilter ) );
            fitnesses = new ArrayList();
            fitnesses.add( tFit );
            if ( cmdFitnessePage != null )
            {
                tFit.setPageName( cmdFitnessePage );
            }
            if ( cmdFitnesseHostName != null )
            {
                tFit.setHostName( cmdFitnesseHostName );
            }
            if ( cmdFitnessePort != -1 )
            {
                tFit.setPort( cmdFitnessePort );
            }
            if ( cmdFitnesseSuiteFilter != null )
            {
                tFit.setSuiteFilter( cmdFitnesseSuiteFilter );
            }
            getLog().info(
                           "using url=[http://" + tFit.getHostName() + ":" + tFit.getPort() + "/" + tFit.getPageName()
                               + "]" );
        }
    }

    /**
     * Accessor.
     * 
     * @param pFitnesses List the FitNesse resources to call or run.
     */
    public void setFitnesses( List pFitnesses )
    {
        fitnesses = pFitnesses;
    }

    /**
     * Accessor.
     * 
     * @param pPosition Index of the configuration.
     * @return The FitNesse server configuration.
     */
    protected Fitnesse getFitnesse( int pPosition )
    {
        return (Fitnesse) fitnesses.get( pPosition );
    }

    /**
     * Accessor.
     * 
     * @return The FitNesse configuration size.
     */
    protected int getFitnesseSize()
    {
        return fitnesses.size();
    }

    /**
     * Accessor.
     * 
     * @param pServerId The identifier of the server that required credentials.
     * @return The credentials to use for this server.
     * @throws MojoExecutionException If there isn't any credential for this server.
     */
    UsernamePasswordCredentials getCredential( String pServerId )
        throws MojoExecutionException
    {
        UsernamePasswordCredentials tResult = null;
        Server tServer;
        for ( Iterator tEnum = servers.iterator(); tEnum.hasNext(); )
        {
            tServer = (Server) tEnum.next();
            if ( pServerId.equals( tServer.getId() ) )
            {
                getLog().info( "Use login/password for user " + tServer.getUsername() );
                tResult = new UsernamePasswordCredentials( tServer.getUsername(), tServer.getPassword() );
            }
        }
        if ( tResult == null )
        {
            throw new MojoExecutionException( "Unable to find credential for ServerId=[" + pServerId
                + "], you must define a <Server> tag in your settings.xml for this Id." );
        }
        return tResult;
    }

    /**
     * Add new server credential configuration.
     * 
     * @param pServer The FitNesse server configuration.
     */
    public void addServer( Server pServer )
    {
        this.servers.add( pServer );
    }

    /**
     * Accessor.
     * 
     * @return True if the build must fail when FitNesse tests failed.
     */
    public boolean isFailOnError()
    {
        return failOnError;
    }

    /**
     * Accessor.
     * 
     * @param failOnError True if the build must fail when FitNesse tests failed.
     */
    public void setFailOnError( boolean failOnError )
    {
        this.failOnError = failOnError;
    }

    /**
     * This method compute the FitNesse Html result page before saving it to file. It formats the page in a format
     * closer to maven site.
     * 
     * @param pIn The original file stream.
     * @param pOut The result file stream.
     * @param pOutputFileName The file name of the final result file.
     * @param pStatus Status of the tests that have been executed during the processing of the current page.
     * @throws IOException When a stream access error occurs.
     * @throws MojoExecutionException When the status isn't valid.
     */
    void transformHtml( InputStream pIn, Writer pOut, String pOutputFileName, String pStatus )
        throws IOException, MojoExecutionException
    {
        String tHtml = FileUtil.getString( pIn );
        int curPosStart = tHtml.indexOf( "<title>" ) + "<title>".length();
        int curPosEnd = tHtml.indexOf( "</title>" );
        pOut.write( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" " );
        pOut.write( "\"http://www.w3.org/TR/html4/strict.DTD\">\r\n" );
        pOut.write( "<html>\r\n" );
        pOut.write( "\t<head>\r\n" );
        pOut.write( "\t\t<title>" );
        pOut.write( tHtml.substring( curPosStart, curPosEnd ) );
        pOut.write( " [" );
        pOut.write( getCurrentTimeAsString() );
        pOut.write( "]</title>\r\n" );
        pOut.write( "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"fitnesse_base.css\" " );
        pOut.write( "media=\"screen\"/>\r\n" );
        pOut.write( "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"fitnesse_print.css\" " );
        pOut.write( "media=\"print\"/>\r\n" );
        pOut.write( "\t\t<script src=\"fitnesse.js\" type=\"text/javascript\"></script>\r\n" );
        pOut.write( "\t</head>\r\n" );
        pOut.write( "\t<body>\r\n" );
        pOut.write( "\t\t<div id=\"execution-status\">\r\n" );
        pOut.write( "\t\t\t<a href=\"" );
        pOut.write( pOutputFileName );
        pOut.write( "\"><img src=\"images/executionStatus/" );
        pOut.write( getImage( pStatus ) );
        pOut.write( "\"/></a>\r\n" );
        pOut.write( "\t\t\t<br/>\r\n" );
        pOut.write( "\t\t\t<a href=\"" );
        pOut.write( pOutputFileName );
        pOut.write( "\">Tests Executed " );
        pOut.write( pStatus );
        pOut.write( "</a>\r\n" );
        pOut.write( "\t\t</div>\r\n" );
        pOut.write( "\t\t<h3>Test executed on " + getCurrentTimeAsString() + "</h3>\r\n" );
        curPosStart = tHtml.indexOf( "<div class=\"main\">" );
        tHtml = tHtml.substring( curPosStart, tHtml.length() );
        tHtml = tHtml.replaceAll( "/files/", "" );
        curPosStart = tHtml.indexOf( "<div id=\"execution-status\">" );
        curPosEnd = tHtml.indexOf( "</div>", curPosStart );
        if ( curPosStart >= 0 && curPosEnd >= 0 )
        {
            pOut.write( tHtml.substring( 0, curPosStart ) );
            pOut.write( tHtml.substring( curPosEnd + "</div>".length() + 2, tHtml.length() ) );
        }
        else
        {
            pOut.write( tHtml );
        }
        pOut.flush();
    }

    /**
     * Get the image associated to the tests status.
     * 
     * @param pStatus The tests status.
     * @return The name of the image.
     * @throws MojoExecutionException If the status is invalid.
     */
    private String getImage( String pStatus )
        throws MojoExecutionException
    {
        if ( FitnessePage.STATUS_OK.equals( pStatus ) )
        {
            return "ok.gif";
        }
        else if ( FitnessePage.STATUS_ERROR.equals( pStatus ) )
        {
            return "error.gif";
        }
        else if ( FitnessePage.STATUS_FAIL.equals( pStatus ) )
        {
            return "output.gif";
        }
        else
        {
            throw new MojoExecutionException( "Invalid status [" + pStatus + "]" );
        }
    }

    /**
     * Return the current time formated as string according to the specified format.
     * 
     * @return The string representation.
     */
    protected String getCurrentTimeAsString()
    {
        SimpleDateFormat tFormat = new SimpleDateFormat( dateFormat );
        return tFormat.format( new Date() );
    }

    /**
     * Accessor.
     * 
     * @param pDateFormat The date format to use when formating date.
     */
    public void setDateFormat( String pDateFormat )
    {
        dateFormat = pDateFormat;
    }

    /**
     * Accessor.
     * 
     * @return The date format to use when formating date.
     */
    public String getDateFormat()
    {
        return dateFormat;
    }

    /**
     * Generate a temp file name for saving fitnesse result.
     * 
     * @param pServer The FitNesse configuration.
     * @return The file name.
     */
    String getTmpFileName( Fitnesse pServer )
    {
        return getResultFileName( pServer, "_tmp", "html" );
    }

    /**
     * Give the final file name for saving fitnesse result.
     * 
     * @param pServer The FitNesse configuration.
     * @return The file name.
     */
    String getFinalFileName( Fitnesse pServer )
    {
        return getResultFileName( pServer, "", "html" );
    }

    /**
     * Contract.
     * 
     * @param pServer The FitNesse server configuration.
     * @return The output file name.
     */
    abstract String getOutputFileName( Fitnesse pServer );

    /**
     * Contract.
     * 
     * @param pServer The FitNesse server configuration.
     * @return The output url.
     */
    abstract String getOutputUrl( Fitnesse pServer );

    /**
     * Generate the full final file name for saving fitnesse result.
     * 
     * @param pServer The FitNesse server configuration.
     * @param pPostfix The postfix extension to use when generating the full file name.
     * @param pExtension The file extension to use.
     * @return The file name.
     */
    protected String getResultFileName( Fitnesse pServer, String pPostfix, String pExtension )
    {
        return this.workingDir + "/" + FITNESSE_RESULT_PREFIX + "_" + pServer.getHostName() + "_"
            + pServer.getPageName() + pPostfix + "." + pExtension;
    }

}
