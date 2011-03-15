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

/**
 * Builds a deployable JBoss Spring Archive.
 * 
 * @goal spring
 * @phase package
 * @requiresDependencyResolution runtime
 * @threadSafe
 */
public class SpringMojo
    extends AbstractPackagingMojo
{

    /**
     * The location of the jboss deployment descriptor file (jboss-spring.xml). If it is present in
     * src/main/resources/META-INF with that name then it will automatically be included. Otherwise this parameter must
     * be set.
     * 
     * @parameter default-value="${project.build.outputDirectory}/META-INF/jboss-spring.xml"
     *            expression="${deploymentDescriptorFile}"
     */
    private File deploymentDescriptorFile;

    /**
     * The artifact type.
     */
    private static final String ARTIFACT_TYPE = "jboss-spring";

    /**
     * Return the name of the jboss-spring deployment descriptor (jboss-spring.xml).
     * 
     * @return Filename of the jboss spring deployment descriptor
     */
    public File getDeploymentDescriptor()
    {
        return deploymentDescriptorFile;
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
