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

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.mockrepo.ByteArrayContent;
import org.codehaus.mojo.mockrepo.Content;
import org.codehaus.mojo.mockrepo.Repository;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class SimpleHttpWorker
    implements Runnable
{
    private final static int BUF_SIZE = 2048;

    private static final byte[] EOL = {(byte) '\r', (byte) '\n'};

    private final byte[] buf = new byte[BUF_SIZE];

    private final Socket s;

    private final Repository repository;

    private final Date lastModified;

    private final Log log;

    SimpleHttpWorker( Socket s, Repository repository, Date lastModified, Log log )
    {
        this.s = s;
        this.lastModified = lastModified;
        this.repository = repository;
        this.log = log;
    }

    private boolean isDir( String path )
    {
        return StringUtils.isEmpty( path ) || path.endsWith( "/" );
    }

    private boolean isMetadata( String path )
    {
        return path.endsWith( "metadata.xml" ) || path.endsWith( "metadata.xml.md5" ) || path.endsWith(
            "metadata.xml.sha1" );
    }

    private boolean isFile( String path )
    {
        String artifactId;
        String version;

        int index1;
        int index2;
        int index3;

        index3 = path.lastIndexOf( '/' );
        if ( index3 == -1 || index3 + 1 >= path.length() )
        {
            // not a valid content path, so nothing at this path
            return false;
        }
        index2 = path.lastIndexOf( '/', index3 - 1 );
        if ( index2 == -1 )
        {
            // not a valid content path, so nothing at this path
            return false;
        }
        version = path.substring( index2 + 1, index3 );
        index1 = path.lastIndexOf( '/', index2 - 1 );
        if ( index1 == -1 )
        {
            // not a valid content path, so nothing at this path
            return false;
        }
        artifactId = path.substring( index1 + 1, index2 );

        String name = path.substring( index3 + 1 );
        
        return name.startsWith( artifactId + '-' + version );
    }

    private String[] listDir( String path )
    {
        final Set result = new TreeSet(repository.getChildPaths( path ));
        if ( repository.getMetadata( path ) != null )
        {
            result.add( "metadata.xml" );
            result.add( "metadata.xml.md5" );
            result.add( "metadata.xml.sha1" );
        }
        return (String[]) result.toArray( new String[result.size()] );
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
                for (; i < nread; i++ )
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
                ps.print( "HTTP/1.0 405 unsupported method type: " );
                ps.write( buf, 0, 5 );
                ps.write( EOL );
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
            String requestPath = ( new String( buf, index, i - index, "UTF-8" ) ).replace( '/', File.separatorChar );
            if ( requestPath.startsWith( File.separator ) )
            {
                requestPath = requestPath.substring( 1 );
            }

            final Content content = sendHeaders( ps, requestPath );
            boolean OK = content != null || isDir( requestPath );
            if ( doingGet )
            {
                if ( OK )
                {
                    sendFile( ps, requestPath, content );
                }
                else
                {
                    send404( ps, requestPath );
                }
            }
        }
        finally
        {
            s.close();
        }
    }

    Content sendHeaders( PrintStream ps, String requestPath )
        throws IOException
    {
        final Content content;
        final boolean isDirectory;
        final boolean isExists;
        if ( isMetadata( requestPath ) )
        {
            log.debug("request path: " + requestPath + " is for metadata");
            isDirectory = false;
            Metadata metadata = repository.getMetadata( requestPath );
            if ( metadata != null )
            {
                MetadataXpp3Writer writer = new MetadataXpp3Writer();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter( bos );
                writer.write( osw, metadata );
                osw.close();
                bos.close();
                content = new ByteArrayContent( bos.toByteArray() );
                isExists = true;
            }
            else
            {
                isExists = false;
                content = null;
            }
        }
        else if ( isDir(requestPath ))
        {
            log.debug("request path: " + requestPath + " is for directory");
            content = null;
            isDirectory = true;
            isExists = true;
        }
        else if (isFile( requestPath ))
        {
            log.debug("request path: " + requestPath + " is for file");            
            content = repository.getContent( requestPath );
            isDirectory = false;
            isExists = content != null;
        } else {
            log.debug("request path: " + requestPath + " does not exist");            
            isDirectory = false;
            isExists = false;
            content = null;
        }
        boolean result;
        if ( !isExists )
        {
            ps.print( "HTTP/1.0 404 not found" );
            ps.write( EOL );
        }
        else
        {
            ps.print( "HTTP/1.0 200 OK" );
            ps.write( EOL );
        }
        ps.print( "Server: Mock Maven Remote Repository Server" );
        ps.write( EOL );
        ps.print( "Date: " + ( new Date() ) );
        ps.write( EOL );
        if ( isExists )
        {
            if ( !isDirectory )
            {
                ps.print( "Content-length: " + content.getSize() );
                ps.write( EOL );
                ps.print( "Last Modified: " + content.getLastModified() );
                ps.write( EOL );

                int ind = requestPath.lastIndexOf( '/' );
                String name = ind == -1 ? requestPath : requestPath.substring( ind + 1 );
                ind = name.lastIndexOf( '.' );
                String ct = null;
                if ( ind > 0 )
                {
                    ct = (String) map.get( name.substring( ind ) );
                }
                if ( ct == null )
                {
                    ct = "unknown/unknown";
                }
                ps.print( "Content-type: " + ct );
                ps.write( EOL );
            }
            else
            {
                ps.print( "Content-type: text/html" );
                ps.write( EOL );
            }
        }
        else
        {
            ps.print( "Content-type: text/html" );
            ps.write( EOL );
        }
        return content;
    }

    void send404( PrintStream ps, String requestPath )
        throws IOException
    {
        ps.println( "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">" );
        ps.println( "<html>" );
        ps.println( "  <head>" );
        ps.println( "    <title>404 Not Found</title>" );
        ps.println( "  </head>" );
        ps.println( "  <body style='font-family: \"Trebuchet MS\",verdana,lucida,arial,helvetica,sans-serif;'>" );
        ps.println( "    <h1>Not Found</h1>" );
        ps.println( "    <p>The requested URL " + requestPath + " was not found on this server.</p>" );
        ps.println( "  </body>" );
        ps.println( "</html>" );
    }

    void sendFile( PrintStream ps, String requestPath, Content content )
        throws IOException
    {
        InputStream is;
        final boolean isDirectory = isDir( requestPath );
        ps.write( EOL );
        if ( isDirectory )
        {
            listDirectory( ps, requestPath );
            return;
        }
        else
        {
            is = content.getInputStream();
        }

        try
        {
            int n;
            while ( ( n = is.read( buf ) ) > 0 )
            {
                ps.write( buf, 0, n );
            }
        }
        finally
        {
            is.close();
        }
    }

    /* mapping of file extensions to repository-types */
    private static final Map map = new HashMap();

    static
    {
        map.put( ".jar", "application/octet-stream" );
        map.put( ".war", "application/octet-stream" );
        map.put( ".ear", "application/octet-stream" );
        map.put( ".par", "application/octet-stream" );
        map.put( ".rar", "application/octet-stream" );
        map.put( ".sar", "application/octet-stream" );
        map.put( ".exe", "application/octet-stream" );
        map.put( ".zip", "application/zip" );
        map.put( ".tar", "application/x-tar" );
        map.put( ".htm", "text/html" );
        map.put( ".html", "text/html" );
        map.put( ".xml", "text/xml" );
        map.put( ".pom", "text/xml" );
        map.put( ".java", "text/plain" );
        map.put( ".md5", "text/plain" );
        map.put( ".sha1", "text/plain" );
    }

    void listDirectory( PrintStream ps, String path )
        throws IOException
    {
        ps.println( "<html>" );
        ps.println( "  <head>" );
        ps.println( "    <title>Index of /" + path + "</title>" );
        ps.println( "    <meta http-equiv=\"Content-Type\" repository=\"text/html; charset=utf-8\"/>" );
        ps.println( "</head>" );
        ps.println( "<body style='font-family: \"Trebuchet MS\",verdana,lucida,arial,helvetica,sans-serif;'>" );
        ps.println( "<h1>Index of /" + path + "</h1>" );
        ps.println( "  <table cellspacing='10'>" );
        ps.println( "    <tr>" );
        ps.println( "      <th align='left'>Name</th>" );
        ps.println( "      <th>Last Modified</th>" );
        ps.println( "      <th>Size</th>" );
        ps.println( "      <th>Description</th>" );
        ps.println( "    </tr>" );

        if ( path.length() > 0 )
        {
            ps.println( "    <tr>" );
            ps.println( "      <td>" );
            ps.println( "        <a href='..'>Parent Directory</a>" );
            ps.println( "      </td>" );
            ps.println( "    </tr>" );
        }
        String[] list = listDir( path );
        for ( int i = 0; list != null && i < list.length; i++ )
        {
            ps.println( "    <tr>" );
            ps.println( "      <td>" );
            final String childPath = path + list[i];
            Content childContent = childPath.endsWith( "/" ) ? null : repository.getContent( childPath );
            if ( childContent != null )
            {
                ps.println( "        <a href=\"" + list[i] + "\">" + list[i] + "</a>" );
            }
            else
            {
                ps.println( "        <a href=\"" + list[i] + "\">" + list[i] + "</a>" );
            }
            ps.println( "      </td>" );
            ps.println( "      <td>" );
            ps.println( "        " + (childContent != null ? childContent.getLastModified() : lastModified) );
            ps.println( "      </td>" );
            ps.println( "      <td align='right'>" );
            if ( childContent != null )
            {
                ps.println( "        " + childContent.getSize() );
            }
            else
            {
                ps.println( "        &nbsp;" );
            }
            ps.println( "      </td>" );
            ps.println( "      <td>&nbsp;</td>" );
            ps.println( "    </tr>" );
        }
        ps.println( "  </table>" );
        ps.println( "</body>" );
        ps.println( "</html>" );
    }

}