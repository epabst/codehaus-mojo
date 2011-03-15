package org.codehaus.mojo.was6;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * Abstract base class for controlling servers.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public abstract class AbstractServerMojo
    extends AbstractWas6Mojo
{
    /**
     * Name of the server to start.
     * 
     * @parameter expression="${was6.server}" default-value="server1"
     * @required
     */
    private String server;

    /**
     * The profile name of the desired server.
     * 
     * @parameter expression="${was6.profileName}"
     */
    private String profileName;

    /**
     * If true, the task with return immediately without waiting for the server to start.
     * 
     * @parameter expression="${was6.noWait}" default-value="false"
     */
    private boolean noWait;

    /**
     * If true, the task will not print any status information.
     * 
     * @parameter expression="${was6.quiet}" default-value="false"
     */
    private boolean quiet;

    /**
     * Specifies the name of the file to log the server start information to. If none is specified maven default will be
     * used.
     * 
     * @parameter expression="${was6.logFile}"
     */
    private String logFile;

    /**
     * If true, the task with erase an existing log file, instead of appending
     * 
     * @parameter expression="${was6.replaceLog}" default-value="false"
     */
    private boolean replaceLog;

    /**
     * If true, the task with print trace information.
     * 
     * @parameter expression="${was6.trace}" default-value="false"
     */
    private boolean trace;

    /**
     * Optional. Wait the specified the amount of time in seconds to wait for the server to successfully start.
     * 
     * @parameter expression="${was6.timeout}"
     */
    private Integer timeout;

    /**
     * Optional. Specifies the TCP Port the server should send status messages to.
     * 
     * @parameter expression="${was6.statusPort}"
     */
    private Integer statusPort;

    /**
     * Optional. Specifies the id of the admin user to authenticate with for administrative access.
     * 
     * @parameter expression="${was6.username}"
     */
    private String username;

    /**
     * Optional. Specifies the password of the admin user to authenticate with for administrative access.
     * 
     * @parameter expression="${was6.password}"
     */
    private String password;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        configureTaskAttribute( document, "server", server );
        configureTaskAttribute( document, "noWait", Boolean.toString( noWait ) );
        configureTaskAttribute( document, "quiet", Boolean.toString( quiet ) );
        configureTaskAttribute( document, "replaceLog", Boolean.toString( replaceLog ) );
        configureTaskAttribute( document, "trace", Boolean.toString( trace ) );
        configureTaskAttribute( document, "profileName", profileName );
        configureTaskAttribute( document, "username", username );
        configureTaskAttribute( document, "password", password );
        configureTaskAttribute( document, "timeout", timeout );
        configureTaskAttribute( document, "statusPort", statusPort );
        configureTaskAttribute( document, "logFile", logFile );
    }

}
