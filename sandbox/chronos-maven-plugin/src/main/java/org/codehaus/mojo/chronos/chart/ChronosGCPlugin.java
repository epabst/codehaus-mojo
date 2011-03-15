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

import java.util.Collections;
import java.util.Map;

import org.codehaus.mojo.chronos.gc.GCSamples;

/**
 * Plugin to add gc charts to the reports.
 * 
 * @author ksr@lakeside.dk
 */
public final class ChronosGCPlugin implements ChronosReportPlugin {
    private GCSamples gcsamples;

    public ChronosGCPlugin(GCSamples samples) {
        this.gcsamples = samples;
    }

    public ChartSource getSummaryChartSource() {
        return new SummaryGCChartSource(gcsamples);
    }

    public Map getDetailChartSources() {
        return Collections.EMPTY_MAP;
    }
}