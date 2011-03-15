package org.codehaus.mojo.ruby;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.component.jruby.JRubyRuntimeInvoker;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.util.StringOutputStream;
import org.jruby.exceptions.RaiseException;

/**
 * This is the default implementation for the RubyMojo, which
 * uses the RubyInvoker.
 * @author Eric Redmond
 */
public class DefaultRubyMojo
    extends AbstractMojo
    implements RubyMojo
{
    private JRubyInvoker jinvoker;

    private Object returned;

    public DefaultRubyMojo( JRubyInvoker invoker )
    {
        this.jinvoker = invoker;
    }

    /**
     * Sets any string key with an object value.
     * @param key identifier for this object.
     * @param value some value object.
     */
    public void set( String key, Object value )
    {
        if ( "basedir".equals( key ) )
        {
            jinvoker.setCurrentDirectory( value.toString() );
            jinvoker.putGlobal( key, value );
        }
        else if ( "libraryPaths".equals( key ) )
        {
//          TODO: for future use
//            jinvoker.setLibraryPaths( (String[])value );
        }
        else if ( "requires".equals( key ) )
        {
            jinvoker.setRequires( (String[])value );
        }
        else
        {
            jinvoker.putGlobal( key, value );
        }
    }

    /**
     * Implementation of Mojo.execute. Invokes the underlying
     * Ruby script.
     * @throws MojoExecutionException satisfies Mojo contact, not used.
     * @throws MojoFailureException satisfies Mojo contact, not used.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        //jinvoker.setDebug( true );

        StringOutputStream stdout = new StringOutputStream();

        StringOutputStream stderr = new StringOutputStream();

        try
        {
            returned = jinvoker.invoke( stdout, stderr );

            // TODO: This is for future work. Returned object may be a Mojo,
            // to work like BeanShell
            if( returned instanceof Mojo )
            {
                ((Mojo)returned).execute();
            }
        }
        catch ( RaiseException e )
        {
            JRubyRuntimeInvoker.printREStackTrace( e, stderr );

            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( Throwable e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            try
            {
                stdout.flush();

                logOutput( stdout.toString(), false );

                stdout.close();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }

            try
            {
                stderr.flush();

                logOutput( stderr.toString(), true );

                stderr.close();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    public Object getReturned()
    {
    	return returned;
    }

    /**
     * Outputs Strings as info or error to the mojo's log.
     * 
     * @param out 
     * @param error true if error
     */
    private void logOutput(  String output, boolean error )
    {
        if ( output != null && output.length() > 0 )
        {
            for ( StringTokenizer tokens = new StringTokenizer( output, "\n" ); tokens.hasMoreTokens(); )
            {
                if ( error )
                {
                    getLog().error( tokens.nextToken() );
                }
                else
                {
                    getLog().info( tokens.nextToken() );
                }
            }
        }
    }

    public void addComponentRequirement( ComponentRequirement componentrequirement, Object obj )
        throws ComponentConfigurationException
    {
        set( componentrequirement.getFieldName(), obj );
    }

    public void setComponentConfiguration( Map map )
        throws ComponentConfigurationException 
    {
        for( Iterator iter = map.entrySet().iterator(); iter.hasNext(); )
        {
            Map.Entry entry = (Map.Entry)iter.next();
            set( (String)entry.getKey(), entry.getValue() );
        }
    }
}
