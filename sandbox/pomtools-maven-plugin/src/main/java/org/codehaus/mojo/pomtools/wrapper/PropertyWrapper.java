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

import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class PropertyWrapper
    extends ObjectWrapper
{
    public PropertyWrapper( ObjectWrapper parent, Object objectToWrap, String name, Class implClass )
    {
        super( parent, objectToWrap, name, implClass );
    }

    public String toString()
    {
        if ( isEmpty() )
        {
            return null;
        }
        
        KeyValuePair kvPair = (KeyValuePair) getWrappedObject();
        
        StringBuffer sb = new StringBuffer();

        sb.append( StringUtils.defaultString( kvPair.getKey() ) );

        if ( kvPair.getKey() != null && kvPair.getValue() != null )
        {
            sb.append( "=" );
        }

        sb.append( StringUtils.defaultString( kvPair.getValue() ) );

        return sb.toString();
    }
    
    String getWrappedKey()
    {
        return ( (KeyValuePair) getWrappedObject() ).getKey();
    }
    
    String getWrappedValue()
    {
        return ( (KeyValuePair) getWrappedObject() ).getValue();
    }
    
    public static class KeyValuePair
    {
        private String key;

        private String value;

        public KeyValuePair()
        {
        }

        public KeyValuePair( String key, String value )
        {
            this.key = key;

            this.value = value;
        }

        public String getKey()
        {
            return key;
        }

        public String getValue()
        {
            return value;
        }

        public void setKey( String key )
        {
            this.key = key;
        }

        public void setValue( String value )
        {
            this.value = value;
        }
    }
}
