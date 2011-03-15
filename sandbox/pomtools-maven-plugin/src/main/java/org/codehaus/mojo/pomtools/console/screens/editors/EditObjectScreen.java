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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.console.screens.ScreenHelper;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableColumn;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableLayout;
import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanFields;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditObjectScreen
    extends AbstractEditListItemScreen
{
    /* our fields in order of display but also contains null items to add empty rows in the display */
    private List screenFields;
    
    /* Our fields in the order of display */
    private List modelFields;

    public EditObjectScreen( ObjectWrapper editorObject )
    {
        super( editorObject.getName(), editorObject );
        
        createFieldList();
    }
    
    public EditObjectScreen( ObjectWrapper ownerObject, BeanField field )
    {
        super( ( (ObjectWrapper) ownerObject.getFieldValue( field ) ).getName(), 
               (ObjectWrapper) ownerObject.getFieldValue( field ) );
        
        createFieldList();
    }
    
    public EditObjectScreen( ListWrapper containingList, int itemIndex )
    {
        super( containingList.getObject( itemIndex ).getName(), 
               containingList.getItems(), itemIndex );
        
        createFieldList();
    }
    
    private void createFieldList()
    {
        PomToolsPluginContext context = PomToolsPluginContext.getInstance();
        
        screenFields = new ArrayList();
        modelFields = new ArrayList();
        
        ObjectWrapper editorObject = (ObjectWrapper) getEditorObject();
        
        BeanFields tmpFields = editorObject.getFields();
        
        Set seen = new HashSet();
        
        FieldConfiguration fieldConfig = context.getFieldConfiguration( editorObject.getFullName() );

        String sortOrder = null;
        
        if ( fieldConfig != null )
        {
            sortOrder = fieldConfig.getEditorFieldOrder();
        }

        if ( sortOrder != null )
        {
            String[] fieldNames = LocalStringUtils.splitPreserveAllTokens( sortOrder, "," );
            
            for ( int i = 0; i < fieldNames.length; i++ )
            {
                String fieldName = StringUtils.trim( fieldNames[i] );
                
                BeanField field = tmpFields.get( fieldName );
                
                if ( field == null && StringUtils.isNotEmpty( fieldName ) )
                {
                    throw new ModelReflectionException( "Unable to locate the field specified in sort order: " 
                                                           + fieldName );
                }
                
                // Add to screenFields even if its null
                screenFields.add( field );
                
                if ( field != null )
                {
                    modelFields.add( field );
                    seen.add( field.getFieldName() );
                }
            }
        }
        
        for ( Iterator i = editorObject.getFields().iterator(); i.hasNext(); )
        {
            BeanField field = (BeanField) i.next();
            
            if ( !seen.contains( field.getFieldName() ) )
            {
                screenFields.add( field );
                
                modelFields.add( field );
                
                seen.add( field.getFieldName() );
            }
        }

    }
    
    private void addFieldRow( TableLayout tab, int index, BeanField field )
    {
        ObjectWrapper obj = (ObjectWrapper) getEditorObject();
        
        String value = StringUtils.defaultString( obj.getFieldValue( field, false ) );
        
        addFieldRow( tab, index, field, (String) value, obj.isFieldModified( field ) );
    }

    private void addFieldRow( TableLayout tab, int index, BeanField field, String value, boolean modified )
    {
        String nullStr = LocalStringUtils.ifTrue( field.getType().equals( BeanField.TYPE_LIST ),
                                                  "(empty)", "" );
        
        String label = getModifiedLabel( LocalStringUtils.splitCamelCase( field.getLabel() ), modified );
        
        tab.add( new String[] { numberPrompt( index ), 
                                label + ":", 
                                StringUtils.defaultString( value, nullStr ) } );
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        final int valueWidth = 60;
        
        TableColumn contentColumn = new TableColumn( valueWidth );
        contentColumn.setWrap( false );
        
        StringBuffer sb = new StringBuffer( getFieldNameHeader( ( (ObjectWrapper) getEditorObject() ).getFullName() ) );
        
        TableLayout tab = new TableLayout( getTerminal(), new TableColumn[] {
                new TableColumn( TableColumn.ALIGN_RIGHT, TableColumn.BOLD ),
                TableColumn.ALIGN_LEFT_COLUMN,
                contentColumn } );
        
        int index = 1; // 1 based items
        
        for ( Iterator i = screenFields.iterator(); i.hasNext(); )
        {
            BeanField field = (BeanField) i.next();
            if ( field != null )
            {
                addFieldRow( tab, index++, field );
            }
            else
            {
                tab.addEmptyRow();
            }
        }
        
        sb.append( tab.getOutput() );
        
        if ( hasContainingList() )
        {
            sb.append( getOptionsPane().getOutput() );            
        }
        
        return createDisplay( sb.toString(), "Select an attribute to configure" );
    }
    
    protected ConsoleScreen getEditorScreen( BeanField field )
        throws ConsoleExecutionException
    {
        return ScreenHelper.getObjectEditorScreen( (ObjectWrapper) getEditorObject(), field );        
    }
    
    
    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        final ConsoleEventDispatcher ced = getDefaultEventDispatcher();
        
        ced.addFirst( new NumericRangeListener( 1, modelFields.size(), "Select an attribute to configure." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                int index = Integer.parseInt( event.getConsoleInput() ) - 1;
                event.setNextScreen( getEditorScreen( (BeanField) modelFields.get( index ) ) );
            }
        } );
        
        return ced;
    }
}
