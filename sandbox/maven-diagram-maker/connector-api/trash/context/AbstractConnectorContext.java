package org.apache.maven.diagrams.connector_api.context;

import org.apache.maven.diagrams.connector_api.logger.Logger;

/**
 * AbstractConnectorContext is the base implementation for most classes implementing "ConnectorContext".
 * 
 * The implementation provides simple logger getter and setter.
 * 
 * @author Piotr Tabor (ptab@newitech.com)
 */
public abstract class AbstractConnectorContext implements ConnectorContext
{
    private Logger logger;

    public Logger getLogger()
    {
        return logger;
    }

    public void setLogger( Logger a_logger )
    {
        logger = a_logger;
    }

}
