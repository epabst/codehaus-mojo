package org.apache.maven.diagrams.gui.controller;

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
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.DiagramConnector;
import org.apache.maven.diagrams.graph_api.Graph;
import org.apache.maven.diagrams.gui.MainWindow;
import org.apache.maven.diagrams.gui.bindings.connectors.BindingsManager;
import org.apache.maven.diagrams.gui.bindings.connectors.ResolvedBinding;
import org.apache.maven.diagrams.gui.connector.AbstractConnectorConfigurationPanel;
import org.apache.maven.diagrams.gui.graph_adapter.GraphToPrefuseGraph;
import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfiguration;
import org.apache.maven.diagrams.gui.model.Model;
import org.apache.maven.diagrams.gui.renderers.ConfigurableRenderer;
import org.apache.maven.diagrams.gui.renderers.RendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationPanel;
import org.apache.maven.diagrams.gui.sidebar.ConnectorConfigurationContainerPanel;
import org.apache.maven.diagrams.gui.sidebar.LayoutConfigurationContainerPanel;
import org.apache.maven.diagrams.gui.sidebar.RendererConfigurationContainerPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.TreeLayout;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import sun.security.action.GetLongAction;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class MainController
{
    private Model model;

    private MainWindow view;

    private BindingsManager bindingsManager;

    private LayoutController layoutController;

    private ConfigurableRenderer renderer;

    public MainController() throws DiagramGuiException
    {
        model = createModel( null );
        layoutController = new LayoutController( this );
        view = new MainWindow( this );
    }

    public MainController( DiagramConnector connector ) throws DiagramGuiException
    {
        model = createModel( connector );
        layoutController = new LayoutController( this );
        view = new MainWindow( this );
    }

    public Model getModel()
    {
        return model;
    }

    public void setModel( Model model )
    {
        this.model = model;
    }

    public MainWindow getView()
    {
        return view;
    }

    public void setView( MainWindow view )
    {
        this.view = view;
    }

    public BindingsManager getBindingsManager()
    {
        if ( bindingsManager == null )
        {
            bindingsManager = new BindingsManager();
            InputStream is = this.getClass().getResourceAsStream( "/diagrams-gui-bindings.xml" );
            if ( is == null )
            {
                // TODO: Logger
                return null;
            }
            bindingsManager.loadAndResolveBindings( is );
        }
        return bindingsManager;
    }

    public void setBindingsManager( BindingsManager bindingsManager )
    {
        this.bindingsManager = bindingsManager;
    }

    @SuppressWarnings( "unchecked" )
    public List<JPanel> calculatePanelsForSideBar()
    {

        List<JPanel> result = new LinkedList<JPanel>();
        if ( model.getConnector() != null )
        {
            ResolvedBinding<DiagramConnector> binding =
                getBindingsManager().getBindingsForConnector( model.getConnector() );
            try
            {
                AbstractConnectorConfigurationPanel p = binding.getConnectorConfigurationPanelClass().newInstance();
                p.setCurrentConfiguration( model.getConnectorConfiguration() );
                ConnectorConfigurationContainerPanel cccp = new ConnectorConfigurationContainerPanel( this, p );
                cccp.setName( "Connector" );
                result.add( cccp );

                if ( ( binding.getRendererConfigurationPanel() != null )
                                || ( binding.getRendererConfiguration() != null ) )
                {
                    RendererConfigurationContainerPanel rccp =
                        new RendererConfigurationContainerPanel( this.layoutController );
                    rccp.setName( "Node apperance" );
                    RendererConfigurationPanel rcp =
                        (RendererConfigurationPanel) ( (Class) binding.getRendererConfigurationPanel() ).newInstance();
                    RendererConfiguration rendererConfiguration = binding.getRendererConfiguration().newInstance();
                    rcp.setCurrentRendererConfiguration( rendererConfiguration );
                    rccp.setRendererConfigurationPanel( rcp );
                    result.add( rccp );
                }

            }
            catch ( InstantiationException e )
            {
                // TODO: Logger
                e.printStackTrace();
            }
            catch ( IllegalAccessException e )
            {
                // TODO: Logger
                e.printStackTrace();
            }
        }

        LayoutConfigurationContainerPanel lccp = new LayoutConfigurationContainerPanel( this.layoutController );
        lccp.setName( "Layout" );
        result.add( lccp );

        return result;
    }

    // ============================================================

    protected Model createModel( DiagramConnector connector ) throws DiagramGuiException
    {
        Model model = new Model();
        // model.setConnectorContext( connectorContext );

        if ( connector != null )
        {
            // ResolvedBinding<DiagramConnector> binding = getBindingsManager().getBindingsForGraphType( graphType );
            // if ( binding != null )
            // {
            try
            {
                // DiagramConnector connecto = binding.getConnectorClass().newInstance();

                ConnectorConfiguration configuration =
                    connector.getConnectorDescriptor().getConfigurationClass().newInstance();

                model.setConnector( connector );
                model.setConnectorConfiguration( configuration );
            }
            catch ( ConnectorException e )
            {
                throw new DiagramGuiException( "Cannot instante connector for connector type: "
                                + connector.getClass().getName(), e );
            }
            catch ( InstantiationException e )
            {
                throw new DiagramGuiException( "Cannot instante connector for connector type: "
                                + connector.getClass().getName(), e );
            }
            catch ( IllegalAccessException e )
            {
                throw new DiagramGuiException( "Cannot instante connector for connector type: "
                                + connector.getClass().getName(), e );
            }
        }
        // else
        // {
        // throw new DiagramGuiException( "Binding for graph type: " + conne + " has not been found" );
        // }
        // }
        return model;
    }

    // ==============================================================

    public void updateConnectorConfiguration( ConnectorConfiguration new_connector_configuration, boolean refresh )
    {
        model.setConnectorConfiguration( new_connector_configuration );
        if ( refresh )
            refreshGraph();
    }

    public void refreshGraph()
    {
        // model.getConnector().setConnectorContext( model.getConnectorContext() );
        try
        {
            Graph g = model.getConnector().calculateGraph( model.getConnectorConfiguration() );
            prefuse.data.Graph prefuse_graph = GraphToPrefuseGraph.graph2PrefuseGraph( g );

            if ( !Display.class.isInstance( view.getDiagramPanel() ) )
            {
                initializeDisplay( prefuse_graph );
            }
            else
            {
                getVisualization().removeGroup( "graph" );
                getVisualization().add( "graph", prefuse_graph );

                Layout currentLayout = layoutController.getCurrentLayout();
                if ( TreeLayout.class.isInstance( currentLayout ) )
                {
                    layoutController.updateRootOnTreeLayout( getVisualization(), (TreeLayout) currentLayout );
                }

                getVisualization().run( "color" );
                getVisualization().run( "layout" );
                getVisualization().putAction( "refresh", new RepaintAction() );
            }

        }
        catch ( ConnectorException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ConfigurableRenderer getRenderer()
    {
        if ( renderer == null )
        {
            ResolvedBinding<DiagramConnector> binding =
                getBindingsManager().getBindingsForConnector( model.getConnector() );
            try
            {
                renderer = binding.getRenderer().newInstance();
                renderer.setConfiguration( binding.getRendererConfiguration().newInstance() );
            }
            catch ( InstantiationException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            catch ( IllegalAccessException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

        }
        return renderer;
    }

    public Visualization getVisualization()
    {
        if ( ( view != null ) && ( view.getDiagramPanel() != null ) )
            return ( (Display) view.getDiagramPanel() ).getVisualization();
        else
            return null;
    }

    // ==============================================================

    private void initializeDisplay( prefuse.data.Graph prefuse_graph )
    {
        Display d = prepareDisplay();

        view.setDiagramPanel( d );

        configureDisplay( d, prefuse_graph, "Tree", null, getRenderer() );

    }

    public Display prepareDisplay()
    {
        Display d = new Display( new Visualization() );
        d.setSize( 720, 500 ); // set display size

        DragControl dc = new DragControl();
        dc.setFixPositionOnMouseOver( false ); // FixPositionOnMouseOver is realized by FixNodeControl()
        d.addControlListener( dc );

        // pan with left-click drag on background
        d.addControlListener( new PanControl() );

        // zoom with right-click drag
        d.addControlListener( new ZoomControl() );
        d.addControlListener( new ZoomToFitControl() );
        d.addControlListener( new FixNodeControl() );

        return d;
    }

    public Display configureDisplay( Display d, prefuse.data.Graph graph, String layoutName,
                                     AbstractLayoutConfiguration<?> configuration, Renderer r )
    {
        d.getVisualization().add( "graph", graph );
        d.getVisualization().setInteractive( "graph.edges", null, false );

        DefaultRendererFactory drf = new DefaultRendererFactory( r );
        drf.setDefaultRenderer( r );
        d.getVisualization().setRendererFactory( drf );

        // / int[] palette = new int[] { ColorLib.rgb( 255, 255, 255 ), ColorLib.rgb( 190, 190, 255 ) };
        // // map nominal data values to colors using our provided palette
        ColorAction fill = new ColorAction( "graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb( 255, 255, 255 ) );
        // new DataColorAction( "graph.nodes", "interface", Constants.NOMINAL, VisualItem.FILLCOLOR, palette );

        FontAction font = new FontAction( "graph.nodes", FontLib.getFont( "Arial", 4 ) );

        // use light grey for edges
        ColorAction edges = new ColorAction( "graph.edges", VisualItem.STROKECOLOR, ColorLib.gray( 200 ) );
        ColorAction edges0 = new ColorAction( "graph.edges", VisualItem.FILLCOLOR, ColorLib.gray( 200 ) );

        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add( fill );
        color.add( edges );
        color.add( edges0 );
        color.add( font );

        d.getVisualization().putAction( "color", color );

        // -- 5. the display and interactive controls -------------------------

        // assign the colors
        d.getVisualization().run( "color" );

        layoutController.setCurrentLayout( layoutName, configuration );

        d.getVisualization().putAction( "refresh", new RepaintAction() );

        return d;
    }

    // ==============================================================
    public void run()
    {
        view.run();
        if ( model.getConnectorConfiguration() != null && model.getConnector() != null )
            refreshGraph();
    }

}
