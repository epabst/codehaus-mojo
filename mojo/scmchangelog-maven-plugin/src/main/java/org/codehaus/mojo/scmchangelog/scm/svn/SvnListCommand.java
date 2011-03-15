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
import java.util.Iterator;

import java.util.regex.Pattern;
import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.list.AbstractListCommand;
import org.apache.maven.scm.command.list.ListScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.command.SvnCommand;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnexe.command.SvnCommandLineUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Command to list files in SVN ( <code>svn list --xml</code> command.
 * @author ehsavoie
 * @version $Id$
 */
public class SvnListCommand
    extends AbstractListCommand
    implements SvnCommand
{
  /**
   * Temporary dir, where the command is executed.
   */
  private static final File TMP_DIR = new File( System.getProperty( "java.io.tmpdir" ) );

 /**
   * The filter on the tag names to be used.
   */
  private Pattern filter;

  /**
   * Creates a new instance of SvnListCommand.
   * @param filter the filter on the tag names to be used.
   */
  public SvnListCommand( Pattern filter )
  {
    this.filter = filter;
  }

  /**
   * Executes a <code>svn list --xml repository_url</code> command.
   * @param repository the Subversion repository.
   * @param fileSet the list of files.
   * @param recursive true if we want a recursive list command - false otherwise.
   * @param version the version target (branch, tags, trunk).
   * @return a ListScmResult containing a List&gt;Tag&lt;.
   * @throws org.apache.maven.scm.ScmException in case of an error with the SCM.
   */
  protected ListScmResult executeListCommand( ScmProviderRepository repository, ScmFileSet fileSet,
      boolean recursive, ScmVersion version )
      throws ScmException
  {
    getLogger().info( "Executing our command " + version );
    Commandline cl = createCommandLine( ( SvnScmProviderRepository ) repository, fileSet, recursive, version );
    SvnListConsumer consumer = new SvnListConsumer( this.filter );
    consumer.setLogger( getLogger() );

    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();
    getLogger().info( "Executing: " + SvnCommandLineUtils.cryptPassword( cl ) );
    getLogger().info( "Working directory: "  + cl.getWorkingDirectory().getAbsolutePath() );
    int exitCode;
    getLogger().info( "Executing this command " + cl.toString() );
    try
    {
      exitCode = SvnCommandLineUtils.execute( cl, consumer, stderr, getLogger() );
    }
    catch ( CommandLineException ex )
    {
      getLogger().error( "Error while executing command.", ex );
      throw new ScmException( "Error while executing command.", ex );
    }

    if ( exitCode != 0 )
    {
      return new ListScmResult( cl.toString(), "The svn command failed.",
          stderr.getOutput(), false );
    }

    return new ListScmResult( cl.toString(), consumer.analyse() );
  }

  /**
   * Create the command line for svn list.
   * @param repository the URL to the repository.
   * @param fileSet the fileset.
   * @param recursive true if we want the --recursive option - false otherwise.
   * @param version the beginning and end revisions.
   * @return the command line to be executed against the SCM.
   */
  static Commandline createCommandLine( SvnScmProviderRepository repository,
      ScmFileSet fileSet, boolean recursive, ScmVersion version )
  {
    Commandline cl = SvnCommandLineUtils.getBaseSvnCommandLine( TMP_DIR,
        repository );
    cl.createArgument().setValue( "list" );

    if ( recursive )
    {
      cl.createArgument().setValue( "--recursive" );
    }

    cl.createArgument().setValue( "--xml" );

    if ( ( version != null ) 
        && StringUtils.isNotEmpty( version.getName() ) 
        && version instanceof ScmRevision )
    {
      cl.createArgument().setValue( "-r" );
      cl.createArgument().setValue( version.getName() );
    }

    String baseUrl;

    if ( version instanceof ScmTag )
    {
      baseUrl = repository.getTagBase();
    } 
    else if ( version instanceof ScmBranch )
    {
      baseUrl = repository.getBranchBase();
    }
    else
    {
      baseUrl = repository.getUrl();
    }

    Iterator it = fileSet.getFileList().iterator();

    while ( it.hasNext() )
    {
      File file = ( File ) it.next();
      cl.createArgument().setValue( baseUrl + "/" + file.getPath().replace( '\\', '/' ) );
    }

    return cl;
  }
}
