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

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Element;

/**
 * 
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @goal refactor
 * @aggregator
 * @requiresProject true
 * @requiresDirectInvocation false
 * @executionStrategy once-per-session
 * @since 1.0.0
 */
public class PomRefactorMojo extends AbstractPomModifyMojo {

  /**
   * The new groupId to apply.
   * 
   * @parameter expression="${newGroupId}"
   * @since 1.0.0
   */
  private String newGroupId;

  /**
   * The new artifactId to apply.
   * 
   * @parameter expression="${newArtifactId}"
   * @since 1.0.0
   */
  private String newArtifactId;

  /**
   * The new version to apply.
   * 
   * @parameter expression="${newVersion}"
   * @since 1.0.0
   */
  private String newVersion;

  /**
   * The new type to apply.
   * 
   * @parameter expression="${newType}"
   * @since 1.0.0
   */
  private String newType;

  /**
   * The new scope to apply.
   * 
   * @parameter expression="${newScope}"
   * @since 1.0.0
   */
  private String newScope;

  /**
   * The new classifier to apply.
   * 
   * @parameter expression="${newClassifier}"
   * @since 1.0.0
   */
  private String newClassifier;

  /**
   * The flag to indicate if properties should be resolved for matching. If a
   * <code>pom.xml</code> contains
   * <code>&lt;version&gt;${foo.version}&lt;/version&gt;</code> and
   * <code>resolveVariables</code> is <code>true</code>, then the property
   * <code>foo.version</code> is resolved and in case of a replacement, the
   * property-value is replaced where declared while the reference remains
   * untouched. Otherwise, the property is treated literally in order to be able
   * to replace the property reference (<code>${foo.version}</code>) with a new
   * value.
   * 
   * @parameter expression="${resolveVariables}" default-value="true"
   * @required
   * @since 1.0.0
   */
  private boolean resolveVariables;

  // ----------------------------------------------------------------------
  // Mojo fields
  // ----------------------------------------------------------------------

  /** The new classifier to apply. */
  private DependencyInfo newAttributes;

  protected DependencyInfo getNewAttributes() {

    if (this.newAttributes == null) {
      this.newAttributes = new DependencyInfo(this.newGroupId, this.newArtifactId, this.newVersion,
          this.newType, this.newScope, this.newClassifier);
    }
    return this.newAttributes;
  }

  protected void updateDependencies(List<Dependency> dependencyList,
      ProjectContainer projectContainer, String tagname) throws MojoExecutionException,
      MojoFailureException {

    for (Dependency dependency : dependencyList) {
      if (getMatcher().isMatching(dependency)) {
        DependencyInfo delta = getNewAttributes().getDiff(dependency);
        if (!delta.isEmpty()) {
          getLog().info(
              "Chaning dependency " + new DependencyInfo(dependency) + " to "
                  + delta.toDiffString());
          // try to find dependency in XML
          List<Element> dependencyElementList;
          if (ProjectContainer.XML_TAG_DEPENDENCIES.equals(tagname)) {
            dependencyElementList = projectContainer.getPomDependenciesList();
          } else if (ProjectContainer.XML_TAG_DEPENDENCY_MANAGEMENT.equals(tagname)) {
            dependencyElementList = projectContainer.getPomDependencyManagementList();
          } else {
            throw new MojoExecutionException("Internal Error: Unknown dependency tagname '"
                + tagname + "'!");
          }
          boolean notFound = true;
          for (Element elementDependency : dependencyElementList) {
            DependencyInfo dependencyInfo = projectContainer.createDependencyInfo(
                elementDependency, this.resolveVariables);
            getLog().debug("Checking dependency '" + dependencyInfo + "' ...");
            if (dependencyInfo.isMatching(dependency)) {
              getLog().debug("Found dependency in pom.xml: " + dependencyInfo);
              notFound = false;
              if (delta.getGroupId() != null) {
                updateValue(elementDependency, ProjectContainer.XML_TAG_GROUPID, projectContainer,
                    delta.getGroupId());
              }
              if (delta.getArtifactId() != null) {
                updateValue(elementDependency, ProjectContainer.XML_TAG_ARTIFACTID,
                    projectContainer, delta.getArtifactId());
              }
              if (delta.getVersion() != null) {
                updateValue(elementDependency, ProjectContainer.XML_TAG_VERSION, projectContainer,
                    delta.getVersion());
              }
              if (delta.getType() != null) {
                updateValue(elementDependency, ProjectContainer.XML_TAG_TYPE, projectContainer,
                    delta.getType());
              }
              if (delta.getScope() != null) {
                updateValue(elementDependency, ProjectContainer.XML_TAG_SCOPE, projectContainer,
                    delta.getScope());
              }
              if (delta.getClassifier() != null) {
                updateValue(elementDependency, ProjectContainer.XML_TAG_CLASSIFIER,
                    projectContainer, delta.getClassifier());
              }
            }
          }
          if (notFound) {
            getLog().warn("Dependency NOT found in pom.xml");
          }
        }
      }
    }
  }

  protected void updateValue(Element containerElement, String valueTagname,
      ProjectContainer projectContainer, String newValue) throws MojoExecutionException,
      MojoFailureException {

    Element elementValue = DomUtilities.getChildElement(containerElement, valueTagname);
    if (elementValue != null) {
      String currentValue = elementValue.getTextContent();
      if (currentValue.startsWith(ProjectContainer.PROPERTY_PREFIX)) {
        String propertyName = currentValue.substring(ProjectContainer.PROPERTY_PREFIX.length(),
            currentValue.length() - ProjectContainer.PROPERTY_SUFFIX.length());
        if (propertyName.startsWith(ProjectContainer.PROPERTY_PREFIX_POM)
            || propertyName.startsWith(ProjectContainer.PROPERTY_PREFIX_PROJECT)) {
          getLog().debug("Keeping internal property '" + propertyName + "' ...");
        } else {
          projectContainer.updatePropertyValue(propertyName, newValue);
        }
      } else {
        String oldValue = elementValue.getTextContent();
        elementValue.setTextContent(newValue);
        getLog().debug(
            "Updating '" + valueTagname + "' of '" + projectContainer.getId() + "' from '"
                + oldValue + "' to '" + newValue + "'.");
        projectContainer.setModified();
      }
    }

  }

  protected void updateProject(ProjectContainer projectContainer, MavenProject project,
      boolean parent) throws MojoExecutionException, MojoFailureException {

    if (getMatcher().isMatching(project)) {
      DependencyInfo delta = getNewAttributes().getDiff(project);
      if (!delta.isEmpty()) {
        String source;
        if (parent) {
          source = "parent";
        } else {
          source = "project";
        }
        getLog().info("Changing " + source + " " + project.getId() + " to " + delta.toDiffString());
        Element elementProject = projectContainer.getPomDocument().getDocumentElement();
        if (parent) {
          elementProject = DomUtilities.getChildElement(elementProject,
              ProjectContainer.XML_TAG_PARENT);
        }
        if (delta.getGroupId() != null) {
          updateValue(elementProject, ProjectContainer.XML_TAG_GROUPID, projectContainer, delta
              .getGroupId());
        }
        if (delta.getArtifactId() != null) {
          updateValue(elementProject, ProjectContainer.XML_TAG_ARTIFACTID, projectContainer, delta
              .getArtifactId());
        }
        if (delta.getVersion() != null) {
          updateValue(elementProject, ProjectContainer.XML_TAG_VERSION, projectContainer, delta
              .getVersion());
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public void execute(ProjectContainer projectContainer) throws MojoExecutionException,
      MojoFailureException {

    if (getNewAttributes().isEmpty()) {
      throw new MojoExecutionException(
          "At least one of the new*-parameters (newVersion, newArtifactId, newGroupId, newScope or newClassifier) have to be configured!");
    }
    super.execute(projectContainer);
    MavenProject project = projectContainer.getProject();
    updateProject(projectContainer, project, false);
    MavenProject parent = project.getParent();
    if (parent != null) {
      updateProject(projectContainer, parent, true);
    }
    updateDependencies(project.getDependencies(), projectContainer,
        ProjectContainer.XML_TAG_DEPENDENCIES);
    DependencyManagement dependencyManagement = project.getDependencyManagement();
    if (dependencyManagement != null) {
      updateDependencies(dependencyManagement.getDependencies(), projectContainer,
          ProjectContainer.XML_TAG_DEPENDENCY_MANAGEMENT);
    }
  }
}
