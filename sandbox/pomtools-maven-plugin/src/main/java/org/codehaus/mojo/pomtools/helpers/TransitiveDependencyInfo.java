package org.codehaus.mojo.pomtools.helpers;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.versioning.DefaultVersionInfo;
import org.codehaus.mojo.pomtools.versioning.VersionInfo;
import org.codehaus.mojo.pomtools.wrapper.custom.ModelVersionRange;
import org.codehaus.plexus.util.StringUtils;

/** This class is used my {@link org.codehaus.mojo.pomtools.helpers.MetadataHelper#getTransitiveDependencies()}.
 * An instance is created for every possible transitive dependency (whether it is used or not). 
 * The {@link #getResolutionNodes()} represent each possible path that this groupId:artifactId:type 
 * are included into a project.  The "selectedNode" is the actual resolutionNode instance that maven
 * is using during a build.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TransitiveDependencyInfo
{
    private ResolutionNode selectedNode;
    
    private boolean hasConflicts = false;
    
    private final List resolutionNodes = new ArrayList();
    
    public TransitiveDependencyInfo( ResolutionNode node )
    {
        this.selectedNode = node;
    }
  
    public String toString()
    {
        return getKey();
    }
    
    public Artifact getSelectedArtifact()
    {
        return selectedNode.getArtifact();
    }

    public List getResolutionNodes()
    {
        Collections.sort( resolutionNodes, new Comparator() {
            public int compare( Object arg0, Object arg1 )
            {
                return ( (ResolutionNode) arg0 ).getDepth() - ( (ResolutionNode) arg1 ).getDepth();
            }
        } );
        
        return Collections.unmodifiableList( resolutionNodes );
    }
    
    /** Adds a resolution node to the list. If the artifact version 
     * from the new node differs from the artifact version of the "selectedNode",
     * this object is noted as having conflicting versions.
     * <p>
     * If the version strings do not match literally,
     * A version range is created and the item being added is
     * tested to see if it's range includes the "selectedNode" version.
     * If so, it is not considered a conflict. 
     * 
     * @param pathNode
     */
    public void addResolutionNode( ResolutionNode pathNode )
    {
        // If the pathNode isn't a direct project dependency, and it is optional, it will
        // never get included into the build so ignore it.
        if ( pathNode.getDepth() > 1 && pathNode.getArtifact().isOptional() )
        {
            return;
        }
        
        resolutionNodes.add( pathNode );
        
        if ( !StringUtils.equals( selectedNode.getArtifact().getVersion(),  pathNode.getArtifact().getVersion() ) )
        {
            ModelVersionRange newRange = new ModelVersionRange( pathNode.getArtifact().getVersionRange() );
            
            if ( newRange.hasRestrictions() )
            {
                hasConflicts = hasConflicts || !newRange.containsVersion( selectedNode.getArtifact().getVersion() );
            }
            else
            {
                // The newly added version isn't a range, so see if it is a snapshot build
                // Snapshots have their fully qualified build numbers appended to them at this point,
                // So 2 different snapshots can have different string equality, but still represent the same version. 
                // If it is a snapshot, then compare the release version of the string to the release version 
                // of the selectedNode.
                VersionInfo selectVersion = new DefaultVersionInfo( selectedNode.getArtifact().getVersion() );
                VersionInfo newVersion = new DefaultVersionInfo( pathNode.getArtifact().getVersion() );
                
                if ( selectVersion.isParsed() && newVersion.isParsed() )
                {
                    if ( selectVersion.isSnapshot() && newVersion.isSnapshot() )
                    {
                        hasConflicts = hasConflicts || !StringUtils.equals( selectVersion.getReleaseVersionString(), 
                                                                            newVersion.getReleaseVersionString() );
                    }
                    else
                    {
                        hasConflicts = true;
                    }
                }
                else
                {
                    hasConflicts = true;
                }
            }
        }
    }
    
    /** Returns the node used in the constructor which represents the
     * actual dependency that was resolved for the project.
     * 
     * @return
     */
    public ResolutionNode getSelectedNode()
    {
        return selectedNode;
    }

    public String getKey()
    {
        return (String) selectedNode.getKey();
    }
   
    /** Returns wheter any of the possible transitive dependencies
     * for this groupId:artifactId:type have a different version than
     * the "selectedNode" which maven is using for the build.
     */
    public boolean hasConflicts()
    {
        return hasConflicts;
    }

    /** Returns a sorted list of distinct versions from all of the ResolutionNodes
     * and the number of occurances.
     * 
     * @return a list of {@link VersionCount} objects
     */
    public List getDistinctVersionCounts()
    {
        List infoList = new ArrayList();
        List unparsedList = new ArrayList();
        CountMap countMap = new CountMap();
        
        // Create a list of VersionInfo objects so we can sort them.
        for ( Iterator i = resolutionNodes.iterator(); i.hasNext(); )
        {
            ResolutionNode node = (ResolutionNode) i.next();
            
            ModelVersionRange vRange = new ModelVersionRange( node.getArtifact().getVersionRange() );
            
            if ( vRange.hasRestrictions() )
            {
                if ( countMap.increment( vRange.toString() ) )
                {
                    unparsedList.add( vRange.toString() );
                }
            }
            else
            {
                VersionInfo vInfo = new DefaultVersionInfo( 
                                             StringUtils.defaultString( vRange.toString(), "(unknown)" ) );
                
                if ( vInfo.isParsed() )
                {
                    // Add it to our parsed list
                    if ( countMap.increment( vInfo.getVersionString() ) )
                    {
                        infoList.add( vInfo );
                    }
                }
                else
                {
                    // Add it to the unparsed list
                    if ( countMap.increment( vRange.toString() ) )
                    {
                        unparsedList.add( vRange.toString() );
                        
                        PomToolsPluginContext.getInstance().getLog().warn( "Dependency contained an unparsable " 
                                + "version: \"" + node.getArtifact().getVersion() + "\" " + node.getKey() );
                    }
                }
            }
        }
        
        List result = new ArrayList( infoList.size() );
        
        // Add all the unparsed versions 
        for ( Iterator iter = unparsedList.iterator(); iter.hasNext(); )
        {
            String version = (String) iter.next();
            
            result.add( new VersionCount( version, countMap.get( version ) ) );
        }
        
        // Add the sorted parsed versions
        Collections.sort( infoList );
        for ( Iterator iter = infoList.iterator(); iter.hasNext(); )
        {
            VersionInfo info = (VersionInfo) iter.next();
            
            result.add( new VersionCount( info.getVersionString(), countMap.get( info.getVersionString() ) ) );
        }
        
        
        return result;
    }

    /** Returns an object tree of all of the possible paths which can transitively include
     * this artifact. The root immediate children of the root node of the resulting tree will
     * be the directly included dependencies which lead to this artifact.
     * 
     * @return
     * @throws PomToolsException
     */
    public TreeNode getInclusionTree()
        throws PomToolsException
    {
        TreeNode tree = new TreeNode( null, new Comparator() {
            public int compare( Object arg0, Object arg1 )
            {
                ResolutionNode n0 = (ResolutionNode) arg0;
                ResolutionNode n1 = (ResolutionNode) arg1;
                
                return ( (Comparable) n0.getKey() ).compareTo( n1.getKey() );
            }
        } );

        for ( Iterator iter = getResolutionNodes().iterator(); iter.hasNext(); )
        {
            ResolutionNode node = (ResolutionNode) iter.next();

            List depTrail = APIWorkaroundHelper.getNodeLineage( node );

            TreeNode currentTree = tree;

            for ( Iterator trailIter = depTrail.iterator(); trailIter.hasNext(); )
            {
                // currentTree is reassigned each time to the tree of the newly added child
                currentTree = currentTree.addChild( trailIter.next() );
            }
        }
        
        if ( !tree.hasChildren() ) 
        {
            // this shouldn't happen
            throw new PomToolsException( "Unable to determine lineage of transitive dependency" );
        }
        
        return tree.getSingleChild();        
    }
        
    public static class VersionCount
    {
        private final String version;
        private final int count;
        
        VersionCount( String version, int count )
        {
            this.version = version;
            this.count = count;
        }

        public int getCount()
        {
            return count;
        }

        public String getVersion()
        {
            return version;
        }
    }
    
    
    /** Simple helper class which assists in incrementing an integer for each key.
     */
    private static class CountMap
    {
        private final Map counts = new HashMap();
        
        public CountMap()
        {
        }

        /** Adds or Increments the Integer in the map for the key.
         * 
         * @param countMap
         * @param key
         * @return true if the item was created rather than updated
         */
        public boolean increment( Object key )
        {
            if ( counts.containsKey( key ) )
            {
                Integer cnt = (Integer) counts.get( key );
                counts.put( key, new Integer( cnt.intValue() + 1 ) );
                
                return false;
            }
            else
            {
                counts.put( key, new Integer( 1 ) );
                return true;
            }   
        }
        
        public int get( Object key )
        {
            Integer result = (Integer) counts.get( key );
            
            if ( result == null )
            {
                throw new IllegalArgumentException( "Key was not found in the map: " + key.toString() );
            }
            
            return result.intValue();
        }
    }

}
