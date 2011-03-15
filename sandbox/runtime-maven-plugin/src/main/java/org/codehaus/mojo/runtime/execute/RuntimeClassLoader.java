package org.codehaus.mojo.runtime.execute;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.codehaus.mojo.runtime.model.Runtime;

/**
 * The RuntimeClassLoader is meant to be used inside of a jar file and takes in a
 * Runtime object (as defined by the runtime mojo, not the System Runtime) and
 * uses the dependencies specified in it to construct references to jar files
 * inside of that jar file.
 * <p/>
 * This classloader can be used with a RuntimeExecutor to generate a complete
 * execution environment.
 * <p/>
 * NOTE: the classes that the RuntimeExecutor are trying in instantiate need to be
 * in their own jars and referenced as dependencies as well, if they are in the same
 * loader as the RuntimeExecutor class then they are loaded by the parent classloader
 * regardless if they are loaded _through_ this classloader...meaning they are unable
 * to resolve the dependencies this classloader services.
 *
 * @author jesse
 * @version $Id$
 */
public class RuntimeClassLoader
    extends SecureClassLoader
{
    private Map classMap = new HashMap();

    private List dependencyUrlList = new ArrayList();

    /**
     * constructor for the classloader
     *
     * @param runtime
     * @param parentLoader
     */
    public RuntimeClassLoader( Runtime runtime, ClassLoader parentLoader )
    {

        super( parentLoader );

        try
        {
            List dependencies = runtime.getJar().getDependencies();

            for ( int i = 0; i < dependencies.size(); ++i )
            {
                String dependency = (String) dependencies.get( i );
                String[] data = dependency.split( ":" );

                if ( data.length != 3 )
                {
                    throw new IllegalArgumentException( "invalid dependency string '" + dependency + "'" );
                }
                StringBuffer sb = new StringBuffer();
                sb.append( "lib" ).append( File.separator ).append( data[1] ).append( "-" );
                sb.append( data[2] ).append( ".jar" );

                if ( Thread.currentThread().getContextClassLoader().getResource( sb.toString() ) != null )
                {
                    dependencyUrlList.add(
                        Thread.currentThread().getContextClassLoader().getResource( sb.toString() ) );
                }
            }
        }
        catch ( Exception e )
        {
            System.err.println( "FATAL: error constructing classpath: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    /**
     * @see ClassLoader
     */
    public synchronized Class findClass( String className )
        throws ClassNotFoundException
    {
        String classPath = className.replace( '.', '/' ) + ".class";

        //System.out.println("looking for: " + className );

        if ( this.classMap.containsKey( classPath ) )
        {
            //System.out.println("getting loaded class" + className);
            return (Class) this.classMap.get( classPath );
        }

        try
        {
            for ( Iterator i = dependencyUrlList.iterator(); i.hasNext(); )
            {

                URL jarUrl = (URL) i.next();

                //System.out.println("checking " + jarUrl.toExternalForm());

                JarInputStream in = new JarInputStream( jarUrl.openStream() );

                try
                {
                    JarEntry entry;

                    while ( ( entry = in.getNextJarEntry() ) != null )
                    {
                        if ( entry.getName().equals( classPath ) )
                        {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();

                            try
                            {
                                byte[] buffer = new byte[2048];

                                int read;

                                while ( in.available() > 0 )
                                {
                                    read = in.read( buffer, 0, buffer.length );

                                    if ( read < 0 )
                                    {
                                        break;
                                    }

                                    out.write( buffer, 0, read );
                                }

                                buffer = out.toByteArray();

                                //System.out.println("defining class " + className);

                                Class cls = defineClass( className, buffer, 0, buffer.length );
                                //System.out.println("linking class " + className);
                                resolveClass( cls );

                                this.classMap.put( className, cls );

                                return cls;
                            }
                            finally
                            {
                                out.close();
                            }
                        }
                    }
                }
                finally
                {
                    in.close();
                }
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw new ClassNotFoundException( "io error reading stream for: " + className );
        }

        return super.findClass( className );

        //        throw new ClassNotFoundException( className );
    }

    protected synchronized Class loadClass( String className, boolean resolve )
        throws ClassNotFoundException
    {
        //System.out.println(className + " runtime classloader bool" + resolve);
        Class c = super.loadClass( className, resolve );

        if ( c != null )
        {
            return c;
        }
        else
        {
            if ( resolve )
            {
                c = findClass( className );
                if ( c != null )
                {
                    resolveClass( c );
                    return c;
                }
                else
                {
                    throw new ClassNotFoundException( className );
                }
            }
            else
            {
                return findClass( className );
            }
        }
    }

    public Class loadClass( String className )
        throws ClassNotFoundException
    {
        //System.out.println(className + " runtime classloader");

        Class c = super.loadClass( className );

        if ( c == null )
        {
            return findClass( className );
        }
        else
        {
            return c;
        }
    }
}
