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
 * Starts JBoss.
 * 
 * @author <a href="mailto:jgenender@apache.org">Jeff Genender</a>
 * @goal start
 * @requiresProject false
 */
public class StartMojo
    extends AbstractJBossServerMojo
{

    /**
     * The command to start JBoss.
     */
    public static final String STARTUP_COMMAND = "run";
    
    /**
     * The set of space separated options to pass to the JBoss "run" command.
     * 
     * @parameter default-value="" expression="${jboss.startOptions}"
     * @since 1.5.0
     */
    protected String startOptions;

    /**
     * The set of options to pass to the JBoss "run" command.
     * 
     * @parameter default-value="" expression="${jboss.options}"
     * @deprecated use startOptions instead
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
        if ( options == null )
        {
            options = "";
        }
        if ( startOptions == null )
        {
            startOptions = "";
        }
        startOptions += options;
        
        if ( ! serverName.equals( "default" ) )
        {
            startOptions += " -c " + serverName;
        }
        
        getLog().info( "Starting JBoss..." );
        launch( STARTUP_COMMAND, startOptions );
    }

}
