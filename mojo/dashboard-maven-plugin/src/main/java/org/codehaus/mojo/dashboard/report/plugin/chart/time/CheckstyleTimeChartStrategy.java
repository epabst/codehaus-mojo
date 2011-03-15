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

import java.awt.Paint;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;
import org.jfree.chart.ChartColor;
import org.jfree.data.time.TimeSeries;

public class CheckstyleTimeChartStrategy extends AbstractTimeChartStrategy
{
    
    public CheckstyleTimeChartStrategy(ResourceBundle bundle, String title,List results, String timeUnit, Date startDate, Date endDate)
    {
        super(bundle, title, results,timeUnit, startDate, endDate);
    }
    
    public void fillDataset()
    {
        TimeSeries s1 = new TimeSeries(this.bundle.getString( "report.checkstyle.files" ), this.periodClass);
        TimeSeries s2 = new TimeSeries(this.bundle.getString( "report.checkstyle.column.total" ),  this.periodClass);
        TimeSeries s3 = new TimeSeries(this.bundle.getString( "report.checkstyle.column.infos" ),  this.periodClass);
        TimeSeries s4 = new TimeSeries(this.bundle.getString( "report.checkstyle.column.warnings" ),  this.periodClass);
        TimeSeries s5 = new TimeSeries(this.bundle.getString( "report.checkstyle.column.errors" ),  this.periodClass);
        Iterator iter = mResults.iterator();
        while(iter.hasNext()){
            
            CheckstyleReportBean check = (CheckstyleReportBean)iter.next();
            Date date = check.getDateGeneration();
            s1.addOrUpdate( getChartDate(this.timePeriod.normalize( date )), check.getNbClasses() );
            s2.addOrUpdate( getChartDate(this.timePeriod.normalize( date )), check.getNbTotal());
            s3.addOrUpdate( getChartDate(this.timePeriod.normalize( date )), check.getNbInfos());
            s4.addOrUpdate( getChartDate(this.timePeriod.normalize( date )), check.getNbWarnings());
            s5.addOrUpdate( getChartDate(this.timePeriod.normalize( date )), check.getNbErrors());
        }
        
        defaultdataset.addSeries(s1);
        defaultdataset.addSeries(s2);
        defaultdataset.addSeries(s3);
        defaultdataset.addSeries(s4);
        defaultdataset.addSeries(s5);

    }
    
    public Paint[] getPaintColor()
    {
        return new Paint[] { ChartColor.GREEN, ChartColor.BLACK, ChartUtils.BLUE_LIGHT, ChartUtils.YELLOW_LIGHT, ChartColor.RED };
    }

}
