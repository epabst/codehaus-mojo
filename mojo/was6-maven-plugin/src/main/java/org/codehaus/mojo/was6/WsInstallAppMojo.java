package org.codehaus.mojo.was6;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.dom4j.Document;

/**
 * Installs an EAR into WebSphere Application Server.
 * 
 * @goal installApp
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class WsInstallAppMojo
    extends AbstractAppMojo
{
    /**
     * Flag for updating existing application or installing a brand new.
     * 
     * @parameter expression="${was6.updateExisting}" default-value="true"
     */
    private boolean updateExisting;

    /**
     * Name of target cluster to deploy to.
     * 
     * @parameter expression="${was6.targetCluster}"
     */
    private String targetCluster;
    
    /**
     * The target cell for deployment.
     * @parameter expression="${was6.targetCell}"
     */
    private String targetCell;
    
    /**
     * The target node for deployment.
     * @parameter expression="${was6.targetNode}"
     */
    private String targetNode;
    
    /**
     * The target server for deployment.
     * @parameter expression="${was6.targetServer}"
     */
    private String targetServer;

    /**
     * EAR archive to deploy.
     * 
     * @parameter expression="${was6.earFile}" default-value="${project.artifact.file}"
     */
    private File earFile;
    
    /**
     * The security roles mapping information to use.
     * 
     * @parameter
     * @since 1.1.1
     */
    private List roles;

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsInstallApp";
    }

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        super.configureBuildScript( document );

        if ( earFile == null ) {
            throw new MojoExecutionException( "Earfile not specified" );
        }
        if ( !earFile.canRead() )
        {
            throw new MojoExecutionException( "Bad archive: " + earFile.getAbsolutePath() );
        }
        configureTaskAttribute( document, "ear", earFile.getAbsolutePath() );

        StringBuffer options = new StringBuffer();

        options.append( "-appname " ).append( applicationName );

        if ( updateExisting )
        {
            options.append( " -update" );
        }

        if ( StringUtils.isNotEmpty( targetCluster ) )
        {
            options.append( " -cluster " ).append( targetCluster );
        }
        
        if ( StringUtils.isNotEmpty( targetCell ) )
        {
            options.append( " -cell " ).append( targetCell );
        }
        
        if ( StringUtils.isNotEmpty( targetNode ) )
        {
            options.append( " -node " ).append( targetNode );
        }
        
        if ( StringUtils.isNotEmpty( targetServer ) )
        {
            options.append( " -server " ).append( targetServer );
        }

        if ( CollectionUtils.isNotEmpty( roles ) )
        {
            options.append( " -MapRolesToUsers { " );

            for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
                Role role = (Role) iterator.next();
                options.append( role.getRoleMapping() );
            }
            
            options.append( " } " );
        }
        
        configureTaskAttribute( document, "options", options );
    }

}
