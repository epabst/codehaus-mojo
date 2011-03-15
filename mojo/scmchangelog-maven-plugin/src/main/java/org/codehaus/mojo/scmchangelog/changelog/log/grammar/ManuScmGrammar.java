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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple grammar which uses a <code>@type:id;comment</code> structure.
 * @author ehsavoie
 * @version $Id$
 */
public class ManuScmGrammar
    extends AbstractRegexpScmGrammar
{

  protected static final Pattern FIX_PATTERN = Pattern.compile( "@[fF][iI][xX][\\s]*:[^;]*[;]" );
  protected static final Pattern REMOVE_PATTERN = Pattern.compile( "@[rR][eE][mM][oO][vV][eE][\\s]*:[^;]*[;]" );
  protected static final Pattern ADD_PATTERN = Pattern.compile( "@[aA][dD][dD][\\s]*:[^;]*[;]" );
  protected static final Pattern UPDATE_PATTERN = Pattern.compile( "@[uU][pP][dD][aA][tT][eE][\\s]*:[^;]*[;]" );
  protected static final Pattern FIX_CLEANER_PATTERN = Pattern.compile( "@[fF][iI][xX][\\s]*:" );
  protected static final Pattern REMOVE_CLEANER_PATTERN = Pattern.compile( "@[rR][eE][mM][oO][vV][eE][\\s]*:" );
  protected static final Pattern ADD_CLEANER_PATTERN = Pattern.compile( "@[aA][dD][dD][\\s]*:" );
  protected static final Pattern UPDATE_CLEANER_PATTERN = Pattern.compile( "@[uU][pP][dD][aA][tT][eE][\\s]*:" );

  public Matcher getFixCleaner( String expression )
  {
    return FIX_CLEANER_PATTERN.matcher( expression );
  }

  public Matcher getUpdateCleaner( String expression )
  {
    return UPDATE_CLEANER_PATTERN.matcher( expression );
  }

  public Matcher getAddCleaner( String expression )
  {
    return ADD_CLEANER_PATTERN.matcher( expression );
  }

  public Matcher getRemoveCleaner( String expression )
  {
    return REMOVE_CLEANER_PATTERN.matcher( expression );
  }

  public Matcher getFixMatcher( String expression )
  {
    return FIX_PATTERN.matcher( expression );
  }

  public Matcher getAddMatcher( String expression )
  {
    return ADD_PATTERN.matcher( expression );
  }

  public Matcher getRemoveMatcher( String expression )
  {
    return REMOVE_PATTERN.matcher( expression );
  }

  public Matcher getUpdateMatcher( String expression )
  {
    return UPDATE_PATTERN.matcher( expression );
  }

}
