package org.apache.maven.diagrams.gui.bindings.connectors;

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
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.diagrams.connector_api.DiagramConnector;

import com.thoughtworks.xstream.XStream;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class BindingsManager
{
    /* Map from graph type into resolved binding */
    private Map<String, ResolvedBinding<DiagramConnector>> bindingsMapByGraphType =
        new HashMap<String, ResolvedBinding<DiagramConnector>>();

    private Map<String, ResolvedBinding<DiagramConnector>> bindingsMapByConnectorName =
        new HashMap<String, ResolvedBinding<DiagramConnector>>();

    public void loadAndResolveBindings( InputStream is )
    {
        XStream xstream = new XStream();
        xstream.aliasType( "diagrams-gui", DiagramGUIMapping.class );
        xstream.aliasType( "binding", Binding.class );

        // List<Binding> bindings_list = new LinkedList<Binding>();
        DiagramGUIMapping diagramGUIMapping = new DiagramGUIMapping();
        xstream.fromXML( is, diagramGUIMapping );

        List<ResolvedBinding<DiagramConnector>> resolvedBindings = resolveBindings( diagramGUIMapping.getBindings() );

        for ( ResolvedBinding<DiagramConnector> resolvedBinding : resolvedBindings )
        {
            bindingsMapByGraphType.put( resolvedBinding.getGraphType(), resolvedBinding );
            bindingsMapByConnectorName.put( resolvedBinding.getConnectorClass().getName(), resolvedBinding );
        }
    }

    private List<ResolvedBinding<DiagramConnector>> resolveBindings( List<Binding> bindings_list )
    {
        List<ResolvedBinding<DiagramConnector>> res = new LinkedList<ResolvedBinding<DiagramConnector>>();
        for ( Binding binding : bindings_list )
        {

            try
            {
                res.add( binding.resolveBinding() );
            }
            catch ( ResolveBindingException e )
            {
                // TODO: Logger
                e.printStackTrace( System.err );
            }

        }
        return res;
    }

    public Collection<ResolvedBinding<DiagramConnector>> getResolvedBindings()
    {
        return bindingsMapByConnectorName.values();
    }

    public ResolvedBinding<DiagramConnector> getBindingsForConnector( Class<? extends DiagramConnector> class_ )
    {
        return bindingsMapByConnectorName.get( class_.getName() );
    }

    public ResolvedBinding<DiagramConnector> getBindingsForGraphType( String graphType )
    {
        return bindingsMapByGraphType.get( graphType );
    }

    public ResolvedBinding<DiagramConnector> getBindingsForConnector( DiagramConnector con )
    {
        return getBindingsForConnector( con.getClass() );
    }

}
