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
 * Builds a deployable JBoss Service Archive.
 * 
 * @goal sar
 * @phase package
 * @requiresDependencyResolution runtime
 * @threadSafe
 */
public class SarMojo
    extends AbstractPackagingMojo
{

    /**
     * The artifact type.
     */
    private static final String ARTIFACT_TYPE = "jboss-sar";

    /**
     * The location of the jboss deployment descriptor file (jboss-service.xml) If it is present in
     * src/main/resources/META-INF then it will automatically be included. Otherwise this parameter must be set.
     * 
     * @parameter default-value="${project.build.outputDirectory}/META-INF/jboss-service.xml"
     *            expression="${deploymentDescriptorFile}"
     */
    private File deploymentDescriptorFile;

    /**
     * Get the type of the artifact.
     * 
     * @return The type of the generated artifact.
     */
    public String getArtifactType()
    {
        return ARTIFACT_TYPE;
    }

    public File getDeploymentDescriptor()
    {
        return deploymentDescriptorFile;
    }

}
