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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 */
public abstract class AbstractPomMojo extends AbstractMojo {

  /**
   * The Maven Project.
   * 
   * @parameter expression="${project}"
   * @required
   * @readonly
   * @since 1.0.0
   */
  private MavenProject project;

  /**
   * @parameter expression="${reactorProjects}"
   * @required
   * @readonly
   * @since 1.0.0
   */
  protected List<MavenProject> reactorProjects;

  /**
   * @parameter expression="${requireToplevel}" default-value="true"
   * @since 1.0.0
   */
  protected boolean requireToplevel;

  protected MavenProject findReactorProject(DependencyInfo info) throws MojoFailureException {

    MavenProject result = null;
    for (MavenProject module : this.reactorProjects) {
      if (info.isMatching(module)) {
        if (result != null) {
          throw new MojoFailureException("ambiguous reactor search for " + info + "!\nFound "
              + new DependencyInfo(result) + " and " + new DependencyInfo(module));
        }
        result = module;
      }
    }
    return result;
  }

  protected MavenProject requireReactorProject(DependencyInfo info) throws MojoFailureException {

    MavenProject result = findReactorProject(info);
    if (result == null) {
      throw new MojoFailureException("Could NOT find project " + info + " in reactor");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public void execute() throws MojoExecutionException, MojoFailureException {

    if (this.project.getParent() != null) {
      if (this.requireToplevel) {
        throw new MojoFailureException(
            "You have to invoke this plugin on your top-level POM!\nHowever you have a parent "
                + new DependencyInfo(this.project.getParent())
                + ".\nUse -DrequireToplevel=false to continue.");
      } else {
        getLog().warn("Maven was NOT executed on top-level POM!");
        getLog().info("Continue since ${requireToplevel} is false...");
      }
    }
  }
}
