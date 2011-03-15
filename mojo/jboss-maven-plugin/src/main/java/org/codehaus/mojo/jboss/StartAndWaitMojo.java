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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RMISecurityManager;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.maven.plugin.MojoExecutionException;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;
import org.jnp.interfaces.NamingContext;

/**
 * Starts JBoss and waits until the server is started.
 * 
 * @author <a href="mailto:jc7442@yahoo.fr">J-C</a>
 * @author <a href="mailto:fuzail@fingerprintsoft.org">Fuzail Sarang</a>
 * @goal start-and-wait
 * @requiresProject false
 * @since 1.4
 */
public class StartAndWaitMojo
    extends StartMojo
{

    /**
     * One second in millis.
     */
    public static final long ONE_SECOND = 1000;

    /**
     * Maximum number of retries to get JBoss JMX MBean connection.
     * 
     * @parameter default-value="4" expression="${jboss.retry}"
     */
    protected int retry;

    /**
     * Wait in ms before each retry of the JBoss JMX MBean connection.
     * 
     * @parameter default-value="5000" expression="${jboss.retryWait}"
     */
    protected int retryWait;

    /**
     * Time in ms to start the application server (once JMX MBean connection has been reached).
     * 
     * @parameter default-value="20000" expression="${jboss.timeout}"
     */
    protected int timeout;

    /**
     * The port for the naming service.
     * 
     * @parameter default-value="1099" expression="${jboss.namingPort}"
     */
    protected String namingPort;

    /**
     * The host JBoss is running on.
     * 
     * @parameter default-value="localhost" expression="${jboss.hostname}"
     */
    protected String hostName;

    /**
     * Main plugin execution.
     * 
     * @throws MojoExecutionException
     */
    public void execute()
        throws MojoExecutionException
    {
        // Start JBoss
        super.execute();

        // Set up the security manager to allow remote code to execute.
        try
        {
            File policyFile = File.createTempFile( "jboss-client", ".policy" );
            policyFile.deleteOnExit();
            JBossServerUtil.writeSecurityPolicy( policyFile );
            // Get the canonical file which expands the shortened directory names in Windows
            policyFile = policyFile.getCanonicalFile();
            System.setProperty( "java.security.policy", policyFile.toURI().toString() );
            System.setSecurityManager( new RMISecurityManager() );
        }
        catch ( IOException e )
        {
            getLog().info( "Unable to create security policy file for loading remote classes: " + e.getMessage(), e );
            getLog().info( "Will try to load required classes from local classpath." );
        }
        catch ( SecurityException e )
        {
            getLog().info( "Unable to set security manager for loading remote classes: " + e.getMessage(), e );
            getLog().info( "Will try to load required classes from local classpath." );
        }
        InitialContext ctx = getInitialContext();

        // Try to get JBoss jmx MBean connection
        MBeanServerConnection server = null;
        NamingException ne = null;
        for ( int i = 0; i < retry; ++i )
        {
            try
            {
                Thread.sleep( retryWait );
                server = (MBeanServerConnection) ctx.lookup( "jmx/invoker/RMIAdaptor" );
                break;
            }
            catch ( NamingException e )
            {
                ne = e;
                getLog().info( "Waiting to retrieve JBoss JMX MBean connection... " );
            }
            catch ( InterruptedException e )
            {
                getLog().warn( "Thread interrupted while waiting for MBean connection: " + e.getMessage() );
                e.printStackTrace();
            }
        }

        if ( server == null )
        {
            throw new MojoExecutionException( "Unable to get JBoss JMX MBean connection: " + ne.getMessage(), ne );
        }

        getLog().info( "JBoss JMX MBean connection successful!" );

        // Wait until server startup is complete
        boolean started = false;
        long startTime = System.currentTimeMillis();
        while ( !started && System.currentTimeMillis() - startTime < timeout )
        {
            try
            {
                Thread.sleep( ONE_SECOND );
                started = isStarted( server );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Unable to wait: " + e.getMessage(), e );
            }
        }
        if ( !started )
        {
            throw new MojoExecutionException( "JBoss AS is not stared before timeout has expired! " );
        }
        getLog().info( "JBoss AS started!" );
    }

    /**
     * Check if the server has finished startup. Will throw one of several exceptions if the server connection fails.
     * 
     * @param s
     * @return
     * @throws Exception
     */
    protected boolean isStarted( MBeanServerConnection server )
        throws Exception
    {
        ObjectName serverMBeanName = new ObjectName( "jboss.system:type=Server" );
        return ( (Boolean) server.getAttribute( serverMBeanName, "Started" ) ).booleanValue();
    }

    /**
     * Set up the context information for connecting the the jboss server.
     * 
     * @return
     * @throws MojoExecutionException
     */
    protected InitialContext getInitialContext()
        throws MojoExecutionException
    {
        Properties env = new Properties();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory" );
        env.put( Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces" );
        env.put( Context.PROVIDER_URL, hostName + ":" + namingPort );
        env.put( NamingContext.JNP_DISABLE_DISCOVERY, "true" );

        String username = getUsername();
        if ( username != null )
        {
            SecurityAssociation.setPrincipal( new SimplePrincipal( username ) );
            SecurityAssociation.setCredential( getPassword() );
        }
                
        try
        {
            return new InitialContext( env );
        }
        catch ( NamingException e )
        {
            throw new MojoExecutionException( "Unable to instantiate naming context: " + e.getMessage(), e );
        }
    }

}
