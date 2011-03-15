package org.codehaus.mojo.dashboard.report.plugin.configuration;

/*
 * Copyright 2007 David Vicente
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class Section
{
    private String id;
    
    private String title;
    
    private String groupId;
    
    private String artifactId;
    
    private List historicgraphs = new ArrayList();
    
    private List warningsMsg = new ArrayList();
    
    public String getArtifactId()
    {
        return artifactId;
    }
   
   public List getGraphs()
   {
       return historicgraphs;
   }

    public String getGroupId()
    {
        return groupId;
    }

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }
    
    public boolean isValidGraphs()
    {
        boolean valid = true;
        Iterator iter = historicgraphs.iterator();
        while ( iter.hasNext() )
        {
            Graph graph = (Graph) iter.next();
            if ( !graph.isPeriodsValid() )
            {
                valid = false;
                if ( warningsMsg == null )
                {
                    warningsMsg = new ArrayList();
                }
                warningsMsg.add( graph.getWarningMessage() );
                break;
            }
        }
        return valid;
    }
    /**
     * 
     * @return
     */
    public List getWarningMessages()
    {
        return warningsMsg;
    }

}
