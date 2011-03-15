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

import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.DefaultListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.NonNullTextListener;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.plexus.util.StringUtils;

/** Editor for editing a String from a List of String objects.
 * Generally we go straight to the {@link org.codehaus.mojo.pomtools.console.screens.editors.EditObjectScreen}
 * from the {@link org.codehaus.mojo.pomtools.console.screens.editors.EditListScreen}, but in the case of
 * a List of String, we want to go straight to a String editor.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditStringListValueScreen
    extends AbstractModelScreen
{
    private final ListWrapper containingList;
    
    private final ObjectWrapper obj;
    
    private final BeanField field;
    
    private final String itemTypeLabel;
    
    public EditStringListValueScreen( ListWrapper containingList, int itemIndex, String itemTypeLabel )
    {
        super( containingList.getObject( itemIndex ).getName() );
        
        this.obj = containingList.getObject( itemIndex );
        
        this.itemTypeLabel = itemTypeLabel;
        
        this.field = obj.getFields().get( "value" );
        
        this.containingList = containingList;
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer();

        // This obj is a string edit object, we want the parent to get the actual field name
        sb.append( getFieldNameHeader( ModelHelper.buildFullName( obj.getParent().getFullName(), itemTypeLabel ) ) );
        
        sb.append( "Current Value: " + StringUtils.defaultString( obj.getFieldValue( field ), NULL_VALUE ) )
          .append( NEWLINE );
        
        return createDisplay( sb.toString(), "Please enter a value or null to delete this item" );
    }

    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = getDefaultEventManager( false );

        ced.add( new NonNullTextListener( "Enter a value or null to delete this item." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                obj.setFieldValue( field, event.getConsoleInput().trim() );
                
                event.setReturnToPreviousScreen( );                
            }
        } );

        ced.add( new DefaultListener()
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                containingList.remove( obj );
                
                event.setReturnToPreviousScreen( ); 
            }
        } );

        return ced;
    }
}
