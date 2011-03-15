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

import org.jfree.data.general.Dataset;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public interface IChartStrategy
{
    /**
     * 
     * @param dashboardReport
     * @return
     */
    //Dataset createDataset( IDashBoardReportBean dashboardReport );
    /**
     * 
     */
    Dataset getDataset();
    
    void fillDataset();
        
    /**
     * 
     * @param dataset
     * @param dashboardReport
     */
    //void createDatasetElement( Dataset dataset, IDashBoardReportBean dashboardReport );

    /**
     * 
     * @return
     */
    String getXAxisLabel();

    /**
     * 
     * @return
     */
    String getYAxisLabel();
    
    /**
     * 
     * @return
     */
    void setXAxisLabel( String xAxisLabel);

    /**
     * 
     * @return
     */
    void setYAxisLabel( String yAxisLabel );

    /**
     * 
     * @return
     */
    Paint[] getPaintColor();

    /**
     * 
     * @return
     */
    boolean isDatasetEmpty();

    /**
     * 
     * @param isDatasetEmpty
     */
    void setDatasetEmpty( boolean isDatasetEmpty );

    /**
     * 
     * @return
     */
    //ResourceBundle getBundle();
    /**
     * 
     * @return
     */
    String getTitle();
        
}
