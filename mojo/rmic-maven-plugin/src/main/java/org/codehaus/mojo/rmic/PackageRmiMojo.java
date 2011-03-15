package org.codehaus.mojo.rmic;

/*
 * Copyright (c) 2004-2007, Codehaus.org
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
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Creates a jar containing the rmic generated classes.
 * 
 * @goal package
 * @phase package
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PackageRmiMojo
    extends AbstractMojo
{
    /**
     * The directory to which the generated jar should be written.
     * 
     * @parameter default-value="${project.build.directory}"
     */
    private File target;

    /**
     * The base name of the generated jar.  This name does not include
     * the classifier or the extension.
     * 
     * @parameter default-value="${project.build.finalName}"
     */
    private String finalName;

    /**
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * Classifier to append to the jar.
     * 
     * @parameter default-value="client"
     */
    private String classifier;

    /**
     * This directory contains the output of rmic (where the Stub classes are located). This is not the directory where
     * the jar file will be written.
     * 
     * @parameter default-value="${project.build.directory}/rmi-classes"
     */
    private File outputDirectory;

    /**
     * The file patterns to include in the jar. By default, all classes ending with _Stub.class
     * will be included.
     * 
     * @parameter
     */
    private String[] includes;

    /**
     * The file patterns to exclude from the jar.
     * 
     * @parameter
     */
    private String[] excludes;

    /**
     * The maven project helper.
     * 
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Execute the plugin
     * 
     * @throws MojoExecutionException If there is a problem executing the plugin.
     */
    public void execute()
        throws MojoExecutionException
    {
        if ( includes == null )
        {
            includes = new String[] { "**/*_Stub.class" };
        }
        if ( excludes == null )
        {
            excludes = new String[0];
        }

        File stubJar = new File( target, finalName + "-" + classifier + ".jar" );

        JarArchiver jarArchiver = new JarArchiver();

        jarArchiver.setDestFile( stubJar );

        try
        {
            jarArchiver.addDirectory( outputDirectory, includes, excludes );

            getLog().info( "Building RMI stub jar: " + stubJar.getAbsolutePath() );

            jarArchiver.createArchive();

        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "Could not create the RMI stub jar", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not create the RMI stub jar", e );
        }

        projectHelper.attachArtifact( project, "jar", classifier, stubJar );
    }
}
