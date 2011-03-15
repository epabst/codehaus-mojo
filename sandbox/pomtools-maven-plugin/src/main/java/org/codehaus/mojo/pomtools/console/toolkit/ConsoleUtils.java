package org.codehaus.mojo.pomtools.console.toolkit;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * wordWrap routine copied from 
 * http://svn.apache.org/repos/asf/jakarta/taglibs/proper/string/trunk/src/org/apache/taglibs/string/util/StringW.java
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class ConsoleUtils
{
    public static final int DEFAULT_WRAP_WIDTH = 80;
    
    private ConsoleUtils()
    {
    }

    /**
     * Create a word-wrapped version of a String. Wrap at 80 characters and 
     * use newlines as the delimiter. If a word is over 80 characters long 
     * use a - sign to split it.
     */
    public static String wordWrap( String str )
    {
        return wordWrap( str, DEFAULT_WRAP_WIDTH, "\n", "-", true );
    }

    /**
     * Create a word-wrapped version of a String. Wrap at a specified width and 
     * use newlines as the delimiter. If a word is over the width in lenght 
     * use a - sign to split it.
     */
    public static String wordWrap( String str, int width )
    {
        return wordWrap( str, width, "\n", "-", true );
    }

    /**
     * Word-wrap a string.
     *
     * @param str   String to word-wrap
     * @param width int to wrap at
     * @param delim String to use to separate lines
     * @param split String to use to split a word greater than width long
     *
     * @return String that has been word wrapped (with the delim inside width boundaries)
     */
    public static String wordWrap( String str, int width, String delim, String split )
    {
        return wordWrap( str, width, delim, split, true );
    }

    /**
     * Word-wrap a string.
     *
     * @param str   String to word-wrap
     * @param width int to wrap at
     * @param delim String to use to separate lines
     * @param split String to use to split a word greater than width long
     * @param delimInside wheter or not delim should be included in chunk before length reaches width.
     *
     * @return String that has been word wrapped
     */
    public static String wordWrap( String str, int width, String delim, String split, boolean delimInside )
    {
        int sz = str.length();

        /// shift width up one. mainly as it makes the logic easier
        width++;

        // our best guess as to an initial size
        StringBuffer buffer = new StringBuffer( sz / width * delim.length() + sz );

        // every line might include a delim on the end
        if ( delimInside )
        {
            width = width - delim.length();
        }
        else
        {
            width--;
        }

        int idx = -1;
        String substr = null;

        // beware: i is rolled-back inside the loop
        for ( int i = 0; i < sz; i += width )
        {
            // on the last line
            if ( i > sz - width )
            {
                buffer.append( str.substring( i ) );
                break;
            }

            // the current line
            substr = str.substring( i, i + width );

            // is the delim already on the line
            idx = substr.indexOf( delim );
            if ( idx != -1 )
            {
                buffer.append( substr.substring( 0, idx ) );
                buffer.append( delim );
                i -= width - idx - delim.length();

                // Erase a space after a delim. Is this too obscure?
                if ( substr.length() > idx + 1 )
                {
                    if ( substr.charAt( idx + 1 ) != '\n' )
                    {
                        if ( Character.isWhitespace( substr.charAt( idx + 1 ) ) )
                        {
                            i++;
                        }
                    }
                }
                continue;
            }

            idx = -1;

            // figure out where the last space is
            char[] chrs = substr.toCharArray();
            for ( int j = width; j > 0; j-- )
            {
                if ( Character.isWhitespace( chrs[j - 1] ) )
                {
                    idx = j;
                    break;
                }
            }

            // idx is the last whitespace on the line.
            if ( idx == -1 )
            {
                buffer.append( substr );
                buffer.append( delim );
            }
            else
            {
                // insert spaces
                buffer.append( substr.substring( 0, idx ) );
                buffer.append( StringUtils.repeat( " ", width - idx ) );
                buffer.append( delim );

                i -= width - idx;
                //                }
            }
        }
        return buffer.toString();
    }
    
}
