/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codehaus.mojo.scmchangelog.tracker;

import junit.framework.TestCase;

/**
 *
 * @author Emmanuel Hugonnet
 */
public class JiraBugTrackLinkerTest extends TestCase {

  public JiraBugTrackLinkerTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testLinkToJira() {
    JiraBugTrackLinker linker = new JiraBugTrackLinker(
            "http://jira.codehaus.org/browse/MSCMCHGLOG");
    String result = linker.getLinkUrlForBug("MSCMCHGLOG-2");
    assertNotNull(result);
    assertEquals("http://jira.codehaus.org/browse/MSCMCHGLOG-2",
            result);
    linker = new JiraBugTrackLinker(
            "http://jira.codehaus.org/browse/MSCMCHGLOG/");
    result = linker.getLinkUrlForBug("MSCMCHGLOG-2");
    assertNotNull(result);
    assertEquals("http://jira.codehaus.org/browse/MSCMCHGLOG-2",
            result);
  }
}
