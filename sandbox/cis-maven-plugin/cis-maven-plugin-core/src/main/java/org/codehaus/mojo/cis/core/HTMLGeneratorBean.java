/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.mojo.cis.core;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * The HTML generator is used to convert the XML layout files into
 * HTML files. It is invoked after creating the web application
 * directory. In particular, it creates its own classpath by using
 * files in the WEB-INF/lib directory.
 */
public class HTMLGeneratorBean extends AbstractCisBean
{
    private boolean htmlDirChecked, xmlTargetDirChecked, logDirChecked;
    private boolean accessPathDirChecked;
    private Object htmlGenerator;
    private Method htmlGeneratorMethod;
    private File xmlDir, xmlTargetDir, htmlDir, logDir, accessPathDir;

    /**
     * Returns the directory containing the XML layouts. Basically,
     * this is the source directory.
     */
    public File getXmlDir()
    {
        return xmlDir;
    }

    /**
     * Sets the directory containing the XML layouts. Basically,
     * this is the source directory.
     */
    public void setXmlDir( File pXmlDir )
    {
        xmlDir = pXmlDir;
    }


    /**
     * Returns the target directory for XML layouts. If present, this
     * is used as the target directory for uptodate checks. Otherwise,
     * the {@link #getHtmlDir() HTML directory} is used.
     */
    public File getXmlTargetDir()
    {
        return xmlTargetDir;
    }

    /**
     * Sets the target directory for XML layouts. If present, this
     * is used as the target directory for uptodate checks. Otherwise,
     * the {@link #getHtmlDir() HTML directory} is used.
     */
    public void setXmlTargetDir( File pXmlTargetDir )
    {
        xmlTargetDir = pXmlTargetDir;
    }

    /**
     * Returns the directory containing the generated HTML files.
     * Basically, this is the target directory.
     */
    public File getHtmlDir()
    {
        return htmlDir;
    }

    /**
     * Sets the directory containing the generated HTML files.
     * Basically, this is the target directory.
     */
    public void setHtmlDir( File pHtmlDir )
    {
        htmlDir = pHtmlDir;
    }

    /**
     * Returns the directory containing the log files.
     */
    public File getLogDir()
    {
        return logDir;
    }

    /**
     * Sets the directory containing the log files.
     */
    public void setLogDir( File pLogDir )
    {
        logDir = pLogDir;
    }

    /**
     * Returns the directory containing the accesspath files.
     */
    public File getAccessPathDir()
    {
        return accessPathDir;
    }

    /**
     * Sets the directory containing the accesspath files.
     */
    public void setAccessPathDir( File pAccessDir )
    {
        accessPathDir = pAccessDir;
    }

    private File makeFileName( File pXmlFile, File pTargetDir, String pExtension )
    {
        final String xmlName = pXmlFile.getName();
        final String newName = xmlName.substring( 0, xmlName.length() - ".xml".length() ) + pExtension;
        return new File( pTargetDir, newName );
    }

    /**
     * Prepares the settings for accessing the CIS home directory.
     */
    private Object prepareHtmlGenerator( File pLogDir ) throws CisCoreException
    {
        if ( htmlGenerator != null )
        {
            return htmlGenerator;
        }
        File cisHomeDirectory = checkCisHomeDir();
        System.setProperty( "cis.home", cisHomeDirectory.getPath() );
        final File webInfClassesDirectory = new File( new File( cisHomeDirectory, "WEB-INF" ), "classes" );
        final List urls = new ArrayList();
        if ( webInfClassesDirectory.isDirectory() )
        {
        	try
        	{
        		urls.add( webInfClassesDirectory.toURI().toURL() );
        	}
        	catch ( MalformedURLException e )
        	{
        		throw new CisCoreErrorMessage( e );
        	}
        }
        final File webInfLibDirectory = new File( new File( cisHomeDirectory, "WEB-INF" ), "lib" );
        if ( !webInfLibDirectory.isDirectory() )
        {
            throw new CisCoreErrorMessage( "The configured CIS home directory "
                                           + cisHomeDirectory
                                           + " does not contain a subdirectory WEB-INF/lib." );
        }
        final File[] files = webInfLibDirectory.listFiles( new FilenameFilter()
        {
			public boolean accept( File pDirectory, String pFileName )
			{
				return pFileName.endsWith( ".jar" );
			}
        } );
        Arrays.sort( files, new Comparator()
        {
			public int compare( Object pFile1, Object pFile2 ) {
				final String s1 = ( (File) pFile1 ).getName();
				final String s2 = ( (File) pFile2 ).getName();
				return s1.compareToIgnoreCase( s2 );
			}
        } );
        for ( int i = 0;  i < files.length;  i++ )
        {
        	final File f = files[ i ];
            if ( f.isFile() )
            {
            	try
            	{
            		urls.add( f.toURI().toURL() );
            	}
            	catch ( MalformedURLException e )
            	{
            		throw new CisCoreErrorMessage( e );
            	}
            }
        }
        final URL[] urlArray = (URL[]) urls.toArray( new URL[ urls.size() ] );
        final ClassLoader cl = new URLClassLoader( urlArray, ClassLoader.getSystemClassLoader() );
        final CISInitializer cisInitializer = new CISInitializer();
        cisInitializer.setCisHomeDir( cisHomeDirectory );
        cisInitializer.setLogDir( pLogDir );
        cisInitializer.setCisUtils( getCisUtils() );
        cisInitializer.setTempDir( getCisUtils().getTempDir() );
        cisInitializer.init( cl );
        final String className = "com.softwareag.cis.gui.generate.HTMLGenerator";
        System.setProperty( className + ".running", "true" );
        final Class clazz;
        try
        {
            clazz = cl.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new CisCoreException( "Failed to load class " + className + ": "
                                        + e.getMessage(), e );
        }
        final Object o;
        try
        {
            o = clazz.newInstance();
        }
        catch ( InstantiationException e )
        {
            throw new CisCoreException( "Failed to instantiate class " + className + ": "
                                        + e.getMessage(), e );
        }
        catch ( IllegalAccessException e )
        {
            throw new CisCoreException( "Illegal access to class " + className + ": "
                                        + e.getMessage(), e );
        }
        final Method m;
        try
        {
            final Class[] parameterTypes = new Class[]
            {
                String.class, String.class, String.class, String.class
            };
            m = clazz.getMethod( "generateHTMLFile", parameterTypes );
        }
        catch ( NoSuchMethodException e )
        {
            throw new CisCoreException( "The class " + className
                                        + " contains no method generateHTMLFile(String,String,String,String)."
                                        , e );
        }
        htmlGenerator = o;
        htmlGeneratorMethod = m;
        return htmlGenerator;
    }

    /**
     * Invokes the HTML generator to convert the given layout into the
     * given HTML file.
     */
    private void runHtmlGenerator( File pXmlFile,
                                   File pHtmlFile, File pLogFile,
                                   File pAccessPathFile ) throws CisCoreException
    {
        final Object[] args = new Object[]
        {
            pXmlFile.getPath(),
            pHtmlFile.getPath(),
            pLogFile.getPath(),
            pAccessPathFile.getPath()
        };
        try
        {
            htmlGeneratorMethod.invoke( htmlGenerator, args );
        }
        catch ( IllegalAccessException e )
        {
            throw new CisCoreException( "Illegal access to method "
                                        + htmlGeneratorMethod.getName()
                                        + " of class " + htmlGenerator.getClass().getName()
                                        + ": " + e.getMessage(), e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new CisCoreException( "Illegal argument for method "
                                        + htmlGeneratorMethod.getName()
                                        + " of class " + htmlGenerator.getClass().getName()
                                        + ": " + e.getMessage(), e );
        }
        catch ( InvocationTargetException e )
        {
            final Throwable t = e.getTargetException();
            throw new CisCoreException( "Failed to invoke method "
                                        + htmlGeneratorMethod.getName()
                                        + " of class " + htmlGenerator.getClass().getName()
                                        + ": " + t.getMessage(), t );
        }
    }

    /**
     * Called to generate a single HTML file from its corresponding
     * XML file.
     */
    private boolean generate( File pXmlFile ) throws CisCoreException
    {
        final CisUtils.Resource sourceResource = new DefaultResource( pXmlFile );
        final File xmlTargetDirectory = checkXmlTargetDirectory();
        final File htmlDirectory = checkHtmlDirectory();
        final CisUtils utils = getCisUtils();
        final File htmlFile = makeFileName( pXmlFile, htmlDirectory, ".html" );
        final File xmlTargetFile = xmlTargetDirectory == null ? null : new File( xmlTargetDirectory, pXmlFile.getName() );
        final File targetFile =  xmlTargetFile == null ? htmlFile : xmlTargetFile;
        final CisUtils.Resource targetResource = new DefaultResource( targetFile );
        if ( utils.isUpToDate( sourceResource, targetResource ) )
        {
            utils.debug( "File " + htmlFile + " is uptodate." );
            return false;
        }
        utils.debug( "HTML file " + htmlFile + " is being generated." );
        final File accessPathDirectory = checkAccessPathDirectory();
        final File accessPathFile = makeFileName( pXmlFile, accessPathDirectory, ".access" );
        final File logDirectory = checkLogDirectory();
        final File logFile = makeFileName( pXmlFile, logDirectory, ".log" );
        prepareHtmlGenerator( logDirectory );
        boolean success = false;
        try
        {
            if ( xmlTargetFile != null )
            {
                utils.copy( pXmlFile, xmlTargetFile );
            }
            runHtmlGenerator( pXmlFile, htmlFile, logFile, accessPathFile );
            success = true;
        }
        finally
        {
            if ( !success  &&  xmlTargetFile != null )
            {
                /* Remove the XML target file, so that the generator will run again
                 * the next time.
                 */
                xmlTargetFile.delete();
            }
        }
        return true;
    }

    private File checkAccessPathDirectory() throws CisCoreErrorMessage, CisCoreException
    {
        final File accessPathDirectory = getAccessPathDir();
        if ( !accessPathDirChecked )
        {
            if ( accessPathDirectory == null )
            {
                throw new CisCoreErrorMessage( "The access path directory is not set." );
            }
            if ( !accessPathDirectory.isDirectory()  &&  !accessPathDirectory.mkdirs() )
            {
                throw new CisCoreException( "Unable to create the configured access path directory "
                                            + accessPathDirectory.getPath() );
            }
            accessPathDirChecked = true;
        }
        return accessPathDirectory;
    }

    private File checkLogDirectory() throws CisCoreErrorMessage, CisCoreException
    {
        final File logDirectory = getLogDir();
        if ( !logDirChecked )
        {
            if ( logDirectory == null )
            {
                throw new CisCoreErrorMessage( "The log directory is not set." );
            }
            if ( !logDirectory.isDirectory()  &&  !logDirectory.mkdirs() )
            {
                throw new CisCoreException( "Unable to create the configured log directory "
                                            + logDirectory.getPath() );
            }
            logDirChecked = true;
        }
        return logDirectory;
    }

    private File checkXmlTargetDirectory() throws CisCoreException
    {
        final File xmlTargetDirectory = getXmlTargetDir();
        if ( !xmlTargetDirChecked )
        {
            if ( xmlTargetDirectory != null
                    &&  !xmlTargetDirectory.isDirectory()
                    &&  !xmlTargetDirectory.mkdirs() )
            {
                throw new CisCoreException( "Failed to create XML target directory "
                                            + xmlTargetDirectory.getPath() );
            }
            xmlTargetDirChecked = true;
        }
        return xmlTargetDirectory;
    }

    private File checkHtmlDirectory() throws CisCoreException
    {
        final File htmlDirectory = getHtmlDir();
        if ( !htmlDirChecked )
        {
            if ( htmlDirectory == null )
            {
                throw new CisCoreErrorMessage( "The HTML directory is not set." );
            }
            if ( !htmlDirectory.isDirectory()  &&  !htmlDirectory.mkdirs() )
            {
                throw new CisCoreException( "Unable to create the configured HTML directory "
                                            + htmlDirectory.getPath() );
            }
            htmlDirChecked = true;
        }
        return htmlDirectory;
    }

    /**
     * Called to scan the {@link #setXmlDir(File)} for XML
     * layouts and generating the HTML layouts.
     */
    public void execute() throws CisCoreException {
        final File xmlDirectory = getXmlDir();
        if ( xmlDirectory == null )
        {
            throw new CisCoreErrorMessage( "The XML directory is not set." );
        }
        if ( !xmlDirectory.isDirectory() )
        {
            throw new CisCoreErrorMessage( "The configured XML directory "
                                           + xmlDirectory.getPath()
                                           + " does not exist or is no directory." );
        }
        final File[] files = xmlDirectory.listFiles( new FilenameFilter()
        {
            public boolean accept( File pDir, String pName )
            {
                return pName.endsWith( ".xml" );
            }
        } );
        int numGeneratedFiles = 0;
        int numUpToDateFiles = 0;
        for ( int i = 0;  i < files.length;  i++ )
        {
            if ( generate( files[i] ) ) 
            {
                ++numGeneratedFiles;
            }
            else
            {
                ++numUpToDateFiles;
            }
        }
        logResult( numGeneratedFiles, numUpToDateFiles );
    }

    private void logResult( int numGeneratedFiles, int numUpToDateFiles )
    {
        if ( numGeneratedFiles == 0 )
        {
            getCisUtils().debug( "All files are uptodate." );
        }
        else
        {
            final StringBuffer sb = new StringBuffer();
            sb.append("Generated ");
            sb.append(String.valueOf(numGeneratedFiles));
            sb.append(" HTML ");
            if (numGeneratedFiles == 1)
            {
                sb.append("file");
            }
            else
            {
                sb.append("files");
            }
            sb.append(", ");
            sb.append(String.valueOf(numUpToDateFiles));
            if (numUpToDateFiles == 1)
            {
                sb.append(" file has been uptodate.");
            }
            else {
                sb.append(" files have been uptodate.");
            }
            getCisUtils().info( sb.toString() );
        }
    }
}
