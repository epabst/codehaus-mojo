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
package org.codehaus.mojo.chronos.responsetime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.StatUtils;
import org.codehaus.mojo.chronos.Utils;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * Contains info from a jmeter jtl file.
 * 
 * @author ksr@lakeside.dk
 */
public abstract class ResponsetimeSamples implements Serializable {

    private static final int PERCENTILE = 95;

    private static final long serialVersionUID = 4056724466498233661L;

    protected final List samples = new ArrayList();

    private int succeeded;

    /**
     * add a (hopefully successful) sample.
     * 
     * @param sample
     *            a JMeterSample
     * @param sampleName
     */
    public void add(ResponsetimeSample sample, String sampleName) {
        samples.add(sample);
        if(sample.isSuccess()) {
            succeeded++;
        }
    }

    /**
     * @return the number of samples
     */
    public final int size() {
        return samples.size();
    }

    /**
     * @return the successrate (in percentage)
     */
    public final double getSuccessrate() {
        return 100 * ((double)getSucceeded()) / samples.size();
    }

    /**
     * @return the number of failed samples
     */
    public final int getFailed() {
        return samples.size() - getSucceeded();
    }

    /**
     * @return the number of succeeded samples
     */
    public final int getSucceeded() {
        return succeeded;
    }

    /**
     * @param responsetimedivider
     *            TODO
     * @return the average responsetime of all samples
     */
    public final double getAverage(int responsetimedivider) {
        return StatUtils.mean(extractResponsetimes(responsetimedivider));
    }

    /**
     * @param responsetimedivider
     *            TODO
     * @return the minimum responsetime of all samples
     */
    public final double getMin(int responsetimedivider) {
        return StatUtils.min(extractResponsetimes(responsetimedivider));
    }

    /**
     * @param responsetimedivider
     *            TODO
     * @return the maximum responsetime of all samples
     */
    public final double getMax(int responsetimedivider) {
        return StatUtils.max(extractResponsetimes(responsetimedivider));
    }

    /**
     * @param responsetimedivider
     *            TODO
     * @return the 95% fractile of all responsetimes
     */
    public final double getPercentile95(int responsetimedivider) {
        return StatUtils.percentile(extractResponsetimes(responsetimedivider), PERCENTILE);
    };

    /**
     * Note that the samples is ordered by timestamp, so the first one has the lowest timestamp.
     * 
     * @return the first timestamp of all samples
     */
    public final long getFirstTimestamp() {
        ResponsetimeSample responsetimeSample = (ResponsetimeSample)samples.get(0);
        return responsetimeSample.getTimestamp();
    }

    /**
     * Extracts all responsetimes.
     * 
     * @param responsetimedivider
     * @return the responsetimes as an array
     */
    public final double[] extractResponsetimes(int responsetimedivider) {
        double[] responsetimes = new double[samples.size()];
        int i = 0;
        for (Iterator it = samples.iterator(); it.hasNext();) {
            ResponsetimeSample sample = (ResponsetimeSample)it.next();
            responsetimes[i++] = sample.getResponsetime() / responsetimedivider;
        }
        return responsetimes;
    }

    public final void appendResponsetimes(TimeSeries series, int responsetimeDivider) {
        long delta = getFirstTimestamp();
        for (Iterator it = samples.iterator(); it.hasNext();) {
            ResponsetimeSample sample = (ResponsetimeSample)it.next();
            Millisecond timestamp = Utils.createMS(sample.getTimestamp() - delta);
            double responseTime = sample.getResponsetime() / responsetimeDivider;
            series.addOrUpdate(timestamp, responseTime);
        }
    }

    public final void appendThreadCounts(TimeSeries series, long threadCountDuration) {
        if(samples.size() > 0) {
            long firstSerial = getFirstTimestamp();
            Map activeThreads = new HashMap();
            for (Iterator it = samples.iterator(); it.hasNext();) {
                ResponsetimeSample sample = (ResponsetimeSample)it.next();
                int threadCount = activeThreads.size();
                for (Iterator it2 = activeThreads.keySet().iterator(); it2.hasNext();) {
                    String key = (String)it2.next();
                    if(sample.getTimestamp() > ((ResponsetimeSample)activeThreads.get(key)).getTimestamp()
                            + threadCountDuration) {
                        it2.remove();
                    }
                }
                activeThreads.put(sample.getThreadId(), sample);

                if(threadCount != activeThreads.size()) {
                    series.addOrUpdate(Utils.createMS(sample.getTimestamp() - firstSerial), threadCount);
                    series.addOrUpdate(Utils.createMS(sample.getTimestamp() - firstSerial + 1), activeThreads.size());
                }
            }
            ResponsetimeSample sample = (ResponsetimeSample)samples.get(samples.size() - 1);
            series.addOrUpdate(Utils.createMS(sample.getTimestamp() - firstSerial + 1), activeThreads.size());
        }
    }

    public final TimeSeries createMovingThroughput(String name, final int responsetimedivider) {
        TimeSeries series = new TimeSeries(name, Millisecond.class);
        if(samples.isEmpty()) {
            return series;
        }

        Collections.sort(samples, new Comparator() {
            public int compare(Object arg1, Object arg2) {
                ResponsetimeSample sample1 = (ResponsetimeSample)arg1;
                ResponsetimeSample sample2 = (ResponsetimeSample)arg2;
                long endtime1 = sample1.getTimestamp() + (long)sample1.getResponsetime() / responsetimedivider;
                long endtime2 = sample2.getTimestamp() + (long)sample2.getResponsetime() / responsetimedivider;
                return (int)(endtime1 - endtime2);
            }
        });

        int periodLength = 1000;
        long rampUpTime = 0;
        int measurements = 0;
        final long firstAllowedTimestamp = getFirstTimestamp() + rampUpTime;
        long periodStart = firstAllowedTimestamp;
        long periodEnd = periodStart + periodLength;
        for (int i = 0; i < samples.size(); i++) {
            ResponsetimeSample sample = (ResponsetimeSample)samples.get(i);
            long sampleEndTime = sample.getTimestamp() + sample.getResponsetime() / responsetimedivider;
            if(sampleEndTime < periodStart) {
                continue;
            }
            if(sampleEndTime <= periodEnd) {
                measurements++;
            } else {
                if(measurements > 0) {
                    series.addOrUpdate(Utils.createMS(periodEnd - firstAllowedTimestamp), measurements
                            * responsetimedivider);
                } else {
                    series.addOrUpdate(Utils.createMS(periodEnd - firstAllowedTimestamp), null);
                }
                measurements = 1;
                periodStart = periodEnd;
                periodEnd = periodStart + periodLength;
            }
        }
        return series;
    }

    /**
     * 
     * @param responsetimedivider
     *            TODO
     * @return the maximum of a moving average of the throughput
     */
    public final double getMaxAverageThroughput(int averageduration, int responsetimedivider) {
        TimeSeries series = createMovingThroughput("", responsetimedivider);
        TimeSeries averageseries = MovingAverage.createMovingAverage(series, "", averageduration, 0);
        double max = 0;
        for (Iterator it = averageseries.getItems().iterator(); it.hasNext();) {
            TimeSeriesDataItem item = (TimeSeriesDataItem)it.next();
            if(item.getValue() != null) {
                max = Math.max(max, item.getValue().doubleValue());
            }
        }
        return max;
    }

    public final long getTotalTime() {
        ResponsetimeSample first = (ResponsetimeSample)samples.get(0);
        ResponsetimeSample last = (ResponsetimeSample)samples.get(samples.size() - 1);
        return last.getTimestamp() + last.getResponsetime() - first.getTimestamp();
    }
}