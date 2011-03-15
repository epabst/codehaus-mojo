package org.codehaus.mojo.chronos.it;

import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.ReportConfig;
import org.codehaus.mojo.chronos.chart.ChartSource;
import org.codehaus.mojo.chronos.chart.ChronosReportPlugin;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

/**
 * Hello world!
 *
 */
public class DummyPlugin implements ChronosReportPlugin 
{
    public int values = 1;
    private int samplesprvalue = 1;
    
    
    public ChartSource getSummaryChartSource()
    {
        return new ChartSource() {
            public boolean isEnabled( ResourceBundle bundle, ReportConfig config )
            {
                return true;
            }
            
            public String getFileName( ResourceBundle bundle, ReportConfig config )
            {
                return "dummychart";
            }
            
            public JFreeChart getChart( ResourceBundle bundle, ReportConfig config )
            {
                double sampleArray[] = new double[samplesprvalue*values];
                for (int currentValue = 0; currentValue < values; currentValue++) {
                    for (int sampleIndex = 0; sampleIndex < samplesprvalue; sampleIndex++) {
                        int index = samplesprvalue*currentValue + sampleIndex;
                        System.out.println("value=" + currentValue + " index=" + index);
                        sampleArray[index] = currentValue;
                    }
                }
                HistogramDataset dataset = new HistogramDataset();
                dataset.addSeries( "seriestitle", sampleArray, 100 );
                JFreeChart chart =
                    ChartFactory.createHistogram( "title", "x", "y", dataset,
                                                  PlotOrientation.VERTICAL, true, false, false );
                XYPlot xyplot = (XYPlot) chart.getPlot();
                xyplot.setForegroundAlpha( 0.85F );
                XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
                xybarrenderer.setDrawBarOutline( false );
                return chart;
            }
        };
    }

    public Map getDetailChartSources()
    {
        return Collections.EMPTY_MAP;
    }
    
    public String toString() {
        return getClass().getName() + ":samplesprvalue=" + samplesprvalue + " values=" + values;
    }

}
