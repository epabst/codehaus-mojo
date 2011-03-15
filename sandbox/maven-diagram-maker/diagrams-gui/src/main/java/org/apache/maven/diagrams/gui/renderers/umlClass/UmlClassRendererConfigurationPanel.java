package org.apache.maven.diagrams.gui.renderers.umlClass;

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
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.diagrams.gui.renderers.RendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationItem;
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationPanel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class UmlClassRendererConfigurationPanel extends RendererConfigurationPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 3985633179077245840L;

    private List<UmlClassRendererConfigurationItemPanel> items;

    public UmlClassRendererConfigurationPanel()
    {
    }

    @Override
    public void setCurrentRendererConfiguration( RendererConfiguration configuration )
    {
        this.removeAll();
        rebuildUI( configuration );

    }

    private void rebuildUI( RendererConfiguration configuration )
    {
        setLayout( new GridBagLayout() );
        GridBagConstraints c =
            new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                                    new Insets( 2, 2, 2, 2 ), 0, 0 );

        items = new LinkedList<UmlClassRendererConfigurationItemPanel>();

        for ( RendererConfigurationItem item : configuration.getRenderConfigurationItems().values() )
        {
            UmlClassRendererConfigurationItemPanel itemPanel = new UmlClassRendererConfigurationItemPanel();
            itemPanel.setCurrentRendererConfigurationItem( item );
            items.add( itemPanel );
            add( itemPanel, c );
            c.gridy++;
        }
    }

    @Override
    public RendererConfiguration getCurrentRendererConfiguration()
    {
        UmlClassRendererConfiguration ucrc = new UmlClassRendererConfiguration();
        ucrc.getRenderConfigurationItems().clear();
        for ( UmlClassRendererConfigurationItemPanel itemPanel : items )
        {
            RendererConfigurationItem item = itemPanel.getCurrentRendererConfiguration();
            ucrc.getRenderConfigurationItems().put( item.getName(), item );
        }
        return ucrc;
    }

}
