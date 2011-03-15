package org.apache.maven.diagrams.connector_api.context;

import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.logger.Logger;
import org.apache.maven.project.MavenProject;

/**
 * The ConnectorContext interface contains environment in which every connector is run.
 * 
 * @author Piotr Tabor (ptab@newitech.com)
 */
public interface ConnectorContext
{
    /**
     * The method returns the maven's project which the plugin's run should use
     * 
     * @throws ConnectorException
     * @return MavenProject
     */
    public MavenProject getMavenProject() throws ConnectorException;

    /**
     * The method returns logger that will be used by the connector
     * 
     * @return the logger
     */
    public Logger getLogger();
}
