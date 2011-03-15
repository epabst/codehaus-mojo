package org.codehaus.mojo.dashboard.report.plugin;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.dashboard.report.plugin.beans.AbstractReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject;
import org.hibernate.Query;

/**
 * A Dashboard report which aggregates all other report results and stores all results in database.
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * @goal persist
 */
public class DashBoardDBMojo extends AbstractDashBoardMojo
{

    private Date generatedDate;

    private boolean isPropHibernateSet = false;

    /*
     * (non-Javadoc)
     *
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {

        boolean persistDB = this.canPersistDB();
        boolean recursive = this.isRecursive();
        if (recursive)
        {
            this.dashBoardUtils = DashBoardUtils.getInstance(this.getLog(), this.mavenProjectBuilder,
                    this.localRepository, true);
            this.generatedDate = new Date(System.currentTimeMillis());
            DashBoardMavenProject mavenProject = this.dashBoardUtils.getDashBoardMavenProject(this.project,
                    this.dashboardDataFile, this.generatedDate);
            this.dashBoardUtils.saveXMLDashBoardReport(this.project, mavenProject, this.dashboardDataFile);
            if (persistDB)
            {
                this.configureHibernateDriver();

                long start = System.currentTimeMillis();

                this.getLog().info("DashBoardDBMojo project = " + this.project.getName());
                this.getLog().info("DashBoardDBMojo nb modules = " + this.project.getModules().size());
                this.getLog().info("DashBoardDBMojo is root = " + this.project.isExecutionRoot());
                this.getLog().info("DashBoardDBMojo base directory = " + this.project.getBasedir());
                this.getLog().info("DashBoardDBMojo output directory = " + this.outputDirectory);
                this.getLog().info(
                        "DashBoardDBMojo project language = "
                                + this.project.getArtifact().getArtifactHandler().getLanguage());
                this.refactorMavenProject(mavenProject);

                this.hibernateService.saveOrUpdate(mavenProject);

                long end = System.currentTimeMillis();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
                this.getLog().info(
                                    "DashBoardDBMojo save Dashboard elapsed time = "
                                                    + formatter.format( new Date( end - start ) ) );
            }
        }
    }

    private boolean canPersistDB()
    {
        boolean persist = false;

        boolean recursive = this.isRecursive();
        boolean root = this.project.isExecutionRoot();

        this.isPropHibernateSet = this.isDBAvailable();

        if (recursive && root && this.isPropHibernateSet)
        {
            persist = true;
        }
        else
        {
            if (!root)
            {
                this.getLog().warn("DashBoardDBMojo: Not root project - skipping persist goal.");
            }
            if (!this.isPropHibernateSet)
            {
                this.getLog().warn("DashBoardDBMojo: Hibernate properties not set - skipping persist goal.");
            }
        }

        return persist;
    }

    private boolean isRecursive()
    {
        boolean recursive =
            ( this.project.getCollectedProjects().size() < this.project.getModules().size() ) ? false : true;
        if (!recursive)
        {
            this.getLog().warn("DashBoardDBMojo: Not recursive into sub-projects - skipping XML generation.");
        }
        return recursive;
    }

    private void refactorMavenProject(DashBoardMavenProject mavenProject)
    {
        StringBuffer queryString = new StringBuffer();
        queryString.append("select m.id from DashBoardMavenProject m where ");
        queryString.append("m.artifactId = :artifactid ");
        queryString.append("and m.groupId = :groupid ");
        queryString.append("and m.version = :version ");

        Query query = this.hibernateService.getSession().getNamedQuery(
                "org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject.getDashBoardMavenProjectID");
        query.setParameter("artifactid", mavenProject.getArtifactId());
        query.setParameter("groupid", mavenProject.getGroupId());
        query.setParameter("version", mavenProject.getVersion());

        List result = query.list();
        if (result != null && !result.isEmpty())
        {
            long id = ((Long) (result.get(0))).longValue();
            mavenProject.setId(id);
        }

        Set reports = mavenProject.getReports();
        Iterator iter = reports.iterator();
        while (iter.hasNext())
        {
            AbstractReportBean report = (AbstractReportBean) iter.next();
            if (report != null)
            {
                report.setMavenProject(mavenProject);
            }
        }
        Set modules = mavenProject.getModules();
        Iterator iterModule = modules.iterator();
        while (iterModule.hasNext())
        {
            DashBoardMavenProject project = (DashBoardMavenProject) iterModule.next();
            this.refactorMavenProject(project);
        }
    }

}