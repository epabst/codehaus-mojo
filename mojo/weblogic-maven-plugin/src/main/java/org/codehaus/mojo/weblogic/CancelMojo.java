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
 * Cancel the deployment of an artifact.
 * <p/>
 * Date: Apr 1, 2008
 * Time: 10:04:51 PM
 *
 * @author <a href="mailto:josborn@belltracy.com>Jon Osborn</a>
 * @version $Id$
 * @description issue the weblogic -cancel deployment task
 */
public class CancelMojo
    extends DeployMojoBase
{
    /**
     * This method will perform a -cancel operation
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Thrown if we fail to obtain a Weblogic deployment instance.
     */
    public void execute()
        throws MojoExecutionException
    {

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic cancel beginning " );
        }

        // get the basic parameters
        String[] parameters = this.getInputParameters( "cancel" );
        final boolean success = super.executeDeployer( parameters, "Exception encountered during artifact cancel" );

        if ( getLog().isInfoEnabled() )
        {
            if ( success )
            {
                getLog().info( "Weblogic artifact cancel successful " );
            }
            else
            {
                getLog().info( "Weblogic artifact cancel failed " );
            }
        }

    }
}
