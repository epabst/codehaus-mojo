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
package org.codehaus.mojo.chronos.report;

import java.util.ResourceBundle;

import org.codehaus.doxia.sink.Sink;
import org.codehaus.mojo.chronos.history.HistoricSamples;

/**
 * Helper class doing th eheavy listing when generating historic reports.
 * 
 * @author ksr@lakeside.dk
 */
public final class HistoryReportGenerator {
    private static final String IMG_EXT = ".png";

    private String dataId;

    private ResourceBundle bundle;

    private ReportSink reportSink;

    private String title;

    private String description;

    private Sink sink;

    public HistoryReportGenerator(String dataId, ResourceBundle bundle, String title, String description) {
        this.dataId = dataId;
        this.bundle = bundle;
        this.title = title;
        this.description = description;
    }

    /**
     * Generate a report (as an html page).
     * 
     * @param aSink
     *            The {@link Sink} to output the report content to
     * @param samples
     *            The {@link org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples} to create a report from
     */
    public void doGenerateReport(Sink aSink, HistoricSamples samples, boolean showgc) {
        this.reportSink = new ReportSink(bundle, aSink);
        this.sink = aSink;

        aSink.head();
        aSink.text(bundle.getString("chronos.description"));
        aSink.head_();
        aSink.body();

        String anchor = "Report" + dataId;
        reportSink.constructHeaderSection(title, description, anchor);
        String text = bundle.getString("chronos.label.summary");
        String anchor1 = "Summary" + dataId;
        reportSink.title2(text, anchor1);
        constructReportHotLinks();
        reportSink.graphics("history-response-summary-" + dataId + IMG_EXT);
        reportSink.graphics("history-throughput-" + dataId + IMG_EXT);
        if(showgc) {
            reportSink.graphics("history-gc-" + dataId + IMG_EXT);
        }
        reportSink.title2(bundle.getString("chronos.label.testcases"), "Test_Cases" + dataId);
        constructReportHotLinks();
        String[] groupNames = samples.getGroupNames();
        for (int i = 0; i < groupNames.length; i++) {
            reportSink.title3(groupNames[i], i + dataId);
            reportSink.graphics("history-response-" + i + "-" + dataId + IMG_EXT);
        }
        reportSink.sinkLineBreak();
        aSink.body_();
        aSink.flush();
        aSink.close();
    }

    private void constructReportHotLinks() {
        sink.section3();
        reportSink.sinkLink(bundle.getString("chronos.label.summary"), "Summary" + dataId);
        reportSink.sinkLink(bundle.getString("chronos.label.testcases"), "Test_Cases" + dataId);
        sink.section3_();
    }

}
