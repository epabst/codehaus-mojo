package org.codehaus.mojo.runtime.builder;

/*
 * Copyright (c) 2004, Codehaus.org
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.runtime.model.Runtime;
import org.codehaus.mojo.runtime.model.ShellRuntime;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="jesse.mcconnell@gmail.com">Jesse McConnell</a>
 * @version $Id$
 */
public class MavenShellRuntimeBuilder
    extends AbstractMavenRuntimeBuilder
{
    private File runtimeOutputDirectory;

    private File runtimeLibraryDirectory;

    private File runtimeExecutableDirectory;

    private File runtimeMetaDirectory;

    /* (non-Javadoc)
	 * @see org.codehaus.mojo.runtime.builder.shell.MavenRuntimeBuilder#build(org.codehaus.mojo.runtime.model.Runtime, org.apache.maven.project.MavenProject, java.io.File, java.lang.String)
	 */
    public void build( Runtime runtime, MavenProject project, File runtimeFile, File outputDirectory )
        throws MavenRuntimeBuilderException
    {
        try
        {
            initializeOutputDirectories( runtime, outputDirectory );

            // drop shell script

            String scriptName = runtimeExecutableDirectory + File.separator + runtime.getShell().getId() + ".sh";
            IOUtil.copy( getClass().getResourceAsStream( "/templates/runtime-sh.template" ),
                         new FileWriter( new File( scriptName ) ) );

            // copy libs to script dir

            List classpathElements = project.getRuntimeClasspathElements();

            for ( Iterator i = classpathElements.iterator(); i.hasNext(); )
            {
                String element = (String) i.next();
                if ( element.endsWith( ".jar" ) )
                {
                    FileUtils.copyFileToDirectory( element , runtimeLibraryDirectory.getAbsolutePath() );
                }
            }

            // copy runtime to META-INF in build dir
            FileUtils.copyFileToDirectory( runtimeFile, runtimeMetaDirectory );

        }
        catch ( DependencyResolutionRequiredException der )
        {
            throw new MavenRuntimeBuilderException( "error getting dependencies while building runtime", der );
        }
        catch ( IOException ex )
        {
            throw new MavenRuntimeBuilderException( "Exception while building the runtime", ex );
        }
    }

    private void initializeOutputDirectories( Runtime runtime, File outputDirectory )
        throws MavenRuntimeBuilderException
    {
        try
        {
            ShellRuntime shell = runtime.getShell();

            if ( shell == null )
            {
                System.out.println( "ahhh! null" );
            }

            runtimeOutputDirectory = new File( outputDirectory, runtime.getShell().getId() );
            runtimeLibraryDirectory = new File( runtimeOutputDirectory , "lib" );
            runtimeExecutableDirectory = new File( runtimeOutputDirectory, "bin" );
            runtimeMetaDirectory = new File( runtimeOutputDirectory, "META-INF/runtimes" );

            mkdir( runtimeOutputDirectory );
            mkdir( runtimeLibraryDirectory );
            mkdir( runtimeExecutableDirectory );
            mkdir( runtimeMetaDirectory );
        }
        catch ( Exception e )
        {
            throw new MavenRuntimeBuilderException( "error creating output directory structure", e );
        }
    }
}
