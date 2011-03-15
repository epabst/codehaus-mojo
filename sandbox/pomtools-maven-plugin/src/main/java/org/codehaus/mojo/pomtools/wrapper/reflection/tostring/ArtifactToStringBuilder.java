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

import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ArtifactToStringBuilder
    implements ObjectToStringBuilder
{

    public ArtifactToStringBuilder()
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
        
        String groupId    = (String) owrapper.getFieldValue( ModelHelper.GROUP_ID );
        String artifactId = (String) owrapper.getFieldValue( ModelHelper.ARTIFACT_ID );
        
        String version = null;
        if ( owrapper.getFields().get( ModelHelper.VERSION ) != null )
        {
            version = (String) owrapper.getFieldValue( ModelHelper.VERSION );
        }
        
        return getLabel( groupId, artifactId, version );
    }

    private void append( StringBuffer sb, String prefix, String value )
    {
        if ( value != null )
        {
            if ( prefix != null && sb.toString().length() > 0 )
            {
                sb.append( prefix );
            }
            sb.append( value );
        }
    }

    private String getLabel( String groupId, String artifactId, String version )
    {
        if ( groupId == null && artifactId == null && version == null )
        {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        append( sb, null, StringUtils.defaultString( groupId, ModelHelper.UNKNOWN ) );
        append( sb, ":",  StringUtils.defaultString( artifactId, ModelHelper.UNKNOWN ) );
        append( sb, ":", version );

        return sb.toString();
    }

}
