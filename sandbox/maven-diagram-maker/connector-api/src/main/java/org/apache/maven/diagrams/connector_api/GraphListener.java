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

import org.apache.maven.diagrams.graph_api.Edge;
import org.apache.maven.diagrams.graph_api.GraphMetadata;
import org.apache.maven.diagrams.graph_api.Node;

/**
 * Interface for listening event's about graph's changes.
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public interface GraphListener
{
    public void init( GraphMetadata metadata );

    public void addNode( Node node );

    public void delNode( Node node );

    public void addEdge( Edge edge );

    public void delEdge( Node node );

    /**
     * Marks that the graph is finished (no more changes are allowed)
     */
    public void finish();
}
