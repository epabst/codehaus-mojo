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

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;

/**
 * ???
 *
 * @version $Id$
 */
public class DependencyTree
{
    Map artifacts = new HashMap();

    Node rootNode;

    public Collection getArtifacts() {
        return artifacts.values();
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    //
    // Node
    //

    public static class Node
    {
        Node parent;

        List children = new ArrayList();

        Artifact artifact;

        int depth;

        public List getChildren() {
            return children;
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public int getDepth() {
            return depth;
        }

        public String toString() {
            return "DependencyTree$Node: " + artifact + "(" + depth + ")";
        }
    }
}
