package org.codehaus.mojo.javancss;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javancss.Javancss;

import org.apache.maven.reporting.MavenReportException;

/**
 * The NcssExecuter is able to call JavaNCSS to produce a code analysis.<br>
 * The results are produced into a raw xml file.
 *
 * @author <a href="jeanlaurent@gmail.com">Jean-Laurent de Morlhon</a>
 * @version $Id$
 */
public class NcssExecuter
{
    private static final String DEFAULT_ERROR_MESSAGE = "Error while JavaNCSS was executing";

    private static final int ARG_SIZE = 8;

    // the full path to the directory holding the sources to point JavaNCSS to.
    // Or the location of a file holding the path towards all files. (javancss style *sigh* :)
    private File sourceLocation;

    // JavaNCSS will write an xml output into this file.
    private String outputFilename;

    private String[] fileList;

    private String encoding = null;

    /**
     * Construct a NcssExecuter with no arguments.<br>
     * Used for testing.
     */
    /* package */NcssExecuter()
    {
        this.sourceLocation = null;
        this.outputFilename = null;
        this.fileList = null;
    }

    /**
     * Construct a NcssExecuter.
     *
     * @param sourceDirectory the directory where the source to analyze are.
     * @param outputFilename the output file where the result will be written.
     */
    public NcssExecuter( File sourceDirectory, String outputFilename )
    {
        this.sourceLocation = sourceDirectory;
        this.outputFilename = outputFilename;
        this.fileList = null;
    }

    public NcssExecuter( String[] fileList, String outputFilename )
    {
        this.sourceLocation = null;
        this.fileList = fileList;
        this.outputFilename = outputFilename;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Call the JavaNCSS code analysis tool to produce the result to a temporary file.
     *
     * @throws MavenReportException if something goes bad during the execution
     */
    public void execute()
        throws MavenReportException
    {
        try
        {
            Javancss javancss = new Javancss( getCommandLineArgument() );
            Throwable ncssException = javancss.getLastError();
            if ( ncssException != null )
            {
                if ( ncssException instanceof Exception )
                {
                    throw new MavenReportException( DEFAULT_ERROR_MESSAGE, (Exception) ncssException );
                }
                else
                {
                    throw new MavenReportException( DEFAULT_ERROR_MESSAGE, new Exception( ncssException ) );
                }
            }
        }
        catch ( IOException ioe )
        {
            throw new MavenReportException( "IO Error while running JavaNCSS", ioe );
        }
    }

    private String[] getCommandLineArgument()
    {
        List argumentList = new ArrayList( ARG_SIZE );
        argumentList.add( "-package" );
        argumentList.add( "-object" );
        argumentList.add( "-function" );
        argumentList.add( "-xml" );
        argumentList.add( "-recursive" );
        argumentList.add( "-out" );
        argumentList.add( outputFilename );

        if ( encoding != null )
        {
            argumentList.add( "-encoding" );
            argumentList.add( encoding );
        }

        // If the source location is a directory, it means we can pass it straight to
        // javancss. If it's a file, we assume it's containing the file list to parse
        // so we pass it to javancss the way it expects it.
        // (check javancss cmd line doc for more information)
        if ( ( sourceLocation != null ) && ( sourceLocation.isDirectory() ) )
        {
            argumentList.add( sourceLocation.getAbsolutePath() );
        }
        else
        {
            for ( int i = 0; i < fileList.length; i++ )
            {
                argumentList.add( fileList[i] );
            }
        }
        return (String[]) argumentList.toArray( new String[argumentList.size()] );
    }

}
