package org.codehaus.mojo.naturaldocs;

/*
 * The MIT License
 * 
 * Copyright (c) 2008, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Ease-of-use file manipulation functions. FIXME: Apache Commons IO alleviates the need for this given
 * http://commons.apache.org/io/api-release/index.html
 * 
 * @author <a href="mailto:timothy.astle@caris.com">Tim Astle</a>
 */
public final class FileUtils
{
    /**
     * This is a recursive directory copy function.
     * 
     * @param source The source file to copy.
     * @param destination The location of where the source file will end up.
     * @throws IOException A problem happened while copying the source file to the destination.
     */
    public static void copyDirectory( File source, File destination )
        throws IOException
    {
        if ( source.isDirectory() )
        {
            if ( !destination.exists() )
            {
                destination.mkdir();
            }

            String[] contents = source.list();
            for ( int i = 0; i < contents.length; i++ )
            {
                copyDirectory( new File( source, contents[i] ), new File( destination, contents[i] ) );
            }
        }
        else
        {
            if ( destination.isDirectory() )
            {
                copyFile( source, new File( destination, source.getName() ) );
            }
            else
            {
                copyFile( source, destination );
            }
        }
    }

    /**
     * Copy a single file or directory from one location to another.
     * 
     * @param source The source file to copy.
     * @param destination How and where the source file will end up.
     * @throws IOException There was a problem copying the file from the source to the destination.
     */
    public static void copyFile( File source, File destination )
        throws IOException
    {
        InputStream istream = new FileInputStream( source );
        OutputStream ostream = new FileOutputStream( destination );

        BufferedInputStream oBuffInputStream = new BufferedInputStream( istream );

        int length;
        byte[] bytes = new byte[1024];
        while ( ( length = oBuffInputStream.read( bytes ) ) > 0 )
        {
            ostream.write( bytes, 0, length );
        }
        istream.close();
        ostream.close();
    }
}
