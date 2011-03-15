package org.codehaus.mojo.pomtools;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.pomtools.console.screens.custom.ListProjectsScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditObjectScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleApp;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreen;
import org.codehaus.mojo.pomtools.console.toolkit.LogOutputHandler;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.GenericTerminal;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.VT100Terminal;
import org.codehaus.plexus.components.interactivity.DefaultOutputHandler;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Provides console based editing of the project's pom.xml
 * as well as helpful information on transtitive dependencies
 *
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 * @aggregator
 * @goal console
 */
public class ConsoleMojo
    extends AbstractPomToolsMojo
{
    /**
     * Maven's default input handler
     * 
     * @component
     * @required
     * @readonly
     */
    private InputHandler inputHandler;


    /**
     * Output will be quasi formatted for an ANSI terminal instead of using 
     * Maven's default log output. Specify -DemulateTerminal=true on the command
     * line to enable this feature.
     *
     * @parameter expression="${emulateTerminal}" default-value="false" 
     */
    private boolean emulateTerminal;
    
    public void execute()
        throws MojoExecutionException
    {
        initialize();
        
        final PomToolsPluginContext modelContext = PomToolsPluginContext.getInstance();
        
        try
        {
            ConsoleApp app = new ConsoleApp( getTerminal(), new ConsoleApp.InitialState()
            {
                public ConsoleScreen getInitialScreen()
                {
                    if ( modelContext.getProjects().size() == 1 )
                    {
                        return new EditObjectScreen( modelContext.getActiveProject().getWrappedModel() );
                    }
                    else
                    {
                        return new ListProjectsScreen();
                    }
                }
            } );

            ConsoleApp.setCurrent( app );

            app.execute();
        }
        catch ( ConsoleExecutionException e )
        {
            throw new MojoExecutionException( "Model configuration failed", e );
        }

    }

    protected Terminal getTerminal()
        throws ConsoleExecutionException
    {
        try
        {
            if ( emulateTerminal )
            {
                // this is a very assumptive way to determine a VT100 compatible terminal
                DefaultOutputHandler outputHandler = new DefaultOutputHandler();
                outputHandler.initialize();
                return new VT100Terminal( inputHandler, outputHandler );
            }
            else
            {
                return new GenericTerminal( inputHandler, new LogOutputHandler( getLog() ) );
            }
        }
        catch ( InitializationException e )
        {
            throw new ConsoleExecutionException( "Error initializing terminal", e );
        }
    }
}
