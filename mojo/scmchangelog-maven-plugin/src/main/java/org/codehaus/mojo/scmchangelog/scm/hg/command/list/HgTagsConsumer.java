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
package org.codehaus.mojo.scmchangelog.scm.hg.command.list;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.hg.command.HgConsumer;
import org.codehaus.mojo.scmchangelog.tags.Tag;

/**
 * Consumer for the output of the command: <code>hg tags --verbose path</code>.
 * @author ehsavoie
 * @version $Id$
 */
class HgTagsConsumer
    extends HgConsumer
{

  /**
   * List of tags found in the Mercurial repository.
   */
  private final List repositoryStatus = new ArrayList();

  /**
   * The filter on the tag names to be used.
   */
  private Pattern filter;

  /**
   * Instantiate a new HgTagsConsumer.
   * @param logger the logger.
   * @param filter the filter on the tag names to be used.
   */
  HgTagsConsumer( ScmLogger logger , Pattern filter )
  {
    super( logger );
    this.filter = filter;
  }

  /**
   * Consume a line of the command output.
   * @param status null.
   * @param trimmedLine the line.
   */
  public void doConsume( ScmFileStatus status, String trimmedLine )
  {
    getLogger().debug( trimmedLine );

    int startRevisionIndex = trimmedLine.lastIndexOf( ' ' );
    int endRevisionIndex = trimmedLine.lastIndexOf( ':' );
    String title = trimmedLine.substring( 0, startRevisionIndex );
    String revisionId = trimmedLine.substring( startRevisionIndex + 1,
        endRevisionIndex );
    if ( isTagAccepted( title ) )
    {
      Tag tag = new Tag( title );
      tag.setStartRevision( "0" );
      tag.setEndRevision( revisionId );
      updateLastTagStartRevision(revisionId);
      repositoryStatus.add( tag );
    }
  }

  private void updateLastTagStartRevision(String revisionId) {
	  if (!repositoryStatus.isEmpty()) {
		  Tag lastTag = (Tag) repositoryStatus.get(repositoryStatus.size()-1);
		  lastTag.setStartRevision(revisionId);
	  }
  }

/**
   * Return the list of Tag.
   * @return List&lt;Tag&gt;
   * @see org.codehaus.mojo.scmchangelog.tags.Tag
   */
  List getStatus()
  {
    return repositoryStatus;
  }

  /**
   * Checks if the tag name matches the filter.
   * @param title the name of the tag to be checked.
   * @return true if the tag matches - false otherwise.
   */
  protected boolean isTagAccepted( String title )
  {
    if ( filter != null )
    {
      Matcher matcher = filter.matcher( title );
      getLogger().debug( "Filtering " + title + " against " + filter.pattern()
              + " : " +  matcher.matches() );
      return matcher.matches();
    }
    return true;
  }
}
