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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.maven.diagrams.gui.renderers.RendererConfigurationItem;
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationItemImpl;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class UmlClassRendererConfigurationItemPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JCheckBox visible;

    private JCheckBox full_qualified_class_names;

    private String name;

    public UmlClassRendererConfigurationItemPanel()
    {
    }

    public void setCurrentRendererConfigurationItem( RendererConfigurationItem configuration )
    {
        rebuildUI( configuration );
    }

    private void rebuildUI( RendererConfigurationItem configuration )
    {
        this.removeAll();
        this.setBorder( new TitledBorder( configuration.getName() ) );
        this.setLayout( new GridBagLayout() );
        GridBagConstraints c =
            new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                                    new Insets( 2, 2, 2, 2 ), 0, 0 );

        name = configuration.getName();

        visible = new JCheckBox( "Show section" );
        visible.setSelected( configuration.isVisible() );
        this.add( visible, c );

        if ( UmlClassRendererConfigurationItem.class.isInstance( configuration ) )
        {
            full_qualified_class_names = new JCheckBox( "Use full qualified class names" );
            full_qualified_class_names.setSelected( ( (UmlClassRendererConfigurationItem) configuration ).getFull_class_names() );
            c.gridy++;
            this.add( full_qualified_class_names, c );

        }
        else
        {
            full_qualified_class_names = null;
        }

    }

    public RendererConfigurationItem getCurrentRendererConfiguration()
    {
        return full_qualified_class_names != null ? new UmlClassRendererConfigurationItem(
                                                                                           name,
                                                                                           visible.isSelected(),
                                                                                           full_qualified_class_names.isSelected() )
                        : new RendererConfigurationItemImpl( name, visible.isSelected() );
    }

}
