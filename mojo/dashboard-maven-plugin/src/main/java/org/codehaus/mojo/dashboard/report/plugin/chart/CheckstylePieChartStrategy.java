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

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartColor;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class CheckstylePieChartStrategy extends AbstractPieChartStrategy
{

    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param dashboardReport
     */
    public CheckstylePieChartStrategy( ResourceBundle bundle, String title, IDashBoardReportBean dashboardReport )
    {
        super( bundle, title, dashboardReport );
    }

    /**
     *
     */
    public void fillDataset()
    {
        if ( mDashboardReport instanceof CheckstyleReportBean )
        {
            CheckstyleReportBean checkstyleReportBean = (CheckstyleReportBean) mDashboardReport;

            int info = checkstyleReportBean.getNbInfos();
            int error = checkstyleReportBean.getNbErrors();
            int warn = checkstyleReportBean.getNbWarnings();

            this.defaultdataset.setValue( this.bundle.getString( "report.checkstyle.column.infos" ) + " = " + info,
                                          info );

            this.defaultdataset.setValue( this.bundle.getString( "report.checkstyle.column.errors" ) + " = " + error,
                                          error );

            this.defaultdataset.setValue( this.bundle.getString( "report.checkstyle.column.warnings" ) + " = " + warn,
                                          warn );
        }
    }

    /**
     *
     */
    public Paint[] getPaintColor()
    {
        return new Paint[] { ChartUtils.BLUE_LIGHT, ChartColor.RED, ChartUtils.YELLOW_LIGHT };
    }
}
