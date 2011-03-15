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
package org.codehaus.mojo.chronos.jmeter;

import java.util.Properties;

import org.codehaus.mojo.chronos.responsetime.ResponsetimeSample;

/**
 * This class represents a JMeter log entry defined in jtl2.1 format (or later).
 * 
 * @author ksr@lakeside.dk
 */
public class Jtl21Sample implements ResponsetimeSample {
    private static final long serialVersionUID = -3776902838855740550L;

    private final int responsetime;

    private final long timestamp;

    private final boolean success;

    private final String threadId;

    /**
     * @param attributes
     *            the attributes of t he sample element
     */
    public Jtl21Sample(Properties attributes) {
        responsetime = Integer.parseInt(attributes.getProperty("t"));
        timestamp = Long.parseLong(attributes.getProperty("ts"));
        success = "true".equals(attributes.getProperty("s"));
        threadId = attributes.getProperty("tn").intern();

    }

    /**
     * @return Return the name of this sample
     */
    public static String getSampleName(Properties attributes) {
        return attributes.getProperty("lb");
    }

    /**
     * @see ResponsetimeSample#getResponsetime()
     * @return Returns the responsetime.
     */
    public final int getResponsetime() {
        return responsetime;
    }

    /**
     * @see ResponsetimeSample#getTimestamp()
     * @return Returns the timestamp.
     */
    public final long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the success.
     * @see ResponsetimeSample#isSuccess()
     */
    public final boolean isSuccess() {
        return success;
    }

    /**
     * @return Returns the threadgroupId.
     * @see ResponsetimeSample#getThreadId()
     */
    public final String getThreadId() {
        return threadId;
    }

}
