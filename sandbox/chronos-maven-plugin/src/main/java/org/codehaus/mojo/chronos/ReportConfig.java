/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos;

/**
 * Represents a readonly view of the configuration.<br/>
 * Makes it more obvious that the reporting will not change the configuration
 * 
 * @author ksr@lakeside.dk
 */
public interface ReportConfig {
    /**
     * @return the id of this report (default is
     */
    String getId();

    /**
     * @return the title of this report
     */
    String getTitle();

    /**
     * @return the description of the report
     */
    String getDescription();

    /**
     * @return whether a summary of the results should be shown.
     */
    boolean isShowsummary();

    /**
     * @return whether a summary of the results should be shown.
     */
    boolean isShowsummarycharts();

    /**
     * @return Should a report of each individual test be shown?
     */
    boolean isShowdetails();

    /**
     * @return Will the report contain an information table for the tests (both the summary and the individual tests)?
     */
    boolean isShowinfotable();

    /**
     * @return Will the information table contain timeinfo?
     */
    boolean isShowtimeinfo();

    /**
     * @return Will the report contain graphs of response times?
     */
    boolean isShowresponse();

    /**
     * @return Will the report contain a histogram
     */
    boolean isShowhistogram();

    /**
     * @return Will the report contain throughput information (graphically
     */
    boolean isShowthroughput();

    /**
     * @return Will the report contain garbage collection statistics
     */
    boolean isShowgc();

    /**
     * @return the duration of the threadcount
     */
    long getThreadcountduration();

    /**
     * @return the average duration
     */
    int getAverageduration();

    /**
     * responsetimeDivider is often used when the reponse time of a single request is so low that the granularity of the
     * system timer corrupts the response time measured.
     * 
     * @return responsetimeDivider - divides response time. Useful when one sample is the response time for multiple
     *         requests.
     */
    int getResponsetimedivider();

    /**
     * @return whether the charts of response times and histograms will contain 95% percentiles
     */
    boolean isShowpercentile();

    /**
     * @return whether the charts of response times and histograms will contain average times
     */
    boolean isShowaverage();

    /**
     * @return Set the maximum upper bound for a chart in the historyreport goal
     */
    /* Merged from Atlassion */
    public double getHistoryChartUpperBound();
}