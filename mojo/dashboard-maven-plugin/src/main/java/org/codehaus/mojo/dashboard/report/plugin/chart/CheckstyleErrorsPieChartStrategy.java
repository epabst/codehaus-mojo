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
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleError;
import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartColor;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Checkstyle error dataset strategy class. Corrections written by <a href="mailto:dvicente72@gmail.com">David Vicente</a>
 *
 * @author <a href="srivollet@objectif-informatique.fr">Sylvain Rivollet</a>
 */
public class CheckstyleErrorsPieChartStrategy extends AbstractPieChartStrategy
{

    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param dashboardReport
     */
    public CheckstyleErrorsPieChartStrategy( ResourceBundle bundle, String title, IDashBoardReportBean dashboardReport )
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
        if ( mDashboardReport instanceof CheckstyleReportBean )
        {
            float percentVal = 0;
            int nbItInfPercent = 0;
            CheckstyleReportBean checkstyleReportBean = (CheckstyleReportBean) mDashboardReport;
            CheckstyleError error = new CheckstyleError();
            Iterator iterator = checkstyleReportBean.getErrors().iterator();

            while ( iterator.hasNext() )
            {
                error = (CheckstyleError) iterator.next();
                percentVal = percent( error.getNbIteration(), checkstyleReportBean.getNbTotal() );

                if ( percentVal > 1 )
                {
                    ( (DefaultPieDataset) this.defaultdataset ).setValue(
                                                                          error.getMessage() + " = " + percentVal + "%",
                                                                          error.getNbIteration() );
                }
                else
                {
                    nbItInfPercent += error.getNbIteration();
                }
            }

            if ( nbItInfPercent > 0 )
            {
                percentVal = percent( nbItInfPercent, checkstyleReportBean.getNbTotal() );
                ( (DefaultPieDataset) this.defaultdataset ).setValue(
                                                                      this.bundle.getString( "chart.checkstyle.violations.others.label" )
                                                                                      + " = " + percentVal + "%",
                                                                      nbItInfPercent );
            }
        }

    }

}
