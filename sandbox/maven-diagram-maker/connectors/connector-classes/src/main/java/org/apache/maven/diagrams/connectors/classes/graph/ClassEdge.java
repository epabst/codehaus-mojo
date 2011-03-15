package org.apache.maven.diagrams.connectors.classes.graph;

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
import org.apache.maven.diagrams.graph_api.Node;

/**
 * The common Edge conneting two classes
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public abstract class ClassEdge implements Edge
{
    /** source */
    private ClassNode child;

    /** destination */
    private ClassNode parent;

    public ClassEdge( ClassNode child, ClassNode parent )
    {
        this.child = child;
        this.parent = parent;
    }

    public Node getEndNode()
    {
        return parent;
    }

    public String getId()
    {
        return child.getId() + "-" + parent.getId();
    }

    public Node getStartNode()
    {
        return child;
    }

    /**
     * Is the edge directed
     */
    public boolean isDirected()
    {
        return true;
    }

}
