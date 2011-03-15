/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.commons.io.FileUtils;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.mojo.webtest.components.ResourceCopy;
import org.codehaus.mojo.webtest.components.XslTransformer;
import org.codehaus.mojo.webtest.components.ReportCollector;
import org.codehaus.mojo.webtest.components.XomHelper;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Date;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;

/**
 * Creates the HTML report for a test run based on XSLT.
 *
 * @author Siegfried Goeschl
 * @phase site
 * @goal report
 */
public class WebtestReportMojo extends AbstractMavenReport
{
    /**
     * List of images copied to the webreport directory
     */
    private static final String[] IMAGE_FILES = {
        "canoo.gif",
        "collapseall.png",
        "expandall.png",
        "expandMinus.png",
        "expandPlus.png",
        "favicon.ico",
        "failed.gif",
        "less.gif",
        "more.gif",
        "ok.gif",
        "optional.gif",
        "todo.gif",
        "webtest.jpg"
    };

    /**
     * List of web resources copied to the webreport directory
     */
    private static final String[] WEB_RESOURCE_FILES = {
        "report.css",
        "responseBrowser.html",
        "responseBrowser.js",
        "responseBrowserCommands.html",
        "responseBrowserPreviews.html",
        "showHide.js",
        "sorttable.js"
    };

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     *
     * @component
     */
    private SiteRenderer siteRenderer;

    /**
     * <i>Maven Internal</i>: Project to interact with.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Defines the resultpath of saved responses (for example last response).
     * Under this directory Canoo WebTest will created test-related subfolders,
     * e.g. "001_GreenTest"
     *
     * @parameter expression="${project.build.directory}/webtest/webtest-results"
     * @required
     */
    private File resultpath;


    /**
     * Defines the XSLT used to generate the WebTest report overview
     * covering all test.
     *
     * @parameter expression="WebTestReportOverview.xsl"
     * @required
     */
    private String reportoverviewxsl;

    /**
     * The subdirectory created under site containing
     * the webtest report.
     *
     * @parameter expression="${basedir}/target/site/webtest"
     * @required
     */
    private File reportdirectory;

    /**
     * The name of the created report using XSLT
     *
     * @parameter expression="index.html"
     * @required
     */
    private String reportname;

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( Locale locale )
    {
        return "Canoo WebTest Report";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( Locale locale )
    {
        return "Canoo WebTest Report.";
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory()
    {
        return this.reportdirectory.getAbsolutePath();
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        return "webtest/index";
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#isExternalReport()
     */
    public boolean isExternalReport()
    {
        return true;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
     */
    public boolean canGenerateReport()
    {
        try
        {
            ReportCollector walker = new ReportCollector( "WebTestReport.xml" );
            File[] currResultFileList = walker.run( this.resultpath );
            return ( currResultFileList.length > 0 );
        }
        catch ( Exception e )
        {
            this.getLog().error( "Unable to search through the webtest result directories" );
            return false;
        }
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
    protected SiteRenderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)
     */
    public void generate( Sink sink, Locale locale )
        throws MavenReportException
    {
        executeReport( locale );
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     */
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        try
        {
            this.createReportResources();
            this.createHtmlSingleTestReport();
            this.createXmlWebTestSummary();
            this.createXmlWebTestOverview();
            this.copyHtmlResponses();
            this.createHtmlWebTestOverview();
        }
        catch ( Exception e )
        {
            String msg = "Generating the Canoo WebTest report failed";
            throw new MavenReportException( msg, e );
        }
    }

    /**
     * Copy the plugin resources to the report directory.
     *
     * @throws Exception copying the resources failed
     */
    private void createReportResources() throws Exception
    {
        ResourceCopy resourceCopy = new ResourceCopy();

        // copy the images used in the report under 'resources/images"
        File reportResouceDirectory = new File( this.reportdirectory, "resources" );
        File reportImageDirectory = new File( reportResouceDirectory, "images" );
        resourceCopy.copy( "/webtest/images/", IMAGE_FILES, reportImageDirectory );

        // copy the web resource files under 'resources"
        resourceCopy.copy( "/webtest/resources/", WEB_RESOURCE_FILES, reportResouceDirectory );
    }

    /**
     * Creates the HTML report of a single test.
     *
     * @throws Exception creating the HTML report failed
     */
    private void createHtmlSingleTestReport() throws Exception
    {
        ReportCollector walker;
        File[] xmlReportList;
        String xsltName = "WebTestReport.xsl";
        String xsltResourceName = "/webtest/xslt/" + xsltName;

        // get the list of reports to be transformed
        walker = new ReportCollector( "WebTestReport.xml" );
        xmlReportList = walker.run( this.resultpath );

        // create parameters to control the XSLT
        Properties params = new Properties();
        params.setProperty( "resources.dir", "../resources" );
        params.setProperty( "reporttime", new Date().toString() );
        params.setProperty( "title", this.project.getArtifactId() );

        // transform all "WebTestReport.xml" into "WebTestReport.html"
        XslTransformer xslTransformer = new XslTransformer(getLog());
        for (File xmlReportFile : xmlReportList)
        {
            File xmlReportDirectory = xmlReportFile.getParentFile();
            File xmlResultFile = new File(xmlReportDirectory, "WebTestReport.html");
            StreamSource xslin = new StreamSource(this.getClass().getResourceAsStream(xsltResourceName));
            StreamSource xmlin = new StreamSource(new FileInputStream(xmlReportFile));
            Result result = new StreamResult(new FileOutputStream(xmlResultFile));
            xslTransformer.transform(xsltName, xslin, xmlin, result, params);
        }
    }

    /**
     * Creates the 'WebTestSummary.xml' report of a single test.
     *
     * @throws Exception creating the HTML report failed
     */
    private void createXmlWebTestSummary() throws Exception
    {
        ReportCollector walker;
        File[] xmlReportList;
        String xsltName = "WebTestReportSummaryExtractor.xsl";
        String xsltResourceName = "/webtest/xslt/" + xsltName;

        // get the list of reports to be transformed
        walker = new ReportCollector( "WebTestReport.xml" );
        xmlReportList = walker.run( this.resultpath );

        // transform all "WebTestReport.xml" into "WebTestSummary.xml"
        XslTransformer xslTransformer = new XslTransformer(getLog());
        for (File xmlReportFile : xmlReportList)
        {
            File xmlReportDirectory = xmlReportFile.getParentFile();
            File xmlResultFile = new File(xmlReportDirectory, "WebTestSummary.xml");
            StreamSource xslin = new StreamSource(this.getClass().getResourceAsStream(xsltResourceName));
            StreamSource xmlin = new StreamSource(new FileInputStream(xmlReportFile));
            Result result = new StreamResult(new FileOutputStream(xmlResultFile));
            xslTransformer.transform(xsltName, xslin, xmlin, result, null);
        }
    }

    /**
     * Creates the 'index.html' report for all tests.
     *
     * @throws Exception creating the HTML report failed
     */
    private void createHtmlWebTestOverview() throws Exception
    {
        String xsltName = this.reportoverviewxsl;
        String xsltResourceName = "/webtest/xslt/" + xsltName;

        // create parameters to control the XSLT
        Properties params = new Properties();
        params.setProperty( "resources.dir", "./resources" );
        params.setProperty( "reporttime", new Date().toString() );
        params.setProperty( "title", this.project.getArtifactId() );

        // do the transformation
        File xmlInFile = new File( this.reportdirectory, "WebTestOverview.xml" );
        File xmlResultFile = new File( this.reportdirectory, this.reportname );
        StreamSource xslin = new StreamSource( this.getClass().getResourceAsStream( xsltResourceName ) );
        StreamSource xmlin = new StreamSource( new FileInputStream( xmlInFile ) );
        Result result = new StreamResult( new FileOutputStream( xmlResultFile ) );
        XslTransformer xslTransformer = new XslTransformer(getLog());
        xslTransformer.transform( xsltName, xslin, xmlin, result, params );
    }

    /**
     * Creates the 'WebTestOverview.xml' as source for the HTML test report.
     *
     * @throws Exception creating the HTML report failed
     */
    private void createXmlWebTestOverview() throws Exception
    {
        ReportCollector walker;
        File[] xmlWebTestSummaryList;

        // get the list of 'WebTestSummary.xml' files
        walker = new ReportCollector( "WebTestSummary.xml" );
        xmlWebTestSummaryList = walker.run( this.resultpath );

        // create XML documents of this file list
        XomHelper xomHelper = new XomHelper();
        Document[] documentList = xomHelper.parse( xmlWebTestSummaryList );

        // build the 'WebTestOverview.xml'
        Element overview = new Element( "overview" );
        overview.addAttribute( new Attribute( "Implementation-Title", WebtestConstants.IMPLEMENTATION_TITLE ) );
        overview.addAttribute( new Attribute( "Implementation-Version", WebtestConstants.IMPLEMENTATION_VERSION ) );
        for ( int i = 0; i < xmlWebTestSummaryList.length; i++ )
        {
            String currFolderName = xmlWebTestSummaryList[i].getParentFile().getName();
            Document currDocument = documentList[i];
            Element currFolder = new Element( "folder" );
            currFolder.addAttribute( new Attribute( "name", currFolderName ) );
            xomHelper.appendDocument( currFolder, currDocument );
            overview.appendChild( currFolder );
        }

        // save the XML document
        Document document = new Document( overview );
        xomHelper.toFile( document, new File( this.reportdirectory, "WebTestOverview.xml" ) );
    }

    /**
     * Copy the HTML reponses to the report directory.
     *
     * @throws Exception copying the HTML resources failed
     */
    private void copyHtmlResponses() throws Exception
    {
        FileUtils.copyDirectory( this.resultpath, this.reportdirectory );
    }
}
