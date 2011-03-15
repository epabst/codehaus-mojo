package org.codehaus.mojo.webdoclet;

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
 * webdoclet plugin.
 * This plugin uses JaxMe 2 to generate JAXB source files from an XML schema.
 * The source files are generated in a directory under the project build
 * directory derived from the configured properties.
 * 
 * @goal webdoclet
 * @phase generate-sources
 * @requiresDependencyResolution test
 */
public class WebDocletMojo
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
            "@destDir",                      MantGoal.JAVA_GEN,
            "@mergeDir",                     MantGoal.JAVA,
            "deploymentdescriptor/@destDir", MantGoal.WEB_INF_GEN,
            "fileset/@dir",                  MantGoal.JAVA,
            "jbosswebxml/@destDir",          MantGoal.WEB_INF_GEN
        };
        MantGoal goal = new MantGoal(this, project, "xdoclet.modules.web.WebDocletTask", task, mappings);
        goal.execute("xdoclet.class.path");
    }
}
