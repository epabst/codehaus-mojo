package org.apache.maven.diagrams.connectors.classes;
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

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.context.ConnectorContext;
import org.apache.maven.diagrams.connector_api.context.RunMavenConnectorContext;
import org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor;
import org.apache.maven.diagrams.connector_api.descriptor.ConnectorInterfaceEnum;
import org.apache.maven.diagrams.connector_api.manager.ConnectorManager;
import org.apache.maven.diagrams.connectors.classes.config.AggregateEdgeType;
import org.apache.maven.diagrams.connectors.classes.config.ClassesConnectorConfiguration;
import org.apache.maven.diagrams.connectors.classes.config.EdgeType;
import org.apache.maven.diagrams.connectors.classes.config.ExcludeClasses;
import org.apache.maven.diagrams.connectors.classes.config.ImplementEdgeType;
import org.apache.maven.diagrams.connectors.classes.config.IncludeClasses;
import org.apache.maven.diagrams.connectors.classes.config.InheritanceEdgeType;
import org.apache.maven.diagrams.connectors.classes.config.Nodes;
import org.apache.maven.diagrams.graph_api.Graph;
import org.codehaus.plexus.component.repository.ComponentDependency;

public class ClassesConnectorTest extends TestCase
{
    
    protected static ConnectorContext getContext() throws URISyntaxException
    {
        RunMavenConnectorContext context=new RunMavenConnectorContext();
       // context.setBaseDir(new File("/home/ptab/newitech/x/pr/NITcommons/NITweblib") );        
        File f=new File(ClassesConnectorTest.class.getResource("/maven_test").toURI());
        System.err.println(f);
        context.setBaseDir(f) ;
        //context.setMavenHomeDir( new File("/home/ptab/mvn21/") );        
        return context;
    }
    
    public void test1() throws Exception
    {        
        ClassesConnector cc=new ClassesConnector();
        ConnectorDescriptor desc=cc.getConnectorDescriptor();
        assertNotNull("Cannot get connector's descriptor",desc);
        assertEquals(desc.getPreferredInterface(),ConnectorInterfaceEnum.STATIC);
        assertEquals(desc.getConfigurationClass(),ClassesConnectorConfiguration.class);
    }
    
    public void testCalculateGraph() throws Exception
    {        
        ClassesConnector cc=new ClassesConnector();
        ConnectorDescriptor desc=cc.getConnectorDescriptor();
        assertNotNull("Cannot get connector's descriptor",desc);
        assertEquals(desc.getPreferredInterface(),ConnectorInterfaceEnum.STATIC);
        assertEquals(desc.getConfigurationClass(),ClassesConnectorConfiguration.class);
        ConnectorManager cm=new ConnectorManager();
        ClassesConnectorConfiguration conf=(ClassesConnectorConfiguration) cm.fromXML( ClassesConnectorTest.class.getResourceAsStream( "testCalculateGraph1-configuration.xml" ), desc );
        cc.setConnectorContext( getContext() );
        Graph g=cc.calculateGraph( conf );
        System.err.println(g.getNodes());
    }
    
    public void testToXML() throws InstantiationException, IllegalAccessException, ConnectorException
    {
        ClassesConnector cc=new ClassesConnector();
        ConnectorDescriptor desc=cc.getConnectorDescriptor();
        ConnectorConfiguration con= desc.getConfigurationClass().newInstance();
        
        ClassesConnectorConfiguration ccc=(ClassesConnectorConfiguration)con;
        ccc.setFullInheritancePaths( true );
       // ccc.setDependencies( new ArrayList<ComponentDependency>() );
        ComponentDependency dep= new ComponentDependency();
        dep.setArtifactId( "log4j" );
        dep.setGroupId( "log4j" );
        dep.setVersion( "1.2.8" );
        //ccc.getDependencies().add( dep);
        
        ccc.setEdges( new ArrayList<EdgeType>() );
        ccc.getEdges().add(new AggregateEdgeType());
        ccc.getEdges().add(new ImplementEdgeType());
        ccc.getEdges().add(new InheritanceEdgeType());
        
        ccc.setIncludes( new ArrayList<IncludeClasses>() );
        ccc.getIncludes().add(new IncludeClasses("org\\.apache\\.*"));
        ccc.getIncludes().add(new IncludeClasses("org\\.codehaus\\.*"));
        
        ccc.setExcludes( new ArrayList<ExcludeClasses>() );
        ccc.getExcludes().add(new ExcludeClasses("org\\.apache\\.maven\\.*",true));
        ccc.getExcludes().add(new ExcludeClasses("org\\.apache\\.commons\\.*",false));
        
        ccc.setNodes( new Nodes() );
        
        ConnectorManager cm=new ConnectorManager();
        System.out.println(cm.toXML(  con,desc));
    }
    
    public void testFromXML() throws InstantiationException, IllegalAccessException, ConnectorException
    {
        ClassesConnector cc=new ClassesConnector();
        ConnectorDescriptor desc=cc.getConnectorDescriptor();

        
        ConnectorManager cm=new ConnectorManager();
        ClassesConnectorConfiguration conf=(ClassesConnectorConfiguration) cm.fromXML( ClassesConnectorTest.class.getResourceAsStream( "/conf2.xml" ), desc );
        System.out.println("==============================");
        System.out.println(cm.toXML( conf, desc ));
    }
}
