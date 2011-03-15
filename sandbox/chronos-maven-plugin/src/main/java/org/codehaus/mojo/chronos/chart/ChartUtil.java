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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * Utilityclass to assist in generating charts.
 * 
 * @author ksr@lakeside.dk
 */
public class ChartUtil {

    private static final double GAP = 10D;
    private static final double MARGIN = 0.02D;

    public static List createDefaultPlugins(ResponsetimeSamples samples, GCSamples gcSamples) {
        List plugins = new ArrayList();
        plugins.add(new ChronosResponsetimePlugin(samples));
        plugins.add(new ChronosHistogramPlugin(samples));
        plugins.add(new ChronosThroughputPlugin(samples));
        plugins.add(new ChronosGCPlugin(gcSamples));
        return plugins;
    }

    public static void addDomainMarker(XYPlot xyplot, String label, double value) {
        xyplot.addDomainMarker(addValueMarker(label, value, true));
    }

    public static void addRangeMarker(XYPlot xyplot, String label, double value) {
        xyplot.addRangeMarker(addValueMarker(label, value, false));
    }

    /**
     * Generate a {@link ValueMarker}.
     */
    private static ValueMarker addValueMarker(String text, double x, boolean domain) {
        ValueMarker marker = new ValueMarker(x);
        marker.setPaint(Color.GRAY);
        marker.setLabel(text);
        if(domain) {
            marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        } else {
            marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        }
        return marker;
    }

    public static XYPlot setupXYPlot(JFreeChart chart, DateFormat dateFormat) {
        XYPlot plot = chart.getXYPlot();

        DateAxis axis = (DateAxis)plot.getDomainAxis();
        axis.setDateFormatOverride(dateFormat);

        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return plot;
    }

    /* Method merged from Atlassion */
    public static XYPlot setUpperBound(JFreeChart chart, double max) {
        XYPlot plot = chart.getXYPlot();

        ValueAxis axis = (ValueAxis)plot.getDomainAxis();
        axis.setUpperBound(max);
        return plot;
    }

    static CombinedDomainXYPlot createCombinedPlot(DateAxis timeAxis, XYPlot xyplot1, XYPlot xyplot2) {
        CombinedDomainXYPlot combineddomainxyplot = new CombinedDomainXYPlot(timeAxis);
        combineddomainxyplot.setGap(GAP);
        combineddomainxyplot.add(xyplot1, 2);
        combineddomainxyplot.add(xyplot2, 1);
        combineddomainxyplot.setOrientation(PlotOrientation.VERTICAL);
        return combineddomainxyplot;
    }

    public static XYPlot newPlot(TimeSeries timeSeries, String label, boolean forceIncludeZero) {
        XYDataset dataset = asDataset(timeSeries);
        return newPlot(dataset, label, forceIncludeZero);
    }

    static XYPlot newPlot(XYDataset dataset, String label, boolean forceIncludeZero) {
        StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
        NumberAxis numberaxis = new NumberAxis(label);
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRangeIncludesZero(forceIncludeZero);
        return new XYPlot(dataset, null, numberaxis, standardxyitemrenderer);
    }

    private static XYDataset asDataset(TimeSeries series) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    static DateAxis createTimeAxis(String label, SimpleDateFormat dateFormat) {
        DateAxis timeAxis = new DateAxis(label);
        timeAxis.setDateFormatOverride(dateFormat);
        timeAxis.setLowerMargin(MARGIN);
        timeAxis.setUpperMargin(MARGIN);
        return timeAxis;
    }

}
