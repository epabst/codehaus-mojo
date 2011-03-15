package org.apache.maven.diagrams.connector_api.context;

import org.apache.maven.project.MavenProject;

/**
 * The GivenMavenConnectorContext class is implementation of ConnectorContext that just stores and returns MavenProject.
 * 
 * The class should be used by plugins to forward the plugin's mavenProject to the connector.
 * 
 * @author Piotr Tabor (ptab@newitech.com)
 */
public class GivenMavenConnectorContext extends AbstractConnectorContext
{
    private MavenProject mavenProject;

    public GivenMavenConnectorContext()
    {
        mavenProject = null;
    }

    public GivenMavenConnectorContext( MavenProject project )
    {
        mavenProject = project;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

}
