package org.codehaus.mojo.was6;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * Enables you to uninstall an existing application from a WebSphere Server or Cell. This goal builds upon the wsadmin
 * task and shares many of the same attributes. This task is a wrapper for the AdminApp.uninstall() command of the
 * wsadmin tool.
 * 
 * @goal wsUninstallApp
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsUninstallAppMojo
    extends AbstractAppMojo
{

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsUninstallApp";
    }

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        super.configureBuildScript( document );
        configureTaskAttribute( document, "application", applicationName );
    }

}
