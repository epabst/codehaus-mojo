package org.codehaus.mojo.dashboard.report.plugin.chart.time;

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

import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.time.TimeSeries;

public class CloverTimeChartStrategy extends AbstractTimeChartStrategy
{

    public CloverTimeChartStrategy( ResourceBundle bundle, String title, List results, String timeUnit, Date startDate,
                                    Date endDate )
    {
        super( bundle, title, results, timeUnit, startDate, endDate );
    }

    public void fillDataset()
    {
        TimeSeries s1 = new TimeSeries( this.bundle.getString( "report.clover.label.total" ), this.periodClass );
        TimeSeries s2 = new TimeSeries( this.bundle.getString( "report.clover.label.conditionals" ), this.periodClass );
        TimeSeries s3 = new TimeSeries( this.bundle.getString( "report.clover.label.statements" ), this.periodClass );
        TimeSeries s4 = new TimeSeries( this.bundle.getString( "report.clover.label.methods" ), this.periodClass );
        Iterator iter = mResults.iterator();
        while ( iter.hasNext() )
        {

            CloverReportBean clover = (CloverReportBean) iter.next();
            Date date = clover.getDateGeneration();
            int total = clover.getElements();
            int covered = clover.getCoveredElements();
            s1.addOrUpdate( getChartDate( this.timePeriod.normalize( date ) ),  ( covered / (double) total ) );
            
            int totalCond = clover.getConditionals();
            int coveredCond = clover.getCoveredConditionals();
            s2.addOrUpdate( getChartDate( this.timePeriod.normalize( date ) ), ( coveredCond / (double) totalCond ) );
            
            int totalStat = clover.getStatements();
            int coveredStat = clover.getCoveredStatements();
            s3.addOrUpdate( getChartDate( this.timePeriod.normalize( date ) ), ( coveredStat / (double) totalStat ) );
            
            int totalMeth = clover.getMethods();
            int coveredMeth = clover.getCoveredMethods();
            s4.addOrUpdate( getChartDate( this.timePeriod.normalize( date ) ), ( coveredMeth / (double) totalMeth ) );
            
        }

        defaultdataset.addSeries( s1 );
        defaultdataset.addSeries( s2 );
        defaultdataset.addSeries( s3 );
        defaultdataset.addSeries( s4 );

    }
    /**
     * 
     */
    public NumberAxis getRangeAxis()
    {
        NumberAxis valueaxis = new NumberAxis();
        valueaxis.setLowerMargin( 0.0D );
        valueaxis.setUpperMargin(0.05D);
        valueaxis.setRangeWithMargins( 0.0D, 1.0D );
        valueaxis.setLabel( this.getYAxisLabel() );
        valueaxis.setNumberFormatOverride( NumberFormat.getPercentInstance() );
        return valueaxis;
    }
    /**
     * 
     */
    public String getYAxisLabel()
    {
        return this.bundle.getString( "report.clover.label.coverage" );
    }
    
    public XYItemLabelGenerator getLabelGenerator()
    {
        StandardXYItemLabelGenerator labelgenerator =
            new StandardXYItemLabelGenerator( StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                                              this.timePeriod.getDateFormat(),
                                              NumberFormat.getPercentInstance( Locale.getDefault() ) );
        return labelgenerator;
    }

}
