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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.diagrams.connectors.classes.ClassDataSourceException;
import org.apache.maven.diagrams.connectors.classes.graph.AggregationEdge;
import org.apache.maven.diagrams.connectors.classes.graph.ClassEdge;
import org.apache.maven.diagrams.connectors.classes.graph.ClassNode;
import org.apache.maven.diagrams.connectors.classes.model.FieldModel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class AggregateEdgeSource extends AbstractEdgeSource
{
    public AggregateEdgeSource()
    {
    }

    @Override
    protected List<ClassEdge> createOutgoingEdges( ClassNode sourceNode )
    {
        List<ClassEdge> result = new LinkedList<ClassEdge>();

        /* Add objects aggregated by fields */
        if ( sourceNode.getFields() != null )
            result.addAll( createOutgoingsAggregateEdgesFromFieldsList( sourceNode, sourceNode.getFields() ) );

        /* Add objects aggregated by properties */
        if ( sourceNode.getProperties() != null )
            result.addAll( createOutgoingsAggregateEdgesFromFieldsList( sourceNode, sourceNode.getProperties() ) );

        return result;
    }

    /**
     * The method returns all edges to external nodes (aggregated by given fields)
     * 
     * @param sourceNode
     * @param fields
     * @return
     */
    protected List<ClassEdge> createOutgoingsAggregateEdgesFromFieldsList( ClassNode sourceNode,
                                                                           Collection<FieldModel> fields )
    {
        List<ClassEdge> result = new LinkedList<ClassEdge>();
        for ( FieldModel field : fields )
        {
            String type = getBaseType( field.getType() );
            if ( isClassType( type ) )
            {
                try
                {
                    result.add( new AggregationEdge( sourceNode, getClassNodesRepository().getClassNode( type ) ) );
                }
                catch ( ClassDataSourceException e )
                {
                    if ( getLogger() != null )
                        getLogger().warn(
                                          "Cannot get informations about class: " + type + " (aggregated by "
                                                          + sourceNode.getFull_name() + ") - skipping", e );
                }
            }
        }
        return result;
    }

    /**
     * If we have an array of object - we don't want to know about it. The information about the base type of the array
     * is sufficient. It is returned by this method.
     * 
     * TODO: Move to single "helpers" class
     * 
     * @param type
     * @return
     */
    private String getBaseType( String type )
    {
        int index = type.indexOf( '[' );
        if ( index > 0 )
            return type.substring( 0, index );
        else
            return type;
    }

    /**
     * Returns true if the given type represents "class". Otherwise (int,void,boolean,...) returns "false". The method
     * works only for "base types" (does not works for arrays)
     * 
     */
    private boolean isClassType( String type )
    {

        return ( type != null ) && ( !type.equals( "void" ) ) && ( !type.equals( "int" ) )
                        && ( !type.equals( "boolean" ) ) && ( !type.equals( "float" ) ) && ( !type.equals( "long" ) )
                        && ( !type.equals( "double" ) && ( !type.equals( "char" ) ) );
    }

    @Override
    protected AddNodeStatus canAddNode( ClassNode node )
    {
        return AddNodeStatus.DONT_ADD_NODE;
    }
}
