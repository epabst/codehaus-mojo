package org.codehaus.mojo.ruby.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.plugin.descriptor.DuplicateParameterException;
import org.apache.maven.plugin.descriptor.InvalidPluginDescriptorException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.tools.plugin.extractor.AbstractScriptedMojoDescriptorExtractor;
import org.apache.maven.tools.plugin.extractor.ExtractionException;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.component.jruby.JRubyRuntimeInvoker;
import org.codehaus.plexus.util.StringOutputStream;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyExtractor
    extends AbstractScriptedMojoDescriptorExtractor
{
    protected List extractMojoDescriptors( Map scriptFilesKeyedByBasedir, PluginDescriptor pluginDescriptor )
        throws ExtractionException, InvalidPluginDescriptorException
    {
        List descriptors = new ArrayList();

        for ( Iterator mapIterator = scriptFilesKeyedByBasedir.entrySet().iterator(); mapIterator.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) mapIterator.next();

            String basedir = (String) entry.getKey();
            Set metadataFiles = (Set) entry.getValue();

            for ( Iterator it = metadataFiles.iterator(); it.hasNext(); )
            {
                File scriptFile = (File) it.next();

                String relativePath = null;

                if ( basedir.endsWith( "/" ) )
                {
                    basedir = basedir.substring( 0, basedir.length() - 2 );
                }

                relativePath = scriptFile.getPath().substring( basedir.length() );

                relativePath = relativePath.replace( '\\', '/' );

                MojoDescriptor mojoDescriptor = createMojoDescriptor( basedir, relativePath, pluginDescriptor );
                descriptors.add( mojoDescriptor );
            }
        }

        return descriptors;
    }

    private MojoDescriptor createMojoDescriptor( String basedir, String resource, PluginDescriptor pluginDescriptor )
        throws InvalidPluginDescriptorException
    {
        MojoDescriptor mojoDescriptor = new MojoDescriptor();
        mojoDescriptor.setPluginDescriptor( pluginDescriptor );

        mojoDescriptor.setLanguage( "jruby-mojo" );
        mojoDescriptor.setComponentConfigurator( "jruby" );

        mojoDescriptor.setImplementation( resource );

        getLogger().info( "Ruby Mojo File: " + resource );

        try
        {
            InputStream extractor = new FileInputStream( new File( basedir, resource ).getAbsolutePath() );

            JRubyInvoker invoker = new JRubyRuntimeInvoker( new InputStreamReader( extractor ) );
            invoker.setRequires( new String[] { "inline_extractor.rb" } );

            StringOutputStream stdout = new StringOutputStream();
            StringOutputStream stderr = new StringOutputStream();
            Map map = (Map)invoker.invoke();
            logOutput( stdout.toString(), false );
            logOutput( stderr.toString(), true );

            Ruby runtime = Ruby.getDefaultInstance();

            String goal = getParameter( runtime, map, "goal" );
            if ( goal == null )
            {
            	throw new InvalidPluginDescriptorException( "Cannot create plugin descriptor. Goal is a required value for a Mojo" );
            }
            mojoDescriptor.setGoal( goal );
            mojoDescriptor.setPhase( getParameter( runtime, map, "phase" ) );
            mojoDescriptor.setDependencyResolutionRequired( getParameter( runtime, map, "requiresDependencyResolution" ) );
            mojoDescriptor.setDescription( getParameter( runtime, map, "description" ) );

        	IRubyObject executes = (IRubyObject)map.get( RubyString.newString( runtime, "execute" ) );
        	if ( executes != null && !executes.isNil()  )
            {
        	    Map executesMap = (RubyHash)executes;

                mojoDescriptor.setExecutePhase( getParameter( runtime, executesMap, "phase" ) );
                mojoDescriptor.setExecuteLifecycle( getParameter( runtime, executesMap, "lifecycle" ) );
                mojoDescriptor.setExecuteGoal( getParameter( runtime, executesMap, "goal" ) );
            }

        	IRubyObject fields = (IRubyObject)map.get( RubyString.newString( runtime, "fields" ) );
            if ( fields != null && !fields.isNil() )
            {
            	RubyArray fieldsList = (RubyArray) fields;
                for ( Iterator iter = fieldsList.iterator(); iter.hasNext(); )
                {
                	RubyHash field = (RubyHash)iter.next();
                    setParameter( runtime, field, mojoDescriptor );
                }
            }
        }
        catch ( Exception e )
        {
            getLogger().error( "", e );
        }

        return mojoDescriptor;
    }

    private String getParameter( Ruby runtime, Map params, String annotation )
    {
        Object param = params.get( RubyString.newString( runtime, annotation ) );
        if( param instanceof String )
        {
            return (String)param;
        }
        IRubyObject anno = (IRubyObject)param;
        if ( anno != null && !anno.isNil() )
        {
        	return anno.toString();
        }
        return null;
    }

    private void setParameter( Ruby runtime, Map field, MojoDescriptor mojoDescriptor )
        throws DuplicateParameterException
    {
    	String name = getParameter( runtime, field, "name" );
        if ( name == null )
        {
            throw new NullPointerException( "Expected a non-null value for a declared parameter name" );
        }

        Parameter param = new Parameter();
        param.setName( name );
        param.setRequired( "true".equals( getParameter( runtime, field,  "required" ) ) );
        param.setEditable( !"true".equals( getParameter( runtime, field, "readonly" ) ) );
        param.setDeprecated( getParameter( runtime, field, "deprecated" ) );
        param.setDescription( getParameter( runtime, field, "description" ) );
        param.setType( getParameter( runtime, field, "type" ) );
        param.setAlias( getParameter( runtime, field, "alias" ) );
        param.setDefaultValue( getParameter( runtime, field, "default" ) );
        param.setExpression( getParameter( runtime, field, "expression" ) );

        mojoDescriptor.addParameter( param );
    }

    protected String getScriptFileExtension()
    {
        return "rb";
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
                    getLogger().error( tokens.nextToken() );
                }
                else
                {
                    getLogger().info( tokens.nextToken() );
                }
            }
        }
    }
}
