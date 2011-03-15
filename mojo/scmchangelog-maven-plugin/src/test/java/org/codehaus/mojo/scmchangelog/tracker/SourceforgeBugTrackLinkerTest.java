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
public class SourceforgeBugTrackLinkerTest extends TestCase {

  public SourceforgeBugTrackLinkerTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testLinkToSourceforge() {
    SourceforgeBugTrackLinker linker = new SourceforgeBugTrackLinker(
            "http://sourceforge.net/tracker2/?func=browse&group_id=23629&atid=379133");
    String result = linker.getLinkUrlForBug("2189340");
    assertNotNull(result);
    assertEquals("http://sourceforge.net/tracker2/?func=detail&aid=2189340&group_id=23629&atid=379133",
            result);
    linker = new SourceforgeBugTrackLinker(
            "http://sourceforge.net/tracker2/?func=browse&group_id=23629&atid=379133");
    result = linker.getLinkUrlForBug("1986422");
    assertNotNull(result);
    assertEquals("http://sourceforge.net/tracker2/?func=detail&aid=1986422&group_id=23629&atid=379133",
            result);
  }
}
