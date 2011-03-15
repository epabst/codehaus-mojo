package org.codehaus.mojo.dashboard.report.plugin.chart.time;

/*
 * Copyright 2008 David Vicente
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
import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean;
import org.codehaus.mojo.dashboard.report.plugin.chart.AbstractChartDecorator;
import org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.codehaus.mojo.dashboard.report.plugin.utils.TimePeriod;
import org.jfree.chart.ChartColor;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * @author A315941
 *
 */
public class SurefirePercentAxisDecorator extends AbstractChartDecorator
{

    private static final double BLOCK_CONTAINER_WIDTH = 2000D;

    private static final int ITEM_LABEL_FONT_SIZE = 10;

    private static final double AXIS_UPPER_MARGIN = 0.099D;

    /**
     *
     */
    private static final int PCENT = 100;

    /**
     * Default constructor
     *
     * @param chartToDecorate
     *            the chart to decorate with new Axis
     * @param results
     *            data used with new Axis
     */
    public SurefirePercentAxisDecorator( IChartRenderer chartToDecorate, List results )
    {
        super( chartToDecorate, results );
    }

    /**
     *
     */
    public void createChart()
    {

        XYPlot xyplot = (XYPlot) report.getPlot();
        if ( this.decoratedChart instanceof TimeChartRenderer && this.results != null && !this.results.isEmpty() )
        {

            Iterator iter = this.results.iterator();
            TimeSeriesCollection defaultdataset = new TimeSeriesCollection();
            TimeSeries s1 = new TimeSeries( "% success", Day.class );

            while ( iter.hasNext() )
            {
                SurefireReportBean surefire = (SurefireReportBean) iter.next();
                Date date = surefire.getDateGeneration();
                s1.addOrUpdate( new Day( TimePeriod.DAY.normalize( date ) ), surefire.getSucessRate() / PCENT );

            }

            defaultdataset.addSeries( s1 );

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setBaseShapesVisible( true );
            renderer.setBaseShapesFilled( true );
            renderer.setSeriesPaint( 0, ChartColor.DARK_BLUE );
            renderer.setBaseShapesVisible( true );
            renderer.setDrawOutlines( true );
            StandardXYItemLabelGenerator labelgenerator =
                new StandardXYItemLabelGenerator( StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                                                  TimePeriod.DAY.getDateFormat(),
                                                  NumberFormat.getPercentInstance( Locale.getDefault() ) );
            renderer.setBaseItemLabelGenerator( labelgenerator );
            renderer.setBaseItemLabelFont( new Font( "SansSerif", Font.BOLD, ITEM_LABEL_FONT_SIZE ) );
            renderer.setBaseItemLabelsVisible( true );
            renderer.setBasePositiveItemLabelPosition( new ItemLabelPosition( ItemLabelAnchor.OUTSIDE10,
                                                                          TextAnchor.BASELINE_RIGHT ) );

            renderer.setBaseStroke( new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

            LegendTitle legendtitle = new LegendTitle( xyplot.getRenderer( 0 ) );
            legendtitle.setMargin( new RectangleInsets( 2D, 2D, 2D, 2D ) );
            legendtitle.setFrame( new BlockBorder() );
            legendtitle.setBackgroundPaint( ChartColor.WHITE );

            LegendTitle legendtitle1 = new LegendTitle( renderer );
            legendtitle1.setMargin( new RectangleInsets( 2D, 2D, 2D, 2D ) );
            legendtitle1.setFrame( new BlockBorder() );
            legendtitle1.setBackgroundPaint( ChartColor.WHITE );

            BlockContainer blockcontainer = new BlockContainer( new BorderArrangement() );
            blockcontainer.add( legendtitle, RectangleEdge.LEFT );
            blockcontainer.add( legendtitle1, RectangleEdge.RIGHT );
            blockcontainer.add( new EmptyBlock( BLOCK_CONTAINER_WIDTH, 0.0D ) );

            CompositeTitle compositetitle = new CompositeTitle( blockcontainer );
            compositetitle.setPosition( RectangleEdge.BOTTOM );

            report.clearSubtitles();
            report.addSubtitle( compositetitle );

            xyplot.setDataset( 1, defaultdataset );

            NumberAxis valueaxis = new NumberAxis( "% success" );
            valueaxis.setLowerMargin( 0.0D );
            valueaxis.setUpperMargin( AXIS_UPPER_MARGIN );
            valueaxis.setRangeWithMargins( 0.0D, 1.0D );
            valueaxis.setNumberFormatOverride( NumberFormat.getPercentInstance() );
            xyplot.setRangeAxis( 1, valueaxis );
            xyplot.mapDatasetToRangeAxis( 1, 1 );
            xyplot.setRenderer( 1, renderer );
        }

    }

    /**
     * get specific background color
     *
     * @return used color
     */
    public Color getBackgroundColor()
    {
        return (Color) ChartUtils.BLUE_STEEL2_LIGHT;
    }

}
