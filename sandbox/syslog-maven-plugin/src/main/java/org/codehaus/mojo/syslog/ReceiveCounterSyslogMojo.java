package org.codehaus.mojo.syslog;

import org.apache.commons.lang.time.StopWatch;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.syslog.handler.SyslogServerHandlerCounter;

/**
 * Receive syslog messages and display receive statistics periodically
 * @goal receive-fast
 * @requiresProject false
 */
public class ReceiveCounterSyslogMojo
    extends AbstractReceiveSyslogMojo
{
    /**
     * How often we want to show the receive rate at the console
     * @parameter expression="${displayRate}" default-value="5000";
     */
    
    private int displayRate; 
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        SyslogServerHandlerCounter eventHandler = new SyslogServerHandlerCounter();
        
        this.createSyslogServer( eventHandler );
        
        StopWatch sw = new StopWatch();
        sw.start();
        
        while ( true )
        {
            try
            {
                Thread.sleep( displayRate );
                long count = eventHandler.getCounter();
                System.out.println( count + ":" + count*1000/sw.getTime() );
            }
            catch ( Exception e )
            {
            }
        }
    }
}
