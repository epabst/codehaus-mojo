package org.codehaus.mojo.pomtools.console.screens;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.console.screens.editors.EditBooleanScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditListScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditLovScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditObjectScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditStringListValueScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditStringScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditVersionScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreen;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.StringValueWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class ScreenHelper
{
    
    private ScreenHelper()
    {
        super();
    }

    public static String getFieldEditorSetting( String fieldName )
    {
        FieldConfiguration fieldConfig = PomToolsPluginContext.getInstance().getFieldConfiguration( fieldName );
        
        if ( fieldConfig != null )
        {
            return fieldConfig.getEditorClass();
        }
        
        return null;
    }

    public static ConsoleScreen getObjectEditorScreen( ObjectWrapper obj, BeanField field )
        throws ConsoleExecutionException
    {
        ConsoleScreen screen = null;
        
        String fullName = field.getFullFieldName( obj );
        
        String editorClassName = getFieldEditorSetting( fullName );
        
        if ( editorClassName != null )
        {
            screen = createScreen( editorClassName, 
                                   new Class[] { ObjectWrapper.class, BeanField.class },
                                   new Object[] { obj, field } );  
        }
        else if ( field.getType().equals( BeanField.TYPE_LIST ) || field.getType().equals( BeanField.TYPE_PROPERTIES ) )
        {
            screen = new EditListScreen( obj, field );
        }
        else if ( field.getType().equals( BeanField.TYPE_COMPOSITE ) )
        {
            screen = new EditObjectScreen( obj, field );
        }
        else if ( field.getType().equals( BeanField.TYPE_BOOLEAN ) )
        {
            screen = new EditBooleanScreen( obj, field );
        }
        else if ( field.getType().equals( BeanField.TYPE_VERSION ) )
        {
            screen = new EditVersionScreen( obj, field );
        }
        else
        {
            if ( field.getPossibleValues() != null && !field.getPossibleValues().isEmpty() )
            {
                screen = new EditLovScreen( obj, field );
            }
            else
            {
                screen = new EditStringScreen( obj, field );
            }
        }
        
        return screen;
    }

    public static ConsoleScreen getListItemEditorScreen( ListWrapper objList, int itemIndex )
    {
        ObjectWrapper obj = (ObjectWrapper) objList.getItems().get( itemIndex );
        
        String editorClassName = ScreenHelper.getFieldEditorSetting( obj.getFullName() );
        if ( editorClassName != null )
        {
            return createScreen( editorClassName, 
                                 new Class[] { ListWrapper.class, int.class },
                                 new Object[] { objList, new Integer( itemIndex ) } );
        }
        else if ( objList.getItemClass().equals( StringValueWrapper.StringWrapper.class ) )
        {
            return new EditStringListValueScreen( objList, itemIndex, objList.getItemTypeLabel() );
        }
        else 
        {
            return new EditObjectScreen( objList, itemIndex );
        }
    }
    
    private static ConsoleScreen createScreen( String className, Class[] signature, Object[] params )
    {
        try
        {
            Class c = Class.forName( className );
            
            Constructor con = c.getConstructor( signature );
            
            return (ConsoleScreen) con.newInstance( params );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( InstantiationException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( InvocationTargetException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( SecurityException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new ModelReflectionException( e );
        }
    }
}
