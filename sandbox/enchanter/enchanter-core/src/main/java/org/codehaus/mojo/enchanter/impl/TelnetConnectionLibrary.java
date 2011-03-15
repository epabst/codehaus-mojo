package org.codehaus.mojo.enchanter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.codehaus.mojo.enchanter.ConnectionLibrary;

public class TelnetConnectionLibrary
    implements ConnectionLibrary
{

    private TelnetClient sess;

    public void connect( String host )
        throws IOException, OperationNotSupportedException
    {
        connect( host, 23 );
    }

    public void connect( String host, int port )
        throws IOException, OperationNotSupportedException
    {

        sess = new TelnetClient();

        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler( "VT100", false, false, true, false );
        EchoOptionHandler echoopt = new EchoOptionHandler( true, false, true, false );
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler( true, true, true, true );

        try
        {
            sess.addOptionHandler( ttopt );
            sess.addOptionHandler( echoopt );
            sess.addOptionHandler( gaopt );
        }
        catch ( InvalidTelnetOptionException e )
        {
            System.err.println( "Error registering option handlers: " + e.getMessage() );
        }

        sess.connect( host, port );

        // prevent blocking read socket
        //sess.setSoTimeout(1000);
    }

    public void connect( String host, String username )
        throws IOException, OperationNotSupportedException
    {

        throw new OperationNotSupportedException();

    }

    public void connect( String host, int port, String username, String password )
        throws IOException, OperationNotSupportedException
    {

        throw new OperationNotSupportedException();

    }

    public void connect( String host, int port, String username, String password, String privateKeyPath )
        throws IOException, OperationNotSupportedException
    {

        throw new OperationNotSupportedException();

    }

    public void disconnect()
        throws IOException
    {
        if ( sess != null )
        {
            sess.disconnect();
            sess = null;
        }
    }

    public InputStream getInputStream()
    {
        return sess.getInputStream();
    }

    public OutputStream getOutputStream()
    {
        return sess.getOutputStream();
    }

    public void setReadTimeout( int msec )
        throws IOException, OperationNotSupportedException
    {

        sess.setSoTimeout( msec );
    }

}
