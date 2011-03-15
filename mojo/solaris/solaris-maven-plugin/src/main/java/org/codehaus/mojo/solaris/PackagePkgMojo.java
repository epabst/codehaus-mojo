package org.codehaus.mojo.solaris;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Creates a System V package from a source directory by running <code>pkgmk</code> and <code>pkgtrans</code>.
 *
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @goal package
 */
public class PackagePkgMojo
    extends AbstractSolarisMojo
{
    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

    /**
     * The base directory for all command line executions and relative paths.
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * There directory where <code>pkgmk</code> and <code>pkgtrans</code> will be executed. All files that are to be
     * a part of the package has to be in this directory before the prototype file is generated.
     *
     * @parameter expression="${project.build.directory}/solaris/assembled-pkg"
     * @required
     */
    private File packageRoot;

    /**
     * The directory where all temporary files are placed.
     *
     * @parameter expression="${project.build.directory}/solaris"
     * @required
     * @readonly
     */
    private File solarisDir;

    /**
     * The resulting <code>pkg</code> file will be attached to this project as its primary artifact.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    // -----------------------------------------------------------------------
    // Mojo Implementation
    // -----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File prototypeFile = new File( packageRoot, "prototype" );
        File pkginfoFile = new File( packageRoot, "pkginfo" );

        if ( !prototypeFile.canRead() )
        {
            throw new MojoFailureException( "Could not read prototype file '" +
                prototypeFile.getAbsolutePath() + "'." );
        }

        if ( !pkginfoFile.canRead() )
        {
            throw new MojoFailureException( "Could not read pkginfo file '" + pkginfoFile.getAbsolutePath() + "'." );
        }

        String packageName = getPkgName( pkginfoFile );

        pkgmk( prototypeFile );

        File pkgFile = pkgtrans( packageName );

        project.getArtifact().setFile( pkgFile );
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private String getPkgName( File pkginfoFile )
        throws MojoExecutionException, MojoFailureException
    {
        FileInputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream( pkginfoFile );

            Properties properties = new Properties();
            properties.load( inputStream );

            String packageName = properties.getProperty( "PKG" );

            if ( packageName == null )
            {
                throw new MojoFailureException( "Could not read package name (PKG) from pkginfo file: '" +
                    pkginfoFile.getAbsolutePath() + "'." );
            }

            return packageName;
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error while reading pkginfo file." );
        }
        finally
        {
            IOUtil.close( inputStream );
        }
    }

    private void pkgmk( File prototypeFile )
        throws MojoFailureException, MojoExecutionException
    {
        Commandline commandline = new Commandline();
        commandline.setExecutable( "pkgmk" );
        commandline.setWorkingDirectory( basedir.getAbsolutePath() );

        // Overwrites the same instance; package instance will be overwritten if it already exists.
        commandline.createArgument().setValue( "-o" );
        commandline.createArgument().setValue( "-d" );
        // The additional slash is to make sure pkgmk writes to a directory and not a file
        commandline.createArgument().setValue( solarisDir.getAbsolutePath() );
        commandline.createArgument().setValue( "-f" );
        // I wonder why pkgmk need this reference -- trygve
        commandline.createArgument().setValue( prototypeFile.getAbsolutePath() );
        commandline.createArgument().setValue( "-r" );
        commandline.createArgument().setValue( packageRoot.getAbsolutePath() );

        execute( commandline, "pkgmk" );
    }

    private File pkgtrans( String packageName )
        throws MojoFailureException, MojoExecutionException
    {
        File pkgFile = new File( solarisDir, packageName + ".pkg" );

        Commandline commandline = new Commandline();
        commandline.setExecutable( "pkgtrans" );
        commandline.setWorkingDirectory( basedir.getAbsolutePath() );
        commandline.createArgument().setValue( "-s" );
        commandline.createArgument().setValue( "-o" );
        commandline.createArgument().setValue( solarisDir.getAbsolutePath() );
        commandline.createArgument().setValue( pkgFile.getAbsolutePath() );
        commandline.createArgument().setValue( packageName );

        execute( commandline, "pkgtrans" );

        return pkgFile;
    }

    private void execute( Commandline commandline, String programName )
        throws MojoFailureException, MojoExecutionException
    {
        PkgStreamConsumer streamConsumer = null;

        try
        {
            File outputFile = new File( solarisDir, programName + "-output.txt" );

            FileOutputStream fos = new FileOutputStream( outputFile );

            streamConsumer = new PkgStreamConsumer( fos );

            int exitValue;

            getLog().debug( "Executing: " + programName );
            getLog().debug( "Working directory: " + commandline.getWorkingDirectory() );
            getLog().debug( commandline.toString() );
            getLog().debug( "The output is in " + outputFile.getAbsolutePath() );

            exitValue = CommandLineUtils.executeCommandLine( commandline, streamConsumer, streamConsumer );

            if ( exitValue != 0 )
            {
                throw new MojoFailureException( programName + " " +
                    "returned a non-zero exit code. See the output file for output: " + outputFile.getAbsolutePath() );
            }
        }
        catch ( CommandLineException e )
        {
            throw new MojoExecutionException( "Error executing " + programName + " command.", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new MojoExecutionException( "Error while opening file.", e );
        }
        finally
        {
            IOUtil.close( streamConsumer );
        }
    }

    private class PkgStreamConsumer
        extends PrintWriter
        implements StreamConsumer
    {
        public PkgStreamConsumer( FileOutputStream fos )
        {
            super( fos );
        }

        public synchronized void consumeLine( String line )
        {
            println( line );
        }
    }
}
