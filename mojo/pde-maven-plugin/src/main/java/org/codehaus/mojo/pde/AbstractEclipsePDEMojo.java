package org.codehaus.mojo.pde;

/*
 * Copyright 2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.pde.descriptor.Descriptor;
import org.codehaus.mojo.pde.descriptor.DescriptorUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

/**
 * Base class of all Eclispe PDE mojos
 * 
 * @author dtran@gmail.com
 * @version $Id:$
 */

public abstract class AbstractEclipsePDEMojo
    extends AbstractMojo
{

    /**
     * A pde build.properties ${configs} must contain this many comma separated
     * fields.
     */
    private static final int CONFIG_TUPLE = 3;

    /**
     * The POM
     * 
     * @parameter expression="${project}
     * @required
     * @readonly
     */

    protected MavenProject project;

    /**
     * Contains the PDE project source
     * 
     * @parameter expression="${pdeDirectory}"
     *            default-value="${project.basedir}"
     * @required
     * @readonly
     */
    protected File pdeDirectory;

    /**
     * Build a product instead of a feature.
     * 
     * If defined then a PDE Product will be built (instead of the feature,
     * fragment or plug-in). This value defines the product file that will be
     * used to build the product.
     * 
     * @parameter expression="${pdeProductFilename}"
     * @optional
     */
    protected String pdeProductFilename;

    /**
     * When a product build is run then use this version of the pde build
     * scripts. The build scripts are located at
     * ${eclipseInstall}/plugins/org.eclipse.pde.build_{pdeBuildVersion}/scripts/productBuild
     * 
     * @parameter expression="${pdeBuildVersion}"
     * @optional
     */
    protected String pdeBuildVersion;

    /**
     * When a product build is run, then the location of the PDE build
     * configuration directory must be specified. A product build will expect a
     * build.properties file to exist in this directory. You can use
     * ${eclipseInstall}/plugins/org.eclipse.pde.build_{pdeBuildVersion}/templates/headless-build/build.properties
     * as a starter file. See
     * http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_product_build.htm
     * Building an RCP application from a product configuration file for more
     * details.
     * 
     * @parameter expression="${pdeBuildConfigDirectory}"
     *            default-value="buildConfiguration"
     * @optional
     */
    protected String pdeBuildConfigDirectory;

    /**
     * Eclipse Platform SDK Directory
     * 
     * @parameter expression="${eclipseInstall}" default-value="c:/eclipse"
     * @required
     */

    protected File eclipseInstall;

    /**
     * Ant debug capability
     * 
     * @parameter expression="${antDebug}" default-value="false"
     */

    protected boolean antDebug;

    /**
     * Ant verbose capability
     * 
     * @parameter expression="${antVerbose}" default-value="false"
     */

    protected boolean antVerbose;

    // ////////////////////////////////////////////////////////////////////
    // Other internal properties
    // ////////////////////////////////////////////////////////////////////

    /**
     * The pde descriptor for the pde project.
     */
    protected Descriptor descriptor;

    /**
     * The eclipse startup.jar file.
     */
    private File startupJar;
    
    /**
     * startup class in the startup jar
     */
    private String startupClass = "org.eclipse.core.launcher.Main";

    /**
     * The pdeDirectory/buildConfiguraion/build.properties file.
     */
    PropertiesConfiguration buildConfigurationProperties = null;

    /**
     * Execute the Mojo
     * 
     * @throws MojoExecutionException
     *             build failure
     * @throws MojoFailureException
     *             build failure
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        initialize();
    }

    /**
     * Initialise the mojo.
     * 
     * @throws MojoExecutionException
     *             build failure
     * @throws MojoFailureException
     *             build failure
     */
    protected void initialize()
        throws MojoExecutionException, MojoFailureException
    {

        if ( this.pdeDirectory == null )
        {
            this.pdeDirectory = new File( System.getProperty( "user.dir" ) );
        }

        this.startupJar = this.findStartupJar();

        if ( !startupJar.exists() )
        {
            throw new MojoExecutionException( startupJar.getPath()
                + " not found.  Have you set up your -DeclipseInstall?" );
        }
        
        this.getLog().info( "Startup jar found at: " + this.startupJar );

        if ( pdeProductFilename != null )
        {
            if ( pdeBuildVersion == null )
            {
                throw new MojoExecutionException( "pdeBuildVersion must be specified for a product build." );
            }
            // Fail fast on build.properties missing.
            loadBuildConfigurationProperties();
        }

        this.descriptor = DescriptorUtil.getDescriptor( this.pdeDirectory, pdeProductFilename );

        // TODO check for empty id

    }
    
    protected File findStartupJar()
    {
        File startupJar = new File( this.eclipseInstall, "startup.jar" );
        
        if ( !startupJar.exists() )
        {
            // list all plugins and try to found a plugin that mathches
            // org.eclipse.equinox.launcher_*.jar
            // @TODO implement a mechanism to select the newer plugin

            File[] plugins = new File( eclipseInstall.getAbsolutePath() + "/plugins/" ).listFiles();
            for ( int i = 0; i < plugins.length; i++ )
            {
                if ( plugins[i].isFile() && plugins[i].getName().indexOf( "org.eclipse.equinox.launcher" ) >= 0 )
                {
                    startupJar = plugins[i];
                    startupClass = "org.eclipse.equinox.launcher.Main";
                    break;
                }
            }
        }
        
        return startupJar;
        
    }

    /**
     * Execute the specified Commandline
     * 
     * @param cl
     *            the Commandline to execute
     * @throws MojoExecutionException
     *             build failures
     */
    protected void executeCommandLine( Commandline cl )
        throws MojoExecutionException
    {
        int ok;

        try
        {
            DefaultConsumer stdout = new DefaultConsumer();

            DefaultConsumer stderr = stdout;

            this.getLog().info( cl.toString() );

            ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch ( CommandLineException e )
        {
            throw new MojoExecutionException( "Error executing PDE build command line.", e );
        }

        /* return code 13 from PDE means the real error is in the build log file */
        if ( ok == 13 )
        {
            throw new MojoExecutionException( "Error returned by PDE build. The cause for this error should be found within the PDE build logfile.");
        }
        
        /* otherwise something else went pear shaped */
        if ( ok != 0 )
        {
            throw new MojoExecutionException( "Error returned by PDE build. Exit code: " + ok );
        }
    }

    /**
     * Create a command line to invoke an Ant build file using the Eclipse pde
     * environment.
     * 
     * @param buildFile
     *            the Ant buildfile to use as part of the command
     * @param targets
     *            the targets to invoke on the Ant build file
     * @return A command line to invoke the targets on the Ant buildfile
     */
    protected Commandline createCommandLine( File buildFile, String targets )
    {
        Commandline cl = new Commandline();

        cl.setWorkingDirectory( this.pdeDirectory.getPath() );

        cl.setExecutable( "java" );

        cl.createArgument().setValue( "-classpath" );

        cl.createArgument().setFile( this.startupJar );

        cl.createArgument().setValue( this.startupClass );

        cl.createArgument().setValue( "-application" );
        cl.createArgument().setValue( "org.eclipse.ant.core.antRunner" );

        cl.createArgument().setValue( "-buildfile" );
        cl.createArgument().setValue( buildFile.toString() );

        if ( targets != null )
        {
            cl.createArgument().setLine( targets );
        }

        if ( this.antVerbose )
        {
            cl.createArgument().setValue( "-verbose" );
        }

        if ( this.antDebug )
        {
            cl.createArgument().setValue( "-debug" );
        }

        return cl;

    }

    /**
     * Create a command line to invoke an Ant build file with the default target
     * 
     * @param buildFile
     *            the Ant buildfile to use as part of the command
     * @return A command line to invoke the targets on the Ant buildfile
     */
    protected Commandline createCommandLine( File buildFile )
    {
        return this.createCommandLine( buildFile, null );
    }

    /**
     * The PDE Build Directory, which is always located two directories higher
     * than the pdeDirectory (i.e "../../${pdeDirectory")
     * 
     * @return The PDE Build Directory
     */
    protected File getPDEBuildDirectory()
    {
        return new File( this.pdeDirectory, "../.." );
    }

    /**
     * @return The Eclipse Base Location.
     */
    protected File getBaseLocation()
    {
        return this.eclipseInstall;
    }

    /**
     * Load the properties from pdeBuildConfiguration\build.properties.
     * 
     * @return the properties from pdeBuildConfiguration\build.properties.
     * @throws MojoExecutionException
     *             build failures.
     */
    protected PropertiesConfiguration loadBuildConfigurationProperties()
        throws MojoExecutionException
    {
        if ( buildConfigurationProperties != null )
        {
            return buildConfigurationProperties;
        }

        File buildPropertiesFile = new File( pdeDirectory, pdeBuildConfigDirectory + "/build.properties" );
        try
        {
            buildConfigurationProperties = new PropertiesConfiguration();
            buildConfigurationProperties.setDelimiterParsingDisabled( true );
            buildConfigurationProperties.load( buildPropertiesFile );

            return buildConfigurationProperties;
        }
        catch ( ConfigurationException e )
        {
            throw new MojoExecutionException( "Failed to load pde build.properties from "
                + buildPropertiesFile.getPath(), e );
        }
    }

    /**
     * Convert the list of {os, ws, arch} configurations to build as contained
     * in the build.properties file ${configs} field into the filename suffix
     * for the created artifact. *
     * 
     * <b>Note</b> PDE Build support multiple configs, here only one config is
     * supported.
     * 
     * @param configs
     *            the string form of the configs
     * @return the filename suffix for the configs
     * @throws MojoExecutionException
     *             build failures.
     */
    protected String convertPdeConfigsToFilenameSuffix( String configs )
        throws MojoExecutionException
    {
        if ( StringUtils.isEmpty( configs ) )
        {
            throw new MojoExecutionException( "Null pde configs can not be converted to filename suffix" );
        }
        if ( configs.indexOf( "&" ) > 0 )
        {
            throw new MojoExecutionException( "Pde build only supports a build with a single config only." );
        }
        String[] strings = StringUtils.split( configs, "," );
        if ( strings.length != CONFIG_TUPLE )
        {
            throw new MojoExecutionException( "Invalid PDE Config \"" + configs
                + "\": must be of the form: os, ws, arch" );
        }
        for ( int i = 0; i < strings.length; i++ )
        {
            strings[i] = strings[i].trim();
        }
        return StringUtils.join( strings, "." );
    }

}
