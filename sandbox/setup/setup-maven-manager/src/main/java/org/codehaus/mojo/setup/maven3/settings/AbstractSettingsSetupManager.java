package org.codehaus.mojo.setup.maven3.settings;

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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.TrackableBase;
import org.apache.maven.settings.io.SettingsReader;
import org.apache.maven.settings.io.SettingsWriter;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.SetupExecutionRequest;
import org.codehaus.mojo.setup.SetupMergeException;

/**
 * 
 * @author Robert Scholte
 * @since 1.0.0
 */
public abstract class AbstractSettingsSetupManager
    extends AbstractSetupManager
{

    /**
     * @plexus.requirement role="org.apache.maven.settings.io.SettingsReader"
     */
    // Using List instead of the class to mislead Maven. Now we can warn.
    private List<?> settingsReaderList;

    /**
     * @plexus.requirement role="org.apache.maven.settings.io.SettingsWriter"
     */
    // Using List instead of the class to mislead Maven. Now we can warn.
    private List<?> settingsWriterList;

    private SettingsReader getSettingsReader()
    {
        return (SettingsReader) settingsReaderList.get( 0 );
    }
    
    private SettingsWriter getSettingsWriter()
    {
        return (SettingsWriter) settingsWriterList.get( 0 );
    }
    
    @Override
    protected Reader merge( Reader dominant, Reader recessive )
        throws SetupMergeException
    {
        try
        {
            Settings dominantSettings = getSettingsReader().read( dominant, null );
            Settings recessiveSettings = getSettingsReader().read( recessive, null );
            SettingsUtils.merge( dominantSettings, recessiveSettings, TrackableBase.GLOBAL_LEVEL );
            Writer writer = new StringWriter();
            getSettingsWriter().write( writer, null, dominantSettings );

            return new StringReader( writer.toString() );
        }
        catch ( IOException e )
        {
            throw new SetupMergeException( e.getMessage() );
        }
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected Reader postMerge( Reader targetSettingsFileReader, SetupExecutionRequest request )
        throws SetupMergeException
    {
        Reader result = targetSettingsFileReader;
        try
        {
            Settings settings;
            if ( targetSettingsFileReader != null )
            {
                settings = getSettingsReader().read( targetSettingsFileReader, null );
            }
            else
            {
                settings = new Settings();
            }

            Map < String, String > encryptedPasswordsMap =
                ( Map < String, String > ) request.getAdditionalProperties().get( "encryptedPasswordMap" );

            if ( encryptedPasswordsMap != null )
            {
                for ( Server server : (List < Server > ) settings.getServers() )
                {
                    if ( encryptedPasswordsMap.containsKey( server.getPassword() ) )
                    {
                        server.setPassword( encryptedPasswordsMap.get( server.getPassword() ) );
                    }
                }
            }
            Writer writer = new StringWriter();
            getSettingsWriter().write( writer, null, settings );
            result = new StringReader( writer.toString() );
        }
        catch ( IOException e )
        {
            throw new SetupMergeException( e.getMessage() );
        }
        return result;
    }

    @Override
    protected String getPrototypeFilename()
    {
        return "settings.xml";
    }

}
