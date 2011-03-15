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
import java.awt.geom.Line2D;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartColor;
import org.jfree.chart.LegendItem;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.CenterArrangement;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.block.FlowArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendGraphic;
import org.jfree.chart.title.LegendItemBlockContainer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import org.codehaus.mojo.dashboard.report.plugin.chart.AbstractChartDecorator;
import org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class MarkerTimeChartDecorator extends AbstractChartDecorator
{

    public MarkerTimeChartDecorator( IChartRenderer chartToDecorate, List markersToPlot )
    {
        super( chartToDecorate, markersToPlot );
    }

    public void createChart()
    {

        XYPlot xyplot = (XYPlot) report.getPlot();
        // if (this.decoratedChart instanceof TimeChartRenderer ) {

        if ( this.results != null && !this.results.isEmpty() )
        {
            Iterator iter1 = this.results.iterator();
            ValueMarker valuemarker = null;
            BlockContainer blockcontainerLabel = new BlockContainer( new ColumnArrangement() );
            // blockcontainerLabel.setFrame( new LineBorder() );
            int i = 0;
            while ( iter1.hasNext() )
            {
                Object[] item = (Object[]) iter1.next();
                Date date = (Date) item[1];
                Calendar cal = Calendar.getInstance();
                cal.setTime( date );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                valuemarker =
                    new ValueMarker( cal.getTimeInMillis(), ChartColor.createDefaultPaintArray()[i],
                                     new BasicStroke( 2.0F ) );

                xyplot.addDomainMarker( valuemarker );
                LegendItem legendLabel =
                    new LegendItem( (String) item[0], null, null, null, new Line2D.Double( -7.0, 0.0, 7.0, 0.0 ),
                                    valuemarker.getPaint(), valuemarker.getStroke(), valuemarker.getPaint() );

                blockcontainerLabel.add( createLegendItemBlock( legendLabel, i ) );
                i++;
            }
            createLegendBlock( blockcontainerLabel );
        }
        // }
    }

    public Color getBackgroundColor()
    {
        return (Color) ChartUtils.BLUE_STEEL2_LIGHT;
    }

    /**
     * Creates a legend item block.
     * 
     * @param item
     *            the legend item.
     * 
     * @return The block.
     */
    protected void createLegendBlock( BlockContainer blockcontainerLabel )
    {

        XYPlot xyplot = (XYPlot) report.getPlot();

        int nbRenderer = xyplot.getDatasetCount();
        BlockContainer blockcontainer = new BlockContainer( new BorderArrangement() );
        if ( nbRenderer > 1 )
        {
            BlockContainer oldLegendBlockContainer =
                new BlockContainer( new FlowArrangement( HorizontalAlignment.LEFT, VerticalAlignment.TOP, 2.0D, 2.0D ) );
            for ( int i = 0; i < nbRenderer; i++ )
            {
                LegendTitle legendtitle = new LegendTitle( xyplot.getRenderer( i ) );
                legendtitle.setMargin( new RectangleInsets( 1.0, 1.0, 1.0, 1.0 ) );
                legendtitle.setFrame( new LineBorder() );
                legendtitle.setBackgroundPaint( ChartColor.WHITE );
                oldLegendBlockContainer.add( legendtitle );
            }

            blockcontainer.add( oldLegendBlockContainer, RectangleEdge.LEFT );
        }
        else
        {
            LegendTitle legendtitle = new LegendTitle( xyplot.getRenderer( 0 ) );
            legendtitle.setMargin( new RectangleInsets( 1.0, 1.0, 1.0, 1.0 ) );
            legendtitle.setFrame( new LineBorder() );
            legendtitle.setBackgroundPaint( ChartColor.WHITE );
            blockcontainer.add( legendtitle, RectangleEdge.LEFT );
        }

        LegendTitle legendtitle1 = new LegendTitle( xyplot.getRenderer() );
        legendtitle1.setMargin( new RectangleInsets( 1.0, 1.0, 1.0, 1.0 ) );
        legendtitle1.setFrame( new LineBorder() );
        legendtitle1.setBackgroundPaint( ChartColor.WHITE );
        legendtitle1.setWrapper( blockcontainerLabel );

        blockcontainer.add( legendtitle1, RectangleEdge.RIGHT );
        blockcontainer.add( new EmptyBlock( 1000D, 0.0D ) );

        CompositeTitle compositetitle = new CompositeTitle( blockcontainer );
        compositetitle.setPosition( RectangleEdge.BOTTOM );
        report.clearSubtitles();
        report.addSubtitle( compositetitle );
    }

    /**
     * Creates a legend item block.
     * 
     * @param item
     *            the legend item.
     * 
     * @return The block.
     */
    protected Block createLegendItemBlock( LegendItem item, int i )
    {
        BlockContainer result = null;
        LegendGraphic lg = new LegendGraphic( item.getShape(), item.getFillPaint() );
        lg.setFillPaintTransformer( item.getFillPaintTransformer() );
        lg.setShapeFilled( true );
        lg.setLine( item.getLine() );
        lg.setLineStroke( item.getLineStroke() );
        lg.setLinePaint( item.getFillPaint() );
        lg.setLineVisible( true );
        lg.setShapeVisible( true );
        lg.setShapeOutlineVisible( true );
        lg.setOutlinePaint( item.getFillPaint() );
        lg.setOutlineStroke( item.getOutlineStroke() );
        lg.setPadding( new RectangleInsets( 2.0, 2.0, 2.0, 2.0 ) );

        LegendItemBlockContainer legendItem = new LegendItemBlockContainer( new BorderArrangement(), 0, i );
        lg.setShapeAnchor( RectangleAnchor.CENTER );
        lg.setShapeLocation( RectangleAnchor.CENTER );
        legendItem.add( lg, RectangleEdge.LEFT );

        LabelBlock labelBlock = new LabelBlock( item.getLabel(), new Font( "SansSerif", Font.BOLD, 10 ), Color.black );
        labelBlock.setPadding( new RectangleInsets( 2.0, 2.0, 2.0, 2.0 ) );
        legendItem.add( labelBlock );
        legendItem.setToolTipText( item.getToolTipText() );
        legendItem.setURLText( item.getURLText() );

        result = new BlockContainer( new CenterArrangement() );
        result.add( legendItem );

        return result;
    }

}
