package org.codehaus.mojo.ruby.extractor;

import java.io.File;
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
import org.jruby.RubyIO;
import org.jruby.exceptions.RaiseException;

public class RDocExtractor
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

                if( mojoDescriptor != null )
                {
                    descriptors.add( mojoDescriptor );
                }
            }
        }

        return descriptors;
    }

    private MojoDescriptor createMojoDescriptor( String basedir, String resource, PluginDescriptor pluginDescriptor )
        throws ExtractionException, InvalidPluginDescriptorException
    {
        MojoDescriptor mojoDescriptor = new MojoDescriptor();
        mojoDescriptor.setPluginDescriptor( pluginDescriptor );

        mojoDescriptor.setLanguage( "jruby-mojo" );
        mojoDescriptor.setComponentConfigurator( "jruby" );

        mojoDescriptor.setImplementation( resource );

        InputStream extractor = Thread.currentThread().getContextClassLoader().getResourceAsStream( "rdoc_extractor.rb" );

        StringOutputStream stdout = new StringOutputStream();

        StringOutputStream stderr = new StringOutputStream();

        try
        {
            JRubyInvoker invoker = new JRubyRuntimeInvoker( new InputStreamReader( extractor ) );

            invoker.setRequires( new String[] { "mojo_require.rb" } );

            invoker.putGlobal( "file_name", new File( basedir, resource ).getAbsolutePath() );

            Object invoked = invoker.invoke( stdout, stderr );

            if( invoked instanceof RubyIO )
            {
                if( stderr.toString().startsWith( "No goal" ) )
                {
                    return null;
                }

                logOutput( stdout.toString(), false );

                logOutput( stderr.toString(), true );

                throw new ExtractionException( stdout.toString() );
            }

            getLogger().info( "Ruby Mojo File: " + resource );

            logOutput( stdout.toString(), false );

            logOutput( stderr.toString(), true );

            Map map = (Map)invoked;

            mojoDescriptor.setGoal( (String) map.get( "goal" ) );

            mojoDescriptor.setPhase( (String) map.get( "phase" ) );

            mojoDescriptor.setDependencyResolutionRequired( (String) map.get( "requiresDependencyResolution" ) );

            mojoDescriptor.setDescription( (String) map.get( "description" ) );

            mojoDescriptor.setAggregator( "true".equals( map.get( "aggregator" ) ) );

            String configurator = (String) map.get( "configurator" );

            if ( configurator != null )
            {
                mojoDescriptor.setComponentConfigurator( configurator );
            }

            Object executes = map.get( "execute" );

            if ( executes instanceof Map )
            {
                Map executesMap = (Map) executes;

                mojoDescriptor.setExecutePhase( (String) executesMap.get( "phase" ) );

                mojoDescriptor.setExecuteLifecycle( (String) executesMap.get( "lifecycle" ) );

                mojoDescriptor.setExecuteGoal( (String) executesMap.get( "goal" ) );
            }

            Object fields = map.get( "fields" );

            if ( fields != null )
            {
                if ( fields instanceof List )
                {
                    List fieldsList = (List) fields;

                    for ( Iterator iter = fieldsList.iterator(); iter.hasNext(); )
                    {
                        Map field = (Map) iter.next();

                        setParameter( field, mojoDescriptor );
                    }
                }
                else if ( fields instanceof Map )
                {
                    setParameter( (Map) ( (Map) fields ).get( "field" ), mojoDescriptor );
                }
                else
                {
                    throw new IllegalArgumentException( "Internal exception. 'fields' is assumed to be a Map or a List." );
                }
            }

            setBasedirParameter( mojoDescriptor );
        }
        catch( RaiseException e )
        {
            // TODO: should send to log
            JRubyRuntimeInvoker.printREStackTrace( e, System.err );
        }
        catch ( Exception e )
        {
            getLogger().error( "", e );
        }

        return mojoDescriptor;
    }


    private void setParameter( Map field, MojoDescriptor mojoDescriptor )
        throws DuplicateParameterException
    {
        if ( field == null || !field.containsKey( "parameter" ) )
        {
            return;
        }

        String name = (String) field.get( "name" );

        if ( name == null )
        {
            throw new NullPointerException( "Expected a non-null value for a declared parameter name" );
        }

        if ( "execute".equals( name ) )
        {
            // We don't need to add this
            return;
        }

        Parameter param = new Parameter();

        param.setName( name );

        param.setRequired( "true".equals( field.get( "required" ) ) );

        param.setEditable( !"true".equals( field.get( "readonly" ) ) );

        param.setDeprecated( (String) field.get( "deprecated" ) );

        param.setDescription( (String) field.get( "description" ) );

        Object parameter = field.get( "parameter" );

        if ( parameter instanceof Map )
        {
            Map parameterMap = (Map) parameter;

            param.setAlias( (String) parameterMap.get( "alias" ) );

            param.setDefaultValue( (String) parameterMap.get( "default-value" ) );

            String expression = (String) parameterMap.get( "expression" );

            param.setExpression( expression );

            String type = (String)parameterMap.get( "type" );

            if( type != null && type.length() > 0 )
            {
                param.setType( type );

                // XXX: This is a horrible hack to deal with ComponentConfigurator's lack of full plugin.xml access
                if( expression == null && !"java.lang.String".equals( type ) && !"java.util.Map".equals( type ) )
                {
                    param.setExpression( "${" + name + "}" );
                }
            }
            else
            {
                param.setType( "java.lang.String" );
            }
        }

        mojoDescriptor.addParameter( param );
    }

    private void setBasedirParameter( MojoDescriptor mojoDescriptor )
        throws DuplicateParameterException
    {
        Map parameters = mojoDescriptor.getParameterMap();

        if ( parameters == null || parameters.containsKey( "basedir" ) )
        {
            return;
        }

        Parameter param = new Parameter();

        param.setName( "basedir" );

        param.setRequired( true );

        param.setEditable( false );

        param.setExpression( "${basedir}" );

        param.setType( File.class.getName() );

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
