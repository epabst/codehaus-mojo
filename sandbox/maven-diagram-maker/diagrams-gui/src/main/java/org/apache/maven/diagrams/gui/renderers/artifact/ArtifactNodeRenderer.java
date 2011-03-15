package org.apache.maven.diagrams.gui.renderers.artifact;

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

import org.apache.maven.diagrams.connectors.dependencies.graph.ArtifactNode;
import org.apache.maven.diagrams.gui.renderers.RendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.umlClass.renderer.ListRenderer;
import org.apache.maven.diagrams.gui.renderers.umlClass.renderer.RendererListItem;
import org.apache.maven.diagrams.gui.renderers.umlClass.renderer.TextItem;

import prefuse.visual.VisualItem;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class ArtifactNodeRenderer extends ListRenderer
{
    public ArtifactNodeRenderer()
    {
        super();
    }

    @SuppressWarnings( "unchecked" )
    protected List<RendererListItem> getList( VisualItem vi )
    {
        List<RendererListItem> list;
        // = (List<RendererListItem>) vi.get( "vcache" );
        // if ( list == null )

        list = new LinkedList<RendererListItem>();

        ArtifactNode node = (ArtifactNode) vi.get( "node" );

        list.add( new TextItem( node.artifact.getDependencyConflictId(), true, false, true ) );

        return list;
    }

    @Override
    public void setConfiguration( RendererConfiguration newRendererConfiguration )
    {
    }
}