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
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * This class is responsible for generating responsetime charts.
 * 
 * @author ksr@lakeside.dk
 */
public abstract class ResponseChartGenerator {
    protected final JFreeChart createResponseChart(String datasetName, ResponsetimeSamples samples,
            ResourceBundle bundle, ReportConfig config) {
        XYDataset dataset = createResponseDataset(datasetName, samples, bundle, config);

        String title = bundle.getString("chronos.label.responsetimes");
        String timeAxisLabel = bundle.getString("chronos.label.responsetimes.time");
        String valueAxisLabel = bundle.getString("chronos.label.responsetimes.responsetime");
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset, true,
                true, false);
        ChartUtil.setupXYPlot(chart, new SimpleDateFormat("HH:mm:ss"));
        // change rendering order - so average is in front
        chart.getXYPlot().setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);

        if(config.isShowpercentile()) {
            String text = bundle.getString("chronos.label.percentile95.arrow");
            ChartUtil.addRangeMarker(chart.getXYPlot(), text, samples.getPercentile95(config.getResponsetimedivider()));
        }
        if(config.isShowaverage()) {
            String text = bundle.getString("chronos.label.average.arrow");
            ChartUtil.addRangeMarker(chart.getXYPlot(), text, samples.getAverage(config.getResponsetimedivider()));
        }
        return chart;
    }

    private TimeSeriesCollection createResponseDataset(String name, ResponsetimeSamples samples, ResourceBundle bundle,
            ReportConfig config) {
        TimeSeries series = new TimeSeries(name, Millisecond.class);

        samples.appendResponsetimes(series, config.getResponsetimedivider());
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        String averageLabel = bundle.getString("chronos.label.average");
        TimeSeries averageseries = MovingAverage.createMovingAverage(series, averageLabel, config.getAverageduration(),
                0);
        dataset.addSeries(averageseries);
        return dataset;
    }
}
