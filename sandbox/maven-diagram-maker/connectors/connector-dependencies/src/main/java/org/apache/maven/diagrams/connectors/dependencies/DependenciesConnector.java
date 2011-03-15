package org.apache.maven.diagrams.connectors.dependencies;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Iterator;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.DiagramConnector;
import org.apache.maven.diagrams.connector_api.DynamicDiagramConnector;
import org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor;
import org.apache.maven.diagrams.connectors.dependencies.graph.ArtifactNode;
import org.apache.maven.diagrams.connectors.dependencies.graph.DependenciesGraphMetadata;
import org.apache.maven.diagrams.connectors.dependencies.graph.DependencyEdge;
import org.apache.maven.diagrams.graph_api.Graph;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DependenciesConnector extends AbstractLogEnabled implements DiagramConnector
{

    /* ============== Plexus injected ================================ */

    private ArtifactFactory artifactFactory;

 //   private ArtifactFilter artifactFilter = new AndArtifactFilter();

    private ArtifactRepository artifactRepository;

    private ArtifactMetadataSource artifactMetadataSource;

    private DependencyTreeBuilder dependencyTreeBuilder;

    private ArtifactCollector collector;

    private ConnectorDescriptor connectorDescriptor;

    private MavenProject mavenProject;

    // required: dependencies-tree 1.1-SNAPSHOT
    // public Graph calculateGraph( ConnectorConfiguration arg0 ) throws ConnectorException
    // {
    // DependencyNode dependencyTree;
    //
    // try
    // {
    // dependencyTree =
    // dependencyTreeBuilder.buildDependencyTree(
    // mavenProject,
    // artifactRepository,
    // artifactFactory, artifactMetadataSource, artifactFilter,
    // collector );
    //
    // }
    // catch ( DependencyTreeBuilderException e )
    // {
    // throw new ConnectorException( "Unable to build dependency tree", e );
    // }
    //
    // return dependencyTreeToGraph( dependencyTree );
    // }

    public Graph calculateGraph( ConnectorConfiguration arg0 ) throws ConnectorException
    {
        DependencyTree dependencyTree;

        try
        {
            dependencyTree =
                dependencyTreeBuilder.buildDependencyTree( mavenProject, artifactRepository, artifactFactory,
                                                           artifactMetadataSource, collector );

        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new ConnectorException( "Unable to build dependency tree", e );
        }

        return dependencyTreeToGraph( dependencyTree );
    }

    public DynamicDiagramConnector getDynamicDiagramConnector() throws ConnectorException
    {
        throw new IllegalStateException( "DependenciesConnector doesn't support dynamic diagram connector" );
    }

    public ConnectorDescriptor getConnectorDescriptor() throws ConnectorException
    {
        return connectorDescriptor;
    }

    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    private Graph dependencyTreeToGraph( DependencyTree dependencyTree )
    {
        Graph g = new org.apache.maven.diagrams.graph_api.impl.GraphImpl( new DependenciesGraphMetadata() );
        addSubtreeToGraph( g, dependencyTree.getRootNode() );
        return g;
    }

    private Graph dependencyTreeToGraph( DependencyNode dependencyTree )
    {
        Graph g = new org.apache.maven.diagrams.graph_api.impl.GraphImpl( new DependenciesGraphMetadata() );
        addSubtreeToGraph( g, dependencyTree );
        return g;
    }

    @SuppressWarnings( "unchecked" )
    private ArtifactNode addSubtreeToGraph( Graph g, DependencyNode rootNode )
    {
        ArtifactNode root = addNodeToGraph( g, rootNode );
        Iterator<DependencyNode> iterator = rootNode.getChildren().iterator();
        while ( iterator.hasNext() )
        {
            DependencyNode child = iterator.next();
            if ( rootNode != child )
            {
                ArtifactNode child_new = addSubtreeToGraph( g, child );
                addEdgeToGraph( g, root, child_new );
            }
        }
        return root;
    }

    private void addEdgeToGraph( Graph g, ArtifactNode root, ArtifactNode child_new )
    {
        g.addEdge( new DependencyEdge( root, child_new ) );

    }

    private ArtifactNode addNodeToGraph( Graph g, DependencyNode rootNode )
    {
        ArtifactNode node = new ArtifactNode();
        System.out.println( rootNode.getArtifact().getDependencyConflictId() );
        node.setArtifact( rootNode.getArtifact() );
        g.addNode( node );
        return node;
    }

    public void setArtifactRepository( ArtifactRepository artifactRepository )
    {
        this.artifactRepository = artifactRepository;
    }
}
