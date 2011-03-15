package org.codehaus.mojo.pomtools.wrapper;

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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class PropertiesWrapper
    extends ListWrapper 
{
    public PropertiesWrapper( ObjectWrapper parent, Object objectToWrap, String typeLabel )
    {
        super( parent, buildKeyValueList( (Properties) objectToWrap ), 
               PropertyWrapper.KeyValuePair.class, PropertyWrapper.class, typeLabel, "property" );
    }
    
    /**
     * We have to pass a list to the constuctor of our child objects. 
     * Since properties is not a proper list, mimic one by creating 
     * a KeyValuePair as a mock wrapped object.
     * 
     * @param props
     * @return
     */
    private static List buildKeyValueList( Properties props )
    {
        List result = new ArrayList();
        if ( props != null )
        {
            for ( Enumeration i = props.keys(); i.hasMoreElements(); )
            {
                String key = (String) i.nextElement();
                
                result.add( new PropertyWrapper.KeyValuePair( key, (String) props.get( key ) ) );
            }
        }
        
        return result;
    }
    
    public Object getWrappedObject() 
    {
        if ( isEmpty() )
        {
            return null;
        }
        
        Properties props = new Properties();
        
        for ( Iterator i = getItems().iterator(); i.hasNext(); )
        {
            PropertyWrapper prop = (PropertyWrapper) i.next();
            
            if ( prop.getWrappedKey() != null ) 
            {
                props.put( prop.getWrappedKey(), prop.getWrappedValue() );
            }
        }
        
        return props;
    }
}
