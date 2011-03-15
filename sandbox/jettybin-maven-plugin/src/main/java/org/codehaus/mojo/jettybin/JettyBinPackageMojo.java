package org.codehaus.mojo.jettybin;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.digest.Sha1Digester;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * JettyBinMojo 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @goal package
 * @phase package
 * @requiresDependencyResolution compile
 */
public class JettyBinPackageMojo
    extends AbstractMojo
{
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${jettybin.outputDirectory}" default-value="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter expression="${jettybin.workDirectory}" default-value="${project.build.directory}/jettybin"
     * @required
     */
    private File workDirectory;

    /**
     * @parameter expression="${jettybin.installPrefix}" default-value="${project.build.finalName}"
     * @required
     */
    private String webappName;

    /**
     * Single directory for extra files to include in the jetty distribution.
     *
     * @parameter expression="${basedir}/src/main/jetty"
     * @required
     */
    private File jettySourceDirectory;

    /**
     * @parameter expression="${jettybin.useTimestamp}" default-value="false"
     * @required
     */
    private boolean useTimestamp;

    /**
     * @parameter expression="${jettybin.timestampPattern}" default-value="yyyyMMdd"
     * @required
     */
    private String timestampPattern;

    /**
     * @parameter expression="${jettybin.archiveFormat}" default-value="tar.gz"
     * @required
     */
    private String archiveFormat;

    /**
     * @parameter expression="${jettybin.classifier}" default-value="jetty"
     * @required
     */
    private String classifier;

    /**
     * To look up Archiver/UnArchiver implementations
     *
     * @component
     * @required
     * @readonly
     */
    protected ArchiverManager archiverManager;

    /**
     * Maven ProjectHelper
     *
     * @component
     */
    private MavenProjectHelper projectHelper;

    private Sha1Digester sha1Digester = new Sha1Digester();

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Artifact warArtifact = findWarArtifact();

        if ( warArtifact == null )
        {
            getLog().warn( "Not processing jettybin:package - no war dependency found." );
            return;
        }

        if ( StringUtils.isEmpty( classifier ) )
        {
            throw new MojoFailureException( "Unable to execute jettybin:package with empty classifier." );
        }

        try
        {
            JettyIOUtil.ensureDirectoryExists( workDirectory );

            String installPrefix = project.getArtifactId() + "-" + project.getVersion() + "-" + classifier;

            File tarDirectory = new File( workDirectory, installPrefix );

            JettyIOUtil.ensureDirectoryExists( tarDirectory );

            copyJettyToWorkDir( tarDirectory );

            File webappDirectory = new File( tarDirectory, "webapps/" + webappName );
            JettyIOUtil.ensureDirectoryExists( webappDirectory );

            explodeWarToWebappDir( warArtifact.getFile(), webappDirectory );

            copyCustomJettyFiles( tarDirectory );

            StringBuffer tarfilename = new StringBuffer();
            tarfilename.append( installPrefix );
            if ( useTimestamp )
            {
                SimpleDateFormat format = new SimpleDateFormat( timestampPattern );
                tarfilename.append( "-" );
                String now = format.format( new Date() );
                tarfilename.append( now );
                classifier = classifier + "-" + now;
            }
            tarfilename.append( "." ).append( archiveFormat );

            File tarball = new File( outputDirectory, tarfilename.toString() );

            createTarball( tarDirectory, tarball );

            projectHelper.attachArtifact( project, ArchiverUtil.getFileExtention( tarball ), classifier, tarball );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to process jettybin:package goal due to IOException.", e );
        }
    }

    private void createTarball( File tarDirectory, File tarball )
        throws MojoExecutionException
    {
        try
        {
            Archiver archiver = ArchiverUtil.createArchiver( archiverManager, tarball );

            archiver.setDestFile( tarball );
            archiver.addDirectory( tarDirectory.getParentFile(), new String[] { tarDirectory.getName() + "/**" }, null );

            archiver.createArchive();
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "Unable to create archiver for " + tarball.getAbsolutePath(), e );
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "Unable to configure archiver for " + tarball.getAbsolutePath(), e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create " + tarball.getAbsolutePath(), e );
        }
    }

    private void copyCustomJettyFiles( File tarDirectory )
        throws MojoExecutionException
    {
        if ( jettySourceDirectory.exists() )
        {
            getLog().info( "Copying custom jetty files." );
            List fileList = JettyIOUtil.getFileList( jettySourceDirectory, "**/*", null );

            Iterator it = fileList.iterator();
            while ( it.hasNext() )
            {
                String filename = (String) it.next();
                File file = new File( jettySourceDirectory, filename );
                File outputDir = new File( tarDirectory, FileUtils.dirname( filename ) );

                try
                {
                    JettyIOUtil.ensureDirectoryExists( outputDir );
                    getLog().debug( "Copying " + filename );
                    FileUtils.copyFileToDirectory( file, outputDir );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Unable to copy file " + filename + " to "
                        + outputDir.getAbsolutePath() );
                }
            }
        }
    }

    public void explodeWarToWebappDir( File warFile, File webappDirectory )
        throws MojoExecutionException
    {
        getLog().info( "Unpacking project WAR file work directory." );
        try
        {
            UnArchiver unArchiver;

            unArchiver = archiverManager.getUnArchiver( warFile );

            unArchiver.setSourceFile( warFile );

            unArchiver.setDestDirectory( webappDirectory );

            unArchiver.extract();
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "Unknown archiver type", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error unpacking war file: " + warFile + "to: " + webappDirectory, e );
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "Error unpacking war file: " + warFile + "to: " + webappDirectory, e );
        }
    }

    public Artifact findWarArtifact()
        throws MojoExecutionException
    {
        Set dependencies = project.getDependencyArtifacts();
        Artifact warArtifact = null;
        int warDepCount = 0;

        Iterator it = dependencies.iterator();
        while ( it.hasNext() )
        {
            Artifact artifact = (Artifact) it.next();
            if ( StringUtils.equals( "war", artifact.getType() ) )
            {
                warArtifact = artifact;
                warDepCount++;
            }
        }

        if ( warDepCount > 1 )
        {
            throw new MojoExecutionException( "Unable to process more than 1 war dependency." );
        }

        return warArtifact;
    }

    private void copyJettyToWorkDir( File destDir )
        throws MojoFailureException, MojoExecutionException
    {
        getLog().info( "Copying jetty boilerplate files to work directory." );

        URL jettyFilesResource = this.getClass().getResource( "/jetty-files.sha1" );
        if ( jettyFilesResource == null )
        {
            throw new MojoFailureException( "Resource not found /jetty-files.sha1" );
        }

        List jettyFileList = new ArrayList();

        // Parse jetty-files.sha1 file.
        try
        {
            InputStream is = jettyFilesResource.openStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );

            String line = reader.readLine();
            while ( line != null )
            {
                if ( StringUtils.isNotEmpty( line ) )
                {
                    jettyFileList.add( JettyResource.parseRawJettyResourceLine( line ) );
                }
                line = reader.readLine();
            }
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "Unable to parse /jetty-files.txt resource." );
        }

        // Copy and verify files from jetty-files.sha1
        Iterator it = jettyFileList.iterator();
        while ( it.hasNext() )
        {
            JettyResource jettyres = (JettyResource) it.next();

            try
            {
                jettyres.copyTo( destDir, sha1Digester );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Unable to copy resource " + jettyres.getName() + " to output file "
                    + destDir.getAbsolutePath(), e );
            }
        }
    }
}
