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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class Configuration
{
    private String version;

    private List sections = new ArrayList();
    
    private Map map = new Hashtable();

    public Configuration()
    {

    }

    public String getVersion()
    {
        return version;
    }

    public List getSections()
    {
        return sections;
    }

    public void setSections( List sections )
    {
        this.sections = sections;
    }
    
    public Section getSectionById( String artifactId)
    {
        if( map == null || map.isEmpty() )
        {
            map = new Hashtable();
            
            Iterator iter = this.sections.iterator();

            while ( iter.hasNext() )
            {
                Section section = (Section) iter.next();
                map.put( section.getId(), section );
            }
        }
        return (Section) map.get( artifactId );
    }

}
