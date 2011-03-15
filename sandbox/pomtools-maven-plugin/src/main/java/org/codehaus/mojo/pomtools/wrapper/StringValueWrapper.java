package org.codehaus.mojo.pomtools.wrapper;

import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.codehaus.plexus.util.StringUtils;

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


/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class StringValueWrapper
    extends ObjectWrapper
{
    public StringValueWrapper( ObjectWrapper parent, Object value, String name, Class implClass )
    {
        super( parent, new StringWrapper( (String) value ), "value", implClass );
    }

    public Object getWrappedObject()
    {
        return ( (StringWrapper) getInternalWrappedObject() ).getValue();
    }

    public boolean isEmpty()
    {
        try 
        {
            return StringUtils.isEmpty( ( (StringWrapper) getInternalWrappedObject() ).getValue() );
        }
        catch ( ClassCastException e )
        {
            throw new ModelReflectionException( getFullName() + " is not a StrinWrapper candidate." );
        }
    }
    
    public String toString()
    {
        return ( (StringWrapper) getInternalWrappedObject() ).getValue();
    }
    
    public String getStringValue()
    {
        return toString();
    }

    public static class StringWrapper
    {
        private String value;

        public StringWrapper()
        {
        }

        public StringWrapper( String value )
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue( String value )
        {
            this.value = value;
        }
    }
}
