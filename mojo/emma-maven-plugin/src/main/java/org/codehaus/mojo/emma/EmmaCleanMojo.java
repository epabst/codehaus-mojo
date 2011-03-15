package org.codehaus.mojo.emma;

/*
 * The MIT License
 *
 * Copyright (c) 2007-8, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Clean EMMA generated resources.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 * @goal clean
 * @phase clean
 */
public class EmmaCleanMojo
    extends AbstractEmmaMojo
{
    /**
     * Location to store class coverage metadata.
     * 
     * @parameter expression="${emma.metadataFile}" default-value="${project.build.directory}/coverage.em"
     */
    protected File metadataFile;

    /**
     * Class coverage data files.
     * 
     * @parameter
     */
    protected File[] dataFiles;

    /**
     * Checks the parameters before doing the work.
     * 
     * @throws MojoExecutionException if things go wrong.
     * @throws MojoFailureException if things go wrong.
     */
    protected void checkParameters()
        throws MojoExecutionException, MojoFailureException
    {
        super.checkParameters();

        if ( dataFiles == null )
        {
            dataFiles = new File[] { new File( project.getBasedir(), "coverage.ec" ) };
        }
    }

    /**
     * Does the work.
     * 
     * @throws MojoExecutionException if things go wrong.
     * @throws MojoFailureException if things go wrong.
     */
    protected void doExecute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( metadataFile.exists() )
        {
            getLog().info( "Deleting file " + metadataFile.getAbsolutePath() );
            metadataFile.delete();
        }

        for ( int i = 0; i < dataFiles.length; ++i )
        {
            final File dataFile = dataFiles[i];
            if ( dataFile.exists() )
            {
                getLog().info( "Deleting file " + dataFile.getAbsolutePath() );
                dataFile.delete();
            }
        }

        if ( outputDirectory.exists() )
        {
            getLog().info( "Deleting directory " + outputDirectory.getAbsolutePath() );
            try
            {
                FileUtils.deleteDirectory( outputDirectory );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Failed to delete EMMA generated resources", e );
            }
        }
    }
}
