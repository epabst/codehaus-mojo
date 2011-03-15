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

/**
 * Edge definition.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class Edge
{
    private Node node1;

    private Node node2;

    private int type;

    /**
     * 
     * @param n1
     * @param n2
     */
    public Edge( Node n1, Node n2 )
    {
        this( n1, n2, GraphModel.EDGE_NORMAL );
    }

    /**
     * 
     * @param n1
     * @param n2
     * @param edgeType
     */
    public Edge( Node n1, Node n2, int edgeType )
    {
        this.node1 = n1;
        this.node2 = n2;
        this.type = edgeType;
    }

    /**
     * @return Returns the node1.
     */
    public Node getNode1()
    {
        return node1;
    }

    /**
     * @param node1 The node1 to set.
     */
    public void setNode1( Node node1 )
    {
        this.node1 = node1;
    }

    /**
     * @return Returns the node2.
     */
    public Node getNode2()
    {
        return node2;
    }

    /**
     * @param node2 The node2 to set.
     */
    public void setNode2( Node node2 )
    {
        this.node2 = node2;
    }

    /**
     * @return Returns the type.
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType( int type )
    {
        this.type = type;
    }
    
    public String getId() {
        return node1.getId() + "<->" + node2.getId();
    }

    public String toString()
    {
        return "Edge:" + node1 + "-" + node2;
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof Node ) )
        {
            return false;
        }
        Edge rhs = (Edge) obj;
        if ( rhs.getId().equals( this.getId() ) )
        {
            return false;
        }
        return true;
    }

    public int hashCode()
    {
        int result = 15;
        result = 21 * result + getId().hashCode();
        return result;
    }
}
