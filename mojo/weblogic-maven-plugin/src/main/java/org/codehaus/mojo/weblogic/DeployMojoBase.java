package org.codehaus.mojo.weblogic;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.weblogic.util.WeblogicMojoUtilities;
import weblogic.Deployer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is a base class for all deployment related mojos.
 *
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @version $Id$
 * @description Deploy an artifact (war, ear, etc) to a target(s) which can be servers or clusters.
 */
public abstract class DeployMojoBase
    extends AbstractWeblogicMojo
{

    /**
     * The dns hostname of the Weblogic Admin server.
     *
     * @parameter expression="${weblogic.adminServer.hostName}" default-value="localhost"
     */
    private String adminServerHostName;

    /**
     * The protocol to use to access the Weblogic Admin server for deployment.
     *
     * @parameter expression="${weblogic.adminServer.protocol}" default-value="t3"
     */
    private String adminServerProtocol;

    /**
     * The admin port of the Weblogic Admin Server.
     *
     * @parameter expression="${weblogic.adminServer.port}" default-value="7001"
     */
    private String adminServerPort;

    /**
     * The Admin UserId to access the Weblogic Admin server for deployment.
     *
     * @parameter expression="${weblogic.user}" default-value="weblogic"
     */
    private String userId;

    /**
     * The admin password to access the Weblogic Admin server for deployment.
     *
     * @parameter expression="${weblogic.password}" default-value="weblogic"
     */
    private String password;

    /**
     * The full path to artifact to be deployed.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     */
    private String artifactPath;

    /**
     * The project packaging used to check the suffix on the artifact.
     *
     * @parameter expression="${project.packaging}"
     */
    private String projectPackaging;

    /**
     * The name to use when deploying the object.
     *
     * @parameter expression="${project.artifactId}"
     */
    private String name;

    /**
     * A comma seperated list of names of servers or clusters to deploy the artifact onto.
     *
     * @parameter expression="${weblogic.targetNames}" default-value="AdminServer"
     */
    private String targetNames;

    /**
     * True if you are running on a machine that is remote to the admin server. If this is a remote deployment and this
     * is set to false then it is assumed that all source paths are valid paths on the admin server.
     *
     * @parameter default-value="false"
     */
    private boolean remote;

    /**
     * True if you want to turn off staging
     *
     * @parameter default-value="true"
     */
    private boolean stage;

    /**
     * True to turn on debugging
     *
     * @parameter default-value="false"
     */
    private boolean debug;

    /**
     * True to turn on debugging
     *
     * @parameter default-value="true"
     */
    private boolean verbose;


    /**
     * Set to true to upload the code.
     *
     * @parameter default-value="false"
     */
    private boolean upload;

    /**
     * If set to true, the deployer will not stop if a failure is detected.
     *
     * @parameter default-value="false"
     */
    private boolean continueOnFailure;

    /**
     * Set this value to true so weblogic will return immediately
     *
     * @parameter default-value="false"
     */
    private boolean noWait;


    /**
     * Set this value to the timeout seconds. Set to <=0 to disable
     *
     * @parameter default-value="-1"
     */
    private int timeout;


    /**
     * Print the version for weblogic
     *
     * @parameter default-value="false"
     */
    private boolean version;

    /**
     * Tell weblogic to ignore the versions an apply the task to all versions.
     *
     * @parameter default-value="false"
     */
    private boolean noVersion;

    /**
     * tell weblogic to not exit if there is a deployment failure.
     *
     * @parameter default-value="false"
     */
    private boolean noExit;

    /**
     * Deploy the target as an exploded directory. The target should be the
     * target directory.
     *
     * @parameter expression="${weblogic.exploded}" default-value="false"
     */
    private boolean exploded;

    /**
     * Deploy the target with the referenced deployment plan
     *
     * @parameter expression="${weblogic.deploymentPlanPath}"
     */
    private String deploymentPlanPath;

    /**
     * This method will perform the deployment of the object to the proper server url.
     *
     * @throws MojoExecutionException Thrown if we fail to obtain a Weblogic deployment instance.
     */
    public void execute()
        throws MojoExecutionException
    {
        super.execute();

        if ( getLog().isWarnEnabled() )
        {
            getLog().warn( "Running the Deploy Mojo Base without an implementation" );
        }

        //throw new MojoExecutionException( "Unimplemented Mojo Base" );
    }

    /**
     * Returns the value for the admin server host name property.
     *
     * @return The value of the admin server host name property.
     */
    public String getAdminServerHostName()
    {
        return this.adminServerHostName;
    }

    /**
     * Sets the value of the admin server host name property.
     *
     * @param inAdminServerHostName The new value of the admin server host name property.
     */
    public void setAdminServerHostName( final String inAdminServerHostName )
    {
        this.adminServerHostName = inAdminServerHostName;
    }

    /**
     * Returns the value for the admin server port property.
     *
     * @return The value of the admin server port property.
     */
    public String getAdminServerPort()
    {
        return this.adminServerPort;
    }

    /**
     * Sets the value of the admin server port property.
     *
     * @param inAdminServerPort The new value of the admin server port property.
     */
    public void setAdminServerPort( final String inAdminServerPort )
    {
        this.adminServerPort = inAdminServerPort;
    }

    /**
     * Returns the value for the name property.
     *
     * @return The value of the name property.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param inName The new value of the name property.
     */
    public void setName( final String inName )
    {
        this.name = inName;
    }

    /**
     * Returns the value for the password property.
     *
     * @return The value of the password property.
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param inPassword The new value of the password property.
     */
    public void setPassword( final String inPassword )
    {
        this.password = inPassword;
    }

    /**
     * Returns the value for the target names property.
     *
     * @return The value of the target names property.
     */
    public String getTargetNames()
    {
        return this.targetNames;
    }

    /**
     * Sets the value of the target names property.
     *
     * @param inTargetNames The new value of the target names property.
     */
    public void setTargetNames( final String inTargetNames )
    {
        this.targetNames = inTargetNames;
    }

    /**
     * Returns the value for the user id property.
     *
     * @return The value of the user id property.
     */
    public String getUserId()
    {
        return this.userId;
    }

    /**
     * Sets the value of the user id property.
     *
     * @param inUserId The new value of the user id property.
     */
    public void setUserId( final String inUserId )
    {
        this.userId = inUserId;
    }

    /**
     * This method will return a list of target by parsing the comma separated list of targets..
     *
     * @return The value of target list.
     */
    public List getTargetList()
    {
        List targets = new ArrayList();
        StringTokenizer tokens = new StringTokenizer( this.getTargetNames(), "," );

        while ( tokens.hasMoreTokens() )
        {
            targets.add( tokens.nextToken() );
        }

        return targets;
    }

    /**
     * Getter for property remote.
     *
     * @return The value of remote.
     */
    public boolean isRemote()
    {
        return this.remote;
    }

    /**
     * Setter for the remote.
     *
     * @param inRemote The value of remote.
     */
    public void setRemote( final boolean inRemote )
    {
        this.remote = inRemote;
    }

    /**
     * Getter for property admin server protocol.
     *
     * @return The value of admin server protocol.
     */
    public String getAdminServerProtocol()
    {
        return this.adminServerProtocol;
    }

    /**
     * Setter for the admin server protocol.
     *
     * @param inAdminServerProtocol The value of admin server protocol.
     */
    public void setAdminServerProtocol( final String inAdminServerProtocol )
    {
        this.adminServerProtocol = inAdminServerProtocol;
    }

    /**
     * Setter for the admin server protocol.
     *
     * @param deploymentPlanPath The path to the deployment plan
     */
    public void setDeploymentPlanPath( String deploymentPlanPath )
    {
        this.deploymentPlanPath = deploymentPlanPath;
    }

    /**
     * This method will build up the parameters required for the various deployment operations.
     *
     * @param inOperation The deployment operation to take place.
     * @return The value of input parameters to be passed to the deployer.
     */
    protected String[] getInputParameters( String inOperation )
    {
        final List parameterList = new ArrayList();

        // Create the admin URL
        final String adminURL = WeblogicMojoUtilities.getAdminUrl( this.adminServerProtocol, this.adminServerHostName,
                                                                   this.adminServerPort );

        // Load the admin url
        parameterList.add( "-adminurl" );
        parameterList.add( adminURL );

        // add the admin user name
        parameterList.add( "-username" );
        parameterList.add( this.getUserId() );

        // add the admin password
        parameterList.add( "-password" );
        parameterList.add( this.getPassword() );

        if ( this.verbose )
        {
            parameterList.add( "-verbose" );
        }

        if ( this.debug )
        {
            parameterList.add( "-debug" );
        }

        if ( this.noWait )
        {
            parameterList.add( "-nowait" );
        }

        if ( this.timeout > 0 )
        {
            parameterList.add( "-timeout " + this.timeout );
        }

        if ( this.version )
        {
            parameterList.add( "-version" );
        }

        if ( this.noVersion )
        {
            parameterList.add( "-noVersion" );
        }

        if ( this.noExit )
        {
            parameterList.add( "-noexit" );
        }

        // Not valid for listapps operation
        if ( !inOperation.equalsIgnoreCase( "listapps" ) )
        {

            // add the artifact name
            parameterList.add( "-name" );
            parameterList.add( this.getName() );
        }

        // not use in listabpps
        if ( !inOperation.equalsIgnoreCase( "listapps" ) )
        {

            // add the target comma seperated list
            parameterList.add( "-targets" );
            parameterList.add( this.getTargetNames() );
        }

        // Only use these parameters for a deploy operation
        if ( inOperation.equalsIgnoreCase( "deploy" ) )
        {

            // add if remote
            if ( this.isRemote() )
            {
                parameterList.add( "-upload" );
            }

            parameterList.add( "-source" );
            parameterList.add( this.getArtifactPath() );

            if ( this.deploymentPlanPath != null && this.deploymentPlanPath.length() > 0 )
            {
                parameterList.add("-plan");
                parameterList.add( this.deploymentPlanPath );
            }

        }

        // Set the operation
        String operation = "-" + inOperation;
        parameterList.add( operation );

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic Deployment parameters " + parameterList );
        }

        return (String[]) parameterList.toArray( new String[parameterList.size()] );
    }

    /**
     * Executes the deployer with the given parameters. Returns success or failure.
     *
     * @param parameters   the parameters to execute
     * @param errorMessage the error message
     * @return success
     * @throws MojoExecutionException when the deployer fails and the continueOnFailure is false
     */
    protected boolean executeDeployer( String[] parameters, final String errorMessage )
        throws MojoExecutionException
    {
        try
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "continueOnFailure is " + continueOnFailure );
            }
            Deployer deployer = new Deployer( parameters );
            deployer.run();
        }
        catch ( Exception e )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( errorMessage + ": " + e.getMessage() );
            }
            if ( this.continueOnFailure )
            {
                getLog().info( "Continuing on failure." );
                getLog().error( e );
                return false;
            }
            else
            {
                throw new MojoExecutionException( errorMessage + ": " + e.getMessage(), e );
            }
        }
        finally
        {
            WeblogicMojoUtilities.unsetWeblogicProtocolHandler();
        }
        return true;
    }


    /**
     * Getter for property artifact path. If the {@link #exploded} attribute is set
     * the value will be {@link #artifactPath}.
     *
     * @return The value of artifact path.
     */
    public String getArtifactPath()
    {
        final String fixedArtifactPath;
        if ( this.exploded )
        {
            fixedArtifactPath = this.artifactPath;
        }
        else
        {
            fixedArtifactPath = WeblogicMojoUtilities.updateArtifactName( this.artifactPath, this.projectPackaging );
        }
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "fixedArtifactPath=" + fixedArtifactPath );
        }
        return fixedArtifactPath;
    }

    /**
     * Setter for the artifact path.
     *
     * @param inArtifactPath The value of artifact path.
     */
    public void setArtifactPath( final String inArtifactPath )
    {
        this.artifactPath = inArtifactPath;
    }

    /**
     * Getter for property project packaging.
     *
     * @return The value of project packaging.
     */
    public String getProjectPackaging()
    {
        return projectPackaging;
    }

    /**
     * Setter for the project packaging.
     *
     * @param inProjectPackaging The value of project packaging.
     */
    public void setProjectPackaging( final String inProjectPackaging )
    {
        this.projectPackaging = inProjectPackaging;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "DeployMojoBase[" );
        buffer.append( "adminServerHostName = " ).append( adminServerHostName );
        buffer.append( ", adminServerProtocol = " ).append( adminServerProtocol );
        buffer.append( ", adminServerPort = " ).append( adminServerPort );
        buffer.append( ", userId = " ).append( userId );
        buffer.append( ", password = ****" );
        buffer.append( ", artifactPath = " ).append( artifactPath );
        buffer.append( ", projectPackaging = " ).append( projectPackaging );
        buffer.append( ", name = " ).append( name );
        buffer.append( ", targetNames = " ).append( targetNames );
        buffer.append( ", remote = " ).append( remote );
        buffer.append( "]" );
        return buffer.toString();
    }

    /**
     * @return the stage
     */
    public boolean isStage()
    {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage( boolean stage )
    {
        this.stage = stage;
    }

    /**
     * @return the debug
     */
    public boolean isDebug()
    {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * @param verbose the verbose to set
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /**
     * @return the upload
     */
    public boolean isUpload()
    {
        return upload;
    }

    /**
     * @param upload the upload to set
     */
    public void setUpload( boolean upload )
    {
        this.upload = upload;
    }

    /**
     * Getter for continueOnFailure
     *
     * @return true if the deployer should continue
     */
    public boolean isContinueOnFailure()
    {
        return continueOnFailure;
    }

    /**
     * Setter for continue on failure. Set to true so the deployer
     * will not stop if there is a failure.
     *
     * @param continueOnFailure the value to set
     */
    public void setContinueOnFailure( boolean continueOnFailure )
    {
        this.continueOnFailure = continueOnFailure;
    }

    /**
     * Getter for noWait
     *
     * @return the value of noWait
     */
    public boolean isNoWait()
    {
        return noWait;
    }

    /**
     * Setter for noWait
     *
     * @param noWait if set to true, weblogic will not wait for deployment success
     */
    public void setNoWait( boolean noWait )
    {
        this.noWait = noWait;
    }

    /**
     * Getter for deployment timeout
     *
     * @return the timeout in seconds
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Setter for the deployment timeout
     *
     * @param timeout in seconds
     */
    public void setTimeout( int timeout )
    {
        this.timeout = timeout;
    }

    /**
     * Getter for version
     *
     * @return true if weblogic should print the version
     */
    public boolean isVersion()
    {
        return version;
    }

    /**
     * Setter for version
     *
     * @param version set to true to have weblogic print the version
     */
    public void setVersion( boolean version )
    {
        this.version = version;
    }

    /**
     * Getter for the -noversion flag
     *
     * @return true if the -noversion flag should be used.
     */
    public boolean isNoVersion()
    {
        return noVersion;
    }

    /**
     * Setter for noVersion.
     *
     * @param noVersion set to true if the -noversion flag should be used
     */
    public void setNoVersion( boolean noVersion )
    {
        this.noVersion = noVersion;
    }

    /**
     * getter for this.noExit
     *
     * @return true if weblogic should not use System.exit() when problems arise.
     */
    public boolean isNoExit()
    {
        return noExit;
    }

    /**
     * Setter for this.noExit
     *
     * @param noExit set to true if weblogic should NOT use System.exit() when problems arise.
     */
    public void setNoExit( boolean noExit )
    {
        this.noExit = noExit;
    }

    /**
     * Getter for {@link #exploded}.
     *
     * @return true if the deployer should use the target artifact path as the source parameter
     */
    public boolean isExploded()
    {
        return exploded;
    }

    /**
     * Setter for {@link #exploded}.
     *
     * @param exploded true to use the target artifact path as the -source parameter
     */
    public void setExploded( boolean exploded )
    {
        this.exploded = exploded;
    }
}
