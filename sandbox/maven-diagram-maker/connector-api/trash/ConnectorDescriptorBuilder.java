package org.apache.maven.diagrams.connector_api.descriptor;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * The class is responsible for serializing and deserializing the connectorDescriptor from the Reader to the
 * {@link ConnectorConfiguration} class.
 * 
 * @author Piotr Tabor
 */
public class ConnectorDescriptorBuilder
{

    /**
     * Creates the ConnectorDescriptor using the given reader.
     * 
     * @param reader -
     *            the source of data (xml input stream) for creating the connectorDescriptor
     * @return
     * @throws PlexusConfigurationException
     */
    @SuppressWarnings( "unchecked" )
    public ConnectorDescriptor build( Reader reader ) throws PlexusConfigurationException
    {

        /* Transform the reader into plexus configuration */
        PlexusConfiguration c = buildConfiguration( reader );

        ConnectorDescriptor connectorDescriptor = new DefaultConnectorDescriptor();
     
        connectorDescriptor.setGroupId( c.getChild( "groupId" ).getValue() );
        connectorDescriptor.setArtifactId( c.getChild( "artifactId" ).getValue() );
        connectorDescriptor.setVersion( c.getChild( "version" ).getValue() );
        connectorDescriptor.setName( c.getChild( "name" ).getValue() );
        connectorDescriptor.setDescription( c.getChild( "description" ).getValue() );
        // connectorDescriptor.setParameters( getParametersList( c ) );
        connectorDescriptor.setMappings( getMappingsList( c ) );

        String className = c.getChild( "connectorConfigurationClassName" ).getValue();
        if ( className == null )
            throw new PlexusConfigurationException( "Missing configuration option: connectorConfigurationClassName" );
        try
        {
            connectorDescriptor.setConfigurationClass( (Class<? extends ConnectorConfiguration>) Class.forName( className ) );
        }
        catch ( ClassNotFoundException e )
        {
            throw new PlexusConfigurationException( "No such class: " + className, e );
        }

        List<ConnectorInterfaceEnum> interfaces = getInterfacesList( c );
        connectorDescriptor.setProvidedInterfaces( EnumSet.copyOf( interfaces ) );
        connectorDescriptor.setPreferredInterface( interfaces.size() > 0 ? interfaces.get( 0 ) : null );

        return connectorDescriptor;
    }

    /**
     * Reads the mappings from the Plexus configuration
     * 
     * @param c
     * @return
     * @throws PlexusConfigurationException
     */
    private List<Mapping> getMappingsList( PlexusConfiguration c ) throws PlexusConfigurationException
    {
        PlexusConfiguration[] parameterConfigurations = c.getChild( "mappings" ).getChildren( "mapping" );

        List<Mapping> mappings = new ArrayList<Mapping>();

        for ( int i = 0; i < parameterConfigurations.length; i++ )
        {
            PlexusConfiguration d = parameterConfigurations[i];

            Mapping m = new Mapping();
            m.setTagName( d.getChild( "name" ).getValue() );

            String className = d.getChild( "className" ).getValue();
            if ( className == null )
                throw new PlexusConfigurationException( "Missing configuration option: className" );
            try
            {
                m.setClazz( Class.forName( className ) );
            }
            catch ( ClassNotFoundException e )
            {
                throw new PlexusConfigurationException( "No such class: " + className, e );
            }

            String converterClassName = d.getChild( "converterClassName" ).getValue();
            if ( converterClassName != null )
            {
                try
                {
                    m.setConverter( Class.forName( converterClassName ) );
                }
                catch ( ClassNotFoundException e )
                {
                    throw new PlexusConfigurationException( "No such class: " + converterClassName, e );
                }
            }

            mappings.add( m );
        }

        return mappings;
    }

    // Copied
    //
    // protected List<Parameter> getParametersList( PlexusConfiguration c ) throws PlexusConfigurationException
    // {
    //
    // PlexusConfiguration[] parameterConfigurations = c.getChild( "parameters" ).getChildren( "parameter" );
    //
    // List<Parameter> parameters = new ArrayList<Parameter>();
    //
    // for ( int i = 0; i < parameterConfigurations.length; i++ )
    // {
    // PlexusConfiguration d = parameterConfigurations[i];
    //
    // Parameter parameter = new Parameter();
    //
    // parameter.setName( d.getChild( "name" ).getValue() );
    //
    // parameter.setAlias( d.getChild( "alias" ).getValue() );
    //
    // parameter.setType( d.getChild( "type" ).getValue() );
    //
    // String required = d.getChild( "required" ).getValue();
    //
    // parameter.setRequired( Boolean.valueOf( required ).booleanValue() );
    //
    // PlexusConfiguration editableConfig = d.getChild( "editable" );
    //
    // if ( editableConfig != null )
    // {
    // String editable = d.getChild( "editable" ).getValue();
    //
    // parameter.setEditable( editable == null || Boolean.valueOf( editable ).booleanValue() );
    // }
    //
    // parameter.setDescription( d.getChild( "description" ).getValue() );
    //
    // parameter.setDeprecated( d.getChild( "deprecated" ).getValue() );
    //
    // parameter.setImplementation( d.getChild( "implementation" ).getValue() );
    //
    // parameters.add( parameter );
    // }
    //
    // return parameters;
    // }

    protected List<ConnectorInterfaceEnum> getInterfacesList( PlexusConfiguration c )
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] parameterConfigurations = c.getChild( "interfaces" ).getChildren( "interface" );
        List<ConnectorInterfaceEnum> interfaces = new ArrayList<ConnectorInterfaceEnum>();

        for ( int i = 0; i < parameterConfigurations.length; i++ )
        {
            PlexusConfiguration d = parameterConfigurations[i];
            try
            {
                interfaces.add( ConnectorInterfaceEnum.valueOf( d.getValue().toUpperCase() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new PlexusConfigurationException( "No such constant:" + d.getValue().toUpperCase(), e );
            }
        }

        return interfaces;
    }

    /**
     * Transforms Reader's instance into {@link PlexusConfiguration}
     * 
     * @param configuration
     * @return
     * @throws PlexusConfigurationException
     */
    public PlexusConfiguration buildConfiguration( Reader configuration ) throws PlexusConfigurationException
    {
        try
        {
            return new XmlPlexusConfiguration( Xpp3DomBuilder.build( configuration ) );
        }
        catch ( IOException e )
        {
            throw new PlexusConfigurationException( "Error creating configuration", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new PlexusConfigurationException( "Error creating configuration", e );
        }
    }
}
