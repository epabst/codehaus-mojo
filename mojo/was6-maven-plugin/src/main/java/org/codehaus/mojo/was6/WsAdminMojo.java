package org.codehaus.mojo.was6;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * The wsadmin goal executes the WebSphere command-line administration tool with the specified arguments.
 * 
 * @goal wsAdmin
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsAdminMojo
    extends AbstractWas6Mojo
{
    /**
     * The language to be used to interpret scripts. Valid values are: "jacl", "javascript" or "jython".
     * 
     * @parameter expression="${was6.language}"
     */
    private String language;

    /**
     * A single command to be passed to the script processor. Either specify this or {@link #script}
     * 
     * @parameter expression="${was6.command}"
     */
    private String command;

    /**
     * A java properties file containing attributes to set in the JVM System properties
     * 
     * @parameter expression="${was6.properties}"
     */
    private File properties;

    /**
     * A script file to be executed before the main command or file
     * 
     * @parameter expression="${was6.profile}"
     */
    private File profile;

    /**
     * Name of the server profile to use
     * 
     * @parameter expression="${was6.profileName}"
     */
    private String profileName;

    /**
     * A file containing a set of commands in a file to be passed to the script processor. Either specify this parameter
     * or {@link #command}
     * 
     * @parameter expression="${was6.script}"
     */
    private File script;

    /**
     * The Default type is SOAP. Valid values are SOAP, RMI, and NONE. NONE means that no server connection is made.
     * 
     * @parameter expression="${was6.conntype}" default-value="SOAP"
     */
    private String conntype;

    /**
     * The host attribute is optional and only specified if the conntype is specified. It contains the hostname of the
     * machine to connect to
     * 
     * @parameter expression="${was6.host}" default-value="localhost"
     */
    private String host;

    /**
     * The port on the host to connect to.
     * 
     * @parameter expression="${was6.port}"
     */
    private Integer port;

    /**
     * Contains the user ID to authenticate with.
     * 
     * @parameter expression="${was6.user}"
     */
    private String user;

    /**
     * Contains the password to authenticate with.
     * 
     * @parameter expression="${was6.password}"
     */
    private String password;

    /**
     * sets maximum size of the memory for the underlying VM.
     * 
     * @parameter expression="${was6.jvmMaxMemory}" default-value="256M"
     */
    private String jvmMaxMemory;

    /**
     * Arguments passed to the script.
     * 
     * @parameter expression="${was6.args}"
     */
    private String[] args;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        super.configureTaskAttribute( document, "profileName", profileName );
        super.configureTaskAttribute( document, "profile", profile );
        super.configureTaskAttribute( document, "lang", language );
        super.configureTaskAttribute( document, "properties", properties );
        super.configureTaskAttribute( document, "user", user );
        super.configureTaskAttribute( document, "password", password );
        super.configureTaskAttribute( document, "host", host );
        super.configureTaskAttribute( document, "port", port );
        super.configureTaskAttribute( document, "conntype", conntype );
        super.configureTaskAttribute( document, "jvmMaxMemory", jvmMaxMemory );
        super.configureTaskAttribute( document, "command", command );
        super.configureTaskAttribute( document, "script", script );

        if ( args != null )
        {
            Element wsAdminElement = (Element) document.selectSingleNode( "//target[@name='wsAdmin']/wsAdmin" );
            for ( int i = 0; i < args.length; i++ )
            {
                String value = args[i];
                wsAdminElement.addElement( "arg" ).addAttribute( "value", value );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsAdmin";
    }

}
