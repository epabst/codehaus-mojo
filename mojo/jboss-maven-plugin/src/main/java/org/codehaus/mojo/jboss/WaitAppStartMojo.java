package org.codehaus.mojo.jboss;

import java.io.File;
import java.io.IOException;
import java.rmi.RMISecurityManager;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Waits until an app is started.
 * 
 * @author <a href="mailto:viniciusfk@hotmail.com">Vinicius Kopcheski</a>
 * @goal wait-app-start
 * @requiresProject false
 * @since 1.5.0
 */
public class WaitAppStartMojo
    extends StartAndWaitMojo
{
    /**
     * @parameter default-value="" expression="${jboss.earName}"
     */
    protected String earName;

    private static String command = null;

    public void execute()
        throws MojoExecutionException
    {
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
            this.getLog().info( "Unable to create security policy file for loading remote classes: " + e.getMessage(),
                                e );
            this.getLog().info( "Will try to load required classes from local classpath." );
        }
        catch ( SecurityException e )
        {
            this.getLog().info( "Unable to set security manager for loading remote classes: " + e.getMessage(), e );
            this.getLog().info( "Will try to load required classes from local classpath." );
        }
        InitialContext ctx = this.getInitialContext();

        // Try to get JBoss jmx MBean connection
        MBeanServerConnection server = null;
        NamingException ne = null;
        for ( int i = 0; i < this.retry; ++i )
        {
            try
            {
                Thread.sleep( this.retryWait );
                server = (MBeanServerConnection) ctx.lookup( "jmx/invoker/RMIAdaptor" );
                break;
            }
            catch ( NamingException e )
            {
                ne = e;
                this.getLog().info( "Waiting to retrieve JBoss JMX MBean connection... " );
            }
            catch ( InterruptedException e )
            {
                this.getLog().warn( "Thread interrupted while waiting for MBean connection: " + e.getMessage() );
                e.printStackTrace();
            }
        }

        if ( server == null )
        {
            throw new MojoExecutionException( "Unable to get JBoss JMX MBean connection: " + ne.getMessage(), ne );
        }

        this.getLog().info( "JBoss JMX MBean connection successful!" );

        // Wait until server startup is complete
        boolean started = false;
        for ( int i = 0; i < this.retry; ++i )
        {
            this.getLog().info( "Trying " + ( i + 1 ) + " of " + this.retry );
            try
            {
                started = this.isAppStarted( server );
                if ( started )
                {
                    break;
                }
                this.getLog().info( "App not started yet" );
                Thread.sleep( this.retryWait );
            }
            catch ( Exception e )
            {
                this.getLog().error( e );
                throw new MojoExecutionException( "Unable to wait: " + e.getMessage(), e );
            }
        }
        if ( !started )
        {
            throw new MojoExecutionException( "App is not stared before timeout has expired! " );
        }
        this.getLog().info( "App started!" );
    }

    /**
     * Check if the server has finished starting the app. Will throw one of several exceptions if the server connection
     * fails.
     * 
     * @param server The connection to the server
     * @return true if the app is started
     * @throws Exception
     */
    protected boolean isAppStarted( MBeanServerConnection server )
        throws Exception
    {

        ObjectName serverMBeanName = new ObjectName( this.getAttribute() );
        try
        {
            this.getLog().info( "Checking if " + this.earName + " is already started..." );
            return server.getAttribute( serverMBeanName, "StateString" ).equals( "Started" );
        }
        catch ( InstanceNotFoundException infe )
        {
            return false;
        }
    }

    private String getAttribute()
    {
        if ( WaitAppStartMojo.command == null )
        {
            StringBuilder commandSB = new StringBuilder( "jboss.j2ee:service=EARDeployment,url='" );
            commandSB.append( this.earName );
            commandSB.append( "'" );
            WaitAppStartMojo.command = commandSB.toString();
        }
        return WaitAppStartMojo.command;
    }

}
