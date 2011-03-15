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

import org.codehaus.mojo.scmchangelog.AbstractBufferedConsumer;
import org.codehaus.mojo.scmchangelog.tags.Tag;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.CommitDocument.Commit;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.EntryDocument.Entry;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.ListDocument.List;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.ListsDocument;
import org.codehaus.mojo.scmchangelog.scm.svn.xml.ListsDocument.Factory;

import org.apache.xmlbeans.XmlException;


import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses the output of the command and returns a list of Tag.
 * @author ehsavoie
 * @version $Id$
 */
public class SvnListConsumer
    extends AbstractBufferedConsumer
{

  /**
   * The elements result of the parsing.
   */
  private java.util.List elements = new ArrayList();


  /**
   * The filter on the tag names to be used.
   */
  private Pattern filter;

  /**
   * Create new instance of the SvnListConsumer.
   * @param filter the filter on the tag names to be used.
   */
  public SvnListConsumer ( Pattern filter )
  {
    this.filter = filter;
  }

  /**
   * Parses the output of the command and returns a list of elements.
   * @return a list of Tag.
   * @see org.codehaus.mojo.scmchangelog.tags.Tag
   */
  public java.util.List analyse()
  {
    getLogger().debug( "Receiving this line " + getOutput() );

    try
    {
      ListsDocument doc = Factory.parse( new StringReader( getOutput() ) );

      for ( int i = 0; i < doc.getLists().getListArray().length; i++ )
      {
        List list = doc.getLists().getListArray()[i];

        for ( int j = 0; j < list.getEntryArray().length; j++ )
        {
          Entry entry = list.getEntryArray()[j];
          getLogger().debug( entry.getName() );
          if ( isTagAccepted( entry.getName() ) )
          {
            getLogger().debug( "Creating new Tag" );
            Tag tag = new Tag( entry.getName() );
            Commit commit = entry.getCommit();
            tag.setDate( commit.getDate().getTime() );
            tag.setEndRevision( commit.getRevision().toString() );
            tag.setAuthor( commit.getAuthor() );
            elements.add( tag );
          }
        }
      }
      Collections.sort( elements );
      Tag oldTag = new Tag( "" );
      oldTag.setEndRevision( "0" );
      Iterator iter = elements.iterator();
      while ( iter.hasNext() )
      {
        Tag tag = ( Tag ) iter.next();
        tag.setStartRevision( oldTag.getEndRevision() );
        oldTag = tag;
      }
      return this.elements;
    }
    catch ( XmlException ex )
    {
      getLogger().error( getOutput(), ex );
      throw new RuntimeException( ex );
    }
    catch ( IOException ioe )
    {
      getLogger().error( getOutput(), ioe );
      throw new RuntimeException( ioe );
    }
  }

  /**
   * Checks if the tag name matches the filter.
   * @param title the name of the tag to be checked.
   * @return true if the tag matches - false otherwise.
   */
  protected boolean isTagAccepted( String title )
  {
    if ( "tags".equalsIgnoreCase( title ) )
    {
      return false;
    }
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
