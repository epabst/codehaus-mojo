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

import java.awt.Paint;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartColor;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class CheckstyleBarChartStrategy extends AbstractCategoryChartStrategy
{
    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param datas
     */
    public CheckstyleBarChartStrategy( ResourceBundle bundle, String title, Map datas )
    {
        super( bundle, title, datas );
    }

    public void fillDataset()
    {
        if ( datas != null && !datas.isEmpty() )
        {
            Iterator iter = datas.keySet().iterator();

            while ( iter.hasNext() )
            {
                String key = (String) iter.next();
                CheckstyleReportBean checkReportBean = (CheckstyleReportBean) datas.get( key );
                int info = checkReportBean.getNbInfos();
                int error = checkReportBean.getNbErrors();
                int warn = checkReportBean.getNbWarnings();

                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                                      info,
                                                                      this.bundle.getString( "report.checkstyle.column.infos" ),
                                                                      key );

                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                                      error,
                                                                      this.bundle.getString( "report.checkstyle.column.errors" ),
                                                                      key );

                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                                      warn,
                                                                      this.bundle.getString( "report.checkstyle.column.warnings" ),
                                                                      key );
            }
        }
    }

    public Paint[] getPaintColor()
    {
        return new Paint[] { ChartUtils.BLUE_LIGHT, ChartColor.RED, ChartUtils.YELLOW_LIGHT };
    }

}
