package org.codehaus.mojo.dashboard.report.plugin.chart;

/*
 * Copyright 2006 David Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Color;
import java.awt.Paint;
import java.text.NumberFormat;
import java.util.Locale;

import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class BarChartRenderer extends AbstractChartRenderer
{

    private static final double NUMBER_AXIS_RANGE = 1.0D;

    public BarChartRenderer( IChartStrategy strategy )
    {
        super( strategy );
    }

    public BarChartRenderer( IChartStrategy strategy, int width, int height )
    {
        super( strategy, width, height );
    }

    public void createChart()
    {
        CategoryDataset categorydataset = (CategoryDataset) this.datasetStrategy.getDataset();
        report =
            ChartFactory.createBarChart( this.datasetStrategy.getTitle(), this.datasetStrategy.getYAxisLabel(),
                                         this.datasetStrategy.getXAxisLabel(), categorydataset,
                                         PlotOrientation.HORIZONTAL, true, true, false );
        // report.setBackgroundPaint( Color.lightGray );
        report.setPadding( new RectangleInsets( 5.0d, 5.0d, 5.0d, 5.0d ) );
        CategoryPlot categoryplot = (CategoryPlot) report.getPlot();
        categoryplot.setBackgroundPaint( Color.white );
        categoryplot.setRangeGridlinePaint( Color.lightGray );
        categoryplot.setDomainGridlinePaint( Color.lightGray );
        categoryplot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        if ( datasetStrategy instanceof CoberturaBarChartStrategy || datasetStrategy instanceof CloverBarChartStrategy
                        || datasetStrategy instanceof MultiCloverBarChartStrategy )
        {
            numberaxis.setRange( 0.0D, BarChartRenderer.NUMBER_AXIS_RANGE );
            numberaxis.setNumberFormatOverride( NumberFormat.getPercentInstance() );
        }
        else
        {
            numberaxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        }
        numberaxis.setLowerMargin( 0.0D );
        CategoryAxis axis = categoryplot.getDomainAxis();
        axis.setLowerMargin( 0.02 ); // two percent
        axis.setCategoryMargin( 0.10 ); // ten percent
        axis.setUpperMargin( 0.02 ); // two percent
        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setItemMargin( 0.10 );
        barrenderer.setDrawBarOutline( false );
        barrenderer.setBaseItemLabelsVisible( true );
        if ( datasetStrategy instanceof CoberturaBarChartStrategy || datasetStrategy instanceof CloverBarChartStrategy
                        || datasetStrategy instanceof MultiCloverBarChartStrategy )
        {
            barrenderer.setBaseItemLabelGenerator( new StandardCategoryItemLabelGenerator(
                                                                                           "{2}",
                                                                                           NumberFormat.getPercentInstance( Locale.getDefault() ) ) );
        }
        else
        {
            barrenderer.setBaseItemLabelGenerator( new StandardCategoryItemLabelGenerator() );
        }

        int height =
            ( categorydataset.getColumnCount() * ChartUtils.STANDARD_BARCHART_ENTRY_HEIGHT * categorydataset.getRowCount() );
        if ( height > ChartUtils.MINIMUM_HEIGHT )
        {
            super.setHeight( height );
        }
        else
        {
            super.setHeight( ChartUtils.MINIMUM_HEIGHT );
        }

        Paint[] paints = this.datasetStrategy.getPaintColor();

        for ( int i = 0; i < categorydataset.getRowCount() && i < paints.length; i++ )
        {
            barrenderer.setSeriesPaint( i, paints[i] );
        }

    }

}
