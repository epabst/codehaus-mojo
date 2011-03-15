package org.apache.maven.diagrams.graph_api;

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
import java.util.List;

/**
 * Interface for object providing basic (general) informations about the graph
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public interface GraphMetadata
{
    /**
     * Return's if the edges in the graph are directed
     * 
     * @return information if edges in the graph are directed
     */
    public boolean isDirected();

    /**
     * List of node's properties (nodes are JavaBeans) so it should be list of possible properties names in the graph's
     * node implementation
     * 
     * @return
     */
    public List<String> getNodePropertiesNames();

    /**
     * List of edge's properties (edges are JavaBeans) so it should be list of possible properties names in the graph's
     * edge implementation
     * 
     * @return
     */
    public List<String> getEdgePropertiesNames();
}
