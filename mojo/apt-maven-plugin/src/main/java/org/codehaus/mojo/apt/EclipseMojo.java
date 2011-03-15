package org.codehaus.mojo.apt;

/*
 * The MIT License
 *
 * Copyright 2006-2008 The Codehaus.
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generates Eclipse files for apt integration.
 * 
 * @author <a href="mailto:jubu@codehaus.org">Juraj Burian</a>
 * @version $Id$
 * @goal eclipse
 * @requiresDependencyResolution compile
 */
public class EclipseMojo extends ProcessMojo
{
    // TODO: tidy up code and write tests
    // TODO: move to maven-eclipse-plugin?

    // read-only parameters ---------------------------------------------------

    /**
     * The directory to run apt in.
     * 
     * @parameter default-value="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * The local artifact repository.
     * 
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    // AbstractAptMojo methods ------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeImpl() throws MojoExecutionException
    {
        // exclude this:
        ArtifactHandler artifactHandler = getProject().getArtifact().getArtifactHandler();

        if ( !"java".equals( artifactHandler.getLanguage() ) )
        {
            getLog().info( "Not executing apt eclipse goal as the project is not a Java classpath-capable package" );
            return;
        }

        if ( !isAptDefined() )
        {
            getLog().info( "Not executing apt eclipse goal, plugin is not configuret for this project." );
            return;
        }

        getLog().info( "Executing apt eclipse goal!" );

        // write prefs file

        File prefs = new File( basedir, ".settings" + File.separator + "org.eclipse.jdt.apt.core.prefs" );

        try
        {
            prefs.getParentFile().mkdirs();
            prefs.createNewFile();
        }
        catch ( IOException exception )
        {
            throw new MojoExecutionException( "Can't create file: " + prefs.getPath() );
        }

        PrintWriter out = null;

        try
        {
            out = new PrintWriter( prefs, "ISO-8859-1" );
        }
        catch ( FileNotFoundException exception )
        {
            // can't happen
            throw new MojoExecutionException( null, exception );
        }
        catch ( UnsupportedEncodingException exception )
        {
            // can't happen
            throw new MojoExecutionException( null, exception );
        }

        out.println( "#" + new Date() );
        out.println( "eclipse.preferences.version=1" );
        out.println( "org.eclipse.jdt.apt.aptEnabled=true" );
        out.println( "org.eclipse.jdt.apt.genSrcDir=" + getOutputDirectory().getPath() );

        // write processor options

        if ( getOptions() != null )
        {
            for ( String option : getOptions() )
            {
                out.println( "org.eclipse.jdt.apt.processorOptions/" + option );
            }
        }

        out.close();

        // write .factorypath

        File factorypathFile = new File( basedir, ".factorypath" );

        try
        {
            prefs.createNewFile();
        }
        catch ( IOException exception )
        {
            throw new MojoExecutionException( "Can't create file: " + factorypathFile.getPath() );
        }

        try
        {
            out = new PrintWriter( factorypathFile, "UTF-8" );
        }
        catch ( FileNotFoundException exception )
        {
            // can't happen
        }
        catch ( UnsupportedEncodingException exception )
        {
            // can't happen
        }

        String localRepo = null;

        try
        {
            localRepo = new File( localRepository.getBasedir() ).getCanonicalPath();
        }
        catch ( IOException exception )
        {
            throw new MojoExecutionException( "Local repository: " + localRepository.getBasedir()
                                              + " doesn't exists!" );
        }

        out.println( "<factorypath> " );

        for ( String factorypathentry : getClasspathElements() )
        {
            // EXTJAR VARJAR
            String kind = "EXTJAR";

            // force skip tools jar
            if ( factorypathentry.endsWith( "tools.jar" ) )
            {
                continue;
            }

            try
            {
                String tmp = new File( factorypathentry ).getCanonicalPath();

                if ( tmp.startsWith( localRepo ) )
                {
                    kind = "VARJAR";
                    factorypathentry = tmp.replace( localRepo, "" );
                    factorypathentry = "M2_REPO" + factorypathentry.replace( "\\", "/" );
                }
            }
            catch ( IOException exception )
            {
                // ignore this
            }

            String batchModeString = hasAnnotationProcessorFactory( factorypathentry ) ? "true" : "false";

            out.println( "    <factorypathentry kind=\"" + kind + "\" id=\"" + factorypathentry
                            + " \" enabled=\"true\" runInBatchMode=\"" + batchModeString + "\"/>" );
        }

        out.println( "</factorypath> " );
        out.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected File getOutputDirectory()
    {
        String path = super.getOutputDirectory().getPath();

        // return only relative part of generated dir and replace \ -> /
        path = path.replace( getProject().getBasedir().getAbsolutePath(), "" ).replace( "\\", "/" );

        return new File( path );
    }

    // private methods --------------------------------------------------------

    private boolean isAptDefined()
    {
        Set<Artifact> pluginArtifacts = CollectionUtils.genericSet( getProject().getPluginArtifacts(), Artifact.class );

        for ( Artifact artifact : pluginArtifacts )
        {
            if ( "apt-maven-plugin".equals( artifact.getArtifactId() ) )
            {
                return true;
            }
        }

        return false;
    }

    private static boolean hasAnnotationProcessorFactory( String factorypathentry )
    {
        try
        {
            if ( factorypathentry.endsWith( "jar" ) )
            {
                JarFile file = new JarFile( factorypathentry );

                if ( file.getEntry( "META-INF/services/com.sun.mirror.apt.AnnotationProcessorFactory" ) != null )
                {
                    return true;
                }
            }
        }
        catch ( IOException exception )
        {
            // ignore this
        }

        return false;
    }
}
