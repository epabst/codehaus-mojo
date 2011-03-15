package org.codehaus.mojo.setup.security;

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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.execution.MavenSession;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.SetupExecutionRequest;
import org.codehaus.mojo.setup.SetupMergeException;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;
import org.sonatype.plexus.components.sec.dispatcher.model.io.xpp3.SecurityConfigurationXpp3Writer;

/**
 * @author Robert Scholte
 * @since 1.0.0
 * @plexus.component role="org.codehaus.mojo.setup.SetupManager" role-hint="settings-security"
 */
public final class SettingsSecuritySetupManager
    extends AbstractSetupManager
{
    /**
     * @plexus.requirement
     */
    private SecDispatcher secDispatcher;

    private SecurityConfigurationXpp3Writer securityConfigurationWriter = new SecurityConfigurationXpp3Writer();

    @Override
    public File getTargetFile( MavenSession session )
    {
        // copied from org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher.getSec()
        String location =
            System.getProperty( DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION,
                                ( (DefaultSecDispatcher) secDispatcher ).getConfigurationFile() );
        String realLocation =
            ( location.charAt( 0 ) == '~' ) ? ( System.getProperty( "user.home" ) + location.substring( 1 ) )
                            : location;
        return new File( realLocation );
    }

    @Override
    protected Reader postMerge( Reader targetSettingsFileReader, SetupExecutionRequest request )
        throws SetupMergeException
    {
        SettingsSecurity settingsSecurity = new SettingsSecurity();
        if ( request.getAdditionalProperties().containsKey( "master" ) )
        {
            settingsSecurity.setMaster( (String) request.getAdditionalProperties().get( "master" ) );
        }
        Writer writer = new StringWriter();
        try
        {
            securityConfigurationWriter.write( writer, settingsSecurity );
        }
        catch ( IOException e )
        {
            throw new SetupMergeException( e.getMessage() );
        }
        return new StringReader( writer.toString() );
    }

    @Override
    protected String getPrototypeFilename()
    {
        return "settings-security.xml";
    }

    @Override
    protected Reader merge( Reader dominant, Reader recessive )
        throws SetupMergeException
    {
        throw new SetupMergeException( "merge not supported" );
    }
}
