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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A grouping collection of samples (grouped by the name of the samples).
 * 
 * @author ksr@lakeside.dk
 */
public final class GroupedResponsetimeSamples extends ResponsetimeSamples {
    private static final long serialVersionUID = 5054656881107118329L;

    private final Map sampleGroupsByName = new LinkedHashMap();

    /**
     * Add a sample (and group it).
     * 
     * @see ResponsetimeSamples#add(ResponsetimeSample,String)
     */
    public void add(ResponsetimeSample sample, String sampleName) {
        super.add(sample, sampleName);
        ResponsetimeSamples sampleGroup = getSampleGroup(sampleName);
        if(sampleGroup == null) {
            sampleGroup = new ResponsetimeSampleGroup(sampleName);
            sampleGroupsByName.put(sampleName, sampleGroup);
        }
        sampleGroup.add(sample, sampleName);
    }

    private ResponsetimeSamples getSampleGroup(String sampleName) {
        return (ResponsetimeSamples)sampleGroupsByName.get(sampleName);
    }

    /**
     * @return a list of {@link ResponsetimeSampleGroup}
     */
    public List getSampleGroups() {
        return new ArrayList(sampleGroupsByName.values());
    }

}
