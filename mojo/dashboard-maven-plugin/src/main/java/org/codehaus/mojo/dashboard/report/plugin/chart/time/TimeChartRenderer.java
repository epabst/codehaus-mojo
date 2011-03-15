package org.codehaus.mojo.dashboard.report.plugin.chart.time;

/*
 * Copyright 2007 David Vicente
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import org.codehaus.mojo.dashboard.report.plugin.chart.AbstractChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.chart.IChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.codehaus.mojo.dashboard.report.plugin.utils.DateUtils;
import org.codehaus.mojo.dashboard.report.plugin.utils.TimePeriod;

public class TimeChartRenderer extends AbstractChartRenderer
{

    public TimeChartRenderer( IChartStrategy strategy )
    {
        super( strategy );
    }

    public TimeChartRenderer( IChartStrategy strategy, int width, int height )
    {
        super( strategy, width, height );
    }

    public void createChart()
    {
        XYDataset dataset = (XYDataset) this.datasetStrategy.getDataset();
        report = ChartFactory.createTimeSeriesChart( this.datasetStrategy.getTitle(), // title
                                                     this.datasetStrategy.getXAxisLabel(), // x-axis label
                                                     this.datasetStrategy.getYAxisLabel(), // y-axis label
                                                     dataset, // data
                                                     true, // create legend?
                                                     true, // generate tooltips?
                                                     false // generate URLs?
        );

        // report.setBackgroundPaint( Color.lightGray );
        XYPlot plot = report.getXYPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.lightGray );
        plot.setRangeGridlinePaint( Color.lightGray );
        plot.setAxisOffset( new RectangleInsets( 5D, 5D, 5D, 5D ) );
        XYItemRenderer xyitemrenderer = plot.getRenderer();
        if ( xyitemrenderer instanceof XYLineAndShapeRenderer )
        {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyitemrenderer;
            renderer.setBaseShapesVisible( true );
            renderer.setBaseShapesFilled( true );

            renderer.setBaseShapesVisible( true );
            renderer.setDrawOutlines( true );
            renderer.setBaseItemLabelGenerator( ( (AbstractTimeChartStrategy) this.datasetStrategy ).getLabelGenerator() );
            renderer.setBaseItemLabelFont( new Font( "SansSerif", Font.BOLD, 10 ) );
            renderer.setBaseItemLabelsVisible( true );
            renderer.setBasePositiveItemLabelPosition( new ItemLabelPosition( ItemLabelAnchor.OUTSIDE10,
                                                                          TextAnchor.BASELINE_RIGHT ) );
        }

        Paint[] paints = this.datasetStrategy.getPaintColor();
        for ( int i = 0; i < dataset.getSeriesCount() && i < paints.length; i++ )
        {
            xyitemrenderer.setSeriesPaint( i, paints[i] );
            xyitemrenderer.setSeriesStroke( i, new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        }
        plot.setRangeAxis( ( (AbstractTimeChartStrategy) this.datasetStrategy ).getRangeAxis() );
        DashDateAxis axisDate = new DashDateAxis();
        axisDate.setDateFormatOverride( ( (AbstractTimeChartStrategy) this.datasetStrategy ).getTimePeriod().getDateFormat() );
        axisDate.setLabel( this.datasetStrategy.getXAxisLabel() );
        axisDate.setTickUnit( getTickUnit( ( (AbstractTimeChartStrategy) this.datasetStrategy ).getTimePeriod() ) );
        axisDate.setUpperMargin( 0.0D );
        axisDate.setDateTickLabelAngle( -0.6 );

        if ( ( (AbstractTimeChartStrategy) this.datasetStrategy ).getStartDate() != null
                        && ( (AbstractTimeChartStrategy) this.datasetStrategy ).getEndDate() != null )
        {
            axisDate.setRangeWithMargins( new DateRange(
                                                         ( (AbstractTimeChartStrategy) this.datasetStrategy ).getStartDate(),
                                                         ( (AbstractTimeChartStrategy) this.datasetStrategy ).getEndDate() ) );

        }
        plot.setDomainAxis( axisDate );

        Date[] dates =
            DateUtils.getAllDates( ( (AbstractTimeChartStrategy) this.datasetStrategy ).getStartDate(),
                                   ( (AbstractTimeChartStrategy) this.datasetStrategy ).getEndDate(),
                                   ( (AbstractTimeChartStrategy) this.datasetStrategy ).getTimePeriod() );
        int width = ( dates.length * ChartUtils.STANDARD_TIME_ENTRY_WIDTH ) + ChartUtils.STANDARD_TIME_ADDITIONAL_WIDTH;
        if ( width > ChartUtils.MINIMUM_WIDTH )
        {
            this.setWidth( width );
        }
        else
        {
            this.setWidth( ChartUtils.MINIMUM_WIDTH );
        }
    }

    private DateTickUnit getTickUnit( TimePeriod timePeriod )
    {
        DateTickUnit tickUnit = null;
        if ( timePeriod.equals( TimePeriod.MINUTE ) )
        {
            tickUnit = new DateTickUnit( DateTickUnit.MINUTE, 10 );
        }
        else if ( timePeriod.equals( TimePeriod.HOUR ) )
        {
            tickUnit = new DateTickUnit( DateTickUnit.HOUR, 1 );
        }
        else if ( timePeriod.equals( TimePeriod.DAY ) )
        {
            tickUnit = new DateTickUnit( DateTickUnit.DAY, 1 );
        }
        else if ( timePeriod.equals( TimePeriod.WEEK ) )
        {
            tickUnit = new DateTickUnit( DateTickUnit.DAY, 7 );
        }
        else if ( timePeriod.equals( TimePeriod.MONTH ) )
        {
            tickUnit = new DateTickUnit( DateTickUnit.MONTH, 1 );
        }
        else
        {
            tickUnit = new DateTickUnit( DateTickUnit.HOUR, 1 );
        }
        return tickUnit;
    }

}
