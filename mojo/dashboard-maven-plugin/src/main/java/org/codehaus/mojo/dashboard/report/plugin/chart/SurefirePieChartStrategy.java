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
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean;
import org.jfree.chart.ChartColor;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class SurefirePieChartStrategy extends AbstractPieChartStrategy
{

    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param dashboardReport
     */
    public SurefirePieChartStrategy( ResourceBundle bundle, String title, IDashBoardReportBean dashboardReport )
    {
        super( bundle, title, dashboardReport );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartStrategy#fillDataset()
     */
    public void fillDataset()
    {
        if ( mDashboardReport != null )
        {
            SurefireReportBean fireReportBean = (SurefireReportBean) mDashboardReport;

            int total = fireReportBean.getNbTests();
            int error = fireReportBean.getNbErrors();
            int fail = fireReportBean.getNbFailures();
            int skip = fireReportBean.getNbSkipped();
            int success = total - error - fail - skip;
            defaultdataset.setValue( this.bundle.getString( "report.surefire.label.success" )
                            + " = " + ( success ), ( success ) );

            defaultdataset.setValue( this.bundle.getString( "report.surefire.label.errors" )
                            + " = " + error, error );

            defaultdataset.setValue( this.bundle.getString( "report.surefire.label.failures" )
                            + " = " + fail, fail );

            defaultdataset.setValue( this.bundle.getString( "report.surefire.label.skipped" )
                            + " = " + skip, skip );
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
