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
import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditStringScreen
    extends AbstractModelScreen
{
    private final ObjectWrapper obj;

    private final BeanField field;
    
    private String addlMessage;
    
    public EditStringScreen( ObjectWrapper obj, BeanField field )
    {
        super( field.getFullFieldName( obj ) );

        this.obj = obj;
        this.field = field;
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer();

        sb.append( getFieldNameHeader( field.getFullFieldName( obj ) ) );

        if ( addlMessage != null )
        {
            sb.append( addlMessage )
              .append( NEWLINE );
        }
        
        sb.append( LocalStringUtils.splitCamelCase( field.getLabel() ) + ": " 
                   + StringUtils.defaultString( obj.getFieldValue( field ), NULL_VALUE ) )
          .append( NEWLINE );
        
        return createDisplay( sb.toString(), 
                              "Please enter a new value for " + field.getFieldName() );
    }

    private void setFieldValue( String value )
        throws ConsoleExecutionException
    {
        obj.setFieldValue( field, value );           
    }
    
    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = getDefaultEventManager( false );

        ced.add( new NonNullTextListener( "Enter a new value for " + field.getFieldName() )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                setFieldValue( event.getConsoleInput().trim() );
                
                event.setReturnToPreviousScreen( );                
            }
        } );

        ced.add( new DefaultListener()
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                setFieldValue( null );
                
                event.setReturnToPreviousScreen( ); 
            }
        } );

        return ced;
    }

    public String getAddlMessage()
    {
        return addlMessage;
    }

    public void setAddlMessage( String addlMessage )
    {
        this.addlMessage = addlMessage;
    }
}
