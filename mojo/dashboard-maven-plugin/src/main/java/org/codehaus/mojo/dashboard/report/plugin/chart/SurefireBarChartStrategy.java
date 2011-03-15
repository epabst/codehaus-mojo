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

import org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean;
import org.jfree.chart.ChartColor;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class SurefireBarChartStrategy extends AbstractCategoryChartStrategy
{
    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param datas
     */
    public SurefireBarChartStrategy( ResourceBundle bundle, String title, Map datas )
    {
        super( bundle, title, datas );
    }

    /**
     *
     */
    public void fillDataset()
    {

        if( datas != null && !datas.isEmpty())
        {
            Iterator iter = datas.keySet().iterator();

            while(iter.hasNext()){
                String key = (String)iter.next();
                SurefireReportBean fireReportBean = (SurefireReportBean)datas.get( key );
                int total = fireReportBean.getNbTests();
                int error = fireReportBean.getNbErrors();
                int fail = fireReportBean.getNbFailures();
                int skip = fireReportBean.getNbSkipped();
                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                   ( total - error - fail - skip ),
                                                   this.bundle.getString( "report.surefire.label.success" ),
                                                   key );

                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                   error,
                                                   this.bundle.getString( "report.surefire.label.errors" ),
                                                   key );

                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                   fail,
                                                   this.bundle.getString( "report.surefire.label.failures" ),
                                                   key );

                ( (DefaultCategoryDataset) defaultdataset ).setValue(
                                                   skip,
                                                   this.bundle.getString( "report.surefire.label.skipped" ),
                                                   key );
            }
        }

    }
    /**
     *
     */
    public Paint[] getPaintColor()
    {
        return new Paint[] { ChartColor.GREEN, ChartColor.RED, ChartColor.ORANGE, ChartColor.LIGHT_GRAY };
    }
}
