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
package org.codehaus.mojo.scmchangelog.scm;

import java.util.regex.Pattern;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.codehaus.mojo.scmchangelog.scm.util.ScmAdapter;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.mojo.scmchangelog.changelog.log.grammar.GrammarEnum;
import org.codehaus.mojo.scmchangelog.scm.hg.HgScmAdapter;
import org.codehaus.mojo.scmchangelog.scm.hg.HgScmProvider;
import org.codehaus.mojo.scmchangelog.scm.svn.SvnScmAdapter;
import org.codehaus.mojo.scmchangelog.scm.svn.SvnXmlExeScmProvider;
import org.codehaus.mojo.scmchangelog.scm.util.DefaultScmAdapter;
import org.codehaus.plexus.util.StringUtils;

/**
 * The utility class to separate the report from the scm secific implementations.
 * 
 * @author ehsavoie
 * @version $Id$
 */
public class ScmAdapterFactory
{

  /**
   * Create the instance of ScmAdpater corresponding to the current Scm.
   * @param currentManager the ScmManager
   * @param currentGrammar the grammar.
   * @param repository the ScmRepository.
   * @param logger thelogger.
   * @return a ScmAdapter for the current SCM.
   */
  public static final ScmAdapter getInstance( ScmManager currentManager,
    GrammarEnum currentGrammar, ScmRepository repository, Log logger )
  {
    ScmAdapter adapter;
    if ( "svn".equals( repository.getProvider() ) )
    {
      adapter = new SvnScmAdapter( currentManager, currentGrammar );
    }
    else if ( "hg".equals( repository.getProvider() ) )
    {
      adapter = new HgScmAdapter( currentManager, currentGrammar );
    }
    else
    {
      adapter = new DefaultScmAdapter( currentManager, currentGrammar );
    }
    adapter.setLogger( logger );
    return adapter;
  }

  /**
   * Register the scm providers supported by this plugin.
   * @param manager the Scm manager.
   * @param grammar the selected grammar for extracting comments.
   * @param logger the current logger.
   * @param pattern the regexp pattern to filter tags and branches names.
   */
  public static final void registerProviders( ScmManager manager,
          GrammarEnum grammar, Log logger, Pattern pattern )
  {
    SvnXmlExeScmProvider svnProvider = new SvnXmlExeScmProvider( grammar ,
            pattern );
    svnProvider.setLogger( logger );
    HgScmProvider hgProvider = new HgScmProvider( grammar , pattern );
    hgProvider.setLogger( logger );
    manager.setScmProvider( "svn", svnProvider );
    manager.setScmProvider( "hg", hgProvider );
  }

  /**
   * Defines the base url to access tags.
   * This is used only in the case of a svn repository.
   * @param repository the scm repository.
   * @param tagBase the url to the tags.
   */
  public static final void setTagBase( ScmRepository repository, String tagBase )
  {
    if ( !StringUtils.isEmpty( tagBase )
          && "svn".equals( repository.getProvider() ) )
      {
        SvnScmProviderRepository svnRepo = ( SvnScmProviderRepository ) repository.getProviderRepository();
        svnRepo.setTagBase( tagBase );
      }
  }
}
