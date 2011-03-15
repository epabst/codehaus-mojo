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

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class CloverBarChartStrategy extends AbstractCategoryChartStrategy
{
    /**
     * Default constructor
     *
     * @param bundle
     * @param title
     * @param datas
     */
    public CloverBarChartStrategy( ResourceBundle bundle, String title, Map datas )
    {
        super( bundle, title, datas );
    }

    public void fillDataset()
    {

        if ( datas != null && !datas.isEmpty() )
        {
            Iterator iter = datas.keySet().iterator();
            String coveredLabel = this.bundle.getString( "report.clover.label.covered" );
            // String uncoveredLabel = this.bundle.getString( "report.clover.label.uncovered" );
            while ( iter.hasNext() )
            {
                String key = (String) iter.next();
                CloverReportBean cloverReportBean = (CloverReportBean) datas.get( key );

                this.defaultdataset.setValue( cloverReportBean.getPercentCoveredElements(), coveredLabel,
                                              this.bundle.getString( "report.clover.label.total" ) );

                // this.defaultdataset.setValue( cloverReportBean.getPercentUnCoveredElements(), uncoveredLabel,
                // this.bundle.getString( "report.clover.label.total" ) );

                this.defaultdataset.setValue( cloverReportBean.getPercentCoveredConditionals(), coveredLabel,
                                              this.bundle.getString( "report.clover.label.conditionals" ) );

                // this.defaultdataset.setValue( cloverReportBean.getPercentUnCoveredConditionals(), uncoveredLabel,
                // this.bundle.getString( "report.clover.label.conditionals" ) );
                this.defaultdataset.setValue( cloverReportBean.getPercentCoveredStatements(), coveredLabel,
                                              this.bundle.getString( "report.clover.label.statements" ) );

                // this.defaultdataset.setValue( cloverReportBean.getPercentUnCoveredStatements(), uncoveredLabel,
                // this.bundle.getString( "report.clover.label.statements" ) );
                this.defaultdataset.setValue( cloverReportBean.getPercentCoveredMethods(), coveredLabel,
                                              this.bundle.getString( "report.clover.label.methods" ) );

                // this.defaultdataset.setValue( cloverReportBean.getPercentUnCoveredMethods() , uncoveredLabel,
                // this.bundle.getString( "report.clover.label.methods" ) );
            }
        }
    }

    public Paint[] getPaintColor()
    {
        return new Paint[] { Color.GREEN, Color.RED };
    }
}
