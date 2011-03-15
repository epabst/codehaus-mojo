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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public abstract class AbstractChartRenderer implements IChartRenderer
{

    /**
     * Wrapped chart.
     */
    protected JFreeChart report = null;

    /**
     * Width of the resulting chart file.
     */
    private int width = ChartUtils.STANDARD_WIDTH;

    /**
     * Height of the resulting chart file.
     */
    private int height = ChartUtils.STANDARD_HEIGHT;

    /**
     *
     */
    protected IChartStrategy datasetStrategy;

    protected AbstractChartRenderer()
    {

    }

    /**
     *
     * @param strategy
     */
    public AbstractChartRenderer( IChartStrategy strategy )
    {

        this.datasetStrategy = strategy;
        createChart();
    }
    /**
     *
     * @param strategy
     * @param width
     * @param height
     */
    public AbstractChartRenderer( IChartStrategy strategy, int width, int height )
    {
        this( strategy );
        this.width = width;
        this.height = height;
    }
    /**
     * create the chart with the IChartStrategy
     *
     */
    public abstract void createChart();

    /**
     * create the chart with the IChartStrategy
     *
     */
    public JFreeChart getChart(){
    	return report;
    }

    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#isEmpty()
	 */
    public boolean isEmpty()
    {
        return this.datasetStrategy.isDatasetEmpty();
    }
    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#getFileExtension()
	 */
    public String getFileExtension()
    {
        return "png";
    }


    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#getMimeType()
	 */
    public String getMimeType()
    {
        return "image/png";
    }

    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#saveToFile(java.lang.String)
	 */
    public void saveToFile( String filename ) throws IOException
    {
    	report.setBackgroundPaint( getBackgroundColor() );
    	File imageFile = new File( filename );

        imageFile.getParentFile().mkdirs();

        ChartUtilities.saveChartAsPNG( new File( filename ), report, width, height );
    }

    public Color getBackgroundColor()
    {
    	return (Color) ChartUtils.BLUE_STEEL2_LIGHT;
    }

    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#createBufferedImage(int, int)
	 */
    public BufferedImage createBufferedImage( int imageWidth, int imageHeight )
    {
        return report.createBufferedImage( imageWidth, imageHeight );
    }
    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#setHeight(int)
	 */
    public void setHeight( int _height )
    {
        this.height = _height;
    }
    /* (non-Javadoc)
	 * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer#setWidth(int)
	 */
    public void setWidth( int _width )
    {
        this.width = _width;
    }
}