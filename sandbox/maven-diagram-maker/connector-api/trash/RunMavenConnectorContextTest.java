package org.apache.maven.diagrams.connector_api.context;

import java.io.File;

import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.project.MavenProject;

import junit.framework.TestCase;

public class RunMavenConnectorContextTest extends TestCase
{

    public void testGetMavenProject() throws ConnectorException
    {
        RunMavenConnectorContext context=new RunMavenConnectorContext();
        //context.setBaseDir( "../graph-api" );
        context.setBaseDir( new File("/home/ptab/newitech/x/pr/NITcommons/NITweblib") );
        //context.setMavenHomeDir( new File("/home/ptab/mvn21") );
        MavenProject mp=context.getMavenProject();
        assertNotNull(  mp);
    }

}
