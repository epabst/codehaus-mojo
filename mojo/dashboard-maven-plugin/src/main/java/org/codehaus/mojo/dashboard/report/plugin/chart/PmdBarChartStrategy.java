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


import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class PmdBarChartStrategy extends AbstractCategoryChartStrategy
{

    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param datas
     */
    public PmdBarChartStrategy( ResourceBundle bundle, String title, Map datas )
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
                PmdReportBean pmdReportBean = (PmdReportBean)datas.get( key );
                //String project = this.getTitle();
                String classes = this.bundle.getString( "report.pmd.label.nbclasses" );
                String violations = this.bundle.getString( "report.pmd.label.nbviolations" );

                ( (DefaultCategoryDataset) defaultdataset ).addValue( pmdReportBean.getNbClasses(), classes, key );
                ( (DefaultCategoryDataset) defaultdataset ).addValue( pmdReportBean.getNbViolations(), violations, key );
            }
        }

    }
    /**
     *
     */
    public String getXAxisLabel()
    {
        return "value";
    }
}
