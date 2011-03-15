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

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * Utility class for performing the actual rendering of charts.
 * 
 * @author ksr@lakeside.dk
 */
public final class ChartRendererImpl implements ChartRenderer {

    private static final int HEIGHT = 400;
    private static final int WIDTH = 800;
    private String outputDirectory;

    /**
     * Constructor for the <code>ChartRendererImpl</code> class.
     * 
     * @param outputDir
     *            The directory where generated charts is to be saved.
     */
    public ChartRendererImpl(String outputDir) {
        this.outputDirectory = outputDir;
    }

    /**
     * Save a {@link JFreeChart} to the filesystem.
     * 
     * @param filename
     *            The filename of the chart to save
     * @param chart
     *            the {@link JFreeChart} to save as a file
     * @throws IOException
     *             If the file cannot be saved
     */
    public void renderChart(String filename, JFreeChart chart) throws IOException {
        File parentDir = new File(outputDirectory);

        File imageDir = new File(parentDir, "images");
        if(!imageDir.exists()) {
            imageDir.mkdirs();
        }

        File pngFile = new File(imageDir, filename + ".png");
        ChartUtilities.saveChartAsPNG(pngFile, chart, WIDTH, HEIGHT);
    }
}