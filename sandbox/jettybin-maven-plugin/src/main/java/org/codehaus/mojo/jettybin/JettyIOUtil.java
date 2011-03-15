package org.codehaus.mojo.jettybin;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * JettyIOUtil - enhancements to the IOUtil from plexus-util. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo migrate these to plexus-util's IOUtil if needed. 
 */
public class JettyIOUtil
{
    public static void ensureParentDirectoryExists( File file )
        throws IOException
    {
        if ( file.getParentFile() == null )
        {
            // No parent directory. no worries. :-)
            return;
        }

        ensureDirectoryExists( file.getParentFile() );
    }

    public static void ensureDirectoryExists( File dir )
        throws IOException
    {
        if ( !dir.exists() )
        {
            if ( !dir.mkdirs() )
            {
                throw new IOException( "Unable to create directories " + dir.getAbsolutePath() );
            }
        }
    }

    public static void copyResourceToFile( URL resource, File file )
        throws IOException
    {
        final int BUFSIZE = 32768;
        FileOutputStream fos = new FileOutputStream( file );
        BufferedOutputStream out = new BufferedOutputStream( fos );
        InputStream is = resource.openStream();
        BufferedInputStream in = new BufferedInputStream( is );

        try
        {
            byte[] buffer = new byte[BUFSIZE];
            int numRead;
            do
            {
                numRead = in.read( buffer, 0, BUFSIZE );
                if ( numRead > 0 )
                {
                    out.write( buffer, 0, numRead );
                }
            }
            while ( numRead != -1 );
            
            out.flush();
        }
        finally
        {
            IOUtil.close( in );
            IOUtil.close( out );
        }
    }

    public static List getFileList( File directory, String includes, String excludes )
    {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( directory );

        if ( includes != null )
        {
            scanner.setIncludes( StringUtils.split( includes, "," ) );
        }

        if ( excludes != null )
        {
            scanner.setExcludes( StringUtils.split( excludes, "," ) );
        }

        scanner.setCaseSensitive( true );

        scanner.addDefaultExcludes();

        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        List list = new ArrayList();

        for ( int i = 0; i < files.length; i++ )
        {
            list.add( files[i] );
        }

        return list;
    }

}
