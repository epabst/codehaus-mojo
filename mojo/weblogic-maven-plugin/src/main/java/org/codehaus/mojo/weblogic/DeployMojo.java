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

/**
 * Deploy an artifact to Weblogic servers(s) or cluster(s).
 *
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @version $Id$
 * @description Deploy an artifact (war, ear, etc) to a target(s) which can be servers or clusters.
 * @goal deploy
 */
public class DeployMojo
    extends DeployMojoBase
{

    /**
     * This method will perform the deployment of artifact to the targets.
     *
     * @throws MojoExecutionException Thrown if we fail to obtain a Weblogic deployment instance.
     */
    public void execute()
        throws MojoExecutionException
    {

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic Deployment beginning with parameters " + this.toString() );
        }

        // get the basic parameters
        final String[] parameters = this.getInputParameters( "deploy" );
        final boolean success = super.executeDeployer( parameters, "Exception encountered during artifact start" );

        if ( getLog().isInfoEnabled() )
        {
            if ( success )
            {
                getLog().info( "Weblogic artifact deploy successful " );
            }
            else
            {
                getLog().info( "Weblogic artifact deploy failed " );
            }
        }

    }

}
