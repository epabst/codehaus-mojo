package org.apache.maven.diagrams.connectors.classes;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.codehaus.plexus.util.DirectoryScanner;

/**
 * The class provides methods to get list of classes contained in given directory or jar.
 * 
 * 
 * TODO: Should be merged with: org.apache.maven.shared.dependency.analyzer.ClassAnalyzer
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class PackageUtils
{

    /**
     * Returns list of fully dot-qualified class names contained in given jar
     */
    private static List<String> getClassNamesInJar( URL jarPath ) throws IOException
    {
        return getClassNamesInJar( jarPath.openStream() );
    }

    /**
     * Returns list of fully dot-qualified class names contained in given jar InputScreen
     */
    private static List<String> getClassNamesInJar( InputStream is ) throws IOException
    {
        ArrayList<String> classes = new ArrayList<String>();

        JarInputStream jarFile = new JarInputStream( is );
        JarEntry jarEntry;

        while ( true )
        {
            jarEntry = jarFile.getNextJarEntry();
            if ( jarEntry == null )
            {
                break;
            }
            if ( jarEntry.getName().endsWith( ".class" ) )
            {
                classes.add( classFileToClassName( jarEntry.getName() ) );
            }
        }
        return classes;
    }

    /**
     * Returns list of fully dot-qualified class names contained in given directory
     */
    private static List<String> getClassNamesInDirectory( URL directory ) throws IOException, URISyntaxException
    {
        ArrayList<String> classes = new ArrayList<String>();

        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir( new File( directory.toURI() ) );
        directoryScanner.setIncludes( new String[] { "**/*.class" } );
        directoryScanner.addDefaultExcludes();
        directoryScanner.scan();
        String[] files = directoryScanner.getIncludedFiles();

        for ( String file : files )
        {
            classes.add( classFileToClassName( file ) );
        }
        return classes;
    }

    /**
     * Returns list of fully dot-qualified class names contained in given jar or directory (classpath item)
     */
    public static List<String> getClassNamesOnClassPathItem( URL item ) throws URISyntaxException
    {
        try
        {
            File f = new File( item.toURI() );

            if ( f.isDirectory() )
                return getClassNamesInDirectory( item );

            if ( f.isFile() )
                return getClassNamesInJar( item );

            return null;
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    /*
     * TODO: Move to single place
     */
    static protected String classFileToClassName( String file )
    {
        return file.substring( 0, file.length() - 6 ).replace( "/", "." ).replace( "\\", "." );

    }
};
