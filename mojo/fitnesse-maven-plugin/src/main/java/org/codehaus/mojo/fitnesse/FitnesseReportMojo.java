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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * Generates a <a href="http://fitnesse.org">FitNesse</a> report from a FitNesse web server. The generated report is an
 * external report generated FitNesse itself. If the project use Clover for code coverage and if FitNesse has clover
 * dependency (ie use the <i>ArtifactId-Version-clover.jar</i>), the code executed during the FitNesse execution (phase
 * integration-test) will be had to the unit-test code coverage. See the <a href="examples/multiproject.html">clover
 * example</a>.
 * 
 * @goal fitnesse
 * @aggregator
 */
public class FitnesseReportMojo
    extends AbstractMavenReport
{

    /**
     * The Maven project instance for the executing project.
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Report output directory. It should be defined in the reporting section of the pom.
     * 
     * @parameter expression="${project.build.directory}/generated-site/xdoc/fitnesse"
     * @required
     */
    private File xmlOutputDirectory;

    /**
     * The directory where the Fitnesse report will be generated.
     * @parameter expression="${project.reporting.outputDirectory}/fitnesse"
     * @required
     */
    private File outputDirectory;

    /**
     * The directory where the Fitnesse report has be generated. It must be defined when it's not the default value
     * (${project.build.directory}/fitnesse. It's the case for exemple with the clover plugin (that use
     * ${project.build.directory}/clover/fitnesse).
     * 
     * @parameter
     */
    private File fitnesseOutputDirectory;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File workingDir;

    /**
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @component
     */
    private Renderer siteRenderer;

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     */
    protected void executeReport( Locale pArg0 )
        throws MavenReportException
    {
        // Ensure the output directory exists
        getLog().info( "outputDirectory="+outputDirectory );
        this.outputDirectory.mkdirs();
        this.xmlOutputDirectory.mkdirs();

        checkReport();

        createReport();
        createIndex();
        copyAllResources( this.outputDirectory, getLog(), getClass().getClassLoader() );

        getLog().info( "Fitnesse report finished" );
    }

    public static void copyAllResources( File pToDir, Log pLog, ClassLoader pLoader )
        throws MavenReportException
    {
        copyResource( pToDir, "fitnesse.js", pLog, pLoader );
        copyResource( pToDir, "fitnesse_base.css", pLog, pLoader );
        copyResource( pToDir, "fitnesse_print.css", pLog, pLoader );
        // images
        new File( pToDir + "/images" ).mkdir();
        copyResource( pToDir, "images/collapsableClosed.gif", pLog, pLoader );
        copyResource( pToDir, "images/collapsableOpen.gif", pLog, pLoader );
        copyResource( pToDir, "images/FitNesseLogo.gif", pLog, pLoader );
        copyResource( pToDir, "images/FitNesseLogoMedium.jpg", pLog, pLoader );
        copyResource( pToDir, "images/folder.gif", pLog, pLoader );
        copyResource( pToDir, "images/importedPage.jpg", pLog, pLoader );
        copyResource( pToDir, "images/virtualPage.jpg", pLog, pLoader );
        new File( pToDir + "/images/executionStatus" ).mkdir();
        copyResource( pToDir, "images/executionStatus/error.gif", pLog, pLoader );
        copyResource( pToDir, "images/executionStatus/ok.gif", pLog, pLoader );
        copyResource( pToDir, "images/executionStatus/output.gif", pLog, pLoader );
    }

    private static void copyResource( File pToDir, String pFileName, Log pLog, ClassLoader pLoader )
        throws MavenReportException
    {
        File tDest = new File( pToDir + "/" + pFileName );
        copyFile( pLog, pLoader.getResourceAsStream( "fitnesse_resources/" + pFileName ), tDest );
    }

    private void createReport()
        throws MavenReportException
    {
        File curFile;
        File[] tFileArray = getFitnesseReportDir().listFiles();
        for ( int i = 0; i < tFileArray.length; i++ )
        {
            curFile = tFileArray[i];
            if ( !curFile.exists() )
            {
                throw new MavenReportException( "Unable to find Fitnesse report for server " + curFile );
            }
            if ( curFile.getName().startsWith( FitnesseRunnerMojo.FITNESSE_RESULT_PREFIX ) )
            {
                File tDestFile = new File( outputDirectory + "/" + curFile.getName() );
                try
                {
                    copyFile( getLog(), new FileInputStream( curFile ), tDestFile );
                }
                catch ( IOException e )
                {
                    throw new MavenReportException( "Unable to create File [" + curFile.getAbsolutePath() + "].", e );
                }
            }
        }
    }

    void checkReport()
        throws MavenReportException
    {
        if ( getFitnesseReportDir().listFiles().length == 0 )
        {
            getLog().error(
                            "Your should configure at least one Fitnesse server. "
                                + "Check your Fitnesse plugin configuration." );
            throw new MavenReportException( "Your should configure at least one Fitnesse server. "
                + "Check your Fitnesse plugin configuration." );
        }
    }

    static void copyFile( Log pLogger, InputStream pIn, File pDestFile )
        throws MavenReportException
    {
        FileOutputStream tOut = null;
        try
        {
            if ( !pDestFile.exists() )
            {
                pDestFile.createNewFile();
            }
            tOut = new FileOutputStream( pDestFile );
            byte[] tBuff = new byte[100];
            int tRead = pIn.read( tBuff );
            while ( tRead >= 0 )
            {
                tOut.write( tBuff, 0, tRead );
                tRead = pIn.read( tBuff );
            }
            pLogger.debug( "File copied to " + pDestFile );
            pLogger.debug( "File exist " + pDestFile.exists() );
        }
        catch ( FileNotFoundException e )
        {
            throw new MavenReportException( "File doesn't exist", e );
        }
        catch ( IOException e )
        {
            throw new MavenReportException( "Unable to write into file...", e );
        }
        finally
        {
            try
            {
                if ( tOut != null )
                {
                    tOut.close();
                }
                if ( pIn != null )
                {
                    pIn.close();
                }
            }
            catch ( IOException e )
            {
                throw new MavenReportException( "Unable to close report file report...", e );
            }
        }
    }

    File getFitnesseReportDir()
        throws MavenReportException
    {
        File tExecutionFile;
        if ( fitnesseOutputDirectory != null )
        {
            getLog().info( "Using the specified fitnesse outpout directory " + fitnesseOutputDirectory );
            tExecutionFile = fitnesseOutputDirectory;
        }
        else
        {
            getLog().debug( "Trying to find the fitnesse default dir..." );
            tExecutionFile = new File( workingDir + "/fitnesse" );
            if ( !tExecutionFile.exists() )
            {
                getLog().info( "Fitnesse default report not found, " + tExecutionFile );
                getLog().debug( "Trying to find the fitnesse with clover dir..." );
                tExecutionFile = new File( workingDir + "/clover/fitnesse" );
            }
        }

        FilenameFilter filterSvnDirectory = new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                return !name.equals( ".svn" );
            }
        };

        if ( !tExecutionFile.exists() || !tExecutionFile.isDirectory() || tExecutionFile.list(filterSvnDirectory).length == 0 )
        {
            String tError =
                "Can't find any report in the following folder: [" + fitnesseOutputDirectory.getAbsolutePath() + "], ["
                    + new File( workingDir + "/fitnesse" ).getAbsolutePath() + "] or ["
                    + tExecutionFile.getAbsolutePath() + "]";

            throw new MavenReportException( tError );
        }
        else
        {
            return tExecutionFile;
        }
    }

    void createIndex()
        throws MavenReportException
    {
        if ( outputDirectory.listFiles().length > 1 )
        {
            File tIndex = new File( this.xmlOutputDirectory + "/index.xml" );
            FileWriter tWriter = null;
            int tNbPage = 0;
            try
            {
                tIndex.createNewFile();
                tWriter = new FileWriter( tIndex );
                tWriter.write( "<document>\n" );
                tWriter.write( " <properties>\n" );
                tWriter.write( "   <title>maven-fitnesse-plugin - execution report</title>\n" );
                tWriter.write( " </properties>\n" );
                tWriter.write( " <body>\n" );

                tWriter.write( "<section name=\"List of the Fitnesse Pages:\">\n" );
                tWriter.write( "<ul>\n" );
                FitnessePage curChil;
                File[] tFileArray = outputDirectory.listFiles();
                for ( int i = 0; i < tFileArray.length; i++ )
                {
                    curChil = new FitnessePage( tFileArray[i] );
                    if ( curChil.isFitnessePageResult() )
                    {
                        tWriter.write( "<li><a href=\"" + curChil.getName() + "\">" + curChil.getFitnessePageName()
                            + ".html</a></li>\n" );
                        tNbPage++;
                    }
                }
                tWriter.write( "</ul>\n" );
                tWriter.write( "</section>\n" );
                tWriter.write( "</body>\n" );
                tWriter.write( "</document>\n" );
                tWriter.flush();
            }
            catch ( IOException e )
            {
                throw new MavenReportException( "Unable to create index file " + tIndex.getAbsolutePath(), e );
            }
            finally
            {
                if ( tWriter != null )
                {
                    try
                    {
                        tWriter.close();
                    }
                    catch ( IOException e )
                    {
                        throw new MavenReportException( "Unable to close index file " + tIndex.getAbsolutePath(), e );
                    }
                }
            }
            if ( tNbPage == 1 )
            {
                tIndex.delete();
            }
        }
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory()
    {
        return this.outputDirectory.getAbsoluteFile().toString();
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    protected MavenProject getProject()
    {
        return project;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    protected Renderer getSiteRenderer()
    {
        return this.siteRenderer;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( Locale locale )
    {
        return "Fitnesse report";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( Locale locale )
    {
        return "Fitnesse report";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        File tDir;
        FitnessePage curChild = null, tGoodFile = null;
        try
        {
            tDir = getFitnesseReportDir();
            String[] tChildren = tDir.list();
            int tNbPage = 0;
            for ( int i = 0; i < tChildren.length; i++ )
            {
                curChild = new FitnessePage( new File( tChildren[i] ) );
                if ( curChild.isFitnessePageResult() )
                {
                    tNbPage++;
                    tGoodFile = curChild;
                }
            }
            if ( tNbPage == 1 )
            {
                return "fitnesse/" + FitnesseRunnerMojo.FITNESSE_RESULT_PREFIX + "_" + tGoodFile.getFitnessePageName();
            }
            else
            {
                return "fitnesse/index";
            }
        }
        catch ( MavenReportException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Always return true as we're using the report generated by Clover rather than creating our own report.
     * 
     * @return true
     */
    public boolean isExternalReport()
    {
        return true;
    }

    void setWorkingDir( File pWorkingDir )
    {
        workingDir = pWorkingDir;
    }

    void setFitnesseOutputDirectory( File pFitnesseOutputDirectory )
    {
        fitnesseOutputDirectory = pFitnesseOutputDirectory;
    }

    void setOutputDirectory( File pOutputDirectory )
    {
        outputDirectory = pOutputDirectory;
    }

    public void setXmlOutputDirectory( File xmlOutputDirectory )
    {
        this.xmlOutputDirectory = xmlOutputDirectory;
    }

}
