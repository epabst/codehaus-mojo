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
public class PackageComparator implements Comparator
{
    public int compare(Object package1, Object package2)
    {
        if (package1 instanceof Package && package2 instanceof Package)
        {
            Package p1 = (Package) package1;
            Package p2 = (Package) package2;
            if (p1.getName() != null && p2.getName() != null)
            {
                String lower1 = p1.getName().toLowerCase();
                String lower2 = p2.getName().toLowerCase();
                return lower1.compareTo(lower2);
            }
            else
            {
                if (p1.getName() == null && p2.getName() == null)
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
        }
        else
        {
            return -1;
        }
    }
}
