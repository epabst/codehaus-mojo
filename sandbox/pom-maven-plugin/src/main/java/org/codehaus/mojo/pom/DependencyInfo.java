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

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Element;

/**
 * TODO
 * 
 * @author joerg
 */
public class DependencyInfo extends ProjectId {

  private final String version;

  private final String type;

  private final String scope;

  private final String classifier;

  /**
   * The constructor from a {@link MavenProject}.
   */
  public DependencyInfo(MavenProject project) {

    this(project.getGroupId(), project.getArtifactId(), project.getVersion(), project
        .getPackaging(), null, null);
  }

  /**
   * The constructor from a {@link MavenProject}.
   */
  public DependencyInfo(Dependency dependency) {

    this(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency
        .getType(), dependency.getScope(), dependency.getClassifier());
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   */
  public DependencyInfo(String groupId, String artifactId, String version) {

    this(groupId, artifactId, version, null, null, null);
  }

  /**
   * @param groupId
   * @param artifactId
   * @param version
   * @param type
   * @param scope
   * @param classifier
   */
  public DependencyInfo(String groupId, String artifactId, String version, String type,
      String scope, String classifier) {

    super(groupId, artifactId);
    this.version = version;
    this.type = type;
    this.scope = scope;
    this.classifier = classifier;
  }

  /**
   * @return the version
   */
  public String getVersion() {

    return this.version;
  }

  /**
   * @return the type
   */
  public String getType() {

    return this.type;
  }

  /**
   * @return the scope
   */
  public String getScope() {

    return this.scope;
  }

  /**
   * @return the classifier
   */
  public String getClassifier() {

    return this.classifier;
  }

  /**
   * @see #equals(Object)
   */
  public final boolean isMatching(DependencyInfo dependency) {

    return isMatching(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
        dependency.getType(), dependency.getScope(), dependency.getClassifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMatching(String groupId, String artifactId, String version, String type,
      String scope, String classifier) {

    if (super.isMatching(groupId, artifactId, version, type, scope, classifier)) {
      if ((this.version == null) || (version == null) || (this.version.equals(version))) {
        if ((this.type == null) || (type == null) || (this.type.equals(type))) {
          if ((this.scope == null) || (scope == null) || (this.scope.equals(scope))) {
            if ((this.classifier == null) || (classifier == null)
                || (this.classifier.equals(classifier))) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public DependencyInfo getDiff(MavenProject project) {

    return getDiff(project.getGroupId(), project.getArtifactId(), project.getVersion(), null, null,
        null);
  }

  public DependencyInfo getDiff(Dependency dependency) {

    return getDiff(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
        dependency.getType(), dependency.getScope(), dependency.getClassifier());
  }

  public DependencyInfo getDiff(String groupId, String artifactId, String version, String type,
      String scope, String classifier) {

    String newGroupId = null;
    if ((groupId != null) && (getGroupId() != null) && (!groupId.equals(getGroupId()))) {
      newGroupId = getGroupId();
    }
    String newArtifactId = null;
    if ((artifactId != null) && (getArtifactId() != null) && (!artifactId.equals(getArtifactId()))) {
      newArtifactId = getArtifactId();
    }
    String newVersion = null;
    if ((version != null) && (this.version != null) && (!version.equals(this.version))) {
      newVersion = this.version;
    }
    String newType = null;
    if ((type != null) && (this.type != null) && (!type.equals(this.type))) {
      newType = this.type;
    }
    String newScope = null;
    if ((scope != null) && (this.scope != null) && (!scope.equals(this.scope))) {
      newScope = this.scope;
    }
    String newClassifier = null;
    if ((classifier != null) && (this.classifier != null) && (!classifier.equals(this.classifier))) {
      newClassifier = this.classifier;
    }
    return new DependencyInfo(newGroupId, newArtifactId, newVersion, newType, newScope,
        newClassifier);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEmpty() {

    if (!super.isEmpty()) {
      return false;
    }
    if (this.version != null) {
      return false;
    }
    if (this.type != null) {
      return false;
    }
    if (this.scope != null) {
      return false;
    }
    if (this.classifier != null) {
      return false;
    }
    return true;
  }


  public String toDiffString() {

    StringBuilder sb = new StringBuilder();
    String prefix = "";
    if (getGroupId() != null) {
      sb.append("groupId ");
      sb.append(getGroupId());
      prefix = ",";
    }
    if (getArtifactId() != null) {
      sb.append(prefix);
      sb.append("artifactId ");
      sb.append(getArtifactId());
      prefix = ",";
    }
    if (this.version != null) {
      sb.append(prefix);
      sb.append("version ");
      sb.append(this.version);
      prefix = ",";
    }
    if (this.type != null) {
      sb.append(prefix);
      sb.append("type ");
      sb.append(this.type);
      prefix = ",";
    }
    if (this.scope != null) {
      sb.append(prefix);
      sb.append("scope ");
      sb.append(this.scope);
      prefix = ",";
    }
    if (this.classifier != null) {
      sb.append(prefix);
      sb.append("classifier ");
      sb.append(this.classifier);
      prefix = ",";
    }
    return sb.toString();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void toString(StringBuilder sb) {

    // groupId:artifactId:type:version:scope:classifier
    super.toString(sb);
    if ((this.type != null) || (this.version != null) || (this.scope != null)
        || (this.classifier != null)) {
      sb.append(':');
      sb.append(this.type);
      if ((this.version != null) || (this.scope != null) || (this.classifier != null)) {
        sb.append(':');
        sb.append(this.version);
        if ((this.scope != null) || (this.classifier != null)) {
          sb.append(':');
          sb.append(this.scope);
          if (this.classifier != null) {
            sb.append(':');
            sb.append(this.classifier);
          }
        }
      }
    }
  }

}
