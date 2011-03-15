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

import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class PeriodUtils
{
    private static final int PATTERN_MINUS_SUBSTR_LENGTH = 5;

    private static final int PATTERN_PLUS_SUBSTR_LENGTH = 4;

    private static final int DELTA_NEXT_WEEK = 7;

    private static final int DELTA_PREVIOUS_WEEK = -DELTA_NEXT_WEEK;

    private static final int MILLISECOND_END_OF_DAY = 999;

    private static final int SECOND_END_OF_DAY = 59;

    private static final int MINUTE_END_OF_DAY = 59;

    private static final int HOUR_END_OF_DAY = 23;

    public static final String NOW = "Now";

    public static final String STARTOF_TODAY = "Startof_today";

    public static final String ENDOF_TODAY = "Endof_today";

    public static final String STARTOF_TOMORROW = "Startof_tomorrow";

    public static final String ENDOF_TOMORROW = "Endof_tomorrow";

    public static final String STARTOF_YESTERDAY = "Startof_yesterday";

    public static final String ENDOF_YESTERDAY = "Endof_yesterday";

    public static final String STARTOF_THISWEEK = "Startof_thisweek";

    public static final String ENDOF_THISWEEK = "Endof_thisweek";

    public static final String STARTOF_NEXTWEEK = "Startof_nextweek";

    public static final String ENDOF_NEXTWEEK = "Endof_nextweek";

    public static final String STARTOF_LASTWEEK = "Startof_lastweek";

    public static final String ENDOF_LASTWEEK = "Endof_lastweek";

    public static final String STARTOF_THISMONTH = "Startof_thismonth";

    public static final String ENDOF_THISMONTH = "Endof_thismonth";

    public static final String STARTOF_LASTMONTH = "Startof_lastmonth";

    public static final String ENDOF_LASTMONTH = "Endof_lastmonth";

    public static final String STARTOF_NEXTMONTH = "Startof_nextmonth";

    public static final String ENDOF_NEXTMONTH = "Endof_nextmonth";

    public static final String STARTOF_THISYEAR = "Startof_thisyear";

    public static final String ENDOF_THISYEAR = "Endof_thisyear";

    public static final String STARTOF_LASTYEAR = "Startof_lastyear";

    public static final String ENDOF_LASTYEAR = "Endof_lastyear";

    public static final String STARTOF_NEXTYEAR = "Startof_nextyear";

    public static final String ENDOF_NEXTYEAR = "Endof_nextyear";

    /**
     * Plus[0-9]{1,3}
     */
    public static final String PATTERN_PLUS = "Plus";

    /**
     * Minus[0-9]{1,3}
     */
    public static final String PATTERN_MINUS = "Minus";
    /**
     *
     * @param pattern
     * @return
     */
    public static Date getDateFromPattern( String pattern )
    {
        return getDateFromPattern( pattern, new Date( System.currentTimeMillis() ) );
    }
    /**
     *
     * @param pattern
     * @param currentDate
     * @return
     */
    public static Date getDateFromPattern( String pattern, Date currentDate )
    {

        Date returnDate = null;
        if ( pattern != null && pattern.length() > 0 )
        {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek( Calendar.MONDAY );
            cal.setTime( currentDate );


            if ( NOW.equals( pattern ) )
            {
                returnDate = currentDate;
            }
            else if ( STARTOF_TODAY.equals( pattern ) )
            {
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_TODAY.equals( pattern ) )
            {
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_TOMORROW.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_TOMORROW.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_YESTERDAY.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, -1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_YESTERDAY.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, -1 );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_THISWEEK.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_THISWEEK.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
                cal.add( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_NEXTWEEK.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, DELTA_NEXT_WEEK );
                cal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_NEXTWEEK.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, DELTA_NEXT_WEEK );
                cal.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
                cal.add( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_LASTWEEK.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, DELTA_PREVIOUS_WEEK );
                cal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_LASTWEEK.equals( pattern ) )
            {
                cal.add( Calendar.DAY_OF_MONTH, DELTA_PREVIOUS_WEEK );
                cal.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
                cal.add( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_THISMONTH.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_THISMONTH.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_LASTMONTH.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.add( Calendar.MONTH, -1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_LASTMONTH.equals( pattern ) )
            {
                cal.add( Calendar.MONTH, -1 );
                cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_NEXTMONTH.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.add( Calendar.MONTH, 1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_NEXTMONTH.equals( pattern ) )
            {
                cal.add( Calendar.MONTH, 1 );
                cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_THISYEAR.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.MONTH, Calendar.JANUARY );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_THISYEAR.equals( pattern ) )
            {
                cal.set( Calendar.MONTH, Calendar.DECEMBER );
                cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_LASTYEAR.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.MONTH, Calendar.JANUARY );
                cal.add( Calendar.YEAR, -1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_LASTYEAR.equals( pattern ) )
            {
                cal.set( Calendar.MONTH, Calendar.DECEMBER );
                cal.add( Calendar.YEAR, -1 );
                cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( STARTOF_NEXTYEAR.equals( pattern ) )
            {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.set( Calendar.MONTH, Calendar.JANUARY );
                cal.add( Calendar.YEAR, 1 );
                cal.set( Calendar.HOUR_OF_DAY, 0 );
                cal.set( Calendar.MINUTE, 0 );
                cal.set( Calendar.SECOND, 0 );
                cal.set( Calendar.MILLISECOND, 0 );
                returnDate = cal.getTime();
            }
            else if ( ENDOF_NEXTYEAR.equals( pattern ) )
            {
                cal.set( Calendar.MONTH, Calendar.DECEMBER );
                cal.add( Calendar.YEAR, 1 );
                cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
                cal.set( Calendar.HOUR_OF_DAY, HOUR_END_OF_DAY );
                cal.set( Calendar.MINUTE, MINUTE_END_OF_DAY );
                cal.set( Calendar.SECOND, SECOND_END_OF_DAY );
                cal.set( Calendar.MILLISECOND, MILLISECOND_END_OF_DAY );
                returnDate = cal.getTime();
            }
            else if ( pattern.startsWith( PATTERN_PLUS ) )
            {
                String strDelta = pattern.substring( PATTERN_PLUS_SUBSTR_LENGTH, pattern.length() );
                int delta = Integer.parseInt( strDelta );
                cal.add( Calendar.DAY_OF_MONTH, delta );
                returnDate = cal.getTime();
            }
            else if ( pattern.startsWith( PATTERN_MINUS ) )
            {
                String strDelta = pattern.substring( PATTERN_MINUS_SUBSTR_LENGTH, pattern.length() );
                int delta = Integer.parseInt( strDelta );
                cal.add( Calendar.DAY_OF_MONTH, -( delta ) );
                returnDate = cal.getTime();
            }
        }
        return returnDate;

    }
}
