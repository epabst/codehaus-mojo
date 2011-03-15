package org.codehaus.mojo.jetty;

/**
 * The MIT License
 *
 * Copyright (c) 2005, Kristian Nordal
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.MultiException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @goal run-war
 *
 * @execute phase="package"
 *
 * @description Maven 2 Jetty plugin
 *
 * A Maven 2 plugin for running a war file as a web application in Jetty
 * @author <a href="mailto:kristian.nordal@gmail.com">Kristian Nordal</a>
 */
public class RunWarMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="8080"
     * @required
     */
    private int port;

    /**
     * The context path.
     * @parameter expression="/app"
     * @required
     */
    private String contextPathSpec;

    /**
     * The location of the war file.
     * @parameter expression="${project.build.directory}/${project.build.finalName}.war"
     * @required
     */
    private String webApp;

    /**
     * An optional jetty xml configuration file, will override the other parameters.
     * @parameter
     */
    private String jettyConfig;

    public void execute()
        throws MojoExecutionException
    {
        ClassLoader classLoader = this.getClass().getClassLoader();

        Thread.currentThread().setContextClassLoader( classLoader );

        Server server = new Server();

        try
        {
            if ( jettyConfig != null )
            {
                getLog().info( "Configuring Jetty from xml configuration file: " + jettyConfig );

                server.configure( jettyConfig );
            }
            else
            {
                getLog().info( "Configuring Jetty from the parameters." );

                SocketListener listener = new SocketListener();

                listener.setPort( port );

                server.addListener(listener);

                server.addWebApplication( contextPathSpec, webApp );
            }
        }
        catch ( IOException ex )
        {
            throw new MojoExecutionException( "Failed to configure the server.", ex );
        }

        try
        {
            getLog().info( "Starting Jetty..." );

            server.start();

            getLog().info( "Jetty is running!" );

            // Better way to let the server run?
            Thread.sleep( 1000000 );
        }
        catch ( MultiException ex )
        {
            List exceptions = ex.getExceptions();

            getLog().info( "Caught a MultiException containing " + exceptions.size() + " exception(s)." );
            
            for ( Iterator it = exceptions.iterator(); it.hasNext(); )
            {
                getLog().info( "=====================================================================", (Exception) it.next() );
            }

            // TODO: Replave with a MojoFailureException
            throw new MojoExecutionException( "Failed to start Jetty." );
        }
        catch ( InterruptedException ex )
        {
            // ignore, we've been signalled and should stop.
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Failed to start the server", ex );
        }
    }
}
