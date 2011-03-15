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

import org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.time.TimeSeries;

public class CoberturaTimeChartStrategy extends AbstractTimeChartStrategy
{
    private TimeSeries linecoverSerie;
    private TimeSeries branchcoverSerie;

    public CoberturaTimeChartStrategy( ResourceBundle bundle, String title, List results, String timeUnit,
                                       Date startDate, Date endDate )
    {
        super( bundle, title, results, timeUnit, startDate, endDate );
    }

    public void fillDataset()
    {
        linecoverSerie = new TimeSeries( this.bundle.getString( "report.cobertura.label.linecover" ), this.periodClass );
        branchcoverSerie = new TimeSeries( this.bundle.getString( "report.cobertura.label.branchcover" ), this.periodClass );

        Iterator iter = mResults.iterator();
        while ( iter.hasNext() )
        {
            CoberturaReportBean cober = (CoberturaReportBean) iter.next();
            Date date = cober.getDateGeneration();
            linecoverSerie.addOrUpdate( getChartDate( this.timePeriod.normalize( date ) ), cober.getLineCoverRate() );
            branchcoverSerie.addOrUpdate( getChartDate( this.timePeriod.normalize( date ) ), cober.getBranchCoverRate() );
        }

        defaultdataset.addSeries( linecoverSerie );
        defaultdataset.addSeries( branchcoverSerie );

    }

    public NumberAxis getRangeAxis()
    {
        NumberAxis valueaxis = new NumberAxis();
        valueaxis.setLowerMargin( 0.0D );
        valueaxis.setUpperMargin(0.099D);
        valueaxis.setRangeWithMargins( 0.0D, 1.0D );
        valueaxis.setLabel( this.getYAxisLabel() );
        valueaxis.setNumberFormatOverride( NumberFormat.getPercentInstance() );
        return valueaxis;
    }

    public String getYAxisLabel()
    {
        return this.bundle.getString( "report.cobertura.label.coverage" );
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
