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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.ReportConfig;
import org.jfree.chart.JFreeChart;

/**
 * Generates the charts of the jmeter report.
 * 
 * @author ksr@lakeside.dk
 */
public final class GraphGenerator {
    private List summaryChartSources = new ArrayList();
    private Map detailsChartSources = new LinkedHashMap();

    public GraphGenerator(List plugins) {
        for (Iterator iterator = plugins.iterator(); iterator.hasNext();) {
            ChronosReportPlugin plugin = (ChronosReportPlugin)iterator.next();
            ChartSource summarySource = plugin.getSummaryChartSource();
            if(summarySource != null) {
                summaryChartSources.add(summarySource);
            }
            Map detailsSources = plugin.getDetailChartSources();
            for (Iterator iterator2 = detailsSources.keySet().iterator(); iterator2.hasNext();) {
                String testName = (String)iterator2.next();
                List existing = (List)detailsChartSources.get(testName);
                if(existing == null) {
                    existing = new ArrayList();
                    detailsChartSources.put(testName, existing);
                }
                existing.add(detailsSources.get(testName));
            }
        }
    }

    /**
     * Generates response, throughput, histogram and gc charts according to report parameters.
     */
    public void generateGraphs(ChartRenderer renderer, ResourceBundle bundle, ReportConfig config) throws IOException {
        for (Iterator iterator = getSummaryChartSources().iterator(); iterator.hasNext();) {
            ChartSource chartSource = (ChartSource)iterator.next();
            if(chartSource.isEnabled(bundle, config)) {
                JFreeChart chart = chartSource.getChart(bundle, config);
                String fileName = chartSource.getFileName(bundle, config);
                renderer.renderChart(fileName, chart);
            }
        }
        for (Iterator iterator = detailsChartSources.values().iterator(); iterator.hasNext();) {
            List sources = (List)iterator.next();
            for (Iterator iterator2 = sources.iterator(); iterator2.hasNext();) {
                ChartSource source = (ChartSource)iterator2.next();
                if(source.isEnabled(bundle, config)) {
                    JFreeChart chart = source.getChart(bundle, config);
                    String fileName = source.getFileName(bundle, config);
                    renderer.renderChart(fileName, chart);
                }
            }
        }
    }

    public List getSummaryChartSources() {
        return summaryChartSources;
    }

    public List getDetailsChartSources(String testName) {
        return (List)detailsChartSources.get(testName);
    }
}
