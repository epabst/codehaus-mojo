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
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Sep 1, 2009 2:35:43 PM
 */
public class ErrorPageContent
    implements HttpContent
{
    private final byte[] content;

    private final Date lastModified;

    public ErrorPageContent( int code, String title )
        throws UnsupportedEncodingException
    {
        this( code, title, title );
    }

    public ErrorPageContent( int code, String title, String message )
        throws UnsupportedEncodingException
    {
        this.lastModified = new Date();
        title = title.replaceAll( "\\&", "&amp;" ).replaceAll( "\\<", "&lt;" ).replaceAll( "\\>", "&gt;" );
        StringWriter w = new StringWriter();
        writeln( w, "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">" );
        writeln( w, "<html>" );
        writeln( w, "  <head>" );
        writeln( w, "    <title>" + code + " " + title + "</title>" );
        writeln( w, "  </head>" );
        writeln( w, "  <body style='font-family: \"Trebuchet MS\",verdana,lucida,arial,helvetica,sans-serif;'>" );
        writeln( w, "    <h1>" + title + "</h1>" );
        writeln( w, "    <p>" + message + "</p>" );
        writeln( w, "    </p>" );
        writeln( w, "  </body>" );
        writeln( w, "</html>" );

        this.content = w.toString().getBytes( "UTF-8" );
    }

    private static void writeln( StringWriter w, String s )
    {
        w.write( s );
        w.write( '\n' );
    }

    public InputStream getInputStream()
        throws IOException
    {
        return new ByteArrayInputStream( content );
    }

    public long getSize()
    {
        return content.length;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public String getContentType()
    {
        return "text/html";
    }
}
