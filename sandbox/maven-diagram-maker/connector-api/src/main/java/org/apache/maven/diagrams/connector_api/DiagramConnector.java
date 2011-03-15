package org.apache.maven.diagrams.connector_api;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor;
import org.apache.maven.diagrams.graph_api.Graph;
import org.apache.maven.project.MavenProject;

/**
 * Interface for all connector's
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public interface DiagramConnector
{
    /**
     * If the connector supports the "Static interface" (ConnectorInterfaceEnum.STATIC) the method should return
     * calculated graph. It returns null otherwise
     * 
     * @param configuration
     * @return
     * @throws ConnectorException
     */
    public Graph calculateGraph( ConnectorConfiguration configuration ) throws ConnectorException;

    /**
     * If the connector supports the "Dynamic interface / Listener model" (ConnectorInterfaceEnum.Dynamic) the method
     * should return DynamicDiagramConnector (for the Listening management - not thread safe, for single thread use
     * only)
     * 
     * @param configuration
     * @return
     * @throws ConnectorException
     */
    public DynamicDiagramConnector getDynamicDiagramConnector() throws ConnectorException;

    /**
     * Returns connector's descriptor.
     * 
     * @throws ConnectorException
     */
    public ConnectorDescriptor getConnectorDescriptor() throws ConnectorException;

    //
    // /**
    // * Sets the connector's context (environment)
    // *
    // * @param new_context
    // */
    // public void setConnectorContext( ConnectorContext new_context );
    //
    // /**
    // * Returns the connector's context (environment)
    // *
    // * @return
    // */
    // public ConnectorContext getConnectorContext();

    public void setMavenProject( MavenProject mavenProject );

    public void setArtifactRepository( ArtifactRepository artifactRepository );
}
