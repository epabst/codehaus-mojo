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
package org.codehaus.mojo.chronos.history;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.chart.ChartRenderer;
import org.codehaus.mojo.chronos.chart.ChartRendererImpl;
import org.codehaus.mojo.chronos.chart.HistoryChartGenerator;
import org.codehaus.mojo.chronos.report.HistoryReportGenerator;

/**
 * Creates a historic report of performance test results.
 * 
 * @author ksr@lakeside.dk
 * @goal historyreport
 * @execute phase=post-integration-test
 */
public class HistoryReportMojo

extends AbstractMavenReport {
    /**
     * Location where generated html will be created.
     * 
     * @parameter expression="${project.build.directory}/site "
     * @required
     * @readonly
     */
    private String outputDirectory;

    /**
     * Doxia Site Renderer.
     * 
     * @component role="org.codehaus.doxia.site.renderer.SiteRenderer"
     * @required
     * @readonly
     */
    private SiteRenderer siteRenderer;

    /**
     * Current Maven Project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The id of the data, to create a report from.Used to separate between several performancetest insode the same
     * maven project
     * 
     * @parameter default-value = "performancetest"
     */
    private String dataid;

    /**
     * The directory where historic data are stored.
     * 
     * @parameter expression="${basedir}/target/chronos/history"
     */
    private File historydir;

    /**
     * The title of the generated report
     * 
     * @parameter default-value = ""
     */
    private String title;

    /**
     * The description of the generated report.
     * 
     * @parameter default-value = ""
     */
    private String description;

    /**
     * Should the report contain garbage collections? Note that garbage collections are only relevant if they are from
     * the code being tested (if you use JMeter to test wbsites, the jmeter gc logs are totally irrelevant)!
     * 
     * @parameter default-value=true
     */
    private boolean showgc;

    /**
     * This sets the default maximum value on the history report charts. This can be set to prevent "spikes" in the
     * charts which can throw the scale off.
     * 
     * @parameter default-value=0
     */
    /* Merged from Atlassion */
    private double historychartupperbound;

    protected void executeReport(Locale locale) throws MavenReportException {
        try {
            HistoricSamples samples = new HistoricSamples();
            File dataDirectory = new File(historydir, dataid);
            samples.load(dataDirectory);
            ResourceBundle bundle = Utils.getBundle(locale);

            // charts
            getLog().info(" generating charts...");
            ChartRenderer renderer = new ChartRendererImpl(getOutputDirectory());
            HistoryChartGenerator charts = new HistoryChartGenerator(renderer, bundle);
            /* Merged from Atlassion */
            // charts.createResponseSummaryChart(samples, dataid);
            charts.createResponseSummaryChart(samples, dataid, historychartupperbound);
            charts.createThroughputChart(samples, dataid);
            if(showgc) {
                charts.createGcChart(samples, dataid);
            }
            charts.createResponseDetailsChart(samples, dataid);

            HistoryReportGenerator reportgenerator = new HistoryReportGenerator(dataid, bundle, title, description);
            reportgenerator.doGenerateReport(getSink(), samples, showgc);
        } catch (IOException e) {
            throw new MavenReportException("ReportGenerator failed", e);
        }
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public void setHistorydir(File historydir) {
        this.historydir = historydir;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShowgc(boolean showgc) {
        this.showgc = showgc;
    }

    /* Merged from Atlassion */
    public void setGraphMaxUpperBound(double historychartupperbound) {
        this.historychartupperbound = historychartupperbound;
    }

    protected String getOutputDirectory() {
        return outputDirectory;
    }

    protected MavenProject getProject() {
        return project;
    }

    protected SiteRenderer getSiteRenderer() {
        return siteRenderer;
    }

    public String getDescription(Locale locale) {
        return description;
    }

    public String getName(Locale locale) {
        return getOutputName();
    }

    public String getOutputName() {
        return "history-" + dataid;
    }

    /**
     * We skip this report if no historical samples can be found...
     */
    public boolean canGenerateReport() {
        if(!historydir.exists()) {
            missingDir(historydir);
            return false;
        }
        File dataDirectory = new File(historydir, dataid);
        if(!dataDirectory.exists()) {
            missingDir(dataDirectory);
            return false;
        }
        return true;
    }

    private void missingDir(File dir) {
        getLog().info("Directory with historic results " + dir + " not found, skipping historic report.");
    }

}
