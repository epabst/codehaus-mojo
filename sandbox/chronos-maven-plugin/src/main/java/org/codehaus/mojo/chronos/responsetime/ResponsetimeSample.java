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

/**
 * Contains info from a jmeter logentry.
 * 
 * @author ksr@lakeside.dk
 */
public interface ResponsetimeSample extends Serializable {
    /**
     * @return the responsetime
     */
    int getResponsetime();

    /**
     * Returns the timestamp of this sample, as defined by * {@link System#currentTimeMillis()}.
     * 
     * @return the timestamp of this sample.
     */
    long getTimestamp();

    /**
     * Was the invocation successful or did it fail?
     * 
     * @return whether the invocation succeeded or not.
     */
    boolean isSuccess();

    /**
     * What is the threadgroup id of this sample? This is derived by the threadname.
     * 
     * @return the threadgroupid of this samples
     */
    String getThreadId();
}