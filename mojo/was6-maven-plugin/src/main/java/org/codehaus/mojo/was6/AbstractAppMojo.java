package org.codehaus.mojo.was6;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.dom4j.Document;

/**
 * Abstract base class for working with application tasks.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public abstract class AbstractAppMojo
    extends AbstractWas6Mojo
{
    /**
     * The name of the application that you wish to administer.
     * 
     * @parameter expression="${was6.applicationName}" default-value="${project.build.finalName}"
     * @required
     */
    protected String applicationName;

    /**
     * The host to connect to. This will also be used as the keys to lookup userId/password from settings.xml, if not
     * specified in the username and/or password parameter in the plugin.
     * 
     * @parameter expression="${was6.host}" default-value="localhost"
     * @required
     */
    private String host;

    /**
     * The port on the host to connect to.
     * 
     * @parameter expression="${was6.port}"
     */
    private Integer port;

    /**
     * The profile name of the desired server.
     * 
     * @parameter expression="${was6.profileName}"
     */
    private String profileName;

    /**
     * user ID to authenticate with. This takes precedence over values defined in settings.xml.
     * 
     * @parameter expression="${was6.username}"
     */
    private String username;

    /**
     * Password to authenticate with. This takes precedence over values defined in settings.xml.
     * 
     * @parameter expression="${was6.password}"
     */
    private String password;

    /**
     * Specifies the type of connection to be used. Valid values are: SOAP, RMI or NONE.
     * 
     * @parameter expression="${was6.conntype}" default-value="NONE"
     * @required
     */
    private String conntype;

    /**
     * Optional script file to be executed before the main command or file.
     * 
     * @parameter expression="${was6.profile}"
     */
    private String profile;

    /**
     * Optional java properties file containing attributes to set in the JVM System properties
     * 
     * @parameter expression="${was6.properties}"
     */
    private File properties;

    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        Validate.isTrue( StringUtils.equalsIgnoreCase( conntype, "SOAP" ) ||
            StringUtils.equalsIgnoreCase( conntype, "RMI" ) || StringUtils.equalsIgnoreCase( conntype, "NONE" ),
                         "Invalid value for conntype: " + conntype );

        configureTaskAttribute( document, "conntype", conntype );
        configureTaskAttribute( document, "host", host );
        configureTaskAttribute( document, "port", port );
        configureTaskAttribute( document, "profileName", profileName );
        configureTaskAttribute( document, "profile", profile );
        configureTaskAttribute( document, "properties", properties != null ? properties.getAbsolutePath() : null );

        Server server = getSettings().getServer( host );

        String usernameToUse = username != null ? username : server != null ? server.getUsername() : "";
        configureTaskAttribute( document, "user", usernameToUse );

        String passwordToUse = password != null ? password : server != null ? server.getPassword() : "";
        configureTaskAttribute( document, "password", passwordToUse );
    }

}
