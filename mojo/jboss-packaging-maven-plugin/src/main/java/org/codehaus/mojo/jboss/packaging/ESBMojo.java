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

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Builds a deployable JBoss ESB Archive.
 * 
 * @author <a href="mailto:kevin.conner@jboss.com">Kevin Conner</a>
 * @goal esb
 * @phase package
 * @requiresDependencyResolution runtime
 * @threadSafe
 */
public class ESBMojo
    extends AbstractPackagingMojo
{
    /**
     * The name of the meta-inf directory.
     */
    private static final String META_INF = "META-INF";

    /**
     * The location of the deployment.xml file.
     */
    private static final String DEPLOYMENT_XML = "deployment.xml";

    /**
     * The artifact type.
     */
    private static final String ARTIFACT_TYPE = "jboss-esb";

    /**
     * Override the deployment xml file
     * 
     * @parameter expression="${maven.esb.deployment.xml}"
     */
    private File deploymentXml;

    /**
     * The location of the jboss deployment descriptor file (jboss-esb.xml) If it is present in
     * src/main/resources/META-INF then it will automatically be included. Otherwise this parameter must be set.
     * 
     * @parameter default-value="${project.build.outputDirectory}/META-INF/jboss-esb.xml"
     *            expression="${deploymentDescriptorFile}"
     */
    private File deploymentDescriptorFile;

    /**
     * Perform any packaging specific to this type.
     * 
     * @param excludes The exclude list.
     * @throws MojoExecutionException For plugin failures.
     * @throws MojoFailureException For unexpected plugin failures.
     * @throws IOException For exceptions during IO operations.
     */
    protected void buildSpecificPackaging( final Set excludes )
        throws MojoExecutionException
    {
        final File metainfDir = new File( getOutputDirectory(), META_INF );
        if ( deploymentXml != null )
        {
            try
            {
                FileUtils.copyFile( deploymentXml, new File( metainfDir, DEPLOYMENT_XML ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Unable to copy deployment file ", e );
            }
        }
    }

    /**
     * @return deployment descriptor file name, sans path
     */
    public File getDeploymentDescriptor()
    {
        return this.deploymentDescriptorFile;
    }

    /**
     * Get the type of the artifact.
     * 
     * @return The type of the generated artifact.
     */
    public String getArtifactType()
    {
        return ARTIFACT_TYPE;
    }

}
