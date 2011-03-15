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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.mojo.runtime.model.Executable;
import org.codehaus.mojo.runtime.model.Runtime;
import org.codehaus.mojo.runtime.model.io.xpp3.RuntimeXpp3Reader;

/**
 * The default implementation of an executor for the runtime plugin jar implementation.
 * <p/>
 * The executor acts as a conduit to main methods that are keyed in the runtime descriptor.
 * <p/>
 * Everything is wrapped up in their own jars with the Executor and the RuntimeClassLoader as
 * the only classes in the jar.  This allows the runtime classloader to load up all of the
 * underlying dependencies main methods referred to in the runtime descriptor
 *
 * @author jesse
 * @version $Id$
 */
public class RuntimeExecutor
{
    private Runtime runtime = null;

    private Map executables = null;

    /**
     * harvest the jar.runtime descriptor from the jar and pull out executables map
     */
    public void init()
    {
        try
        {
            RuntimeXpp3Reader modelReader = new RuntimeXpp3Reader();

            InputStream is =
                Thread.currentThread().getContextClassLoader().getResourceAsStream( "META-INF/runtimes/jar.runtime" );

            if ( is != null )
            {
                runtime = modelReader.read( new InputStreamReader( is ) );

                executables = runtime.getJar().getExecutableMap();

                List dependencies = runtime.getJar().getDependencies();

                for ( Iterator i = dependencies.iterator(); i.hasNext(); )
                {
                    System.out.println( "dependency: " + i.next() );
                }
            }
            else
            {
                System.err.println( "unable to retrieve jar.runtime" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void processRequest( String[] args )
    {
        String executableId = args[0];

        if ( "list".equalsIgnoreCase( executableId ) )
        {
            System.out.println( "available execution ids:" );
            for ( Iterator i = executables.keySet().iterator(); i.hasNext(); )
            {
                System.out.println( "\t" + (String) i.next() );
            }
        }
        else if ( "help".equalsIgnoreCase( executableId ) )
        {
            printOptions();
        }
        else if ( executables != null && executables.containsKey( executableId ) )
        {
            try
            {

                ClassLoader loader = new RuntimeClassLoader( runtime, getClass().getClassLoader() );
                Thread.currentThread().setContextClassLoader( loader );

                Class c = loader.loadClass( ( (Executable) executables.get( executableId ) ).getImplementation() );

                Class types[] = {String[].class};

                Method m = c.getMethod( "main", types );

                // prune the id off of the args list
                String[] truncatedArgs = new String[args.length - 1];

                System.arraycopy( args, 1, truncatedArgs, 0, args.length - 1 );

                m.invoke( m, new Object[]{truncatedArgs} );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.err.println( "unavailable or unknown executable id: " + executableId );
        }

    }

    private static void printOptions()
    {
        System.err.println( "java -jar <jar> [id|help|list] arg1 arg2 ..." );
    }

    public static void main( String[] args )
    {
        if ( args.length < 1 )
        {
            printOptions();
            System.exit( -1 );
        }

        RuntimeExecutor executor = new RuntimeExecutor();

        executor.init();

        executor.processRequest( args );
    }
}
