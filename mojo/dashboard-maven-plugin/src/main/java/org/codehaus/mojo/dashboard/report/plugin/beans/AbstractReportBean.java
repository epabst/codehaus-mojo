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
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public abstract class AbstractReportBean implements IDashBoardReportBean, Cloneable
{

    /**
     *
     */
    private Date dateGeneration;

    private long id;

    private DashBoardMavenProject mavenProject;
    /**
     *
     *
     */
    public AbstractReportBean()
    {
    }

    public AbstractReportBean( Date dateGeneration )
    {
        this.dateGeneration = dateGeneration;
    }
    /* (non-Javadoc)
     * @see org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean#getDateGeneration()
     */
    public Date getDateGeneration()
    {
        return dateGeneration;
    }
    /* (non-Javadoc)
     * @see org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean#setDateGeneration(java.util.Date)
     */
    public void setDateGeneration( Date dateGeneration )
    {
        this.dateGeneration = dateGeneration;
    }
    /**
     *
     */
    protected Object clone()
    {
        Object clone = null;
        try
        {
            clone = super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            System.err.println( "AbstractReportBean can't clone" );
        }
        return clone;
    }
    public long getId()
    {
        return id;
    }
    public void setId( long id )
    {
        this.id = id;
    }

    public DashBoardMavenProject getMavenProject()
    {
        return mavenProject;
    }

    public void setMavenProject( DashBoardMavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }
    protected double getPercentageValue( int numerator, int denominator )
    {
        double percentage = 0.0d;
        if(denominator >0)
        {
            percentage = numerator / (double) denominator;
        }
        return percentage;
    }

}
