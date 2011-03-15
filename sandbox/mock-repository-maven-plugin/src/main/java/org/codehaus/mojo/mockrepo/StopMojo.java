package org.codehaus.mojo.mockrepo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Stops a mock maven remote repository.
 *
 * @author Stephen Connolly
 * @goal stop
 * @description Stops the mock maven remote repository.
 * @since 1.0-alpha-1
 */
public class StopMojo
    extends AbstractMockRepoMojo
{
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Stopping mock maven remote repository" );
        try
        {
            synchronized ( serverLock )
            {
                if ( server != null )
                {
                    server.stop();
                    getLog().info( "Mock maven repository stopped" );
                    server = null;
                }
                else
                {
                    getLog().info( "Mock maven repository not running!" );
                }
            }
        }
        catch ( Exception e )
        {
            getLog().error( e );
        }
    }
}
