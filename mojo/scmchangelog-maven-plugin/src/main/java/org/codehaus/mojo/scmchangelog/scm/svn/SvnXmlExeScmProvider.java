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
package org.codehaus.mojo.scmchangelog.scm.svn;

import java.util.regex.Pattern;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.scm.log.ScmLogger;
import org.codehaus.mojo.scmchangelog.scm.util.MavenScmLogger;
import org.apache.maven.scm.provider.svn.command.SvnCommand;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.codehaus.mojo.scmchangelog.changelog.log.grammar.GrammarEnum;

/**
 * Wrapper over SvnExeScmProvider to use xml output from Subversion.
 * @author ehsavoie
 * @version $Id$
 * @plexus.component role="org.apache.maven.scm.provider.ScmProvider" role-hint="svn"
 * @see org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider
 */
public class SvnXmlExeScmProvider
    extends SvnExeScmProvider
{
  /**
   * The scm logger.
   */
  private ScmLogger logger;

  /**
   * The currentlogger.
   * @return the logger
   */
  public ScmLogger getLogger()
  {
    return logger;
  }

  /**
   * The current logger to be used.
   * @param log the maven logger to be wrapped as a ScmLogger.
   */
  public void setLogger( Log log )
  {
    this.logger = new MavenScmLogger( log );
  }

  /**
   * The comment grammar to be used.
   */
  private GrammarEnum grammar;

  /**
   * The filter on the tag names to be used.
   */
  private Pattern filter;

  /**
   * Creates a new instance of SvnXmlExeScmProvider.
   * @param commentGrammar the grammar tobe used.
   * @param filter the filter on the tag names to be used.
   */
  public SvnXmlExeScmProvider( GrammarEnum commentGrammar , Pattern filter )
  {
    this.grammar = commentGrammar;
    this.filter = filter;
  }

  /**
   * Returns a new instance of SvnCommand to execute a
   * <code>svn list --xml</code> command.
   * @return a SvnListCommand.
   */
  protected SvnCommand getListCommand()
  {
    SvnListCommand command = new SvnListCommand( this.filter );
    command.setLogger( getLogger() );
    return command;
  }

  /**
   * Returns a new instance of SvnCommand to execute a
   * <code>svn log --xml</code> command.
   * @return a SvnLogCommand.
   */
  protected SvnCommand getChangeLogCommand()
  {
    SvnCommand command = new SvnChangeLogCommand( grammar );
    command.setLogger( getLogger() );
    return command;
  }

  /**
   * Returns the String to be used as issue separator.
   * @return the String to be used as issue separator.
   */
  public String getCommentSeparator()
  {
    return this.grammar.getIssueSeparator();
  }
}
