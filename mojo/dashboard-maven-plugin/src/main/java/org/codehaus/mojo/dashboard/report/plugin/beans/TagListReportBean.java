package org.codehaus.mojo.dashboard.report.plugin.beans;

/*
 *  Copyright 2008 Henrik Lynggaard
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Henrik Lynggaard
 */
public class TagListReportBean extends AbstractReportBean
{
	/**
	 * 
	 */
    private Map tags = new HashMap();
    /**
     * 
     */
    private int nbTotal = 0;
    
    /**
     * 
     */
    private int nbClasses;
    
    /**
     * Construct a new CloverReportBean against the given project.
     */
    public TagListReportBean()
    {

    }

    /**
     * 
     * @param projectName
     */
    public TagListReportBean( Date dateGeneration )
    {
        super( dateGeneration );
    }

    public Map getTags()
    {
        return tags;
    }

    public void setTag( String name, Integer count )
    {
        tags.put( name, count );
        this.nbTotal = this.nbTotal + count.intValue();
    }
    
    /**
     * @param error
     */
    public void addAllEntries( Map entries )
    {
        if ( this.tags.isEmpty() )
        {
            this.tags.putAll( entries );
        }
        else
        {
            Iterator iter = entries.keySet().iterator();
            while ( iter.hasNext() )
            {
                String key = (String) iter.next();
                Integer newValuetoAdd = ( (Integer) ( entries.get( key ) ) );
                if ( ( this.tags.containsKey( key ) ) )
                {
                	Integer oldCount = (Integer) tags.get( key );
                    this.tags.put( key, new Integer( oldCount.intValue() + newValuetoAdd.intValue() ) );
                }
                else
                {
                	tags.put( key, newValuetoAdd );
                }
            }
        }
    }
    
    /**
     * 
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {

        if ( dashboardReport != null && dashboardReport instanceof TagListReportBean )
        {
        	this.nbTotal = this.nbTotal + ( (TagListReportBean) dashboardReport ).getNbTotal();
        	this.nbClasses = this.nbClasses + ( (TagListReportBean) dashboardReport ).getNbClasses();
        	this.addAllEntries(((TagListReportBean)dashboardReport).getTags());
        }

        
    }
    
    /**
     * 
     */
    protected Object clone()
    {
    	TagListReportBean clone = new TagListReportBean( this.getDateGeneration() );
    	clone.setNbTotal(this.nbTotal);
    	clone.setNbClasses(this.nbClasses);
        clone.addAllEntries( this.tags );
        return clone;
    }

	public int getNbTotal() {
		return nbTotal;
	}

	public void setNbTotal(int nbTotal) {
		this.nbTotal = nbTotal;
	}

	public int getNbClasses() {
		return nbClasses;
	}

	public void setNbClasses(int nbClasses) {
		this.nbClasses = nbClasses;
	}
}
