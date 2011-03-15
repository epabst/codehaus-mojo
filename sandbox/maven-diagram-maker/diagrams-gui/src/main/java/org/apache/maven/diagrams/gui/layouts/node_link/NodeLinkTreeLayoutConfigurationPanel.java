package org.apache.maven.diagrams.gui.layouts.node_link;

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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfigurationPanel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class NodeLinkTreeLayoutConfigurationPanel
    extends AbstractLayoutConfigurationPanel<NodeLinkTreeLayoutConfiguration> implements ActionListener, ChangeListener
{
    private JComboBox orientationList;

    private JSlider bspace, tspace, dspace;

    private static final long serialVersionUID = 1636416084494453583L;

    public NodeLinkTreeLayoutConfigurationPanel()
    {
        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[] { 20, 0 };
        setLayout( gbl );

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        add( new JLabel( "Orientation:" ), c );

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        c.gridx = 1;
        c.gridwidth = 1;
        orientationList = new JComboBox();
        orientationList.addItem( "From left to right" );
        orientationList.addItem( "From top to down" );
        orientationList.addItem( "From right to left" );
        orientationList.addItem( "From down to top" );
        orientationList.addActionListener( this );
        add( orientationList, c );

        c.gridy += 2;
        bspace = new JSlider( 0, 300 );
        bspace.setPaintLabels( true );
        bspace.setLabelTable( bspace.createStandardLabels( 50 ) );
        bspace.addChangeListener( this );
        add( bspace, c );

        c.gridy += 2;
        tspace = new JSlider( 0, 300 );
        tspace.setPaintLabels( true );
        tspace.setLabelTable( tspace.createStandardLabels( 50 ) );
        tspace.addChangeListener( this );
        add( tspace, c );

        c.gridy += 2;
        dspace = new JSlider( 0, 300 );
        dspace.setPaintLabels( true );
        dspace.setLabelTable( dspace.createStandardLabels( 50 ) );
        dspace.addChangeListener( this );
        add( dspace, c );

        // labels
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        add( new JLabel( "Spacing between sibling nodes:" ), c );

        c.gridy += 2;
        add( new JLabel( "Spacing between subtrees:" ), c );

        c.gridy += 2;
        add( new JLabel( "Spacing between depth levels:" ), c );

    }

    @Override
    public NodeLinkTreeLayoutConfiguration getCurrentConfiguration()
    {
        NodeLinkTreeLayoutConfiguration config = new NodeLinkTreeLayoutConfiguration();

        switch ( orientationList.getSelectedIndex() )
        {
            case 0:
                config.setOrientation( NodeLinkTreeLayoutConfiguration.ORIENT_LEFT_RIGHT );
                break;
            case 1:
                config.setOrientation( NodeLinkTreeLayoutConfiguration.ORIENT_TOP_BOTTOM );
                break;
            case 2:
                config.setOrientation( NodeLinkTreeLayoutConfiguration.ORIENT_RIGHT_LEFT );
                break;
            case 3:
                config.setOrientation( NodeLinkTreeLayoutConfiguration.ORIENT_BOTTOM_TOP );
                break;
            default:
                assert false;
        }

        config.setBspace( bspace.getValue() );
        config.setDspace( dspace.getValue() );
        config.setTspace( tspace.getValue() );

        return config;
    }

    @Override
    public void setCurrentConfiguration( NodeLinkTreeLayoutConfiguration configuration )
    {
        switch ( configuration.getOrientation() )
        {
            case NodeLinkTreeLayoutConfiguration.ORIENT_LEFT_RIGHT:
                orientationList.setSelectedIndex( 0 );
                break;
            case NodeLinkTreeLayoutConfiguration.ORIENT_TOP_BOTTOM:
                orientationList.setSelectedIndex( 1 );
                break;
            case NodeLinkTreeLayoutConfiguration.ORIENT_RIGHT_LEFT:
                orientationList.setSelectedIndex( 2 );
                break;
            case NodeLinkTreeLayoutConfiguration.ORIENT_BOTTOM_TOP:
                orientationList.setSelectedIndex( 3 );
                break;
            default:
                assert false;
        }

        bspace.setValue( (int) configuration.getBspace() );
        dspace.setValue( (int) configuration.getDspace() );
        tspace.setValue( (int) configuration.getTspace() );
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( isAutoApply() )
            apply();
    }

    public void stateChanged( ChangeEvent e )
    {
        if ( isAutoApply() )
            apply();
    }

}
