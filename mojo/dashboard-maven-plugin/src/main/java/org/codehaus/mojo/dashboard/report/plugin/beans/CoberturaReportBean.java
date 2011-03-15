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
public class CoberturaReportBean extends AbstractReportBean
{
    /**
     * 
     */
    private int nbClasses;
    /**
     * 
     */
    private double lineCoverRate;
    /**
     * 
     */
    private double branchCoverRate;
    /**
     * 
     */
    private int numberOfCoveredBranches;
    /**
     * 
     */
    private int numberOfCoveredLines;
    /**
     * 
     */
    private int numberOfValidBranches;
    /**
     * 
     */
    private int numberOfValidLines;
    /**
     * Default constructor
     *
     */
    public CoberturaReportBean()
    {
    }
    /**
     * 
     * @param dateGeneration
     */
    public CoberturaReportBean( Date dateGeneration )
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
    public double getLineCoverRate()
    {
        return lineCoverRate;
    }
    /**
     * 
     * @param lineCoverRate
     */
    public void setLineCoverRate( double lineCoverRate )
    {
        this.lineCoverRate = lineCoverRate;
    }
    /**
     * 
     * @return
     */
    public double getBranchCoverRate()
    {
        return branchCoverRate;
    }
    /**
     * 
     * @param branchCoverRate
     */
    public void setBranchCoverRate( double branchCoverRate )
    {
        this.branchCoverRate = branchCoverRate;
    }
    /**
     * 
     * @return
     */
    public int getNumberOfCoveredBranches()
    {
        return numberOfCoveredBranches;
    }
    /**
     * 
     * @param numberOfCoveredBranches
     */
    public void setNumberOfCoveredBranches( int numberOfCoveredBranches )
    {
        this.numberOfCoveredBranches = numberOfCoveredBranches;
    }
    /**
     * 
     * @return
     */
    public int getNumberOfCoveredLines()
    {
        return numberOfCoveredLines;
    }
    /**
     * 
     * @param numberOfCoveredLines
     */
    public void setNumberOfCoveredLines( int numberOfCoveredLines )
    {
        this.numberOfCoveredLines = numberOfCoveredLines;
    }
    /**
     * 
     * @return
     */
    public int getNumberOfValidBranches()
    {
        return numberOfValidBranches;
    }
    /**
     * 
     * @param numberOfValidBranches
     */
    public void setNumberOfValidBranches( int numberOfValidBranches )
    {
        this.numberOfValidBranches = numberOfValidBranches;
    }
    /**
     * 
     * @return
     */
    public int getNumberOfValidLines()
    {
        return numberOfValidLines;
    }
    /**
     * 
     * @param numberOfValidLines
     */
    public void setNumberOfValidLines( int numberOfValidLines )
    {
        this.numberOfValidLines = numberOfValidLines;
    }
    /**
     * 
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {
        if ( dashboardReport != null && dashboardReport instanceof CoberturaReportBean )
        {
            this.nbClasses = this.nbClasses + ( (CoberturaReportBean) dashboardReport ).getNbClasses();
            this.numberOfCoveredBranches =
                this.numberOfCoveredBranches + ( (CoberturaReportBean) dashboardReport ).getNumberOfCoveredBranches();
            this.numberOfCoveredLines =
                this.numberOfCoveredLines + ( (CoberturaReportBean) dashboardReport ).getNumberOfCoveredLines();
            this.numberOfValidBranches =
                this.numberOfValidBranches + ( (CoberturaReportBean) dashboardReport ).getNumberOfValidBranches();
            this.numberOfValidLines =
                this.numberOfValidLines + ( (CoberturaReportBean) dashboardReport ).getNumberOfValidLines();
            if ( numberOfValidBranches == 0 )
            {
                // no branches, therefore 100% branch coverage.
                branchCoverRate = 1d;
            }
            else
            {
                branchCoverRate = (double) numberOfCoveredBranches / numberOfValidBranches;
            }
            //MOJO-662 correction
            if ( numberOfValidLines == 0 )
            {
                // no branches, therefore 100% branch coverage.
                lineCoverRate = 1d;
            }
            else
            {
                lineCoverRate = (double) numberOfCoveredLines / numberOfValidLines;
            }
        }
    }
}
