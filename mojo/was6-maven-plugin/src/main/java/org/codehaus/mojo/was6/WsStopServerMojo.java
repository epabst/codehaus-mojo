package org.codehaus.mojo.was6;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * This goal enables you to stop a standalone server instance. This is not used to stop a server controlled by
 * DeploymentManager. Therefore, this task is useful for the Base Application Server, and to stop the Node Agent and/or
 * DeploymentManager. If you wish to stop a server managed by the Deployment Manager, use the wsadmin task to execute a
 * scripting command.
 * 
 * @goal wsStopServer
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsStopServerMojo
    extends AbstractServerMojo
{

    /**
     * Specifies the type of connection to be used. Valid values are: SOAP or RMI
     * 
     * @parameter expression="${was6.conntype}" default-value="SOAP"
     */
    private String conntype;

    /**
     * Admin port of the server you wish to stop.
     * 
     * @parameter expression="${was6.port}"
     */
    private Integer port;

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsStopServer";
    }

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        super.configureBuildScript( document );
        configureTaskAttribute( document, "conntype", conntype );
        configureTaskAttribute( document, "port", port );
    }

}
