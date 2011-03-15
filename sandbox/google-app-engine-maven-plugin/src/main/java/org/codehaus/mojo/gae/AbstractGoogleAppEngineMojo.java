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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Base class for GAE related Mojos
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 * @requireDepedencyResolution compile
 */
public abstract class AbstractGoogleAppEngineMojo
    extends AbstractMojo
{
    public static final String GAE_GROUPID = "com.google.appegine";

    public static final String GAE_ARTIFACTID = "appengine-api-1.0-sdk";

    /**
     * Location of a local AppEngine SDK
     *
     * @parameter
     */
    private File sdkHome;

    /**
     * AppEngine version to resolve and use within project
     *
     * @parameter default-value="1.2.0"
     */
    private String sdkVersion;

    /**
     * The maven project descriptor
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${basedir}/war"
     */
    private File webappDirectory;


    /**
     * @component
     */
    protected ArtifactResolver resolver;

    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @component
     */
    protected ArchiverManager archiverManager;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    protected List<ArtifactRepository> remoteRepositories;

    protected void executeSDKClass( String className, String[] args, boolean includORM )
        throws MojoExecutionException
    {
        // Use a Set to avoid duplicates in classpath
        // i.e. DataNucleus loading error with "Plugin (Bundle) is already registered"
        Set<String> classpath = new LinkedHashSet<String>();
        File sdk = resolveSDK();

        // Include all SDK libs in classpath
        // ugly, but SDK has no POM :'(
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( sdk );
        if ( includORM )
        {
            scanner.setIncludes( new String[] { "lib/tools/orm/*.jar", "lib/appengine-tools-api.jar" } );
        }
        else
        {
            scanner.setIncludes( new String[] { "lib/appengine-tools-api.jar" } );
        }
        scanner.scan();
        for ( String path : scanner.getIncludedFiles() )
        {
            classpath.add( new File( sdk, path ).getAbsolutePath() );
        }

        List<String> artifacts;
        try
        {
            artifacts = project.getCompileClasspathElements();
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( "Failed to resolve project dependencies", e );
        }
        classpath.addAll( artifacts );
        String cp = StringUtils.join( classpath.iterator(), File.pathSeparator );

        Commandline cmd = new Commandline();
        cmd.setExecutable( getJavaCommand() );
        cmd.createArg().setValue( "-cp" );
        cmd.createArg().setValue( cp );
        cmd.createArg().setValue( className );
        for ( String arg : args )
        {
            cmd.createArg().setValue( arg );
        }

        try
        {
            getLog().debug( "Execute \n" + cmd );
            CommandLineUtils.executeCommandLine( cmd, System.in, out, err );
        }
        catch ( Exception e )
        {
            getLog().error( "Failed to execute SDK tool " + className, e );
            throw new MojoExecutionException( "Google App Engine SDK tool failed", e );
        }
    }

    private String getJavaCommand()
        throws MojoExecutionException
    {
        String jvm = System.getProperty( "java.home" );

        // does-it exists ? is-it a directory or a path to a java executable ?
        File jvmFile = new File( jvm );
        if ( !jvmFile.exists() )
        {
            throw new MojoExecutionException( "the configured jvm " + jvm
                + " doesn't exists please check your environnement" );
        }
        if ( jvmFile.isDirectory() )
        {
            // it's a directory we construct the path to the java executable
            return jvmFile.getAbsolutePath() + File.separator + "bin" + File.separator + "java";
        }
        return jvm;
    }

    protected File resolveSDK()
        throws MojoExecutionException
    {
        if ( sdkHome != null )
        {
            return sdkHome;
        }

        getSDKVersion();
        Artifact appEngineSDK =
            artifactFactory.createArtifactWithClassifier( "com.google.appengine", "appengine-java-sdk", sdkVersion,
                "zip", null );
        File repoLocation = new File( localRepository.getBasedir() + "/" + localRepository.pathOf( appEngineSDK ) );
        sdkHome = new File( repoLocation.getParentFile(), "appengine-java-sdk-" + sdkVersion );

        if ( sdkHome.exists() )
        {
            getLog().info( "SDK " + sdkVersion + " allready available in local repository" );
            return sdkHome;
        }

        try
        {
            getLog().info( "Resolve dependency for Google App Engine SDK version " + sdkVersion );
            resolver.resolve( appEngineSDK, remoteRepositories, localRepository );
            getLog().debug( "Unpack SDK in " + sdkHome );
            unpackSDK( appEngineSDK.getFile() );
        }
        catch ( Exception e )
        {
            getLog().error( "Failed to resolve AppEngine SDK for version " + sdkVersion, e );
            throw new MojoExecutionException( "Failed to resolve SDK" );
        }
        return sdkHome;
    }

    public String getSDKVersion()
        throws MojoExecutionException
    {
        if ( sdkHome != null )
        {
            // TODO detect SDK version ...
        }

        if ( sdkVersion == null )
        {
            detectVersionFromDependencies();
        }
        return sdkVersion;
    }

    private void detectVersionFromDependencies()
        throws MojoExecutionException
    {
        // detect form dependencies
        Collection<Artifact> artifacts = project.getArtifacts();
        for ( Artifact artifact : artifacts )
        {
            if ( GAE_GROUPID.equals( artifact.getGroupId() ) && GAE_ARTIFACTID.equals( artifact.getArtifactId() ) )
            {
                sdkVersion = artifact.getVersion();
                break;
            }
        }
        if ( sdkVersion == null )
        {
            if ( project.getDependencyManagement() != null
                && project.getDependencyManagement().getDependencies() != null )
            {
                Collection<Dependency> dependencyManagement = project.getDependencyManagement().getDependencies();
                for ( Dependency dependency : dependencyManagement )
                {
                    if ( GAE_GROUPID.equals( dependency.getGroupId() )
                        && GAE_ARTIFACTID.equals( dependency.getArtifactId() ) )
                    {
                        sdkVersion = dependency.getVersion();
                        break;
                    }
                }
            }
        }
        if ( sdkVersion == null )
        {
            throw new MojoExecutionException( "Cannot detect the Google App Engine SDK version to use" );
        }
    }

    private void unpackSDK( File zip )
        throws MojoExecutionException
    {
        try
        {
            UnArchiver unArchiver = archiverManager.getUnArchiver( zip );
            unArchiver.setSourceFile( zip );
            unArchiver.setDestDirectory( zip.getParentFile() );
            unArchiver.extract();
            unArchiver.setOverwrite( false );
            getLog().info( "Unpack Google App Engine SDK" );
        }
        catch ( Exception e )
        {
            getLog().error( "Failed to unpack Google App Engine SDK", e );
            throw new MojoExecutionException( "SDK setup failed" );
        }
    }

    public final void contextualize( Context context )
        throws ContextException
    {
        PlexusContainer plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
        try
        {
            archiverManager = (ArchiverManager) plexusContainer.lookup( ArchiverManager.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new ContextException( e.getMessage(), e );
        }

    }

    public MavenProject getProject()
    {
        return project;
    }

    /**
     * A plexus-util StreamConsumer to redirect messages to plugin log
     */
    protected StreamConsumer out = new StreamConsumer()
    {
        public void consumeLine( String line )
        {
            getLog().info( line );
        }
    };

    /**
     * A plexus-util StreamConsumer to redirect errors to plugin log
     */
    protected StreamConsumer err = new StreamConsumer()
    {
        public void consumeLine( String line )
        {
            getLog().error( line );
        }
    };

    public String getWebappDirectory()
    {
        return webappDirectory.getAbsolutePath();
    }

    public File getWebappDirectoryFile()
    {
        return webappDirectory;
    }
}
