/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.mojo.chronos.chart.ChartRenderer;
import org.codehaus.mojo.chronos.chart.ChartRendererImpl;
import org.codehaus.mojo.chronos.chart.ChartUtil;
import org.codehaus.mojo.chronos.chart.GraphGenerator;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.report.ReportGenerator;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;

/**
 * Creates a report of the currently executed performancetest in html format.
 * 
 * @author ksr@lakeside.dk
 * @goal report
 */
// Line merged from Atlession
// Removed the @execution phase=verify descriptor to prevent double lifecycle execution
// * @execute phase=verify
public class ReportMojo extends AbstractMavenReport {

    private static final int DEFAULT_DURATION = 20000;

    /**
     * Location (directory) where generated html will be created.
     * 
     * @parameter expression="${project.build.directory}/site "
     */
    protected String outputDirectory;

    /**
     * Doxia Site Renderer.
     * 
     * @component role="org.codehaus.doxia.site.renderer.SiteRenderer"
     * @required
     * @readonly
     */
    protected SiteRenderer siteRenderer;

    /**
     * Maven Project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The id of the report and the name of the generated html-file. If no id is defined, the dataid is used
     * 
     * @parameter
     */
    protected String reportid;

    /**
     * The id of the data, to create a report from.
     * 
     * @parameter default-value = "performancetest"
     */
    protected String dataid;

    /**
     * The title of the generated report.
     * 
     * @parameter
     */
    protected String title;

    /**
     * The description of the generated report.
     * 
     * @parameter
     */
    protected String description;

    /**
     * responsetimeDivider may be used when the response time of a single request is so low that the granularity of the
     * system timer corrupts the response time measured.
     * 
     * @parameter default-value = 1
     */
    protected int responsetimedivider = 1;

    /**
     * The timeinterval (in millis) to base moving average calculations on.
     * 
     * @parameter default-value = 20000
     */
    protected int averageduration = DEFAULT_DURATION; // 20 seconds

    /**
     * The timeinterval (in millis) to count threads within.
     * 
     * @parameter default-value = 20000
     */
    protected int threadcountduration = DEFAULT_DURATION; // 20 seconds

    /**
     * Should a summary of the tests taken together be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showsummary = true;

    /**
     * Should a summary of the tests include graphs?
     * 
     * @parameter default-value=true
     */
    protected boolean showsummarycharts = true;

    /**
     * Should details of each individual test be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showdetails = true;

    /**
     * Should responsetimes be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showresponse = true;

    /**
     * Should a histogram be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showhistogram = true;

    /**
     * Should a graph of throughput be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showthroughput = true;

    /**
     * Will information tables be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showinfotable = true;

    /**
     * Will the information tables contain timing info?
     * 
     * @parameter default-value=true
     */
    protected boolean showtimeinfo = true;

    /**
     * Will graphs of responsetimes and histogram show 95 percentiles?
     * 
     * @parameter default-value=true
     */
    protected boolean showpercentile = true;

    /**
     * Will graphs of responsetimes and histogram show the average?
     * 
     * @parameter default-value=true
     */
    protected boolean showaverage = true;

    /**
     * Will garbage collections be shown?
     * 
     * @parameter default-value=true
     */
    protected boolean showgc = true;

    /**
     * @parameter
     */
    protected List plugins;

    /**
     * Set the history chart upper bound
     * 
     * @parameter default-value=0
     */
    /* Merged from Atlassion */
    protected double historychartupperbound = 0;

    /**
     * @see AbstractMavenReport#executeReport(Locale)
     * @param locale
     * @throws MavenReportException
     */
    public void executeReport(Locale locale) throws MavenReportException {
        String dataId = getDataId();
        File responsetimeSamples = Utils.getPerformanceSamplesSer(project.getBasedir(), dataId);
        if(!responsetimeSamples.exists()) {
            throw new MavenReportException("File " + responsetimeSamples.getAbsolutePath() + " not found");
        }
        try {
            // parse logs
            GroupedResponsetimeSamples jmeterSamples = (GroupedResponsetimeSamples)Utils
                    .readObject(responsetimeSamples);
            getLog().info("  tests: " + jmeterSamples.getSampleGroups().size());
            getLog().info("  jmeter samples: " + jmeterSamples.size());

            // charts
            getLog().info(" generating charts...");
            GCSamples gcSamples = getGcSamples(dataId);
            List defaultPlugins = ChartUtil.createDefaultPlugins(jmeterSamples, gcSamples);
            getLog().info("" + plugins);
            if(plugins != null) {
                defaultPlugins.addAll(plugins);
            }
            GraphGenerator graphGenerator = new GraphGenerator(defaultPlugins);
            ChartRenderer renderer = new ChartRendererImpl(getOutputDirectory());
            graphGenerator.generateGraphs(renderer, getBundle(locale), getConfig());

            // report
            ReportGenerator reportGenerator = new ReportGenerator(getBundle(locale), getConfig(), graphGenerator);
            getLog().info(" generating report...");
            reportGenerator.doGenerateReport(getSink(), jmeterSamples);

        } catch (IOException e) {
            throw new MavenReportException("ReportGenerator failed", e);
        }
    }

    private String getDataId() {
        return dataid;
    }

    private GCSamples getGcSamples(String dataId) throws IOException {
        if(!showgc) {
            return null;
        }
        return Utils.readGCSamples(project.getBasedir(), dataId);
    }

    /**
     * @see MavenReport#getName(Locale)
     * @param locale
     */
    public String getName(Locale locale) {
        return getOutputName();
    }

    /**
     * @see MavenReport#getDescription(Locale)
     * @param locale
     */
    public String getDescription(Locale locale) {
        return getBundle(locale).getString("chronos.description");
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    protected SiteRenderer getSiteRenderer() {
        return siteRenderer;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    protected MavenProject getProject() {
        return project;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName() {
        return getConfig().getId();
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getReportOutputDirectory()
     */
    protected String getOutputDirectory() {
        return outputDirectory;
    }

    ResourceBundle getBundle(Locale locale) {
        return Utils.getBundle(locale);
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
     */
    public boolean canGenerateReport() {
        // Only execute reports for java projects
        ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();
        return "java".equals(artifactHandler.getLanguage());
    }

    /**
     * @return Returns the report.
     */
    protected ReportConfig getConfig() {
        return new ReportConfig() {
            public String getId() {
                return reportid != null ? reportid : dataid;
            }

            public String getTitle() {
                return title;
            }

            public String getDescription() {
                return description;
            }

            public int getAverageduration() {
                return averageduration;
            }

            public int getResponsetimedivider() {
                return responsetimedivider;
            }

            public long getThreadcountduration() {
                return threadcountduration;
            }

            /* Merged from Atlassion */
            public double getHistoryChartUpperBound() {
                return historychartupperbound;
            }

            public boolean isShowaverage() {
                return showaverage;
            }

            public boolean isShowdetails() {
                return showdetails;
            }

            public boolean isShowgc() {
                if(!showgc) {
                    return false;
                }
                File gcSamplesSer = Utils.getGcSamplesSer(project.getBasedir(), getDataId());
                if(gcSamplesSer.exists()) {
                    return true;
                }
                getLog().warn("Could not find gc info from " + gcSamplesSer.getAbsolutePath());
                return false;
            }

            public boolean isShowhistogram() {
                return showhistogram;
            }

            public boolean isShowinfotable() {
                return showinfotable;
            }

            public boolean isShowpercentile() {
                return showpercentile;
            }

            public boolean isShowresponse() {
                return showresponse;
            }

            public boolean isShowsummary() {
                return showsummary;
            }

            public boolean isShowsummarycharts() {
                return showsummarycharts;
            }

            public boolean isShowthroughput() {
                return showthroughput;
            }

            public boolean isShowtimeinfo() {
                return showtimeinfo;
            }
        };
    }
}
