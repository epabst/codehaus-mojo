package org.codehaus.mojo.pomtools.helpers;

import org.codehaus.plexus.util.StringUtils;

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


/** Simple utility functions for {@link java.lang.String}
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class LocalStringUtils
{
    private LocalStringUtils()
    {
        super();
    }

    /** Appends a string (suffix) to another string if the string does
     * not already end with the suffix.
     * <p>
     * If s is null, that value will be returned without the suffix. 
     * 
     * @param s the string to be appended
     * @param strToAppend  the string to append if s doesn't aleady end with suffix.
     * @return null if s is null, else suffix appended to s
     */
    public static String makeEndWith( String s, String strToAppend )
    {
        if ( s == null )
        {
            return null;
        }

        if ( s.endsWith( strToAppend ) )
        {
            return s;
        }
        else
        {
            return s + strToAppend;
        }
    }
    
    /** Takes a camelCased string and splits it at each capital letter
     * by adding a space. It also capializes the first letter of each word
     * following whitespace so that the first word capitalization matches the others
     * <p>
     * For example:<br>
     * "fooBarFoo" => "Foo Bar Foo" 
     * "fooBar foo" => "Foo Bar Foo" 
     * null => null
     * "" => ""
     * " " => " "
     * 
     * @param s
     * @return
     */
    public static String splitCamelCase( String s )
    {
        if ( StringUtils.isEmpty( s ) )
        {
            return s;
        }
        
        StringBuffer result = new StringBuffer( s.length() );
        
        char[] chars = s.toCharArray();
        
        char prevCh = (char) -1;
        for ( int i = 0; i < chars.length; i++ )
        {
            char ch = chars[i];
            
            if ( i == 0 || Character.isWhitespace( prevCh ) )
            {
                result.append( Character.toUpperCase( ch ) );
            }
            else if ( ch >= 'A' && ch <= 'Z' )
            {
                if ( prevCh != ' ' )
                {
                    result.append( ' ' );
                }
                
                result.append( ch );
            }
            else
            {
                result.append( ch );
            }
            
            prevCh = ch;
        }
        
        return result.toString();
    }

    /** Wrapper function for 
     * {@link org.apache.commons.lang.StringUtils#splitPreserveAllTokens(java.lang.String, java.lang.String)}
     * so that this plugin doesn't depend on commons-lang directly.
     */
    public static String[] splitPreserveAllTokens( String str, String separatorChars )
    {
        return org.apache.commons.lang.StringUtils.splitPreserveAllTokens( str, separatorChars );
    }

    /** If the condition is true, the trueString is returned, otherwise the falseString is returned.
     * 
     * @param condition     boolean condition to evaluate
     * @param trueString    String to return if the condition is true
     * @param falseString   String to return if the condition is false
     */
    public static String ifTrue( boolean condition, String trueString, String falseString )
    {
        return ( condition ) ? trueString : falseString;
    }
}
