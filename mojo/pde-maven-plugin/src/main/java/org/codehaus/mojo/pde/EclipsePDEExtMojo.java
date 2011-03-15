package org.codehaus.mojo.pde;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Build PDE artifact via this plugin's custom build lifecycle. The output of this build is
 * deployable.
 * 
 * @version $Id:$
 * @goal ext
 * @requiresProject true
 * @aggregator
 * @author dtran@gmail.com
 */

public class EclipsePDEExtMojo
    extends EclipsePDEMojo
{

    /**
     * Do the work
     * 
     * @throws MojoExecutionException build failures.
     * @throws MojoFailureException build failures.
     * 
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        Artifact artifact = this.project.getArtifact();

        artifact.setFile( this.locateAntOutputFile() );
    }

}
