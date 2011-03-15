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
import java.util.ListIterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.console.screens.editors.AbstractEditListItemScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.DefaultListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.helpers.TransitiveDependencyInfo;
import org.codehaus.mojo.pomtools.helpers.TreeNode;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TransitiveEndPointDetailScreen
    extends AbstractEditListItemScreen    
{
    private static final String TITLE = "Transitive Dependency Detail";
    
    private final String resolutionKey;
    
    private final ListTransitiveDependenciesScreen transListScreen;
    
    private List cachedDependencyNodes;
    
    public TransitiveEndPointDetailScreen( List containingList, int itemIndex, 
                                           ListTransitiveDependenciesScreen transListScreen )
        throws PomToolsException
    {
        super( TITLE, containingList, itemIndex );
        
        // this shouldn't change regardless of reloading the underlying data.
        TransitiveDependencyInfo resolutionInfo = (TransitiveDependencyInfo) getEditorObject(); 
        this.resolutionKey = resolutionInfo.getKey();
        
        this.transListScreen = transListScreen;
    }
    
    protected void setCurrentIndex( int index )
    {
        super.setCurrentIndex( index );
        
        this.cachedDependencyNodes = null;
    }
    
    private List getDependencyNodes() throws ConsoleExecutionException
    {
        if ( cachedDependencyNodes == null )
        {
            try
            {
                cachedDependencyNodes = ( (TransitiveDependencyInfo) getEditorObject() ).getInclusionTree()
                    .getChildren();
            }
            catch ( PomToolsException e )
            {
                throw new ConsoleExecutionException( e );
            }
        }
        
        
        return cachedDependencyNodes;
    }
    
    /** Returns whether this detail screen is operating on a single dependencyNode that has no children.
     * That would make it a direct project dependency.
     * @throws ConsoleExecutionException 
     * 
     */
    protected boolean isSingleDirectDependency()
        throws ConsoleExecutionException
    {
        return getDependencyNodes().size() == 1 && !( (TreeNode) getDependencyNodes().get( 0 ) ).hasChildren();
    }
    
    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        final TransitiveDependencyInfo resolutionInfo = (TransitiveDependencyInfo) getEditorObject();
    
        if ( resolutionInfo == null )
        {
            return getNoInfoDisplay();
        }

        List dependencyNodes = getDependencyNodes();
        
        TreeNode.Stringifier stringifier = new TreeNode.Stringifier() {
            
            public String getNodeLabel( TreeNode treeNode )
            {
                ResolutionNode node = (ResolutionNode) treeNode.getId();
                
                String label = ModelHelper.versionedKey( node.getArtifact() );
                
                if ( node.getArtifact().isOptional() )
                {
                    label += " (optional)";
                }
                
                if ( !treeNode.hasChildren() ) 
                {
                    // This is an endpoint node, so print it in bold
                    if ( treeNode.getId() == resolutionInfo.getSelectedNode() )
                    {
                        label = getTerminal().bold( label + " <<< (selected)" );
                    }
                    else
                    {
                        label = getTerminal().bold( label );
                    }
                }
                
                return label;
            }
        };
        
        StringBuffer sb = new StringBuffer( getHeader( TITLE ) );
        
        sb.append( "Artifact: " )
          .append( resolutionKey )
          .append( NEWLINE );
    
        sb.append( "Version: " )
          .append( resolutionInfo.getSelectedArtifact().getVersion() )
          .append( NEWLINE );
        
        if ( isSingleDirectDependency() )
        {
            // This is only 1 item in the list and it is a direct dependency from the project.
            // Don't give the user the option to exclude it.
            sb.append( "\nIncluded via explicit dependency: \n" )
              .append( ( (TreeNode) dependencyNodes.get( 0 ) ).toString( stringifier ) );
        }
        else
        {
            sb.append( "Paths:" )
              .append( NEWLINE );
          
            LabeledList lil = new LabeledList( getTerminal(), true, true );
            
            for ( ListIterator iter = dependencyNodes.listIterator(); iter.hasNext(); )
            {
                TreeNode tree = (TreeNode) iter.next();
                
                String treeOutput = tree.toString( stringifier );
                
                lil.add( numberPrompt( iter.nextIndex() ), treeOutput );
            }
    
            sb.append( lil.getOutput() );
        }
        
        return createDisplay( sb.toString(), "Select an item to add an explicit exclude to the dependency" );
    }
    
    protected ConsoleScreenDisplay getSingleItemDisplay( TreeNode node, TreeNode.Stringifier stringifier )
    {
        StringBuffer sb = new StringBuffer( getHeader( TITLE ) );
        
        sb.append( "There are no endpoints to display. You probably excluded all possible paths to:\n" 
                   + resolutionKey );
        
        return createDisplay( sb.toString(), PRESS_ENTER_TO_CONTINUE );
    }
    
    protected ConsoleScreenDisplay getNoInfoDisplay()
    {
        StringBuffer sb = new StringBuffer( getHeader( TITLE ) );
        
        sb.append( "There are no endpoints to display. You probably excluded all possible paths to:\n" 
                   + resolutionKey );
        
        return createDisplay( sb.toString(), PRESS_ENTER_TO_CONTINUE );
    }
    
    protected void reload() 
        throws ConsoleExecutionException
    {
        try
        {
            List infoList = transListScreen.reloadTransitiveDependencies();

            for ( ListIterator iter = infoList.listIterator(); iter.hasNext(); )
            {
                TransitiveDependencyInfo info = (TransitiveDependencyInfo) iter.next();

                if ( StringUtils.equals( info.getKey(), resolutionKey ) )
                {
                    setContainingList( infoList, iter.nextIndex() - 1 );
                    return;
                }
            }

            setContainingList( infoList, -1 );
        }
        catch ( ProjectBuildingException e )
        {
            // I don't think this should happen because we couldn't have navigated to this 
            // screen without passing model validation first.
            throw new ConsoleExecutionException( "Unable to reload transitive dependencies", e );
        }
        catch ( PomToolsException e )
        {
            throw new ConsoleExecutionException( "Unable to reload transitive dependencies", e );
        }
    }
    
    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();
        
        final TransitiveDependencyInfo resolutionInfo = (TransitiveDependencyInfo) getEditorObject();
        
        if ( resolutionInfo == null )
        {
            ced.addFirst( new DefaultListener() {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    event.setReturnToPreviousScreen();
                }
            } );
        }
        else if ( !isSingleDirectDependency() )
        {
            final List dependencyNodes = getDependencyNodes();
            
            ced.addFirst( new NumericRangeListener( 1, dependencyNodes.size(), 
                                                    "Select an item to add an explicit exclude to the dependency." )
            {
                public void processEvent( ConsoleEvent event )
                    throws ConsoleExecutionException
                {
                    int index = Integer.parseInt( event.getConsoleInput() ) - 1;
    
                    try
                    {
                        TreeNode treeNode = (TreeNode) dependencyNodes.get( index );
                        
                        if ( treeNode.hasChildren() )
                        {
                            
                            Artifact depArtifact = ( (ResolutionNode) treeNode.getId() ).getArtifact();
                            Artifact artifactToExclude = resolutionInfo.getSelectedNode().getArtifact();
                            
                            String excludedGroupId = artifactToExclude.getGroupId();
                            String excludedArtifactId = artifactToExclude.getArtifactId();
                            
                            if ( ModelHelper.addExclusionForNode( depArtifact, excludedGroupId, excludedArtifactId ) )
                            {
                                event.addConsoleMessage( "Exclusion " + ModelHelper.versionlessKey( excludedGroupId, 
                                                                                                excludedArtifactId ) 
                                                         + " was added to dependency\n" 
                                                         + ModelHelper.versionedKey( depArtifact ) );
                                
                                reload();
                            }
                            else
                            {
                                event.addConsoleMessage( "Exclusion was not added because it already existed." );
                            }
                        }
                        else
                        {
                            event.addConsoleMessage( "You cannot exclude a direct project dependency." );
                        }
                    }
                    catch ( PomToolsException e )
                    {
                        throw new ConsoleExecutionException( e );
                    }
                }
            } );
        }
        
        return ced;
    }

}
