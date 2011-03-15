package org.apache.maven.diagrams.connectors.classes.edge_source;

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
import java.util.Set;

import org.apache.maven.diagrams.connectors.classes.ClassNodesRepository;
import org.apache.maven.diagrams.connectors.classes.config.ClassesConnectorConfiguration;
import org.apache.maven.diagrams.connectors.classes.filter.FilterRepository;
import org.apache.maven.diagrams.connectors.classes.graph.ClassEdge;
import org.apache.maven.diagrams.connectors.classes.graph.ClassNode;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public interface EdgeSource
{
    public static String ROLE = EdgeSource.class.getName();

    /**
     * Sets the main (dependent components) in one call.
     * 
     * @param a_filterRepository
     * @param a_classNodesRepository
     * @param a_configuration
     */
    public abstract void configure( FilterRepository a_filterRepository, ClassNodesRepository a_classNodesRepository,
                                    ClassesConnectorConfiguration a_configuration );

    /**
     * The method returns created edges. It can also add new nodes to "resultNodes"
     */
    public abstract Set<ClassEdge> calculateEdges( Set<ClassNode> resultNodes );

    public abstract FilterRepository getFilterRepository();

    public abstract ClassNodesRepository getClassNodesRepository();

    public abstract ClassesConnectorConfiguration getConfiguration();

}