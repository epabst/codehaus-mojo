package org.codehaus.mojo.pde;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.pde.descriptor.DescriptorUtil;
import org.codehaus.mojo.pde.descriptor.FeatureDescriptor;
import org.codehaus.mojo.pde.descriptor.ProductDescriptor;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Build PDE artifact. The output is not deployable.
 * 
 * @goal pde
 * @phase compile
 * @requiresProject false
 * @aggregator
 * @requiresDependencyResolution test
 * 
 * @version $Id:$
 * @author dtran@gmail.com
 */

public class EclipsePDEMojo
    extends AbstractEclipsePDEMojo
{
    /**
     * PDE Ant build temporary directory
     * 
     * @parameter expression="${pdeTempDirectory}"
     *            default-value="${project.build.directory}/pdeTemp"
     * 
     * This param should be moved to systemProperties, but due a bug in PDE Ant build, we must
     * specify default value
     */
    protected File pdeBuildTempFolder;

    /**
     * Additional system properties to be passed into PDE ant build. See the generated build.xml
     * files for list of overrideable properties. javacFailOnError=true is injected automatically if
     * not found.
     * 
     * @parameter
     */

    private Map buildProperties = new Properties();

    /**
     * Additional system properties to be passed into PDE eclipse.buildScript task which is use to
     * generete PDE Ant build files. buildDirectory and baseLocation are automatically injected if
     * not set.
     * 
     * @parameter
     */
    private Map eclipseBuildScriptProperties = new Properties();

    /**
     * {@inheritDoc}
     */
    protected void initialize()
        throws MojoExecutionException, MojoFailureException
    {
        super.initialize();
    }

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        if ( pdeProductFilename == null )
        {
            this.executeCommandLine( this.createBootstrapCommandLine() );
        }

        this.executeCommandLine( this.createBuildCommandLine() );

    }

    /**
     * @return a command line for building the PDE project.
     * @throws MojoExecutionException build failures.
     */
    protected Commandline createBuildCommandLine()
        throws MojoExecutionException
    {
        Commandline cl = null;

        if ( pdeProductFilename != null )
        {
            cl = createProductBuildCommandLine();
        }
        else
        {
            cl = createFeatureBuildCommandLine();
        }

        cl.createArgument().setLine( "-DbuildTempFolder=" + this.pdeBuildTempFolder );

        if ( this.buildProperties.get( "javacFailOnError" ) == null )
        {
            this.buildProperties.put( "javacFailOnError", "true" );
        }

        Iterator iter = this.buildProperties.entrySet().iterator();

        while ( iter.hasNext() )
        {
            String arg = iter.next().toString();

            cl.createArgument().setLine( "-D" + arg );
        }

        return cl;
    }

    /**
     * Create a command line for a product build.
     * 
     * See
     * http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_product_build.htm
     * for more details.
     * 
     * @return a command line for building the product.
     * @throws MojoExecutionException build failures.
     */
    private Commandline createProductBuildCommandLine()
        throws MojoExecutionException
    {
        File buildFile =
            new File( eclipseInstall, "plugins/org.eclipse.pde.build_" + pdeBuildVersion
                + "/scripts/productBuild/productBuild.xml" );

        Commandline cl = createCommandLine( buildFile );

        File buildConfigDirectory = new File( pdeDirectory, pdeBuildConfigDirectory );

        try
        {
            cl.createArgument().setLine( "-Dbuilder=" + buildConfigDirectory.getCanonicalPath() );
            cl.createArgument().setLine( "-DbuildDirectory=" + this.getPDEBuildDirectory().getPath() );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "createProductBuildCommandLine failed: ", e );
        }

        return cl;
    }

    /**
     * Create a command line for a feature build (plug-ins and fragments are also feature builds).
     * See
     * http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_plugins_build.htm
     * for more details.
     * 
     * @return a command line for building the feature
     */
    private Commandline createFeatureBuildCommandLine()
    {
        Commandline cl = this.createCommandLine( new File( this.pdeDirectory, "build.xml" ), this.getAntTarget() );

        return cl;
    }

    /**
     * Return the file that the PDE Ant build creates. Note that a PDE product build can create a
     * zip file for each config that is specified, however this mojo does not support that
     * situation.
     * 
     * The product file from a PDE build is located at
     * ${buildDirectory}/${buildLabel}/${buildId}-${configs}.zip.
     * 
     * However the buildConfiguration/build.properties file does not specify the buildDirectory as
     * this is overriden by the Mojo to always be "../..".
     * 
     * <b>Note:</b> A normal pde build supports multiple configs to be built. This maven mojo only
     * supports <b>ONE</b> config to be built. Otherwise attaching the generated artifacts becomes
     * difficult, as which one is the main build and which ones are the extras with classifiers?
     * 
     * @return the output of the PDE Ant build.
     * @throws MojoExecutionException build failures.
     */
    protected File locateAntOutputFile()
        throws MojoExecutionException
    {
        if ( this.descriptor instanceof ProductDescriptor )
        {
            String config = convertPdeConfigsToFilenameSuffix( buildConfigurationProperties.getString( "configs" ) );
            return new File( getPDEBuildDirectory(), buildConfigurationProperties.getString( "buildLabel" ) + "/"
                + buildConfigurationProperties.getProperty( "buildId" ) + "-" + config + ".zip" );
        }
        if ( this.descriptor instanceof FeatureDescriptor )
        {
            return new File( this.pdeDirectory, this.descriptor.getId() + "_" + this.descriptor.getVersion()
                + ".bin.dist.zip" );
        }
        else
        {
            return new File( pdeDirectory, descriptor.getId() + "_" + descriptor.getVersion() + ".zip" );
        }
    }

    /**
     * 
     * @return Ant targets to do the build depending PDE type
     */
    private String getAntTarget()
    {
        String targets = "clean build.jars zip.plugin";

        if ( this.descriptor instanceof FeatureDescriptor )
        {
            targets = "clean build.jars zip.distribution";
        }

        return targets;
    }

    /**
     * 
     * @return a generated bootstrap Ant build.xml so that it can be used to generate neccessary ant
     *         build files to build the PDE project
     * @throws MojoExecutionException build failures.
     */
    private File createBootstrapAntFile()
        throws MojoExecutionException
    {
        File bootstrapFile = new File( this.project.getBuild().getDirectory() + "/pdeBuilder/build.xml" );
        bootstrapFile.getParentFile().mkdirs();

        if ( this.eclipseBuildScriptProperties.get( "buildDirectory" ) == null )
        {
            this.eclipseBuildScriptProperties.put( "buildDirectory", this.getPDEBuildDirectory().getPath() );
        }

        if ( this.eclipseBuildScriptProperties.get( "baseLocation" ) == null )
        {
            this.eclipseBuildScriptProperties.put( "baseLocation", this.getBaseLocation().getPath() );
        }

        try
        {
            PrintStream out = new PrintStream( new FileOutputStream( bootstrapFile ) );

            out.println();
            out.println( "<project default=\"generate\" > " );
            out.println( "  <target name=\"generate\" >" );
            out.println( "    <eclipse.buildScript " );
            out.println( "      elements=\"" + DescriptorUtil.getPDEType( descriptor ) + "@" + this.descriptor.getId()
                + "\"" );

            Iterator iter = this.eclipseBuildScriptProperties.keySet().iterator();
            while ( iter.hasNext() )
            {
                String key = iter.next().toString();
                String value = (String) this.eclipseBuildScriptProperties.get( key );
                out.println( "      " + key + "=\"" + value + "\"" );
            }

            out.println( "    />" );
            out.println( "  </target>" );
            out.println( "</project>" );

            out.close();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error generating bootstrap file: " + bootstrapFile );
        }

        return bootstrapFile;
    }

    /**
     * Returns a command for creating a bootstrap ant file. The bootratp ant file is then used to
     * create the build ant file for building the plugin or feature. A product does not require
     * bootstrapping and will cause a build failure if one is attempted to be created.
     * 
     * @return a command to create a bootstrap ant file
     * @throws MojoExecutionException build failures.
     */
    protected Commandline createBootstrapCommandLine()
        throws MojoExecutionException
    {
        if ( pdeProductFilename != null )
        {
            throw new MojoExecutionException( "A Product Build does not require bootstrapping." );
        }
        File bootstrapFile = this.createBootstrapAntFile();
        return this.createCommandLine( bootstrapFile );
    }
}
