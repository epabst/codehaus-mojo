/*
The MIT License

Copyright (c) 2004, The Codehaus

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.codehaus.mojo.scmchangelog.changelog.log.grammar;

import java.util.Iterator;

import junit.framework.TestCase;

import org.codehaus.mojo.scmchangelog.changelog.log.Issue;
import org.codehaus.mojo.scmchangelog.changelog.log.Message;
import org.codehaus.mojo.scmchangelog.changelog.log.OperationTypeEnum;

/**
 * Test for the MANU grammar.
 * @author ehsavoie
 * @version $Id$
 */
public class ManuSvnGrammarTest extends TestCase {
    private GrammarEnum grammar;

    public ManuSvnGrammarTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        grammar = GrammarEnum.valueOf("MANU");
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of extractMessage method, of class org.codehaus.mojo.scmchangelog.scm.svn.log.grammar.ManuSvnGrammar.
     */
    public void testExtractFixMessage() {
        String content = "@fix:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());
        
        Issue issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.FIX, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "@fix :MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());
        
        issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.FIX, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "fix:MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("fix:MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@fix MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@fix MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@fix:MVN-512 Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@fix:MVN-512 Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@fix:;Hello World";
		result = grammar.extractMessage(content);
		assertNotNull(result);
		assertNotNull(result.getIssues());
		assertEquals("Hello World", result.getComment());
		assertEquals(1, result.getIssues().size());

		issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.FIX, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractRemoveMessage() {
        String content = "@remove:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());

        Issue issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.REMOVE, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "@remove  :MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());
        
        issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.REMOVE, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "remove:MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("remove:MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@remove MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@remove MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@remove:MVN-512 Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@remove:MVN-512 Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@remove:;Hello World";
		result = grammar.extractMessage(content);
		assertNotNull(result);
		assertNotNull(result.getIssues());
		assertEquals("Hello World", result.getComment());
		assertEquals(1, result.getIssues().size());

		issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.REMOVE, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractAddMessage() {
        String content = "@add:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());

        Issue issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.ADD, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "@add   :MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());
        
        issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.ADD, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "add:MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("add:MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@add MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@add MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@add:MVN-512 Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@add:MVN-512 Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@add:;Hello World";
		result = grammar.extractMessage(content);
		assertNotNull(result);
		assertNotNull(result.getIssues());
		assertEquals("Hello World", result.getComment());
		assertEquals(1, result.getIssues().size());

		issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.ADD, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractUpdateMessage() {
        String content = "@update:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());

        Issue issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.UPDATE, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "@update\n\r\t:MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(1, result.getIssues().size());
        
        issue = (Issue) result.getIssues().get(0);
        assertNotNull(issue);
        assertEquals(OperationTypeEnum.UPDATE, issue.getType());
        assertEquals("MVN-512", issue.getIssue());
        
        content = "update:MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("update:MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@update:MVN-512 Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@update:MVN-512 Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@update MVN-512;Hello World";
        result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("@update MVN-512;Hello World", result.getComment());
        assertEquals(0, result.getIssues().size());
        
        content = "@update:;Hello World";
		result = grammar.extractMessage(content);
		assertNotNull(result);
		assertNotNull(result.getIssues());
		assertEquals("Hello World", result.getComment());
		assertEquals(1, result.getIssues().size());

		issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.UPDATE, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractComplexMessage() {
        String content = "@fix:MVN-510; @update:MVN-511;@remove:MVN-512;@add:MVN-513;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World", result.getComment());
        assertEquals(4, result.getIssues().size());

        boolean isUpdate = false;
        boolean isAdd = false;
        boolean isRemove = false;
        boolean isFix = false;

        Iterator iter = result.getIssues().iterator();

        while (iter.hasNext()) {
            Issue issue = (Issue) iter.next();

            if (OperationTypeEnum.ADD.equals(issue.getType())) {
                isAdd = true;
                assertEquals("MVN-513", issue.getIssue());
            }

            if (OperationTypeEnum.FIX.equals(issue.getType())) {
                isFix = true;
                assertEquals("MVN-510", issue.getIssue());
            }

            if (OperationTypeEnum.REMOVE.equals(issue.getType())) {
                isRemove = true;
                assertEquals("MVN-512", issue.getIssue());
            }

            if (OperationTypeEnum.UPDATE.equals(issue.getType())) {
                isUpdate = true;
                assertEquals("MVN-511", issue.getIssue());
            }
        }

        assertTrue(isAdd && isFix && isRemove && isUpdate);
    }

    public void testExtractMultiCommentMessage() {
        String content = "@fix:MVN-510; Message Fix @update:MVN-511; Message " +
            "update @remove:MVN-512;Message remove@add:MVN-513;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Message Fix \r\n Message update \r\nMessage remove\r\nHello World",
            result.getComment());
        assertEquals(4, result.getIssues().size());

        boolean isUpdate = false;
        boolean isAdd = false;
        boolean isRemove = false;
        boolean isFix = false;
        Iterator iter = result.getIssues().iterator();

        while (iter.hasNext()) {
            Issue issue = (Issue) iter.next();

            if (OperationTypeEnum.ADD.equals(issue.getType())) {
                isAdd = true;
                assertEquals("MVN-513", issue.getIssue());
            }

            if (OperationTypeEnum.FIX.equals(issue.getType())) {
                isFix = true;
                assertEquals("MVN-510", issue.getIssue());
            }

            if (OperationTypeEnum.REMOVE.equals(issue.getType())) {
                isRemove = true;
                assertEquals("MVN-512", issue.getIssue());
            }

            if (OperationTypeEnum.UPDATE.equals(issue.getType())) {
                isUpdate = true;
                assertEquals("MVN-511", issue.getIssue());
            }
        }

        assertTrue(isAdd && isFix && isRemove && isUpdate);
    }

    public void testExtractMultipleFixMessage() {
        String content = "@fix:MVN-512;Hello World1@fix:MVN-513;Hello World2";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals("Hello World1\r\nHello World2", result.getComment());
        assertEquals(2, result.getIssues().size());

        Iterator iter = result.getIssues().iterator();

        while (iter.hasNext()) {
            Issue issue = (Issue) iter.next();
            assertEquals(OperationTypeEnum.FIX, issue.getType());
            assertTrue("MVN-513".equals(issue.getIssue()) ||
                "MVN-512".equals(issue.getIssue()));
        }
    }

    public void testExtractMultipleComplexeMessage() {
        String content = "@fix:MVN-512;Hello World1@fix:MVN-513;Hello World2" +
            "@add:MVN-514;Hello World3@fix:MVN-515;Hello World4@add:MVN-516;" +
            "Hello World5";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals(
            "Hello World1\r\nHello World2\r\nHello World3\r\nHello World4" +
            "\r\nHello World5", result.getComment());
        assertEquals(5, result.getIssues().size());

        Iterator iter = result.getIssues().iterator();

        while (iter.hasNext()) {
            Issue issue = (Issue) iter.next();

            if (OperationTypeEnum.FIX.equals(issue.getType())) {
                assertTrue("MVN-513".equals(issue.getIssue()) ||
                    "MVN-512".equals(issue.getIssue()) ||
                    "MVN-515".equals(issue.getIssue()));
            } else {
                assertEquals(OperationTypeEnum.ADD, issue.getType());
                assertTrue("MVN-514".equals(issue.getIssue()) ||
                    "MVN-516".equals(issue.getIssue()));
            }
        }
    }
}
