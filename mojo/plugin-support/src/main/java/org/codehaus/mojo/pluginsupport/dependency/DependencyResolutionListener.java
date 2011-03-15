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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.maven.artifact.Artifact;

import org.codehaus.mojo.pluginsupport.dependency.DependencyTree.Node;

//
// NOTE: Lifetd from the maven-project-info-plugin
//

/**
 * ???
 * 
 * @version $Id$
 */
public class DependencyResolutionListener
    extends ResolutionListenerAdapter
{
    private DependencyTree tree = new DependencyTree();

    private int currentDepth = 0;

    private Stack parents = new Stack();

    private Map artifacts = new HashMap();

    public DependencyTree getDependencyTree() {
        return tree;
    }

    public Collection getArtifacts() {
        return artifacts.values();
    }
    
    //
    // ResolutionListener
    //

    public void startProcessChildren(final Artifact artifact) {
        Node node = (Node) artifacts.get(artifact.getDependencyConflictId());

        node.depth = currentDepth++;
        if (parents.isEmpty()) {
            tree.rootNode = node;
        }

        parents.push(node);
    }

    public void endProcessChildren(final Artifact artifact) {
        Node node = (Node) parents.pop();
        assert artifact.equals(node.artifact);
        currentDepth--;
    }

    public void omitForNearer(final Artifact omitted, final Artifact kept) {
        assert omitted.getDependencyConflictId().equals(kept.getDependencyConflictId());

        Node prev = (Node) artifacts.get(omitted.getDependencyConflictId());
        if (prev != null) {
            if (prev.parent != null) {
                prev.parent.children.remove(prev);
            }
            artifacts.remove(omitted.getDependencyConflictId());
        }

        includeArtifact(kept);
    }

    public void omitForCycle(final Artifact artifact) {
        // intentionally blank
    }

    public void includeArtifact(final Artifact artifact) {
        if (artifacts.containsKey(artifact.getDependencyConflictId())) {
            Node prev = (Node) artifacts.get(artifact.getDependencyConflictId());
            if (prev.parent != null) {
                prev.parent.children.remove(prev);
            }
            artifacts.remove(artifact.getDependencyConflictId());
        }

        Node node = new Node();
        node.artifact = artifact;
        if (!parents.isEmpty()) {
            node.parent = (Node) parents.peek();
            node.parent.children.add(node);
            node.depth = currentDepth;
        }
        artifacts.put(artifact.getDependencyConflictId(), node);
    }

    public void updateScope(final Artifact artifact, final String scope) {
        Node node = (Node) artifacts.get(artifact.getDependencyConflictId());
        node.artifact.setScope(scope);
    }

    public void manageArtifact(final Artifact artifact, final Artifact replacement) {
        Node node = (Node) artifacts.get(artifact.getDependencyConflictId());

        if (node != null) {
            if (replacement.getVersion() != null) {
                node.artifact.setVersion(replacement.getVersion());
            }
            if (replacement.getScope() != null) {
                node.artifact.setScope(replacement.getScope());
            }
        }
    }
}