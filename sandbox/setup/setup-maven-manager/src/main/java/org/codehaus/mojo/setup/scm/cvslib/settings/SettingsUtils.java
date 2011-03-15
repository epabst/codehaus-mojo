package org.codehaus.mojo.setup.scm.cvslib.settings;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Map.Entry;

import org.apache.maven.scm.providers.cvslib.settings.Settings;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author Robert Scholte
 * @since 1.0.0
 */
public class SettingsUtils
{
    //default values of primitive variables
    private static final int DEFAULT_COMPRESSIONLEVEL = 3;
    private static final boolean DEFAULT_USECVSRC = false;
    private static final boolean DEFAULT_USEFORCETAG = true;
    private static final boolean DEFAULT_TRACECVSCOMMAND = false;
    
    private static final String DEFAULT_CHANGELOGCOMMANDDATEFORMAT = "yyyy-MM-dd HH:mm:ssZ";
    
    
    private SettingsUtils()
    {
        // don't allow construction.
    }

    /**
     * @param dominant
     * @param recessive
     */
    public static void merge( Settings dominant, Settings recessive )
    {
        if ( ( dominant == null ) || ( recessive == null ) )
        {
            return;
        }

        if ( StringUtils.isEmpty( dominant.getChangeLogCommandDateFormat() ) 
                        || DEFAULT_CHANGELOGCOMMANDDATEFORMAT.equals( dominant.getChangeLogCommandDateFormat() ) )
        {
            dominant.setChangeLogCommandDateFormat( recessive.getChangeLogCommandDateFormat() );
        }

        if ( dominant.isUseCvsrc() == DEFAULT_USECVSRC )
        {
            dominant.setUseCvsrc( recessive.isUseCvsrc() );
        }
        
        if ( dominant.isUseForceTag() == DEFAULT_USEFORCETAG )
        {
            dominant.setUseForceTag( recessive.isUseForceTag() );
        }

        if ( dominant.getCompressionLevel() == DEFAULT_COMPRESSIONLEVEL )
        { // 3 = default
            dominant.setCompressionLevel( recessive.getCompressionLevel() );
        }

        if ( dominant.isTraceCvsCommand() == DEFAULT_TRACECVSCOMMAND )
        {
            dominant.setTraceCvsCommand( recessive.isTraceCvsCommand() );
        }

        if ( StringUtils.isEmpty( dominant.getTemporaryFilesDirectory() ) )
        {
            dominant.setTemporaryFilesDirectory( recessive.getTemporaryFilesDirectory() );
        }

        for ( Entry < Object, Object > cvsVariable : recessive.getCvsVariables().entrySet() )
        {
            if ( !dominant.getCvsVariables().containsKey( cvsVariable.getKey() ) )
            {
                dominant.addCvsVariable( (String) cvsVariable.getKey(), (String) cvsVariable.getValue() );
            }
        }
    }
}
