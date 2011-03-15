package org.codehaus.mojo.dashboard.report.plugin.beans;

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

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class FindBugsReportBean extends AbstractReportBean
{
    /**
     *
     */
    private int nbClasses;

    /**
     *
     */
    private int nbBugs;

    /**
     *
     */
    private int nbErrors;

    /**
     *
     */
    private int nbMissingClasses;

    /**
     * Map:FindBugs categories.
     */
    private Map categories;

    /**
     * Map:FindBugs priorities.
     */
    private Map priorities;

    /**
     * Default constructor
     *
     */
    public FindBugsReportBean()
    {
        this.categories = new Hashtable();
        this.priorities = new Hashtable();
    }

    /**
     *
     * @param dateGeneration
     */
    public FindBugsReportBean( Date dateGeneration )
    {
        super( dateGeneration );
        this.categories = new Hashtable();
        this.priorities = new Hashtable();
    }

    /**
     *
     * @return int
     */
    public int getNbClasses()
    {
        return nbClasses;
    }

    /**
     *
     * @param nbClasses
     */
    public void setNbClasses( int nbClasses )
    {
        this.nbClasses = nbClasses;
    }

    /**
     *
     * @return
     */
    public int getNbErrors()
    {
        return nbErrors;
    }

    /**
     *
     * @param nbErrors
     */
    public void setNbErrors( int nbErrors )
    {
        this.nbErrors = nbErrors;
    }

    /**
     *
     * @return
     */
    public int getNbBugs()
    {
        return nbBugs;
    }

    /**
     *
     * @param nbBugs
     */
    public void setNbBugs( int nbBugs )
    {
        this.nbBugs = nbBugs;
    }

    /**
     *
     * @return
     */
    public int getNbMissingClasses()
    {
        return nbMissingClasses;
    }

    /**
     *
     * @param nbMissingClasses
     */
    public void setNbMissingClasses( int nbMissingClasses )
    {
        this.nbMissingClasses = nbMissingClasses;
    }

    /**
     * get the findbugs categories map.
     *
     * @return findbugs categories map
     */
    public Map getCategories()
    {
        return this.categories;
    }

    /**
     *
     * @param categories
     */
    public void setCategories( Map categories )
    {
        this.categories = categories;
    }

    /**
     * get the findbugs priorities map.
     *
     * @return findbugs priorities map
     */
    public Map getPriorities()
    {
        return this.priorities;
    }

    /**
     *
     * @param priorities
     */
    public void setPriorities( Map priorities )
    {
        this.priorities = priorities;
    }

    /**
     *
     * @param error
     */
    public void addCategory( String category )
    {
        if ( this.categories.isEmpty() )
        {
            this.categories.put( category, new Integer( 1 ) );
        }
        else
        {
            if ( ( this.categories.containsKey( category ) ) )
            {

                Integer oldValue = ( (Integer) ( this.categories.get( category ) ) );
                this.categories.put( category, new Integer( oldValue.intValue() + 1 ) );
            }
            else
            {
                this.categories.put( category, new Integer( 1 ) );
            }
        }
    }

    /**
     * @param error
     */
    public void addAllCategories( Map categories )
    {
        if ( this.categories.isEmpty() )
        {
            this.categories.putAll( categories );
        }
        else
        {
            Iterator iter = categories.keySet().iterator();
            while ( iter.hasNext() )
            {
                String category = (String) iter.next();
                Integer newValuetoAdd = ( (Integer) ( categories.get( category ) ) );
                if ( ( this.categories.containsKey( category ) ) )
                {

                    Integer oldValue = ( (Integer) ( this.categories.get( category ) ) );
                    this.categories.put( category, new Integer( oldValue.intValue() + newValuetoAdd.intValue() ) );
                }
                else
                {
                    this.categories.put( category, newValuetoAdd );
                }
            }
        }
    }

    /**
     *
     * @param error
     */
    public void addPriority( String priority )
    {
        if ( this.priorities.isEmpty() )
        {
            this.priorities.put( priority, new Integer( 1 ) );
        }
        else
        {
            if ( ( this.priorities.containsKey( priority ) ) )
            {

                Integer oldValue = ( (Integer) ( this.priorities.get( priority ) ) );
                this.priorities.put( priority, new Integer( oldValue.intValue() + 1 ) );
            }
            else
            {
                this.priorities.put( priority, new Integer( 1 ) );
            }
        }
    }

    /**
     * @param error
     */
    public void addAllPriorities( Map priorities )
    {
        if ( this.priorities.isEmpty() )
        {
            this.priorities.putAll( priorities );
        }
        else
        {
            Iterator iter = priorities.keySet().iterator();
            while ( iter.hasNext() )
            {
                String priority = (String) iter.next();
                Integer newValuetoAdd = ( (Integer) ( priorities.get( priority ) ) );
                if ( ( this.priorities.containsKey( priority ) ) )
                {
                    Integer oldValue = ( (Integer) ( this.priorities.get( priority ) ) );
                    this.priorities.put( priority, new Integer( oldValue.intValue() + newValuetoAdd.intValue() ) );
                }
                else
                {
                    this.priorities.put( priority, newValuetoAdd );
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
        if ( dashboardReport != null && dashboardReport instanceof FindBugsReportBean )
        {
            this.nbClasses = this.nbClasses + ( (FindBugsReportBean) dashboardReport ).getNbClasses();

            this.nbBugs = this.nbBugs + ( (FindBugsReportBean) dashboardReport ).getNbBugs();

            this.nbErrors = this.nbErrors + ( (FindBugsReportBean) dashboardReport ).getNbErrors();

            this.nbMissingClasses =
                this.nbMissingClasses + ( (FindBugsReportBean) dashboardReport ).getNbMissingClasses();

            this.addAllCategories( ( (FindBugsReportBean) dashboardReport ).getCategories() );
            this.addAllPriorities( ( (FindBugsReportBean) dashboardReport ).getPriorities() );
        }
    }

    /**
     *
     */
    protected Object clone()
    {
        FindBugsReportBean clone = new FindBugsReportBean( this.getDateGeneration() );
        clone.setNbClasses( this.nbClasses );
        clone.setNbBugs( this.nbBugs );
        clone.setNbMissingClasses( this.nbMissingClasses );
        clone.setNbErrors( this.nbErrors );
        clone.addAllCategories( this.getCategories() );
        clone.addAllPriorities( this.getPriorities() );
        return clone;
    }

}
