package org.codehaus.mojo.dashboard.report.plugin.beans.comparator;

/*
 * Copyright 2008 David Vicente
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

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleError;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class DescNbErrorCheckstyleComparator implements Comparator
{
    /**
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare( Object checkError1, Object checkError2 )
    {
        CheckstyleError pkA = (CheckstyleError) checkError1;
        CheckstyleError pkB = (CheckstyleError) checkError2;

        int ret = new Integer( pkA.getNbIteration() ).compareTo( new Integer( pkB.getNbIteration() ) );

        if ( ret == 0 )
        {
            ret = pkA.getMessage().compareTo( pkB.getMessage() );
        }
        else
        {
            ret = ret * ( -1 );
        }

        return ret;
    }

}
