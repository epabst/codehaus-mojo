package org.codehaus.mojo.setup.scm.starteam.settings;

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

import org.apache.maven.scm.providers.starteam.settings.Settings;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author Robert Scholte
 * @since 1.0.0
 */
public class SettingsUtils
{
    //default values of primitive variables
    private static final boolean DEFAULT_COMPRESSIONENABLED = false;
    
    private static final String DEFAULT_EOL = "on";
    
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

        if ( dominant.isCompressionEnable() == DEFAULT_COMPRESSIONENABLED )
        {
            dominant.setCompressionEnable( recessive.isCompressionEnable() );
        }

        if ( StringUtils.isEmpty( dominant.getEol() ) || DEFAULT_EOL.equals( dominant.getEol() ) )
        {
            dominant.setEol( recessive.getEol() );
        }
        if ( StringUtils.isEmpty( dominant.getModelEncoding() ) )
        {
            dominant.setModelEncoding( recessive.getModelEncoding() );
        }

    }
}
