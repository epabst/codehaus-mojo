package org.apache.maven.diagrams.gui.model;

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
import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.connector_api.DiagramConnector;

import prefuse.data.Graph;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class Model
{
    // private ConnectorContext connectorContext;

    private DiagramConnector connector;

    private ConnectorConfiguration connectorConfiguration;

   // private MainWindow mainWindow;

    // ===============================================================

    private Graph graph;

    // public ConnectorContext getConnectorContext()
    // {
    // return connectorContext;
    // }
    //
    // public void setConnectorContext( ConnectorContext context )
    // {
    // this.connectorContext = context;
    // }

    public ConnectorConfiguration getConnectorConfiguration()
    {
        return connectorConfiguration;
    }

    public void setConnectorConfiguration( ConnectorConfiguration connectorConfiguration )
    {
        this.connectorConfiguration = connectorConfiguration;
    }

    public Graph getGraph()
    {
        return graph;
    }

    public void setGraph( Graph graph )
    {
        this.graph = graph;
    }

    public DiagramConnector getConnector()
    {
        return connector;
    }

    public void setConnector( DiagramConnector connector )
    {
        this.connector = connector;
    }

}
