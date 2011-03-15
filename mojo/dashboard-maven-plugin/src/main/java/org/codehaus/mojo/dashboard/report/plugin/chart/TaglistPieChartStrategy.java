package org.codehaus.mojo.dashboard.report.plugin.chart;

/*
 * Copyright 2008 Henrik Lynggaard
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

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean;

/**
 *
 * @author Henrik Lynggaard
 *
 */
public class TaglistPieChartStrategy extends AbstractPieChartStrategy
{

    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param dashboardReport
     */
    public TaglistPieChartStrategy( ResourceBundle bundle, String title, IDashBoardReportBean dashboardReport )
    {
        super( bundle, title, dashboardReport );
    }

    /**
     *
     */
    public void fillDataset()
    {
        if ( mDashboardReport instanceof TagListReportBean )
        {
            TagListReportBean taglistReportBean = (TagListReportBean) mDashboardReport;
            Iterator tags = taglistReportBean.getTags().entrySet().iterator();
            while ( tags.hasNext() )
            {
                Map.Entry entry = (Map.Entry) tags.next();
                String name = (String) entry.getKey();
                int value = ( (Integer) entry.getValue() ).intValue();
                this.defaultdataset.setValue( name + " = " + value, value );

            }
        }
    }
}
