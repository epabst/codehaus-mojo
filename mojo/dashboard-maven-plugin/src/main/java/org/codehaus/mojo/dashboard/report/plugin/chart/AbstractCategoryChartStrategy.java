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


import java.util.Map;
import java.util.ResourceBundle;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public abstract class AbstractCategoryChartStrategy extends AbstractChartStrategy
{
    /**
     * dataset used to store graph datas
     */
    protected DefaultCategoryDataset defaultdataset = new DefaultCategoryDataset();
    /**
     * resource bundle
     */
    protected ResourceBundle bundle;
    /**
     * datas
     */
    protected Map datas;
    /**
     *
     * @param bundle
     */
    public AbstractCategoryChartStrategy( ResourceBundle bundle, String title, Map datas )
    {
        this.setTitle( title );
        this.bundle = bundle;
        this.datas = datas;
    }

    /* (non-Javadoc)
     * @see org.codehaus.mojo.dashboard.report.plugin.chart.IChartStrategy#getDataset()
     */
    public Dataset getDataset()
    {
        fillDataset();
        if ( defaultdataset.getRowCount() > 0 && defaultdataset.getColumnCount() > 0 )
        {
            this.setDatasetEmpty( false );
        }
        return defaultdataset;
    }

}
