package org.codehaus.mojo.chronos;

public class ReportConfigStub implements ReportConfig {

    public int getAverageduration() {
        return 20000;
    }

    public String getDescription() {
        return "description";
    }

    public String getId() {
        return "id";
    }

    public int getResponsetimedivider() {
        return 10;
    }

    public long getThreadcountduration() {
        return 20000;
    }

    public String getTitle() {
        return "Title";
    }

    public boolean isShowaverage() {
        return true;
    }

    public boolean isShowdetails() {
        return true;
    }

    public boolean isShowgc() {
        return true;
    }

    public boolean isShowhistogram() {
        return true;
    }

    public boolean isShowinfotable() {
        return true;
    }

    public boolean isShowpercentile() {
        return true;
    }

    public boolean isShowresponse() {
        return true;
    }

    public boolean isShowsummary() {
        return true;
    }

    public boolean isShowthroughput() {
        return true;
    }

    public boolean isShowtimeinfo() {
        return true;
    }

    public boolean isShowsummarycharts() {
        return true;
    }

    /* Merged from Atlassion */
    public double getHistoryChartUpperBound() {
        return 30000;
    }
}