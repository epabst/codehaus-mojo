package org.apache.maven.diagrams.gui.layouts.forces;

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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfigurationPanel;

import prefuse.util.force.ForceSimulator;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class ForcesLayoutConfigurationPanel extends AbstractLayoutConfigurationPanel<ForcesLayoutConfiguration>
    implements ChangeListener
{
    private static final long serialVersionUID = 1636416084494453583L;

    private MyJForcePanel panel;

    private ForceSimulator sim;

    public ForcesLayoutConfigurationPanel()
    {
        sim = new ForceSimulator();
        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWeights = new double[] { 1.0 };
        this.setLayout( gbl );
        createJForcePanel();
        this.add( panel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL, new Insets( 2, 2, 2, 2 ), 0, 0 ) );
    }

    private void createJForcePanel()
    {
        panel = new MyJForcePanel( sim );
        panel.setChangeListener( this );
    }

    @Override
    public ForcesLayoutConfiguration getCurrentConfiguration()
    {
        ForcesLayoutConfiguration config = new ForcesLayoutConfiguration();
        config.setInstanceOfForceSimulator( sim );
        return config;
    }

    @Override
    public void setCurrentConfiguration( ForcesLayoutConfiguration configuration )
    {
        sim = configuration.getInstanceOfForceSimulator();
        this.removeAll();
        createJForcePanel();
        this.add( panel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL, new Insets( 2, 2, 2, 2 ), 0, 0 ) );
    }

    public void stateChanged( ChangeEvent e )
    {
        if ( isAutoApply() )
            apply();
    }

}
