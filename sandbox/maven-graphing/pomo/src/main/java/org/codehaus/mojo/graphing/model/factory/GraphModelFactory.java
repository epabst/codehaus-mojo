package org.codehaus.mojo.graphing.model.factory;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.repository.layout.LegacyRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.artifact.resolver.filter.TypeArtifactFilter;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.codehaus.mojo.graphing.model.Edge;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.Node;

public class GraphModelFactory
{
    private ArtifactFactory artifactFactory;

    private Map artifactLayouts;

    private ArtifactRepositoryFactory artifactRepositoryFactory;

    private ArtifactResolver artifactResolver;

    private ArtifactRepository localRepository;

    private Log log;

    private MavenMetadataSource mavenMetadataSource;

    private MavenProject project;

    private String scopeFilter;

    private String typeFilter;

    private boolean verbose;
    
    public GraphModelFactory( Log logger )
    {
        this.log = logger;
        this.verbose = false;
        this.artifactLayouts = new HashMap();

        this.artifactLayouts.put( "default", new DefaultRepositoryLayout() );
        this.artifactLayouts.put( "legacy", new LegacyRepositoryLayout() );
    }

    public ArtifactFactory getArtifactFactory()
    {
        return artifactFactory;
    }

    private ArtifactRepositoryLayout getArtifactLayout( String layoutid )
    {

        ArtifactRepositoryLayout layout = (ArtifactRepositoryLayout) this.artifactLayouts.get( layoutid );
        if ( layout == null )
        {
            layout = new DefaultRepositoryLayout();
        }
        return layout;
    }

    private ArtifactRepositoryPolicy getArtifactPolicy( RepositoryPolicy policy )
    {
        if ( policy == null )
        {
            return null;
        }

        ArtifactRepositoryPolicy artipolicy = new ArtifactRepositoryPolicy();

        artipolicy.setChecksumPolicy( policy.getChecksumPolicy() );
        artipolicy.setEnabled( policy.isEnabled() );
        artipolicy.setUpdatePolicy( policy.getUpdatePolicy() );

        return artipolicy;
    }

    private List getArtifactRepositories()
    {
        Iterator it;
        List artifactRepositories = new ArrayList();

        it = project.getRepositories().iterator();
        while ( it.hasNext() )
        {
            Repository repo = (Repository) it.next();

            artifactRepositories.add( toArtifactRepository( repo ) );
        }

        return artifactRepositories;
    }

    public ArtifactRepositoryFactory getArtifactRepositoryFactory()
    {
        return artifactRepositoryFactory;
    }

    public ArtifactResolver getArtifactResolver()
    {
        return artifactResolver;
    }

    public GraphModel getGraphModel( String groupId, String artifactId, String version)
        throws MojoExecutionException
    {
        Artifact pomArtifact = resolveArtifact( groupId, artifactId, version );
        // Model pom = getModel(pomArtifact);

        List listeners = Collections.EMPTY_LIST;
        if ( verbose )
        {
            listeners = Collections.singletonList( new DebugResolutionListener( getLog() ) );
        }

        List remoteArtifactRepositories = getArtifactRepositories();

        // TODO: managed dependencies
        Map managedDependencies = Collections.EMPTY_MAP;

        ArtifactFilter filter = null;
        if ( scopeFilter != null )
        {
            filter = new ScopeArtifactFilter( scopeFilter );
        }
        if ( typeFilter != null )
        {
            TypeArtifactFilter typeArtifactFilter = new TypeArtifactFilter( typeFilter );
            if ( filter != null )
            {
                AndArtifactFilter andFilter = new AndArtifactFilter();
                andFilter.add( filter );
                andFilter.add( typeArtifactFilter );
                filter = andFilter;
            }
            else
            {
                filter = typeArtifactFilter;
            }
        }

        ArtifactResolutionResult result;
        Set artifacts;

        GraphModel model = new GraphModel();
        Node centerNode = toNode(pomArtifact);
        model.addNode(centerNode);
        model.setCenterNode(centerNode);
        
        try
        {
            artifacts = new HashSet();
            artifacts.add( pomArtifact );
            
            result = artifactResolver.resolveTransitively( artifacts, pomArtifact, managedDependencies,
                                                           localRepository, remoteArtifactRepositories,
                                                           mavenMetadataSource, filter, listeners );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Unable to resolve deps.", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to resolve deps.", e );
        }
        
        getLog().info("Got " + result.getArtifactResolutionNodes().size() + " resolution node(s).");
        
        Iterator it = result.getArtifactResolutionNodes().iterator();
        while(it.hasNext()) {
            ResolutionNode child = (ResolutionNode) it.next();
            Node childNode = toNode(child.getArtifact());
            Edge edge = new Edge(centerNode, childNode);
            if(model.addEdge(edge)) {
                addChildEdges(model, child);
            }
        }

        return model;
    }
    
    private Artifact resolveArtifact(String groupId, String artifactId, String version) {
        Artifact artifact = artifactFactory.createProjectArtifact(groupId, artifactId, version);
        if(!artifact.isResolved()) {
            try
            {
                artifactResolver.resolve(artifact, getArtifactRepositories(), localRepository);
            }
            catch ( ArtifactResolutionException e )
            {
                getLog().error("Unable to resolve artifact.",e);
            }
            catch ( ArtifactNotFoundException e )
            {
                getLog().error("Artifact not found.",e);
            }
        }
        getLog().info("Got resolved artifact: " + artifact);
        return artifact;
    }
    
    private void addChildEdges(GraphModel model, ResolutionNode resolutionNode)
    {
        Node originNode = toNode(resolutionNode.getArtifact());
        
        Iterator it = resolutionNode.getChildrenIterator();
        while(it.hasNext())
        {
            ResolutionNode child = (ResolutionNode) it.next();
            Node childNode = toNode(child.getArtifact());
            Edge edge = new Edge(originNode, childNode);
            if(model.addEdge(edge))
            {
                addChildEdges(model, child);
            }
        }
    }
    
    private Node toNode(Artifact artifact) {
        return toNode(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType());
    }
    
    private Node toNode(String groupId, String artifactId, String version, String type)
    {
        String id = groupId + "/" + artifactId + "-" + version + "." + type;
        String label = id;
        return new Node(id, label);
    }


    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    private Log getLog()
    {
        if ( this.log == null )
        {
            this.log = new SystemStreamLog();
        }
        return this.log;
    }

    public MavenMetadataSource getMavenMetadataSource()
    {
        return mavenMetadataSource;
    }

    public MavenProject getProject()
    {
        return project;
    }

    public String getScopeFilter()
    {
        return scopeFilter;
    }

    public String getTypeFilter()
    {
        return typeFilter;
    }

    public String getUseScope()
    {
        return scopeFilter;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setArtifactFactory( ArtifactFactory artifactFactory )
    {
        this.artifactFactory = artifactFactory;
    }

    public void setArtifactRepositoryFactory( ArtifactRepositoryFactory artifactRepositoryFactory )
    {
        this.artifactRepositoryFactory = artifactRepositoryFactory;
    }

    public void setArtifactResolver( ArtifactResolver artifactResolver )
    {
        this.artifactResolver = artifactResolver;
    }

    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    public void setLog( Log log )
    {
        this.log = log;
    }

    public void setMavenMetadataSource( MavenMetadataSource mavenMetadataSource )
    {
        this.mavenMetadataSource = mavenMetadataSource;
    }

    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    public void setScopeFilter( String scopeFilter )
    {
        this.scopeFilter = scopeFilter;
    }

    public void setTypeFilter( String typeFilter )
    {
        this.typeFilter = typeFilter;
    }

    public void setUseScope( String useScope )
    {
        this.scopeFilter = useScope;
    }

    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    private ArtifactRepository toArtifactRepository( Repository repo )
    {
        ArtifactRepositoryLayout layout = getArtifactLayout( repo.getLayout() );
        ArtifactRepositoryPolicy snapshotPolicy = getArtifactPolicy( repo.getSnapshots() );
        ArtifactRepositoryPolicy releasesPolicy = getArtifactPolicy( repo.getReleases() );

        ArtifactRepository artirepo = artifactRepositoryFactory.createArtifactRepository( repo.getId(), repo.getUrl(),
                                                                                          layout, snapshotPolicy,
                                                                                          releasesPolicy );

        return artirepo;
    }
}
