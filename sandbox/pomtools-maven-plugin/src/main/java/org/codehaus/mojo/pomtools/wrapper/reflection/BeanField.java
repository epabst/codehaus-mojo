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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class BeanField
{
    public static final String TYPE_BOOLEAN    = "boolean";
    public static final String TYPE_COMPOSITE  = "composite";
    public static final String TYPE_LIST       = "list";    
    public static final String TYPE_PROPERTIES = "properties";
    public static final String TYPE_STRING     = "string";
    public static final String TYPE_VERSION    = "version";
    
    private final String fieldName;
    
    private final String fullFieldName;
    
    private final String label;
    
    private final String type;
    
    private final List possibleValues; 
    
    private final boolean wrappedValue;
    
    BeanField( String parentFieldName, String fieldName, String type, boolean wrappedValue )
    {
        this.fieldName = fieldName;
        
        this.fullFieldName = ModelHelper.buildFullName( parentFieldName, this.fieldName );
        
        this.type = type;
        this.wrappedValue = wrappedValue;
        
        FieldConfiguration fieldConfig = PomToolsPluginContext.getInstance().getFieldConfiguration( fullFieldName );
        
        List tmpValues = new ArrayList();
        
        if ( fieldConfig != null ) 
        {
            String strValues = fieldConfig.getPossibleValues();
            
            if ( strValues != null )
            {
                String[] valueArr = StringUtils.split( strValues, "," );
                
                for ( int i = 0; i < valueArr.length; i++ )
                {
                    tmpValues.add( valueArr[i].trim() );
                }
            }
            
            String tmpLabel = fieldConfig.getLabel();
            if ( tmpLabel != null )
            {
                this.label = tmpLabel;
            }
            else
            {
                this.label = fieldName;
            }
        } 
        else
        {
            this.label = fieldName;
        }
        
        this.possibleValues = Collections.unmodifiableList( tmpValues );
    }
    
    protected Class getClassByName( String className )
    {
        if ( className == null )
        {
            return null;
        }
        
        try
        {
            return Class.forName( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ModelReflectionException( e );
        }
    }
    
    public String getFieldName()
    {
        return fieldName;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public String getFullFieldName( ObjectWrapper obj )
    {
        return obj.getFullName() + ObjectWrapper.FIELD_PATH_SEPARATOR + getFieldName();
    }
    
    public String getType() 
    {
        return this.type;
    }

    public List getPossibleValues()
    {
        return possibleValues;
    }
    
    public String toString() 
    {
        return fieldName + ":" + type;
    }
    
    public boolean equals( Object obj )
    {
        BeanField that = (BeanField) obj;
        return this.fieldName.equals( that.fieldName )
            && this.type.equals( that.type );
    }
    
    public int hashCode()
    {
        return toString().hashCode();
    }

    public boolean isWrappedValue()
    {
        return wrappedValue;
    }

}
