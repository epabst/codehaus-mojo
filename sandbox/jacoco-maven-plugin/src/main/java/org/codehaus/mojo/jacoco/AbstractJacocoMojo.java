package org.codehaus.mojo.jacoco;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;

/**
 * @author Evgeny Mandrikov
 */
public abstract class AbstractJacocoMojo extends AbstractMojo {
  /**
   * @parameter default-value="${session}"
   * @required
   * @readonly
   */
  private MavenSession session;

  protected void setProperty(String key, String value) {
    session.getExecutionProperties().setProperty(key, value);
  }
}
