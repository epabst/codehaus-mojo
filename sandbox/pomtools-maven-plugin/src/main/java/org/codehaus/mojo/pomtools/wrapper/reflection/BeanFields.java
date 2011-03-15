package org.codehaus.mojo.pomtools.wrapper.reflection;

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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class BeanFields
{
    private final List fields = new ArrayList();
    
    private final Map fieldMap = new HashMap();

    private final String fieldFullName;
    
    private static final Map LIST_ITEM_TYPE_CACHE = new HashMap();
    
    public BeanFields( String fieldFullName, Object objectToInspect )
    {
        this.fieldFullName = fieldFullName;
        
        PomToolsPluginContext context = PomToolsPluginContext.getInstance();
        
        Log log = context.getLog();
        
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors( objectToInspect );
        
        for ( int i = 0; i < descriptors.length; i++ )
        {
            PropertyDescriptor pd = descriptors[i];
            
            FieldConfiguration config = context.getFieldConfiguration( ModelHelper.buildFullName( fieldFullName, 
                                                                                                  pd.getName() ) ); 
            if ( pd.getWriteMethod() != null && ( config == null || !config.isIgnore() ) )
            {
                if ( log.isDebugEnabled() )
                {
                    log.debug( "Property: " + ModelHelper.buildFullName( fieldFullName, pd.getName() ) 
                               + " => " + pd.getPropertyType().getName() );
                }
                
                if ( pd.getPropertyType().equals( String.class ) )
                {
                    add( new StringField( fieldFullName, pd.getName() ) );
                }
                else if ( pd.getPropertyType().equals( boolean.class ) )
                {
                    add( new BooleanField( fieldFullName, pd.getName() ) );
                }
                else if ( pd.getPropertyType().equals( List.class ) )
                {
                    addListField( pd, objectToInspect );
                }
                else if ( pd.getPropertyType().equals( Properties.class ) )
                {
                    add( new PropertiesBeanField( fieldFullName, pd.getName() ) );
                }
                else 
                {
                    add( new CompositeField( fieldFullName, pd.getName(), pd.getPropertyType() ) );
                }
            }
        }

    }
    
    /** Convert a plural string to its singular form
     */
    protected String makeSingular( String name )
    {
        if ( name == null )
        {
            return null;
        }
        
        if ( name.endsWith( "ies" ) )
        {
            return name.substring( 0, name.length() - "ies".length() ) + "y";
        }
        else if ( name.endsWith( "es" ) && ( name.endsWith( "ches" ) || name.equalsIgnoreCase( "classes" ) ) )
        {
            return name.substring( 0, name.length() - "es".length() );
        }
        else if ( name.endsWith( "s" ) )
        {
            return name.substring( 0, name.length() - "s".length() );
        }

        return name;
    }
    
    private void addListField( PropertyDescriptor pd, Object objectToInspect )
    {
        PomToolsPluginContext context = PomToolsPluginContext.getInstance();
        
        String fieldName = ModelHelper.buildFullName( this.fieldFullName, pd.getName() );
        
        FieldConfiguration fieldConfig = context.getFieldConfiguration( fieldName );
        
        Class itemImplClass = null;        
        String itemLabel = null;
        
        if ( fieldConfig != null )
        {
            String itemClassName = fieldConfig.getItemClassName();
            
            itemLabel = fieldConfig.getItemLabel();
            
            if ( itemClassName != null )
            {
                try
                {
                    itemImplClass = Class.forName( itemClassName );
                }
                catch ( ClassNotFoundException e )
                {
                    throw new ModelReflectionException( "Unable to locate the itemClassName class " + itemClassName );
                }
            }
        }
        
        if ( itemLabel == null )
        {
            itemLabel = makeSingular( pd.getName() );
        }

        
        if ( itemImplClass == null )
        {
            // Try get get the type by looking for an addXXX method
            // Currently field name is something like: project.dependencies
            String cacheKey = objectToInspect.getClass().getName() + ":" + pd.getName();
            
            if ( LIST_ITEM_TYPE_CACHE.containsKey( cacheKey ) )
            {
                itemImplClass = (Class) LIST_ITEM_TYPE_CACHE.get( cacheKey );                
            }
            else
            {
                String methodName = "add" + StringUtils.capitalizeFirstLetter( makeSingular( pd.getName() ) );
    
                Method[] methods = objectToInspect.getClass().getMethods();
    
                for ( int i = 0; i < methods.length; i++ )
                {
                    if ( methods[i].getName().equals( methodName ) )
                    {
                        Class[] paramTypes = methods[i].getParameterTypes();
    
                        if ( paramTypes.length == 1 )
                        {
                            itemImplClass = paramTypes[0];
                            LIST_ITEM_TYPE_CACHE.put( cacheKey, itemImplClass );
                            
                            break;
                        }
    
                    }
                }
            }
        }
        
        
        if ( itemImplClass != null && itemLabel != null )
        {
            add( new ListField( fieldFullName, pd.getName(), itemImplClass.getName(), itemLabel ) );
        }
        else
        {
            context.getLog().warn( "Unable to find field configuration for List: " + fieldName );
        }
    }
    
    private BeanField add( BeanField field )
    {
        if ( fieldMap.containsKey( field.getFieldName() ) )
        {
            throw new IllegalArgumentException( "Duplicate field name found for: " + field.getFieldName() );
        }
        
        fields.add( field );
        fieldMap.put( field.getFieldName(), field );
        
        return field;
    }
    
    public Iterator iterator()
    {
        return fields.iterator();
    }

    public int size()
    {
        return fields.size();
    }
    
    public BeanField get( int id )
    {
        return (BeanField) fields.get( id );
    }
    
    public BeanField get( String fieldName )
    {
        return (BeanField) fieldMap.get( fieldName );
    }

    public String getFieldFullName()
    {
        return fieldFullName;
    }

    public boolean isEmpty()
    {
        return fields.isEmpty();
    }
}
