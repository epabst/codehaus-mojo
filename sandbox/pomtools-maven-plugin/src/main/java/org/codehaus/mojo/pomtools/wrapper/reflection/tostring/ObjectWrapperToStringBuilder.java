package org.codehaus.mojo.pomtools.wrapper.reflection.tostring;

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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ObjectWrapperToStringBuilder
    implements ObjectToStringBuilder
{

    public ObjectWrapperToStringBuilder()
    {
        super();
    }

    public String toString( Object obj )
    {
        ObjectWrapper owrapper = (ObjectWrapper) obj;
        
        if ( owrapper.isEmpty() )
        {
            return null;
        }
        
        org.apache.commons.lang.builder.ToStringBuilder builder = null; 
    
        if ( !owrapper.getFields().isEmpty() )
        {
            builder = new ToStringBuilder( this, ModelHelper.TO_STRING_STYLE );
            
            for ( Iterator i = owrapper.getFields().iterator(); i.hasNext(); )
            {
                BeanField field = (BeanField) i.next();
                Object value = owrapper.getFieldValue( field );
                
                if ( field.isWrappedValue() )
                {
                    builder.append( field.getFieldName(), ( (ObjectWrapper) value ).toString() );
                }
                else
                {
                    builder.append( field.getFieldName(), value );
                }
            }
        }
        else
        {
            builder = new ToStringBuilder( this );
        }
        
        return builder.toString();
    }

}
