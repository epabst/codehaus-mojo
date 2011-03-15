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

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.ReportConfig;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * This class is responsible for generating throughput charts.
 * 
 * @author ksr@lakeside.dk
 */
public final class SummaryThroughputChartSource implements ChartSource {

    private ResponsetimeSamples samples;

    public SummaryThroughputChartSource(ResponsetimeSamples samples) {
        this.samples = samples;
    }

    public boolean isEnabled(ResourceBundle bundle, ReportConfig config) {
        return config.isShowthroughput() && config.isShowsummary();
    }

    public String getFileName(ResourceBundle bundle, ReportConfig config) {
        return "throughput-" + config.getId();
    }

    public JFreeChart getChart(ResourceBundle bundle, ReportConfig config) {
        XYPlot throughputPlot = createThroughputPlot(bundle, config);
        XYPlot threadCountPlot = createThreadCountPlot(bundle, config);

        String label = bundle.getString("chronos.label.throughput.time");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        DateAxis timeAxis = ChartUtil.createTimeAxis(label, dateFormat);
        CombinedDomainXYPlot combineddomainxyplot = ChartUtil.createCombinedPlot(timeAxis, throughputPlot,
                threadCountPlot);
        return new JFreeChart(bundle.getString("chronos.label.throughput"), combineddomainxyplot);
    }

    private XYPlot createThroughputPlot(ResourceBundle bundle, ReportConfig config) {
        TimeSeriesCollection dataset1 = createThroughputDataset(bundle, config);
        XYPlot throughputPlot = ChartUtil
                .newPlot(dataset1, bundle.getString("chronos.label.throughput.requests"), true);
        throughputPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        throughputPlot.getRenderer().setSeriesPaint(0, Color.GREEN);
        throughputPlot.getRenderer().setSeriesPaint(1, Color.BLUE);
        throughputPlot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);

        double maxAvgThroughput = samples.getMaxAverageThroughput(config.getAverageduration(),
                config.getResponsetimedivider());
        String maxThroughputLabel = bundle.getString("chronos.label.maxaveragethroughput");
        ChartUtil.addRangeMarker(throughputPlot, maxThroughputLabel, maxAvgThroughput);
        return throughputPlot;
    }

    private TimeSeriesCollection createThroughputDataset(ResourceBundle bundle, ReportConfig config) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries series = samples.createMovingThroughput(bundle.getString("chronos.label.throughput"),
                config.getResponsetimedivider());
        dataset.addSeries(series);
        int avgDuration = config.getAverageduration();
        String label = bundle.getString("chronos.label.average");
        TimeSeries averageseries = MovingAverage.createMovingAverage(series, label, avgDuration, 0);
        dataset.addSeries(averageseries);
        return dataset;
    }

    private XYPlot createThreadCountPlot(ResourceBundle bundle, ReportConfig config) {
        TimeSeriesCollection dataset2 = createThreadCountdataset(bundle, config);
        String label = bundle.getString("chronos.label.threadcount.y");
        XYPlot threadCountPlot = ChartUtil.newPlot(dataset2, label, false);
        threadCountPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
        threadCountPlot.getRenderer().setSeriesPaint(0, Color.GRAY);
        return threadCountPlot;
    }

    private TimeSeriesCollection createThreadCountdataset(ResourceBundle bundle, ReportConfig config) {
        String label = bundle.getString("chronos.label.threadcount");
        TimeSeries series = new TimeSeries(label, Millisecond.class);
        samples.appendThreadCounts(series, config.getThreadcountduration());
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

}
