package org.codehaus.mojo.scmchangelog.scm.hg.command.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.scmchangelog.scm.util.MavenScmLogger;
import org.codehaus.mojo.scmchangelog.tags.Tag;

/**
 * Unit tests for HgTagsConsumer.
 * 
 * @author Tomas Pollak
 */
public class HgTagsConsumerTest extends TestCase {
	private HgTagsConsumer consumer;

	public HgTagsConsumerTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		super.setUp();
		consumer = new HgTagsConsumer(
				new MavenScmLogger(new SystemStreamLog()), Pattern
						.compile(".*"));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConsumeOneLine() {
		consumer.doConsume(null, "tag1 1:12345678");
		List status = consumer.getStatus();

		// Assert the expected results
		List expected = Collections.singletonList(createTag("tag1", "0", "1"));
		assertEqualTags(expected, status);
	}

	public void testConsumeTwoLines() {
		consumer.doConsume(null, "tag2 2:00000002");
		consumer.doConsume(null, "tag1 1:00000001");
		List status = consumer.getStatus();

		// Assert the expected results
		List expected = Arrays.asList(new Object[] {
				createTag("tag2", "1", "2"), createTag("tag1", "0", "1") });
		assertEqualTags(expected, status);
	}

	private Tag createTag(String title, String startRevision, String endRevision) {
		Tag tag = new Tag(title);
		tag.setStartRevision(startRevision);
		tag.setEndRevision(endRevision);
		return tag;
	}

	private void assertEqualTags(Tag expected, Tag actual) {
		assertEquals(expected.getTitle(), actual.getTitle());
		assertEquals(expected.getStartRevision(), actual.getStartRevision());
		assertEquals(expected.getEndRevision(), actual.getEndRevision());
	}

	private void assertEqualTags(List expected, List actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < actual.size(); i++) {
			assertEqualTags((Tag) expected.get(i), (Tag) actual.get(i));
		}
	}
}