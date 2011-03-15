package org.codehaus.mojo.mockrepo.server;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.logging.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;

class BasicHttpWorker
    implements Runnable
{
    private final static int BUF_SIZE = 2048;

    private static final byte[] EOL = {(byte) '\r', (byte) '\n'};

    private final byte[] buf = new byte[BUF_SIZE];

    private final Socket s;

    private final HttpSite site;

    private final Log log;

    BasicHttpWorker( Socket s, HttpSite site, Log log )
    {
        this.s = s;
        this.site = site;
        this.log = log;
    }

    public void run()
    {
        try
        {
            handleClient();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    void handleClient()
        throws IOException
    {
        InputStream is = new BufferedInputStream( s.getInputStream() );
        PrintStream ps = new PrintStream( s.getOutputStream() );
        s.setTcpNoDelay( true );
        final InetSocketAddress socketAddress = (InetSocketAddress) s.getRemoteSocketAddress();
        String clientSource = socketAddress.getAddress() + ":" + socketAddress.getPort();
        try
        {
            int nread = 0, r = 0;

            outerloop:
            while ( nread < BUF_SIZE )
            {
                r = is.read( buf, nread, BUF_SIZE - nread );
                if ( r == -1 )
                {
                    /* EOF */
                    return;
                }
                int i = nread;
                nread += r;
                for ( ; i < nread; i++ )
                {
                    if ( buf[i] == (byte) '\n' || buf[i] == (byte) '\r' )
                    {
                        /* read one line */
                        break outerloop;
                    }
                }
            }

            boolean doingGet;
            int index;
            if ( buf[0] == (byte) 'G' && buf[1] == (byte) 'E' && buf[2] == (byte) 'T' && buf[3] == (byte) ' ' )
            {
                doingGet = true;
                index = 4;
            }
            else if ( buf[0] == (byte) 'H' && buf[1] == (byte) 'E' && buf[2] == (byte) 'A' && buf[3] == (byte) 'D'
                && buf[4] == (byte) ' ' )
            {
                doingGet = false;
                index = 5;
            }
            else
            {
                index = nread;
                for ( int i = 0; i < nread; i++ )
                {
                    if ( ( buf[i] == (byte) '\n' ) || ( buf[i] == (byte) '\r' ) )
                    {
                        index = i;
                        break;
                    }
                }
                HttpContent content = new ErrorPageContent( 405, "Method Not Allowed" );
                log.error( clientSource + " \"" + new String( buf, 0, index ) + "\"  405 " + content.getSize() );
                sendHeaders( ps, 405, "Method Not Allowed", content );
                ps.print( "Allow: HEAD GET" );
                ps.write( EOL );
                sendContent( ps, content );
                ps.flush();
                s.close();
                return;
            }

            int i = index;
            while ( i < nread )
            {
                if ( buf[i] == (byte) ' ' )
                {
                    break;
                }
                i++;
            }
            int lineEnd = nread;
            for ( int j = i; j < nread; j++ )
            {
                if ( ( buf[j] == (byte) '\n' ) || ( buf[j] == (byte) '\r' ) )
                {
                    lineEnd = i;
                    break;
                }
            }

            // request paths are always in US-ASCII according to RFC2396
            String requestPath = ( new String( buf, index, i - index, "US-ASCII" ) );

            // space can be escaped with the + symbol all other characters are escaped with %nnn
            // there is no defined charset for the high-byte characters, so we will use UTF-8
            // sure why not!
            requestPath = URLDecoder.decode( requestPath.replace( '+', ' ' ), "UTF-8" );

            if ( requestPath.startsWith( File.separator ) )
            {
                requestPath = requestPath.substring( 1 );
            }

            HttpContent content;
            log.info( requestPath );
            if ( !site.isValidPath( requestPath ) )
            {
                content = new ErrorPageContent( 400, "Bad Request" );
                log.warn( clientSource + " \"" + new String( buf, 0, lineEnd ) + "\"  400 " + content.getSize() );
                sendHeaders( ps, 400, "Bad Request", content );
                if ( doingGet )
                {
                    sendContent( ps, content );
                }
            }
            else
            {
                content = site.getContent( requestPath );
                if ( content == null )
                {
                    content =
                        new ErrorPageContent( 404, "Not Found", "The request path: " + requestPath + " was not found" );
                    log.info( clientSource + " \"" + new String( buf, 0, lineEnd ) + "\"  404 " + content.getSize() );
                    sendHeaders( ps, 404, "Not Found", content );
                    if ( doingGet )
                    {
                        sendContent( ps, content );
                    }
                }
                else
                {
                    log.info( clientSource + " \"" + new String( buf, 0, lineEnd ) + "\"  200 " + content.getSize() );
                    sendHeaders( ps, 200, "OK", content );
                    if ( doingGet )
                    {
                        sendContent( ps, content );
                    }
                }
            }
        }
        finally
        {
            ps.flush();
            s.close();
        }
    }

    void sendHeaders( PrintStream ps, int code, String title, HttpContent content )
        throws IOException
    {
        ps.print( "HTTP/1.0 " );
        ps.print( code );
        ps.print( ' ' );
        ps.print( title );
        ps.write( EOL );
        ps.print( "Server: Mock Maven Remote Repository Server" );
        ps.write( EOL );
        ps.print( "Date: " + new Date() );
        ps.write( EOL );
        if ( content.getSize() > 0 )
        {
            ps.print( "Content-length: " + content.getSize() );
            ps.write( EOL );
            if ( content.getLastModified() != null )
            {
                ps.print( "Last Modified: " + content.getLastModified() );
                ps.write( EOL );
            }
        }
        if ( content.getContentType() != null )
        {
            ps.print( "Content-type: " );
            ps.print( content.getContentType() );
            ps.write( EOL );
        }
    }

    void sendContent( PrintStream ps, HttpContent content )
        throws IOException
    {
        ps.write( EOL );
        InputStream is = null;
        try
        {
            is = content.getInputStream();
            int n;
            while ( ( n = is.read( buf ) ) > 0 )
            {
                ps.write( buf, 0, n );
            }
        }
        finally
        {
            if ( is != null )
            {
                is.close();
            }
        }

    }

}