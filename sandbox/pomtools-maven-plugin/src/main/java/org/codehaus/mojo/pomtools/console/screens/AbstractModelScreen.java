package org.codehaus.mojo.pomtools.console.screens;

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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.console.screens.custom.ListTransitiveDependenciesScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleApp;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEventClosure;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.DefaultListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.validation.ProjectValidationResult;
import org.codehaus.mojo.pomtools.wrapper.custom.ProjectWrapper;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class AbstractModelScreen
    implements ConsoleScreen
{
    public static final String NULL_VALUE = "(null)";
    
    public static final String PRESS_ENTER_TO_CONTINUE = "Press <enter> to continue";
    
    protected static final String NEWLINE = "\n";
    
    private static final int DEFAULT_TERMINAL_WIDTH = 80;
    
    private int terminalWidth = DEFAULT_TERMINAL_WIDTH;
    
    private static final String ADDITIONAL_OPTIONS = "Additional options:" + NEWLINE;
    
    protected static final String KEY_NEW_ITEM = "n";
    
    protected static final String KEY_DELETE_ITEM = "d";
    
    protected static final String MODIFIED_LABEL = "**";
    
    private final String name;
    
    private String helpText;

    public AbstractModelScreen( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
    
    protected String getHeader()
    {
        return getHeader( null, true );
    }

    protected String getHeader( String s )
    {
        return getHeader( s, true );
    }

    protected String getFieldNameHeader( String fieldName )
    {
        return getHeader( "   Editing: " + fieldName );
    }
    
    protected String getHeader( String s, boolean printProjectName )
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "----------------------------------------------" 
                   + "------------------------------\n" );

        if ( printProjectName && getModelContext().getActiveProject() != null )
        {
            sb.append( "Project: " + getModelContext().getActiveProject().getValueLabel() )
              .append( NEWLINE );
        }

        if ( s != null )
        {
            sb.append( s )
              .append( NEWLINE );
        }

        sb.append( "----------------------------------------------------------------------------\n" );

        return sb.toString();
    }

    protected ConsoleScreenDisplay createDisplay( String contents, String prompt )
    {
        return createDisplay( contents, prompt, true );
    }
    
    protected ConsoleScreenDisplay createDisplay( String contents, String prompt, boolean includeHelp )
    {
        if ( prompt.endsWith( "." ) || prompt.endsWith( ":" ) )
        {
            prompt = prompt.substring( 0, prompt.length() - 1 );
        }
        
        if ( includeHelp )
        {
            prompt += " (? for help)";
        }
        return new ConsoleScreenDisplay( contents, prompt + ":", true );
    }
    
    protected String numberPrompt( int n )
    {
        return strPrompt( String.valueOf( n ) );
    }

    protected String strPrompt( String s )
    {
        return s + ")";
    }

    protected boolean isNumberInRange( String input, int min, int max )
    {
        try
        {
            int i = Integer.parseInt( input );
            return i >= min && i <= max;
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
    }

    protected boolean isModified()
    {
        return getModelContext().isModified();
    }
    
    /** Returns the supplied label with a "**" appended if the 
     * modified parameter is true.
     */
    protected static String decorateModifiedLabel( String label, boolean modified )
    {
        if ( modified )
        {
            return label + MODIFIED_LABEL;
        }
        else
        {
            return label;
        }
    }
    
    /** Returns a bold label if the terminal supports formatting and the item
     * is modified.  It the terminal does not support formatting, the value
     * is simply decorated with a "**"
     * 
     * @param label
     * @param modified
     * @return
     */
    protected String getModifiedLabel( String label, boolean modified )
    {
        if ( modified && getTerminal().supportsFormatting() )
        {
            return getTerminal().bold( decorateModifiedLabel( label, modified ) );
        }

        return decorateModifiedLabel( label, modified );
    }

    public ConsoleEventDispatcher getDefaultEventDispatcher()
        throws ConsoleExecutionException
    {
        return getDefaultEventManager( true );
    }

    public ConsoleEventDispatcher getDefaultEventManager( boolean includeDefaultListener )
    {
        final ConsoleScreen thisScreen = this;
        
        final ConsoleEventDispatcher ced = new ConsoleEventDispatcher();
        
        // Save listener 
        ced.add( new MatchingListener( new String[] { "save", "s" }, 
                                       "Save any modified poms.", "s[ave]" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                doSave( event );
            }
        } );
        
        // Revert listener 
        ced.add( new MatchingListener( new String[] { "revert" }, "Revert all projects to unmodified state." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                if ( isModified() )
                {
                    ConsoleEventClosure yesClosure = new ConsoleEventClosure()
                    {
                        public void execute( ConsoleEvent event ) 
                            throws ConsoleExecutionException
                        {
                            doRevert( event );
                            event.setReturnToFirstScreen();
                        }
                    };

                    event.setNextScreen( new ConfirmYesNoScreen( "Are you sure you want to revert your changes?", 
                                                                 yesClosure, null ) );
                }
                else
                {
                    event.addConsoleMessage( "Nothing is modified so there are no changes to revert." );
                }
            }
        } );
        
        // Validate Model Listener 
        ced.add( new MatchingListener( new String[] { "validate", "val" }, 
                                       "Validate the model for the current project.",
                                       "val[idate]" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                try
                {
                    ProjectValidationResult result = PomToolsPluginContext.getInstance().getActiveProject()
                        .validateModel();
                    
                    event.setNextScreen( new ModelValidationScreen( Collections.singletonList( result ) ) );
                }
                catch ( ProjectBuildingException e )
                {
                    event.setNextScreen( new ErrorMessageScreen( "Error validating project",
                                                                 "An exception occurred while validating the project: "
                                                                 + e.getProjectId()
                                                                 + NEWLINE + NEWLINE 
                                                                 + e.getMessage() ) );
                }
                catch ( PomToolsException e )
                {
                    throw new ConsoleExecutionException( "Unable to validate model", e );
                }
            }
        } );
        
        // Validate Model Listener 
        ced.add( new MatchingListener( "trans", "View transitive dependency information." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                event.setNextScreen( new ListTransitiveDependenciesScreen() );
            }
        } );

        // Quit listener
        ced.add( new MatchingListener( new String[] { "q", "quit", "x", "exit" }, "Quit", "q[uit]" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                if ( isModified() )
                {
                    ConsoleEventClosure yesClosure = new ConsoleEventClosure() {
                        public void execute( ConsoleEvent event ) throws ConsoleExecutionException
                        {
                            event.setExitApplication( true );
                        }
                    };
                    
                    event.setNextScreen( new ConfirmYesNoScreen( "You have unsaved changes. " 
                                                                 + "Are you sure you want to exit?",
                                                                 yesClosure,
                                                                 null
                                                                ) );
                }
                else
                {
                    event.setExitApplication( true );
                }
            }
        } );

//        // Previous screen
//        ced.add( new MatchingListener( new String[] { "prev", "p" }, 
//                                       "Return to previous screen.", "p[rev]" )
//        {
//            public void processEvent( ConsoleEvent event )
//                throws ConsoleExecutionException
//            {
//                event.setReturnToPreviousScreen( );
//            }
//        } );

        // Default listener
        if ( includeDefaultListener )
        {
            ced.add( new DefaultListener( "Return to previous screen" )
            {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    event.setReturnToPreviousScreen( );
                }
            } );
        }
        
        // Help listener 
        ced.add( new MatchingListener( new String[] { "?", "h", "help" }, "Display this help message." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                event.setNextScreen( new HelpScreen( thisScreen ) );
            }
        } );

        return ced;
    }

    protected void doSave( ConsoleEvent event )
        throws ConsoleExecutionException
    {
        if ( getModelContext().isModified() )
        {
            boolean failure = false;
            
            try
            {
                List saveResults = getModelContext().saveAllProjects();
                
                for ( Iterator i = saveResults.iterator(); i.hasNext(); )
                {
                    ProjectValidationResult result = (ProjectValidationResult) i.next();
                    if ( result.isValid() )
                    {
                        event.addConsoleMessage( result.getProject().getValueLabel() + " saved." );
                    }
                    else
                    {
                        event.addConsoleMessage( result.getProject().getValueLabel() 
                                                 + " was not saved due to validation errors." );
                        failure = true;
                    }
                }
                
                if ( failure )
                {
                    event.addConsoleMessage( "Error: Not all projects were saved.  Some projects did not validate." );
                    event.setNextScreen( new ModelValidationScreen( saveResults ) );
                }
            }
            catch ( ProjectBuildingException e )
            {
                event.setNextScreen( new ErrorMessageScreen( "Error saving project",
                                                             "Error: Not all projects were saved. Exception thrown " 
                                                             + "while validating: "
                                                             + e.getProjectId()
                                                             + NEWLINE + NEWLINE 
                                                             + e.getMessage() ) );
            }
            catch ( PomToolsException e )
            {
                throw new ConsoleExecutionException( e );
            }
        }
        else
        {
            event.addConsoleMessage( "No changes to save." );
        }
    }
    
    protected void doRevert( ConsoleEvent event )
        throws ConsoleExecutionException
    {
        try
        {
            List projectsReverted = getModelContext().revertAllProjects();
            
            for ( Iterator i = projectsReverted.iterator(); i.hasNext(); )
            {
                event.addConsoleMessage( ( (ProjectWrapper) i.next() ).getValueLabel() 
                                         + " reverted." );                    
            }
        }
        catch ( PomToolsException e )
        {
            throw new ConsoleExecutionException( e );
        }        
    }

    protected PomToolsPluginContext getModelContext()
    {
        return PomToolsPluginContext.getInstance();
    }

    public int getTerminalWidth()
    {
        return terminalWidth;
    }

    public void setTerminalWidth( int terminalWidth )
    {
        this.terminalWidth = terminalWidth;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public void setHelpText( String helpText )
    {
        this.helpText = helpText;
    }
    
    protected Terminal getTerminal()
    {
        return ConsoleApp.getCurrent().getTerminal();
    }

    /** Simple wrapper for LabeledList that prints the box below:
     * <p>
     *  Additional Options:
     *  x) Some text 
     * 
     */
    protected class OptionsPane
    {
        private LabeledList lil;
        
        public OptionsPane()
        {
            lil = new LabeledList( getTerminal(), true, true );
        }

        public LabeledList add( String label, String content )
        {
            return lil.add( strPrompt( label ), content );
        }

        public LabeledList addEmpty()
        {
            return lil.addEmpty();
        }

        public String getOutput()
        {
            StringBuffer sb = new StringBuffer();
            
            sb.append( NEWLINE )
              .append( getTerminal().bold( ADDITIONAL_OPTIONS ) );
            
            sb.append( lil.getOutput() );
            
            return sb.toString();
        }
        
    }
}
