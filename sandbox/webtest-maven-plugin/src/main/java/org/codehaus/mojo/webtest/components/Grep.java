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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;

/**
 * Greps through a list of files line by line and searches for matching
 * strings. 
 */
public class Grep
{
    /**
     * the files to grep
     */
    private File[] files;

    /**
     * the argument to search for in the files
     */
    private String argument;

    /**
     * Constructor
     *
     * @param files the list of files to grep
     * @param argument the argument to seach for
     */
    public Grep( File[] files, String argument )
    {
        this.files = files;
        this.argument = argument;
    }

    /**
     * Constructor
     *
     * @param baseDir the base directory of the relative file names
     * @param filenames the list of relative filenames to grep
     * @param argument the argument to seach for
     */
    public Grep( File baseDir, String[] filenames, String argument )
    {
        this.files = new File[filenames.length];
        for ( int i = 0; i < filenames.length; i++ )
        {
            this.files[i] = new File( baseDir, filenames[i] );
        }
        this.argument = argument;
    }

    /**
     * Greps through the files.
     *
     * @return list of files matching the expression
     * @throws IOException accesing the files failed
     */
    public File[] match() throws IOException
    {
        HashSet<File> fileHits = new HashSet<File>();

        if ( this.files == null || this.files.length == 0 )
        {
            return new File[0];
        }

        Pattern pattern = Pattern.compile( this.argument );
        Matcher matcher = pattern.matcher( "" );

        for (File file : this.files)
        {
            BufferedReader br = null;
            String line;

            try
            {
                br = new BufferedReader(new FileReader(file));

                while ((line = br.readLine()) != null)
                {
                    matcher.reset(line);

                    if (matcher.find())
                    {
                        fileHits.add(file);
                    }
                }
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (Exception e)
                    {
                        // just ignore    
                    }
                }
            }
        }

        return fileHits.toArray( new File[fileHits.size()] );
    }
}
