package org.codehaus.mojo.dashboard.report.plugin;

/*
 * Copyright 2006 David Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject;
import org.codehaus.mojo.dashboard.report.plugin.configuration.Configuration;
import org.codehaus.mojo.dashboard.report.plugin.configuration.ConfigurationService;
import org.codehaus.mojo.dashboard.report.plugin.configuration.ConfigurationServiceException;
import org.codehaus.mojo.dashboard.report.plugin.configuration.IConfigurationService;
import org.codehaus.mojo.dashboard.report.plugin.hibernate.HibernateService;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.FileResourceLoader;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringUtils;

/**
 * A Dashboard report which aggregates all other report results.
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * @goal dashboard
 * @phase post-site
 */

public class DashBoardReportMojo extends AbstractMavenReport
{
    /**
     * The maven project
     *
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * Directory containing The generated DashBoard report Datafile "dashboard-report.xml".
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Renderer
     *
     * @component 
     */
    private Renderer siteRenderer;

    /**
     * <p>
     * The generated DashBoard report Datafile.
     * </p>
     *
     * @parameter default-value="dashboard-report.xml"
     * @readonly
     */
    protected String dashboardDataFile;

    /**
     * The filename to use for the report.
     *
     * @parameter default-value="dashboard-report"
     * @readonly
     */
    private String outputName;

    /**
     * The local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * number of XRef JDepend/Cobertura packages to export in dashboard summary page
     *
     * @parameter default-value="10"
     */
    private int nbExportedPackagesSummary;

    /**
     * Project builder
     *
     * @component
     */
    protected MavenProjectBuilder mavenProjectBuilder;

    /**
     * Hibernate Service
     *
     * @component
     */
    protected HibernateService hibernateService;

    /**
     * Hibernate dialect
     *
     * @parameter expression="${dialect}"
     */
    protected String dialect;

    /**
     * Hibernate driver class
     *
     * @parameter expression="${driverClass}"
     */
    protected String driverClass;

    /**
     * Hibernate connection URL
     *
     * @parameter expression="${connectionUrl}"
     */
    protected String connectionUrl;

    /**
     * Hibernate database username
     *
     * @parameter expression="${username}"
     */
    protected String username;

    /**
     * Hibernate database password
     *
     * @parameter expression="${password}"
     */
    protected String password;

    /**
     * <p>
     * Specifies the location of the XML configuration to use.
     * </p>
     * <p>
     * Potential values are a filesystem path, a URL, or a classpath resource. This parameter expects that the contents
     * of the location conform to the xml format (<a href="http://mojo.codehaus.org/dashboard-maven-plugin/">Dashboard
     * Maven plugin</a>) configuration .
     * </p>
     * <p>
     * This parameter is resolved as resource, URL, then file. If successfully resolved, the contents of the
     * configuration is copied into the <code>${project.build.directory}/default-dashboard-config.xml</code> file
     * before being passed to dashboard as a configuration.
     * </p>
     * <p>
     * There are 1 predefined config.
     * </p>
     * <ul>
     * <li><code>config/default-dashboard-config.xml</code>: default config.</li>
     * </ul>
     *
     * @parameter expression="${configLocation}" default-value="config/default-dashboard-config.xml"
     */
    private String configLocation;

    /**
     * @component
     * @required
     * @readonly
     */
    private ResourceManager locator;

    /**
     * Used to generate a dashboard report as Maven 1, only for multi-modules project. It also merges the 2 reports
     * (Summary and detailled) as only one.
     *
     * @parameter expression="${m1LikeRendering}" default-value="false"
     */
    private boolean m1LikeRendering;

    /**
     * This parameter is used to keep the version as a discriminant criteria of a project. If set as "true", the data of
     * reports will be specific to this version of a project. If set as "false", the data of reports will be associated
     * with the project regardless of its version.
     *
     * @parameter expression="${keepVersionAsDiscriminantCriteria}" default-value="true"
     */
    private boolean keepVersionAsDiscriminantCriteria;

    /**
     * <p>
     * This parameter is used to disable the graphics generation to avoid unexpected error when X11 window server not
     * installed on Linux/Unix machine.
     * </p>
     * <p>
     * <ul>
     * <li>If set as "true" or not set (default value is used) , all graphics will be generated.</li>
     * <li>> If set as "false", the Dashboard won't generate graphics for summary and detailled reports and the
     * historic report will be disabled.</li>
     * </ul>
     * </p>
     *
     * @parameter expression="${generateGraphs}" default-value="true"
     */
    private boolean generateGraphs;

    private DashBoardUtils dashBoardUtils;

    private Locale locale;

    /**
     *
     */
    protected void executeReport( Locale arg0 ) throws MavenReportException
    {
        this.locale = arg0;

        // Thanks to the Checkstyle Maven plugin team for this part of code.
        this.locator.addSearchPath( FileResourceLoader.ID, this.project.getFile().getParentFile().getAbsolutePath() );
        this.locator.addSearchPath( "url", "" );
        this.locator.setOutputDirectory( new File( this.project.getBuild().getDirectory() ) );
        // Thanks end.
        this.getLog().info( "MultiReportMojo project = " + this.project.getName() );
        this.getLog().info( "MultiReportMojo nb modules = " + this.project.getModules().size() );
        this.getLog().info( "MultiReportMojo base directory = " + this.project.getBasedir() );
        this.getLog().info( "MultiReportMojo output directory = " + this.outputDirectory );
        this.getLog().info( "MultiReportMojo report output directory = " + this.getReportOutputDirectory() );
        this.getLog().info( "MultiReportMojo project language = "
                                            + this.project.getArtifact().getArtifactHandler().getLanguage() );
        this.copyStaticResources();
        this.dashBoardUtils =
            DashBoardUtils.getInstance( this.getLog(), this.mavenProjectBuilder, this.localRepository, false );

        if ( canGenerateReport() )
        {
            DashBoardMavenProject mavenProject = null;

            if ( isDBAvailable() )
            {
                configureHibernateDriver();
            }

            mavenProject =
                this.dashBoardUtils.getDashBoardMavenProject( this.project, this.dashboardDataFile, 
                                                              new Date( System.currentTimeMillis() ) );
            this.dashBoardUtils.saveXMLDashBoardReport( this.project, mavenProject, this.dashboardDataFile );

            if ( mavenProject != null )
            {
                boolean isSummary = ( mavenProject.getModules() != null && !mavenProject.getModules().isEmpty() );

                AbstractDashBoardGenerator reportGenerator = null;

                if ( isSummary && this.m1LikeRendering )
                {
                    reportGenerator =
                        new DashBoardMaven1ReportGenerator( mavenProject, isDBAvailable(), this.getLog() );
                }
                else
                {
                    reportGenerator =
                        new DashBoardReportGenerator( mavenProject, isSummary, isDBAvailable(),
                                                      this.generateGraphs, this.getLog() );
                }
                reportGenerator.setImagesPath( getReportOutputDirectory() + "/images" );
                reportGenerator.setNbExportedPackagesSummary( this.nbExportedPackagesSummary );
                reportGenerator.doGenerateReport( getBundle( this.locale ), getSink() );
                if ( isDBAvailable() )
                {
                    // Thanks to the Checkstyle Maven plugin team for this part of code.
                    ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
                    Configuration dashConfig = null;
                    try
                    {
                        // dashboard will always use the context classloader in order
                        // to load resources (xml schema)
                        ClassLoader dashboardClassLoader = DashBoardUtils.class.getClassLoader();
                        Thread.currentThread().setContextClassLoader( dashboardClassLoader );

                        String configFile = this.getConfigFile();
                        getLog().info( "getConfigFile() = " + configFile );
                        IConfigurationService configService = new ConfigurationService( configFile );

                        dashConfig = configService.getConfiguration();

                        if ( !configService.isValidConfig() )
                        {
                            List warningMsg = configService.getWarningMessages();

                            Iterator iter = warningMsg.iterator();
                            while ( iter.hasNext() )
                            {
                                getLog().error( (String) iter.next() );
                            }
                            throw new MavenReportException( "The maven-dashboard-config.xml is not valid. " +
                            		"See error messages above or see the maven-dashboard-config.xsd file." );
                        }

                    }
                    catch ( ConfigurationServiceException e )
                    {
                        this.getLog().error( "DashBoardHistoricReportMojo executeReport() failed.", e );
                        throw new MavenReportException( "The maven-dashboard-config.xml is not valid. " +
                        		"See error messages above or see the maven-dashboard-config.xsd file." );
                    }
                    catch ( Exception e )
                    {
                        this.getLog().error( "DashBoardHistoricReportMojo executeReport() failed.", e );
                        throw new MavenReportException( "The maven-dashboard-config.xml is not valid. " +
                        		"See error messages above or see the maven-dashboard-config.xsd file." );
                    }
                    finally
                    {
                        // be sure to restore original context classloader
                        Thread.currentThread().setContextClassLoader( currentClassLoader );
                    }
                    // Thanks end.
                    try
                    {
                        if ( dashConfig != null )
                        {
                            DashBoardHistoricReportGenerator histoReportGenerator =
                                new DashBoardHistoricReportGenerator( mavenProject, this.hibernateService, dashConfig,
                                                                      this.keepVersionAsDiscriminantCriteria,
                                                                      this.generateGraphs, getLog() );
                            histoReportGenerator.setImagesPath( getReportOutputDirectory() + "/images" );
                            histoReportGenerator.doGenerateReport( getBundle( locale ), getSink() );
                        }
                    }
                    catch ( Exception e )
                    {
                        this.getLog().error( "DashBoardHistoricReportMojo executeReport() failed.", e );
                    }
                }
                if ( isSummary && !this.m1LikeRendering )
                {
                    try
                    {
                        
                        DashBoardMultiReportGenerator detailReportGenerator =
                            new DashBoardMultiReportGenerator( mavenProject, isDBAvailable(),
                                                               this.generateGraphs, getLog() );
                        detailReportGenerator.setImagesPath( getReportOutputDirectory() + "/images" );
                        detailReportGenerator.doGenerateReport( getBundle( locale ), getSink() );
                    }
                    catch ( Exception e )
                    {
                        this.getLog().error( "DashBoardReportMojo executeReport() failed.", e );
                    }
                }
            }
        }

    }

    /**
     *
     */
    protected String getOutputDirectory()
    {
        return this.outputDirectory.getPath();
    }

    /**
     *
     */
    protected MavenProject getProject()
    {
        return this.project;
    }

    protected Renderer getSiteRenderer()
    {
        return this.siteRenderer;
    }

    public String getDescription( Locale locale )
    {
        String description = "";
        if ( this.project.getModules().size() > 0 )
        {
            description = this.getBundle( locale ).getString( "dashboard.multireport.description" );
        }
        else
        {
            description = this.getBundle( locale ).getString( "dashboard.report.description" );
        }
        return description;
    }

    public String getName( Locale locale )
    {
        String name = "";
        if ( this.project.getModules().size() > 0 )
        {
            name = this.getBundle( locale ).getString( "dashboard.multireport.name" );
        }
        else
        {
            name = this.getBundle( locale ).getString( "dashboard.report.name" );
        }
        return name;
    }

    public String getOutputName()
    {
        return this.outputName;
    }

    public ResourceBundle getBundle( Locale locale )
    {
        return ResourceBundle.getBundle( "dashboard-report-plugin", locale, this.getClass().getClassLoader() );
    }

    public boolean usePageLinkBar()
    {
        return true;
    }

    private InputStream getSiteDescriptor() throws MojoExecutionException
    {
        String siteDescriptorContent = "";
        try
        {
            siteDescriptorContent = IOUtil.toString( this.getClass().getResourceAsStream( "/default-report.xml" ) );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "The site descriptor cannot be read!", e );
        }
        Map props = new HashMap();
        props.put( "reports", this.getReportsMenu() );
        if ( this.getProject().getName() != null )
        {
            props.put( "project.name", this.getProject().getName() );
        }
        else
        {
            props.put( "project.name", "NO_PROJECT_NAME_SET" );
        }
        if ( this.getProject().getUrl() != null )
        {
            props.put( "project.url", this.getProject().getUrl() );
        }
        else
        {
            props.put( "project.url", "NO_PROJECT_URL_SET" );
        }
        siteDescriptorContent = StringUtils.interpolate( siteDescriptorContent, props );
        return new StringInputStream( siteDescriptorContent );
    }

    private String getReportsMenu()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "<menu name=\"Project Reports\">\n" );
        buffer.append( "  <item name=\"Root\" href=\"/project-reports.html\"/>\n" );
        buffer.append( "  <item name=\"" + this.getName( this.locale ) + "\" href=\"/" + this.getOutputName()
                        + ".html\"/>\n" );
        buffer.append( "</menu>\n" );
        return buffer.toString();
    }

    public boolean canGenerateReport()
    {
        if ( this.project.getCollectedProjects().size() < this.project.getModules().size() )
        {
            this.getLog().info( "DashBoardReportMojo: Not recursive into sub-projects - skipping report." );
            return false;
        }
        return true;
    }

    protected void configureHibernateDriver()
    {
        this.hibernateService.setDialect( this.dialect );
        this.hibernateService.setDriverClass( this.driverClass );
        this.hibernateService.setConnectionUrl( this.connectionUrl );
        this.hibernateService.setUsername( this.username );
        this.hibernateService.setPassword( this.password );
    }

    protected boolean isDBAvailable()
    {
        boolean isDBAvailable = false;
        if ( ( this.dialect != null && this.dialect.length() > 0 )
                        && ( this.driverClass != null && this.driverClass.length() > 0 )
                        && ( this.connectionUrl != null && this.connectionUrl.length() > 0 )
                        && ( this.username != null && this.username.length() > 0 )
                        && ( this.password != null && this.password.length() > 0 ) )
        {
            isDBAvailable = true;
        }
        return isDBAvailable;
    }

    /**
     * @return
     * @throws MavenReportException
     */
    private String getConfigFile() throws MavenReportException
    {
        // Thanks to the Checkstyle Maven plugin team for this part of code.
        try
        {
            this.getLog().info( "getConfigFile() = " + this.configLocation );

            File configFile = this.locator.getResourceAsFile( this.configLocation, "default-dashboard-config.xml" );

            if ( configFile == null )
            {
                throw new MavenReportException( "Unable to process dashboard config location: " + this.configLocation );
            }
            return configFile.getAbsolutePath();
        }
        catch ( org.codehaus.plexus.resource.loader.ResourceNotFoundException e )
        {
            throw new MavenReportException( "Unable to find dashboard configuration file at location "
                            + this.configLocation, e );
        }
        catch ( FileResourceCreationException e )
        {
            throw new MavenReportException( "Unable to process dashboard configuration file location "
                            + this.configLocation, e );
        }
        // Thanks end.
    }

    /**
     * Thanks to the maven-changes-plugin team for this part of code.
     *
     * @throws MavenReportException
     */
    private void copyStaticResources() throws MavenReportException
    {
        String resourceNames[] =
            { "images/down.gif", "images/Down-green-full.jpg", "images/Down-red-full.jpg",
                "images/Down-orange-full.jpg", "images/next.gif", "images/previous.gif", "images/up.gif",
                "images/Stable-green-full.jpg", "images/Stable-red-full.jpg", "images/Stable-orange-full.jpg",
                "images/Up-green-full.jpg", "images/Up-red-full.jpg", "images/Up-orange-full.jpg",
                "css/dashboard.css", "css/dashboard2.css", "css/dashboard2IE.css" };
        try
        {

            this.getLog().debug( "Copying static resources." );
            for ( int i = 0; i < resourceNames.length; i++ )
            {
                URL url = this.getClass().getClassLoader().getResource( resourceNames[i] );
                FileUtils.copyURLToFile( url, new File( this.getReportOutputDirectory(), resourceNames[i] ) );
            }
        }
        catch ( IOException e )
        {
            throw new MavenReportException( "Unable to copy static resources." );
        }
    }
}
