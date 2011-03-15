package org.codehaus.mojo.runtime.util;

/*
 * Copyright (c) 2004, Codehaus.org
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

/**
 * adapted from the JarMojo from maven-jar-plugin
 *
 * @author jesse
 * @version $Id$
 */
public class JarMaker
{

    private File jarFile;

    private byte[] buffer = new byte[4096];

    private Map includes = new HashMap();

    private Map manifestEntries = new HashMap();

    public JarMaker( String fullFileName )
    {
        jarFile = new File( fullFileName );
    }

    public JarMaker( File directory, String name )
    {
        jarFile = new File( directory, name );
    }


    public void addManifestEntries( Map entries )
    {
        manifestEntries.putAll( entries );
    }

    public void addManifestEntry( String key, String value )
    {
        manifestEntries.put( key, value );
    }


    /**
     * Add all files in the specified directory to the archive.
     *
     * @param baseDir  the directory to add
     */
    protected void addDirectory( File baseDir )
        throws IOException
    {
        addDirectory( "", baseDir );
    }

    /**
     * Add all files in the specified directory to the archive.
     *
     * @param prefix   value to be added to the front of jar entry names
     * @param baseDir  the directory to add
     */
    public void addDirectory( String prefix, File baseDir )
        throws IOException
    {
        addDirectory( null, null, prefix, baseDir );
    }

    /**
     * Add all files in the specified directory to the archive.
     *
     * @param includesPattern Sets the list of include patterns to use
     * @param excludesPattern Sets the list of exclude patterns to use
     * @param prefix          value to be added to the front of jar entry names
     * @param baseDir         the directory to add
     */
    public void addDirectory( String includesPattern, String excludesPattern, String prefix, File baseDir )
        throws IOException
    {
        if ( !baseDir.exists() )
        {
            return;
        }

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( baseDir );
        if ( includesPattern != null )
        {
            scanner.setIncludes( StringUtils.split( includesPattern, "," ) );
        }

        if ( excludesPattern != null )
        {
            scanner.setExcludes( StringUtils.split( excludesPattern, "," ) );
        }
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            String file = files[i];
            file = file.replace( '\\', '/' ); // todo shouldn't the scanner return platform independent names?
            includes.put( prefix + file, new File( baseDir, file ) );
        }
    }

    /**
     * Create the jar file specified and include the listed files.
     *
     * @throws IOException if there is a problem writing the archive or reading the sources
     */
    public void create()
        throws IOException
    {
        File parentJarFile = jarFile.getParentFile();
        if ( !parentJarFile.exists() )
        {
            parentJarFile.mkdirs();
        }
        JarOutputStream jos = createJar( jarFile, createManifest() );
        try
        {
            addEntries( jos, includes );
        }
        finally
        {
            jos.close();
        }
    }


    /**
     * Create the specified jar file and return a JarOutputStream to it
     *
     * @param jarFile the jar file to create
     * @param mf      the manifest to use
     * @return a JarOutputStream that can be used to write to that file
     * @throws IOException if there was a problem opening the file
     */
    protected JarOutputStream createJar( File jarFile, Manifest mf )
        throws IOException
    {
        jarFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream( jarFile );
        try
        {
            return new JarOutputStream( fos, mf );
        }
        catch ( IOException e )
        {
            try
            {
                fos.close();
                jarFile.delete();
            }
            catch ( IOException e1 )
            {
                // ignore
            }
            throw e;
        }
    }

    /**
     * Create a manifest for the jar file
     *
     * @return a default manifest; the Manifest-Version and Created-By attributes are initialized
     */
    protected Manifest createManifest()
    {
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        attrs.putValue( Attributes.Name.MANIFEST_VERSION.toString(), "1.0" );
        attrs.putValue( "Created-By", "JarMaker" );

        for ( Iterator i = manifestEntries.keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            attrs.putValue( key, (String) manifestEntries.get( key ) );
        }

        return mf;
    }

    /**
     * Add all entries in the supplied Map to the jar
     *
     * @param jos      a JarOutputStream that can be used to write to the jar
     * @param includes a Map<String, File> of entries to add
     * @throws IOException if there is a problem writing the archive or reading the sources
     */
    protected void addEntries( JarOutputStream jos, Map includes )
        throws IOException
    {
        for ( Iterator i = includes.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            String name = (String) entry.getKey();
            File file = (File) entry.getValue();
            addEntry( jos, name, file );
        }
    }

    /**
     * Add a single entry to the jar
     *
     * @param jos    a JarOutputStream that can be used to write to the jar
     * @param name   the entry name to use; must be '/' delimited
     * @param source the file to add
     * @throws IOException if there is a problem writing the archive or reading the sources
     */
    protected void addEntry( JarOutputStream jos, String name, File source )
        throws IOException
    {
        FileInputStream fis = new FileInputStream( source );
        try
        {
            jos.putNextEntry( new JarEntry( name ) );
            int count;
            while ( ( count = fis.read( buffer ) ) > 0 )
            {
                jos.write( buffer, 0, count );
            }
            jos.closeEntry();
        }
        finally
        {
            fis.close();
        }
    }


}
