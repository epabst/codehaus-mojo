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
 * ConnectorDescriptor is the same for Connector as PluginDescriptor for MavenPlugin.
 * 
 * It contains basic information about connector and the xstream mappings for the connector's configuration file.
 * 
 * In most cases the class is serialized and deserialized form XML by {@link ConnectorDescriptorBuilder}
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class DefaultConnectorDescriptor implements ConnectorDescriptor
{
    private String groupId;

    private String artifactId;

    private String version;

    private String name;

    private String description;

    private String mainClassName;

    // private String source;

    private Class<? extends ConnectorConfiguration> configurationClass;

    private ConnectorInterfaceEnum preferredInterface;

    private EnumSet<ConnectorInterfaceEnum> providedInterfaces;

    // private List<Parameter> parameters;

    private List<Mapping> mappings;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#createConnectorInstance()
     */
    public DiagramConnector createConnectorInstance() throws ConnectorException
    {
        try
        {
            return (DiagramConnector) this.getClass().getClassLoader().loadClass( mainClassName ).newInstance();
        }
        catch ( InstantiationException e )
        {
            throw new ConnectorException( "Cannot create instance of the connector: " + artifactId + ":", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ConnectorException( "Cannot create instance of the connector: " + artifactId + ":", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ConnectorException( "Cannot create instance of the connector: " + artifactId + ":", e );
        }
    }

    /*---------------- Getters and Setters ---------------------*/

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getGroupId()
     */
    public String getGroupId()
    {
        return groupId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setGroupId(java.lang.String)
     */
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getArtifactId()
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setArtifactId(java.lang.String)
     */
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getVersion()
     */
    public String getVersion()
    {
        return version;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setVersion(java.lang.String)
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setDescription(java.lang.String)
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getProvidedInterfaces()
     */
    public EnumSet<ConnectorInterfaceEnum> getProvidedInterfaces()
    {
        return providedInterfaces;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setProvidedInterfaces(java.util.EnumSet)
     */
    public void setProvidedInterfaces( EnumSet<ConnectorInterfaceEnum> providedInterfaces )
    {
        this.providedInterfaces = providedInterfaces;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getPreferredInterface()
     */
    public ConnectorInterfaceEnum getPreferredInterface()
    {
        return preferredInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setPreferredInterface(org.apache.maven.diagrams.connector_api.descriptor.ConnectorInterfaceEnum)
     */
    public void setPreferredInterface( ConnectorInterfaceEnum preferredInterface )
    {
        this.preferredInterface = preferredInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getName()
     */
    public String getName()
    {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getConfigurationClass()
     */
    public Class<? extends ConnectorConfiguration> getConfigurationClass()
    {
        return configurationClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setConfigurationClass(java.lang.Class)
     */
    public void setConfigurationClass( Class<? extends ConnectorConfiguration> configurationClass )
    {
        this.configurationClass = configurationClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#getMappings()
     */
    public List<Mapping> getMappings()
    {
        return mappings;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor#setMappings(java.util.List)
     */
    public void setMappings( List<Mapping> mappings )
    {
        this.mappings = mappings;
    }

    public void setConfigurationClassName( String s ) throws ClassNotFoundException
    {
        configurationClass = (Class<? extends ConnectorConfiguration>) this.getClass().getClassLoader().loadClass( s );
    }
}
