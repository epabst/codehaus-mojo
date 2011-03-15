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
package org.codehaus.mojo.chronos.gc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.mojo.chronos.Utils;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

/**
 * Container for {@link GCSample}.
 * 
 * @author ksr@lakeside.dk
 */
public class GCSamples implements Serializable {
    private static final long serialVersionUID = -4311672413828550701L;

    private final List samples = new ArrayList();

    /**
     * adds a {@link GCSample} to the list.
     * 
     * @param sample
     *            a {@link GCSample}
     */
    public final void add(GCSample sample) {
        samples.add(sample);
    }

    /**
     * @return the number of samples
     */
    public final int getSampleCount() {
        return samples.size();
    }

    public final double getTimeStampForSampleAt(int index) {
        return ((GCSample)samples.get(index)).getTimestamp();
    }

    public final void extractHeapBefore(TimeSeries heapBeforeSeries) {
        for (Iterator it = samples.iterator(); it.hasNext();) {
            GCSample sample = (GCSample)it.next();
            heapBeforeSeries.addOrUpdate(getTimestamp(sample), sample.getHeapBefore());
        }
    }

    public final void extractHeapAfter(TimeSeries heapAfterSeries) {
        for (Iterator it = samples.iterator(); it.hasNext();) {
            GCSample sample = (GCSample)it.next();
            heapAfterSeries.addOrUpdate(getTimestamp(sample), sample.getHeapAfter());
        }
    }

    public final void extractHeapTotal(TimeSeries heapTotalSeries) {
        for (Iterator it = samples.iterator(); it.hasNext();) {
            GCSample sample = (GCSample)it.next();
            heapTotalSeries.addOrUpdate(getTimestamp(sample), sample.getHeapTotal());
        }
    }

    public final void extractProcessingTime(TimeSeries series) {
        for (Iterator it = samples.iterator(); it.hasNext();) {
            GCSample sample = (GCSample)it.next();
            series.addOrUpdate(getTimestamp(sample), sample.getProcessingTime());
        }
    }

    public final double getGarbageCollectionRatio(long totalTime) {
        double totalProcessing = 0.0d;
        for (Iterator it = samples.iterator(); it.hasNext();) {
            GCSample sample = (GCSample)it.next();
            totalProcessing += sample.getProcessingTime();
        }
        return totalProcessing / totalTime;
    }

    public final double getCollectedKBPerSecond(long totalTime) {
        double totalCollected = 0.0d;
        for (Iterator it = samples.iterator(); it.hasNext();) {
            GCSample sample = (GCSample)it.next();
            totalCollected += (sample.getHeapBefore() - sample.getHeapAfter());
        }
        return (totalCollected / 1000) / totalTime;
    }

    private Millisecond getTimestamp(GCSample sample) {
        int milliseconds = (int)(sample.getTimestamp() * 1000);
        return Utils.createMS(milliseconds);
    }
}
