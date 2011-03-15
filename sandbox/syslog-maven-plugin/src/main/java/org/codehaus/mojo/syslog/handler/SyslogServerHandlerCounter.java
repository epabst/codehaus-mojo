package org.codehaus.mojo.syslog.handler;

import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;

public class SyslogServerHandlerCounter
    implements SyslogServerEventHandlerIF
{
    private static final long serialVersionUID = -3496862887351690575L;

    private long counter = 0;

    public synchronized long getCounter()
    {
        return counter;
    }

    private synchronized void increamentCounter()
    {
        counter++;
    }

    public void event(SyslogServerIF syslogServer, SyslogServerEventIF event)
    {
        increamentCounter();
    }

}