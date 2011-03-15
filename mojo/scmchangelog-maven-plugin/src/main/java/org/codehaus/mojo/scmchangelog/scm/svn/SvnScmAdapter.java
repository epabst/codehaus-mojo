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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.list.ListScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.mojo.scmchangelog.changelog.Release;
import org.codehaus.mojo.scmchangelog.changelog.log.grammar.GrammarEnum;
import org.codehaus.mojo.scmchangelog.scm.util.ScmAdapter;
import org.codehaus.mojo.scmchangelog.scm.util.ScmTarget;
import org.codehaus.mojo.scmchangelog.tags.Tag;

/**
 * Adapter wrapping the SVN implementation.
 * @author ehsavoie
 * @version $Id$
 */
public class SvnScmAdapter extends ScmAdapter
{
  /**
   * Constructor of ScmAdapter.
   * @param currentManager the ScmManager to access SCM elements.
   * @param currentGrammar the grammar used to extract elements from the comments.
   */
  public SvnScmAdapter( ScmManager currentManager, GrammarEnum currentGrammar )
  {
    super( currentManager, currentGrammar );
  }

  /**
   * Returns the list of releases defined in the subversion repository.
   * @param repository the SCM repository.
   * @param fileSet the base fileset.
   * @return the list of releases defined in the SCM. <code>List&lt;Release&gt;</code>
   * @throws org.apache.maven.scm.ScmException in case of an error with the SCM.
   * @throws org.apache.maven.plugin.MojoExecutionException in case of an error in executing the Mojo.
   */
  public List getListOfReleases( ScmRepository repository,
      ScmFileSet fileSet )
      throws MojoExecutionException, ScmException
  {
    ListScmResult result = this.manager.list( repository, fileSet, false,
        getScmVersion( SvnTargetEnum.TAG, "" ) );
    final List tags = result.getFiles();
    getLogger().info( tags.toString() );
    final List releases = new ArrayList( 10 );
    Iterator iter = tags.iterator();

    while ( iter.hasNext() )
    {
      Tag tag = ( Tag ) iter.next();
      final ChangeLogScmResult logs = this.manager.changeLog( repository,
          fileSet,
          getScmVersion( SvnTargetEnum.TRUNK, tag.getStartRevision() ),
          getScmVersion( SvnTargetEnum.TRUNK, tag.getEndRevision() ), "" );
      if ( logs.getChangeLog() != null )
      {
        Release release = new Release( tag,
            logs.getChangeLog().getChangeSets() );
        releases.add( release );
      }
    }
    String endRevision = "0";
    if ( !tags.isEmpty() )
    {
      endRevision = ( ( Tag ) tags.get( tags.size() - 1 ) ).getEndRevision();
    }
    getLogger().info( "End revision : " + endRevision );
    final Tag trunk = new Tag( "trunk" );
    trunk.setStartRevision( endRevision );
    trunk.setDate( new Date() );
    trunk.setEndRevision( null );

    final ChangeLogScmResult logs = this.manager.changeLog( repository,
        fileSet, getScmVersion( SvnTargetEnum.TRUNK, endRevision ), null, "" );
    if ( logs.getChangeLog() != null )
    {
      final Release release = new Release( trunk,  logs.getChangeLog().getChangeSets() );
      releases.add( release );
    }
    Collections.reverse( releases );
    return releases;
  }

  /**
   * Returns the Scm version.
   * @param versionType the type of version (tag, trunk, branch).
   * @param version the tag/branche name.
   * @return the corresponding ScmVersion.
   * @throws org.apache.maven.plugin.MojoExecutionException in case of an error in executing the Mojo.
   */
  public ScmVersion getScmVersion( ScmTarget versionType, String version )
      throws MojoExecutionException
  {
    if ( SvnTargetEnum.TAG.equals( versionType ) )
    {
      return new ScmTag( version );
    }
    else if ( SvnTargetEnum.BRANCH.equals( versionType ) )
    {
      return new ScmBranch( version );
    }
    else if ( SvnTargetEnum.TRUNK.equals( versionType ) )
    {
      return new ScmRevision( version );
    }
    throw new MojoExecutionException( "Unknown version type : "
        + versionType );
  }

}
