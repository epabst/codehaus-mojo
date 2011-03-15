package org.apache.maven.diagrams.connector_api.descriptor;

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

import java.util.EnumSet;
import java.util.List;

import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.DiagramConnector;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public interface ConnectorDescriptor
{

    public abstract DiagramConnector createConnectorInstance() throws ConnectorException;

    public abstract String getGroupId();

    public abstract void setGroupId( String groupId );

    public abstract String getArtifactId();

    public abstract void setArtifactId( String artifactId );

    public abstract String getVersion();

    public abstract void setVersion( String version );

    public abstract String getDescription();

    public abstract void setDescription( String description );

    public abstract EnumSet<ConnectorInterfaceEnum> getProvidedInterfaces();

    public abstract void setProvidedInterfaces( EnumSet<ConnectorInterfaceEnum> providedInterfaces );

    /**
     * Return which type of interface to the described connector (static or dynamic) should be preferred by libraries
     * using the connector.
     * 
     * It have to be one of interfaces returned by getProvidedInterfaces
     * 
     * @return preferred interface type.
     */
    public abstract ConnectorInterfaceEnum getPreferredInterface();

    /**
     * Sets the preferred interface.
     * 
     * @param preferredInterface
     */
    public abstract void setPreferredInterface( ConnectorInterfaceEnum preferredInterface );

    public abstract String getName();

    public abstract void setName( String name );

    /**
     * The method returns the ConnectorConfiguration class that the connector use.
     * 
     * @return the class implementing {@link ConnectorConfiguration}
     */
    public abstract Class<? extends ConnectorConfiguration> getConfigurationClass();

    /**
     * The method sets the ConnectorConfiguration class that the connector use.
     * 
     * @param configurationClass
     *            to set.
     */
    public abstract void setConfigurationClass( Class<? extends ConnectorConfiguration> configurationClass );

    /**
     * It return set of mappings "tag name to class" used by xstream library to serialize and deserialize
     * {@link ConnectorConfiguration}
     * 
     * @return
     */
    public abstract List<Mapping> getMappings();

    /**
     * It sets mappings "tag name to class" used by xstream library to serialize and deserialize
     * {@link ConnectorConfiguration}
     * 
     * @param mappings
     *            to be set
     */
    public abstract void setMappings( List<Mapping> mappings );

}