package org.codehaus.mojo.rmic;

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
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SunRmiCompiler
    extends AbstractLogEnabled
    implements RmiCompiler
{
    /**
     * The name of the class to use for rmi compilation.
     */
    public static final String RMIC_CLASSNAME = "sun.rmi.rmic.Main";
    
    /**
     * Execute the compiler
     * 
     * @param rmiConfig The config object
     * @param classesToCompile The list of classes to rmi compile
     * @throws RmiCompilerException if there is a problem during compile
     */
    public void execute( RmicConfig rmiConfig, List classesToCompile )
        throws RmiCompilerException
    {
        // ----------------------------------------------------------------------
        // Construct the RMI Compiler's class path.
        // ----------------------------------------------------------------------

        File toolsJar = new File( System.getProperty( "java.home" ), "../lib/tools.jar" );
        
        URLClassLoader classLoader = null;
        try 
        {
            URL [] classpathUrls = { toolsJar.toURL() };
            classLoader = new URLClassLoader( classpathUrls, null );
        }
        catch ( MalformedURLException e )
        {
            throw new RmiCompilerException( "Unable to resolve tools.jar: " + toolsJar );
        }

        Class rmicMainClass = null;

        // ----------------------------------------------------------------------
        // Try to load the rmic class
        // ----------------------------------------------------------------------

        try
        {
            rmicMainClass = classLoader.loadClass( RMIC_CLASSNAME );
        }
        catch ( ClassNotFoundException e )
        {
            getLogger().warn( "Could not find rmi compiler: " + RMIC_CLASSNAME );
            getLogger().info( "Within this classpath:" );

            for ( int it = 0; it < classLoader.getURLs().length; ++it )
            {
                URL url = classLoader.getURLs()[it];

                getLogger().info( " * " + url.toExternalForm() );
            }
            throw new RmiCompilerException( "Could not find " + RMIC_CLASSNAME + " on the classpath." );
        }

        // ----------------------------------------------------------------------
        // Build the argument list
        // ----------------------------------------------------------------------

        List arguments = new ArrayList();

        List classpathList = rmiConfig.getRmicClasspathElements();
        if ( classpathList.size() > 0 )
        {
            StringBuffer classpath = new StringBuffer();

            for ( int i = 0; i < classpathList.size(); i++ )
            {
                classpath.append( classpathList.get( i ) + File.pathSeparator );
            }

            arguments.add( "-classpath" );

            arguments.add( classpath.toString() );
        }

        arguments.add( "-d" );

        arguments.add( rmiConfig.getOutputDirectory().getAbsolutePath() );
        
        if ( rmiConfig.getVersion() != null )
        {
            arguments.add( "-v" + rmiConfig.getVersion() );
        }
        
        if ( rmiConfig.isIiop() )
        {
            arguments.add( "-iiop" );
            
            if ( rmiConfig.isNoLocalStubs() )
            {
                arguments.add( "-nolocalstubs" );
            }
        }
        
        if ( rmiConfig.isIdl() )
        {
            arguments.add( "-idl" );
            
            if ( rmiConfig.isNoValueMethods() )
            {
                arguments.add( "-noValueMethods" );
            }
        }
        
        if ( rmiConfig.isKeep() )
        {
            arguments.add( "-keep" );
        }
        
        if ( getLogger().isDebugEnabled() || rmiConfig.isVerbose() )
        {
            arguments.add( "-verbose" );
        }
        else if ( rmiConfig.isNowarn() )
        {
            arguments.add( "-nowarn" );
        }

        for ( Iterator it = classesToCompile.iterator(); it.hasNext(); )
        {
            String remoteClass = (String) it.next();

            arguments.add( remoteClass );
        }

        String[] args = (String[]) arguments.toArray( new String[arguments.size()] );

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "rmic arguments: " );

            for ( int i = 0; i < args.length; i++ )
            {
                String arg = args[i];

                getLogger().debug( arg );
            }
        }

        executeMain( rmicMainClass, args );
    }

    /**
     * 
     * @param rmicMainClass The class to use to run the rmic
     * @param args Arguments to be passed to rmic
     * @throws RmiCompilerException If there is a problem during the compile
     */
    private void executeMain( Class rmicMainClass, String[] args )
        throws RmiCompilerException
    {
        Method compile;

        Object main;

        try
        {
            Constructor constructor = rmicMainClass.getConstructor( new Class[] { OutputStream.class, String.class } );

            main = constructor.newInstance( new Object[] { System.out, "rmic" } );

            compile = rmicMainClass.getMethod( "compile", new Class[] { String[].class } );
        }
        catch ( NoSuchMethodException e )
        {
            throw new RmiCompilerException( "Error while initializing rmic.", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RmiCompilerException( "Error while initializing rmic.", e );
        }
        catch ( InvocationTargetException e )
        {
            throw new RmiCompilerException( "Error while initializing rmic.", e );
        }
        catch ( InstantiationException e )
        {
            throw new RmiCompilerException( "Error while initializing rmic.", e );
        }

        try
        {
            compile.invoke( main, new Object[] { args } );
        }
        catch ( IllegalAccessException e )
        {
            throw new RmiCompilerException( "Error while executing rmic.", e );
        }
        catch ( InvocationTargetException e )
        {
            throw new RmiCompilerException( "Error while executing rmic.", e );
        }
    }
}
