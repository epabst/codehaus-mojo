package org.apache.maven.diagrams.gui.connector.classes;

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
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.apache.maven.diagrams.connectors.classes.config.ExcludeClasses;
import org.apache.maven.diagrams.gui.swing_helpers.ObjectEdititingPanel;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ExcludeClassesEditingPanel extends ObjectEdititingPanel<ExcludeClasses>
{
    /**
     * 
     */
    private static final long serialVersionUID = 4081543812180184125L;

    private JTextField textField;

    private JCheckBox keepEdges;

    public ExcludeClassesEditingPanel()
    {
        textField = new JTextField();
        setLayout( new GridLayout( 2, 1 ) );
        add( textField );
        keepEdges = new JCheckBox( "Keep edges" );
        add( keepEdges );
    }

    @Override
    public ExcludeClasses getObject()
    {
        return new ExcludeClasses( textField.getText(), keepEdges.isSelected() );
    }

    @Override
    public void setObject( ExcludeClasses state )
    {
        keepEdges.setSelected( state.getKeepEdges() );
        textField.setText( state.getPattern() );
    }

}
