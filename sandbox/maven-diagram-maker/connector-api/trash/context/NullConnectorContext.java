package org.apache.maven.diagrams.connector_api.context;

import org.apache.maven.project.MavenProject;

/**
 * The very simple implementation of ConnectorContext (for testing purposes and for connector's that don't depend on
 * maven project)
 * 
 * The returned object by getMavenProject method is always null.
 * 
 * 
 * @author Piotr Tabor (ptab@newitech.com)
 */
public class NullConnectorContext extends AbstractConnectorContext
{
    public MavenProject getMavenProject()
    {
        return null;
    }

}
