package org.apache.maven.diagrams.connector_api;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor;
import org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptorBuilder;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * It is base (abstract) implementation of the connector. It automatically creates ConnectorDescriptor from
 * "/META-INF/connector.xml" file and also keeps connectorContext (getter and setter).
 * 
 * @author Piotr Tabor
 * 
 */
public abstract class AbstractDiagramConnector extends AbstractLogEnabled implements DiagramConnector
{
    private ConnectorDescriptor descriptor = null;

    private final String CONNECTOR_DESCRIPTOR_FILE = "/META-INF/connector.xml";

    /**
     * Default implementation - reads and parses CONNECTOR_DESCRIPTOR_FILE file
     * 
     * @throws ConnectorException
     */
    public ConnectorDescriptor getConnectorDescriptor() throws ConnectorException
    {
        if ( descriptor == null )
        {
            ConnectorDescriptorBuilder connector = new ConnectorDescriptorBuilder();
            try
            {
                InputStream is = ConnectorDescriptor.class.getResourceAsStream( CONNECTOR_DESCRIPTOR_FILE );
                if ( is == null )
                    throw new ConnectorException( "Cannot find file: " + CONNECTOR_DESCRIPTOR_FILE );
                descriptor = connector.build( new InputStreamReader( is ) );
            }
            catch ( PlexusConfigurationException e )
            {
                throw new ConnectorException( "Cannot read connector descriptor from file :"
                                + CONNECTOR_DESCRIPTOR_FILE, e );
            }
        }
        return descriptor;
    }

    // public void setConnectorContext( ConnectorContext context )
    // {
    // connectorContext = context;
    // }
    //
    // public ConnectorContext getConnectorContext()
    // {
    // return connectorContext;
    // }
    //
    // public Logger getLogger()
    // {
    //
    // return ( connectorContext != null ) ? connectorContext.getLogger() : null;
    // }

}
