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

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.io.File;
import java.io.IOException;

/**
 * This class provides the general functionality for interacting with a local JBoss server.
 */
public abstract class AbstractJBossServerMojo
    extends AbstractMojo
{

    /**
     * The location of JBoss Home. This is a required configuration parameter (unless JBOSS_HOME is set).
     * 
     * @parameter expression="${env.JBOSS_HOME}"
     * @required
     */
    protected File jbossHome;

    /**
     * The name of the configuration profile to use when starting the server. This might be something like "all",
     * "default", or "minimal".
     * 
     * @parameter default-value="default" expression="${jboss.serverName}"
     */
    protected String serverName;

    /**
     * The Maven Wagon manager to use when obtaining server authentication details.
     * 
     * @component role="org.apache.maven.artifact.manager.WagonManager"
     */
    private WagonManager wagonManager;

    /**
     * The id of the server configuration found in Maven settings.xml. This configuration will determine the
     * username/password to use when authenticating with the JBoss server. If no value is specified, a default username
     * and password will be used.
     * 
     * @parameter expression="${jboss.serverId}"
     */
    private String serverId;

    /**
     * Check that JBOSS_HOME is correctly configured.
     * 
     * @throws MojoExecutionException
     */
    protected void checkConfig()
        throws MojoExecutionException
    {
        getLog().debug( "Using JBOSS_HOME: " + jbossHome );
        if ( jbossHome == null )
        {
            throw new MojoExecutionException( "Neither environment JBOSS_HOME nor the jbossHome parameter is set!" );
        }
        if ( !jbossHome.isDirectory() )
        {
            throw new MojoExecutionException( "Configured JBoss home directory does not exist: " + jbossHome );
        }
    }

    /**
     * Call the JBoss startup or shutdown script.
     * 
     * @param commandName - The name of the command to run
     * @param options - Space separated command line parameters
     * @throws MojoExecutionException
     */
    protected void launch( String commandName, String options )
        throws MojoExecutionException
    {
        checkConfig();

        String osName = System.getProperty( "os.name" );
        String commandExt = osName.startsWith( "Windows" ) ? ".bat" : ".sh";
        String jbossCommand = commandName + commandExt;
        File jbossHomeBin = new File( jbossHome, "bin" );
        File jbossCommandFile = new File( jbossHomeBin, jbossCommand );

        if ( !jbossCommandFile.isFile() )
        {
            throw new MojoExecutionException( "JBoss command '" + commandName + "' at " + jbossCommandFile.toString()
                + " is not an executable program" );
        }

        getLog().debug( "Executing JBoss command: " + jbossCommandFile + " " + options );
        try
        {
            if ( osName.startsWith( "Windows" ) )
            {
                launchWindowsBatch( jbossCommandFile, options );
            }
            else
            {
                launchUnixScript( jbossCommandFile, options );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to execute command: " + jbossCommandFile.toString(), e );
        }
    }

    /**
     * Launch a batch file in Windows
     * 
     * @param commandFile
     * @param options - Space separated command line parameters
     * @throws IOException
     */
    protected void launchWindowsBatch( File commandFile, String options )
        throws IOException
    {
        // Windows does not allow batch files to be called directly, so must use cmd.exe
        String[] commandWithOptions =
            new String[] {
                "cmd.exe",
                "/C",
                "cd /D " + jbossHome + "\\bin & set JBOSS_HOME=\"" + jbossHome + "\"&" + commandFile.getName() + " "
                    + options };

        // Windows did not accept env config during testing, so JBOSS_HOME is set in the command
        Process proc = Runtime.getRuntime().exec( commandWithOptions );
        JBossServerUtil.dump( proc.getInputStream() );
        JBossServerUtil.dump( proc.getErrorStream() );
    }

    /**
     * Launch a Unix shell script
     * 
     * @param commandFile
     * @param options - Space separated command line parameters
     * @throws IOException
     */
    protected void launchUnixScript( File commandFile, String options )
        throws IOException
    {
        String[] optionsArray = new String[0];
        if ( options != null )
        {
            optionsArray = options.trim().split( "\\s+" );
        }
        String[] commandWithOptions = new String[optionsArray.length + 1];
        commandWithOptions[0] = commandFile.getAbsolutePath();
        for ( int i = 0; i < optionsArray.length; ++i )
        {
            commandWithOptions[i + 1] = optionsArray[i];
        }
        String[] env = new String[] { "JBOSS_HOME=" + jbossHome.getAbsolutePath() };

        Process proc = Runtime.getRuntime().exec( commandWithOptions, env, commandFile.getParentFile() );
        JBossServerUtil.dump( proc.getInputStream() );
        JBossServerUtil.dump( proc.getErrorStream() );
    }

    /**
     * Get the username configured in the Maven settings.xml
     * 
     * @return username
     * @throws MojoExecutionException if the server is not configured in settings.xml
     */
    public String getUsername()
        throws MojoExecutionException
    {
        if ( serverId != null )
        {
            // obtain authenication details for specified server from wagon
            AuthenticationInfo info = wagonManager.getAuthenticationInfo( serverId );
            if ( info == null )
            {
                throw new MojoExecutionException( "Server not defined in settings.xml: " + serverId );
            }

            return info.getUserName();
        }

        return null;
    }

    /**
     * Get the password configured in Maven settings.xml
     * 
     * @return The password from settings.xml
     * @throws MojoExecutionException if the server is not configured in settings.xml
     */
    public String getPassword()
        throws MojoExecutionException
    {
        if ( serverId != null )
        {
            // obtain authenication details for specified server from wagon
            AuthenticationInfo info = wagonManager.getAuthenticationInfo( serverId );
            if ( info == null )
            {
                throw new MojoExecutionException( "Server not defined in settings.xml: " + serverId );
            }

            return info.getPassword();
        }

        return null;
    }

}
