package org.codehaus.mojo.hibernatedoclet;

/*
 * Copyright 2005 Ashley Williams.
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.mant.MantGoal;

/**
 * Hibernate doclet plugin.
 * 
 * @goal hibernatedoclet
 * @phase generate-sources
 * @requiresDependencyResolution test
 */
public class HibernateDocletMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     * @required
     */
    private String task;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        String[] mappings = new String[] {
            "@destDir", MantGoal.WEB_INF_GEN,
            "fileset/@dir", MantGoal.JAVA,
        //            ,
        //            "deploymentdescriptor/@destDir", MantGoal.META_INF_GEN,
        //            "jboss/@destDir",                MantGoal.META_INF_GEN
        };
        MantGoal goal = new MantGoal( this, project, "xdoclet.modules.hibernate.HibernateDocletTask", task, mappings );
        goal.execute( "xdoclet.class.path" );
    }
}