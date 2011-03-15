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

import java.util.Date;

/**
 * Represents a log entry from the subversion repository.
 * @author ehsavoie
 * @version $Id$
 */
public class ScmLogEntry
    implements Comparable
{

  /**
   * Holds value of property revision.
   */
  private String revision;
  /**
   * Holds value of property date.
   */
  private Date date;
  /**
   * Holds value of property message.
   */
  private Message message;
  /**
   * Holds value of property author.
   */
  private String author;

  /** Creates a new instance of ScmLogEntry */
  public ScmLogEntry()
  {
  }

  /**
   * Getter for property revision.
   * @return Value of property revision.
   */
  public String getRevision()
  {
    return this.revision;
  }

  /**
   * Setter for property revision.
   * @param revision New value of property revision.
   */
  public void setRevision( String revision )
  {
    this.revision = revision;
  }

  /**
   * Getter for property date.
   * @return Value of property date.
   */
  public Date getDate()
  {
    return this.date;
  }

  /**
   * Setter for property date.
   * @param date New value of property date.
   */
  public void setDate( Date date )
  {
    this.date = date;
  }

  /**
   * Getter for property message.
   * @return Value of property message.
   */
  public Message getMessage()
  {
    return this.message;
  }

  /**
   * Setter for property message.
   * @param message New value of property message.
   */
  public void setMessage( Message message )
  {
    this.message = message;
  }

  /**
   * Getter for property author.
   * @return Value of property author.
   */
  public String getAuthor()
  {
    return this.author;
  }

  /**
   * Setter for property author.
   * @param author New value of property author.
   */
  public void setAuthor( String author )
  {
    this.author = author;
  }

  /**
   * Compare method, to order log entries.
   * @param object the object to be compred with this.
   * @return a positive integer if this is after object -
   * a negative integer if this is before object and 0 if they are equal.
   */
  public int compareTo( Object object )
  {
    if ( object != null )
    {
      ScmLogEntry entry = (ScmLogEntry) object;
      try
      {
        int currentRevision = Integer.parseInt( revision );
        int otherRevision = Integer.parseInt( entry.getRevision() );
        return currentRevision - otherRevision;
      }
      catch ( NumberFormatException nfex )
      {
        //Do nothing revisions are not numbers.
      }
      return this.revision.compareTo( entry.getRevision() );
    }
    return -1;
  }
}
