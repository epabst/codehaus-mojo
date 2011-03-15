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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.codehaus.mojo.dashboard.report.plugin.beans.comparator.DescAfferentCouplingPackageComparator;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class JDependReportBean extends AbstractReportBean
{
    /**
     * 
     */
    private double averageAfferentCoupling = 0;

    /**
     * 
     */
    private int nbPackages = 0;

    /**
     * 
     */
    private int sumAC = 0;
    
    /**
     * 
     */
    private List packages = new ArrayList();
    /**
     * 
     *
     */
    public JDependReportBean()
    {
    }
    /**
     * 
     * @param dateGeneration
     */
    public JDependReportBean( Date dateGeneration )
    {
        super( dateGeneration );
    }

    public int getNbPackages()
    {
        return nbPackages;
    }

    public void setNbPackages( int nbPackages )
    {
        this.nbPackages = nbPackages;
    }

    public int getSumAC()
    {
        return sumAC;
    }

    public void setSumAC( int sumAC )
    {
        this.sumAC = sumAC;
    }

    public double getAverageAfferentCoupling()
    {
        return averageAfferentCoupling;
    }

    public void setAverageAfferentCoupling( double averageAfferentCoupling )
    {
        this.averageAfferentCoupling = averageAfferentCoupling;
    }

    /**
     * 
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {
        if ( dashboardReport != null && dashboardReport instanceof JDependReportBean )
        {
            this.sumAC = this.sumAC + ( (JDependReportBean) dashboardReport ).getSumAC();
            this.nbPackages = this.nbPackages + ( (JDependReportBean) dashboardReport ).getNbPackages();
            if ( this.nbPackages == 0 )
            {
                // no packages, therefore 0.
                this.averageAfferentCoupling = 0d;
            }
            else
            {
                this.averageAfferentCoupling = (double) this.sumAC / this.nbPackages;
            }
            this.packages.addAll( ( (JDependReportBean) dashboardReport ).getPackages() );
            // Collections.sort( this.packages, new DescAfferentCouplingPackageComparator() );
        }
    }

    public List getPackages()
    {
        Collections.sort( this.packages, new DescAfferentCouplingPackageComparator() );
        return packages;
    }

    public void setPackages( List packages )
    {
        this.packages = packages;
    }
    
    /**
     * 
     * @param pack
     */
    public void addPackage( XRefPackageBean pack )
    {
        this.packages.add( pack );
        this.sumAC = this.sumAC + pack.getAfferentCoupling().intValue();
        this.nbPackages = this.nbPackages + 1;
        if ( this.nbPackages == 0 )
        {
            // no packages, therefore 0.
            this.averageAfferentCoupling = 0d;
        }
        else
        {
            this.averageAfferentCoupling = (double) this.sumAC / this.nbPackages;
        }
    }

}
