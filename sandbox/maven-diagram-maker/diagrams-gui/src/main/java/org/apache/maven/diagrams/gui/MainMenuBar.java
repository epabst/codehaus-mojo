package org.apache.maven.diagrams.gui;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.maven.diagrams.gui.controller.MainController;

import prefuse.util.display.ExportDisplayAction;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class MainMenuBar extends JMenuBar
{
    /**
     * 
     */
    private static final long serialVersionUID = -4770551478277405323L;

    private JMenu file;

    private MainController controller;

    public MainMenuBar( MainController a_controller )
    {
        controller = a_controller;

        /*----------------- FILE -------------------- */
        file = new JMenu( "File" );
        file.setMnemonic( 'f' );

        JMenuItem item = new JMenuItem( "New diagram..." );
        file.add( item );

        file.addSeparator();
        file.add( new JMenuItem( "Save view configuration..." ) );
        file.add( new JMenuItem( "Restore view configuration..." ) );
        file.addSeparator();

        item = new JMenuItem( "Export to graphical file..." );
        item.setMnemonic( 'x' );

        item.addActionListener( new ExportToGraphicalFileAction() );

        file.add( item );

        file.addSeparator();

        item = new JMenuItem( "Exit" );
        item.setMnemonic( 'e' );
        item.addActionListener( new ActionListener()
        {
            public void actionPerformed( java.awt.event.ActionEvent e )
            {
                System.exit( 0 );
            };
        } );
        file.add( item );

        this.add( file );
    }

    private class ExportToGraphicalFileAction implements ActionListener
    {
        public void actionPerformed( ActionEvent e )
        {
            ExportDisplayAction exportDisplayAction =
                new ExportDisplayAction( controller.getVisualization().getDisplay( 0 ) );
            exportDisplayAction.actionPerformed( e );
        }
    }
}
