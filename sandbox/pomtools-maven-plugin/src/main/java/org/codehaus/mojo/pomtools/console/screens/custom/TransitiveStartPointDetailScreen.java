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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.codehaus.mojo.pomtools.console.screens.editors.AbstractEditListItemScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;
import org.codehaus.mojo.pomtools.helpers.TransitiveDependencyInfo;
import org.codehaus.mojo.pomtools.helpers.TreeNode;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TransitiveStartPointDetailScreen
    extends AbstractEditListItemScreen
{
    private static final String KEY_OPTIONAL_TOGGLE = "t";
    
    private static final String TITLE = "Transitive Dependency Include Tree";
    
    private static final String HELP_TEXT = 
        "The starting point tree shows the artifacts which are included as transitive dependencies "
        + "by including the root node dependency. A tree with a single node denotes a dependency "
        + "without any child dependencies. Inactive items are dependencies that are not included "
        + "in the classpath during compilation. (they are probably included elsewhere)";
    
    private boolean showInactive = false;
    
    public TransitiveStartPointDetailScreen( List containingList, int itemIndex )
        throws ConsoleExecutionException
    {
        super( TITLE, containingList, itemIndex );
    }

    public String getHelpText()
    {
        return HELP_TEXT;
    }
    
    public ConsoleScreenDisplay getDisplay() throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer( getHeader( TITLE ) );
        
        sb.append( NEWLINE );
        
        TreeNode tree = new TreeNode( null, new Comparator() {

            public int compare( Object arg0, Object arg1 )
            {
                ResolutionNode n0 = (ResolutionNode) arg0;
                ResolutionNode n1 = (ResolutionNode) arg1;
                
                return ( (Comparable) n0.getKey() ).compareTo( n1.getKey() );
            }
            
        } );

        TransitiveDependencyInfo resolutionInfo = (TransitiveDependencyInfo) getEditorObject();
        
        recurseNode( tree, resolutionInfo.getSelectedNode() );
        
        String treeOutput = tree.getSingleChild().toString( new TreeNode.Stringifier() {

            public String getNodeLabel( TreeNode treeNode )
            {
                ResolutionNode node = (ResolutionNode) treeNode.getId();
                StringBuffer sb = new StringBuffer();
                
                Artifact artifact = node.getArtifact();
                
                // Only print bold if we are also showing inactive
                if ( node.isActive() && showInactive )
                {
                    sb.append( getTerminal().bold( artifact.toString() ) );
                }
                else
                {
                    sb.append( artifact.toString() );
                    
                    if ( !getTerminal().supportsFormatting() )
                    {
                        sb.append( " (inactive)" );
                    }
                }
                
                return sb.toString();
            }
            
        } );
        

        sb.append( treeOutput );
        
        OptionsPane options = getOptionsPane( false );
        
        options.add( KEY_OPTIONAL_TOGGLE, "Toggle display of inactive dependencies. currently: " 
                 +  ( showInactive ? "ON" : "OFF" ) );

        sb.append( options.getOutput() );
        
        return createDisplay( sb.toString(), PRESS_ENTER_TO_CONTINUE );
    }
    
    private void recurseNode( TreeNode tree, ResolutionNode node )
    {
        TreeNode childTree = tree.addChild( node );
        
        if ( node.isResolved() )
        {
            for ( Iterator iter = node.getChildrenIterator(); iter.hasNext(); )
            {
                ResolutionNode childNode = (ResolutionNode) iter.next();
                
                if ( showInactive || childNode.isActive() )
                {
                    recurseNode( childTree, childNode );
                }
            }
        }
        
    }
    
    public ConsoleEventDispatcher getEventDispatcher() throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = super.getDefaultEventDispatcher();
        
        ced.addFirst( new MatchingListener( new String[] { "t", "toggle" },
                                            "Toggle the display of inactive dependencies.",
                                            "t[oggle]" )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                showInactive = !showInactive;
            }
        } );
        
        return ced;
    }

}
