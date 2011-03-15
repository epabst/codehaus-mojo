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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.history.HistoricSamples;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * Responsible for generating charts showing historic results.
 * 
 * @author ksr@lakeside.dk
 */
public class HistoryChartGenerator {
    private ChartRenderer renderer;

    private ResourceBundle bundle;

    public HistoryChartGenerator(ChartRenderer renderer, ResourceBundle bundle) {
        this.renderer = renderer;
        this.bundle = bundle;
    }

    /**
     * create a summary chart of all samples together.
     * 
     * @param samples
     *            the {@link HistoricSamples} to chart
     * @param dataId
     *            an id of the current report
     * @throws IOException
     *             If the chart cannot be written to the filesystem
     */
    /* Merged from Atlassion */
    // public final void createResponseSummaryChart(HistoricSamples samples, String dataId) throws IOException {
    public final void createResponseSummaryChart(HistoricSamples samples, String dataId, double max) throws IOException {
        String label1 = bundle.getString("chronos.label.average.arrow");
        TimeSeries averageSeries = samples.getAverageTime(label1);
        String label2 = bundle.getString("chronos.label.percentile95.arrow");
        TimeSeries percentileseries = samples.getpercentile95(label2);
        XYDataset dataset = getResponseDataset(averageSeries, percentileseries);
        /* Merged from Atlassion */
        // renderResponseChart(dataset, "history-response-summary-" + dataId);
        renderResponseChart(dataset, "history-response-summary-" + dataId, max);
    }

    public final void createThroughputChart(HistoricSamples samples, String dataId) throws IOException {
        XYPlot xyplot = newPlot(samples.getThroughput(dataId), "chronos.label.throughput.requests", true);
        xyplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        xyplot.getRenderer().setSeriesPaint(0, Color.GREEN);

        String timeLabel = bundle.getString("chronos.label.throughput.historytime");
        DateAxis timeAxis = ChartUtil.createTimeAxis(timeLabel, new SimpleDateFormat());
        xyplot.setDomainAxis(timeAxis);
        JFreeChart chart = new JFreeChart(bundle.getString("chronos.label.throughput"), xyplot);
        renderer.renderChart("history-throughput-" + dataId, chart);
    }

    public final void createGcChart(HistoricSamples samples, String dataId) throws IOException {
        XYPlot xyplot1 = newPlot(samples.getGcRatio(dataId), "chronos.label.gc.ratio", true);
        xyplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        xyplot1.getRenderer().setSeriesPaint(0, Color.GREEN);
        xyplot1.getRangeAxis().setStandardTickUnits(NumberAxis.createStandardTickUnits());

        XYPlot xyplot2 = newPlot(samples.getKbCollectedPrSecond(dataId), "chronos.label.gc.kbpersec", true);
        xyplot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
        xyplot2.getRenderer().setSeriesPaint(0, Color.GRAY);
        xyplot2.getRangeAxis().setStandardTickUnits(NumberAxis.createStandardTickUnits());

        String timeLabel = bundle.getString("chronos.label.gc.historytime");
        DateAxis timeAxis = ChartUtil.createTimeAxis(timeLabel, new SimpleDateFormat());
        XYPlot combinedPlot = ChartUtil.createCombinedPlot(timeAxis, xyplot1, xyplot2);
        // xyplot1.setDomainAxis( timeAxis );
        // XYPlot combinedPlot = xyplot1;
        JFreeChart chart = new JFreeChart(bundle.getString("chronos.label.gc"), combinedPlot);
        renderer.renderChart("history-gc-" + dataId, chart);
    }

    private XYPlot newPlot(TimeSeries timeSeries, String label, boolean forceIncludeZero) {
        return ChartUtil.newPlot(timeSeries, bundle.getString(label), forceIncludeZero);
    }

    /**
     * create the response charts for the individual samples.
     * 
     * @param samples
     *            the {@link HistoricSamples} to chart
     * @param dataId
     *            an id of the current report
     * @throws IOException
     *             if the chart cannot be written to the filesystem
     */
    public final void createResponseDetailsChart(HistoricSamples samples, String dataId) throws IOException {
        // Merged from Atlassion
        double maxgraphupperbound = 0;
        String[] groupNames = samples.getGroupNames();
        for (int i = 0; i < groupNames.length; i++) {
            String label1 = bundle.getString("chronos.label.average.arrow");
            TimeSeries averageSeries = samples.getAverageTime(label1, groupNames[i]);
            String label2 = bundle.getString("chronos.label.percentile95.arrow");
            TimeSeries percentileseries = samples.getPercentile95(label2, groupNames[i]);
            XYDataset dataset = getResponseDataset(averageSeries, percentileseries);
            // Merged from Atlassion
            // renderResponseChart(dataset, "history-response-" + i + "-" + dataId);
            renderResponseChart(dataset, "history-response-" + i + "-" + dataId, maxgraphupperbound);
        }
    }

    /* Merged from Atlassion */
    // private void renderResponseChart(XYDataset dataset, String name) throws IOException {
    private void renderResponseChart(XYDataset dataset, String name, double max) throws IOException {
        String title = bundle.getString("chronos.label.responsetimes");
        String xLabel = bundle.getString("chronos.label.responsetimes.historytime");
        String yLabel = bundle.getString("chronos.label.responsetimes.responsetime");
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, dataset, true, true, false);
        /* Merged from Atlassion */
        if(max > 0) {
            ChartUtil.setUpperBound(chart, max);
        }
        ChartUtil.setupXYPlot(chart, new SimpleDateFormat());
        renderer.renderChart(name, chart);
    }

    private XYDataset getResponseDataset(TimeSeries averageSeries, TimeSeries percentileseries) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(averageSeries);
        dataset.addSeries(percentileseries);
        return dataset;
    }
}
