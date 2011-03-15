package org.codehaus.mojo.pomtools.console.screens.editors;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import java.util.Iterator;
import java.util.List;

import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.screens.ScreenHelper;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditListScreen
    extends AbstractModelScreen
{
    public static final String NO_ITEMS = "No items in list to display" + NEWLINE;
    
    private final String objFullName;
    
    private final ListWrapper listWrapper;
    
    private final List items;
    
    public EditListScreen( ObjectWrapper obj, BeanField field )
    {
        super( field.getFieldName() );

        this.objFullName = field.getFullFieldName( obj );
        
        if ( !field.getType().equals( BeanField.TYPE_LIST ) && !field.getType().equals( BeanField.TYPE_PROPERTIES ) )
        {
            throw new IllegalArgumentException( "Wrong type of field passed to this editor: " + field.getType() );
        }
    
        listWrapper = (ListWrapper) obj.getFieldValue( field );
        
        this.items = listWrapper.getItems();
    }
    
    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer( getFieldNameHeader( objFullName ) );
        
        LabeledList lil = new LabeledList( getTerminal(), true, true );
        
        if ( items.isEmpty() )
        {
            sb.append( NO_ITEMS );
        }
        else
        {
            int index = 0;
            for ( Iterator i = items.iterator(); i.hasNext(); )
            {
                ObjectWrapper tmp = (ObjectWrapper) i.next();
                
                lil.add( numberPrompt( ++index ), getModifiedLabel( tmp.toString(), tmp.isModified() ) );
            }
            
            sb.append( lil.getOutput() );
        }
        
        OptionsPane options = new OptionsPane();
        
        options.add( KEY_NEW_ITEM, "Add new " + listWrapper.getItemTypeLabel() );
        
        sb.append( options.getOutput() );
        
        return createDisplay( sb.toString(), "Select an item to configure" );
    }
    
    
    
    public final ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        final ConsoleEventDispatcher ced = getDefaultEventDispatcher();
        
        ced.addFirst( new NumericRangeListener( 1, items.size(), "Select an item to configure." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                int itemId = Integer.parseInt( event.getConsoleInput() ) - 1;
                
                event.setNextScreen( ScreenHelper.getListItemEditorScreen( listWrapper, itemId ) );
            }
        } );
        
        ced.addFirst( new MatchingListener( KEY_NEW_ITEM, "Add a new " + listWrapper.getItemTypeLabel() )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                listWrapper.createItem( null );
                
                event.setNextScreen( ScreenHelper.getListItemEditorScreen( listWrapper, 
                                                                           listWrapper.size() - 1 ) );
            }
        } );
        
        return ced;
    }
}
