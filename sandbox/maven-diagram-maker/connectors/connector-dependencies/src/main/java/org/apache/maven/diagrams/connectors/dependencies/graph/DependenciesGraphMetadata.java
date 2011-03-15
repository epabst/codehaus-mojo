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
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.diagrams.graph_api.GraphMetadata;

/**
 * Graph metadata (in meaning of Graph-api)
 * 
 * The class describes properties of nodes and edges in classes graph.
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class DependenciesGraphMetadata implements GraphMetadata
{
    private static List<String> nodePropertiesNames;

    private static List<String> edgePropertiesNames;

    public List<String> getNodePropertiesNames()
    {
        synchronized ( DependencyGraphMetadata.class )
        {
            if ( nodePropertiesNames == null )
            {
                nodePropertiesNames = new LinkedList<String>();
            }
            return nodePropertiesNames;
        }
    }

    public List<String> getEdgePropertiesNames()
    {
        synchronized ( DependencyGraphMetadata.class )
        {
            if ( edgePropertiesNames == null )
            {
                edgePropertiesNames = new LinkedList<String>();
            }
            return edgePropertiesNames;
        }
    }

    public boolean isDirected()
    {
        return true;
    }

}
