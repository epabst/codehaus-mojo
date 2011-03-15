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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;


import org.codehaus.mojo.scmchangelog.changelog.log.Issue;
import org.codehaus.mojo.scmchangelog.changelog.log.Message;
import org.codehaus.mojo.scmchangelog.changelog.log.OperationTypeEnum;

/**
 * Abstract grammar to be used as an helper class.
 * @author ehsavoie
 * @version $Id: AbstractScmGrammar.java 9280 2009-03-26 19:21:50Z ehsavoie $
 */
public abstract class AbstractRegexpScmGrammar extends AbstractScmGrammar
{
    /**
   * Extracts the message (list of issues)from the comment.
   * @param content the comment of the log entry.
   * @return the message
   * @see  org.codehaus.mojo.scmchangelog.changelog.log.Message
   */
  public Message extractMessage( String content )
  {
    String expression = content;
    List issues = new ArrayList();
    expression = computeIssues( OperationTypeEnum.FIX, expression, issues );
    expression = computeIssues( OperationTypeEnum.ADD, expression, issues );
    expression = computeIssues( OperationTypeEnum.UPDATE, expression, issues );
    expression = computeIssues( OperationTypeEnum.REMOVE, expression, issues );

    if ( expression.startsWith( getIssueSeparator() ) )
    {
      expression = expression.substring( 2 );
    }

    return new Message( expression.trim(), issues );
  }

  /**
   * Indicates if the content respects the grammar used to extract issues.
   * @param content the content to be checked
   * @return true if the content uses the grammar - false otherwise.
   */
  public boolean hasMessage( String content )
  {
    return ( getAddCleaner( content ).find()
        || getFixCleaner( content ).find()
        || getRemoveCleaner( content ).find()
        || getUpdateCleaner( content ).find() );
  }

  /**
   * Extract all issues of same type from the SVN comment.
   * @param type : type of issue to be extracted.
   * @param expression : svn comment.
   * @param issues : the list of issues already found.
   * @return the comment after extraction.
   */
  protected String computeIssues( OperationTypeEnum type, String expression,
      List issues )
  {
    String tempExpression = expression;
    Matcher matcher = getFinder( type, expression );
    boolean hasMore = matcher.find();

    while ( hasMore )
    {
      String element = matcher.group();
      Matcher cleaner = getCleaner( type, element );
      cleaner.find();

      Issue issue = new Issue( element.substring( cleaner.end(),
          element.length() - 1 ), type );
      issues.add( issue );
      tempExpression = tempExpression.substring( 0, matcher.start() )
          + getIssueSeparator()
          + tempExpression.substring( matcher.end(), tempExpression.length() );
      matcher = getFinder( type, tempExpression );
      hasMore = matcher.find();
    }
    return tempExpression;
  }

  /**
   * Return the regexp used for removing grammar elements from the comments.
   * @param type : type of issue to be extracted.
   * @param expression : svn comment.
   * @return the regexp matcher for 'cleaning' the comments.
   */
  protected Matcher getCleaner( OperationTypeEnum type, String expression )
  {
    if ( OperationTypeEnum.ADD.equals( type ) )
    {
      return getAddCleaner( expression );
    }
    else if ( OperationTypeEnum.FIX.equals( type ) )
    {
      return getFixCleaner( expression );
    }
    else if ( OperationTypeEnum.UPDATE.equals( type ) )
    {
      return getUpdateCleaner( expression );
    }
    else if ( OperationTypeEnum.REMOVE.equals( type ) )
    {
      return getRemoveCleaner( expression );
    }
    return getRemoveCleaner( expression );
  }

  /**
   * Return the regexp used for finding grammar elements from the comments.
   * @param type : type of issue to be extracted.
   * @param expression : svn comment.
   * @return the regexp matcher for finding the comments.
   */
  protected Matcher getFinder( OperationTypeEnum type, String expression )
  {
    if ( OperationTypeEnum.ADD.equals( type ) )
    {
      return getAddMatcher( expression );
    }
    else if ( OperationTypeEnum.FIX.equals( type ) )
    {
      return getFixMatcher( expression );
    }
    else if ( OperationTypeEnum.UPDATE.equals( type ) )
    {
      return getUpdateMatcher( expression );
    }
    else if ( OperationTypeEnum.REMOVE.equals( type ) )
    {
      return getRemoveMatcher( expression );
    }
    return getRemoveMatcher( expression );
  }

  /**
   * Returns the String to be inserted between each issue comment. It may be replaced
   * when generating the report.
   * @return the String to be inserted between each issue comment.
   */
  public String getIssueSeparator()
  {
    return NEW_LINE;
  }

  /**
   * Getter for the regexp matcher to clean the comments of the FIX issues.
   * @param expression the regexp for cleaning the comments of the FIX issues.
   * @return a Matcher to clean the comments of FIX issues.
   */
  public abstract Matcher getFixCleaner( String expression );

  /**
   * Getter for the regexp matcher to clean the comments of the UPDATE issues.
   * @param expression the regexp for cleaning the comments of the UPDATE issues.
   * @return a regexp matcher to clean the comments of UPDATE issues.
   */
  public abstract Matcher getUpdateCleaner( String expression );

  /**
   * Getter for the regexp matcher to clean the comments of the ADD issues.
   * @param expression the regexp for cleaning the comments of the ADD issues.
   * @return a regexp matcher to clean the comments of ADD issues.
   */
  public abstract Matcher getAddCleaner( String expression );

  /**
   * Getter for the regexp matcher to clean the comments of the REMOVE issues.
   * @param expression the regexp for cleaning the comments of the REMOVE issues.
   * @return a regexp matcher to clean the comments of REMOVE issues.
   */
  public abstract Matcher getRemoveCleaner( String expression );

  /**
   * Getter for the regexp matcher to find the comments of the FIX issues.
   * @param expression the regexp for finding the comments of the FIX issues.
   * @return a regexp matcher to find the comments of FIX issues.
   */
  public abstract Matcher getFixMatcher( String expression );

  /**
   * Getter for the regexp matcher to find the comments of the ADD issues.
   * @param expression the regexp for finding the comments of the ADD issues.
   * @return a regexp matcher to find the comments of ADD issues.
   */
  public abstract Matcher getAddMatcher( String expression );

  /**
   * Getter for the regexp matcher to find the comments of the REMOVE issues.
   * @param expression the regexp for finding the comments of the REMOVE issues.
   * @return a regexp matcher to find the comments of REMOVE issues.
   */
  public abstract Matcher getRemoveMatcher( String expression );

  /**
   * Getter for the regexp matcher to find the comments of the UPDATE issues.
   * @param expression the regexp for finding the comments of the UPDATE issues.
   * @return a regexp matcher to find the comments of UPDATE issues.
   */
  public abstract Matcher getUpdateMatcher( String expression );
}
