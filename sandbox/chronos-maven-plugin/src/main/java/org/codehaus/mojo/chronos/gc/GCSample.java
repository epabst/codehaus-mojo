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

/**
 * Contains info from a garbagecollection logentry.
 * 
 * @author ksr@lakeside.dk
 */
public class GCSample implements Serializable {
    private static final long serialVersionUID = -7053949352608279873L;

    private final double timestamp;

    private final int heapBefore;

    private final int heapAfter;

    private final int heapTotal;

    private final double processingTime;

    public GCSample(double timestamp, int heapBefore, int heapAfter, int heapTotal, double processingTime) {
        this.timestamp = timestamp;
        this.heapAfter = heapAfter;
        this.heapBefore = heapBefore;
        this.heapTotal = heapTotal;
        this.processingTime = processingTime;
    }

    /**
     * @return Returns the timestamp.
     */
    public final double getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the heapBefore.
     */
    public final int getHeapBefore() {
        return heapBefore;
    }

    /**
     * @return Returns the heapAfter.
     */
    public final int getHeapAfter() {
        return heapAfter;
    }

    /**
     * @return Returns the heapTotal.
     */
    public final int getHeapTotal() {
        return heapTotal;
    }

    /**
     * @return Returns the processingTime.
     */
    public final double getProcessingTime() {
        return processingTime;
    }
}