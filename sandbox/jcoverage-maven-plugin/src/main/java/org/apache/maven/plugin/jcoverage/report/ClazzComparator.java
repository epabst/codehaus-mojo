package org.apache.maven.plugin.jcoverage.report;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

import java.util.Comparator;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class ClazzComparator implements Comparator
{
    public int compare(Object class1, Object class2)
    {
        if (class1 instanceof Clazz && class2 instanceof Clazz)
        {
            Clazz c1 = (Clazz) class1;
            Clazz c2 = (Clazz) class2;
            String lower1 = c1.getName().toLowerCase();
            String lower2 = c2.getName().toLowerCase();
            return lower1.compareTo(lower2);
        }
        else
        {
            return -1;
        }
    }
}
