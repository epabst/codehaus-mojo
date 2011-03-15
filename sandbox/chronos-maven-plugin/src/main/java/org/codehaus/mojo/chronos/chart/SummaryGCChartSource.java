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

import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.ReportConfig;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Source for garbage collection charts.
 * 
 * @author ksr@lakeside.dk
 */
public final class SummaryGCChartSource implements ChartSource {
    private GCSamples samples;

    public SummaryGCChartSource(GCSamples samples) {
        this.samples = samples;
    }

    public boolean isEnabled(ResourceBundle bundle, ReportConfig config) {
        return config.isShowgc() && config.isShowsummary();
    }

    public String getFileName(ResourceBundle bundle, ReportConfig config) {
        return "gc-" + config.getId();
    }

    public JFreeChart getChart(ResourceBundle bundle, ReportConfig config) {
        String beforeLabel = bundle.getString("chronos.label.gc.before");
        String afterLabel = bundle.getString("chronos.label.gc.after");
        TimeSeriesCollection dataset1 = new TimeSeriesCollection();
        TimeSeries heapBeforeSeries = new TimeSeries(beforeLabel, Millisecond.class);
        samples.extractHeapBefore(heapBeforeSeries);
        TimeSeries heapAfterSeries = new TimeSeries(afterLabel, Millisecond.class);
        samples.extractHeapAfter(heapAfterSeries);

        dataset1.addSeries(heapBeforeSeries);
        dataset1.addSeries(heapAfterSeries);
        TimeSeriesCollection dataset = dataset1;

        String title = bundle.getString("chronos.label.gc");
        String timeLabel = bundle.getString("chronos.label.gc.time");
        String valueLabel = bundle.getString("chronos.label.gc.mem");
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeLabel, valueLabel, dataset, true, true, false);
        ChartUtil.setupXYPlot(chart, new SimpleDateFormat("HH:mm:ss"));
        return chart;
    }

}
