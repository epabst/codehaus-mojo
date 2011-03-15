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
package org.codehaus.mojo.chronos.history;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;

/**
 * This is a (serializable) historic ample representing the statistics from a previous run.
 * 
 * @author ksr@lakeside.dk
 */
public class HistoricSample implements Serializable {
    private static final int DEFAULT_DURATION = 20000;

    private static final long serialVersionUID = 8492792243093456318L;

    private long timestamp;
    private double gcRatio = -1d;
    private double collectedPrSecond = -1d;

    private double responsetimeAverage = -1d;
    private double responsetime95Percentile = -1d;
    private HashMap individualPercentiles;
    private HashMap individualAverages;

    private double maxAverageThroughput = -1d;

    public HistoricSample(GroupedResponsetimeSamples responseSamples, GCSamples gcSamples) {
        timestamp = responseSamples.getFirstTimestamp();
        responsetimeAverage = responseSamples.getAverage(1);
        responsetime95Percentile = responseSamples.getPercentile95(1);
        individualAverages = new HashMap();
        individualPercentiles = new HashMap();
        Iterator it = responseSamples.getSampleGroups().iterator();
        while (it.hasNext()) {
            ResponsetimeSampleGroup group = (ResponsetimeSampleGroup)it.next();
            individualAverages.put(group.getName(), new Double(group.getAverage(1)));
            individualPercentiles.put(group.getName(), new Double(group.getPercentile95(1)));
        }
        if(gcSamples != null) {
            gcRatio = gcSamples.getGarbageCollectionRatio(responseSamples.getTotalTime());
            collectedPrSecond = gcSamples.getCollectedKBPerSecond(responseSamples.getTotalTime());
        }
        int averageDuration = Math.max(DEFAULT_DURATION, (int)responsetime95Percentile);
        maxAverageThroughput = responseSamples.getMaxAverageThroughput(averageDuration, 1);
    }

    /**
     * @return Returns the timestamp.
     */
    public final long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the gcRatio.
     */
    public final double getGcRatio() {
        return gcRatio;
    }

    /**
     * @return Returns the collectedPrSecond.
     */
    public final double getCollectedPrSecond() {
        return collectedPrSecond;
    }

    /**
     * @return Returns the responsetimeAverage.
     */
    public final double getResponsetimeAverage() {
        return responsetimeAverage;
    }

    /**
     * @return Returns the responsetime95Percrntile.
     */
    public final double getResponsetime95Percentile() {
        return responsetime95Percentile;
    }

    public final Set getGroupNames() {
        return individualAverages.keySet();
    }

    public final double getResponsetimeAverage(String groupName) {
        return ((Double)individualAverages.get(groupName)).doubleValue();
    }

    public final double getResponsetimePercentiles(String groupName) {
        return ((Double)individualPercentiles.get(groupName)).doubleValue();
    }

    /**
     * @return Returns the maxAverageThroughput.
     */
    public final double getMaxAverageThroughput() {
        return maxAverageThroughput;
    }

}
