/*
 * Copyright (c) 2007, Ounce Labs, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY OUNCE LABS, INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OUNCE LABS, INC. BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.codehaus.mojo.ounce.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 */
public class Utils
{

    // static final String propertyFormat = "${**}";
    static final String propertyFormat = "%**%";

    /**
     * Removes the pathToRemove from the path and optionally replaces it with a key
     * 
     * @param path original path
     * @param pathToRemove string to replace
     * @param key string to replace with
     * @return normalized string beginning with the key
     */
    public static String convertToRelativePath( String path, String pathToRemove, String key )
    {
        path = Utils.convertToUnixStylePath( path );
        pathToRemove = Utils.convertToUnixStylePath( pathToRemove );

        if ( StringUtils.isNotEmpty( key ) )
        {
            String formattedKey = StringUtils.replace( propertyFormat, "**", key );
            path = StringUtils.replace( path, pathToRemove, formattedKey );
        }
        else
        {
            path = StringUtils.replace( path, pathToRemove + "/", "" );

            // if any exact matches remain, replace with .
            path = StringUtils.replace( path, pathToRemove, "." );
        }

        return path;
    }

    /**
     * @param path
     * @param pathVariableMap
     * @return
     */
    static public String convertToVariablePath( String path, Map pathProperties )
    {
        if ( path != null && pathProperties != null && pathProperties.size() > 0 )
        {
            for ( Iterator iter = pathProperties.entrySet().iterator(); iter.hasNext(); )
            {
                Entry entry = (Entry) iter.next();
                path = convertToRelativePath( path, (String) entry.getValue(), (String) entry.getKey() );
            }
        }
        else
        {
            path = convertToUnixStylePath( path );
        }
        return path;
    }

    /**
     * Performs the path/key substitution for all paths.
     * 
     * @param paths
     * @param pathVariableMap map of key/path pairs to replace
     * @return processed list
     */
    static public List convertToPropertyPaths( List paths, Map pathProperties )
    {
        List result = paths;
        if ( pathProperties != null && pathProperties.size() > 0 )
        {
            for ( Iterator iter = pathProperties.entrySet().iterator(); iter.hasNext(); )
            {
                Entry entry = (Entry) iter.next();
                result = convertToRelativePaths( result, (String) entry.getValue(), (String) entry.getKey() );
            }
        }
        return result;
    }

    /**
     * Removes the pathToRemove from the paths and optionally replaces it with a key
     * 
     * @param paths original paths in a list
     * @param pathToRemove string to replace
     * @param key string to replace with
     * @return normalized string beginning with the key
     */
    static public List convertToRelativePaths( List paths, String pathToRemove, String key )
    {
        // go through the list, replace remove the
        // strings
        List newPaths = new ArrayList( paths.size() );
        Iterator iter = paths.iterator();

        while ( iter.hasNext() )
        {
            newPaths.add( convertToRelativePath( (String) iter.next(), pathToRemove, key ) );
        }
        return newPaths;
    }

    /**
     * Converts the path to the correct format for ounce.
     * 
     * @param pName
     * @return
     */
    static public String convertToUnixStylePath( String pName )
    {
        if ( pName != null )
        {
            return pName.replace( '\\', '/' );
        }
        else
        {
            return null;
        }
    }

    /**
     * Use reflection to generate a toString with all parameters.
     * 
     * @param obj
     * @return
     */
    public static synchronized String getDynamicToString( Object obj )
    {
        StringBuffer buf = new StringBuffer();

        try
        {
            Class clazz = obj.getClass();
            buf.append( clazz.getName() + ": " );

            Field[] fields = clazz.getDeclaredFields();
            for ( int i = 0; i < fields.length; i++ )
            {
                buf.append( " " );
                buf.append( fields[i].getName() );
                buf.append( "= " );
                try
                {
                    fields[i].setAccessible( true );

                    buf.append( fields[i].get( obj ) );
                    buf.append( " " );
                }
                catch ( Exception e )
                {
                    buf.append( "Error Retrieving Value " );
                }
            }
        }
        catch ( Exception e )
        {
            buf.append( "Exception: " + e.getMessage() );
        }
        return buf.toString();
    }
}
