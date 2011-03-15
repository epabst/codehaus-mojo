package org.codehaus.mojo.jboss;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Stops JBoss. By default the plugin will return immediately after calling "shutdown" command. The @see #stopWait
 * parameter can be used to force the plugin to wait for a specified time before returning control.
 * 
 * @author <a href="mailto:jgenender@apache.org">Jeff Genender</a>
 * @goal stop
 * @requiresProject false
 */
public class StopMojo
    extends AbstractJBossServerMojo
{

    /**
     * The command to shutdown JBoss.
     */
    public static final String SHUTDOWN_COMMAND = "shutdown";

    /**
     * The set of options to pass to the JBoss "run" command.
     * 
     * @parameter default-value="-S" expression="${jboss.stopOptions}"
     * @since 1.5.0
     */
    protected String stopOptions;

    /**
     * Wait in ms for server to shutdown before the plugin returns.
     * 
     * @since 1.4.1
     * @parameter expression="${jboss.stopWait}"
     */
    protected int stopWait;

    /**
     * The set of options to pass to the JBoss "run" command.
     * 
     * @parameter default-value="" expression="${jboss.options}"
     * @deprecated use stopOptions instead
     */
    protected String options;

    /**
     * Main plugin execution.
     * 
     * @throws MojoExecutionException
     */
    public void execute()
        throws MojoExecutionException
    {
        if ( options != null && !options.equals("") )
        {
            stopOptions = options;
        }
        
        String credentials = "";
        
        if ( getUsername() != null )
        {
            credentials = " -u " + getUsername() + " -p " + getPassword();
        }
        
        stopOptions += credentials;
        
        launch( SHUTDOWN_COMMAND, stopOptions );

        if ( stopWait > 0 )
        {
            try
            {
                Thread.sleep( stopWait );
            }
            catch ( InterruptedException e )
            {
                getLog().warn( "Thread interrupted while waiting for JBoss to stop: " + e.getMessage() );
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Thread interrupted while waiting for JBoss to stop: ", e );
                }
            }
        }
    }

}
