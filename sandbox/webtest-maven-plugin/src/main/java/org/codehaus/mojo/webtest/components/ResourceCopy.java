/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest.components;

import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copy resources loaded from the classpath to a directory
 */
public class ResourceCopy
{
    /**
     * the size of the internal buffer to copy streams
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Make an XSL transformation
     */
    public ResourceCopy()
    {
    }

    /**
     * Copy a list of resources loaded from the classloader to
     * a target directory.
     *
     * @param sourcePath The source path of the resources
     * @param sourceFiles A list of resources to copy
     * @param targetDir The target directory to copy the resources to
     * @throws Exception the copying failed
     */
    public void copy( String sourcePath, String[] sourceFiles, File targetDir ) throws Exception
    {
        String sourceResourceName;
        File targetFile;
        InputStream is = null;
        FileOutputStream fos = null;

        try
        {
            targetDir.mkdirs();

            for ( int i = 0; i < sourceFiles.length; i++ )
            {
                sourceResourceName = sourcePath + sourceFiles[i];
                is = this.getClass().getResourceAsStream( sourceResourceName );
                targetFile = new File( targetDir, sourceFiles[i] );
                fos = new FileOutputStream( targetFile );
                this.copy( is, fos );
                is = null;
                fos = null;
            }
        }
        catch ( Exception e )
        {
            if ( is != null )
            {
                is.close();
            }
            if ( fos != null )
            {
                fos.close();
            }
            throw e;
        }
    }

    /**
     * Pumps the input stream to the output stream.
     *
     * @param is the source input stream
     * @param os the target output stream
     * @return the number of bytes copied
     * @throws IOException the copying failed
     */
    public int copy( InputStream is, OutputStream os ) throws IOException
    {
        Validate.notNull( is, "is is null" );
        Validate.notNull( os, "os is null" );

        int n;
        int total = 0;
        byte[] buf = new byte[BUFFER_SIZE];

        while ( ( n = is.read( buf ) ) > 0 )
        {
            os.write( buf, 0, n );
            total += n;
        }

        is.close();
        os.flush();
        os.close();

        return total;
    }
}


