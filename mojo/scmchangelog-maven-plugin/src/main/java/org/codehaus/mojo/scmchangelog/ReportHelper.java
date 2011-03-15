/*
The MIT License

Copyright (c) 2004, The Codehaus

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.codehaus.mojo.scmchangelog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper to have the images in the site target directory.
 * @author ehsavoie
 * @version $Id$
 */
public class ReportHelper
{
  /**
   * Size for the buffer when reading/writing binary stream.
   */
  public static final int BUFFER_LENGTH = 8;
  
  /**
   * Copies images from the plugin jar to the site directory.
   * @param resourcePath the name of the resource (path relative to the classpath).
   * @param outputDir the site target directory.
   * @throws IOException in case of an I/O error.
   */
  public static void copyImage( String resourcePath, String outputDir )
      throws IOException
  {
    String imageDir = outputDir 
        + System.getProperty( "file.separator" )
        + "images";
    File image = new File( imageDir );
    image.mkdirs();
    image = new File( imageDir 
        + System.getProperty( "file.separator" )
        + getImageName( resourcePath ) );
    image.createNewFile();

    FileOutputStream out = new FileOutputStream( image );
    InputStream in = ReportHelper.class.getClassLoader().getResourceAsStream( resourcePath );
    byte[] buffer = new byte[ BUFFER_LENGTH ];
    int c = 0;
    while ( ( c = in.read( buffer ) ) > 0 )
    {
      out.write( buffer, 0, c );
    }
    in.close();
    out.close();
  }

  /**
   * Computes the name of the image based on the reousrce name.
   * @param resourcePath the name of the resource (path relative to the classpath).
   * @return the name of the image.
   */
  public static String getImageName( String resourcePath )
  {
    int index = resourcePath.lastIndexOf( '/' );
    if ( index >= 0 )
    {
      return resourcePath.substring( index + 1 );
    }
    return resourcePath;
  }
}
