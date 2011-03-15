package org.codehaus.mojo.enchanter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.OperationNotSupportedException;

import org.codehaus.mojo.enchanter.ConnectionLibrary;

public class JSchConnectionLibrary
    implements ConnectionLibrary
{

    public void connect( String host )
        throws IOException, OperationNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void connect( String host, int port )
        throws IOException, OperationNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void connect( String host, String username )
        throws IOException, OperationNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void connect( String host, int port, String username, String password )
        throws IOException, OperationNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void connect( String host, int port, String username, String password, String privateKeyPath )
        throws IOException, OperationNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void disconnect()
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    public InputStream getInputStream()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OutputStream getOutputStream()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setReadTimeout( int msec )
        throws IOException, OperationNotSupportedException
    {
        throw new OperationNotSupportedException();

    }

}
