package org.codehaus.mojo.was6;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * The wsStartApplication goal enables you to start an existing or newly installed application on a WebSphere Server or
 * in a WebSphere Cell.
 * 
 * @goal wsStartApp
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsStartAppMojo
    extends AbstractAppMojo
{
    /**
     * Optional parameter specifying the name of the node containing the application you wish to stop.
     * 
     * @parameter expression="${was6.node}" default-value="";
     */
    private String node;

    /**
     * Optional parameter specifying the name of the server containing the application you wish to start.
     * 
     * @parameter expression="${was6.server}" default-value="server1"
     */
    private String server;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        super.configureBuildScript( document );
        super.configureTaskAttribute( document, "application", applicationName );
        super.configureTaskAttribute( document, "server", server );
        super.configureTaskAttribute( document, "node", node );
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsStartApp";
    }

}
