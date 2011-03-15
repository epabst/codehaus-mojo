package org.codehaus.mojo.setup.scm.clearcase.settings;

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
import java.lang.reflect.Field;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.scm.provider.clearcase.util.ClearCaseUtil;
import org.apache.maven.scm.providers.clearcase.settings.Settings;
import org.apache.maven.scm.providers.clearcase.settings.io.xpp3.ClearcaseXpp3Reader;
import org.apache.maven.scm.providers.clearcase.settings.io.xpp3.ClearcaseXpp3Writer;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.SetupMergeException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author Robert Scholte
 * @since 1.0.0
 * @plexus.component role="org.codehaus.mojo.setup.SetupManager" role-hint="clearcase"
 */
public final class ClearcaseSetupManager
    extends AbstractSetupManager
{
    private ClearcaseXpp3Reader clearcaseReader = new ClearcaseXpp3Reader();

    private ClearcaseXpp3Writer clearcaseWriter = new ClearcaseXpp3Writer();

    public String getFilename()
    {
        // both targetfile or filename are not exposed, so use reflection to get those values
        String result = null;
        Field field = null;
        try
        {
            field = ClearCaseUtil.class.getDeclaredField( "CLEARCASE_SETTINGS_FILENAME" );
            field.setAccessible( true );

            result = (String) field.get( null );
        }
        catch ( NoSuchFieldException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IllegalAccessException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if ( field != null )
            {
                field.setAccessible( false );
            }
        }
        return result;
    }

    @Override
    public File getTargetFile( MavenSession session )
    {
        File result = null;
        Field field = null;
        try
        {
            field = ClearCaseUtil.class.getDeclaredField( "settingsDirectory" );
            field.setAccessible( true );

            File settingsDirectory = (File) field.get( null );
            result = new File( settingsDirectory, getFilename() );
        }
        catch ( SecurityException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( NoSuchFieldException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IllegalArgumentException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IllegalAccessException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if ( field != null )
            {
                field.setAccessible( false );
            }
        }
        return result;
    }

    @Override
    protected Reader merge( Reader dominantReader, Reader recessiveReader )
        throws SetupMergeException
    {
        Reader result = null;
        try
        {

            Settings dominantAndMergedSettings = clearcaseReader.read( dominantReader );
            Settings recessiveSettings = clearcaseReader.read( recessiveReader );

            SettingsUtils.merge( dominantAndMergedSettings, recessiveSettings );

            Writer writer = new StringWriter();
            clearcaseWriter.write( writer, dominantAndMergedSettings );
            result = new StringReader( writer.toString() );
        }
        catch ( IOException e )
        {
            throw new SetupMergeException( e.getMessage() );

        }
        catch ( XmlPullParserException e )
        {
            throw new SetupMergeException( e.getMessage() );

        }
        return result;
    }

    @Override
    protected String getPrototypeFilename()
    {
        return "clearcase-settings.xml";
    }

}