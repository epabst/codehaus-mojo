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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.screens.ErrorMessageScreen;
import org.codehaus.mojo.pomtools.console.screens.HelpScreen;
import org.codehaus.mojo.pomtools.console.screens.ModelValidationScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableColumn;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableLayout;
import org.codehaus.mojo.pomtools.helpers.MetadataHelper;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.helpers.TransitiveDependencyInfo;
import org.codehaus.mojo.pomtools.validation.ProjectValidationResult;
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ListTransitiveDependenciesScreen
    extends AbstractModelScreen
{
    private static final String TITLE = "Transitive Dependencies";
    
    private static final String KEY_TOGGLE_POINTS = "t";
    
    private List startpointDeps;
    
    private List transitiveDeps;
    
    private boolean displayEndpoints = false;
    
    private ConsoleScreen errorScreen;
    
    public ListTransitiveDependenciesScreen()
    {
        super( TITLE );        
    }
    
    public String getHelpText()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( "Displays a list of both direct and transitive dependencies for this project.\n" )
          .append( "You can toggle between viewing starting points and end points.\n\n" );
        
        TableColumn rightColumn = new TableColumn();
        rightColumn.setMaxWidth( HelpScreen.HELP_TEXT_MAX_WIDTH - "Starting Point:  ".length() );
        
        TableLayout tab = new TableLayout( getTerminal(), 
                                           new TableColumn[] { TableColumn.ALIGN_LEFT_COLUMN,
                                                               rightColumn }, HelpScreen.HELP_TEXT_MAX_WIDTH );
        
        
        tab.add( "Starting Point:", "A direct dependency of this project. Listed in the <dependencies> " 
                 + "section of the pom. Selecting an item from the list in this mode will display a "
                 + "tree of transitive dependencies that this item brings into the project." );
        
        tab.addEmptyRow();
        
        tab.add( "End Point:", "A dependency that is included directly or indirectly by "
                 + "this project. Selecting an item from the list will display all "
                 + "of the possible paths which include this artifact. Conflicts, if any, are noted as a "
                 + "comma delimited list of versions along with the number of paths that lead to "
                 + "that version." );
        
        sb.append( tab.getOutput() );
        
        return sb.toString();
    }

    private List getEndPointDependencies()
        throws PomToolsException, ProjectBuildingException
    {
        if ( transitiveDeps == null )
        {
            MetadataHelper metaHelper = getModelContext().getMetadataHelper();
            
            this.transitiveDeps = metaHelper.getTransitiveDependencies();
            
            Collections.sort( this.transitiveDeps, new Comparator() {
                public int compare( Object arg0, Object arg1 )
                {
                    String key0 = ( (TransitiveDependencyInfo) arg0 ).getKey();
                    String key1 = ( (TransitiveDependencyInfo) arg1 ).getKey();
                    
                    return key0.compareTo( key1 );                        
                }
            } );
        }
        
        return this.transitiveDeps;
    }
    
    List reloadTransitiveDependencies()
        throws PomToolsException, ProjectBuildingException
    {
        this.transitiveDeps = null;
        return getEndPointDependencies();
    }
    
    private List getStartPointDependencies()
        throws PomToolsException, ProjectBuildingException
    {
        if ( startpointDeps == null )
        {
            List allDeps = getEndPointDependencies();
            
            startpointDeps = new ArrayList();
            
            for ( Iterator iter = allDeps.iterator(); iter.hasNext(); )
            {
                TransitiveDependencyInfo info = (TransitiveDependencyInfo) iter.next();
                
                if ( info.getSelectedNode().getDepth() == 1 )
                {
                    startpointDeps.add( info );
                }
            }            
        }
        
        return this.startpointDeps;
    }
    
    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        if ( this.errorScreen != null )
        {
            return errorScreen.getDisplay();
        }
    
        try
        {
            return ( displayEndpoints ) ? getEndPointDisplay() : getStartPointDisplay();
        }
        catch ( Exception e )
        {
           return getErrorScreen( e ).getDisplay();
        }
    }
    
    protected ConsoleScreen getErrorScreen( Exception e )
        throws ConsoleExecutionException
    {
        if ( e instanceof InvalidProjectModelException )
        {
            ProjectValidationResult val = new ProjectValidationResult( PomToolsPluginContext.getInstance()
                .getActiveProject(), ( (InvalidProjectModelException) e ).getValidationResult() );

            this.errorScreen = new ModelValidationScreen( Collections.singletonList( val ),
                                                          "Unable to resolve transitive dependencies due to the " 
                                                          + "following validation errors:" );
            
            return this.errorScreen;
        }
        else if ( e instanceof PomToolsException )
        {
            this.errorScreen = new ErrorMessageScreen( "Error resolving transitive dependencies", 
                                                       "Unable to resolve transitive dependencies due to the " 
                                                       + "following error:\n\n"
                                                       + e.getCause().getMessage() );
            
            return this.errorScreen;
        }
        else
        {
            throw new ConsoleExecutionException( e );
        }
    }
    
    public ConsoleScreenDisplay getEndPointDisplay() 
        throws PomToolsException, ProjectBuildingException
    {
        StringBuffer sb = new StringBuffer( getHeader( TITLE + " [End Points]" ) );

        LabeledList lil = new LabeledList( getTerminal(), true, true );
        
        Terminal term = getTerminal();
        
        for ( ListIterator iter = getEndPointDependencies().listIterator(); iter.hasNext(); )
        {
            TransitiveDependencyInfo info = (TransitiveDependencyInfo) iter.next();
            
            StringBuffer conflictSb = new StringBuffer();
            if ( info.hasConflicts() )
            {
                conflictSb.append( "\n    " + term.bold( "Conflicts: " ) );
                
                List tmp = new ArrayList();
                for ( Iterator vi = info.getDistinctVersionCounts().iterator(); vi.hasNext(); )
                {
                    TransitiveDependencyInfo.VersionCount vcount = (TransitiveDependencyInfo.VersionCount) vi.next();
                    
                    tmp.add( term.underline( vcount.getVersion() ) + "(" + vcount.getCount() + ")" );
                }
                conflictSb.append( StringUtils.join( tmp.iterator(), ", " ) );
            }
            
            lil.add( numberPrompt( iter.nextIndex() ),
                     ModelHelper.versionedKey( info.getSelectedArtifact() ) + conflictSb.toString() );
        }
        
        sb.append( lil.getOutput() );
        
        addAdditionalOptions( sb );
        
        return createDisplay( sb.toString(), "Select an item to view its detail." );
    }
    
    protected void addAdditionalOptions( StringBuffer sb )
    {
        OptionsPane options = new OptionsPane();
        
        options.add( KEY_TOGGLE_POINTS, "Toggle display to view " 
                 + ( displayEndpoints ? "start" : "end" ) + " points" );

        sb.append( options.getOutput() );
    }
    
    public ConsoleScreenDisplay getStartPointDisplay() 
        throws ConsoleExecutionException, PomToolsException, ProjectBuildingException
    {
        StringBuffer sb = new StringBuffer( getHeader( TITLE + " [Start Points]" ) );
        
        LabeledList lil = new LabeledList( getTerminal(), true, true );
        
        for ( ListIterator iter = getStartPointDependencies().listIterator(); iter.hasNext(); )
        {
            TransitiveDependencyInfo info = (TransitiveDependencyInfo) iter.next();
         
            lil.add( numberPrompt( iter.nextIndex() ), ModelHelper.versionedKey( info.getSelectedArtifact() ) );
        }
        
        sb.append( lil.getOutput() );
        
        addAdditionalOptions( sb );
        
        return createDisplay( sb.toString(), "Select an item to view its detail." );
    }

    public ConsoleEventDispatcher getEventDispatcher() throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();
        
        final List currentList;
        try
        {
            currentList = ( displayEndpoints ) ? getEndPointDependencies() : getStartPointDependencies();
        }
        catch ( Exception e )
        {
            return getErrorScreen( e ).getEventDispatcher();
        }
        
        ced.addFirst( new MatchingListener( new String[] { "t", "toggle" },
                                            "Toggle between start points and end points.",
                                            "t[oggle]" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                displayEndpoints = !displayEndpoints;
            }
        } );
        
        final ListTransitiveDependenciesScreen listScreen = this;
        
        ced.addFirst( new NumericRangeListener( 1, currentList.size(), 
                                                "Select an item to view its detail" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                int index = Integer.parseInt( event.getConsoleInput() ) - 1;
                
                try
                {
                    if ( displayEndpoints )
                    {
                        event.setNextScreen( new TransitiveEndPointDetailScreen( currentList, index, listScreen ) );
                    }
                    else
                    {
                        event.setNextScreen( new TransitiveStartPointDetailScreen( currentList, index ) );
                    }
                }
                catch ( PomToolsException e )
                {
                    throw new ConsoleExecutionException( e );
                }
            }
        } );
        
        

        return ced;
    }

}
