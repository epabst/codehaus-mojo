package org.codehaus.mojo.dashboard.report.plugin.chart;

/*
 * Copyright 2007 David Vicente
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

import org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartColor;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Findbugs categories dataset strategy class.
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class FindbugsCategoriesPieChartStrategy extends AbstractPieChartStrategy
{
    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param dashboardReport
     */
    public FindbugsCategoriesPieChartStrategy( ResourceBundle bundle, String title, IDashBoardReportBean dashboardReport )
    {
        super( bundle, title, dashboardReport );
    }

    /**
     *
     */
    public Paint[] getPaintColor()
    {
        return new Paint[] { ChartUtils.BLUE_LIGHT, ChartColor.RED, ChartUtils.YELLOW_LIGHT };
    }

    /**
     * give the percentage of iteration compared to the total error count
     */
    private float percent( int nbIteration, int nbTotal )
    {
        float percent = nbIteration * 100f / nbTotal;
        percent *= 1000;
        percent = (int) ( percent + .5 );
        percent /= 1000;
        return percent;
    }

    public void fillDataset()
    {
        if ( mDashboardReport instanceof FindBugsReportBean )
        {
            float percentVal = 0;
            FindBugsReportBean findBugsReportBean = (FindBugsReportBean) mDashboardReport;

            Map categories = findBugsReportBean.getCategories();
            Iterator iterator = categories.keySet().iterator();

            while ( iterator.hasNext() )
            {
                String category = (String) iterator.next();
                Integer value = (Integer) categories.get( category );
                percentVal = percent( value.intValue(), findBugsReportBean.getNbBugs() );

                ( (DefaultPieDataset) this.defaultdataset ).setValue( category + " = " + percentVal + "%",
                                                                      value.intValue() );
            }
        }

    }

}
