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
package org.codehaus.mojo.webtest;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.mojo.webtest.validation.FileContentValidationSet;
import org.codehaus.mojo.webtest.components.Grep;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;

/**
 * Allows grepping through a set of files to find particular text strings and
 * fail the build if one or matches are found. This is useful for checking HTML
 * files ("an application error occured") or logfiles to decide if any exception
 * are silently ignored by the web application.
 *
 * @goal verify-filecontent
 */
public class WebtestVerifyFileContentMojo
    extends AbstractMojo
{
    /**
     * Specifies a set of files, which are being validated.
     *
     * @parameter
     */
    private FileContentValidationSet[] fileContentValidationSets;

    /**
     * @return the list of file validations to perform
     */
    public FileContentValidationSet[] getFileContentValidationSets()
    {
        return fileContentValidationSets;
    }

    /**
     * Parses the given files and throws an exception if one or more
     * matches are found.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException the execution failed
     */
    public void execute() throws MojoExecutionException
    {
        try
        {
            if ( this.fileContentValidationSets == null )
            {
                return;
            }

            for ( int i = 0; i < this.getFileContentValidationSets().length; i++ )
            {
                FileContentValidationSet fileContentValidationSet = this.getFileContentValidationSets()[i];
                String regexp = fileContentValidationSet.getRegexp();
                this.getLog().info( "Searching for '" + regexp + "' ..." );
                File baseDir = fileContentValidationSet.getDir();
                DirectoryScanner ds = this.createDirectoryScanner( fileContentValidationSet );
                ds.scan();
                String[] includedFileNames = ds.getIncludedFiles();
                Grep grepper = new Grep( baseDir, includedFileNames, regexp );
                File[] fileList = grepper.match();
                if ( fileList.length > 0 )
                {
                    StringBuffer grepResult = new StringBuffer();
                    grepResult.append( "The regexp '" );
                    grepResult.append( regexp );
                    grepResult.append( "' was found in : " );
                    for ( int j = 0; j < fileList.length; j++ )
                    {
                        grepResult.append( fileList[j] );
                        if ( j < fileList.length - 1 )
                        {
                            grepResult.append( ";" );
                        }
                    }

                    this.getLog().error( grepResult.toString() );
                    throw new MojoExecutionException( fileContentValidationSet.getMsg() );
                }
            }
        }
        catch ( MojoExecutionException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Failed to grep the files", e );
        }
    }

    /**
     * Create a file directory scanner based on the files to be validated.
     *
     * @param fileContentValidationSet the files to be validated
     * @return the directory scanner
     */
    private DirectoryScanner createDirectoryScanner( FileContentValidationSet fileContentValidationSet )
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes( fileContentValidationSet.getIncludes() );
        ds.setExcludes( fileContentValidationSet.getExcludes() );
        ds.setBasedir( fileContentValidationSet.getDir() );
        ds.setCaseSensitive( false );
        return ds;
    }
}
