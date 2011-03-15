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

import junit.framework.TestCase;
import org.codehaus.mojo.scmchangelog.changelog.log.Issue;
import org.codehaus.mojo.scmchangelog.changelog.log.Message;
import org.codehaus.mojo.scmchangelog.changelog.log.OperationTypeEnum;

/**
 *
 * @author ehsavoie
 */
public class AcceptAllScmGrammarTest extends TestCase
{
    private GrammarEnum grammar;

    public AcceptAllScmGrammarTest( String testName )
    {
        super(testName);
    }

    protected void setUp() throws Exception {
        grammar = GrammarEnum.valueOf("ALL");
    }

    protected void tearDown() throws Exception {
    }

    public void testExtractFixMessage() {
        String content = "@fix:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals(content, result.getComment());
        assertEquals(1, result.getIssues().size());

		Issue issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.FIX, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractRemoveMessage() {
        String content = "@remove:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals(content, result.getComment());
        assertEquals(1, result.getIssues().size());

		Issue issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.FIX, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractAddMessage() {
        String content = "@add:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals(content, result.getComment());
        assertEquals(1, result.getIssues().size());

		Issue issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.FIX, issue.getType());
		assertEquals("", issue.getIssue());
    }

    public void testExtractUpdateMessage() {
        String content = "@update:MVN-512;Hello World";
        Message result = grammar.extractMessage(content);
        assertNotNull(result);
        assertNotNull(result.getIssues());
        assertEquals(content, result.getComment());
        assertEquals(1, result.getIssues().size());

		Issue issue = (Issue) result.getIssues().get(0);
		assertNotNull(issue);
		assertEquals(OperationTypeEnum.FIX, issue.getType());
		assertEquals("", issue.getIssue());
    }

    
}
