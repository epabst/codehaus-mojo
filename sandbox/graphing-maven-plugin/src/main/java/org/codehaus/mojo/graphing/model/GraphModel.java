package org.codehaus.mojo.graphing.model;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Graph Model.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class GraphModel
{
    /** Edge type for 2 nodes that are equivalent and have no dependency */
    public static final int EDGE_NORMAL = 1;

    /** Edge type for 2 nodes that are equivalent and have no dependency */
    public static final int EDGE_PARENT = 2;

    /** Edge type for a compile dependency from node1 to node2 */
    public static final int EDGE_COMPILE_DEPENDENCY = 10;

    /** Edge type for a test dependency from node1 to node2 */
    public static final int EDGE_TEST_DEPENDENCY = 11;

    /** Edge type for a runtime dependency from node1 to node2 */
    public static final int EDGE_RUNTIME_DEPENDENCY = 12;

    /** Edge type for a provided dependency from node1 to node2 */
    public static final int EDGE_PROVIDED_DEPENDENCY = 13;

    /** Edge type for a system dependency from node1 to node2 */
    public static final int EDGE_SYSTEM_DEPENDENCY = 13;

    /** Node type for a normal node. */
    public static final int NODE_NORMAL = 1;

    /** Node type for the node representing the project. */
    public static final int NODE_PROJECT = 2;

    /** Node type for a node with an error. */
    public static final int NODE_ERROR = 3;

    private Map edges;
    private Map nodes;

    private Node centerNode;

    /**
     * Create a GraphModel.
     */
    public GraphModel()
    {
        this.edges = new HashMap();
        this.nodes = new HashMap();
    }

    /**
     * Add an edge.
     * 
     * @param edge
     */
    public boolean addEdge( Edge edge )
    {
        addNode( edge.getNode1() );
        addNode( edge.getNode2() );
        if(this.edges.containsKey(edge.getId())) {
            return false;
        }
        this.edges.put( edge.getId(), edge );
        return true;
    }

    /**
     * @return Returns the edges.
     */
    public Iterator getEdgesIterator()
    {
        return edges.values().iterator();
    }

    /**
     * Add a node.
     * 
     * @param node
     */
    public boolean addNode( Node node )
    {
        if(this.nodes.containsKey(node.getId())) {
            return false;
        }
        this.nodes.put( node.getId(), node );
        return true;
    }

    /**
     * @return Returns the nodes.
     */
    public Iterator getNodesIterator()
    {
        return nodes.values().iterator();
    }

    public Node getCenterNode()
    {
        return centerNode;
    }

    public void setCenterNode( Node centerNode )
    {
        this.centerNode = centerNode;
    }

}
