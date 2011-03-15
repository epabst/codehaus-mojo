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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.graphing.model.Edge;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.Node;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Old GraphModel Factory.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class OldGraphModelFactory
{
    private Log log;
    private ArtifactFactory artifactFactory;
    private ArtifactResolver artifactResolver;
    private ArtifactRepository localRepository;
    private ArtifactRepositoryFactory artifactRepositoryFactory;
    private MavenProject project;
    private Map artifactLayouts;
    
    public OldGraphModelFactory(Log log) {
        this.log = log;
        this.artifactLayouts = new HashMap();
        
        this.artifactLayouts.put("default", new DefaultRepositoryLayout());
        this.artifactLayouts.put("legacy", new LegacyRepositoryLayout());
    }
    
    private Log getLog() {
        if(this.log == null) {
            this.log = new SystemStreamLog();
        }
        return this.log;
    }
    
    public GraphModel getGraphModel(String groupId, String artifactId, String version) {
        Artifact rootArtifact = resolveArtifact(groupId, artifactId, version);
        GraphModel model = new GraphModel();
        model.setCenterNode(toNode(rootArtifact));
        addEdges(model, rootArtifact);
        
        return model;
    }
    
    private void addEdges(GraphModel model, Artifact artifact) {
        
        Model artifactModel = getModel(artifact);
        
        if(artifactModel == null) {
            Node badNode = toNode(artifact);
            badNode.setType(GraphModel.NODE_ERROR);
            model.addNode(badNode);
        } else {
            Node originNode = toNode(artifactModel);
            
            originNode.setType(GraphModel.NODE_PROJECT);
            getLog().info("adding node: " + originNode);
            List deps = artifactModel.getDependencies();
            Iterator it = deps.iterator();
            while(it.hasNext()) {
                Dependency dep = (Dependency) it.next();
                Node destNode = toNode(dep);
                Edge edge = new Edge(originNode, destNode);
                getLog().info("adding edge: " + edge);
                if(model.addEdge(edge)) {
                    
                    addEdges(model, toArtifact(dep));
                }
            }
        }
    }
    
    public Model getModel(Artifact artifact) {
        try {
            MavenXpp3Reader xmlreader = new MavenXpp3Reader();
            FileReader freader = new FileReader(artifact.getFile());
            BufferedReader bufreader = new BufferedReader(freader);
            Model model = xmlreader.read(bufreader);
            
            // TODO: Ensure deps are properly defined.
            
            return model;
        } catch (IOException e) {
            getLog().error("Unable to read response.", e);
            return null;
        } catch (XmlPullParserException e) {
            getLog().error("Unable to create model parser.", e);
            return null;
        }
    }
    
    private Artifact toArtifact(Dependency dep) {
        return resolveArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion());
    }
    
    private Node toNode(Dependency dep) {
        return toNode(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType());
    }
    
    private Node toNode(Model model) {
        return toNode(model.getGroupId(), model.getArtifactId(), model.getVersion(), model.getPackaging());
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
    
    private List getArtifactRepositories() {
        Iterator it;
        List artifactRepositories = new ArrayList();
        
        it = project.getRepositories().iterator();
        while(it.hasNext()) {
            Repository repo = (Repository) it.next();

            artifactRepositories.add(toArtifactRepository(repo));
        }
        
        return artifactRepositories;
    }
    
    private ArtifactRepository toArtifactRepository(Repository repo) {
        ArtifactRepositoryLayout layout = getArtifactLayout(repo.getLayout());
        ArtifactRepositoryPolicy snapshotPolicy = getArtifactPolicy(repo.getSnapshots());
        ArtifactRepositoryPolicy releasesPolicy = getArtifactPolicy(repo.getReleases());
            
        ArtifactRepository artirepo = 
            artifactRepositoryFactory.createArtifactRepository(repo.getId(), 
                                                               repo.getUrl(), 
                                                               layout,
                                                               snapshotPolicy,
                                                               releasesPolicy);
        
        return artirepo;
    }
    
    private ArtifactRepositoryPolicy getArtifactPolicy(RepositoryPolicy policy) {
        if(policy == null) {
            return null;
        }
        
        ArtifactRepositoryPolicy artipolicy = new ArtifactRepositoryPolicy();
        
        artipolicy.setChecksumPolicy(policy.getChecksumPolicy());
        artipolicy.setEnabled(policy.isEnabled());
        artipolicy.setUpdatePolicy(policy.getUpdatePolicy());
        
        return artipolicy;
    }
    
    private ArtifactRepositoryLayout getArtifactLayout(String layoutid) {
        
        ArtifactRepositoryLayout layout = (ArtifactRepositoryLayout) this.artifactLayouts.get(layoutid);
        if(layout == null) {
            layout = new DefaultRepositoryLayout();
        }
        return layout;
    }

    /**
     * @return Returns the artifactFactory.
     */
    public ArtifactFactory getArtifactFactory()
    {
        return artifactFactory;
    }

    /**
     * @param artifactFactory The artifactFactory to set.
     */
    public void setArtifactFactory( ArtifactFactory artifactFactory )
    {
        this.artifactFactory = artifactFactory;
    }

    /**
     * @return Returns the artifactResolver.
     */
    public ArtifactResolver getArtifactResolver()
    {
        return artifactResolver;
    }

    /**
     * @param artifactResolver The artifactResolver to set.
     */
    public void setArtifactResolver( ArtifactResolver artifactResolver )
    {
        this.artifactResolver = artifactResolver;
    }

    /**
     * @return Returns the localRepository.
     */
    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    /**
     * @param localRepository The localRepository to set.
     */
    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    /**
     * @return Returns the artifactRepositoryFactory.
     */
    public ArtifactRepositoryFactory getArtifactRepositoryFactory()
    {
        return artifactRepositoryFactory;
    }

    /**
     * @param artifactRepositoryFactory The artifactRepositoryFactory to set.
     */
    public void setArtifactRepositoryFactory( ArtifactRepositoryFactory artifactRepositoryFactory )
    {
        this.artifactRepositoryFactory = artifactRepositoryFactory;
    }

    /**
     * @return Returns the project.
     */
    public MavenProject getProject()
    {
        return project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject( MavenProject project )
    {
        this.project = project;
    }
}
