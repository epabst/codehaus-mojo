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


import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */

public class DashBoardMavenProject
{
    /**
     * 
     */
    private String projectName;
    /**
     * 
     */
    private String artifactId;
    
    private String groupId;
    
    private String version;
    
    private long id;
    
    private Set modules = new HashSet();
    
    private Set reports = new HashSet();
    
    
    private transient Map reportsByType = new Hashtable();
    /**
     * 
     *
     */
    public DashBoardMavenProject()
    {
    }
    /**
     * 
     * @param artifactId
     */
    public DashBoardMavenProject( String artifactId )
    {
        this.artifactId = artifactId;
    }
    /**
     * 
     * @param artifactId
     */
    public DashBoardMavenProject( String artifactId, String groupId )
    {
        this.artifactId = artifactId;
        this.groupId = groupId;
    }
    /**
     * 
     * @param artifactId
     * @param projectName
     */
    public DashBoardMavenProject( String artifactId, String groupId, String projectName )
    {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.projectName = projectName;
    }
    /**
     * 
     * @param artifactId
     * @param projectName
     * @param dateGeneration
     */
    public DashBoardMavenProject( String artifactId, String groupId, String projectName, String version )
    {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.projectName = projectName;
        this.version = version;
    }
    /**
     * 
     */
    public String getProjectName()
    {
        return projectName;
    }
    /**
     * 
     */
    public void setProjectName( String projectName )
    {
        this.projectName = projectName;
    }
    /* (non-Javadoc)
     * @see org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean#getartifactId()
     */
    public String getArtifactId()
    {
        return artifactId;
    }
    /* (non-Javadoc)
     * @see org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean#setartifactId(java.lang.String)
     */
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }
    public String getVersion()
    {
        return version;
    }
    public void setVersion( String version )
    {
        this.version = version;
    }
    public long getId()
    {
        return id;
    }
    public void setId( long id )
    {
        this.id = id;
    }
    public Set getModules()
    {
        return modules;
    }
    public void setModules( Set modules )
    {
        this.modules = modules;
    }
    /**
     * 
     * @param report
     */
    public void addModule( DashBoardMavenProject module )
    {
        this.modules.add( module );
        fillReports( module );
    }
    
    /**
     * 
     * @param dashboardReport
     */
    private void fillReports( DashBoardMavenProject module )
    {

        if ( reports.isEmpty() )
        {
            Set reportsModule = module.getReports();
            Iterator iter = reportsModule.iterator();
            while ( iter.hasNext() )
            {
                AbstractReportBean report = (AbstractReportBean) iter.next();
                if ( report != null )
                {
                    this.reports.add( report.clone() );
                }
            }

        }
        else
        {
            Set reportsModule = module.getReports();
            Iterator iter = reportsModule.iterator();
            while ( iter.hasNext() )
            {
                AbstractReportBean report = (AbstractReportBean) iter.next();
                boolean isMerge = false;
                if ( report != null )
                {
                    Iterator iterInternal = this.reports.iterator();
                    while ( iterInternal.hasNext() )
                    {
                        IDashBoardReportBean reportInternal = (IDashBoardReportBean) iterInternal.next();
                        if ( reportInternal != null && reportInternal.getClass().equals( report.getClass() ) )
                        {
                            reportInternal.merge( report );
                            isMerge = true;
                        }
                    }
                    if ( !isMerge )
                    {
                        this.reports.add( report.clone() );
                    }
                }

            }
        }
    }
    public Set getReports()
    {
        return reports;
    }
    public void setReports( Set reports )
    {
        this.reports = reports;
    }
    
    /**
     * 
     * @param report
     */
    public void addReport( AbstractReportBean report )
    {
        this.reports.add( report );
    }
    /**
     * 
     * @param classname
     * @return
     */
    public AbstractReportBean getReportsByType( Class classname )
    {
        if ( reportsByType == null || reportsByType.isEmpty() )
        {
            reportsByType = new Hashtable();

            Iterator iter = this.reports.iterator();

            while ( iter.hasNext() )
            {
                IDashBoardReportBean report = (IDashBoardReportBean) iter.next();
                if ( report != null )
                {
                    reportsByType.put( report.getClass(), report );
                }
            }
        }
        return (AbstractReportBean) reportsByType.get( classname );
    }

}
