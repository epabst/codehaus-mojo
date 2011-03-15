package org.codehaus.mojo.scmchangelog.tracker;

import junit.framework.TestCase;

/**
 * 
 * @author Tomas Pollak
 */
public class XPlannerBugTrackLinkerTest extends TestCase {

	public XPlannerBugTrackLinkerTest(String testName) {
		super(testName);
	}

	public void testLinkToXPlanner() {
		BugTrackLinker linker = new XPlannerBugTrackLinker(
				"http://xplanner.example.org/xplanner/");
		String result = linker.getLinkUrlForBug("1234");
		assertNotNull(result);
		assertEquals(
				"http://xplanner.example.org/xplanner/do/search/id?searchedId=1234",
				result);
	}
}
