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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;

import org.apache.maven.project.MavenProject;

import org.codehaus.mojo.pluginsupport.dependency.DependencyTree.Node;

//
// NOTE: Lifetd from the maven-project-info-plugin
//

/**
 * ???
 *
 * @version $Id$
 */
public class Dependencies
{
    private List projectDependencies;

    private DependencyResolutionListener resolvedDependencies;

    public Dependencies(final MavenProject project, final DependencyResolutionListener listener) {
        assert project != null;
        assert listener != null;

        this.projectDependencies = listener.getDependencyTree().getRootNode().getChildren();
        this.resolvedDependencies = listener;

        //
        // Workaround to ensure proper File objects in the Artifacts from the DependencyResolutionListener
        //
        Map projectMap = new HashMap();
        Iterator iter = project.getArtifacts().iterator();

        while (iter.hasNext()) {
            Artifact artifact = (Artifact) iter.next();
            projectMap.put(ArtifactUtils.versionlessKey(artifact), artifact);
        }

        mapArtifactFiles(listener.getDependencyTree().getRootNode(), projectMap);
    }

    private void mapArtifactFiles(final Node node, final Map projectMap) {
        assert node != null;
        assert projectMap != null;

        List childs = node.getChildren();
        if ((childs == null) || childs.isEmpty()) {
            return;
        }

        Iterator iter = childs.iterator();
        while (iter.hasNext()) {
            Node anode = (Node) iter.next();
            String key = ArtifactUtils.versionlessKey(anode.getArtifact());
            Artifact projartifact = (Artifact) projectMap.get(key);
            if (projartifact != null) {
                anode.getArtifact().setFile(projartifact.getFile());
            }

            mapArtifactFiles(anode, projectMap);
        }
    }

    public boolean hasDependencies() {
        return (projectDependencies != null) && (!this.projectDependencies.isEmpty());
    }

    public List getProjectDependencies() {
        return new ArrayList(projectDependencies);
    }

    public List getTransitiveDependencies() {
        List deps = new ArrayList(resolvedDependencies.getArtifacts());
        deps.removeAll(projectDependencies);
        return deps;
    }

    public List getAllDependencies() {
        List deps = new ArrayList();

        for (Iterator iter = resolvedDependencies.getArtifacts().iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            Artifact artifact = node.getArtifact();
            deps.add(artifact);
        }
        return deps;
    }

    public Map getDependenciesByScope() {
        Map dependenciesByScope = new HashMap();
        for (Iterator i = getAllDependencies().iterator(); i.hasNext();) {
            Artifact artifact = (Artifact) i.next();

            List multiValue = (List) dependenciesByScope.get(artifact.getScope());
            if (multiValue == null) {
                multiValue = new ArrayList();
            }
            multiValue.add(artifact);
            dependenciesByScope.put(artifact.getScope(), multiValue);
        }
        return dependenciesByScope;
    }

    public Node getResolvedRoot() {
        return resolvedDependencies.getDependencyTree().getRootNode();
    }
}