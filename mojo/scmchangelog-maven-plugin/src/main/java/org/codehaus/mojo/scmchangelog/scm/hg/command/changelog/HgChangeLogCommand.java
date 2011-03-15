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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.AbstractChangeLogCommand;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.hg.HgUtils;
import org.apache.maven.scm.provider.hg.command.HgCommandConstants;


/**
 * Excute the command <code>hg log --verbose -rx:y</code>.
 * @author ehsavoie
 * @version $Id$
 *
 * @see org.apache.maven.scm.provider.hg.command.HgCommand
 * @see org.codehaus.mojo.scmchangelog.scm.hg.changelog.HgChangeLogConsumer
 */
public class HgChangeLogCommand
    extends AbstractChangeLogCommand    
{
  /**
   * Execute the command <code>hg log --verbose -rx:y</code>.
   * @param repository the Mercurial repository.
   * @param fileSet the files on which to execute the command.
   * @param startVersion the starting revision (x).
   * @param endVersion the last revision (y).
   * @param datePattern the pattern for parsing dates.
   * @return the changelog results.
   * @throws org.apache.maven.scm.ScmException  in case of an error with the scm command.
   */
  protected ChangeLogScmResult executeChangeLogCommand(
      ScmProviderRepository repository, ScmFileSet fileSet,
      ScmVersion startVersion, ScmVersion endVersion, String datePattern )
      throws ScmException
  {
    String revisions = "-r"
        + startVersion.getName() 
        + ':'
        + endVersion.getName();
    String[] cmd = new String[]
    {
      HgCommandConstants.LOG_CMD, HgCommandConstants.VERBOSE_OPTION, revisions
    };
    ScmLogger logger = getLogger();
    HgChangeLogConsumer consumer = new HgChangeLogConsumer( logger,
        datePattern );
    ScmResult result = HgUtils.execute( consumer, logger,
        fileSet.getBasedir(), cmd );
    logger.debug( result.toString() );

    List logEntries = consumer.getModifications();
    List inRangeAndValid = new ArrayList();
    Date startDate = new Date( 0 ); //From 1. Jan 1970
    Date endDate = new Date(); //Upto now
    Iterator it = logEntries.iterator();

    while ( it.hasNext() )
    {
      ChangeSet change = ( ChangeSet ) it.next();

      if ( change.getFiles().size() > 0 )
      {
        inRangeAndValid.add( change );
      }
    }

    ChangeLogSet changeLogSet = new ChangeLogSet( inRangeAndValid,
        startDate, endDate );

    return new ChangeLogScmResult( changeLogSet, result );
  }

  /**
   * Execute the command <code>hg log --verbose -rx:y</code>.
   * @param repository the Mercurial repository.
   * @param fileSet the files on which to execute the command.
   * @param startDate the start date.
   * @param endDate the end date.
   * @param branch used to get the revisions.
   * @param datePattern the pattern for parsing dates.
   * @return the changelog results.
   * @throws org.apache.maven.scm.ScmException  in case of an error with the scm command.
   */
  protected ChangeLogScmResult executeChangeLogCommand(
      ScmProviderRepository repository, ScmFileSet fileSet, Date startDate,
      Date endDate, ScmBranch branch, String datePattern )
      throws ScmException
  {
    String revisions = "-r " 
        + branch.getName();
    String[] cmd = new String[]
    {
      HgCommandConstants.LOG_CMD, HgCommandConstants.VERBOSE_OPTION, revisions
    };
    HgChangeLogConsumer consumer = new HgChangeLogConsumer( getLogger(),
        datePattern );
    ScmResult result = HgUtils.execute( consumer, getLogger(),
        fileSet.getBasedir(), cmd );
    List logEntries = consumer.getModifications();
    List inRangeAndValid = new ArrayList();
    startDate = ( startDate == null ) ? new Date( 0 ) : startDate; //From 1. Jan 1970
    endDate = ( endDate == null ) ? new Date() : endDate; //Upto now
    Iterator it = logEntries.iterator();
    while ( it.hasNext() )
    {
      ChangeSet change = ( ChangeSet ) it.next();
      if ( change.getFiles().size() > 0 )
      {
        if ( !change.getDate().before( startDate )
            && !change.getDate().after( endDate ) )
        {
          inRangeAndValid.add( change );
        }
      }
    }
    ChangeLogSet changeLogSet = new ChangeLogSet( inRangeAndValid,
        startDate, endDate );
    return new ChangeLogScmResult( changeLogSet, result );
  }
}
