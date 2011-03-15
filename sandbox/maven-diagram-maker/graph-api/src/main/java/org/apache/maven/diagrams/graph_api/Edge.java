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
/**
 * Interface for single graph edge (director or undirected)
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public interface Edge
{
    /**
     * Get business id of the edge
     * 
     * Business id should be unique within the graph
     * 
     * @return business id of the edge
     */
    public String getId();

    /**
     * Returns the start node (in directed graph) or one of the two nodes (in indirected) of the edge.
     * 
     * @return the node
     */
    public Node getStartNode();

    /**
     * Returns the start node (in directed graph) or second of the two nodes (in indirected) of the edge.
     * 
     * @return the node
     */
    public Node getEndNode();

}
