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

import java.util.ArrayList;
import java.util.List;
import org.codehaus.mojo.scmchangelog.changelog.log.Issue;
import org.codehaus.mojo.scmchangelog.changelog.log.Message;
import org.codehaus.mojo.scmchangelog.changelog.log.OperationTypeEnum;

/**
 * Grammar to accept all comments.
 * @author ehsavoie
 * @version $Id$
 */
public class AcceptAllScmGrammar extends AbstractScmGrammar
{

    /**
     * Extracts the message (list of issues)from the comment.
     * @param content the comment of the log entry.
     * @return the message
     * @see  org.codehaus.mojo.scmchangelog.changelog.log.Message
     */
    public Message extractMessage( String content )
    {
        List issues = new ArrayList();
        issues.add( new Issue( "", OperationTypeEnum.FIX ) );
        return new Message( content, issues );
    }

    /**
     * Indicates if the content respects the grammar used to extract issues.
     * @param content the content to be checked
     * @return true if the content uses the grammar - false otherwise.
     */
    public boolean hasMessage( String content )
    {
        return true;
    }

    /**
     * Returns the String to be inserted between each issue comment. It may be replaced
     * when generating the report.
     * @return the String to be inserted between each issue comment.
     */
    public String getIssueSeparator()
    {
        return NEW_LINE;
    }
}
