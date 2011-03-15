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

import java.net.URL;
import java.util.List;

import org.apache.maven.diagrams.connector_api.DiagramConnector;
import org.apache.maven.diagrams.connector_api.manager.ConnectorManager;
import org.apache.maven.diagrams.connectors.classes.config.ClassesConnectorConfiguration;
import org.apache.maven.diagrams.connectors.classes.graph.ClassNode;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ClassNodesRepositoryTest extends PlexusTestCase
{

    public void testGetClassNode() throws Exception
    {
        ConnectorManager cm = new ConnectorManager();
        ClassesConnector classesConnector = (ClassesConnector) lookup( DiagramConnector.class, "connector-classes" );
        ClassesConnectorConfiguration config =
            (ClassesConnectorConfiguration) cm.fromXML(
                                                        DefaultClassNodesRepository.class.getResourceAsStream( "testGetClassNode-configuration.xml" ),
                                                        classesConnector.getConnectorDescriptor() );

        // URL[] classpathItems=new RL[]{new
        // File("/home/ptab/gsoc/Maveny_dev/plugins/maven-assembly-plugin/target/classes/").toURL()};
        URL[] classpathItems = new URL[] { DefaultClassNodesRepository.class.getResource( "/log4j.jar" ) };

        List<String> lista = PackageUtils.getClassNamesOnClassPathItem( classpathItems[0] );
        ClassModelsRepository classModelsRepository = new ClassModelsRepository( classpathItems );
        ClassNodesRepository classNodesRepository = new DefaultClassNodesRepository( classModelsRepository, config );

        for ( String s : lista )
        {
            System.out.println( "-------------------------------------------------------" );
            System.out.println( s );
            ClassNode classNode = classNodesRepository.getClassNode( s );
            classNode.print( System.out );
        }

        System.out.println( "-------------------------------------------------------" );
        ClassNode classNode = classNodesRepository.getClassNode( "java.lang.Object" );
        classNode.print( System.out );
    }

}
