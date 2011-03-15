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

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract super class for all the packaging mojos. This class contains the logic for actually building the packaging
 * types.
 */
public abstract class AbstractPackagingMojo
    extends AbstractMojo
{

    /**
     * The maven project.
     * 
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * The directory for the generated packaging.
     * 
     * @parameter default-value="${project.build.directory}"
     */
    private File outputDirectory;

    /**
     * The directory containing generated classes.
     * 
     * @parameter default-value="${project.build.outputDirectory}"
     * @readonly
     */
    private File classesDirectory;

    /**
     * The directory where the JBoss packaging is built.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}"
     */
    private File packagingDirectory;

    /**
     * The filename for the output deployment descriptor. By default the deployment descriptor will retain the same
     * filename.
     * 
     * @parameter
     */
    private String deploymentDescriptorDestName;

    /**
     * The destination of the deployment descriptor file.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}/META-INF"
     */
    private File deploymentDescriptorDest;

    /**
     * The directory where to put the libs.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}/lib"
     */
    private File libDirectory;

    /**
     * The name of the generated packaging archive.
     * 
     * @parameter default-value="${project.build.finalName}"
     */
    private String archiveName;

    /**
     * All artifacts are excluded.
     * 
     * @parameter expression="${excludeAll}" default-value="false"
     */
    private boolean excludeAll;

    /**
     * Dependency Artifacts excluded from packaging within the generated archive file. Use artifactId:groupId in nested
     * exclude tags.
     * 
     * @parameter
     */
    private Set excludes;

    /**
     * The Jar archiver.
     * 
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="jar"
     */
    private JarArchiver jarArchiver;

    /**
     * The maven archive configuration to use.
     * 
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * The manifest file for the archive.
     * 
     * @parameter
     */
    private File manifest;

    /**
     * Classifier to add to the generated artifact. If given, the artifact will not be the primary project artifact.
     * 
     * @parameter
     */
    private String classifier;

    /**
     * Whether this is the main artifact of the current project.
     * 
     * @parameter default-value="true"
     */
    private boolean primaryArtifact;

    /**
     * The project helper.
     * 
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * The artifact handler manager.
     * 
     * @component
     */
    private ArtifactHandlerManager artifactHandlerManager;

    /**
     * Whether to remove the version numbers from the filenames of the included dependencies. By default the included
     * dependencies will have the format [artifactId]-[version]-[classifier].[type] If this parameter is set to true,
     * the jar name will be in the format [artifactId]-[classifier].[type]
     * 
     * @parameter default-value="false"
     */
    private boolean removeDependencyVersions;

    /**
     * Whether to generate only the exploded archive format. By default both an exploded directory and a zipped file
     * will be created. If set to "true" only the exploded directory will be created.
     * 
     * @parameter default-value="false" expression="${explodedOnly}"
     * @since 2.0
     */
    private boolean explodedOnly;

    /**
     * @return Whether only the exploded format should be created
     */
    public boolean isExplodedOnly()
    {
        return explodedOnly;
    }

    /**
     * @return the maven project
     */
    public MavenProject getProject()
    {
        return project;
    }

    /**
     * @return the packaging directory
     */
    public File getPackagingDirectory()
    {
        return packagingDirectory;
    }

    /**
     * @return the packaging directory
     */
    public File getClassesDirectory()
    {
        return classesDirectory;
    }

    /**
     * @return the lib directory
     */
    public File getLibDirectory()
    {
        return libDirectory;
    }

    /**
     * Get the deployment descriptor file. Subclasses may override this method to provide a different name for their
     * type of archive packaging.
     * 
     * @return deployment descriptor File
     */
    public abstract File getDeploymentDescriptor();

    /**
     * Get the type of the artifact.
     * 
     * @return The type of the generated artifact
     */
    public abstract String getArtifactType();

    /**
     * @return The directory where to write the archive
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Build the package in an exploded format.
     * 
     * @throws MojoExecutionException if an error occurred
     * @throws MojoFailureException if an error occurred
     */
    public void buildExplodedPackaging()
        throws MojoExecutionException
    {
        buildExplodedPackaging( Collections.EMPTY_SET );
    }

    /**
     * Build the package in an exploded format.
     * 
     * @param excludes File patterns to exclude from the packaging.
     * @throws MojoExecutionException if an error occurred
     * @throws MojoFailureException if an error occurred
     */
    public void buildExplodedPackaging( Set excludes )
        throws MojoExecutionException
    {
        getLog().info( "Assembling JBoss packaging " + project.getArtifactId() + " in " + packagingDirectory );

        if ( excludes == null )
        {
            excludes = Collections.EMPTY_SET;
        }

        packagingDirectory.mkdirs();
        libDirectory.mkdirs();

        try
        {
            packageResources();
        }
        catch ( Exception e1 )
        {
            throw new MojoExecutionException( "Failed while packaging resources", e1 );
        }

        if ( classesDirectory.exists() && !classesDirectory.equals( packagingDirectory ) )
        {
            try
            {
                packageClasses();
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Unable to copy classes directory", e );
            }
        }

        File deploymentDescriptorFile = this.getDeploymentDescriptor();
        if ( deploymentDescriptorFile == null || !deploymentDescriptorFile.exists() )
        {
            throw new MojoExecutionException( "Could not find descriptor file: " + deploymentDescriptorFile );
        }

        String destName = this.getDeploymentDescriptorDestName();
        if ( destName == null )
        {
            destName = deploymentDescriptorFile.getName();
        }
        File deploymentDescriptorTarget = new File( getDeploymentDescriptorDest(), destName );

        if ( !deploymentDescriptorTarget.exists() )
        {
            deploymentDescriptorTarget.getParentFile().mkdirs();

            try
            {
                FileUtils.copyFile( deploymentDescriptorFile, deploymentDescriptorTarget );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Could not copy deployment descriptor", e );
            }
        }

        Set artifacts = project.getArtifacts();
        List rejects = new ArrayList();
        final Set includedArtifacts = new HashSet();
        final ScopeArtifactFilter filter = new ScopeArtifactFilter( Artifact.SCOPE_RUNTIME );
        getLog().debug( "" );
        getLog().debug( "    Including artifacts: " );
        getLog().debug( "    -------------------" );
        for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( !artifact.isOptional() && filter.include( artifact ) )
            {
                String descriptor = artifact.getGroupId() + ":" + artifact.getArtifactId();

                if ( !excludeAll && artifact.getArtifactHandler().isAddedToClasspath()
                    && !excludes.contains( descriptor ) )
                {
                    getLog().debug( "        o " + descriptor );

                    String name = getArtifactName( artifact );
                    if ( !includedArtifacts.add( name ) )
                    {
                        name = artifact.getGroupId() + "-" + name;
                        getLog().info( "Duplicate artifact discovered, using full name: " + name );
                    }

                    try
                    {
                        packageLib( artifact, name );
                    }
                    catch ( Exception e )
                    {
                        throw new MojoExecutionException( "Could not copy dependency", e );
                    }
                }
                else
                {
                    rejects.add( artifact );
                }
            }
        }

        if ( !excludes.isEmpty() )
        {
            getLog().debug( "" );
            getLog().debug( "    Excluded artifacts: " );
            getLog().debug( "    ------------------" );
            for ( int ii = 0; ii < rejects.size(); ii++ )
            {
                getLog().debug( "        o " + rejects.get( ii ) );
            }
        }
        else
        {
            getLog().debug( "No artifacts have been excluded." );
        }

        getLog().debug( "" );

        buildSpecificPackaging( excludes );

        if ( libDirectory.isDirectory() )
        {
            String[] files = libDirectory.list();

            if ( files.length == 0 )
            {
                libDirectory.delete();
            }
        }
    }

    /**
     * Perform any packaging specific to this type.
     * 
     * @param excludes The exclude list.
     * @throws MojoExecutionException For plugin failures.
     * @throws MojoFailureException For unexpected plugin failures.
     * @throws IOException For exceptions during IO operations.
     */
    protected void buildSpecificPackaging( final Set excludes )
        throws MojoExecutionException
    {
    }

    /**
     * @return The name of the archive
     */
    public String getArchiveName()
    {
        return archiveName;
    }

    /**
     * Generates the packaged archive.
     * 
     * @throws MojoExecutionException if there is a problem
     */
    protected void performPackaging()
        throws MojoExecutionException
    {

        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler( getArtifactType() );
        String extension = artifactHandler.getExtension();
        String type = getArtifactType();

        final File archiveFile = calculateFile( outputDirectory, archiveName, classifier, extension );

        // generate archive file
        getLog().debug( "Generating JBoss packaging " + archiveFile.getAbsolutePath() );
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( jarArchiver );
        archiver.setOutputFile( archiveFile );
        try
        {
            jarArchiver.addDirectory( getPackagingDirectory() );
            if ( manifest != null )
            {
                jarArchiver.setManifest( manifest );
            }
            archiver.createArchive( getProject(), archive );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Problem generating archive file.", e );
        }

        // If there is a classifier, then this archive is not the primary project artifact.
        if ( classifier != null && !classifier.equals( "" ) )
        {
            primaryArtifact = false;
        }

        if ( primaryArtifact )
        {
            Artifact artifact = project.getArtifact();
            artifact.setFile( archiveFile );
            artifact.setArtifactHandler( artifactHandler );
        }
        else
        {
            projectHelper.attachArtifact( project, type, classifier, archiveFile );
        }
    }

    /**
     * Calculate the name of the archive file.
     * 
     * @param outputDirectory The output directory.
     * @param archiveName The name of the artifact archive.
     * @param classifier The classifier of the artifact.
     * @param extension The artifact archive extension.
     * @return The archive file.
     */
    private static File calculateFile( final File outputDirectory, final String archiveName, final String classifier,
                                       final String extension )
    {
        final String basename;
        if ( StringUtils.isEmpty( classifier ) )
        {
            basename = archiveName;
        }
        else
        {
            basename = archiveName + '-' + classifier;
        }
        return new File( outputDirectory, basename + '.' + extension );
    }

    /**
     * Get the name of the artifact.
     * 
     * @param artifact The current artifact.
     * @return The name of the artifact.
     */
    private String getArtifactName( Artifact artifact )
    {
        String artifactName = artifact.getArtifactId();

        if ( !this.removeDependencyVersions )
        {
            artifactName += "-" + artifact.getVersion();
        }

        if ( !StringUtils.isEmpty( artifact.getClassifier() ) )
        {
            artifactName += "-" + artifact.getClassifier();
        }

        artifactName += "." + artifact.getArtifactHandler().getExtension();

        return artifactName;
    }

    public String getDeploymentDescriptorDestName()
    {
        return deploymentDescriptorDestName;
    }

    public File getDeploymentDescriptorDest()
    {
        return deploymentDescriptorDest;
    }

    /**
     * Main execution for the goal.
     * 
     * @throws MojoExecutionException if an error occurred while building the webapp
     */
    public void execute()
        throws MojoExecutionException
    {

        buildExplodedPackaging( excludes );

        if ( !isExplodedOnly() )
        {
            performPackaging();
        }
    }

    /**
     * Routine that packages resources not handled by default resource handling.
     */
    protected void packageResources()
        throws Exception
    {
        // Do nothing, override to handle specific resources
    }

    /**
     * Routine that includes the specified artifact into the exploded packaging.
     * 
     * @param artifact
     * @param name
     * @throws Exception
     */
    protected void packageLib( Artifact artifact, String name )
        throws Exception
    {
        FileUtils.copyFile( artifact.getFile(), new File( libDirectory, name ) );
    }

    /**
     * Routine that includes the specified artifact into the exploded packaging.
     * 
     * @throws Exception
     */
    protected void packageClasses()
        throws Exception
    {
        FileUtils.copyDirectoryStructure( classesDirectory, packagingDirectory );
    }
}
