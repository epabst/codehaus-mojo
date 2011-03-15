package org.apache.maven.diagrams.gui.controller;

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
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import prefuse.Display;
import prefuse.controls.Control;
import prefuse.data.Table;
import prefuse.data.event.EventConstants;
import prefuse.data.event.TableListener;
import prefuse.visual.VisualItem;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class FixNodeControl implements Control, TableListener
{
    private VisualItem activeItem;

    private boolean fixOnMouseOver;

    private boolean enabled;

    private boolean wasFixed;

    private boolean resetItem;

    public FixNodeControl()
    {
        fixOnMouseOver = true;
        enabled = true;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void itemClicked( VisualItem arg0, MouseEvent arg1 )
    {
        if ( arg0 == activeItem )
        {
            wasFixed = !wasFixed;
        }
    }

    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered( VisualItem item, MouseEvent e )
    {
        Display d = (Display) e.getSource();
        d.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        activeItem = item;
        if ( fixOnMouseOver )
        {
            wasFixed = item.isFixed();
            resetItem = true;
            item.setFixed( true );
            item.getTable().addTableListener( this );
        }
    }

    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemExited( VisualItem item, MouseEvent e )
    {
        if ( activeItem == item )
        {
            activeItem = null;
            item.getTable().removeTableListener( this );
            item.setFixed( wasFixed );
        }
        Display d = (Display) e.getSource();
        d.setCursor( Cursor.getDefaultCursor() );
    } //

    public void itemDragged( VisualItem arg0, MouseEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemKeyPressed( VisualItem arg0, KeyEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemKeyReleased( VisualItem arg0, KeyEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemKeyTyped( VisualItem arg0, KeyEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemMoved( VisualItem arg0, MouseEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemPressed( VisualItem arg0, MouseEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemReleased( VisualItem arg0, MouseEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void itemWheelMoved( VisualItem arg0, MouseWheelEvent arg1 )
    {
        // TODO Auto-generated method stub

    }

    public void keyPressed( KeyEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void keyReleased( KeyEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void keyTyped( KeyEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseClicked( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseDragged( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseEntered( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseExited( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseMoved( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mousePressed( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseReleased( MouseEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void mouseWheelMoved( MouseWheelEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

    public void setEnabled( boolean arg0 )
    {
        enabled = arg0;
    }

    /**
     * @see prefuse.data.event.TableListener#tableChanged(prefuse.data.Table, int, int, int, int)
     */
    public void tableChanged( Table t, int start, int end, int col, int type )
    {
        if ( activeItem == null || type != EventConstants.UPDATE || col != t.getColumnNumber( VisualItem.FIXED ) )
            return;
        int row = activeItem.getRow();
        if ( row >= start && row <= end )
            resetItem = false;
    }

}
