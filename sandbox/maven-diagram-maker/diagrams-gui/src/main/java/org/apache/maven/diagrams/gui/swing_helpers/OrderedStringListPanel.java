package org.apache.maven.diagrams.gui.swing_helpers;

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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class OrderedStringListPanel<Type> extends ObjectEdititingPanel<List<Type>>
    implements ActionListener, ListSelectionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -3777125063621150317L;

    static final String ACTION_ADD = "ADD";

    static final String ACTION_DEL = "DEL";

    static final String ACTION_UP = "UP";

    static final String ACTION_DOWN = "DOWN";

    static final String ACTION_UPDATE = "UPDATE";

    private String name;

    private List<Type> items;

    private JLabel title;

    private JList list;

    private ListListModel<Type> itemsListModel;

    private JButton add, remove, up, down, update;

    private ObjectEdititingPanel<Type> editPanel;

    private ObjectToStringConverter<Type> objectToStringConverter = null;

    public OrderedStringListPanel( List<Type> items, ObjectEdititingPanel<Type> a_editPanel )
    {
        this( items, a_editPanel, null, null );
    }

    public OrderedStringListPanel( List<Type> items, ObjectEdititingPanel<Type> a_editPanel,
                                   ObjectToStringConverter<Type> a_ObjectToStringConverter, String a_name )
    {
        editPanel = a_editPanel;

        name = a_name;
        this.setBorder( new TitledBorder( name ) );

        //        
        // if ( name != null )
        // title = new JLabel( name );

        add = new JButton( "add" );
        add.setActionCommand( ACTION_ADD );
        add.addActionListener( this );

        update = new JButton( "apply" );
        update.setActionCommand( ACTION_UPDATE );
        update.addActionListener( this );

        remove = new JButton( "del" );
        remove.setActionCommand( ACTION_DEL );
        remove.addActionListener( this );

        up = new JButton( "up" );
        up.setActionCommand( ACTION_UP );
        up.addActionListener( this );

        down = new JButton( "down" );
        down.setActionCommand( ACTION_DOWN );
        down.addActionListener( this );

        setItems( items );
        list = new JList( itemsListModel );
        list.addListSelectionListener( this );

        objectToStringConverter = a_ObjectToStringConverter;
        if ( objectToStringConverter != null )
        {
            ListCellRenderer defaultCellRenderer = list.getCellRenderer();
            list.setCellRenderer( new DelegateClassRenderer( defaultCellRenderer, objectToStringConverter ) );
        }

        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWeights = new double[] { 1.0, 0.0 };
        setLayout( gbl );
        GridBagConstraints c = new GridBagConstraints();

        // c.gridx = 0;
        // c.gridy = 0;
        // c.gridheight = 1;
        // c.gridwidth = 1;
        // add( title, c );

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        add( editPanel, c );

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridheight = 5;
        c.gridy = 1;
        add( new JScrollPane( list ), c );

        c.insets = new Insets( 1, 1, 2, 2 );
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridx = 1;

        c.gridy = 0;
        add( update, c );

        c.gridy = 1;
        add( add, c );

        c.gridy = 3;
        add( remove, c );

        c.gridy = 4;
        add( up, c );

        c.gridy = 5;
        add( down, c );
        updateButtons();
    }

    public List<Type> getItems()
    {
        return items;
    }

    public void setItems( List<Type> items )
    {
        this.items = items;
        itemsListModel = new ListListModel<Type>( items );
        if ( list != null )
            list.setModel( itemsListModel );
        updateButtons();
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getActionCommand().equals( ACTION_ADD ) )
        {
            Type new_value = editPanel.getObject();
            if ( new_value != null )
            {
                items.add( new_value );
                itemsListModel.notify( new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED, items.size() - 1,
                                                          items.size() - 1 ) );
                list.setSelectedIndex( items.size() - 1 );
            }
        }

        if ( e.getActionCommand().equals( ACTION_UPDATE ) )
        {
            int index = list.getSelectedIndex();
            if ( index >= 0 )
            {
                Type new_value = editPanel.getObject();
                if ( new_value != null )
                {
                    items.set( index, new_value );
                    itemsListModel.notify( new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED, index, index ) );
                }
            }
        }

        if ( e.getActionCommand().equals( ACTION_DEL ) )
        {
            int index = list.getSelectedIndex();
            if ( index >= 0 && ( index < items.size() ) )
            {
                items.remove( index );
                itemsListModel.notify( new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, index, index ) );
            }
        }

        if ( e.getActionCommand().equals( ACTION_DOWN ) )
        {
            int index = list.getSelectedIndex();
            if ( index >= 0 && ( index + 1 < items.size() ) )
            {
                Type t = items.get( index );
                items.remove( index );
                items.add( index + 1, t );
                itemsListModel.notify( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, index, index + 1 ) );
                list.setSelectedIndex( index + 1 );
            }
        }

        if ( e.getActionCommand().equals( ACTION_UP ) )
        {
            int index = list.getSelectedIndex();
            if ( index >= 1 )
            {
                Type t = items.get( index );
                items.remove( index );
                items.add( index - 1, t );
                itemsListModel.notify( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, index - 1, index ) );
                list.setSelectedIndex( index - 1 );
            }
        }
        updateButtons();
    }

    public void valueChanged( ListSelectionEvent e )
    {
        int index = list.getSelectedIndex();
        if ( index >= 0 )
        {
            editPanel.setObject( items.get( index ) );
        }
        updateButtons();
    }

    private void updateButtons()
    {
        if ( ( list != null ) && ( items != null ) )
        {
            boolean hasValidValue = editPanel.getObject() != null;
            boolean isSelected = ( list.getSelectedIndex() >= 0 ) && ( list.getSelectedIndex() < items.size() );

            add.setEnabled( hasValidValue );
            update.setEnabled( hasValidValue && isSelected );
            up.setEnabled( list.getSelectedIndex() > 0 );
            down.setEnabled( list.getSelectedIndex() + 1 < items.size() );
            remove.setEnabled( isSelected );
        }
        else
        {
            add.setEnabled( false );
            update.setEnabled( false );
            up.setEnabled( false );
            down.setEnabled( false );
            remove.setEnabled( false );
        }

    }

    @Override
    public List<Type> getObject()
    {
        return items;
    }

    @Override
    public void setObject( List<Type> state )
    {
        setItems( state );
    }

    // public void setObjectToStringConverter( ObjectToStringConverter<Type> objectToStringConverter )
    // {
    // this.objectToStringConverter = objectToStringConverter;
    // }
    //
    // public ObjectToStringConverter<Type> getObjectToStringConverter()
    // {
    // return objectToStringConverter;
    // }

}
