package org.codehaus.mojo.native2ascii;

/*
 * The MIT License
 * 
 * Copyright (c) 2007, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;

/**
 * @goal native2ascii
 * @phase generate-resources
 * @author dtran
 *
 */
public class Native2AsciiMojo
    extends AbstractMojo
{
    /**
     * The directory to find files in.
     *
     * @parameter default-value="${basedir}/src/main/native2ascii"
     * @since 1.0-alpha-1
     */
    protected File src;

    /**
     * The directory to output files to.
     *
     * @parameter default-value="${project.build.directory}/native2ascii"
     * @since 1.0-alpha-1
     */
    protected File dest;

    /**
     * File extension to use when renaming output files.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    protected String ext;

    /**
     * The native encoding the files are in.<br />
     * <strong>Default value is:</strong> the default encoding for the JVM.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    protected String encoding;

    /**
     * A comma- or space-separated list of patterns of files that must be included. All files are included when omitted.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    protected String includes;

    /**
     * A comma- or space-separated list of patterns of files that must be excluded. No files (except default excludes) are excluded when omitted.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    protected String excludes;

    /**
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * If false, log a warning message, but do not stop the build, when the files to convert do not exist or an error occurs while converting.
     *
     * @parameter default-value="true"
     * @since 1.0-alpha-2
     */
    protected boolean failonerror = true;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        executeAnt();

        Resource resource = new Resource();
        resource.setDirectory( dest.getPath() );
        this.project.addResource( resource );
    }

    protected void executeAnt()
        throws MojoFailureException
    {
        try
        {
            Project antProject = new Project();
            antProject.setName( "native2ascii" );

            Native2Ascii antTask = new Native2Ascii();
            antTask.setProject( antProject );

            antTask.setSrc( src );
            antTask.setDest( dest );
            antTask.setEncoding( encoding );
            antTask.setExt( ext );
            antTask.setExcludes( excludes );
            antTask.setIncludes( includes );

            antTask.execute();
        }
        catch ( BuildException ex )
        {
            String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            if ( failonerror )
            {
                throw new MojoFailureException( msg );
            }
            else
            {
                this.getLog().warn( msg );
            }
        }
    }

}
