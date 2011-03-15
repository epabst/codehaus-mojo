package org.apache.maven.diagrams.gui.connector.dependencies;

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
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import org.apache.maven.diagrams.connectors.dependencies.config.DependenciesConnectorConfiguration;
import org.apache.maven.diagrams.gui.connector.AbstractConnectorConfigurationPanel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class DependenciesConnectorConfigurationPanel
    extends AbstractConnectorConfigurationPanel<DependenciesConnectorConfiguration>
{
    // /* data */
    private DependenciesConnectorConfiguration conf;

    //
    // private JCheckBox fullInheritancePathCheckBox;
    //
    // /* edge type controllers */
    // private JPanel edgeTypesPanel;
    //
    // private JCheckBox aggregateEdgesCheckBox;
    //
    // private JCheckBox inheritanceEdgesCheckBox;
    //
    // private JCheckBox implementEdgesCheckBox;
    //
    // /* nodes details controllers */
    // private JPanel nodesPanel;
    //
    // private JCheckBox compressJavaBeanProperties;
    //
    // private JCheckBox propagateInheritedMethods;
    //
    // private JCheckBox propagateInheritedFields;

    /**
     * 
     */
    private static final long serialVersionUID = -6691930260152705247L;

    public DependenciesConnectorConfigurationPanel()
    {
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.setPreferredSize( new Dimension( 200, 500 ) );
        gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
        this.setLayout( gridBagLayout );
        // GridBagConstraints g = new GridBagConstraints();

        // g.anchor = GridBagConstraints.NORTHWEST;
        // g.gridx = 0;
        // g.gridy = 0;
        // g.fill = GridBagConstraints.BOTH;
        // includedPanel =
        // new OrderedStringListPanel<IncludeClasses>( null, new IncludeClassesEditingPanel(),
        // new ObjectToStringConverter<IncludeClasses>()
        // {
        // public String convert( IncludeClasses object )
        // {
        // return object.getPattern();
        // }
        // }, "Includes: " );
        // this.add( includedPanel, g );
        //
        // g.gridy++;
        // excludedPanel =
        // new OrderedStringListPanel<ExcludeClasses>( null, new ExcludeClassesEditingPanel(),
        // new ObjectToStringConverter<ExcludeClasses>()
        // {
        // public String convert( ExcludeClasses object )
        // {
        // return object.getPattern()
        // + ( object.getKeepEdges() ? " [keep edges]"
        // : "" );
        // }
        //
        // }, "Excludes: " );
        // this.add( excludedPanel, g );

        // g.gridy++;
        // fullInheritancePathCheckBox = new JCheckBox( "Full inheritance path" );
        // this.add( fullInheritancePathCheckBox, g );
        //
        // // ----------------------- EDGE TYPES panel --------------------------------
        // aggregateEdgesCheckBox = new JCheckBox( "Add class aggregation edges" );
        // inheritanceEdgesCheckBox = new JCheckBox( "Add class inheritence edges" );
        // implementEdgesCheckBox = new JCheckBox( "Add interface implement edges" );
        //
        // edgeTypesPanel = new JPanel();
        // edgeTypesPanel.setBorder( new TitledBorder( "Edges" ) );
        // edgeTypesPanel.setLayout( new GridLayout( 3, 1 ) );
        // edgeTypesPanel.add( aggregateEdgesCheckBox );
        // edgeTypesPanel.add( inheritanceEdgesCheckBox );
        // edgeTypesPanel.add( implementEdgesCheckBox );
        //
        // g.gridy++;
        // this.add( edgeTypesPanel, g );
        // // ----------------------- Nodes types panel -------------------------------
        //
        // compressJavaBeanProperties = new JCheckBox( "Compress Java Bean properties" );
        // propagateInheritedMethods = new JCheckBox( "Propagate inherited methods" );
        // propagateInheritedFields = new JCheckBox( "Propagate inherited fields" );
        //
        // nodesPanel = new JPanel();
        // nodesPanel.setBorder( new TitledBorder( "Nodes" ) );
        // nodesPanel.setLayout( new GridLayout( 3, 1 ) );
        // nodesPanel.add( compressJavaBeanProperties );
        // nodesPanel.add( propagateInheritedFields );
        // nodesPanel.add( propagateInheritedMethods );
        // g.gridy++;
        // this.add( nodesPanel, g );
    }

    public void setCurrentConfiguration( DependenciesConnectorConfiguration classesConnectorConfigurationPanel )
    {
        conf = classesConnectorConfigurationPanel;

        // fullInheritancePathCheckBox.setSelected( conf.getFullInheritancePaths() );
        //
        // aggregateEdgesCheckBox.setSelected( isOneInstanceOf( AggregateEdgeType.class, conf.getEdges() ) );
        // inheritanceEdgesCheckBox.setSelected( isOneInstanceOf( InheritanceEdgeType.class, conf.getEdges() ) );
        // implementEdgesCheckBox.setSelected( isOneInstanceOf( ImplementEdgeType.class, conf.getEdges() ) );
        //
        // compressJavaBeanProperties.setSelected( conf.getNodes().getCompressJavaBeanProperties() );
        // propagateInheritedMethods.setSelected( conf.getNodes().getPropagateInheritedMethods() );
        // propagateInheritedFields.setSelected( conf.getNodes().getPropagateInheritedFields() );
    }

    public DependenciesConnectorConfiguration getCurrentConfiguration()
    {
        updateConfState();
        return conf;
    }

    private void updateConfState()
    {
        // conf.setFullInheritancePaths( fullInheritancePathCheckBox.isSelected() );
        //
        // conf.getEdges().clear();
        // if ( aggregateEdgesCheckBox.isSelected() )
        // conf.getEdges().add( new AggregateEdgeType() );
        // if ( inheritanceEdgesCheckBox.isSelected() )
        // conf.getEdges().add( new InheritanceEdgeType() );
        // if ( implementEdgesCheckBox.isSelected() )
        // conf.getEdges().add( new ImplementEdgeType() );
        //
        // conf.getNodes().setCompressJavaBeanProperties( compressJavaBeanProperties.isSelected() );
        // conf.getNodes().setPropagateInheritedMethods( propagateInheritedMethods.isSelected() );
        // conf.getNodes().setPropagateInheritedFields( propagateInheritedFields.isSelected() );
    }

    // private boolean isOneInstanceOf( Class<? extends Object> clas,
    // Collection<? extends Object> edges )
    // {
    // for ( Object o : edges )
    // {
    // if ( clas.isInstance( o ) )
    // return true;
    // }
    // return false;
    // }

    /* TODO: For testing purposes - remove later */

    public static void main( String[] args )
    {
        DependenciesConnectorConfigurationPanel c = new DependenciesConnectorConfigurationPanel();
        c.setCurrentConfiguration( new DependenciesConnectorConfiguration() );
        c.run();
    }

    private void createAndShowGUI()
    {

        // Create and set up the window.
        JFrame frame = new JFrame( "Maven diagram GUI" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.getContentPane().add( this );
        frame.setPreferredSize( new Dimension( 300, 300 ) );

        // Display the window.
        frame.pack();
        frame.setVisible( true );
    }

    public void run()
    {
        javax.swing.SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        } );
    }
}
