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
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Build PDE artifact. 
 * Use this goal with Maven default build lifecycle.
 * The output of this build is deployable
 * 
 * @version $Id:$
 * @goal attach
 * @phase compile
 * @requiresProject true
 * @aggregator
 * @requiresDependencyResolution test
 * @author dtran@gmail.com
 */

public class EclipsePDEAttachMojo
    extends EclipsePDEMojo
{

    /**
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * Do the work. Attach Ant's output artifact to maven with extension "zip"
     * 
     * @throws MojoExecutionException build failures.
     * @throws MojoFailureException build failures.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        Artifact artifact = artifactFactory.createArtifact( project.getGroupId(), project.getArtifactId(), project
            .getVersion(), null, "zip" );

        artifact.setFile( this.locateAntOutputFile() );
        
        project.addAttachedArtifact( artifact );
    }

}
