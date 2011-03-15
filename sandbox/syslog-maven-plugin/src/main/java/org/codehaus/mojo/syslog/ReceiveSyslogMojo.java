package org.codehaus.mojo.syslog;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;

/**
 * Receive syslog messages
 * @goal receive
 * @requiresProject false
 */
public class ReceiveSyslogMojo
    extends AbstractReceiveSyslogMojo
{
    /**
     * Syslog receiver handler
     * @parameter expression="${receiverHandler}" default-value="org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler"
     * @required
     */
    private String receiverHandler; 
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        SyslogServerEventHandlerIF eventHandler = null;
        
        try 
        {
            eventHandler = (SyslogServerEventHandlerIF) Class.forName( receiverHandler ).newInstance();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        this.createSyslogServer( eventHandler );
        
        while ( true )
        {
            try
            {
                Thread.sleep( 500 );
            }
            catch ( Exception e )
            {
            }
        }
    }
}
