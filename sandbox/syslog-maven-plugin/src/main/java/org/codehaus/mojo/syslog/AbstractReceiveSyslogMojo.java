package org.codehaus.mojo.syslog;

import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.net.AbstractNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;

/**
 * Receive syslog messages
 */
public abstract class AbstractReceiveSyslogMojo
    extends AbstractSyslogMojo
{
    protected void createSyslogServer( SyslogServerEventHandlerIF handler )
    {
        AbstractNetSyslogServerConfig config = new TCPNetSyslogServerConfig();
        
        if ( "udp".equals( this.protocol ) ) 
        {
            config = new UDPNetSyslogServerConfig();
        }
        
        config.setPort( port );
        config.addEventHandler( handler );

        SyslogServer.createThreadedInstance( "audit", config );
    }
}
