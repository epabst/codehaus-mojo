/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.mojo.pom;

import java.util.regex.Pattern;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

/**
 * @author joerg
 * 
 */
public class DependencyPatternMatcher extends DependencyInfo {

  private final Pattern patternGroupId;

  private final Pattern patternArtifactId;

  private final Pattern patternVersion;

  /**
   * @param dependency
   */
  public DependencyPatternMatcher(Dependency dependency) {

    this(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency
        .getType(), dependency.getScope(), dependency.getClassifier());
  }

  /**
   * @param project
   */
  public DependencyPatternMatcher(MavenProject project) {

    this(project.getGroupId(), project.getArtifactId(), project.getVersion(), project
        .getPackaging(), null, null);
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   * @param type
   * @param scope
   * @param classifier
   */
  public DependencyPatternMatcher(String groupId, String artifactId, String version, String type,
      String scope, String classifier) {

    super(groupId, artifactId, version, type, scope, classifier);
    this.patternGroupId = compileGlob(groupId);
    this.patternArtifactId = compileGlob(artifactId);
    this.patternVersion = compileGlob(version);
  }
  
  private static Pattern compileGlob(String pattern) {

    if (pattern == null) {
      return null;
    }
    String globPattern = pattern.replace("\\", "\\\\").replace("^", "\\^").replace("+", "\\+")
        .replace("$", "\\$").replace(".", "\\.").replace("(", "\\(").replace("[", "\\[").replace(
            "{", "\\{").replace("*", ".*").replace("?", ".");
    return Pattern.compile(globPattern);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMatching(String groupId, String artifactId, String version, String type,
      String scope, String classifier) {

    if (this.patternGroupId != null) {
      if (!this.patternGroupId.matcher(groupId).matches()) {
        return false;
      }
    }
    if (this.patternArtifactId != null) {
      if (!this.patternArtifactId.matcher(artifactId).matches()) {
        return false;
      }      
    }
    if (this.patternVersion != null) {
      if (!this.patternVersion.matcher(version).matches()) {
        return false;
      }      
    }
    return super.isMatching(null, null, null, type, scope, classifier);
  }
  
}
