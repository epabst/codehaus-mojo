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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

/**
 * Pie Chart 3D renderer.
 * Refactoring by <a href="mailto:dvicente72@gmail.com">David Vicente</a>
 * @author <a href="srivollet@objectif-informatique.fr">Sylvain Rivollet</a>
 */
public class PieChart3DRenderer extends AbstractChartRenderer
{

    private static final double START_ANGLE = 45D;

    private static final float FOREGROUND_ALPHA = 0.5f;

    /**
     *
     */
    private static final int FONT_SIZE = 10;

    /**
     *
     * @param dashboardReport
     * @param strategy
     */
    public PieChart3DRenderer( IChartStrategy strategy )
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
    public PieChart3DRenderer( IChartStrategy strategy, int width, int height )
    {
        super( strategy, width, height );
    }

    public void createChart()
    {
        PieDataset dataset = (PieDataset) this.datasetStrategy.getDataset();
        report = ChartFactory.createPieChart3D( this.datasetStrategy.getTitle(), dataset, false, true, true );

        PiePlot3D plot3D = (PiePlot3D) report.getPlot();
        plot3D.setDirection( Rotation.ANTICLOCKWISE );
        plot3D.setStartAngle( PieChart3DRenderer.START_ANGLE );
        plot3D.setForegroundAlpha( PieChart3DRenderer.FOREGROUND_ALPHA );
        plot3D.setLabelFont( new Font("Lucida", 0, PieChart3DRenderer.FONT_SIZE ) );

    }
}