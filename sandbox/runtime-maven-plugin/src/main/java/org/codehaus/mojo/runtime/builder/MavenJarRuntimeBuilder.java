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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.runtime.model.Runtime;
import org.codehaus.mojo.runtime.model.io.xpp3.RuntimeXpp3Writer;
import org.codehaus.mojo.runtime.util.JarMaker;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="jesse.mcconnell@gmail.com">Jesse McConnell</a>
 * @version $Id$
 */
public class MavenJarRuntimeBuilder
    extends AbstractMavenRuntimeBuilder
{
    private File runtimeOutputDirectory;

    private File runtimeLibraryDirectory;

    private File runtimeExecutableDirectory;

    private File runtimeMetaDirectory;

    public void build( Runtime runtime, MavenProject project, File runtimeFile, File outputDirectory )
        throws MavenRuntimeBuilderException
    {
        try
        {
            // prepare output directories
            initializeOutputDirectories( runtime, outputDirectory );

            // add needed dependencies to the runtime object
            prepareRuntimeDependencies( runtime, project );

            prepareRuntimeClasses();

            prepareRuntimeExecutionDependencies( project );

            // we need all the compiled classes into a jar of their own
            JarMaker projectJar = new JarMaker( runtimeLibraryDirectory,
                                                project.getArtifactId() + "-" + project.getVersion() + "-runtime.jar" );

            projectJar.addDirectory( "**/**", "**/package.html,**/.svn/**", "",
                                     new File( project.getBuild().getDirectory() + File.separator + "classes" ) );
            projectJar.create();

            // make a dependency reference for this newly created jar
            runtime.getJar().addDependency(
                project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion() + "-runtime" );

            // place the finalized runtime descriptor
            RuntimeXpp3Writer runtimeWriter = new RuntimeXpp3Writer();

            FileWriter fw = new FileWriter( new File( runtimeMetaDirectory, "jar.runtime" ) );
            runtimeWriter.write( fw, runtime );
            fw.close();

            // finally generate the end game jar
            JarMaker runtimeJar = new JarMaker( outputDirectory,
                                                project.getArtifactId() + "-runtime-" + project.getVersion() + ".jar" );
            runtimeJar.addDirectory( "**/**", "**/package.html,**/.svn/**", "", runtimeOutputDirectory );
            runtimeJar.addManifestEntry( "Main-Class", "org.codehaus.mojo.runtime.execute.RuntimeExecutor" );
            runtimeJar.create();
        }
        catch ( IOException ex )
        {
            throw new MavenRuntimeBuilderException( "Exception while building the runtime", ex );
        }
    }

    private void prepareRuntimeClasses()
        throws MavenRuntimeBuilderException
    {
        List runtimeModelObjects = new ArrayList();
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/Executable.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/Runtime.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/JarRuntime.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/RuntimeBase.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/ShellRuntime.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/io/xpp3/RuntimeXpp3Reader.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/model/io/xpp3/RuntimeXpp3Writer.class" );
        runtimeModelObjects.add( "org/codehaus/plexus/util/xml/pull/XmlPullParserException.class" );
        runtimeModelObjects.add( "org/codehaus/plexus/util/xml/pull/XmlPullParser.class" );
        runtimeModelObjects.add( "org/codehaus/plexus/util/xml/pull/MXParser.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/execute/RuntimeClassLoader.class" );
        runtimeModelObjects.add( "org/codehaus/mojo/runtime/execute/RuntimeExecutor.class" );

        try
        {
            for ( Iterator i = runtimeModelObjects.iterator(); i.hasNext(); )
            {
                String file = (String) i.next();
                FileUtils.copyURLToFile( Thread.currentThread().getContextClassLoader().getResource( file ),
                                         new File( runtimeOutputDirectory + File.separator + file ) );
            }
        }
        catch ( IOException e )
        {
            throw new MavenRuntimeBuilderException( "unable to prepare runtime classes", e );
        }
    }

    private void initializeOutputDirectories( Runtime runtime, File outputDirectory )
        throws MavenRuntimeBuilderException
    {
        runtimeOutputDirectory = new File( outputDirectory, runtime.getJar().getId() );
        runtimeLibraryDirectory = new File( runtimeOutputDirectory, "lib" );
        runtimeExecutableDirectory = new File( runtimeOutputDirectory, "bin" );
        runtimeMetaDirectory = new File( runtimeOutputDirectory, "META-INF/runtimes" );

        mkdir( runtimeOutputDirectory );
        mkdir( runtimeLibraryDirectory );
        mkdir( runtimeExecutableDirectory );
        mkdir( runtimeMetaDirectory );
    }

    /**
     * put the dependencies that we are interested in using into the runtime object
     */
    private void prepareRuntimeDependencies( Runtime runtime, MavenProject project )
    {
        List artifactList = project.getRuntimeArtifacts();
        List dependencies = runtime.getJar().getDependencies();

        for ( Iterator i = artifactList.iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();

            String dependency = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();

            // let preexisting dependencies take precedence
            if ( !dependencies.contains( dependency ) )
            {
                runtime.getJar().addDependency( dependency );
            }
        }
    }

    //  copy libs to script dir
    private void prepareRuntimeExecutionDependencies( MavenProject project )
        throws MavenRuntimeBuilderException
    {
        try
        {
            List classpathElements = project.getRuntimeClasspathElements();

            for ( Iterator i = classpathElements.iterator(); i.hasNext(); )
            {
                String element = (String) i.next();

                if ( element.endsWith( ".jar" ) )
                {
                    FileUtils.copyFileToDirectory( (String) i.next(), runtimeLibraryDirectory.getAbsolutePath() );
                }
            }
        }
        catch ( Exception e )
        {
            throw new MavenRuntimeBuilderException( "unable to prepare runtime execution dependencies", e );
        }
    }
}
