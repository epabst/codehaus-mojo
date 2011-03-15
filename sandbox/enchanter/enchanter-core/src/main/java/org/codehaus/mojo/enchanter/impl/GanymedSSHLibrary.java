package org.codehaus.mojo.enchanter.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.OperationNotSupportedException;

import org.codehaus.mojo.enchanter.ConnectionLibrary;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.Session;

/**
 * An implementation of an ssh library using Ganymed
 */
public class GanymedSSHLibrary
    implements ConnectionLibrary
{

    private Session sess;

    public void connect( String host )
        throws IOException, OperationNotSupportedException
    {
        throw new OperationNotSupportedException();
    }

    public void connect( String host, int port )
        throws OperationNotSupportedException
    {
        throw new OperationNotSupportedException();
    }

    public void connect( String host, String username )
        throws IOException
    {
        connect( host, 22, username, "" );
    }

    public void connect( String host, int port, String username, String password )
        throws IOException
    {
        connect( host, port, username, password, null );
    }

    public void connect( String host, int port, String username, final String password, String privateKeyPath )
        throws IOException
    {
        /* Create a connection instance */
        final Connection conn = new Connection( host, port );

        /* Now connect */
        conn.connect();

        /*
         * Authenticate. If you get an IOException saying something like
         * "Authentication method password not supported by the server at this
         * stage." then please check the FAQ.
         */

        boolean triedCustomPublicKey = false;
        boolean triedStandardDSAPublicKey = false;
        boolean triedStandardRSAPublicKey = false;
        boolean triedPassword = false;
        boolean triedPasswordInteractive = false;

        boolean isAuthenticated = false;
        if ( privateKeyPath != null )
        {
            triedCustomPublicKey = true;
            isAuthenticated = conn.authenticateWithPublicKey( username, new File( privateKeyPath ), password );
        }
        else
        {
            File home = new File( System.getProperty( "user.home" ) );
            try
            {
                triedStandardDSAPublicKey = true;
                isAuthenticated = conn.authenticateWithPublicKey( username, new File( home, ".ssh/id_dsa" ), password );
            }
            catch ( IOException ex )
            {
                // dsa key probably can't be found
            }
            if ( !isAuthenticated )
            {
                try
                {
                    triedStandardRSAPublicKey = true;
                    isAuthenticated = conn.authenticateWithPublicKey( username, new File( home, ".ssh/id_rsa" ),
                                                                      password );
                }
                catch ( IOException ex )
                {
                    // rsa key probably can't be found
                }
            }
        }

        if ( !isAuthenticated )
        {
            try
            {
                triedPassword = true;
                isAuthenticated = conn.authenticateWithPassword( username, password );
            }
            catch ( IOException ex )
            {
                // Password authentication probably not supported
            }
            if ( !isAuthenticated )
            {
                try
                {
                    triedPasswordInteractive = true;
                    isAuthenticated = conn.authenticateWithKeyboardInteractive( username, new InteractiveCallback()
                    {

                        public String[] replyToChallenge( String name, String instruction, int numPrompts,
                                                          String[] prompt, boolean[] echo )
                            throws Exception
                        {
                            String[] responses = new String[numPrompts];
                            for ( int x = 0; x < numPrompts; x++ )
                            {
                                responses[x] = password;
                            }
                            return responses;
                        }
                    } );
                }
                catch ( IOException ex )
                {
                    // Password interactive probably not supported
                }
            }
        }

        if ( !isAuthenticated )
        {
            throw new IOException( "Authentication failed.  Tried \n"
                + ( triedCustomPublicKey ? "\tpublic key using " + privateKeyPath + "\n" : "" )
                + ( triedStandardDSAPublicKey ? "\tpublic key using ~/.ssh/id_dsa\n" : "" )
                + ( triedStandardRSAPublicKey ? "\tpublic key using ~/.ssh/id_rsa\n" : "" )
                + ( triedPassword ? "\tpassword\n" : "" ) + ( triedPasswordInteractive ? "\tpassword interactive" : "" ) );
        }

        /* Create a session */

        openSession( conn );

    }

    private void openSession( final Connection conn )
        throws IOException
    {
        sess = conn.openSession();

        sess.requestDumbPTY();

        sess.startShell();

    }

    public OutputStream getOutputStream()
    {
        return sess.getStdin();
    }

    public InputStream getInputStream()
    {
        return sess.getStdout();
    }

    public void disconnect()
    {
        sess.close();
        sess = null;
    }

    public void setReadTimeout( int msec )
        throws IOException, OperationNotSupportedException
    {

    }

}
