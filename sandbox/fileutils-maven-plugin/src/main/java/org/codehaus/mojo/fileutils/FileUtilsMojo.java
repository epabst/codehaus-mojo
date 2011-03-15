package org.codehaus.mojo.fileutils;

/*
 * Copyright 2006 The Codehaus.
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
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;


/**
 * Executes the list of shellscript commands specified by the scripts parameter.
 *
 * @author John Tolentino<john.tolentino@gmail.com>
 * @goal fileutils
 */
public class FileUtilsMojo
    extends AbstractMojo
{
    private final static String COPY = "cp";

    private final static String DELETE = "rm";

    private final static String MKDIR = "mkdir";

    private final static String COMMANDLIST[] = {COPY, DELETE, MKDIR};

    /**
     * Put your file operation scripts here. Current valid commands are: cp, rm, mkdir. Delimeter for each command line is a
     * semicolon. Examples of usage: cp &lt;source-file&gt; &lt;target-file&gt;; cp &lt;source-dir&gt; &lt;target-dir&gt;;
     * rm &lt;target-file&gt;; rm &lt;target-dir&gt; mkdir &lt;new-dir&gt;
     *
     * @parameter expression="${scripts}"
     * @required
     */
    private String scripts;

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            for ( Iterator itr = stringToList( scripts ); itr.hasNext(); )
            {
                StringTokenizer currentScript = (StringTokenizer) itr.next();
                String command = currentScript.nextToken().toLowerCase();

                if ( COPY.equals( command ) )
                {
                    String parameter1 = currentScript.nextToken();
                    String parameter2 = currentScript.nextToken();
                    File source = new File( parameter1 );
                    File target = new File( parameter2 );

                    System.out.println( "copying " + source + " to " + target );
                    if ( source.isDirectory() )
                    {
                        System.out.println( "copyDirectory: " + source + " to " + target );
                        FileUtils.copyDirectory( source, target );
                    }
                    else
                    {
                        System.out.println( "copyFile: " + source + " to " + target );
                        FileUtils.copyFile( source, target );
                    }
                }

                if ( DELETE.equals( command ) )
                {
                    String parameter = currentScript.nextToken();
                    System.out.println( "deleting " + parameter );

                    File target = new File( parameter );
                    if ( target.isDirectory() )
                    {
                        FileUtils.deleteDirectory( target );
                        System.out.println( "deleted directory" );
                    }
                    else
                    {
                        target.delete();
                        System.out.println( "deleted file" );
                    }
                }

                if ( MKDIR.equals( command ) )
                {
                    String parameter = currentScript.nextToken();
                    System.out.println( "creating directory " + parameter );

                    File target = new File( parameter );
                    target.mkdirs();
                }
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private Iterator stringToList( String scripts )
    {
        List scriptList = new ArrayList();
        StringTokenizer st = new StringTokenizer( scripts, ";" );
        while ( st.hasMoreTokens() )
        {
            scriptList.add( new StringTokenizer( st.nextToken() ) );
        }
        return scriptList.iterator();
    }
}