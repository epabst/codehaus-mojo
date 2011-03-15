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
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;
import org.jfree.chart.JFreeChart;

/**
 * Source of histograms for a single test.
 * 
 * @author ksr@lakeside.dk
 */
public final class DetailsHistogramChartSource extends HistogramChartGenerator implements ChartSource {
    private ResponsetimeSampleGroup sampleGroup;

    public DetailsHistogramChartSource(ResponsetimeSampleGroup sampleGroup) {
        this.sampleGroup = sampleGroup;
    }

    public JFreeChart getChart(ResourceBundle bundle, ReportConfig config) {
        return createHistogramChart(sampleGroup, sampleGroup.getName(), bundle, config);
    }

    public String getFileName(ResourceBundle bundle, ReportConfig config) {
        return "histogram-" + sampleGroup.getIndex() + "-" + config.getId();
    }

    public boolean isEnabled(ResourceBundle bundle, ReportConfig config) {
        return config.isShowdetails() && config.isShowhistogram();
    }

}
