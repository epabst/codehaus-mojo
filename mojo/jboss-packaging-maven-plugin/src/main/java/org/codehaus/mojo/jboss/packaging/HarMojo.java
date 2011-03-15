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
 * Builds a deployable JBoss Hibernate Archive. Note that in versions of jboss prior to 4.0.3 the deployment descriptor
 * for a HAR was "hibernate-service.xml". In 4.0.3 this changed to use "jboss-service.xml" similar to the SAR format. In
 * JBoss 5 and above, the HAR deployment descriptor can be any file with a name that matches the pattern
 * "*-hibernate.xml". If the parameter "deploymentDescriptorFile" is not set, this goal will first look for
 * "jboss-service.xml", then "hibernate-service.xml" and if those are not found, it will search for a file ending with *
 * "-hibernate.xml"
 * 
 * @goal har
 * @phase package
 * @requiresDependencyResolution runtime
 * @threadSafe
 */
public class HarMojo
    extends AbstractPackagingMojo
{

    /**
     * The name of the hibernate deployment descriptor file. If left blank, the goal will automatically search for
     * "jboss-service.xml", "hibernate-service.xml", and "*-hibernate.xml" in that order.
     * 
     * @parameter expression="${deploymentDescriptorFile}"
     */
    private File deploymentDescriptorFile;

    /**
     * The artifact type.
     */
    private static final String ARTIFACT_TYPE = "jboss-har";

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

        if ( deploymentDescriptorFile != null )
        {
            return deploymentDescriptorFile;
        }

        // Look for deployment descriptor in the standard places
        File metaInf = new File( getProject().getBuild().getOutputDirectory(), "META-INF" );

        if ( !metaInf.exists() )
        {
            return null;
        }

        deploymentDescriptorFile = new File( metaInf, "jboss-service.xml" );
        if ( deploymentDescriptorFile.exists() )
        {
            return deploymentDescriptorFile;
        }

        deploymentDescriptorFile = new File( metaInf, "hibernate-service.xml" );
        if ( deploymentDescriptorFile.exists() )
        {
            return deploymentDescriptorFile;
        }

        // Look for "*-hibernate.xml" in META-INF
        String[] files = metaInf.list();
        for ( int i = 0; i < files.length; ++i )
        {
            if ( files[i].endsWith( "-hibernate.xml" ) )
            {
                return new File( metaInf, files[i] );
            }
        }
        return null;
    }

}
