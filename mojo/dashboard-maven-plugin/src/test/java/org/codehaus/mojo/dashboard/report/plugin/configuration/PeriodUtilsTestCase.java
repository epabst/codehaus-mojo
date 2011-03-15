package org.codehaus.mojo.dashboard.report.plugin.configuration;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Test Class of org.codehaus.mojo.dashboard.report.plugin.configuration.PeriodUtils
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class PeriodUtilsTestCase extends TestCase
{

    private String strDate = "27/04/2007 12:33:52.000";

    private SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss.SSS" );

    private Date current;

    /**
     * default constructor
     */
    public PeriodUtilsTestCase() throws Exception
    {
        super();
    }

    /**
     * default constructor
     * 
     * @param arg0
     */
    public PeriodUtilsTestCase( String arg0 ) throws Exception
    {
        super( arg0 );
    }

    /**
     * main
     * 
     * @param args
     *            no args
     */
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( PeriodUtilsTestCase.class );
    }

    public void testNOW() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.NOW, current );
        assertTrue( current.equals( dt ) );
    }

    public void testSTARTOF_TODAY() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_TODAY, current );
        Date testDT = formatter.parse( "27/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_TODAY : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_TODAY() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_TODAY, current );
        Date testDT = formatter.parse( "27/04/2007 23:59:59.999" );
        // System.out.println( "ENDOF_TODAY : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_TOMORROW() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_TOMORROW, current );
        Date testDT = formatter.parse( "28/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_TOMORROW : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_TOMORROW() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_TOMORROW, current );
        Date testDT = formatter.parse( "28/04/2007 23:59:59.999" );
        // System.out.println( "ENDOF_TOMORROW : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_YESTERDAY() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_YESTERDAY, current );
        Date testDT = formatter.parse( "26/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_YESTERDAY : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_YESTERDAY() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_YESTERDAY, current );
        Date testDT = formatter.parse( "26/04/2007 23:59:59.999" );
        // System.out.println( "ENDOF_YESTERDAY : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_THISWEEK() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_THISWEEK, current );
        Date testDT = formatter.parse( "23/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_THISWEEK : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_THISWEEK() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_THISWEEK, current );
        Date testDT = formatter.parse( "29/04/2007 23:59:59.999" );
        // System.out.println( "ENDOF_THISWEEK : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_NEXTWEEK() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_NEXTWEEK, current );
        Date testDT = formatter.parse( "30/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_NEXTWEEK : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_NEXTWEEK() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_NEXTWEEK, current );
        Date testDT = formatter.parse( "06/05/2007 23:59:59.999" );
        // System.out.println( "ENDOF_NEXTWEEK : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_LASTWEEK() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_LASTWEEK, current );
        Date testDT = formatter.parse( "16/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_LASTWEEK : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_LASTWEEK() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_LASTWEEK, current );
        Date testDT = formatter.parse( "22/04/2007 23:59:59.999" );
        // System.out.println( "ENDOF_LASTWEEK : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_THISMONTH() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_THISMONTH, current );
        Date testDT = formatter.parse( "01/04/2007 00:00:00.000" );
        // System.out.println( "STARTOF_THISMONTH : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_THISMONTH() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_THISMONTH, current );
        Date testDT = formatter.parse( "30/04/2007 23:59:59.999" );
        // System.out.println( "ENDOF_THISMONTH : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_LASTMONTH() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_LASTMONTH, current );
        Date testDT = formatter.parse( "01/03/2007 00:00:00.000" );
        // System.out.println( "STARTOF_LASTMONTH : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_LASTMONTH() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_LASTMONTH, current );
        Date testDT = formatter.parse( "31/03/2007 23:59:59.999" );
        // System.out.println( "ENDOF_LASTMONTH : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_NEXTMONTH() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_NEXTMONTH, current );
        Date testDT = formatter.parse( "01/05/2007 00:00:00.000" );
        // System.out.println( "STARTOF_NEXTMONTH : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_NEXTMONTH() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_NEXTMONTH, current );
        Date testDT = formatter.parse( "31/05/2007 23:59:59.999" );
        // System.out.println( "ENDOF_NEXTMONTH : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_THISYEAR() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_THISYEAR, current );
        Date testDT = formatter.parse( "01/01/2007 00:00:00.000" );
        // System.out.println( "STARTOF_THISYEAR : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_THISYEAR() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_THISYEAR, current );
        Date testDT = formatter.parse( "31/12/2007 23:59:59.999" );
        // System.out.println( "ENDOF_THISYEAR : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_LASTYEAR() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_LASTYEAR, current );
        Date testDT = formatter.parse( "01/01/2006 00:00:00.000" );
        // System.out.println( "STARTOF_LASTYEAR : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_LASTYEAR() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_LASTYEAR, current );
        Date testDT = formatter.parse( "31/12/2006 23:59:59.999" );
        // System.out.println( "ENDOF_LASTYEAR : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testSTARTOF_NEXTYEAR() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.STARTOF_NEXTYEAR, current );
        Date testDT = formatter.parse( "01/01/2008 00:00:00.000" );
        // System.out.println( "STARTOF_NEXTYEAR : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testENDOF_NEXTYEAR() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( PeriodUtils.ENDOF_NEXTYEAR, current );
        Date testDT = formatter.parse( "31/12/2008 23:59:59.999" );
        // System.out.println( "ENDOF_NEXTYEAR : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testPlus17() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( "Plus17", current );
        Date testDT = formatter.parse( "14/05/2007 12:33:52.000" );
        // System.out.println( "Plus17 : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    public void testMinus17() throws Exception
    {
        Date dt = PeriodUtils.getDateFromPattern( "Minus17", current );
        Date testDT = formatter.parse( "10/04/2007 12:33:52.000" );
        // //System.out.println( "Minus17 : " + dt + " / " + testDT );
        assertTrue( testDT.equals( dt ) );
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        current = formatter.parse( strDate );
    }
}
