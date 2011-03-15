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
 * Stop an artifact on Weblogic server(s) or cluster(s).
 *
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @version $Id$
 * @description This mojo will stop a component (EAR, WAR) on a server or group of servers.
 * @goal stop
 */
public class StopMojo
    extends DeployMojoBase
{

    /**
     * This method will perform the stop of an application unit.
     *
     * @throws MojoExecutionException Thrown if we fail to obtain a Weblogic deployment instance.
     */
    public void execute()
        throws MojoExecutionException
    {

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic artifact stop beginning with parameters " + this.toString() );
        }

        // get the basic parameters
        final String[] parameters = this.getInputParameters( "stop" );
        final boolean success = super.executeDeployer( parameters, "Exception encountered during artifact stop" );

        if ( getLog().isInfoEnabled() )
        {
            if ( success )
            {
                getLog().info( "Weblogic artifact stop successful " );
            }
            else
            {
                getLog().info( "Weblogic artifact stop failed " );
            }
        }
    }
}
