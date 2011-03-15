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

/**
 * @author ksr@lakeside.dk
 */
public class ResponsetimeSampleGroup extends ResponsetimeSamples {
    private static final long serialVersionUID = -5442330396281337888L;

    private static int indexes = 0;

    private final String name;
    private final int index;

    public ResponsetimeSampleGroup(String name) {
        this.name = name;
        this.index = ++indexes;
    }

    public final int getIndex() {
        return index;
    }

    /**
     * @return the name of this samplegroup
     */
    public final String getName() {
        return name;
    }
}
