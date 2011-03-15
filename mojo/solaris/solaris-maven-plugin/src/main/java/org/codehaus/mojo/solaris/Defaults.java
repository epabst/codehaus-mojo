package org.codehaus.mojo.solaris;

import org.codehaus.plexus.util.DirectoryScanner;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Defaults
{
    private static final String DEFAULT_DIRECTORY_CLASS = "none";

    private static final String DEFAULT_DIRECTORY_MODE = "0755";

    private static final String DEFAULT_DIRECTORY_USER = "root";

    private static final String DEFAULT_DIRECTORY_GROUP = "sys";

    private static final Boolean DEFAULT_DIRECTORY_RELATIVE = Boolean.FALSE;

    private static final String[] DEFAULT_DIRECTORY_INCLUDES = new String[]{"**"};

    private static final String[] DEFAULT_DIRECTORY_EXCLUDES;

    private static final String DEFAULT_FILE_CLASS = "none";

    private static final String DEFAULT_FILE_MODE = "0644";

    private static final String DEFAULT_FILE_USER = "root";

    private static final String DEFAULT_FILE_GROUP = "sys";

    private static final Boolean DEFAULT_FILE_RELATIVE = Boolean.FALSE;

    private static final String[] DEFAULT_FILE_INCLUDES = new String[]{"**"};

    private static final String[] DEFAULT_FILE_EXCLUDES;

    private String pkgClass;

    private String mode;

    private String user;

    private String group;

    private String[] includes;

    private String[] excludes;

    private Boolean relative;


    static
    {
        String[] excludes = new String[ DirectoryScanner.DEFAULTEXCLUDES.length + 2 ];
        excludes[0] = "*prototype*";
        excludes[1] = "*pkginfo*";
        System.arraycopy( DirectoryScanner.DEFAULTEXCLUDES, 0, excludes, 2, DirectoryScanner.DEFAULTEXCLUDES.length );

        DEFAULT_DIRECTORY_EXCLUDES = excludes;
        DEFAULT_FILE_EXCLUDES = excludes;
    }

    public static Defaults directoryDefaults()
    {
        return new Defaults(
            DEFAULT_DIRECTORY_CLASS,
            DEFAULT_DIRECTORY_MODE,
            DEFAULT_DIRECTORY_USER,
            DEFAULT_DIRECTORY_GROUP,
            DEFAULT_DIRECTORY_RELATIVE,
            DEFAULT_DIRECTORY_INCLUDES,
            DEFAULT_DIRECTORY_EXCLUDES );
    }

    public static Defaults fileDefaults()
    {
        return new Defaults(
            DEFAULT_FILE_CLASS,
            DEFAULT_FILE_MODE,
            DEFAULT_FILE_USER,
            DEFAULT_FILE_GROUP,
            DEFAULT_FILE_RELATIVE,
            DEFAULT_FILE_INCLUDES,
            DEFAULT_FILE_EXCLUDES );
    }

    public Defaults()
    {
    }

    public static Defaults merge( Defaults superior, Defaults inferior )
    {
        if ( inferior == null )
        {
            inferior = new Defaults();
        }

        if ( superior == null )
        {
            return inferior;
        }

        return new Defaults(
            superior.pkgClass != null ? superior.pkgClass : inferior.pkgClass,
            superior.mode != null ? superior.mode : inferior.mode,
            superior.user != null ? superior.user : inferior.user,
            superior.group != null ? superior.group : inferior.group,
            superior.relative != null ? superior.relative : inferior.relative,
            superior.includes != null ? superior.includes : inferior.includes,
            superior.excludes != null ? superior.excludes : inferior.excludes );
    }

    public Defaults( String pkgClass, String mode, String user, String group, Boolean relative,
                     String[] includes, String[] excludes)
    {
        this.pkgClass = pkgClass;
        this.mode = mode;
        this.user = user;
        this.group = group;
        this.relative = relative;
        this.includes = includes;
        this.excludes = excludes;
    }

    public String getPkgClass()
    {
        return pkgClass;
    }

    public void setPkgClass( String pkgClass )
    {
        this.pkgClass = pkgClass;
    }

    public void setClass( String pkgClass )
    {
        this.pkgClass = pkgClass;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser( String user )
    {
        this.user = user;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public Boolean isRelative()
    {
        return relative;
    }

    public void setRelative( Boolean relative )
    {
        this.relative = relative;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    public void setIncludes( String[] includes )
    {
        this.includes = includes;
    }

    public String[] getExcludes()
    {
        return excludes;
    }

    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public String toString()
    {
        return ToStringBuilder.reflectionToString( this, ToStringStyle.MULTI_LINE_STYLE );
    }
}
