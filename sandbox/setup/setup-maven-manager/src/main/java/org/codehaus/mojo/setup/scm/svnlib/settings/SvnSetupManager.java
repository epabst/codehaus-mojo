package org.codehaus.mojo.setup.scm.svnlib.settings;

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
import org.apache.maven.scm.provider.svn.util.SvnUtil;
import org.apache.maven.scm.providers.svn.settings.Settings;
import org.apache.maven.scm.providers.svn.settings.io.xpp3.SvnXpp3Reader;
import org.apache.maven.scm.providers.svn.settings.io.xpp3.SvnXpp3Writer;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.SetupMergeException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author Robert Scholte
 * @since 1.0.0
 * @plexus.component role="org.codehaus.mojo.setup.SetupManager" role-hint="svn"
 */
public final class SvnSetupManager
    extends AbstractSetupManager
{
    private SvnXpp3Reader svnReader = new SvnXpp3Reader();

    private SvnXpp3Writer svnWriter = new SvnXpp3Writer();

    @Override
    public File getTargetFile( MavenSession session )
    {
        return SvnUtil.getSettingsFile();
    }

    @Override
    protected Reader merge( Reader dominantReader, Reader recessiveReader )
        throws SetupMergeException
    {
        Reader result = null;
        try
        {
            Settings dominantAndMergedSettings = svnReader.read( dominantReader );
            Settings recessiceSettings = svnReader.read( recessiveReader );

            SettingsUtils.merge( dominantAndMergedSettings, recessiceSettings );

            Writer writer = new StringWriter();
            svnWriter.write( writer, dominantAndMergedSettings );
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
        return "svn-settings.xml";
    }
}
