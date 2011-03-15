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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Used to set the time period of the analysis.
 * 
 * @author Karim REFEYTON
 * @version 0.1
 */
public class TimePeriod implements Comparable
{

    /**
     * 1 minute time period.
     */
    public static final TimePeriod MINUTE = new TimePeriod( "MINUTE", new SimpleDateFormat( "yyyy-MM-dd HH:mm" ), 20 );

    /**
     * 1 hour time period.
     */
    public static final TimePeriod HOUR = new TimePeriod( "HOUR", new SimpleDateFormat( "yyyy-MM-dd HH:00" ), 30 );

    /**
     * 1 day time period.
     */
    public static final TimePeriod DAY = new TimePeriod( "DAY", new SimpleDateFormat( "yyyy-MM-dd" ), 40 );

    /**
     * 1 week time period.
     */
    public static final TimePeriod WEEK = new TimePeriod( "WEEK", new SimpleDateFormat( "yyyy-MM-dd" ), 47 )
    {
        /**
         * Set the day to the first day of week according to default localization.
         * 
         * @see net.logAnalyzer.analysis.TimePeriod#normalizeToString(java.util.Date)
         */
        public String normalizeToString( final Date date )
        {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime( date );
            calendar.set( GregorianCalendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() );
            String dateString = getDateFormat().format( calendar.getTime() );
            return dateString;
        }
    };

    /**
     * 1 month time period.
     */
    public static final TimePeriod MONTH = new TimePeriod( "MONTH", new SimpleDateFormat( "yyyy-MM" ), 50 );

    /**
     * Name of the period.
     */
    private String periodName;

    /**
     * Date format used to parse or format a date for the period.
     */
    private SimpleDateFormat dateFormat;

    /**
     * Used to compare two periods.
     */
    private Integer order;

    /**
     * Constructs a new period with the defined name, format and order.
     * 
     * @param period
     *            Name of the period (must be unique between all periods).
     * @param dateFormat
     *            Date format to parse or format dates from/to strings.
     * @param order
     *            Order value used to compare two periods.
     */
    private TimePeriod( String period, SimpleDateFormat dateFormat, int order )
    {
        this.periodName = period;
        this.dateFormat = dateFormat;
        this.order = new Integer( order );
    }

    /**
     * Return the date format.
     * 
     * @return Date format.
     */
    public SimpleDateFormat getDateFormat()
    {
        return this.dateFormat;
    }

    /**
     * Return the period name.
     * 
     * @return Period name.
     */
    public String getName()
    {
        return this.periodName;
    }

    /**
     * Normalize the specified date with current date format.
     * 
     * @param date
     *            Date to normalize
     * @return Normalized date.
     */
    public Date normalize( Date date )
    {
        Date dateNormalized = null;
        try
        {
            dateNormalized = this.dateFormat.parse( normalizeToString( date ) );
        }
        catch ( Exception e )
        {
            dateNormalized = date;
        }
        return dateNormalized;
    }

    /**
     * Format the specified date to String.
     * 
     * @param date
     *            Date to format
     * @return Formatted date.
     */
    public String normalizeToString( Date date )
    {
        return this.dateFormat.format( date );
    }

    /**
     * Return the period associated to the specified name.
     * 
     * @param name
     *            Period name
     * @return Period
     */
    public static TimePeriod getPeriod( String name )
    {
        if ( "MINUTE".equalsIgnoreCase( name ) )
        {
            return MINUTE;
        }
        else if ( "HOUR".equalsIgnoreCase( name ) )
        {
            return HOUR;
        }
        else if ( "DAY".equalsIgnoreCase( name ) )
        {
            return DAY;
        }
        else if ( "WEEK".equalsIgnoreCase( name ) )
        {
            return WEEK;
        }
        else if ( "MONTH".equalsIgnoreCase( name ) )
        {
            return MONTH;
        }
        else
        {
            return DAY;
        }
    }

    /**
     * Compare two periods. Return <tt>0</tt> if the current period is equal to the specified period; a value lower
     * than <tt>0</tt> if the period is lower than the specified period; a value greater than <tt>0</tt> if the
     * period is greater than the specified period.
     * 
     * @param anotherPeriod
     *            The period to be compared.
     * @return <tt>0</tt> if this equals the other period; <tt>&lt; 0</tt> if lower; <tt>&gt; 0</tt> if greater.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( Object anotherPeriod )
    {
        return this.order.compareTo( ( (TimePeriod) anotherPeriod ).order );
    }
}
