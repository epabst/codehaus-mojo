package org.codehaus.mojo.gae;

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
import org.apache.maven.plugin.MojoFailureException;

/**
 * Rollback (i.e. undeploy) the GAE application.
 * @goal rollback
 * @requiresProject
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class RollbackMojo
    extends AbstractGoogleAppEngineMojo
{
   
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        String[] args = new String[] { "--disable_prompt", "rollback", getWebappDirectory() };
        executeSDKClass( "com.google.appengine.tools.admin.AppCfg", args, false );
    }

}
