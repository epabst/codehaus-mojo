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
import org.apache.maven.diagrams.connectors.classes.config.AggregateEdgeType;
import org.apache.maven.diagrams.connectors.classes.config.EdgeType;
import org.apache.maven.diagrams.connectors.classes.config.ImplementEdgeType;
import org.apache.maven.diagrams.connectors.classes.config.InheritanceEdgeType;

/**
 * The object creates EdgeSource instance for given EdgeType configuration.
 * 
 * @author Piotr Tabor
 * 
 */
public class EdgeSourceFactory
{
    /**
     * The method creates EdgeSource instance for given EdgeType configuration.
     * 
     * @param edgeType
     * @return
     */
    public static AbstractEdgeSource createEdgeSource( EdgeType edgeType )
    {
        if ( ImplementEdgeType.class.isInstance( edgeType ) )
        {
            return new ImplementEdgeSource( (ImplementEdgeType) edgeType );
        }
        else if ( InheritanceEdgeType.class.isInstance( edgeType ) )
        {
            return new InheritanceEdgeSource( (InheritanceEdgeType) edgeType );
        }
        else if ( AggregateEdgeType.class.isInstance( edgeType ) )
        {
            return new AggregateEdgeSource( (AggregateEdgeType) edgeType );
        }

        return null;
    }
}
