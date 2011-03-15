package org.codehaus.mojo.mockrepo;

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Repository content backed by a byte array.
 */
public class ByteArrayContent
    implements Content
{
    private final byte[] content;

    private final Date lastModified;

    public ByteArrayContent( byte[] content )
    {
        this(content, new Date( ));
    }

    public ByteArrayContent( Object obj )
    {
        this( asBytes( obj ) );
    }

    public ByteArrayContent( byte[] content, Date lastModified )
    {
        this.content = content;
        this.lastModified = lastModified;
    }

    private static byte[] asBytes( Object obj )
    {
        try
        {
            return obj.toString().getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            // should never happen, but just in case
            return obj.toString().getBytes();
        }
    }

    public InputStream getInputStream()
    {
        return new ByteArrayInputStream( content );
    }

    public long getSize()
    {
        return content.length;
    }

    public Date getLastModified()
    {
        return (Date) lastModified.clone();
    }
}
