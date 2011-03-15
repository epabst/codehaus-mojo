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

import java.util.List;

import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditBooleanScreen
    extends AbstractModelScreen
{
    private final ObjectWrapper obj;

    private final BeanField field;

    public EditBooleanScreen( ObjectWrapper obj, BeanField field )
    {
        super( field.getFullFieldName( obj ) );

        this.obj = obj;
        this.field = field;
        
        if ( !field.getType().equals( BeanField.TYPE_BOOLEAN ) )
        {
            throw new IllegalArgumentException( "Wrong type of field passed to this editor: " + field.getType() );
        }
        
        if ( field.getPossibleValues() == null )
        {
            throw new IllegalArgumentException( "Boolean field does not contain any values: " + field.getType() );
        }
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer();

        sb.append( getFieldNameHeader( field.getFullFieldName( obj ) ) );
        
        List lovValues = field.getPossibleValues();
        
        String currentValue = StringUtils.defaultString( obj.getFieldValue( field ) );
        
        LabeledList lil = new LabeledList( getTerminal(), true );
        
        for ( int i = 0; i < lovValues.size(); i++ )
        {
            if ( StringUtils.equals( currentValue, (String) lovValues.get( i ) ) )
            {
                lil.add( numberPrompt( i ), lovValues.get( i ) + " (current)" );
            }
            else 
            {
                lil.add( numberPrompt( i ), (String) lovValues.get( i ) );
            }
        }
        
        sb.append( lil.getOutput() )
          .append( NEWLINE );
        
        return createDisplay( sb.toString(), 
                              "Please select a value for " + field.getFieldName() );
    }

    private void setFieldValue( String value )
        throws ConsoleExecutionException
    {
        obj.setFieldValue( field, value );           
    }
    
    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = getDefaultEventManager( true );

        ced.add( new NumericRangeListener( 0, field.getPossibleValues().size() - 1, 
                                           "Select a value for " + field.getFieldName() )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                int index = Integer.parseInt( event.getConsoleInput() );
                setFieldValue( (String) field.getPossibleValues().get( index ) );
                
                event.setReturnToPreviousScreen( );                
            }
        } );
        
        return ced;
    }
}
