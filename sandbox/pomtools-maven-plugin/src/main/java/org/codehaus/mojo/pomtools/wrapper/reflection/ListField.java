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

import java.util.List;

import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.StringValueWrapper;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ListField
    extends BeanField
    implements FactoryBeanField
{
    private final Class itemClass;
    
    private final Class itemWrapperClass;

    private final String itemLabel;

    public ListField( String parentName, String name, String itemClassName, String itemLabel )
    {
        this( parentName, name, itemClassName, itemLabel, TYPE_LIST );
    }

    protected ListField( String parentName, String name, String itemClassName, String itemLabel, String type )
    {
        super( parentName, name, type, true );

        String strItemClassName = StringUtils.defaultString( itemClassName, BeanField.TYPE_STRING );

        this.itemLabel = itemLabel;

        if ( strItemClassName.equalsIgnoreCase( BeanField.TYPE_STRING ) 
            || strItemClassName.equals( String.class.getName() ) )
        {
            this.itemClass = StringValueWrapper.StringWrapper.class;
            this.itemWrapperClass = StringValueWrapper.class;
        }
        else
        {
            this.itemClass = getClassByName( strItemClassName );
            this.itemWrapperClass = ObjectWrapper.class;
        }
    }

    public ObjectWrapper createWrapperObject( ObjectWrapper parent, Object objectToWrap )
    {
        return new ListWrapper( parent, (List) objectToWrap, itemClass, itemWrapperClass, 
                                getFieldName(), itemLabel );
    }

}
