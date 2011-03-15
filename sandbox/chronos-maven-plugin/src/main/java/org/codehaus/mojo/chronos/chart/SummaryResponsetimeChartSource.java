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
package org.codehaus.mojo.chronos.chart;

import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.ReportConfig;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.jfree.chart.JFreeChart;

/**
 * This class is responsible for generating responsetime charts (aggregated for all tests).
 * 
 * @author ksr@lakeside.dk
 */
public final class SummaryResponsetimeChartSource extends ResponseChartGenerator implements ChartSource {
    private ResponsetimeSamples samples;

    public SummaryResponsetimeChartSource(ResponsetimeSamples samples) {
        this.samples = samples;
    }

    public JFreeChart getChart(ResourceBundle bundle, ReportConfig config) {
        return createResponseChart(bundle.getString("chronos.label.testcases"), samples, bundle, config);
    }

    public String getFileName(ResourceBundle bundle, ReportConfig config) {
        return "response-summary-" + config.getId();
    }

    public boolean isEnabled(ResourceBundle bundle, ReportConfig config) {
        return config.isShowsummary() && config.isShowresponse() && !config.isShowdetails();
    }

}
