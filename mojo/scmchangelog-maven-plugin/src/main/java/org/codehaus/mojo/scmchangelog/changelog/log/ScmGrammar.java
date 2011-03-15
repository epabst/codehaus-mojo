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
 * This interface represents the grammar used to manipulate the SCM comments extracting informations
 * @author ehsavoie
 * @version $Id$
 */
public interface ScmGrammar
{

  /**
   * Default separator for issues.
   */
  String NEW_LINE = "\r\n";

  /**
   * Extract a Message from a comment.
   * @param content <html>
   *  <head>
   *
   *  </head>
   *  <body>
   *    <p style="margin-top: 0">
   *      the comment to be parsed and from which a Message will be extracted
   *    </p>
   *  </body>
   * </html>
   *
   * @return <html>
   *  <head>
   *
   *  </head>
   *  <body>
   *    <p style="margin-top: 0">
   *      the Message extracted from the content
   *    </p>
   *  </body>
   * </html>
   */
  Message extractMessage( String content );

  /**
   * Indicates if the content has a Message to be extracted.
   * @param content <html>
   *  <head>
   *
   *  </head>
   *  <body>
   *    <p style="margin-top: 0">
   *      the content to be tested
   *    </p>
   *  </body>
   * </html>
   *
   * @return <html>
   *  <head>
   *
   *  </head>
   *  <body>
   *    <p style="margin-top: 0">
   *      true if a Message can be extracted - false otherwise
   *    </p>
   *  </body>
   * </html>
   */
  boolean hasMessage( String content );

  /**
   * Returns the String to be inserted between each issue comment. It may be replaced
   * when generating the report.
   * @return the String to be inserted between each issue comment.
   */
  String getIssueSeparator();

  /**
   * Removes the comments from the SCM comments. Comments are marked by being
   * surrounded by &qutote;---&quote;.
   * @param content - the content from which comments will be removed.
   * @return the content without the elemets commented.
   */
  String removeComments( String content );

}
