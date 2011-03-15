package org.apache.maven.diagrams.gui.graph_adapter;

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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.diagrams.graph_api.Edge;
import org.apache.maven.diagrams.gui.renderers.RendererNodeCache;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

import prefuse.data.Graph;
import prefuse.data.Node;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class GraphToPrefuseGraph
{
    static public Graph graph2PrefuseGraph( org.apache.maven.diagrams.graph_api.Graph sourceGraph )
    {
        Graph g = new Graph( true );

        g.addColumn( RendererNodeCache.CACHE_ITEM_COLUMN_NAME, Object.class );

        g.addColumn( "node", org.apache.maven.diagrams.graph_api.Node.class );
        g.addColumn( "id", String.class );

        if ( sourceGraph.getGraphMetadata() != null )
        {
            for ( String prop : sourceGraph.getGraphMetadata().getNodePropertiesNames() )
            {
                g.addColumn( prop, Object.class );
            }
        }

        Map<String, Node> nodes = new HashMap<String, Node>( sourceGraph.getNodes().size() );

        for ( org.apache.maven.diagrams.graph_api.Node node : sourceGraph.getNodes().values() )
        {
            Node n = g.addNode();
            n.set( "node", node );
            n.setString( "id", node.getId() );

            if ( sourceGraph.getGraphMetadata() != null )
            {
                for ( String prop : sourceGraph.getGraphMetadata().getNodePropertiesNames() )
                {
                    try
                    {
                        n.set( prop, ReflectionValueExtractor.evaluate( prop, node, false ) );
                    }
                    catch ( Exception e )
                    {
                        // TODO: Add logging (transform this to plexus
                        // component)
                        // getLogger().
                        e.printStackTrace( System.err );
                    }
                }
            }

            // TO_MA_SIE NIE KOMPILOWACn.setBoolean( "interface", (Boolean) (
            // (ClassNode) node ).isInterface() );
            // n.setDouble( "width", -1.0);
            // n.setDouble( "height", -1.0);
            nodes.put( node.getId(), n );
        }

        for ( Edge edge : sourceGraph.getEdges().values() )
        {
            //System.out.println("Edge:"+edge.getId());
            Node n1 = nodes.get( edge.getStartNode().getId() );
            Node n2 = nodes.get( edge.getEndNode().getId() );
            g.addEdge( n1, n2 );
        }

        return g;
    }
}
