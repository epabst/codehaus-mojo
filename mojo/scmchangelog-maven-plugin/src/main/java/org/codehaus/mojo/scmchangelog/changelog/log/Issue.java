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

/**
 * Represents an issue with its tracker's id and its type.
 * @author ehsavoie
 * @version $Id$
 */
public class Issue
{

  /**
   * The id of the issue.
   */
  private String issue;
  /**
   * The operation (FIX/ADD/UPDATE/REMOVE).
   */
  private OperationTypeEnum type;

  /**
   * Creates a new instance of Issue.
   * @param issue tracker's id.
   * @param type fix/add/remove/update.
   */
  public Issue( String issue, OperationTypeEnum type )
  {
    this.issue = issue;
    this.type = type;
  }

  /**
   * Getter for the issue id.
   * @return the issue id.
   */
  public String getIssue()
  {
    return issue;
  }

  /**
   * Getter for the issue operation.
   * @return the issue operation.
   */
  public OperationTypeEnum getType()
  {
    return type;
  }
}
