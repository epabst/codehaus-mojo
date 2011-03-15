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


import java.awt.Font;
import java.awt.Paint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class PieChartRenderer extends AbstractChartRenderer
{
    /**
     * 
     */
    private static final int FONT_SIZE = 10;
    /**
     * 
     */
    private static final double INTERIOR_GAP = 0.33D;
    /**
     * 
     */
    private static final double LABEL_GAP = 0.02D;
    /**
     * 
     */
    private static final double START_ANGLE = 45D;

    /**
     * 
     * @param dashboardReport
     * @param strategy
     */
    public PieChartRenderer( IChartStrategy strategy )
    {
        super( strategy );
    }

    /**
     * 
     * @param dashboardReport
     * @param strategy
     * @param width
     * @param height
     */
    public PieChartRenderer( IChartStrategy strategy,
                             int width, int height )
    {
        super( strategy, width, height );
    }
    
    /* (non-Javadoc)
     * @see org.codehaus.mojo.dashboard.report.plugin.chart.AbstractChartRenderer#createChart()
     */
    public void createChart()
    {

        PieDataset dataset = (PieDataset) this.datasetStrategy.getDataset();
        report = ChartFactory.createPieChart( this.datasetStrategy.getTitle(), dataset, true, true, false );
        PiePlot plot = (PiePlot) report.getPlot();
//        plot.setCircular( false );
        plot.setDirection( Rotation.ANTICLOCKWISE );
        /*
         * plot.setExplodePercent(0, 0.15D); plot.setExplodePercent(1, 0.15D);
         */
//        plot.setInteriorGap( PieChartRenderer.INTERIOR_GAP );
        plot.setLabelFont( new Font("Lucida", 0, PieChartRenderer.FONT_SIZE ) );
        plot.setLabelGap( PieChartRenderer.LABEL_GAP );
        plot.setNoDataMessage( "No data available" );
        plot.setStartAngle( PieChartRenderer.START_ANGLE );
        Paint[] paints = this.datasetStrategy.getPaintColor();

        for ( int i = 0; i < dataset.getItemCount() && i < paints.length; i++ )
        {
            plot.setSectionPaint( dataset.getKey( i ), paints[i] );
        }
    }
}
