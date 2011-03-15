package org.codehaus.mojo.jboss.packaging;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.util.FileUtils;

/**
 * Builds a deployable JBoss Process Archive.
 * 
 * @goal par
 * @phase package
 * @requiresDependencyResolution runtime
 * @threadSafe
 * @since 2.1
 */
public class ParMojo
    extends AbstractPackagingMojo
{

    /**
     * The artifact type.
     */
    private static final String ARTIFACT_TYPE = "jboss-par";

    /**
     * Component that determines how to extract archive files.
     * 
     * @component
     */
    private ArchiverManager archiverManager;

    /**
     * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" role-hint="default"
     * @required
     */
    private MavenResourcesFiltering mavenResourcesFiltering;

    /**
     * The resources to include in the packaged archive. Specifying this overrides using jbpmDirectory and
     * jpdlDirectory.
     * 
     * @parameter
     */
    private List resources;

    /**
     * The character encoding of the resource files.
     * 
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    /**
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    private MavenSession session;

    /**
     * The filters used when copying the resource files.  By default this
     * will use the resource filters specified in the POM.
     * 
     * @parameter default-value="${project.build.filters}"
     */
    private List filters;

    /**
     * The location of the jbpm deployment descriptor file (processdefinition.xml) If it is present in
     * src/main/resources then it will automatically be included. Otherwise this parameter must be set.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}/processdefinition.xml"
     *            expression="${deploymentDescriptorFile}"
     */
    private File deploymentDescriptorFile;

    /**
     * The directory where compiled classes and resources are placed.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}/classes"
     */
    private File packagingClassesDirectory;

    /**
     * The destination of the deployment descriptor file.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}"
     */
    private File deploymentDescriptorDest;
    
    /**
     * The directory where JBoss JBPM resources are located.
     * 
     * @parameter default-value="src/main/jbpm"
     */
    private File jbpmDirectory;

    /**
     * The directory where JBoss JPDL resources are located.
     * 
     * @parameter default-value="src/main/jpdl/${project.artifactId}"
     */
    private File jpdlDirectory;

    /**
     * Get the type of the artifact.
     * 
     * @return The type of the generated artifact.
     */
    public String getArtifactType()
    {
        return ARTIFACT_TYPE;
    }

    public File getDeploymentDescriptor()
    {
        return deploymentDescriptorFile;
    }
    
    public File getDeploymentDescriptorDest() {
        return deploymentDescriptorDest;
    }

    /**
     * Packages the par-specific resources.
     */
    protected void packageResources()
        throws Exception
    {
        // If resources weren't specified, add defaults
        if ( resources == null )
        {
            resources = new ArrayList();

            if ( jbpmDirectory.exists() )
            {
                getLog().info( "Configuring jbpm directory: " + jbpmDirectory.getPath() );
                Resource resource = new Resource();
                resource.setDirectory( jbpmDirectory.getPath() );
                resource.addInclude( "**/*" );
                resource.setFiltering( false );
                resources.add( resource );
            }

            if ( jpdlDirectory.exists() )
            {
                getLog().info( "Configuring jpdl directory: " + jpdlDirectory.getPath() );
                Resource resource = new Resource();
                resource.setDirectory( jpdlDirectory.getPath() );
                resource.addInclude( "**/*" );
                resource.setFiltering( false );
                resources.add( resource );
            }
        }

        MavenResourcesExecution resourcesExec =
            new MavenResourcesExecution( resources, getPackagingDirectory(), getProject(), encoding, filters,
                                         Collections.EMPTY_LIST, session );
        try
        {
            mavenResourcesFiltering.filterResources( resourcesExec );
        }
        catch ( MavenFilteringException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    /**
     * Overrides the default implementation so classes are packaged under a subdirectory.
     */
    protected void packageClasses()
        throws Exception
    {
        FileUtils.copyDirectoryStructure( getClassesDirectory(), packagingClassesDirectory );
    }

    /**
     * Overrides the default implementation to explode the depdencies into the classes directory.
     */
    protected void packageLib( Artifact artifact, String name )
        throws Exception
    {
        UnArchiver unArchiver = archiverManager.getUnArchiver( artifact.getFile() );
        unArchiver.setSourceFile( artifact.getFile() );
        unArchiver.setDestDirectory( packagingClassesDirectory );
        unArchiver.extract();
    }
}