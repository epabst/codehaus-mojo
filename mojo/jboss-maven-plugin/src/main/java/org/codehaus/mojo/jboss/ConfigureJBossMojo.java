package org.codehaus.mojo.jboss;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Allows you to configure JBoss installation by overlaying a conf, lib and deploy directory.
 * 
 * @goal configure
 */
public class ConfigureJBossMojo
    extends AbstractJBossServerMojo
{
    private static final String SERVER_DIR_NAME = "server";

    /**
     * The directory for overrides to the conf directory.
     * 
     * @parameter default-value="${basedir}/jboss/conf"
     */
    private File confDir;

    /**
     * The directory for overrides to the lib directory.
     * 
     * @parameter default-value="${basedir}/jboss/lib"
     */
    private File libDir;

    /**
     * The directory for overrides to the deploy directory.
     * 
     * @parameter default-value="${basedir}/jboss/deploy"
     */
    private File deployDir;

    /**
     * @parameter default-value="${project.build.directory}/jboss"
     */
    protected File outputDirectory;

    /**
     * @parameter expression="${jboss.javaOpts}"
     */
    protected String javaOpts;

    /**
     * The set of options to pass to the JBoss "run" command.
     * 
     * @parameter expression="${jboss.options}"
     */
    protected String options;

    /**
     * Main plugin execution.
     * 
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        checkConfig();
        checkJBossHome();

        File serverDir = new File( outputDirectory.getAbsolutePath() + File.separator + serverName );

        checkOutputDirectory( serverDir );
        copyBaseConfDir( serverDir );
        copyBaseDeployDir( serverDir );
        copyBaseLibDir( serverDir );
        overlayConfDir( serverDir );
        overlayDeployDir( serverDir );
        overlayLibDir( serverDir );
        buildBinDir( serverDir );
    }

    /**
     * Check that jboss home is configured correctly.
     * 
     * @throws MojoFailureException
     */
    private void checkJBossHome()
        throws MojoFailureException
    {
        if ( !jbossHome.exists() )
        {
            throw new MojoFailureException( "The jbossHome specifed does not exist." );
        }

        File serverParentDir = new File( jbossHome.getAbsolutePath() + File.separator + SERVER_DIR_NAME );

        if ( !serverParentDir.exists() )
        {
            throw new MojoFailureException( jbossHome.getAbsolutePath() + " does not appear to be a valid jboss home" );
        }

        File serverDir = new File( serverParentDir.getAbsolutePath() + File.separator + serverName );

        if ( !serverDir.exists() )
        {
            throw new MojoFailureException( serverName + " is not a valid server in "
                + serverParentDir.getAbsolutePath() );
        }
    }

    /**
     * Check that the output directory is configured.
     * 
     * @param serverDir
     * @throws MojoFailureException
     */
    private void checkOutputDirectory( File serverDir )
        throws MojoFailureException
    {
        if ( outputDirectory == null )
        {
            throw new MojoFailureException( "I don't know how you did it, but the outputDirectory is null" );
        }

        if ( !outputDirectory.exists() )
        {
            outputDirectory.mkdirs();
        }

        if ( !serverDir.exists() )
        {
            serverDir.mkdir();
        }
    }

    /**
     * Copy conf.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void copyBaseConfDir( File serverDir )
        throws MojoExecutionException
    {
        copyBaseDir( serverDir, "conf" );
    }

    /**
     * Copy lib dir.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void copyBaseLibDir( File serverDir )
        throws MojoExecutionException
    {
        copyBaseDir( serverDir, "lib" );
    }

    /**
     * Copy deploy dir.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void copyBaseDeployDir( File serverDir )
        throws MojoExecutionException
    {
        copyBaseDir( serverDir, "deploy" );
    }

    /**
     * Copy base dir.
     * 
     * @param serverDir
     * @param dirName
     * @throws MojoExecutionException
     */
    private void copyBaseDir( File serverDir, String dirName )
        throws MojoExecutionException
    {
        File baseDir =
            new File( jbossHome + File.separator + SERVER_DIR_NAME + File.separator + serverName + File.separator
                + dirName );
        File dir = new File( serverDir.getAbsolutePath() + File.separator + dirName );
        try
        {
            FileUtils.copyDirectoryStructure( baseDir, dir );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could Not Copy " + dirName + " Dir", e );
        }
    }

    /**
     * Overlay conf dir.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void overlayConfDir( File serverDir )
        throws MojoExecutionException
    {
        overLayDir( serverDir, confDir, "conf" );
    }

    /**
     * Overlay deploy dir.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void overlayDeployDir( File serverDir )
        throws MojoExecutionException
    {
        overLayDir( serverDir, deployDir, "deploy" );
    }

    /**
     * Overlay lib dir.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void overlayLibDir( File serverDir )
        throws MojoExecutionException
    {
        overLayDir( serverDir, libDir, "lib" );
    }

    /**
     * Overlay dir.
     * 
     * @param serverDir
     * @param overLayDir
     * @param dirName
     * @throws MojoExecutionException
     */
    private void overLayDir( File serverDir, File overLayDir, String dirName )
        throws MojoExecutionException
    {
        File baseDir = new File( serverDir + File.separator + dirName );
        if ( overLayDir.exists() )
        {
            try
            {
                FileUtils.copyDirectoryStructure( overLayDir, baseDir );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Could not overlay " + dirName + "Dir", e );
            }
        }
    }

    /**
     * Build the bin dir.
     * 
     * @param serverDir
     * @throws MojoExecutionException
     */
    private void buildBinDir( File serverDir )
        throws MojoExecutionException
    {
        File binDir = new File( outputDirectory.getAbsolutePath() + File.separator + "bin" );

        if ( !binDir.exists() )
        {
            binDir.mkdirs();
        }

        VelocityEngine engine = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty( "resource.loader", "class" );
        p.setProperty( "class.resource.loader.class", ClasspathResourceLoader.class.getName() );
        try
        {
            engine.init( p );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Problem creating initting velcoity engine", e );
        }

        VelocityContext context = new VelocityContext();
        context.put( "jbossServerHome", serverDir.getAbsolutePath() );
        context.put( "jbossHome", jbossHome.getAbsolutePath() );
        context.put( "serverName", serverName );
        context.put( "options", options == null ? "" : options  );
        context.put( "javaOpts", javaOpts == null ? "" : javaOpts );

        String osName = System.getProperty( "os.name" );

        if ( osName.startsWith( "Windows" ) )
        {
            buildWindowsScripts( binDir, engine, context );
        }
        else
        {
            buildUnixScipts( binDir, engine, context );
        }
    }

    private void buildUnixScipts( File binDir, VelocityEngine engine, VelocityContext context )
        throws MojoExecutionException
    {
        File runScript = new File( binDir + File.separator + "run.sh" );
        File shutdownScript = new File( binDir + File.separator + "shutdown.sh" );

        try
        {
            Runtime runtime = Runtime.getRuntime();

            Template runTemplate = engine.getTemplate( "run.sh.vm" );
            FileWriter fileWriter = new FileWriter( runScript );
            runTemplate.merge( context, fileWriter );
            fileWriter.flush();

            String command = "chmod 755 " + runScript.getAbsolutePath();
            Process process = runtime.exec( command );

            Template shutdownTemplate = engine.getTemplate( "shutdown.sh.vm" );
            fileWriter = new FileWriter( shutdownScript );
            shutdownTemplate.merge( context, fileWriter );
            fileWriter.flush();

            command = "chmod 755 " + shutdownScript.getAbsolutePath();
            process = runtime.exec( command );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Problem generating scripts", e );
        }
    }

    private void buildWindowsScripts( File binDir, VelocityEngine engine, VelocityContext context )
        throws MojoExecutionException
    {
        File runScript = new File( binDir + File.separator + "run.bat" );
        File shutdownScript = new File( binDir + File.separator + "shutdown.bat" );

        try
        {
            Template runTemplate = engine.getTemplate( "run.bat.vm" );
            FileWriter fileWriter = new FileWriter( runScript );
            runTemplate.merge( context, fileWriter );
            fileWriter.flush();

            Template shutdownTemplate = engine.getTemplate( "shutdown.bat.vm" );
            fileWriter = new FileWriter( shutdownScript );
            shutdownTemplate.merge( context, fileWriter );
            fileWriter.flush();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Problem generating scripts", e );
        }
    }
}
