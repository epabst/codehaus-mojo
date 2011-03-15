package org.codehaus.mojo.dashboard.report.plugin.utils;

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

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class ChartUtils
{

    /**
     * Blue light color.
     */
    public static final Paint BLUE_LIGHT = new Color( 65, 105, 225 );
    public static final Paint BLUE_STEEL1_LIGHT = new Color( 188, 210, 238 );
    public static final Paint BLUE_STEEL2_LIGHT = new Color( 202, 225, 255 );
    public static final Paint BLUE_VERY_LIGHT = new Color( 222, 245, 245 );



    /**
     * Yellow light color.
     */
    public static final Paint YELLOW_LIGHT = new Color( 255, 255, 153 );

    /**
     * Standard width of the resulting chart file.
     */
    public static final int STANDARD_WIDTH = 800;

    /**
     * Standard Height of the resulting chart file.
     */
    public static final int STANDARD_HEIGHT = 400;

    /**
     * minimum width of the resulting chart file.
     */
    public static final int MINIMUM_WIDTH = 600;

    /**
     * minimum Height of the resulting chart file.
     */
    public static final int MINIMUM_HEIGHT = 300;

    /**
     * Standard Height of a single bar chart line.
     */
    public static final int STANDARD_BARCHART_ENTRY_HEIGHT = 13;

    /**
     * Additional Height of a bar chart.
     */
    public static final int STANDARD_BARCHART_ADDITIONAL_HEIGHT = 40;

    /**
     * Standard width of a time series chart line.
     */
    public static final int STANDARD_TIME_ENTRY_WIDTH = 60;

    /**
     * Additional width of a time series chart.
     */
    public static final int STANDARD_TIME_ADDITIONAL_WIDTH = 50;

    /**
     * Additional width of a time series chart.
     */
    public static final int STANDARD_TIME_ADDITIONAL_HEIGHT = 50;

}
