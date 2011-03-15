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

import org.codehaus.mojo.scmchangelog.changelog.log.grammar.GrammarEnum;
import org.codehaus.mojo.scmchangelog.changelog.log.ScmLogEntry;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.LogDocument;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.LogDocument.Factory;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.LogentryDocument.Logentry;

import org.apache.xmlbeans.XmlException;


import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import org.codehaus.mojo.scmchangelog.AbstractBufferedConsumer;

/**
 * Command stream consumer that parses the xml output from a
 * <code>svn log --xml</code> command, using a grammar to build
 * the changelog.
 * @author ehsavoie
 * @version $Id$
 *
 * @see org.apache.maven.scm.provider.svn.svnexe.command.SvnChangeLogConsumer
 * @see org.codehaus.mojo.scmchangelog.changelog.SvnChangeLogCommand
 */
public class SvnChangeLogConsumer
    extends AbstractBufferedConsumer
{

  /**
   * The elemnts result of the parsing.
   */
  private java.util.List elements = new ArrayList();
  /**
   * The grammar to be used to parse the comments.
   */
  private GrammarEnum grammar;

  /**
   * Creates a new instance of SvnChangeLogConsumer
   * @param commentGrammar the grammar to be used.
   */
  public SvnChangeLogConsumer( GrammarEnum commentGrammar )
  {
    this.grammar = commentGrammar;
  }

  /**
   * Parsing of the output.
   * @return a List of ScmLogEntry.
   */
  public java.util.List analyse()
  {
    try
    {
      LogDocument doc = Factory.parse( new StringReader( getOutput() ) );
      for ( int i = 0; i < doc.getLog().getLogentryArray().length; i++ )
      {
        Logentry logEntry = doc.getLog().getLogentryArray()[i];
        getLogger().debug( "Log entry: " + logEntry.getMsg() );
        getLogger().debug( "has message: " + grammar.hasMessage( logEntry.getMsg() ) );
        if ( grammar.hasMessage( logEntry.getMsg() ) )
        {
          ScmLogEntry entry = new ScmLogEntry();
          entry.setRevision( logEntry.getRevision().toString() );
          entry.setAuthor( logEntry.getAuthor() );
          entry.setDate( logEntry.getDate().getTime() );
          entry.setMessage( grammar.extractMessage( logEntry.getMsg() ) );
          this.elements.add( entry );
        }
      }
      return this.elements;
    }
    catch ( XmlException ex )
    {
      getLogger().error( ex );
      throw new RuntimeException( ex );
    }
    catch ( IOException ex )
    {
      getLogger().error( ex );
      throw new RuntimeException( ex );
    }
  }
}
