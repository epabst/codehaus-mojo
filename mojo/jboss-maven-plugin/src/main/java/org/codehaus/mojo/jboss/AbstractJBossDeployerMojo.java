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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

/**
 * Provides basic functionality for deploying an application over HTTP.
 */
public abstract class AbstractJBossDeployerMojo
    extends AbstractMojo
{

    /**
     * The default username to use when authenticating with Tomcat manager.
     */
    private static final String DEFAULT_USERNAME = "admin";

    /**
     * The default password to use when authenticating with Tomcat manager.
     */
    private static final String DEFAULT_PASSWORD = "";

    /**
     * The port JBoss is running on.
     * 
     * @parameter default-value="8080" expression="${jboss.port}"
     */
    protected int port;

    /**
     * The host JBoss is running on.
     * 
     * @parameter default-value="localhost" expression="${jboss.hostName}"
     */
    protected String hostName;

    /**
     * The name of the file or directory to deploy or undeploy.
     * 
     * @parameter
     */
    protected List fileNames;

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
     * @parameter
     * @deprecated Use serverId instead
     */
    private String server;

    /**
     * The id of the server configuration found in Maven settings.xml. This configuration will determine the
     * username/password to use when authenticating with the JBoss server. If no value is specified, a default username
     * and password will be used.
     * 
     * @parameter expression="${jboss.serverId}"
     * @since 1.5.0
     */
    private String serverId;

    /**
     * Skip the mojo execution.
     * 
     * @parameter default-value="false" expression="${jboss.skip}"
     * @since 1.5.0
     */
    protected boolean skip;

    /**
     * Main common deploy mojo execution
     */
    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            getLog().debug( "Skipping execution of jboss-maven-plugin" );
            return;
        }

        if ( fileNames == null )
        {
            getLog().info( "No files configured to deploy/undeploy." );
            return;
        }

        doExecute();
    }

    /**
     * Mojo specific execution implemented by subclasses
     */
    protected abstract void doExecute()
        throws MojoExecutionException;

    /**
     * Open a URL.
     * 
     * @param url
     * @throws MojoExecutionException
     */
    protected void doURL( String url )
        throws MojoExecutionException
    {
        try
        {

            url = url.replaceAll( "\\s", "%20" );

            getLog().debug( "url = " + url );

            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();
            connection.setInstanceFollowRedirects( false );
            connection.setRequestProperty( "Authorization", toAuthorization() );

            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            reader.readLine();
            reader.close();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Mojo error occurred: " + e.getMessage(), e );
        }
    }

    /**
     * Gets the HTTP Basic Authorization header value for the supplied username and password.
     * 
     * @return the HTTP Basic Authorization header value
     * @throws MojoExecutionException
     */
    private String toAuthorization()
        throws MojoExecutionException
    {
        String userName;
        String password;

        if ( serverId == null || serverId.equals( "" ) )
        {
            serverId = server;
        }

        if ( serverId == null )
        {
            // no server set, use defaults
            getLog().info( "No server id specified for authentication - using defaults" );
            userName = DEFAULT_USERNAME;
            password = DEFAULT_PASSWORD;
        }
        else
        {
            // obtain authenication details for specified server from wagon
            AuthenticationInfo info = wagonManager.getAuthenticationInfo( serverId );
            if ( info == null )
            {
                throw new MojoExecutionException( "Server not defined in settings.xml: " + serverId );
            }

            userName = info.getUserName();
            if ( userName == null )
            {
                getLog().info( "No server username specified - using default" );
                userName = DEFAULT_USERNAME;
            }

            password = info.getPassword();
            if ( password == null )
            {
                getLog().info( "No server password specified - using default" );
                password = DEFAULT_PASSWORD;
            }
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append( userName ).append( ':' );
        if ( password != null )
        {
            buffer.append( password );
        }
        return "Basic " + new String( Base64.encodeBase64( buffer.toString().getBytes() ) );
    }

}
