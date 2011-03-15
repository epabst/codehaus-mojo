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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

/**
 * This class is responsible for generating histograms.
 * 
 * @author ksr@lakeside.dk
 */
public abstract class HistogramChartGenerator extends ChartUtil {

    private static final int BINS = 100;
    private static final float FOREGROUND_ALPHA = 0.85F;

    protected final JFreeChart createHistogramChart(ResponsetimeSamples samples, String label, ResourceBundle bundle,
            ReportConfig config) {
        HistogramDataset histogramdataset = new HistogramDataset();

        double[] sampleArray = samples.extractResponsetimes(config.getResponsetimedivider());
        histogramdataset.addSeries(label, sampleArray, BINS);
        HistogramDataset dataset = histogramdataset;
        JFreeChart chart = ChartFactory.createHistogram(bundle.getString("chronos.label.histogram"),
                bundle.getString("chronos.label.histogram.x"), bundle.getString("chronos.label.histogram.y"), dataset,
                PlotOrientation.VERTICAL, true, false, false);
        XYPlot xyplot = (XYPlot)chart.getPlot();
        xyplot.setForegroundAlpha(FOREGROUND_ALPHA);
        XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
        xybarrenderer.setDrawBarOutline(false);

        if(config.isShowpercentile()) {
            String label1 = bundle.getString("chronos.label.percentile95.arrow");
            double value = samples.getPercentile95(config.getResponsetimedivider());
            ChartUtil.addDomainMarker(xyplot, label1, value);
        }
        if(config.isShowaverage()) {
            String label2 = bundle.getString("chronos.label.average.arrow");
            double value = samples.getAverage(config.getResponsetimedivider());
            ChartUtil.addDomainMarker(xyplot, label2, value);
        }
        return chart;
    }
}
