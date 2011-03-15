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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.hg.command.HgConsumer;

/**
 * Command consumer that parses the output from a <code>hg log --verbose -rx:y</code> 
 * command, using a grammar to build  the changelog.
 * @author ehsavoie
 * @version $Id$
 *
 * @see org.apache.maven.scm.provider.hg.command.HgConsumer
 * @see org.codehaus.mojo.scmchangelog.scm.hg.changelog.HgChangeLogCommand
 */
public class HgChangeLogConsumer
    extends HgConsumer
{

  /**
   * The time stamp format.
   */
  private static final String TIME_PATTERN = "EEE MMM dd HH:mm:ss yyyy Z";
  /**
   * The changeset element in the changelog.
   */
  private static final String REVNO_TAG = "changeset: ";
  /**
   * The tag element in the changelog.
   */
  private static final String TAG_TAG = "tag:         ";
  /**
   * The author element in the changelog.
   */
  private static final String AUTHOR_TAG = "user: ";
  /**
   * The timestamp element in the changelog.
   */
  private static final String TIME_STAMP_TOKEN = "date: ";
  /**
   * The message element in the changelog.
   */
  private static final String MESSAGE_TOKEN = "description:";
  /**
   * The merged element in the changelog.
   */
  private static final String MERGED_TOKEN = "merged: ";
  /**
   * The files element in the changelog.
   */
  private static final String FILES_TOKEN = "files: ";
  /**
   * The previous line to the current parsed line.
   */
  private String prevLine = "";
  /**
   * The previous previous line to the current parsed line.
   */
  private String prevPrevLine = "";
  /**
   * The entries resulting from parsing this changelog output.
   */
  private ArrayList logEntries = new ArrayList();
  /**
   * The current entry.
   */
  private BetterChangeSet currentChange;
  /**
   * Last entry in the current changes.
   */
  private BetterChangeSet lastChange;
  /**
   * Indicates if it is a merge entry.
   */
  private boolean isMergeEntry;
  /**
   * The current revision of the entry.
   */
  private String currentRevision;
  /**
   * Not used.
   */
  private String currentTag; // don't know what to do with this
  /**
   * To specified another date pattern.
   */
  private String userDatePattern;
  /**
   * Used for multiline comments.
   */
  private boolean spoolingComments;
  /**
   * List of comments for the current entry.
   */
  private List currentComment = null;

  /**
   * Construtor for HgChangeLogConsumer.
   * @param logger the logger used for trace.
   * @param userDatePattern the pattern for parsing dates.
   */
  public HgChangeLogConsumer( ScmLogger logger, String userDatePattern )
  {
    super( logger );

    this.userDatePattern = userDatePattern;
  }

  /**
   * Return a list of BetterChangeSet.
   * @return a List&lt;BetterChangeSet&gt;
   * @see org.codehaus.mojo.scmchangelog.scm.hg.changelog.BetterChangeSet
   */
  public List getModifications()
  {
    Collections.reverse( logEntries );
    return logEntries;
  }

  /**
   * Consume a line of the output of the command.
   * @param line the line to be interpreted.
   */
  public void consumeLine( String line )
  {
    // override default behaviour which tries to pick through things for some standard messages.  that
    // does not apply here
    doConsume( null, line );
  }

  /**
   * Consume a line
   * @param status null.
   * @param line a line of the command output.
   */
  public void doConsume( ScmFileStatus status, String line )
  {
    String tmpLine = line;

    // If current status == null then this is a new entry
    // If the line == "" and previous line was "", then this is also a new entry
    if ( ( line.equals( "" ) && ( prevLine.equals( "" ) && prevPrevLine.equals( "" ) ) ) || ( currentComment == null ) )
    {
      if ( currentComment != null )
      {
        StringBuffer comment = new StringBuffer();

        for ( int i = 0; i < ( currentComment.size() - 1 ); i++ )
        {
          comment.append( currentComment.get( i ) );

          if ( ( i + 1 ) < ( currentComment.size() - 1 ) )
          {
            comment.append( '\n' );
          }
        }
        currentChange.setComment( comment.toString() );
      }
      spoolingComments = false;

      //If last entry was part a merged entry
      if ( isMergeEntry 
          && ( lastChange != null ) )
      {
        String comment = lastChange.getComment();
        comment += ( "\n[MAVEN]: Merged from " 
            + currentChange.getAuthor() );
        comment += ( "\n[MAVEN]:    " 
            + currentChange.getDateFormatted() );
        comment += ( "\n[MAVEN]:    " 
            + currentChange.getComment() );
        lastChange.setComment( comment );
      }

      //Init a new changeset
      currentChange = new BetterChangeSet();
      currentChange.setFiles( new ArrayList() );
      logEntries.add( currentChange );

      //Reset memeber vars
      currentComment = new ArrayList();
      currentRevision = "";
      isMergeEntry = false;
    }

    if ( spoolingComments )
    {
      currentComment.add( line );
    } 
    else if ( line.startsWith( MESSAGE_TOKEN ) )
    {
      spoolingComments = true;
    } 
    else if ( line.startsWith( MERGED_TOKEN ) )
    {
      //This is part of lastChange and is not a separate log entry
      isMergeEntry = true;
      logEntries.remove( currentChange );

      if ( logEntries.size() > 0 )
      {
        lastChange = ( BetterChangeSet ) logEntries.get( logEntries.size() - 1 );
      } 
      else
      {
        getLogger().warn( "First entry was unexpectedly a merged entry" );
        lastChange = null;
      }
    } 
    else if ( line.startsWith( REVNO_TAG ) )
    {
      tmpLine = line.substring( REVNO_TAG.length() );
      tmpLine = tmpLine.trim();
      currentRevision = tmpLine;
      currentChange.setRevision( currentRevision.substring( 0,
          currentRevision.indexOf( ':' ) ) );
    } 
    else if ( line.startsWith( TAG_TAG ) )
    {
      tmpLine = line.substring( TAG_TAG.length() ).trim();
      currentTag = tmpLine;
    }
    else if ( line.startsWith( AUTHOR_TAG ) )
    {
      tmpLine = line.substring( AUTHOR_TAG.length() );
      tmpLine = tmpLine.trim();
      currentChange.setAuthor( tmpLine );
    } 
    else if ( line.startsWith( TIME_STAMP_TOKEN ) )
    {
      // TODO: FIX Date Parsing to match Mercurial or fix with template
      tmpLine = line.substring( TIME_STAMP_TOKEN.length() ).trim();

      Date date = parseDate( tmpLine, userDatePattern, TIME_PATTERN,
          Locale.ENGLISH );
      currentChange.setDate( date );
    } 
    else if ( line.startsWith( FILES_TOKEN ) )
    {
      tmpLine = line.substring( FILES_TOKEN.length() ).trim();

      String[] files = tmpLine.split( " " );

      for ( int i = 0; i < files.length; i++ )
      {
        String file = files[i];
        ChangeFile changeFile = new ChangeFile( file, currentRevision );
        currentChange.addFile( changeFile );
      }
    } 
    else
    {
      getLogger().warn( "Could not figure out: " + line );
    }

    // record previous line
    prevLine = line;
    prevPrevLine = prevLine;
  }
}
