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


import java.util.ResourceBundle;

import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public abstract class AbstractPieChartStrategy extends AbstractChartStrategy
{

    /**
     * dataset used to store graph datas
     */
    protected DefaultPieDataset defaultdataset = new DefaultPieDataset();
    /**
     * resource bundle
     */
    protected ResourceBundle bundle;
    /**
     * dashboard report used to retreive datas
     */
    protected IDashBoardReportBean mDashboardReport;

    /**
     *
     * @param bundle
     */
    public AbstractPieChartStrategy( ResourceBundle bundle, String title, IDashBoardReportBean dashboardReport )
    {
        this.setTitle( title );
        this.bundle = bundle;
        this.mDashboardReport = dashboardReport;
    }

    /**
     * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartStrategy#getDataset()
     */
    public Dataset getDataset()
    {
        fillDataset();
        if ( defaultdataset.getItemCount() > 0 )
        {
            this.setDatasetEmpty( false );
        }
        return defaultdataset;
    }

}
