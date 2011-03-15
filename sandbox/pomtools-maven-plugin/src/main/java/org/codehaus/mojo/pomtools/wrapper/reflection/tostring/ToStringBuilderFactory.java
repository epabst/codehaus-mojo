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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class ToStringBuilderFactory
{
    private static final Map INSTANCE_MAP = new HashMap();
    
    private ToStringBuilderFactory()
    {
        super();
    }
    
    public static ObjectToStringBuilder get( String className )
    {
        ObjectToStringBuilder builder = (ObjectToStringBuilder) INSTANCE_MAP.get( className );
        
        if ( builder == null )
        {
            try
            {
                builder = (ObjectToStringBuilder) ConstructorUtils.invokeConstructor( Class.forName( className ), 
                                                                                      null );
                
                INSTANCE_MAP.put( className, builder );
            }
            catch ( NoSuchMethodException e )
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
            catch ( InstantiationException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( ClassNotFoundException e )
            {
                throw new ModelReflectionException( e );
            }
        }
        
        return builder;
    }
}
