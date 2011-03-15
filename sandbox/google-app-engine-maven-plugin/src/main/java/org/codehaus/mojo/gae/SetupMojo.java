package org.codehaus.mojo.gae;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

/**
 * Check the project configuration and setup the Google App Engine /war folder to prepare launching the GAE dev server
 * using Google Eclipse plugin
 * 
 * @goal setup
 * @phase validate
 * @requiresDependencyResolution runtime
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class SetupMojo
    extends AbstractMojo
{
    /**
     * @parameter default-value="${basedir}/war"
     */
    private File warSourceDirectory;

    /**
     * Project runtime classpath.
     * 
     * @parameter expression="${project.runtimeArtifacts}"
     * @required
     * @readonly
     */
    protected List<Artifact> runtimeArtifacts;

    /**
     * @parameter expression="${project}"
     * @readOnly
     * @required
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {

        File outputDirectory = new File( project.getBuild().getOutputDirectory() );
        File classes = new File( warSourceDirectory, "WEB-INF/classes" );
        if ( !outputDirectory.getAbsolutePath().equals( classes.getAbsolutePath() ) )
        {
            throw new MojoExecutionException( "Your build.outputDirectory MUST be set to /war/WEB-INF/classes" );
        }

        File libs = new File( warSourceDirectory, "WEB-INF/lib" );
        for ( Artifact artifact : runtimeArtifacts )
        {
            try
            {
                FileUtils.copyFileToDirectory( artifact.getFile(), libs );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Failed to prepare /war folder", e );
            }
        }
    }
}
