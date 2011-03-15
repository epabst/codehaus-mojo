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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.gui.connector.AbstractConnectorConfigurationPanel;
import org.apache.maven.diagrams.gui.controller.MainController;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ConnectorConfigurationContainerPanel extends JPanel implements ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -7589049137585188595L;

    private static final String APPLY = "APPLY";

    private AbstractConnectorConfigurationPanel<? extends ConnectorConfiguration> panel;

    private JButton applyButton;

    private MainController controller;

    public ConnectorConfigurationContainerPanel(
                                                 MainController a_controller,
                                                 AbstractConnectorConfigurationPanel<? extends ConnectorConfiguration> a_panel )
    {
        panel = a_panel;
        controller = a_controller;

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.rowWeights = new double[] { 1.0, 0.0 };
        this.setLayout( gridBagLayout );

        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        this.add( new JScrollPane( panel ), c );

        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.weighty = 0.0;
        applyButton = new JButton( "Apply" );
        applyButton.addActionListener( this );
        applyButton.setActionCommand( APPLY );

        this.add( applyButton, c );
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getActionCommand().equals( APPLY ) )
        {
            controller.updateConnectorConfiguration( panel.getCurrentConfiguration(), true );
            // try
            // {
            // model.getConnector().setConnectorContext( model.getConnectorContext());

            // model.getConnector().calculateGraph( model.getConnectorConfiguration() );
            // }
            // catch ( ConnectorException e1 )
            // {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }
        }
    }

}
