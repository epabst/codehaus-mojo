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

import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * ???
 *
 * @version $Id$
 */
public class ResolutionListenerAdapter
    implements ResolutionListener
{
    public void testArtifact(Artifact artifact) {
        // empty
    }

    public void startProcessChildren(Artifact artifact) {
        // empty
    }

    public void endProcessChildren(Artifact artifact) {
        // empty
    }

    public void includeArtifact(Artifact artifact) {
        // empty
    }

    public void omitForNearer(Artifact artifact, Artifact artifact1) {
        // empty
    }

    public void updateScope(Artifact artifact, String string) {
        // empty
    }

    public void manageArtifact(Artifact artifact, Artifact artifact1) {
        // empty
    }

    public void omitForCycle(Artifact artifact) {
        // empty
    }

    public void updateScopeCurrentPom(Artifact artifact, String string) {
        // empty
    }

    public void selectVersionFromRange(Artifact artifact) {
        // empty
    }

    public void restrictRange(Artifact artifact, Artifact artifact1, VersionRange versionRange) {
        // empty
    }
}
