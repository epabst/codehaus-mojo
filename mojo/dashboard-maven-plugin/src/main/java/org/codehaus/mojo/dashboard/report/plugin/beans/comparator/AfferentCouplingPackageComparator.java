package org.codehaus.mojo.dashboard.report.plugin.beans.comparator;

/*
 * Copyright 2006 David Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Comparator;

import org.codehaus.mojo.dashboard.report.plugin.beans.XRefPackageBean;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class AfferentCouplingPackageComparator implements Comparator
{
    /**
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare( Object package1, Object package2 )
    {
        XRefPackageBean pkA = (XRefPackageBean) package1;
        XRefPackageBean pkB = (XRefPackageBean) package2;

        int ret = pkA.getAfferentCoupling().compareTo( pkB.getAfferentCoupling() );

        if ( ret == 0 )
        {
            ret = pkA.getPackageName().compareTo( pkB.getPackageName() );
        }

        return ret;
    }

}
