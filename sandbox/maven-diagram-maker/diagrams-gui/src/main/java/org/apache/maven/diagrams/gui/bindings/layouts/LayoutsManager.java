package org.apache.maven.diagrams.gui.bindings.layouts;

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

import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfiguration;
import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfigurationPanel;
import org.apache.maven.diagrams.gui.layouts.forces.ForcesLayoutConfiguration;
import org.apache.maven.diagrams.gui.layouts.forces.ForcesLayoutConfigurationPanel;
import org.apache.maven.diagrams.gui.layouts.node_link.NodeLinkTreeLayoutConfiguration;
import org.apache.maven.diagrams.gui.layouts.node_link.NodeLinkTreeLayoutConfigurationPanel;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;

// TODO: Move to bindings file or plexus components
/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class LayoutsManager
{
    private List<LayoutBinding<? extends AbstractLayoutConfiguration>> layoutBindings;

    public LayoutsManager()
    {
        layoutBindings = new LinkedList<LayoutBinding<? extends AbstractLayoutConfiguration>>();

        LayoutBinding<ForcesLayoutConfiguration> binding1 = new LayoutBinding<ForcesLayoutConfiguration>();
        binding1.setName( "Simulated forces layout" );
        binding1.setDescription( "[to be done]" );// TODO:
        binding1.setLayoutClass( ForceDirectedLayout.class );
        binding1.setConfigurationClass( ForcesLayoutConfiguration.class );
        binding1.setEditingPanelClass( ForcesLayoutConfigurationPanel.class );
        layoutBindings.add( binding1 );

        LayoutBinding<NodeLinkTreeLayoutConfiguration> binding2 = new LayoutBinding<NodeLinkTreeLayoutConfiguration>();
        binding2.setName( "Tree" );
        binding2.setDescription( "[to be done]" );// TODO:
        binding2.setLayoutClass( NodeLinkTreeLayout.class );
        binding2.setConfigurationClass( NodeLinkTreeLayoutConfiguration.class );
        binding2.setEditingPanelClass( NodeLinkTreeLayoutConfigurationPanel.class );
        layoutBindings.add( binding2 );
    }

    @SuppressWarnings( "unchecked" )
    public List<LayoutBinding> getLayoutBindings()
    {
        return (List<LayoutBinding>) ( (List) layoutBindings );
    }

    @SuppressWarnings( "unchecked" )
    public LayoutBinding getBindingForName( String layoutName )
    {
        for ( LayoutBinding<? extends AbstractLayoutConfiguration> binding : layoutBindings )
        {
            if ( binding.getName().equals( layoutName ) )
                return binding;
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public Class<? extends AbstractLayoutConfigurationPanel> getEditPanelFor( String layoutName )
    {
        LayoutBinding binding = getBindingForName( layoutName );
        if ( binding != null )
        {
            return binding.getEditingPanelClass();
        }
        return null;
    }

}
