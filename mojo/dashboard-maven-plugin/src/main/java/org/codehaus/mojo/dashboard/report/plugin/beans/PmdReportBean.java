package org.codehaus.mojo.dashboard.report.plugin.beans;

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

import java.util.Date;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class PmdReportBean extends AbstractReportBean
{
    /**
     * 
     */
    private int nbClasses;
    /**
     * 
     */
    private int nbViolations;
    /**
     * 
     *
     */
    public PmdReportBean()
    {
    }
    /**
     * 
     * @param dateGeneration
     */
    public PmdReportBean( Date dateGeneration )
    {
        super( dateGeneration );
    }
    /**
     * 
     * @return
     */
    public int getNbClasses()
    {
        return nbClasses;
    }
    /**
     * 
     * @param nbClasses
     */
    public void setNbClasses( int nbClasses )
    {
        this.nbClasses = nbClasses;
    }
    /**
     * 
     * @return
     */
    public int getNbViolations()
    {
        return nbViolations;
    }
    /**
     * 
     * @param nbViolations
     */
    public void setNbViolations( int nbViolations )
    {
        this.nbViolations = nbViolations;
    }
    /**
     * 
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {
        if ( dashboardReport != null && dashboardReport instanceof PmdReportBean )
        {
            this.nbClasses = this.nbClasses + ( (PmdReportBean) dashboardReport ).getNbClasses();
            this.nbViolations = this.nbViolations + ( (PmdReportBean) dashboardReport ).getNbViolations();
        }
    }
}
