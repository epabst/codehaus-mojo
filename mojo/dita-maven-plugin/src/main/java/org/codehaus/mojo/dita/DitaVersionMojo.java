package org.codehaus.mojo.dita;

/*
 * Copyright 2000-2006 The Apache Software Foundation
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Display DITA Open Toolkit's built-in version, with option to insert a custom version found under ${dita.dir}/ditaotVersionPath, via
 * <i>version</i> property to Maven's <i>${versionName}</i>. It is mainly used with maven-enforcer-plugin for build validation purpose.
 * 
 * @goal version
 * @requiresProject false
 * @phase validate
 */
public class DitaVersionMojo
    extends AbstractDitaMojo
{

    /**
     * Maven project property name to store custom version value
     * 
     * @parameter expression="${dita.versionName}" default-value="dita.custom.version"
     * @since 1.0-beta-1
     * 
     */
    private String versionName;

    /**
     * Display DITA Open Toolkit's built-in version
     * 
     * @parameter expression="${dita.displayDitaOTVersion}" default-value="true"
     * @since 1.0-beta-1
     * 
     */
    private boolean displayDitaOTVersion;
    
    /**
     * Relative path to DITA-OT custom version properties file path 
     * 
     * @parameter expression="${dita.displayDitaOTVersion}" default-value="custom.version.properties"
     * @since 1.0-beta-1
     * 
     */
    private String ditaotVersionPath;

    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
            return;
        }

        setupDitaDirectory();

        if ( displayDitaOTVersion )
        {
            this.displayDitaOTBuiltinVersion();
        }

        loadCustomVersion();
    }
    
    private void displayDitaOTBuiltinVersion()
      throws MojoExecutionException
    {
        Commandline cl = new Commandline( "java" );

        cl.setWorkingDirectory( project.getBasedir() );

        setupDitaMainClass( cl );

        setupDitaArguments( cl );

        setupClasspathEnv( cl );

        executeCommandline( cl );

    }

    private void loadCustomVersion()
        throws MojoExecutionException
    {
        String version = "UNKNOWN";

        InputStream is = null;

        try
        {
            File customVerionFile = new File( this.ditaDirectory, ditaotVersionPath );
            is = new FileInputStream( customVerionFile );
            Properties props = new Properties();
            props.load( is );

            if ( props.get( "version" ) != null )
            {
                version = (String) props.get( "version" );
            }
        }
        catch ( FileNotFoundException e )
        {

        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( is );
        }

        this.getLog().debug( "Set " + this.versionName + " to " + version );
        project.getProperties().put( this.versionName, version );

    }

    private void setupDitaArguments( Commandline cl )
        throws MojoExecutionException
    {
        cl.createArg().setValue( "-version" );
        cl.createArg().setValue( "/ditadir:" + this.ditaDirectory );
    }

}
