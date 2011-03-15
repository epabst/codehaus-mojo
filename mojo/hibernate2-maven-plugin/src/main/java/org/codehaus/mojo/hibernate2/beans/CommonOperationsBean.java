package org.codehaus.mojo.hibernate2.beans;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="as851@columbia.edu">Alex Shneyderman</a>
 * @author <a href="cameron@braid.com.au">Cameron Braid</a>
 * @version $Id$
 */
public abstract class CommonOperationsBean
    extends AbstractMojo
{
    /**
     * file scanner pattern of files to include - relative to basedir
     *
     * @parameter
     */
    private String includes;

    /**
     * file scanner pattern of files to exclude - relative to basedir
     *
     * @parameter
     */
    private String excludes;

    /**
     * the base dir for the file scanner when locating resources by include and exclude patterns
     *
     * @parameter
     */
    private String basedir;

    /**
     * Controls verbosity of the plugin. Default is set to yes
     *
     * @parameter default-value="true"
     */
    private boolean quiet;

    private String[] classpath;

    public String getBasedir()
    {
        return basedir;
    }

    public void setBasedir( String basedir )
    {
        this.basedir = basedir;
        print( "basedir [" + getBasedir() + "]" );
    }

    public String getExcludes()
    {
        return excludes;
    }

    public void setExcludes( String excludes )
    {
        this.excludes = excludes;
        print( "excludes [" + getExcludes() + "]" );
    }

    public String getIncludes()
    {
        return includes;
    }

    public void setIncludes( String includes )
    {
        this.includes = includes;
        print( "includes [" + getIncludes() + "]" );
    }

    public void setClasspath( String[] classpath )
    {
        this.classpath = classpath;
    }

    public String[] getClasspath()
    {
        return this.classpath;
    }

    protected String[] getBaseDirNames()
    {
        StringTokenizer st = new StringTokenizer( getBasedir(), "," );

        String tokens[] = new String[ st.countTokens() ];

        for ( int i = 0; st.hasMoreTokens(); i++ )
        {
            tokens[ i ] = st.nextToken();
        }

        return tokens;
    }

    protected File[] getBaseDirs()
    {
        String basedirNames[] = getBaseDirNames();

        List dirs = new ArrayList();

        for ( int i = 0; i < basedirNames.length; i++ )
        {
            File basedir = new File( basedirNames[ i ] );

            if ( basedir.isDirectory() )
            {
                dirs.add( basedir );
            }
        }

        return (File[]) dirs.toArray( new File[dirs.size()] );
    }

    protected File[] getIncludeFiles()
    {
        List files = new ArrayList();

        File dirs[] = getBaseDirs();

        for ( int i = 0; i < dirs.length; i++ )
        {
            try
            {
                print(
                    "Scanning from [" + dirs[ i ].getAbsolutePath() + "] includes=[" + includes + "], excludes=[" + excludes + "]" );

                files.addAll( FileUtils.getFiles( dirs[ i ], includes, excludes ) );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( "scanning for files failed", e );
            }
        }
        return (File[]) files.toArray( new File[ files.size() ] );
    }

    protected void print( String message )
    {
        if ( getQuiet() )
        {
            return;
        }

        //getLog().info( message );

        System.out.println( message );
    }

    public boolean getQuiet()
    {
        return quiet;
    }

    public void setQuiet( boolean quiet )
    {
        this.quiet = quiet;
    }
}
