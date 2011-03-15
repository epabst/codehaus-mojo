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

package org.codehaus.mojo.pluginsupport.dependency;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Dependency;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * ???
 *
 * @version $Id$
 */
public class DependencyHelper
    implements Contextualizable
{
    private ArtifactRepositoryFactory artifactRepositoryFactory = null;

    private ArtifactMetadataSource artifactMetadataSource = null;

    private ArtifactCollector artifactCollector = null;

    private ArtifactFactory artifactFactory = null;

    private ArtifactResolver artifactResolver = null;

    private PlexusContainer container;

    private ArtifactRepository repository;

    //
    // TODO: Figure out how to get ${localRepository} injected so we don't need it passed in.
    //

    public void setArtifactRepository(final ArtifactRepository repository) {
        this.repository = repository;
    }

    private ArtifactRepository getArtifactRepository() {
        if (repository == null) {
            throw new IllegalStateException("Not initialized; missing ArtifactRepository");
        }
        return repository;
    }

    public DependencyTree getDependencies(final MavenProject project)
        throws ProjectBuildingException, InvalidDependencyVersionException, ArtifactResolutionException
    {
        assert project != null;

        Map managedVersions = getManagedVersionMap(project, artifactFactory);
        DependencyResolutionListener listener = new DependencyResolutionListener();

        if (project.getDependencyArtifacts() == null) {
            project.setDependencyArtifacts(project.createArtifacts(artifactFactory, null, null));
        }

        artifactCollector.collect(
                project.getDependencyArtifacts(),
                project.getArtifact(),
                managedVersions,
                getArtifactRepository(),
                project.getRemoteArtifactRepositories(),
                artifactMetadataSource,
                null,
                Collections.singletonList(listener));
        
        return listener.getDependencyTree();
    }

    public static Map getManagedVersionMap(final MavenProject project, final ArtifactFactory factory) throws ProjectBuildingException {
        assert project != null;
        assert factory != null;

        DependencyManagement dependencyManagement = project.getDependencyManagement();
        Map managedVersionMap;

        if (dependencyManagement != null && dependencyManagement.getDependencies() != null) {
            managedVersionMap = new HashMap();
            Iterator iter = dependencyManagement.getDependencies().iterator();

            while (iter.hasNext()) {
                Dependency d = (Dependency) iter.next();

                try {
                    VersionRange versionRange = VersionRange.createFromVersionSpec(d.getVersion());
                    Artifact artifact = factory.createDependencyArtifact(
                            d.getGroupId(),
                            d.getArtifactId(),
                            versionRange,
                            d.getType(),
                            d.getClassifier(),
                            d.getScope());
                    managedVersionMap.put(d.getManagementKey(), artifact);
                }
                catch (InvalidVersionSpecificationException e) {
                    throw new ProjectBuildingException(project.getId(),
                            "Unable to parse version '" + d.getVersion() +
                            "' for dependency '" + d.getManagementKey() + "': " + e.getMessage(), e);
                }
            }
        }
        else {
            managedVersionMap = Collections.EMPTY_MAP;
        }

        return managedVersionMap;
    }

    //
    // Contextualizable
    //

    public void contextualize(final Context context) throws ContextException {
        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    //
    // Component Access
    //
    
    public ArtifactResolver getArtifactResolver() {
        return artifactResolver;
    }

    public ArtifactRepositoryFactory getArtifactRepositoryFactory() {
        return artifactRepositoryFactory;
    }

    public ArtifactMetadataSource getArtifactMetadataSource() {
        return artifactMetadataSource;
    }

    public ArtifactCollector getArtifactCollector() {
        return artifactCollector;
    }

    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }
}
