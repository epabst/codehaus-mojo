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
package org.codehaus.mojo.scmchangelog.changelog.log;

import java.util.List;

/**
 * Represents the comment message of a log entry from the subversion repository.
 * It can be one message for several issues.
 * @author ehsavoie
 * @version $Id$
 */
public class Message
{

  /**
   * The comment cleaned of grammar elements, with the Grammar.issueSeparator
   * between each issue comment.
   */
  private String comment;
  /**
   * The issues extracted from the comment.
   */
  private List issues;

  /**
   * Creates a new instance of Message
   * @param cleanedComment : the original comment.
   * @param foundIssues : the issues of this log entry.
   */
  public Message( String cleanedComment, List foundIssues )
  {
    this.comment = cleanedComment;
    this.issues = foundIssues;
  }

  /**
   * Return the comment cleaned of grammar elements, with the Grammar.issueSeparator
   * between each issue comment.
   * @return the comment cleaned of grammar elements, with the Grammar.issueSeparator
   * between each issue comment.
   */
  public String getComment()
  {
    return comment;
  }

  /**
   * Return the issues extracted from the comment.
   * @return a List of Issue.
   * @see org.codehaus.mojo.scmchangelog.changelog.log.Issue
   */
  public List getIssues()
  {
    return issues;
  }
}
