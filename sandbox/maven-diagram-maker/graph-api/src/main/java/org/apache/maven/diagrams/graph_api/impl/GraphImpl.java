package org.apache.maven.diagrams.graph_api.impl;

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
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.maven.diagrams.graph_api.Edge;
import org.apache.maven.diagrams.graph_api.Graph;
import org.apache.maven.diagrams.graph_api.GraphMetadata;
import org.apache.maven.diagrams.graph_api.Node;

/**
 * Simple {@link Graph} implementation (using list of nodes and list of edges)
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class GraphImpl implements Graph
{

    /**
     * Node's id to node map
     */
    private LinkedHashMap<String, Node> nodes;

    /**
     * Edge's id to edge map
     */
    private LinkedHashMap<String, Edge> edges;

    private GraphMetadata metadata;

    // public GraphImpl() {
    // nodes = new LinkedHashMap<String, Node>();
    // edges = new LinkedHashMap<String, Edge>();
    // metadata = null;
    // }

    public GraphImpl( GraphMetadata a_metadata )
    {
        nodes = new LinkedHashMap<String, Node>();
        edges = new LinkedHashMap<String, Edge>();
        metadata = a_metadata;
    }

    public LinkedHashMap<String, Edge> getEdges()
    {
        return edges;
    }

    public LinkedHashMap<String, Node> getNodes()
    {
        return nodes;
    }

    public void addEdge( Edge edge )
    {
        if ( !nodes.containsKey( edge.getStartNode().getId() ) )
            throw new IllegalStateException( "Start node (" + edge.getStartNode().getId() + ") of the edge: "
                            + edge.getId() + " does not belong to the graph" );
        if ( !nodes.containsKey( edge.getEndNode().getId() ) )
            throw new IllegalStateException( "End node (" + edge.getEndNode().getId() + ") of the edge: "
                            + edge.getId() + " does not belong to the graph" );
        edges.put( edge.getId(), edge );
    }

    public void addNode( Node node )
    {
        nodes.put( node.getId(), node );
    }

    public void addNodes( Collection<? extends Node> nodes )
    {
        for ( Node node : nodes )
            addNode( node );
    }

    public void addEdges( Collection<? extends Edge> edges )
    {
        for ( Edge edge : edges )
            addEdge( edge );
    }

    public Edge getEdge( String id )
    {
        return edges.get( id );
    }

    public Node getNode( String id )
    {
        return nodes.get( id );
    }

    public GraphMetadata getGraphMetadata()
    {
        return metadata;
    }

    public void setGraphMetadata( GraphMetadata metadata )
    {
        this.metadata = metadata;
    }

}
