package org.codehaus.mojo.pomtools.helpers;

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

import org.apache.commons.lang.builder.ToStringStyle;

/** Model plugins own toString style which is used by {@link org.apache.commons.lang.builder.ToStringBuilder}.
 * Does not print the class name, identity hash code, or include the field names of null fields. 
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ModelToStringStyle
    extends ToStringStyle
{

    public ModelToStringStyle()
    {
        super();
        this.setUseClassName( false );
        this.setUseFieldNames( false );
        this.setUseIdentityHashCode( false );
        this.setNullText( "" );
    }

    public void append( StringBuffer buffer, String fieldName, Object value, Boolean fullDetail )
    {
        if ( value != null )
        {
            super.append( buffer, fieldName, value, fullDetail );
        }
    }

}
