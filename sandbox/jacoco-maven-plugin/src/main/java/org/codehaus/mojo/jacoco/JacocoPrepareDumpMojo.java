package org.codehaus.mojo.jacoco;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * @goal prepare-dump
 * @phase initialize
 * @aggregator
 * @author Evgeny Mandrikov
 */
public class JacocoPrepareDumpMojo extends AbstractJacocoMojo {

  /**
   * The name of the property in which to store the path to JaCoCo dump.
   * 
   * @parameter default-value="jacoco.dump"
   */
  private String dumpProperty;

  /**
   * @parameter expression="${basedir}"
   * @readonly
   */
  private File baseDir;

  public void execute() throws MojoExecutionException, MojoFailureException {
    File file = new File(baseDir, "target/jacoco-it.exec");

    if (file.exists()) {
      file.delete();
    }

    String dumpPath = file.getAbsolutePath();
    getLog().info("Path to JaCoCo dump : " + dumpPath);
    setProperty(dumpProperty, dumpPath);
  }
}
