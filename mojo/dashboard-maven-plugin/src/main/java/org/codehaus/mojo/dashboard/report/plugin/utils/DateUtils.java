package org.codehaus.mojo.dashboard.report.plugin.utils;

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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class DateUtils
{

    /**
     * Creation forbidden...
     */
    private DateUtils()
    {
        super();
    }

    /**
     * return all dates between 2 dates defined by time Period
     * 
     * @param startDate
     * @param endDate
     * @param timePeriod
     * @return
     *          the Date array
     */
    public static Date[] getAllDates( Date startDate, Date endDate, TimePeriod timePeriod )
    {
        Vector dateList = new Vector();
        Calendar cal = Calendar.getInstance( Locale.getDefault() );

        cal.setTime( startDate );

        Date tmpDate = startDate;

        while ( tmpDate.before( endDate ) )
        {
            dateList.add( tmpDate );

            if ( timePeriod.equals( TimePeriod.MINUTE ) )
            {
                cal.add( Calendar.MINUTE, 1 );
            }
            else if ( timePeriod.equals( TimePeriod.HOUR ) )
            {
                cal.add( Calendar.HOUR_OF_DAY, 1 );
            }
            else if ( timePeriod.equals( TimePeriod.DAY ) )
            {
                cal.add( Calendar.DATE, 1 );
            }
            else if ( timePeriod.equals( TimePeriod.WEEK ) )
            {
                cal.add( Calendar.DATE, 7 );
            }
            else if ( timePeriod.equals( TimePeriod.MONTH ) )
            {
                cal.add( Calendar.MONTH, 1 );
            }
            else
            {
                cal.add( Calendar.DATE, 1 );
            }
            tmpDate = cal.getTime();
        }

        dateList.add( endDate );

        return (Date[]) dateList.toArray( new Date[dateList.size()] );
    }
}
