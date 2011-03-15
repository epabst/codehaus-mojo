package org.codehaus.mojo.dashboard.report.plugin.configuration;

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

import java.util.Date;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class Graph
{
    private String id;

    private String title;
    
    private String timeUnit;

    private String startPeriod;

    private String endPeriod;

    private Date startPeriodDate;

    private Date endPeriodDate;

    private String warningMsg;

    public String getEndPeriod()
    {
        return endPeriod;
    }

    public String getId()
    {
        return id;
    }

    public String getStartPeriod()
    {
        return startPeriod;
    }

    public String getTitle()
    {
        return title;
    }

    public Date getEndPeriodDate()
    {
        if ( endPeriodDate == null )
        {
            endPeriodDate = PeriodUtils.getDateFromPattern( endPeriod );
        }
        return endPeriodDate;
    }

    public Date getStartPeriodDate()
    {
        if ( startPeriodDate == null )
        {
            startPeriodDate = PeriodUtils.getDateFromPattern( startPeriod );
        }
        return startPeriodDate;
    }

    public boolean isPeriodsValid()
    {
        boolean valid = false;
        getStartPeriodDate();
        getEndPeriodDate();
        if ( startPeriodDate != null && endPeriodDate != null && startPeriodDate.before( endPeriodDate ) )
        {
            valid = true;
        }
        else
        {
            StringBuffer buff = new StringBuffer();
            buff.append( "The graph Configuration (id = " );
            buff.append( this.id );
            buff.append( ") is wrong. startPeriod (" );
            buff.append( getStartPeriod() + " = " + startPeriodDate );
            buff.append( ") is not before the endPeriod (" );
            buff.append( getEndPeriod() + " = " + endPeriodDate );
            buff.append( ")." );
            warningMsg = buff.toString();
        }
        return valid;
    }

    public String getWarningMessage()
    {
        return warningMsg;
    }

    public String getTimeUnit()
    {
        return timeUnit;
    }

    public void setTimeUnit( String timeUnit )
    {
        this.timeUnit = timeUnit;
    }

}
