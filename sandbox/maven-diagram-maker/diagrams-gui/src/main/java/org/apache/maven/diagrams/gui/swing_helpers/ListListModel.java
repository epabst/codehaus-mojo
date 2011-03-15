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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ListListModel<Type> implements ListModel
{
    private List<Type> items;

    private Set<ListDataListener> listDataListeners = new HashSet<ListDataListener>();

    public ListListModel( List<Type> a_items )
    {
        items = a_items;
    }

    public void addListDataListener( ListDataListener l )
    {
        listDataListeners.add( l );
    }

    public void removeListDataListener( ListDataListener l )
    {
        listDataListeners.remove( l );
    }

    public void notify( ListDataEvent e )
    {
        for ( ListDataListener l : listDataListeners )
        {
            l.contentsChanged( e );
        }
    }

    public Object getElementAt( int index )
    {
        return items.get( index );
    }

    public Object setElementAt( int index )
    {
        return items.get( index );
    }

    public int getSize()
    {
        if ( items == null )
            return 0;
        return items.size();
    }

}
