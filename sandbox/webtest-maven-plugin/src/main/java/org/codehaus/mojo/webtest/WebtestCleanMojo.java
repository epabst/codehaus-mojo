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

import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;

/**
 * Remove temporary data from running the Canoo WebTests. The
 * following directories are deleted
 * <ul>
 * <li>resultpath</li>
 * <li>reportdirectory</li>
 * </ul>
 *
 * @goal clean
 * @phase clean
 */
public class WebtestCleanMojo
    extends AbstractWebtestMojo
{
    /**
     * Starts the Canoo WebTest.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException the execution failed
     */
    public void execute() throws MojoExecutionException
    {
        try
        {
            this.safeDeleteDirectory( this.getResultpath() );
            this.safeDeleteDirectory( this.getReportdirectory() );
        }
        catch ( Exception e )
        {
            String msg = "Removing the temporary files failed";
            throw new MojoExecutionException( msg, e );
        }
    }

    /**
     * Safely delete a dirctory - skip top-level directories and directories
     * with a very short name since this can be the effect of a broken
     * configuration.
     *
     * @param directory the directory to delete
     * @throws IOException deleting the directory failed
     */
    private void safeDeleteDirectory( File directory ) throws IOException
    {
        String directoryName = directory.getAbsolutePath();

        if ( ( directoryName.length() > 8 ) && ( directory.getParentFile() != null ) )
        {
            FileUtils.deleteDirectory( directory );
        }
        else
        {
            String msg = "Skipped deleting the following directory : " + directoryName;
            throw new IllegalArgumentException( msg );
        }
    }
}
