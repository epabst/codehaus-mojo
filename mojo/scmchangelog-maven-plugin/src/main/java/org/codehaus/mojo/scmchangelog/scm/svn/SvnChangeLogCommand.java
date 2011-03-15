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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.AbstractChangeLogCommand;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.SvnTagBranchUtils;
import org.apache.maven.scm.provider.svn.command.SvnCommand;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnexe.command.SvnCommandLineUtils;
import org.codehaus.mojo.scmchangelog.changelog.log.grammar.GrammarEnum;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Command to get log entries from the SVN ( <code>svn log --xml</code> command, then consume it with our parser.
 * @author ehsavoie
 * @version $Id$
 *
 * @see org.apache.maven.scm.provider.svn.svnexe.command.SvnChangeLogCommand
 * @see org.codehaus.mojo.scmchangelog.changelog.SvnChangeLogConsumer
 */
public class SvnChangeLogCommand
    extends AbstractChangeLogCommand
    implements SvnCommand
{

  /**
   * Date format to be used when using a date with the command.
   */
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
  /**
   * The grammar to be used to parse the comments.
   */
  private GrammarEnum grammar;

  /**
   * Creates a new instance of SvnChangeLogCommand.
   * @param commentGrammar the grammar to be used toparse the comments.
   */
  public SvnChangeLogCommand( GrammarEnum commentGrammar )
  {
    this.grammar = commentGrammar;
  }

  /**
   * Execute the command.
   * @param repo the repository.
   * @param fileSet the list of files.
   * @param startVersion starting revision for the command.
   * @param endVersion end revision for the comand.
   * @param datePattern datePattern for formatting dates.
   * @return the result of the command.
   * @throws org.apache.maven.scm.ScmException in case of an error with the scm command.
   */
  protected ChangeLogScmResult executeChangeLogCommand(
      ScmProviderRepository repo, ScmFileSet fileSet,
      ScmVersion startVersion, ScmVersion endVersion, String datePattern )
      throws ScmException
  {
    return executeChangeLogCommand( repo, fileSet, null, datePattern, startVersion, endVersion );
  }

  /**
   * Execute the command.
   * @param repo the repository.
   * @param fileSet the list of files.
   * @param startDate starting date of the revision for the command.
   * @param endDate end date of the revision for the comand.
   * @param branch the selected branch/tag.
   * @param datePattern datePattern for formatting dates.
   * @return the result of the command.
   * @throws org.apache.maven.scm.ScmException in case of an error with the scm command.
   */
  protected ChangeLogScmResult executeChangeLogCommand(
      ScmProviderRepository repo, ScmFileSet fileSet, Date startDate,
      Date endDate, ScmBranch branch, String datePattern )
      throws ScmException
  {
    return executeChangeLogCommand( repo, fileSet, branch, datePattern, null, null );
  }

  /**
   * Execute the command.
   * @param repo the repository.
   * @param fileSet the list of files.
   * @param branch the selected branch/tag.
   * @param datePattern datePattern for formatting dates.
   * @param startVersion starting revision for the command.
   * @param endVersion end revision for the comand.
   * @return the result of the command.
   * @throws org.apache.maven.scm.ScmException in case of an error with the scm command.
   */
  protected ChangeLogScmResult executeChangeLogCommand(
      ScmProviderRepository repo, ScmFileSet fileSet, ScmBranch branch,
      String datePattern, ScmVersion startVersion, ScmVersion endVersion )
      throws ScmException
  {
    Commandline cl = createCommandLine( ( SvnScmProviderRepository ) repo,
        fileSet.getBasedir(), branch, startVersion, endVersion );
    SvnChangeLogConsumer consumer = new SvnChangeLogConsumer( grammar );
    consumer.setLogger( this.getLogger() );
    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();
    getLogger().info( "Executing: " + SvnCommandLineUtils.cryptPassword( cl ) );
    getLogger().info( "Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );
    int exitCode;
    try
    {
      exitCode = SvnCommandLineUtils.execute( cl, consumer, stderr, getLogger() );
    }
    catch ( CommandLineException ex )
    {
      getLogger().error( "Error while executing command.", ex );
      throw new ScmException( "Error while executing svn command.", ex );
    }

    if ( exitCode != 0 )
    {
      getLogger().error( "The svn command failed." + stderr.getOutput() );
      return new ChangeLogScmResult( cl.toString(), "The svn command failed.", stderr.getOutput(), false );
    }

    ChangeLogSet changeLogSet = new ChangeLogSet( consumer.analyse(), null, null );
    changeLogSet.setStartVersion( startVersion );
    changeLogSet.setEndVersion( endVersion );

    return new ChangeLogScmResult( cl.toString(), changeLogSet );
  }

  /**
   * Creates a command line for <code>svn log</code>.
   * @param repository the subversion repository.
   * @param workingDirectory the working directory.
   * @param branch the branch to be used.
   * @param startVersion starting revision for the command.
   * @param endVersion end revision for the comand.
   * @return the result of the command.
   */
  public static Commandline createCommandLine(
      SvnScmProviderRepository repository, File workingDirectory,
      ScmBranch branch, ScmVersion startVersion, ScmVersion endVersion )
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
    dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );

    Commandline cl = SvnCommandLineUtils.getBaseSvnCommandLine( workingDirectory,
        repository );
    cl.createArgument().setValue( "log" );
    cl.createArgument().setValue( "--xml" );

    if ( startVersion != null )
    {
      cl.createArgument().setValue( "-r" );

      if ( endVersion != null )
      {
        if ( startVersion.getName().equals( endVersion.getName() ) )
        {
          cl.createArgument().setValue( startVersion.getName() );
        } 
        else
        {
          cl.createArgument().setValue( startVersion.getName()
              + ':'
              + endVersion.getName() );
        }
      } 
      else
      {
        cl.createArgument().setValue( startVersion.getName() 
            + ":HEAD" );
      }
    }

    if ( ( branch != null ) && StringUtils.isNotEmpty( branch.getName() ) )
    {
      // By specifying a branch and this repository url below, subversion should show
      // the changelog of that branch, but limit it to paths that also occur in this repository.
      if ( branch instanceof ScmTag )
      {
        cl.createArgument().setValue( SvnTagBranchUtils.resolveTagUrl( repository, ( ScmTag ) branch ) );
      } 
      else
      {
        cl.createArgument().setValue( SvnTagBranchUtils.resolveBranchUrl( repository, branch ) );
      }
    }
    cl.createArgument().setValue( repository.getUrl() );

    return cl;
  }
}
