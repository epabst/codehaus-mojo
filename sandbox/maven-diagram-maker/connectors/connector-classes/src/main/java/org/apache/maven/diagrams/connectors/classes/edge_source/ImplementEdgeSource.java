package org.apache.maven.diagrams.connectors.classes.edge_source;

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

import org.apache.maven.diagrams.connectors.classes.ClassDataSourceException;
import org.apache.maven.diagrams.connectors.classes.graph.ClassEdge;
import org.apache.maven.diagrams.connectors.classes.graph.ClassNode;
import org.apache.maven.diagrams.connectors.classes.graph.ImplementationEdge;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ImplementEdgeSource extends AbstractEdgeSource
{

    public ImplementEdgeSource()
    {
    }

    @Override
    protected List<ClassEdge> createOutgoingEdges( ClassNode sourceNode )
    {
        List<ClassEdge> result = new LinkedList<ClassEdge>();
        for ( String interf : sourceNode.getInterfaceNames() )
        {
            try
            {
                result.add( new ImplementationEdge(sourceNode, getClassNodesRepository().getClassNode( interf )));
            }
            catch ( ClassDataSourceException e )
            {
                if ( getLogger() != null )
                    getLogger().warn(
                                      "Cannot get informations about interface: " + interf + " (interface of "
                                                      + sourceNode.getFull_name() + ") - skipping", e );
            }
        }
        return result;
    }

    @Override
    protected AddNodeStatus canAddNode( ClassNode node )
    {
        return AddNodeStatus.DONT_ADD_NODE;
    }
}
