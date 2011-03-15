/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest.components;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Collects all the webtest reports of single tests.
 */
public class ReportCollector extends DirectoryWalker
{
    /** the name of the files to be collected */
    private String name;

    /**
     * Constructor
     *
     * @param name the name of the files to be collected
     */
    public ReportCollector( String name )
    {
        super( HiddenFileFilter.VISIBLE, -1 );
        this.name = name;
    }

    /**
     * Start collecting the report files for the given directory.
     *
     * @param directory the directory
     * @return the list of matching files
     * @throws MojoExecutionException the execution failed
     */
    public File[] run( File directory )
        throws MojoExecutionException
    {
        try
        {
            ArrayList<File> fileList = new ArrayList<File>();
            this.walk( directory, fileList );
            return fileList.toArray( new File[fileList.size()] );
        }
        catch ( Exception e )
        {
            String msg = "Unable to search for '" + this.name + "' in the directory '" + directory + "'";
            throw new MojoExecutionException( msg, e );
        }
    }

    /**
     * @see org.apache.commons.io.DirectoryWalker#handleFile(java.io.File, int, java.util.Collection) 
     */
    protected void handleFile( File file, int depth, Collection results )
        throws IOException
    {
        if ( name.equals( file.getName() ) )
        {
            results.add( file );
        }
    }
}
