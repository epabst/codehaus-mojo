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
import org.codehaus.mojo.scmchangelog.changelog.log.ScmGrammar;

/**
 * Abstract grammar to be used as an helper class.
 * @author ehsavoie
 * @version $Id$
 */
public abstract class AbstractScmGrammar
    implements ScmGrammar
{
   /**
    * The Regexp Pattern for finding comments.
    */
   private static final Pattern COMMENT_PATTERN = Pattern.compile( "/\\*([^*]|(\\*+([^*/])))*\\*+/",
           Pattern.CASE_INSENSITIVE );

   /**
    * Removes the comments from the content.
    * @param content which comments will be removed.
    * @return the content without the comments.
    */
   public String removeComments( String content )
   {
       Matcher matcher = COMMENT_PATTERN.matcher( content );
       String tempExpression = content;
       boolean hasMore = matcher.find();
       while ( hasMore )
       {
          tempExpression = tempExpression.substring( 0, matcher.start() )
              + tempExpression.substring( matcher.end() , tempExpression.length() );
          matcher = COMMENT_PATTERN.matcher( tempExpression );
          hasMore = matcher.find();
       }
       return tempExpression;
   }  
}