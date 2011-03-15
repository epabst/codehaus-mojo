package org.codehaus.mojo.xmlbeans;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.plugin.logging.Log;

public class FilteredJarFile extends JarFile
{

    private Log logger;

    public FilteredJarFile( File arg0, Log log ) throws IOException
    {
        super( arg0 );
        logger = log;
    }

    public List getEntryPathsAndExtract( String[] filter, File prefix ) throws IOException
    {
        final List entries = new ArrayList();

        JarEntry nextEntry;
        for ( Enumeration e = entries(); e.hasMoreElements(); )
        {
            nextEntry = ( ( JarEntry ) e.nextElement() );
            if ( !nextEntry.isDirectory() && !isFiltered( nextEntry.getName(), filter ) )
            {
                if ( logger.isDebugEnabled() )
                {
                    logger.debug( "adding and extracting " + nextEntry.getName() );
                }
                extractEntry( prefix, nextEntry );
                entries.add( nextEntry.getName() );
            }
        }
        return entries;
    }

    private boolean isFiltered( final String name, final String[] filter )
    {
        int size = filter.length;
        if ( name != null )
        {
            for ( int i = 0; i < size; i++ )
            {
                if ( name.endsWith( filter[i] ) )
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "Accepting " + name );
                    }
                    return false;
                }
            }
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "Filtering " + name );
            }
            return true;
        }
        else
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "Filtering out null." );
            }
            return true;
        }
    }

    /**
     * Unpack this entry into the given file location.
     *
     * @param prefix The directory to unpack to.
     * @param entry  The entry to unpack.
     * @throws java.io.IOException if entry cannot be extracted.
     */
    public void extractEntry( File prefix, JarEntry entry ) throws IOException
    {
        File output = new File( prefix, entry.getName() );
        // only do this if the file doesn't exist; otherwise we throw off the
        // stale file check.
        if ( !output.exists() )
        {
            output.getParentFile().mkdirs();
            output.createNewFile();
            InputStream ios = null;
            FileOutputStream fos = null;
            try
            {
                ios = getInputStream( entry );
                fos = new FileOutputStream( output );

                byte[] buf = new byte[8192];
                while ( true )
                {
                    int length = ios.read( buf );
                    if ( length < 0 )
                    {
                        break;
                    }
                    fos.write( buf, 0, length );
                }
            }
            finally
            {
                if ( ios != null )
                {
                    try
                    {
                        ios.close();
                    }
                    catch ( IOException ignore )
                    {
                        //ignore
                    }
                }
                if ( fos != null )
                {
                    try
                    {
                        fos.close();
                    }
                    catch ( IOException ignore )
                    {
                        //ignore
                    }
                }
            }
        }
    }

}
