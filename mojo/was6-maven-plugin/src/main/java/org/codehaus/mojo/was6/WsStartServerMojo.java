package org.codehaus.mojo.was6;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * Starts a standalone server instance. This is not used to start a server controlled by DeploymentManager. Therefore,
 * this goal is useful for the Base Application Server, and to start the Node Agent and/or DeploymentManager. If you
 * wish to start a server managed by the Deployment Manager, use the wsadmin task to execute a scripting command.
 * 
 * @goal wsStartServer
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsStartServerMojo
    extends AbstractServerMojo
{

    /**
     * Specifies the name of a optional script file to execute during server startup.
     * 
     * @parameter expression="${was6.script}"
     */
    private String script;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        super.configureBuildScript( document );
        configureTaskAttribute( document, "script", script );
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsStartServer";
    }

}
