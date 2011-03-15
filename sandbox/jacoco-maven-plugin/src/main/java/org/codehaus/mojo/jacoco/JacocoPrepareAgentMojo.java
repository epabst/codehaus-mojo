package org.codehaus.mojo.jacoco;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.Collections;

/**
 * @goal prepare-agent
 * @phase initialize
 * @aggregator
 * @author Evgeny Mandrikov
 */
public class JacocoPrepareAgentMojo extends AbstractJacocoMojo {
  /**
   * The name of the property in which to store the path to JaCoCo agent.
   * 
   * @parameter default-value="jacoco.agent"
   */
  private String agentProperty;

  /**
   * @component
   * @readonly
   */
  private ArtifactFactory artifactFactory;

  /**
   * @component
   * @readonly
   */
  private ArtifactResolver artifactResolver;

  /**
   * 
   * @parameter expression="${localRepository}"
   * @readonly
   */
  private ArtifactRepository localRepository;

  public void execute() throws MojoExecutionException, MojoFailureException {
    // TODO allow to set artifact name via property
    Artifact jacocoAgentArtifact = artifactFactory.createBuildArtifact("org.jacoco", "agent-all", "0.4.0.20100604151516", "jar");

    try {
      artifactResolver.resolve(jacocoAgentArtifact, Collections.EMPTY_LIST, localRepository);
    } catch (ArtifactResolutionException e) {
      throw new MojoExecutionException(e.getMessage());
    } catch (ArtifactNotFoundException e) {
      throw new MojoExecutionException(e.getMessage());
    }

    File jacocoAgentFile = jacocoAgentArtifact.getFile();

    String jacocoAgentPath = jacocoAgentFile.getAbsolutePath();
    getLog().info("Path to JaCoCo agent : " + jacocoAgentPath);
    setProperty(agentProperty, jacocoAgentPath);
  }
}
