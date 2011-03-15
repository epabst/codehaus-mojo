package org.codehaus.mojo.was6;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * This goal enables you to generate default IBM WebSphere Bindings for the specified EAR file. The goal provides
 * options to control how the bindings are generated and mimics the options provided by the WebSphere Application
 * Install wizards. The goal binds to the package phase by default.
 * 
 * @goal wsDefaultBindings
 * @phase package
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsDefaultBindingsMojo
    extends AbstractWas6Mojo
{

    /**
     * Specifies an optional default data source JNDI name to be used for all EJB 1.x CMPs.
     * 
     * @parameter expression="${was6.defaultDataSource}"
     */
    private String defaultDataSource;

    /**
     * Specifies the user associated with the default data source.
     * 
     * @parameter expression="${was6.dbUser}"
     */
    private String dbUser;

    /**
     * Specifies the password associated with the default data source.
     * 
     * @parameter expression="${was6.dbPassword}"
     */
    private String dbPassword;

    /**
     * Specifies the default connection factory to be used for all EJB 2.x CMPs.
     * 
     * @parameter expression="${was6.defaultConnectionFactory}"
     */
    private String defaultConnectionFactory;

    /**
     * Specifies the resource authorization on the connection factory for EJB 2.x CMPs. <br />
     * Legal values are: PerConnFact or Container
     * 
     * @parameter expression="${was6.resAuth}"
     */
    private String resAuth;

    /**
     * Specifies a prefix that is prepended to any generated EJB JNDI names. The default is "ejb".
     * 
     * @parameter expression="${was6.ejbJndiPrefix}"
     */
    private String ejbJndiPrefix;

    /**
     * Specifies the virtual host for all wars in the application.
     * 
     * @parameter expression="${was6.virtualHost}"
     */
    private String virtualHost;

    /**
     * When false, any pre-existing bindings will not be altered. When true, new bindings are completely generated.
     * 
     * @parameter expression="${was6.forceBindings}" default-value="false"
     */
    private boolean forceBindings;

    /**
     * When specified, this attribute points to a custom strategy file that further affects the bindings. See the
     * properties/dfltbndngs.dtd of your WebSphere installation for more details.
     * 
     * @parameter expression="${was6.strategy}"
     */
    private File strategy;

    /**
     * EAR archive to generate bindings for.
     * 
     * @parameter expression="${was6.earFile}" default-value="${project.artifact.file}"
     */
    private File earFile;

    /**
     * When specified, it points to a file that will be generated containing the bindings information. This file is in
     * the custom strategy format.
     * 
     * @parameter expression="${was6.exportFile}"
     */
    private File exportFile;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        if ( !getMavenProject().getPackaging().equalsIgnoreCase( "ear" ) ) {
            throw new MojoExecutionException( "This goals requires ear packaging" );
        }
        
        if ( !( earFile.canRead() && earFile.canWrite() ) )
        {
            throw new MojoExecutionException( "Invalid archive: " + earFile.getAbsolutePath() );
        }
        
        configureTaskAttribute( document, "ear", earFile );
        configureTaskAttribute( document, "outputFile", earFile );
        configureTaskAttribute( document, "defaultDataSource", defaultDataSource );
        configureTaskAttribute( document, "dbUser", dbUser );
        configureTaskAttribute( document, "dbPassword", dbPassword );
        configureTaskAttribute( document, "defaultConnectionFactory", defaultConnectionFactory );
        configureTaskAttribute( document, "resAuth", resAuth );
        configureTaskAttribute( document, "ejbJndiPrefix", ejbJndiPrefix );
        configureTaskAttribute( document, "virtualHost", virtualHost );
        configureTaskAttribute( document, "forceBindings", Boolean.valueOf( forceBindings ) );
        if ( strategy != null && !( strategy.exists() && strategy.isFile() ) )
        {
            throw new MojoExecutionException( strategy.getAbsolutePath() + " does not exist or is not a file" );
        }
        else
        {
            configureTaskAttribute( document, "strategy", strategy );
        }
        configureTaskAttribute( document, "exportFile", exportFile );
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsDefaultBindings";
    }

}
