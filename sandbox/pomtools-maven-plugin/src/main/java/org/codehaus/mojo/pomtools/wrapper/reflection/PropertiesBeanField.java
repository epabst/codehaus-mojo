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

import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.PropertiesWrapper;
import org.codehaus.mojo.pomtools.wrapper.PropertyWrapper;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class PropertiesBeanField
    extends ListField
{
    public PropertiesBeanField( String parentName, String name )
    {
        super( parentName, name, PropertyWrapper.class.getName(), "property", BeanField.TYPE_PROPERTIES );
    }
    
    public ObjectWrapper createWrapperObject( ObjectWrapper parent, Object objectToWrap )
    {
        return new PropertiesWrapper( parent, objectToWrap, getFieldName() );
    }

}
