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
package org.codehaus.mojo.scmchangelog.changelog;

import java.util.List;

import org.codehaus.mojo.scmchangelog.tags.Tag;

/**
 * Represents a release of the product.
 * A release is a list of log entries for a tag.
 * @author ehsavoie
 * @version $Id$
 *
 * @see org.codehaus.mojo.scmchangelog.changelog.log.ScmLogEntry
 * @see org.codehaus.mojo.scmchangelog.tags.Tag
 */
public class Release
{

  /**
   * Tag for this release.
   */
  private Tag tag;
  /**
   * List of SvnLogEntry for this release.
   * @see org.codehaus.mojo.scmchangelog.changelog.log.ScmLogEntry
   */
  private List entries;

  /**
   * Creates a new instance of Release
   * @param releaseTag the tag corresponding to this release.
   * @param logEntries the log entries for this release.
   */
  public Release( Tag releaseTag, List logEntries )
  {
    this.tag = releaseTag;
    this.entries = logEntries;
  }

  /**
   * Return the log entries for his release.
   * @return a list of ScmLogEntry for this release.
   */
  public List getEntries()
  {
    return this.entries;
  }

  /**
   * Return the tag for his release.
   * @return the tag corresponding to this release.
   */
  public Tag getTag()
  {
    return this.tag;
  }
}
