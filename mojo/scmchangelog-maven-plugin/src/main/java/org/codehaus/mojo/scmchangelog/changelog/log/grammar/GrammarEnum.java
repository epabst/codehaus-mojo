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
package org.codehaus.mojo.scmchangelog.changelog.log.grammar;

import org.codehaus.mojo.scmchangelog.changelog.log.Message;
import org.codehaus.mojo.scmchangelog.changelog.log.ScmGrammar;


/**
 * The currently supported grammars.
 * @author ehsavoie
 * @version $Id$
 */
public class GrammarEnum
{
  /**
   * The MANU grammar @operatio:issue#;comment
   */
  public static final GrammarEnum MANU = new GrammarEnum( new ManuScmGrammar(),
      "MANU" );
  /**
   * The REMY grammar.
   */
  public static final GrammarEnum REMY = new GrammarEnum( new RemyScmGrammar(),
      "REMY" );
  /**
   * The BUGZILLA grammar.
   */
  public static final GrammarEnum BUGZILLA = new GrammarEnum(
          new BugzillaScmGrammar(), "BUGZILLA" );
  /**
   * The ALL grammar.
   */
  public static final GrammarEnum ALL = new GrammarEnum(
          new AcceptAllScmGrammar(), "ALL" );

  /**
   * The grammar of the enum element.
   */
  private ScmGrammar grammar;
  /**
   * The name of the enum element.
   */
  private String name;

  /**
   * Instatiate a new enum element.
   * @param grammar the grammar of the element.
   * @param name the name of the grammar element.
   */
  private GrammarEnum( ScmGrammar grammar, String name )
  {
    this.grammar = grammar;
    this.name = name;
  }

  /**
   * Extract a Message from the specified String content.
   * @param content the String to be parsed.
   * @return the corresponding Message.
   * @see org.codehaus.mojo.scmchangelog.changelog.log.Message
   */
  public Message extractMessage( final String content )
  {
    String realContent = this.grammar.removeComments( content );
    return this.grammar.extractMessage( realContent );
  }

  /**
   * Indicates if the content String matches the grammar.
   * @param content the String to be tested against the grammar.
   * @return true if the content matches the grammar - false otherwise.
   */
  public boolean hasMessage( final String content )
  {
    return this.grammar.hasMessage( content );
  }

  /**
   * Returns the String to be inserted between each issue comment. It may be replaced
   * when generating the report.
   * @return the String to be inserted between each issue comment.
   */
  public String getIssueSeparator()
  {
    return this.grammar.getIssueSeparator();
  }

  /**
   * Return the enum element matching the specified name. MANU if no match is found.
   * @param name the name of the required enum element.
   * @return the enum element matching the specified name. MANU if no match is found.
   */
  public static GrammarEnum valueOf( String name )
  {
    if ( REMY.name.equalsIgnoreCase( name ) )
    {
      return REMY;
    }
    else if ( BUGZILLA.name.equalsIgnoreCase( name ) )
    {
      return BUGZILLA;
    }
    else if ( ALL.name.equalsIgnoreCase( name ) )
    {
      return ALL;
    }
    return MANU;
  }
}
