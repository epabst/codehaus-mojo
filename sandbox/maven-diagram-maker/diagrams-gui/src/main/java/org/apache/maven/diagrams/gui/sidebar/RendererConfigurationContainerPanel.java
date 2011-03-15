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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.maven.diagrams.gui.controller.LayoutController;
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationPanel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class RendererConfigurationContainerPanel extends JPanel implements ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -6689108990495767294L;

    private static final String ACTION_APPLY = "APPLY";

    private JButton applyButton;

    private RendererConfigurationPanel rendererConfigurationPanel;

    private LayoutController controller;

    public RendererConfigurationContainerPanel( LayoutController a_controller )
    {
        controller = a_controller;
        rebuildUI();
    }

    public void setRendererConfigurationPanel( RendererConfigurationPanel rendererConfigurationPanel )
    {
        this.rendererConfigurationPanel = rendererConfigurationPanel;
        removeAll();
        rebuildUI();
    }

    protected void rebuildUI()
    {
        GridBagLayout gbl = new GridBagLayout();
        setLayout( gbl );
        if ( rendererConfigurationPanel != null )
            add( new JScrollPane( rendererConfigurationPanel ), new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0,
                                                                                        GridBagConstraints.CENTER,
                                                                                        GridBagConstraints.BOTH,
                                                                                        new Insets( 2, 2, 2, 2 ), 0, 0 ) );
        applyButton = new JButton( "Apply" );
        applyButton.setActionCommand( ACTION_APPLY );
        applyButton.addActionListener( this );
        add( applyButton, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.NONE, new Insets( 2, 2, 2, 2 ), 0, 0 ) );
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getActionCommand().equals( ACTION_APPLY ) )
        {
            controller.updateRendererConfiguration( rendererConfigurationPanel.getCurrentRendererConfiguration() );
        }
    }

    public void setController( LayoutController controller )
    {
        this.controller = controller;
    }

}
