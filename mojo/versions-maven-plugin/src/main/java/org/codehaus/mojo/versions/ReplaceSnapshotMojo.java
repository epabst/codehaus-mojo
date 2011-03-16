package org.codehaus.mojo.versions;

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
 * Sets the current projects version by replacing "-SNAPSHOT" with the value of <code>newSuffix</code>,
 * updating the details of any child modules as necessary.
 *
 * @author Eric Pabst
 * @goal replace-snapshot
 * @aggregator
 * @requiresProject true
 * @requiresDirectInvocation true
 * @since 1.2
 */
public class ReplaceSnapshotMojo
        extends AbstractSetMojo
{

    /**
     * The new version number suffix to use instead of "-SNAPSHOT".  It should start with a "." or "-".
     *
     * @parameter expression="${newSuffix}"
     * @since 1.2
     */
    private String newSuffix;

    /**
     * Called when this mojo is executed.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          when things go wrong.
     * @throws org.apache.maven.plugin.MojoFailureException
     *          when things go wrong.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( updateMatchingVersions == null )
        {
            updateMatchingVersions = Boolean.TRUE;
        }

        String oldVersion = getProject().getOriginalModel().getVersion();

        if ( oldVersion == null )
        {
            throw new MojoExecutionException( "Project version is inherited from parent." );
        }

        String oldSuffix = "-SNAPSHOT";

        if ( oldVersion.endsWith(oldSuffix) )
        {
            setVersion( oldVersion.substring(0, oldVersion.length() - oldSuffix.length()) + newSuffix, oldVersion);
        }
        else {
            getLog().info( "Leaving version unchanged as " + oldVersion);
        }
    }

}

