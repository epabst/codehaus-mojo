package org.codehaus.mojo.pde.stubs;

/*
 * Copyright 2006 The Apache Software Foundation.
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
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * Stub MavenProject to support native-maven-plugin test harness
 * 
 * @author dtran
 * 
 */
public class ProjectStub
    extends MavenProjectStub
{
    /**
     * Build.
     */
    private Build build = new BuildStub();

    /**
     * dependencyArtifacts.
     */
    private Set dependencyArtifacts = new HashSet();

    /**
     * ProjectStub.
     */
    public ProjectStub()
    {
        super( (Model) null );
    }

    /**
     * kinda dangerous...
     * 
     * @param model stub
     */
    public ProjectStub( Model model )
    {
        super( (Model) null );
    }

    /**
     * kinda dangerous...
     * 
     * @param project stub
     */
    public ProjectStub( MavenProject project )
    {
        super( (Model) null );
    }

    /**
     * Build stub.
     * 
     * @return Stubbed build.
     */
    public Build getBuild()
    {
        return this.build;
    }

    /**
     * dependencyArtifacts stub
     * 
     * @return dependencyArtifacts stub
     */
    public Set getDependencyArtifacts()
    {
        return this.dependencyArtifacts;
    }

    /**
     * artifacts.
     */
    private Set artifacts;

    /**
     * setArtifacts stub
     * 
     * @param theArtifacts stubbed artifacts
     * 
     */
    public void setArtifacts( Set theArtifacts )
    {
        this.artifacts = theArtifacts;
    }

    /**
     * getArtifacts stub
     * 
     * @return stubbed artifacts
     */
    public Set getArtifacts()
    {
        if ( this.artifacts == null )
        {
            this.artifacts = new HashSet();
        }
        return this.artifacts;
    }

    /**
     * getArtifactId stubs
     * 
     * @return stubbed getArtifactId
     */
    public String getArtifactId()
    {
        return "someArtifactId";
    }

}