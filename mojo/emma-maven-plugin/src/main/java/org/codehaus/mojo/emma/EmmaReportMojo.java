package org.codehaus.mojo.emma;

/*
 * The MIT License
 *
 * Copyright (c) 2007-8, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.emma.task.ReportTask;
import org.codehaus.plexus.util.ReaderFactory;

/**
 * Instruments, tests, and generates an EMMA report.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 * @goal emma
 * @execute phase="test" lifecycle="emma"
 * @requiresDependencyResolution test
 */
public class EmmaReportMojo
    extends AbstractMavenReport
{
    /**
     * The bundle base name for i18n and l10n.
     */
    private static final String BUNDLE_BASENAME = EmmaReportMojo.class.getPackage().getName() + ".Resources";

    /**
     * Output directory for the report.
     * 
     * @parameter default-value="${project.reporting.outputDirectory}/emma"
     * @required
     */
    protected File outputDirectory;

    /**
     * Source locations.
     * 
     * @parameter
     */
    protected File[] sourcePaths;

    /**
     * Location to store class coverage metadata.
     * 
     * @parameter expression="${emma.metadataFile}" default-value="${project.build.directory}/coverage.em"
     */
    protected File metadataFile;

    /**
     * Class coverage data files.
     * 
     * @parameter
     */
    protected File[] dataFiles;

    /**
     * Report encoding.
     * 
     * @parameter expression="${emma.report.encoding}" default-value="${project.reporting.outputEncoding}"
     */
    protected String outputEncoding;

    /**
     * Specifies the amount of detail to be included in the generated coverage reports. Valid values are:
     * <code>all</code>, <code>package</code>, <code>source</code>, <code>class</code>, <code>method</code>.
     * 
     * @parameter expression="${emma.report.depth}" default-value="method"
     */
    protected String depth;

    /**
     * Specifies which report columns and in which order to use for report generation, as a comma-separated list of
     * column ids.
     * 
     * @parameter expression="${emma.report.columns}" default-value="name,class,method,block,line"
     */
    protected String columns;

    /**
     * Specifies report column sorting order, as a comma-separated list of columns ids prefixed with "+" for ascending
     * or "-" for descending directions.
     * 
     * @parameter expression="${emma.report.sort}" default-value="+block,+name,+method,+class"
     */
    protected String sort;

    /**
     * Specifies the threshold coverage metrics values for a given set of columns.
     * 
     * @parameter expression="${emma.report.metrics}" default-value="method:70,block:70,line:80,class:100"
     */
    protected String metrics;

    /**
     * Sets EMMA verbosity level to <code>verbose</code>.
     * 
     * @parameter expression="${emma.verbose}" default-value="false"
     */
    protected boolean verbose;

    /**
     * Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Plugin classpath.
     * 
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    protected List pluginClasspath;

    /**
     * Site renderer.
     * 
     * @component
     */
    protected Renderer siteRenderer;

    /**
     * The bundles.
     */
    private final Map bundles = new WeakHashMap();

    /**
     * Check parameters.
     * 
     * @throws MavenReportException if any parameters are wrong
     */
    protected void checkParameters()
        throws MavenReportException
    {
        if ( getLog().isDebugEnabled() )
        {
            verbose = true;
        }

        if ( dataFiles == null )
        {
            dataFiles = new File[] { new File( project.getBasedir(), "coverage.ec" ) };
        }
        if ( sourcePaths == null )
        {
            sourcePaths = new File[] { new File( project.getBuild().getSourceDirectory() ) };
        }
    }

    /**
     * Executes the report for a specific locale.
     * 
     * @param locale the locale.
     * @throws MavenReportException if something goes wrong.
     */
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        checkParameters();

        if ( !canGenerateReport() )
        {
            return;
        }

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Output directory: " + outputDirectory.getAbsolutePath() );

            getLog().debug( "Source paths:" );
            for ( int i = 0; i < sourcePaths.length; ++i )
            {
                getLog().debug( " o " + sourcePaths[i] );
            }
        }

        // aggregate several EMMA coverage data files
        final File[] newDataFiles = EmmaUtils.fixDataFileLocations( project, dataFiles );

        final ReportTask task = new ReportTask();
        task.setVerbose( verbose );
        task.setMetadataFile( metadataFile );
        task.setDataFiles( newDataFiles );
        task.setSourcePaths( sourcePaths );
        task.setOutputDirectory( outputDirectory );
        task.setColumns( columns );
        task.setDepth( depth );
        task.setEncoding( ( outputEncoding != null ) ? outputEncoding : ReaderFactory.UTF_8 );
        task.setMetrics( metrics );
        task.setSort( sort );

        try
        {
            task.execute();
        }
        catch ( IOException e )
        {
            throw new MavenReportException( "Failed to generate EMMA report", e );
        }
    }

    /**
     * Gets the output directory.
     * 
     * @return the output directory.
     */
    protected String getOutputDirectory()
    {
        return outputDirectory.getAbsolutePath();
    }

    /**
     * Gets the maven project.
     * 
     * @return the maven project.
     */
    protected MavenProject getProject()
    {
        return project;
    }

    /**
     * Gets the site renderer.
     * 
     * @return the site renderer.
     */
    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /**
     * Gets the description in the specified locale.
     * 
     * @param locale the locale.
     * @return the description in the specified locale.
     */
    public String getDescription( Locale locale )
    {
        return getResourceBundle( locale ).getString( "emma.plugin.description" );
    }

    /**
     * Gets the name in the specified locale.
     * 
     * @param locale the locale.
     * @return the name in the specified locale.
     */
    public String getName( Locale locale )
    {
        return getResourceBundle( locale ).getString( "emma.plugin.name" );
    }

    /**
     * Gets the output name.
     * 
     * @return the output name.
     */
    public String getOutputName()
    {
        return "emma/index";
    }

    /**
     * Returns <code>true</code> as this is an external report.
     * 
     * @return <code>true</code> as this is an external report.
     */
    public boolean isExternalReport()
    {
        return true;
    }

    /**
     * Checks if the report can be generated.
     * 
     * @return <code>true</code> if the report can be generated.
     */
    public boolean canGenerateReport()
    {
        final boolean ready = metadataFile.exists();
        if ( !ready )
        {
            getLog().info(
                           "Not generating EMMA report as the metadata file (" + metadataFile.getName()
                               + ") could not be found" );
        }
        return ready;
    }

    /**
     * Gets the resource bundle for the specified locale.
     * 
     * @param locale the locale.
     * @return the resource bundle for the specified locale.
     */
    protected ResourceBundle getResourceBundle( Locale locale )
    {
        ResourceBundle bundle = (ResourceBundle) bundles.get( locale );
        if ( bundle == null )
        {
            bundle = ResourceBundle.getBundle( BUNDLE_BASENAME, locale );
            bundles.put( locale, bundle );
        }
        return bundle;
    }
}
