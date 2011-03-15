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

import java.io.File;

import java.util.regex.Pattern;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.list.AbstractListCommand;
import org.apache.maven.scm.command.list.ListScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.hg.HgUtils;

/**
 * List command for Mercurial : the command used is: <code>hg tags --verbose path</code.>
 * @author ehsavoie
 * @version $Id$
 */
public class HgListCommand
    extends AbstractListCommand
{

  /**
   * The name of the Tag tag in Mercurial.
   */
  public static final String TAGS_CMD = "tags";

  /**
   * The name of the Trunk/HEAD tag in Mercurial.
   */
  public static final String TRUNK_TAG = "tip";

  /**
   * The filter on the tag names to be used.
   */
  private Pattern filter;

  /**
   * Creates a new instance of HgListCommand.
   * @param filter the filter on the tag names to be used.
   */
  public HgListCommand( Pattern filter )
  {
    this.filter = filter;
  }
  
  /**
   * Executes a <code>hg tags --verbose path</code> command.
   * @param repository the mercurial repository.
   * @param fileSet the list of files.
   * @param recursive true if we want a recursive list command - false otherwise.
   * @param version the version target (branch, tags, trunk).
   * @return a ListScmResult containing a List&gt;Tag&lt;.
   * @throws org.apache.maven.scm.ScmException in case of an error with the scm command.
   */
  protected ListScmResult executeListCommand( ScmProviderRepository repository,
      ScmFileSet fileSet, boolean recursive, ScmVersion version )
      throws ScmException
  {
    String[] tagsCmd = new String[]
    {
      TAGS_CMD
    };
    File workingDir = fileSet.getBasedir();
    HgTagsConsumer consumer = new HgTagsConsumer( getLogger() , filter );
    ScmResult result = HgUtils.execute( consumer, getLogger(), workingDir,
        tagsCmd );

    return new ListScmResult( consumer.getStatus(), result );
  }
}
