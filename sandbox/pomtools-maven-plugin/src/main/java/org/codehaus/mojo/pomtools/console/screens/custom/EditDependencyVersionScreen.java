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

import java.util.Iterator;

import org.codehaus.mojo.pomtools.PomToolsVersionException;
import org.codehaus.mojo.pomtools.console.screens.editors.EditVersionScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableColumn;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableLayout;
import org.codehaus.mojo.pomtools.versioning.VersionInfo;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.DependencyWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.ModelVersionRange;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditDependencyVersionScreen
    extends EditVersionScreen
{
    private static final String KEY_VERSION = "v";
    private static final String KEY_LBOUND = "l";
    private static final String KEY_UBOUND = "u";
    private static final String KEY_MANUAL_EDIT = "m";
    
    private static final String[] KEYS = new String[] {
        KEY_VERSION, KEY_LBOUND, KEY_UBOUND, KEY_MANUAL_EDIT
    };
    
    private static final int STATE_SELECT_VERSION   = 0;
    private static final int STATE_SELECT_LBOUND    = 1;
    private static final int STATE_SELECT_UBOUND    = 2;
    
    private static final String[] STATE_ADDL_OPTIONS = new String[] {
        "Select a single version",
        "Select a lower bound for version range",
        "Select an upper bound for version range",
    };
                                                             
    private static final String[] STATE_PROMPTS = new String[] {
        "Please select a version",
        "LOWER BOUND: Please select a version for the lower bound",
        "UPPER BOUND: Please select a version for the upper bound",
    };
    
    
    private int state = STATE_SELECT_VERSION;
    
    private final DependencyWrapper dep;
    
    public EditDependencyVersionScreen( ObjectWrapper editorObject, BeanField field )
    {
        super( editorObject, field );
        
        this.dep = new DependencyWrapper( editorObject );
    }
    
    protected void setState( int state ) 
    {
        this.state = state;
    }
    
    protected boolean isVersionsAvailable()
    {
        try 
        {
            return dep.getParsedVersions().size() > 0;
        }
        catch ( PomToolsVersionException e )
        {
            return false;
        }
    }
    
    protected ConsoleScreenDisplay getSelectFromListDisplay() throws ConsoleExecutionException
    {
            StringBuffer sb = new StringBuffer();
            sb.append( getHeader( "Dependency: " + dep.toString() + " " 
                                  + StringUtils.defaultString( dep.getVersionRange() ) ) );
            
        // Display the list of available versions
        int index = 0;
        TableLayout ctab = new TableLayout( getTerminal(), new TableColumn[] {
            new TableColumn( TableColumn.ALIGN_RIGHT, TableColumn.BOLD ),
            TableColumn.ALIGN_LEFT_COLUMN,
            TableColumn.ALIGN_LEFT_COLUMN
        } );
        
        for ( Iterator i = dep.getParsedVersions().iterator(); i.hasNext(); )
        {
            VersionInfo info = (VersionInfo) i.next();
            ModelVersionRange depRange = dep.getVersionRange();

            String comment = null;
            if ( depRange != null )
            {
                if ( depRange.containsVersion( info.getArtifactVersion() ) )
                {
                    comment = depRange.hasRestrictions() ? "(in range)" : "(current)";
                }
            }

            ctab.add( new String[] { numberPrompt( ++index ), // the number for selection
                info.getVersionString(), // the version
                comment // any information about the version
                } );
        }
    
        sb.append( ctab.getOutput() );
        sb.append( NEWLINE );
        
        // Unparsed Versions: List any unparsed versions
        if ( !dep.getUnparsedVersions().isEmpty() )
        {
            final int maxRightColumnWidth = 60;
            
            ctab = new TableLayout( getTerminal(), new TableColumn[] {
                TableColumn.ALIGN_LEFT_COLUMN,
                new TableColumn( maxRightColumnWidth )
            } );
            
            ctab.add( new String[] { "Unparsed versions:", 
                "(" + StringUtils.join( dep.getUnparsedVersions().iterator(), ", " ) + ")" } );
            
            sb.append( ctab.getOutput() );
            sb.append( NEWLINE );
        }
        
        // Additional options:
        OptionsPane options = new OptionsPane();
        for ( int i = 0; i < STATE_ADDL_OPTIONS.length; i++ )
        {
            if ( this.state != i )
            {
                options.add( KEYS[i], STATE_ADDL_OPTIONS[i] );
            }
            
        }
        
        sb.append( options.getOutput() );
        
        return createDisplay( sb.toString(), STATE_PROMPTS[ state ] );
    }
    
    
    protected ConsoleEventDispatcher getSelectFromListDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();
        
        ced.addFirst( getStateChangeListener( new String[] { KEY_UBOUND, "upper" },
                                                             STATE_ADDL_OPTIONS[ STATE_SELECT_UBOUND ],
                                                             STATE_SELECT_UBOUND ) )
            .addFirst( getStateChangeListener( new String[] { KEY_LBOUND, "lower" },
                                                              STATE_ADDL_OPTIONS[ STATE_SELECT_LBOUND ],
                                                              STATE_SELECT_LBOUND ) )
            .addFirst( getStateChangeListener( new String[] { KEY_VERSION, "version" },
                                                              STATE_ADDL_OPTIONS[ STATE_SELECT_VERSION ], 
                                                              STATE_SELECT_VERSION ) )
            .addFirst( getNumberChoiceListener() );
          
        return ced;        
    }
    
    protected ConsoleEventListener getNumberChoiceListener()
    {
        return new NumericRangeListener( 1, dep.getParsedVersions().size(), 
                                         "Select a number corresponding to a version." ) {

            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                VersionInfo selectedVersion = (VersionInfo) dep.getParsedVersions()
                    .get( Integer.parseInt( event.getConsoleInput() ) - 1 );

                boolean success = false;

                switch ( state )
                {
                    case STATE_SELECT_VERSION:
                        dep.setVersion( selectedVersion.getVersionString() );
                        success = true;
                        break;
                    case STATE_SELECT_LBOUND:
                        success = dep.setVersionLowerBound( "[" + selectedVersion.getVersionString() + ",]" );
                        break;
                    case STATE_SELECT_UBOUND:
                        success = dep.setVersionUpperBound( "[," + selectedVersion.getVersionString() + "]" );
                        break;
                    default:
                        throw new IllegalStateException( "Unknown state: " + state );
                }
                
                if ( !success ) 
                {
                    event.addConsoleMessage( "The option you specified produced an invalid version specification. " 
                                             + "Please try again." );
                }
                
            }
            
        };
    }
    
    protected ConsoleEventListener getStateChangeListener( String[] matches, String description, final int newState )
    {
        return new MatchingListener( matches, description ) {

            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                setState( newState );
            }
        };
    }

}
