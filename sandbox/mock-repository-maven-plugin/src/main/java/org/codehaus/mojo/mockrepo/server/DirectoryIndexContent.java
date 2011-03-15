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

import org.codehaus.mojo.mockrepo.utils.MockRepoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;

/**
 * A directory index.
 *
 * @author Stephen Connolly
 * @since 1.0
 */
public class DirectoryIndexContent
    implements HttpContent
{
    private final byte[] content;

    private final Date lastModified;

    public DirectoryIndexContent( String requestPath, Map/*<String,HttpContent>*/ contents )
        throws UnsupportedEncodingException
    {
        Date lastModified = null;
        for ( Iterator i = contents.values().iterator(); i.hasNext(); ) {
            HttpContent content = (HttpContent) i.next();
            final Date anotherDate = content.getLastModified();
            if ( lastModified == null || anotherDate != null && lastModified.compareTo( anotherDate ) < 0 )
            {
                lastModified = anotherDate;
            }
        }
        this.lastModified = lastModified == null ? new Date() : lastModified;
        StringWriter w = new StringWriter();
        writeln( w, "<html>" );
        writeln( w, "  <head>" );
        writeln( w, "    <title>Index of /" + requestPath + "</title>" );
        writeln( w, "    <meta http-equiv=\"Content-Type\" repository=\"text/html; charset=utf-8\"/>" );
        writeln( w, "</head>" );
        writeln( w, "<body style='font-family: \"Trebuchet MS\",verdana,lucida,arial,helvetica,sans-serif;'>" );
        writeln( w, "<h1>Index of /" + requestPath + "</h1>" );
        writeln( w, "  <table cellspacing='10'>" );
        writeln( w, "    <tr>" );
        writeln( w, "      <th align='left'>Name</th>" );
        writeln( w, "      <th>Last Modified</th>" );
        writeln( w, "      <th>Size</th>" );
        writeln( w, "      <th>Description</th>" );
        writeln( w, "    </tr>" );

        if ( requestPath.length() > 0 )
        {
            writeln( w, "    <tr>" );
            writeln( w, "      <td>" );
            writeln( w, "        <a href='..'>Parent Directory</a>" );
            writeln( w, "      </td>" );
            writeln( w, "    </tr>" );
        }
        String[] list = (String[]) contents.keySet().toArray( new String[contents.size()] );
        for ( int i = 0; list != null && i < list.length; i++ )
        {
            writeln( w, "    <tr>" );
            writeln( w, "      <td>" );
            final String childName = list[i];
            final String childPath = requestPath + childName;
            HttpContent childContent = childPath.endsWith( "/" ) ? null : (HttpContent) contents.get( childPath );
            if ( childContent instanceof DirectoryIndexContent )
            {
                String name = childName.toLowerCase();
                if ( name.endsWith( "/index.html" ) || name.endsWith( "/index.htm" ) || childName.indexOf( '/' ) != -1 )
                {
                    name = childName.substring( 0, name.lastIndexOf( '/' ) );
                }
                else
                {
                    name = childName;
                }
                writeln( w, "        <a href=\"" + MockRepoUtils.urlEncodePathSegment( name )+ "/\">" + name + "/</a>" );
            }
            else
            {
                writeln( w, "        <a href=\"" + MockRepoUtils.urlEncodePathSegment( childName ) + "\">" + childName + "</a>" );
            }
            writeln( w, "      </td>" );
            writeln( w, "      <td>" );
            writeln( w, "        " + ( childContent != null ? childContent.getLastModified() : lastModified ) );
            writeln( w, "      </td>" );
            writeln( w, "      <td align='right'>" );
            if ( childContent != null )
            {
                writeln( w, "        " + childContent.getSize() );
            }
            else
            {
                writeln( w, "        &nbsp;" );
            }
            writeln( w, "      </td>" );
            writeln( w, "      <td>&nbsp;</td>" );
            writeln( w, "    </tr>" );
        }
        writeln( w, "  </table>" );
        writeln( w, "</body>" );
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
