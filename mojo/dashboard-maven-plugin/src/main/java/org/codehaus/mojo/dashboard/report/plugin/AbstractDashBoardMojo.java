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
package org.codehaus.mojo.dashboard.report.plugin;

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.mojo.dashboard.report.plugin.hibernate.HibernateService;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public abstract class AbstractDashBoardMojo extends AbstractMojo
{

    /**
     * The maven project
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * Directory containing The generated DashBoard report Datafile "dashboard-report.xml".
     * 
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * <p>
     * The generated DashBoard report Datafile.
     * </p>
     * 
     * @parameter default-value="dashboard-report.xml"
     * @readonly
     */
    protected String dashboardDataFile;

    /**
     * The local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * number of XRef JDepend/Cobertura packages to export in dashboard summary page
     * 
     * @parameter default-value="10"
     */
    protected int nbExportedPackagesSummary;

    /**
     * Hibernate Service
     * 
     * @component
     * 
     */
    protected HibernateService hibernateService;

    /**
     * Hibernate dialect
     * 
     * @parameter expression="${dialect}"
     * @required
     */
    protected String dialect;

    /**
     * Database driver classname
     * 
     * @parameter expression="${driverClass}"
     * @required
     */
    protected String driverClass;

    /**
     * Database URL
     * 
     * @parameter expression="${connectionUrl}"
     * @required
     */
    protected String connectionUrl;

    /**
     * Database username
     * 
     * @parameter expression="${username}"
     * @required
     */
    protected String username;

    /**
     * Database password
     * 
     * @parameter expression="${password}"
     */
    protected String password;

    /**
     * Project builder
     * 
     * @component
     */
    protected MavenProjectBuilder mavenProjectBuilder;

    protected boolean isPropHibernateSet = false;
    
    protected DashBoardUtils dashBoardUtils;

    
    protected void configureHibernateDriver()
    {
        hibernateService.setDialect( dialect );
        hibernateService.setDriverClass( driverClass );
        hibernateService.setConnectionUrl( connectionUrl );
        hibernateService.setUsername( username );
        hibernateService.setPassword( password );
    }
    
    protected boolean isDBAvailable(){
        boolean isDBAvailable = false;
        if ( ( dialect != null && dialect.length() > 0 ) && ( driverClass != null && driverClass.length() > 0 )
                        && ( connectionUrl != null && connectionUrl.length() > 0 )
                        && ( username != null && username.length() > 0 )  )
        {
            isDBAvailable = true;
        }
        return isDBAvailable;
    }
}
