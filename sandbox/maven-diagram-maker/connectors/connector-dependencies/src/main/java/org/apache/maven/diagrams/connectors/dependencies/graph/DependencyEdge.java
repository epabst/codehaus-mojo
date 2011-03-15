package org.apache.maven.diagrams.connectors.dependencies.graph;

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
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class DependencyEdge implements Edge
{
    private ArtifactNode use;

    private ArtifactNode used;

    public DependencyEdge( ArtifactNode a_use, ArtifactNode a_used )
    {
        use = a_use;
        used = a_used;
    }

    public Node getEndNode()
    {
        return use;
    }

    public String getId()
    {
        return use.getId() + "-" + used.getId();
    }

    public Node getStartNode()
    {
        return used;
    }

}
