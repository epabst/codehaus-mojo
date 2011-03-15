package org.codehaus.mojo.jboss.packaging;

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
 * Builds a deployable JBoss ESB exploded Archive.
 * 
 * @author <a href="mailto:kevin.conner@jboss.com">Kevin Conner</a>
 * @goal esb-exploded
 * @phase package
 * @requiresDependencyResolution runtime
 * @deprecated Use esb goal with "explodedOnly" parameter
 */
public class ESBExplodedMojo
    extends ESBMojo
{
    /**
     * Execute the mojo in the current project.
     * 
     * @throws MojoExecutionException For plugin failures.
     * @throws MojoFailureException For unexpected plugin failures.
     */
    public void execute()
        throws MojoExecutionException
    {
        buildExplodedPackaging();
    }
}
