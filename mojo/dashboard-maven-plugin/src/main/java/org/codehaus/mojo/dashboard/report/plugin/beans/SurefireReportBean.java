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
public class SurefireReportBean extends AbstractReportBean
{
    /**
     * 
     */
    private int nbTests;
    /**
     * 
     */
    private int nbErrors;
    /**
     * 
     */
    private int nbFailures;
    /**
     * 
     */
    private int nbSkipped;
    /**
     * 
     */
    private double sucessRate;
    /**
     * 
     */
    private double elapsedTime;
    /**
     * 
     */
    private static final int PCENT = 100;
    /**
     * 
     *
     */
    public SurefireReportBean()
    {
    }
    /**
     * 
     * @param projectName
     */
    public SurefireReportBean( Date dateGeneration )
    {
        super( dateGeneration );
    }
    /**
     * 
     * @return
     */
    public double getElapsedTime()
    {
        return elapsedTime;
    }
    /**
     * 
     * @param elapsedTime
     */
    public void setElapsedTime( double elapsedTime )
    {
        this.elapsedTime = elapsedTime;
    }
    /**
     * 
     * @return
     */
    public int getNbErrors()
    {
        return nbErrors;
    }
    /**
     * 
     * @param nbErrors
     */
    public void setNbErrors( int nbErrors )
    {
        this.nbErrors = nbErrors;
    }
    /**
     * 
     * @return
     */
    public int getNbFailures()
    {
        return nbFailures;
    }
    /**
     * 
     * @param nbFailures
     */
    public void setNbFailures( int nbFailures )
    {
        this.nbFailures = nbFailures;
    }
    /**
     * 
     * @return
     */
    public int getNbSkipped()
    {
        return nbSkipped;
    }
    /**
     * 
     * @param nbSkipped
     */
    public void setNbSkipped( int nbSkipped )
    {
        this.nbSkipped = nbSkipped;
    }
    /**
     * 
     * @return
     */
    public int getNbTests()
    {
        return nbTests;
    }
    /**
     * 
     * @param nbTests
     */
    public void setNbTests( int nbTests )
    {
        this.nbTests = nbTests;
    }
    /**
     * 
     * @return
     */
    public double getSucessRate()
    {
        return sucessRate;
    }
    /**
     * 
     * @param sucessRate
     */
    public void setSucessRate( double sucessRate )
    {
        this.sucessRate = sucessRate;
    }
    /**
     * 
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {
        if ( dashboardReport != null && dashboardReport instanceof SurefireReportBean )
        {
            this.nbTests = this.nbTests + ( (SurefireReportBean) dashboardReport ).getNbTests();

            this.nbErrors = this.nbErrors + ( (SurefireReportBean) dashboardReport ).getNbErrors();

            this.nbFailures = this.nbFailures + ( (SurefireReportBean) dashboardReport ).getNbFailures();

            this.nbSkipped = this.nbSkipped + ( (SurefireReportBean) dashboardReport ).getNbSkipped();

            this.elapsedTime = this.elapsedTime + ( (SurefireReportBean) dashboardReport ).getElapsedTime();

            if ( this.nbTests == 0 )
            {
                this.sucessRate = 0D;
            }
            else
            {
                double success = (double) ( this.nbTests - this.nbErrors - this.nbFailures - this.nbSkipped );
                this.sucessRate = ( success / (double) this.nbTests ) * PCENT;
            }
        }
    }
}
