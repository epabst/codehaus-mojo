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
package org.codehaus.mojo.scmchangelog.scm.hg.command.changelog;

import java.util.Date;
import java.util.List;

import org.apache.maven.scm.ChangeSet;

/**
 * ChangeSet with the revision.
 * @author ehsavoie
 * @version $Id$
 *
 * @see org.apache.maven.scm.ChangeSet
 */
public class BetterChangeSet
    extends ChangeSet
{

  /**
   * The SCM revision id for this changeset.
   */
  private String revision;

  /**
   * Constructor of a ChangeSet.
   * @param strDate         Date the changes were committed.
   * @param userDatePattern pattern of date.
   * @param comment         comment provided at commit time.
   * @param author          User who made changes.
   * @param files           The ChangeFile list.
   * @param currentRevision The revision.
   */
  public BetterChangeSet( String strDate, String userDatePattern,
      String comment, String author, List /*<ChangeFile>*/ files,
      String currentRevision )
  {
    super( strDate, userDatePattern, comment, author, files );
    this.revision = currentRevision;
  }

  /**
   * Constructor of a ChangeSet.
   * @param date    Date the changes were committed
   * @param comment comment provided at commit time
   * @param author  User who made changes
   * @param files   The ChangeFile list
   * @param currentRevision The revision.
   */
  public BetterChangeSet( Date date, String comment, String author,
      List /*<ChangeFile>*/ files, String currentRevision )
  {
    super( date, comment, author, files );
    this.revision = currentRevision;
  }

  /**
   * Constructor used when attributes aren't available until later
   */
  public BetterChangeSet()
  {
    super();
  }

  /**
   * Getter for the SCM revision id of this changeset.
   * @return the SCM revision id of this changeset.
   */
  public String getRevision()
  {
    return this.revision;
  }

  /**
   * Setter for the SCM revision id of this changeset.
   * @param revision the SCM revision id of this changeset.
   */
  public void setRevision( String revision )
  {
    this.revision = revision;
  }
}
