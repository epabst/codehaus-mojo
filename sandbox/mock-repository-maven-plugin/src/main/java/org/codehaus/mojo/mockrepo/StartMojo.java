package org.codehaus.mojo.mockrepo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.mockrepo.server.SimpleHttpServer;
import org.codehaus.mojo.mockrepo.utils.MockRepoUtils;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Starts the mock maven remote repository.
 *
 * @author Stephen Connolly
 * @goal start
 * @description Starts the mock maven remote repository.
 * @since 1.0-alpha-1
 */
public class StartMojo
    extends AbstractMockRepoMojo
{
    /**
     * @component
     * @required
     * @readonly
     */
    private RepositoryMetadataManager repositoryMetadataManager;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @since 1.0-alpha-3
     */
    protected List remoteArtifactRepositories;

    /**
     * @parameter expression="${project.pluginArtifactRepositories}"
     * @readonly
     * @since 1.0-alpha-3
     */
    protected List remotePluginRepositories;

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @since 1.0-alpha-1
     */
    protected ArtifactRepository localRepository;

    /**
     * @component
     * @since 1.0-alpha-1
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @component
     * @since 1.0-alpha-1
     */
    protected ArtifactResolver artifactResolver;

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @readonly
     * @since 1.0-alpha-1
     */
    private MavenProject project;

    /**
     * The port to serve the remote repository on.
     *
     * @parameter expression="${mock-repository.port}" default-value="8080"
     * @since 1.0-alpha-1
     */
    private int port;

    /**
     * The property to set the bound port to.
     *
     * @parameter expression="${mock-repository.propertyName}" default-value="mock-repository.port"
     * @since 1.0-alpha-1
     */
    private String property;

    /**
     * Determines whether or not the server blocks when started. The default
     * behavior (daemon = true) will cause the server start and continue running subsequent
     * processes in an automated build environment.
     * <p/>
     * Often, it is desirable to let the server pause other processes
     * while it continues to handle web requests. This is useful when starting the
     * server with the intent to work with it interactively. This can be facilitated by setting
     * daemon to false.
     *
     * @parameter expression="${mock-repository.daemon}" default-value="true"
     * @since 1.0-alpha-1
     */
    protected boolean daemon;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Starting mock maven remote repository" );
        int boundPort;
        synchronized ( serverLock )
        {
            if ( server != null )
            {
                throw new MojoFailureException( "Mock maven remote repository is already running" );
            }
            server = new SimpleHttpServer( getLog(), port, makeRepository() );
            try
            {
                server.start();
            }
            catch ( InterruptedException e )
            {
                throw new MojoExecutionException( "Could not start mock maven remote repository", e );
            }
            boundPort = server.getBoundPort();
        }
        getLog().info( "Mock maven remote repository started on port " + boundPort + "." );
        if ( project != null )
        {
            getLog().debug( "Setting property " + property + " to " + boundPort );
            project.getProperties().setProperty( property, Integer.toString( boundPort ) );
        }
        if ( !daemon )
        {
            try
            {
                server.join();
            }
            catch ( InterruptedException e )
            {
                throw new MojoFailureException( "Non-daemon mode stopped" );
            }
            finally
            {
                synchronized ( serverLock )
                {
                    server.stop();
                    getLog().info( "Mock maven repository stopped" );
                    server = null;
                }
            }
        }
    }

    private Repository makeRepository()
        throws MojoExecutionException
    {
        byte[] emptyJar = MockRepoUtils.newEmptyJarContent();
        ByteArrayContent emptyJarContent = new ByteArrayContent( emptyJar );
        HostedRepository hosted = new HostedRepository();
        try
        {
            final List pomFiles = FileUtils.getFiles( sourceDirectory, "**/*.pom", null, true );
            final List artifactFiles = FileUtils.getFiles( sourceDirectory, "**", "**/*.pom", true );
            getLog().info( "Found " + pomFiles.size() + " pom files to host" );
            Iterator i = pomFiles.iterator();
            while ( i.hasNext() )
            {
                File file = (File) i.next();
                getLog().debug( "Parsing " + file );
                if ( file.isFile() )
                {
                    try
                    {
                        final Model model = hosted.deployPom( new FileContent( file ) );
                        String groupId = MockRepoUtils.getGroupId( model );
                        String artifactId = MockRepoUtils.getArtifactId( model );
                        String version = MockRepoUtils.getVersion( model );
                        String packaging = model.getPackaging() == null ? "jar" : model.getPackaging();
                        final String basePath = MockRepoUtils.getGAVPathName( groupId, artifactId, version );

                        String baseName = StringUtils.chompLast( file.getName(), ".pom" );
                        List related = new ArrayList();
                        Iterator k = artifactFiles.iterator();
                        while ( k.hasNext() )
                        {
                            File artifactFile = (File) k.next();
                            if ( artifactFile.getParentFile().equals( file.getParentFile() )
                                && artifactFile.getName().startsWith( baseName ) )
                            {
                                related.add( artifactFile );
                                k.remove();
                            }
                        }
                        if ( !related.isEmpty() )
                        {
                            Iterator k1 = related.iterator();
                            while ( k1.hasNext() )
                            {
                                File associatedFile = (File) k1.next();
                                String associatedName = associatedFile.getName().substring( baseName.length() );
                                hosted.deploy( basePath + associatedName, new FileContent( associatedFile ) );
                            }
                        }
                        else
                        {
                            if ( "jar".equals( packaging ) || "maven-plugin".equals( packaging ) )
                            {
                                hosted.deploy( basePath + ".jar", emptyJarContent );
                            }
                            else if ( "war".equals( packaging ) || "ear".equals( packaging )
                                || "rar".equals( packaging ) || "zip".equals( packaging ) || "sar".equals( packaging )
                                || "par".equals( packaging ) )
                            {
                                hosted.deploy( basePath + "." + packaging, emptyJarContent );
                            }
                            hosted.deploy( basePath + "-sources.jar", emptyJarContent );
                        }
                    }
                    catch ( IOException e )
                    {
                        // ignore
                    }
                }
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        ProxyRepository proxy = null;
        try
        {
            proxy =
                new ProxyRepository( repositoryMetadataManager, remoteArtifactRepositories, remotePluginRepositories,
                                     localRepository, artifactFactory, getLog(), artifactResolver );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        return new CompositeRepository( new Repository[]{hosted, proxy}, getLog() );
    }

}
