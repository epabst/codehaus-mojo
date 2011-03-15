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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.PomToolsRTException;
import org.codehaus.mojo.pomtools.PomToolsVersionException;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.ModelVersionRange;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

/** Wrapper library for Maven Artifact and Metadata routines.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a> * @version $Id$
 */
public class MetadataHelper
{
    private final RepositoryMetadataManager repositoryMetadataManager;

    private final ArtifactFactory artifactFactory;

    private final List remoteRepositories;

    private final ArtifactRepository localRepository;
    
    private final ArtifactMetadataSource metadataSource;
    
    private final ArtifactResolver artifactResolver;
    
    public MetadataHelper( MavenSession session,
                           List remoteArtifactRepositories, ArtifactRepository localRepository )
        throws PomToolsRTException
    {
        PlexusContainer container = session.getContainer();
        
        try
        {
            this.repositoryMetadataManager = (RepositoryMetadataManager) container
                .lookup( RepositoryMetadataManager.class.getName() );

            this.artifactFactory = (ArtifactFactory) container.lookup( ArtifactFactory.ROLE );
            
            this.metadataSource = (ArtifactMetadataSource) container.lookup( ArtifactMetadataSource.ROLE );
            
            this.artifactResolver = (ArtifactResolver) container.lookup( ArtifactResolver.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new PomToolsRTException( e );
        }
        
        this.remoteRepositories = remoteArtifactRepositories;
        this.localRepository = localRepository;
    }

    /** Simple wrapper for 
     * {@link ArtifactFactory#createArtifact(java.lang.String, java.lang.String, java.lang.String, 
     * java.lang.String, java.lang.String)}
     * 
     */
    public Artifact createArtifact( String groupId, String artifactId, String version, 
                                    String scope, String type )
    {
        return artifactFactory.createArtifact( groupId, artifactId, 
                                               StringUtils.defaultString( version, Artifact.LATEST_VERSION ), 
                                               scope, 
                                               StringUtils.defaultString( type, "jar" ) );
    }
    
    public Artifact createArtifact( ObjectWrapper obj )
    {
        if ( obj.getFields().get( "scope" ) != null )
        {
            return createArtifact( (String) obj.getFieldValue( "groupId" ),
                                   (String) obj.getFieldValue( "artifactId" ),
                                   (String) obj.getFieldValue( "version" ),
                                   (String) obj.getFieldValue( "scope" ),
                                   (String) obj.getFieldValue( "type" ) );
        }
        else
        {
            return createArtifact( (String) obj.getFieldValue( "groupId" ),
                                   (String) obj.getFieldValue( "artifactId" ),
                                   (String) obj.getFieldValue( "version" ),
                                   null,
                                   null );
        }
    }
    
    public RepositoryMetadata getMetadata( ObjectWrapper obj )
        throws ArtifactMetadataRetrievalException
    {
        return getMetadata( createArtifact( obj ) );
    }

    public RepositoryMetadata getMetadata( Artifact artifact )
        throws ArtifactMetadataRetrievalException
    {
        RepositoryMetadata metadata = new ArtifactRepositoryMetadata( artifact );
        try
        {
            repositoryMetadataManager.resolve( metadata, remoteRepositories, localRepository );

            return metadata;
        }
        catch ( RepositoryMetadataResolutionException e )
        {
            throw new ArtifactMetadataRetrievalException( "An error occured while resolving repository metadata", e );
        }
    }
    
    /** Returns whether the groupId and artifactId appear to be valid.
     * This differs from {@link #isValidDependencyArtifact(Artifact)} in that the version 
     * can be null or even invalid and still get a positive result
     * 
     * @param artifact
     * @return
     */public boolean isValidGroupIdArtifactId( Artifact artifact )
    {
        if ( isValidDependencyArtifact( artifact ) )
        {
            return true;
        }
        
        try
        {
            RepositoryMetadata metadata = getMetadata( artifact );
            
            return metadata.getMetadata().getVersioning() != null;
        }
        catch ( ArtifactMetadataRetrievalException e )
        {
            return false;
        }
        
    }
    
    /** Determines whether the artifact exists ( in that we are able to locate metadata for the groupId and artifactId)
     * and that the version or version range specified refers to a version specified in the repository metadata.  
     * 
     * @param artifact
     * @return
     */
    public boolean isValidDependencyArtifact( Artifact artifact )
    {
        try
        {
            ModelVersionRange versionRange = ModelVersionRange.createFromVersionSpec( 
                                                             artifact.getVersionRange().toString() );
            
            
            Metadata metadata = getMetadata( artifact ).getMetadata();
            
            if ( metadata.getVersioning() != null )
            {
                List availableVersions = metadataSource.retrieveAvailableVersions( artifact, localRepository, 
                                                                                   remoteRepositories );
                
                for ( Iterator iter = availableVersions.iterator(); iter.hasNext(); )
                {
                    ArtifactVersion version = (ArtifactVersion) iter.next();
                    
                    if ( versionRange.containsVersion( version ) )
                    {
                        return true;
                    }
                }
            }
            
            return ( metadata.getVersion() != null && versionRange.containsVersion( metadata.getVersion() ) );
        }
        catch ( InvalidArtifactRTException e )
        {
            return false;
        }       
        catch ( ArtifactMetadataRetrievalException e )
        {
            return false;
        }
        catch ( InvalidVersionSpecificationException e )
        {
            return false;
        }
    }
    
    /** Resolves all transitive dependencies for the current project and returns a list
     * of {@link TransitiveDependencyInfo} objects.  Each object represents a distinct 
     * groupId:artifactId:type dependency.  The {@link TransitiveDependencyInfo#getResolutionNodes()}
     * represent all of the possible ResolutionNodes which resolve to this groupId:artifactId.
     * 
     * @return
     * @throws PomToolsException
     */
    public List getTransitiveDependencies()
        throws PomToolsException, ProjectBuildingException
    {
        // Certain things like groupId or versions for dependencies may be declared in a parent
        // pom so we need to have maven fully resolve the model before walking the tree.
        MavenProject project = PomToolsPluginContext.getInstance().getActiveProject().getTemporaryResolvedProject();
        
        try
        {
            project.setDependencyArtifacts( project.createArtifacts( artifactFactory, null, null ) );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new PomToolsVersionException( "Unable to build project due to an invalid dependency version: " 
                                            + e.getMessage(), e );
        }
        
        Artifact projectArtifact = project.getArtifact();
            
        Set artifacts = project.getDependencyArtifacts();
        
        try
        {
            List dependencies = new ArrayList();
            
            ArtifactResolutionResult result;
            result = artifactResolver.resolveTransitively( artifacts, 
                                                           projectArtifact,
                                                           Collections.EMPTY_MAP,
                                                           localRepository,
                                                           remoteRepositories,
                                                           metadataSource, 
                                                           projectArtifact.getDependencyFilter() );
            
            Map dependencyMap = new HashMap();
            Set seen = new HashSet();
            
            // First build our map of distinct groupId:artifactIds
            for ( Iterator iter = result.getArtifactResolutionNodes().iterator(); iter.hasNext(); ) 
            {
                ResolutionNode node = (ResolutionNode) iter.next();
                
                TransitiveDependencyInfo info = new TransitiveDependencyInfo( node );
                
                dependencyMap.put( info.getKey(), info );
                
                dependencies.add( info );
            }
            
            // Now populate the map with all children
            recurseNode( dependencyMap, seen, result.getArtifactResolutionNodes().iterator(), 0 );
            
            return dependencies;
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new PomToolsException( e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new PomToolsException( e );
        }
     
    }
    
    protected void recurseNode( Map dependencyMap, Set seen, Iterator nodeIter, int depth )
        throws PomToolsException
    {
        while ( nodeIter.hasNext() )
        {
            ResolutionNode node = (ResolutionNode) nodeIter.next();
            
            if ( !seen.contains( node ) )
            {
                seen.add( node );
                
                TransitiveDependencyInfo info = (TransitiveDependencyInfo) dependencyMap.get( node.getKey() );
    
                // if we couldn't find the info in the map, then its not a dependency that we should 
                // care about because it wasn't returned to the top level dependency resolution.
                if ( info != null )
                {
                    info.addResolutionNode( node );
                    
                    if ( node.isResolved() )
                    {
                        recurseNode( dependencyMap, seen, node.getChildrenIterator(), depth + 1 );
                    }
                }
            }
        }
    }

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    public List getRemoteRepositories()
    {
        return remoteRepositories;
    }
}
