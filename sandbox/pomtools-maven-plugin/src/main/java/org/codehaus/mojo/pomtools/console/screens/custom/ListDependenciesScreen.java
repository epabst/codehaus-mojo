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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.screens.ConfirmYesNoScreen;
import org.codehaus.mojo.pomtools.console.screens.ScreenHelper;
import org.codehaus.mojo.pomtools.console.screens.editors.EditListScreen;
import org.codehaus.mojo.pomtools.console.screens.editors.EditObjectScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEventClosure;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableColumn;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableLayout;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.versioning.VersionInfo;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.DependencyWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.ModelVersionRange;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ListDependenciesScreen
    extends AbstractModelScreen
{
    private static final Class[] LIST_EDITOR_SIGNATURE = new Class[] {
        ListWrapper.class, int.class
    };
    
    private static final String KEY_LATEST_VERSION = "l";
    
    private static final String KEY_TOGGLE_SNAPSHOTS = "t";
    
    private static final String KEY_TRANSITIVE_DEPS = "v";
    
    private List depsNotAtLatest = new ArrayList();
    
    private boolean includeSnapshots = true;
    
    private final ListWrapper dependencies;
    
    private final List items;
    
    public ListDependenciesScreen( ObjectWrapper obj, BeanField field )
    {
        super( field.getFieldName() );
        
        dependencies = (ListWrapper) obj.getFieldValue( field );
        
        this.items = dependencies.getItems();
    }

    public ConsoleScreenDisplay getDisplay() throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer();
        
        String title = "Dependencies (" + ( includeSnapshots ? "Including" : "Not including" ) 
            + " snapshots in latest version determination)";
        
        sb.append( getHeader( title ) );
        
        if ( items != null && !items.isEmpty() )
        {
            TableLayout tab = new TableLayout( getTerminal(), new TableColumn[] {
                    new TableColumn( TableColumn.ALIGN_RIGHT, TableColumn.BOLD ),
                    TableColumn.ALIGN_LEFT_COLUMN,
                    TableColumn.ALIGN_LEFT_COLUMN,
                    TableColumn.ALIGN_LEFT_COLUMN } );
    
            depsNotAtLatest = new ArrayList();
            
            for ( int i = 0; i < items.size(); i++ )
            {
                ObjectWrapper wrappedObject = (ObjectWrapper) items.get( i );
                
                DependencyWrapper dep = new DependencyWrapper( wrappedObject );
                
                VersionInfo vinfo = dep.getLatestVersion( includeSnapshots );
                
                String strVersion = dep.getVersion();
                
                String versionDecorator = "";
                
                boolean usingResolvedVersion = false;
                if ( strVersion == null )
                {
                    // Look to the fully resolved model to see if perhaps the version was specified in a parent pom.
                    strVersion = dep.getResolvedVersion();
                    if ( strVersion != null )
                    {
                        usingResolvedVersion = true;
                        versionDecorator = " (inherited)";
                    }
                }
                
                ArrayList messages = new ArrayList();
                if ( !dep.isValidArtifact() )
                {
                    if ( dep.isValidGroupIdArtifactId() )
                    {
                        messages.add( "Error: Bad version specified" );
                    }
                    else
                    {
                        messages.add( "Error: Unknown artifact" );
                    }
                }                
                else if ( strVersion != null && !dep.isValidVersion( strVersion ) )
                {
                    messages.add( "Error: Invalid Version" );                    
                }
                else
                {
                    if ( vinfo != null )
                    {
                        ModelVersionRange depRange = dep.getResolvedVersionRange();
                        if ( !depRange.containsVersion( vinfo.getArtifactVersion() ) )
                        {
                            messages.add( "latest -> " + vinfo.getVersionString() );
                            if ( !usingResolvedVersion )
                            {
                                depsNotAtLatest.add( dep );
                            }
                        }
                    }
                }

                if ( getModelContext().isShowUnparsedVersions() && !dep.getUnparsedVersions().isEmpty() )
                {
                    messages.add( "(contains unparsable)" );
                }

                tab.add( new String[] {
                    numberPrompt( i + 1 ),
                    getModifiedLabel( dep.toString(), wrappedObject.isModified() ),
                    StringUtils.defaultString( strVersion, ModelHelper.NULL ) + versionDecorator,
                    StringUtils.join( messages.iterator(), ", " )
                    } );
            }
    
            sb.append( tab.getOutput() );
            
            OptionsPane options = new OptionsPane();
            options.add( KEY_NEW_ITEM, "Add new dependency" );
            
            options.add( KEY_TOGGLE_SNAPSHOTS, 
                     "Toggle resolve latest with snapshots. currently: " +  ( includeSnapshots ? "ON" : "OFF" ) );
            
            if ( !depsNotAtLatest.isEmpty() )
            {
                options.add( KEY_LATEST_VERSION, "Set all to latest the version" );
            }
            
            options.add( KEY_TRANSITIVE_DEPS, "View transitive dependencies" );

            sb.append( options.getOutput() );
            

            return createDisplay( sb.toString(), "Please select a dependency to configure" );
        }
        else 
        {
            sb.append( EditListScreen.NO_ITEMS );
            
            OptionsPane options = new OptionsPane();
            
            options.add( KEY_NEW_ITEM, "Add new dependency" );
            
            sb.append( options.getOutput() );
            
            return createDisplay( sb.toString(), "Select \"" + KEY_NEW_ITEM + "\" to add a new dependency" );
        }
    }
    
    protected void setAllToLatest()
    {
        for ( Iterator i = depsNotAtLatest.iterator(); i.hasNext(); )
        {
            DependencyWrapper dep = (DependencyWrapper) i.next();
            
            dep.setToLatestVersion( includeSnapshots );            
        }
        
        depsNotAtLatest.clear();
    }

    public ConsoleEventDispatcher getEventDispatcher() throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();
        
        ced.addFirst( new MatchingListener( KEY_NEW_ITEM, "Add a new dependency" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                dependencies.createItem( null );

                event.setNextScreen( getEditor( dependencies.size() - 1 ) );
            }
        } );
        
        if ( items != null && !items.isEmpty() )
        {
            if ( !depsNotAtLatest.isEmpty() )
            {
                String helpText = "Set all to the latest version available. "
                    + "Note that this will not change the versions which are inherited from a parent.";
                
                ced.addFirst( new MatchingListener( KEY_LATEST_VERSION, helpText )
                {
                    public void processEvent( ConsoleEvent event )
                        throws ConsoleExecutionException
                    {
                        ConsoleEventClosure yesClosure = new ConsoleEventClosure() {
                            public void execute( ConsoleEvent evt ) throws ConsoleExecutionException
                            {
                                setAllToLatest();
                            }
                        };
                        event.setNextScreen( new ConfirmYesNoScreen( "Are you sure you want to set all " 
                                                                     + "to the latest version?",
                                                                     yesClosure, null ) );
                    }

                } );
            }

            ced.addFirst( new MatchingListener( KEY_TRANSITIVE_DEPS, "View transitive dependencies" )
            {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    event.setNextScreen( new ListTransitiveDependenciesScreen() );
                }

            } );

            ced.addFirst( new MatchingListener( KEY_TOGGLE_SNAPSHOTS,
                                           "Toggle the use of snapshots when resolving the latest version." )
            {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    includeSnapshots = !includeSnapshots;
                }
            } );
            
            ced.addFirst( new NumericRangeListener( 1, items.size(), "Select a dependency to configure." )
            {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    int index = Integer.parseInt( event.getConsoleInput() ) - 1;
                    event.setNextScreen( getEditor( index ) );
                }
            } );
        }
        
        return ced;
    }
    
    private ConsoleScreen getEditor( int index )
    {
        ObjectWrapper obj = (ObjectWrapper) items.get( index );
        
        String editorClassName = ScreenHelper.getFieldEditorSetting( obj.getFullName() );
        if ( editorClassName != null )
        {
            return createEditorScreen( editorClassName, index );
        }
        else 
        {
            return new EditObjectScreen( dependencies, index );
        }
    }
    
    private ConsoleScreen createEditorScreen( String className, int index )
    {
        try
        {
            Constructor con = ConstructorUtils.getAccessibleConstructor( Class.forName( className ), 
                                                                         LIST_EDITOR_SIGNATURE );
            
            return (ConsoleScreen) con.newInstance( new Object[] { dependencies, new Integer( index ) } );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( InstantiationException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( InvocationTargetException e )
        {
            throw new ModelReflectionException( e );
        }
    }

}
