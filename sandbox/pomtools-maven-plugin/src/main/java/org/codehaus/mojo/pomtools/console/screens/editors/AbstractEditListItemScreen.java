package org.codehaus.mojo.pomtools.console.screens.editors;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class AbstractEditListItemScreen
    extends AbstractModelScreen
{
    protected static final String KEY_PREVIOUS_LIST_ITEM = "<";
    
    protected static final String KEY_NEXT_LIST_ITEM = ">"; 

    private Object editorObject;
    
    private List containingList;

    private int itemIndex = -1;

    public AbstractEditListItemScreen( String name, Object editorObject )
    {
        super( name );

        this.containingList = null;
        
        this.editorObject = editorObject;
        
        if ( this.editorObject == null )
        {
            throw new IllegalArgumentException( "editorObject cannot be null" );
        }
    }
    
    public AbstractEditListItemScreen( String name, List containingList, int index )
    {
        super( name );

        this.containingList = containingList;
        setCurrentIndex( index );
    }
    
    protected void setCurrentIndex( int index )
    {
        this.itemIndex = index;
        if ( itemIndex >= 0 && itemIndex < containingList.size() )
        {
            this.editorObject = containingList.get( itemIndex );
        }
        else
        {
            this.editorObject = null;
        }
    }
    
    protected void setContainingList( List list, int index )
    {
        this.containingList = list;
        setCurrentIndex( index );
    }
    
    protected Object getEditorObject()
    {
        return editorObject;
    }
    
    protected List getContainingList()
    {
        return containingList;
    }

    protected boolean hasContainingList()
    {
        return containingList != null;
    }

    protected boolean hasNext()
    {
        return hasContainingList() && itemIndex < containingList.size() - 1;
    }

    protected boolean hasPrevious()
    {
        return hasContainingList() && itemIndex > 0;
    }

    public ConsoleEventDispatcher getDefaultEventDispatcher()
        throws ConsoleExecutionException
    {
        final ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();

        if ( hasContainingList() )
        {
            ced.addFirst( new MatchingListener( KEY_DELETE_ITEM, "Delete this item" )
            {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    getContainingList().remove( editorObject );
                    event.setReturnToPreviousScreen();
                }
            } );

            if ( hasNext() )
            {
                ced.addFirst( new MatchingListener( KEY_NEXT_LIST_ITEM, "Next item in list" )
                {
                    public void processEvent( ConsoleEvent event )
                        throws ConsoleExecutionException
                    {
                        setCurrentIndex( ++itemIndex );
                    }
                } );
            }

            if ( hasPrevious() )
            {
                ced.addFirst( new MatchingListener( KEY_PREVIOUS_LIST_ITEM, "Previous item in list" )
                {
                    public void processEvent( ConsoleEvent event )
                        throws ConsoleExecutionException
                    {
                        setCurrentIndex( --itemIndex );
                    }
                } );
            }
        }
        
        return ced;
    }
    
    protected OptionsPane getOptionsPane()
    {
        return getOptionsPane( true );
    }
    
    protected OptionsPane getOptionsPane( boolean includeDelete )
    {
        OptionsPane options = new OptionsPane();
        
        if ( hasContainingList() )
        {
            if ( hasPrevious() )
            {
                options.add( KEY_PREVIOUS_LIST_ITEM, "Previous item in list" );
            }

            if ( hasNext() )
            {
                options.add( KEY_NEXT_LIST_ITEM, "Next item in list" );
            }

            if ( includeDelete )
            {
                options.add( KEY_DELETE_ITEM, "Delete this item from the list" );
            }
        }
        
        return options;
    }

}
