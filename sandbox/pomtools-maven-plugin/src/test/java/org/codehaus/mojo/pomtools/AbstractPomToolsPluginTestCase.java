package org.codehaus.mojo.pomtools;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.pomtools.config.PomToolsConfig;
import org.codehaus.plexus.PlexusTestCase;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class AbstractPomToolsPluginTestCase
    extends PlexusTestCase
{
    public AbstractPomToolsPluginTestCase()
    {
        super();
    }

    protected PomToolsConfig getPluginConfiguration()
        throws MojoExecutionException
    {
        return ConsoleMojo.readConfiguration();
    }
    
    protected void setUp()
        throws Exception
    {
        super.setUp();

        Log log = new SystemStreamLog();
        
        PomToolsPluginContext modelContext = new PomToolsPluginContext( null, null, null, 
                                                                  getPluginConfiguration(), true, log );

        PomToolsPluginContext.setInstance( modelContext );
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        
        PomToolsPluginContext.setInstance( null );
    }
    
    
}
