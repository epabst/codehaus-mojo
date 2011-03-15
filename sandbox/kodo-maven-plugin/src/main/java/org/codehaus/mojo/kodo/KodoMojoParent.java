/*
 *  Copyright 2005-2006 Brian Fox (brianefox@gmail.com)
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


package org.codehaus.mojo.kodo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

abstract public class KodoMojoParent extends AbstractMojo
{
    private static final String[] EMPTY_STRING_ARRAY = {};

    
    Log log;
    
    /**
     * Location of files to Enhance.
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    protected File searchDir;

    /**
     * Location of class files
     * 
     * @parameter expression="${project.build.outputDirectory}"
     *  
     */
    protected File classDir;

    /**
     * Where to create the enhanced class files
     * 
     * @parameter
     */
    protected File outputDir = null;

    /**
     * Location of Kodo.Properties File
     * 
     * @parameter expression="${project.resources}"
     * @readonly
     */
    protected ArrayList resources;

    /**
     * Comma separated list of includes to scan searchDir to pass to enhancer.
     * Can be java, jdo or class files. Default is **\*.jdo.
     * 
     * @parameter
     */
    protected String includes = "**/*.jdo";

    /**
     * Comma separated list of excludes to scan searchDir to pass to enhancer.
     * Can be java, jdo or class files. Default is "".
     * 
     * @parameter
     */
    protected String excludes = "";

    /**
     * Array of extra files/folders to add to classpath. Any path that ends with
     * a '/' is assumed to refer to a directory. Otherwise, the file is assumed
     * to refer to a JAR file which will be opened as needed. (See
     * URLClassloader Javadoc)
     * 
     * @parameter
     */
    protected File[] extraClassPathItems;
    
    KodoMojoParent()
    {
        super();
        log = getLog();
    }
    
    /**
     * Adds nessessary items to the classloader.
     * 
     * @return ClassLoader original Classloader.
     * @throws MojoExecutionException
     */
    public ClassLoader setupClassloader()
        throws MojoExecutionException
    {
        
        URLClassLoader loader = null;
        ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
        log.info(originalLoader.toString());
        URL[] urls = buildClassPathURLs();
        loader = new URLClassLoader( urls, originalLoader );

        Thread.currentThread().setContextClassLoader( loader );
        printClassPath();
        return originalLoader;

    }

    /**
     * Build the array of URLs to add to new ClassLoader
     * 
     * @return
     * @throws MojoExecutionException
     */
    private URL[] buildClassPathURLs()
        throws MojoExecutionException
    {
        ArrayList urls = new ArrayList();
        Iterator iter = resources.iterator();
        try
        {
            //add the Resource locations as urls
            while ( iter.hasNext() )
            {
                Resource resource = (Resource) iter.next();
                urls.add( new URL( "file:///" + resource.getDirectory() + "/" ) );
            }

            //add the outputDirectory
            urls.add( new URL( "file:///" + classDir.getAbsolutePath() + "/" ) );

            //add the extra files to classpath
            if ( null != extraClassPathItems )
            {
                for ( int i = 0; i < extraClassPathItems.length; i++ )
                {
                    urls.add( buildURL( extraClassPathItems[i] ) );
                }
            }
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Error adding URL to classpath. Nested Exception:", e );
        }
        //convert back to array
        URL[] urlArray = (URL[]) urls.toArray( new URL[urls.size()] );
        return urlArray;
    }

    /**
     * Build URL from File
     * 
     * @throws MalformedURLException
     *  
     */
    private URL buildURL( File file )
        throws MalformedURLException
    {
        StringBuffer urlStr = new StringBuffer( "file:///" + file.getAbsolutePath() );
        if ( file.isDirectory() )
        {
            urlStr.append( "/" );
        }
        return new URL( urlStr.toString() );
    }

    /**
     * Print the new classpath for debugging
     *  
     */
    public void printClassPath()
    {
        //Get the Classloader
        ClassLoader sysClassLoader = Thread.currentThread().getContextClassLoader();
        //Get the URLs
        URL[] urls = ( (URLClassLoader) sysClassLoader ).getURLs();
        log.debug( "Added to Classpath:" );
        for ( int i = 0; i < urls.length; i++ )
        {
            log.debug( urls[i].getFile() );
        }
    }

    /**
     * Returns a list of filenames that should be passed to the enhancer
     * 
     * @param sourceDir
     *            the searchDir to be scanned
     * @return the array of filenames, relative to the sourceDir
     */
    protected String[] getIncludedFiles( File sourceDir )
    {

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( sourceDir );
        scanner.setIncludes( getIncludes() );
        scanner.setExcludes( getExcludes() );

        scanner.scan();

        return scanner.getIncludedFiles();
    }

    /**
     * Returns a string array of the includes to be used when searching for
     * items to pass to the enhancer
     * 
     * @return an array of tokens to include
     */
    private String[] getIncludes()
    {
        List includeList = new ArrayList();
        if ( includes != null && !"".equals( includes ) )
        {
            includeList.addAll( Arrays.asList( StringUtils.split( includes, "," ) ) );
        }
        return (String[]) includeList.toArray( EMPTY_STRING_ARRAY );
    }

    /**
     * Returns a string array of the excludes to be used when searching for
     * items to pass to the enhancer
     * 
     * @return an array of tokens to exclude
     */
    private String[] getExcludes()
    {
        List excludeList = new ArrayList();
        if ( excludes != null && !"".equals( excludes ) )
        {
            excludeList.addAll( Arrays.asList( StringUtils.split( excludes, "," ) ) );
        }
        return (String[]) excludeList.toArray( EMPTY_STRING_ARRAY );
    }

}
