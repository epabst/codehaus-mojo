package org.codehaus.mojo.pomtools.console.screens.custom;

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

import java.util.List;

import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditObjectScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.wrapper.custom.ProjectWrapper;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ListProjectsScreen
    extends AbstractModelScreen
{
    public ListProjectsScreen()
    {
        super( null );
    }

    public ConsoleScreenDisplay getDisplay() throws ConsoleExecutionException
    {
        List projects = getModelContext().getProjects();

        LabeledList lil = new LabeledList( getTerminal(), true, true );
        
        StringBuffer sb = new StringBuffer( getHeader( "Projects", false ) );
        for ( int i = 0; i < projects.size(); i++ )
        {
            ProjectWrapper project = (ProjectWrapper) projects.get( i );
            
            lil.add( numberPrompt( i + 1 ), getModifiedLabel( project.getValueLabel(), project.isModified() ) ); 
        }
        
        sb.append( lil.getOutput() );

        return createDisplay( sb.toString(), "Please select a project" );

    }

    public ConsoleEventDispatcher getEventDispatcher() throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();

        ced.addFirst( new NumericRangeListener( 1, getModelContext().getProjects().size(), 
                                                "Select a project to configure." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                getModelContext().setActiveProjectIndex( Integer.parseInt( event.getConsoleInput() ) - 1 );
                event.setNextScreen( new EditObjectScreen( getModelContext().getActiveProject()
                                                                      .getWrappedModel() ) );
            }
        } );

        return ced;
    }

}
