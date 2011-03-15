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
 * Node type.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class Node
{
    private String id;

    private String label;

    private int type;

    /**
     * 
     * @param id
     */
    public Node( String id )
    {
        this( id, id, GraphModel.NODE_NORMAL );
    }

    /**
     * 
     * @param id
     * @param label
     */
    public Node( String id, String label )
    {
        this( id, label, GraphModel.NODE_NORMAL );
    }

    /**
     * 
     * @param id
     * @param label
     * @param nodeType
     */
    public Node( String id, String label, int nodeType )
    {
        this.id = id;
        this.label = label;
        this.type = nodeType;
    }

    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param label The label to set.
     */
    public void setLabel( String label )
    {
        this.label = label;
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
    
    public String toString()
    {
        return "Node:" + id + ":" + type;
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !(obj instanceof Node) )
        {
            return false;
        }        
        Node rhs = (Node) obj;
        if(!rhs.getId().equals(id)) {
            return false;
        }
        return true;
    }

    public int hashCode()
    {
        int result = 15;
        result = 67 * result + id.hashCode();
        return result;
    }

}
