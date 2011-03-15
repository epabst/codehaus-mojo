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

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class AbstractPomModifyMojo extends AbstractPomMojo {

  /**
   * The groupId of the module to change version of.
   * 
   * @parameter expression="${groupId}" default-value="${project.groupId}"
   * @required
   * @since 1.0.0
   */
  private String groupId;

  /**
   * The artifactId of the module to change version of.
   * 
   * @parameter expression="${artifactId}"
   * @required
   * @since 1.0.0
   */
  private String artifactId;

  /**
   * The old version to replace. The default value is <code>null</code> and
   * only matches the current version of the according reactor project. Set to
   * <code>"*"</code> in order to match any version.
   * 
   * @parameter expression="${version}"
   * @since 1.0.0
   */
  private String version;

  /**
   * The type (e.g. "jar") to replace in dependencies. The default value is
   * <code>null</code> and matches any type.
   * 
   * @parameter expression="${type}"
   * @since 1.0.0
   */
  private String type;

  /**
   * The scope (e.g. "compile", "test", ...) to replace in dependencies. The
   * default value is <code>null</code> and matches any scope.
   * 
   * @parameter expression="${scope}"
   * @since 1.0.0
   */
  private String scope;

  /**
   * The classifier (e.g. "sources") to replace in dependencies. The default
   * value is <code>null</code> and matches any classifier.
   * 
   * @parameter expression="${classifier}"
   * @since 1.0.0
   */
  private String classifier;

  /**
   * The encoding used to write pom.xml files.
   * 
   * @parameter expression="${xmlEncoding}" default-value="${file.encoding}"
   * @required
   * @since 1.0.0
   */
  private String xmlEncoding;

  /**
   * The flag to indicate if original pom.xml should be overwritten, or a new
   * pom.xml should be created in <code>project.build.outputDirectory</code>.
   * 
   * @parameter expression="${overwrite}" default-value="false"
   * @required
   * @since 1.0.0
   */
  private boolean overwrite;

  private DependencyPatternMatcher matcher;

  private final Map<ProjectId, ProjectContainer> projectContainerMap;

  /**
   * The constructor.
   */
  public AbstractPomModifyMojo() {

    super();
    this.projectContainerMap = new HashMap<ProjectId, ProjectContainer>();
  }

  /**
   * @return the overwrite
   */
  public boolean isOverwrite() {

    return this.overwrite;
  }

  /**
   * @return the matcher
   */
  public DependencyPatternMatcher getMatcher() {

    return this.matcher;
  }

  public ProjectContainer getReactorProject(String groupId, String artifactId) {

    ProjectId pid = new ProjectId(groupId, artifactId);
    ProjectContainer reactorProject = this.projectContainerMap.get(pid);
    return reactorProject;
  }

  /**
   * {@inheritDoc}
   */
  public void execute() throws MojoExecutionException, MojoFailureException {

    super.execute();
    this.matcher = new DependencyPatternMatcher(this.groupId, this.artifactId, this.version,
        this.type, this.scope, this.classifier);
    // pass 1: search matching modules in reactor...
    for (MavenProject module : this.reactorProjects) {
      ProjectContainer parent = null;
      if (module.getParent() != null) {
        MavenProject parentProject = module.getParent();
        ProjectId parentId = new ProjectId(parentProject);
        parent = this.projectContainerMap.get(parentId);
        if (parent == null) {
          getLog().debug("Parent project '" + parentId + "' NOT in reactor.");
        }
      }
      ProjectContainer container = new ProjectContainer(module, parent, getLog());
      this.projectContainerMap.put(container.getId(), container);
      // getLog().info("Matching " + module);
    }
    // pass 2: modify modules...
    for (MavenProject module : this.reactorProjects) {
      ProjectId pid = new ProjectId(module);
      ProjectContainer container = this.projectContainerMap.get(pid);
      execute(container);
    }
    // pass 3: save modified POMs...
    for (MavenProject module : this.reactorProjects) {
      ProjectId pid = new ProjectId(module);
      ProjectContainer container = this.projectContainerMap.get(pid);
      container.save(this.xmlEncoding, this.overwrite);
    }
  }

  protected void execute(ProjectContainer projectContainer) throws MojoExecutionException,
      MojoFailureException {

    getLog().info("Processing project " + projectContainer.getId() + "...");
  }

}
