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

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class XRefPackageBean
{
    /**
     * 
     */
    private Integer afferentCoupling = new Integer( 0 );
    /**
     * 
     */
    private String packageName = "";
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
     * @param projectName
     */
    public XRefPackageBean()
    {
    }
    /**
     * 
     * @return
     */
    public Integer getAfferentCoupling()
    {
        return afferentCoupling;
    }
    /**
     * 
     * @param afferentCoupling
     */
    public void setAfferentCoupling( Integer afferentCoupling )
    {
        this.afferentCoupling = afferentCoupling;
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
    public String getPackageName()
    {
        return packageName;
    }
    /**
     * 
     * @param packageName
     */
    public void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }
}
