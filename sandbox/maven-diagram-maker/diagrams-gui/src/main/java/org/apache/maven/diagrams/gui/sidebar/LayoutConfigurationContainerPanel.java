package org.apache.maven.diagrams.gui.sidebar;

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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.apache.maven.diagrams.gui.bindings.layouts.LayoutBinding;
import org.apache.maven.diagrams.gui.controller.LayoutController;
import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfiguration;
import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfigurationPanel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class LayoutConfigurationContainerPanel extends JPanel implements ActionListener, ItemListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -7589049137585188595L;

    private static final String ACTION_APPLY = "APPLY";

    private static final String ACTION_AUTOMATIC_APPLY = "AUTOAPPLY";

    private static final String ACTION_LAYOUT_SELECTED = "LAYOUT_SELECTED";

    private static final String ACTION_CHANGE_LAYOUT = "CHANGE_LAYOUT";

    private static final String ACTION_ACTIVATE = "ACTION_ACTIVATE";

    private AbstractLayoutConfigurationPanel innerPanel;

    private JScrollPane outerPanel;

    private JButton applyButton;

    private JComboBox layoutsList;

    private JToggleButton activeLayout;

    private JToggleButton autoApply;

    private JButton changeLayoutButton;

    private LayoutController controller;

    public LayoutConfigurationContainerPanel( LayoutController a_controller )
    {
        controller = a_controller;

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 5, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.5, 0.5 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
        gridBagLayout.rowHeights = new int[] { 40, 40, 0, 40, 40 };
        this.setLayout( gridBagLayout );
        GridBagConstraints c = new GridBagConstraints();

        // c.ipadx=20;
        c.insets = new Insets( 5, 5, 5, 5 );

        c.gridheight = 1;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;

        activeLayout = new JToggleButton( "Active layout" );
        activeLayout.addActionListener( this );
        activeLayout.setActionCommand( ACTION_ACTIVATE );
        add( activeLayout, c );

        layoutsList = new JComboBox();
        layoutsList.addItemListener( this );
        // layoutsList.setActionCommand( ACTION_LAYOUT_SELECTED );
        c.gridy++;
        c.insets = new Insets( 5, 5, 0, 5 );
        c.anchor = GridBagConstraints.SOUTHWEST;
        add( layoutsList, c );

        changeLayoutButton = new JButton( "Change" );
        changeLayoutButton.addActionListener( this );
        changeLayoutButton.setActionCommand( ACTION_CHANGE_LAYOUT );
        c.insets = new Insets( 5, 10, 0, 5 );
        c.gridx = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        add( changeLayoutButton, c );

        c.insets = new Insets( 0, 5, 0, 5 );
        c.gridy++;
        c.gridwidth = 2;
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        outerPanel = new JScrollPane();
        outerPanel.setBorder( new TitledBorder( "" ) );
        if ( innerPanel != null )
            outerPanel.setViewportView( innerPanel );
        add( outerPanel, c );

        c.insets = new Insets( 0, 5, 5, 0 );
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.gridy++;
        autoApply = new JToggleButton( "Auto-apply changes" );
        autoApply.setActionCommand( ACTION_AUTOMATIC_APPLY );
        autoApply.addActionListener( this );
        add( autoApply, c );

        c.insets = new Insets( 0, 0, 5, 5 );
        c.gridwidth = 1;
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.weighty = 0.0;
        applyButton = new JButton( "Apply" );
        applyButton.addActionListener( this );
        applyButton.setActionCommand( ACTION_APPLY );

        this.add( applyButton, c );

        fillLayoutsList();
    }

    @SuppressWarnings( "unchecked" )
    public void fillLayoutsList()
    {
        List<LayoutBinding> layouts = controller.getAvailableLayouts();
        layoutsList.removeAllItems();
        for ( LayoutBinding lb : layouts )
        {
            layoutsList.addItem( lb.getName() );
        }

    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getActionCommand().equals( ACTION_APPLY ) )
        {
            innerPanel.apply();
        }

        if ( e.getActionCommand().equals( ACTION_ACTIVATE ) )
        {
            if ( activeLayout.isSelected() )
                controller.startLayoutRunning();
            else
                controller.stopLayoutRunning();
        }

        if ( e.getActionCommand().equals( ACTION_AUTOMATIC_APPLY ) )
        {
            controller.setAutoApplyMode( autoApply.isSelected() );
            if ( autoApply.isSelected() )
            {
                innerPanel.apply();
            }
        }
        if ( e.getActionCommand().equals( ACTION_LAYOUT_SELECTED ) )
        {
            updateCurrentLayoutPanel();
        }
        if ( e.getActionCommand().equals( ACTION_CHANGE_LAYOUT ) )
        {
            String selectedLayoutName = (String) layoutsList.getSelectedItem();
            controller.setCurrentLayout( selectedLayoutName, innerPanel.getCurrentConfiguration() );
            updateCurrentLayoutPanel();
        }
    }

    @SuppressWarnings( "unchecked" )
    private void updateCurrentLayoutPanel()
    {
        String selectedLayoutName = (String) layoutsList.getSelectedItem();
        innerPanel = controller.getPanelFor( selectedLayoutName );
        try
        {
            AbstractLayoutConfiguration abstractLayoutConfiguration =
                controller.provideConfigurationForLayoutNamed( selectedLayoutName );
            if ( abstractLayoutConfiguration != null )
                innerPanel.setCurrentConfiguration( abstractLayoutConfiguration );
        }
        catch ( InstantiationException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IllegalAccessException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        outerPanel.setViewportView( innerPanel );
        updateButtons();
        revalidate();
        repaint();
    }

    public void updateButtons()
    {
        String selectedLayoutName = (String) layoutsList.getSelectedItem();
        boolean isCurrentLayout = controller.isCurrentLayoutNamed( selectedLayoutName );

        autoApply.setEnabled( isCurrentLayout );
        changeLayoutButton.setEnabled( !isCurrentLayout );
        applyButton.setEnabled( isCurrentLayout );
    }

    public void itemStateChanged( ItemEvent e )
    {
        if ( e.getStateChange() == ItemEvent.SELECTED )
            updateCurrentLayoutPanel();

    }
}
