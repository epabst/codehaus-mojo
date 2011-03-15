package org.codehaus.mojo.j2me;

/* 
 * The MIT License
 * 
 * Copyright (c) 2004, The Codehaus
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
import java.io.InputStream;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * The preverify MoJo runs the preverify command from the given J2ME SDK.
 * <p>
 * Preverification takes place in the phase process-classes, that means after
 * successfull compilation. The MoJo takes the classes to preverify from
 * ${project.build.outputDirectory} and places the preverified classes into the
 * same directory. Therefore no change to the packaging phase is neccessary.
 * </p>
 * 
 * @goal preverify
 * @phase process-classes
 * @description Preverifies j2me class files
 * 
 * @author <a href="frank.seidinger@novity.de">Frank Seidinger</a>
 */
public class PreverifyMojo
    extends AbstractMojo
{
    /**
     * The name of the preverify executable
     */
    private static final String PREVERIFY = "preverify";

    /**
     * The Maven project reference.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The path to the j2me sdk to use for preverifying
     * 
     * @parameter
     * @required
     */
    private File sdkPath;

    /**
     * The logger to use
     */
    private Log log;

    /**
     * The main method of this MoJo
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        log = getLog();
        log.debug( "starting plugin" );

        // build preverify command executable
        validateSdk();
        File binPath = getBinPath( sdkPath );
        File cmd = getPreverifyCmd( binPath );

        // build class path
        String classPath = getClassPath( project );

        // get target directory
        String target = project.getBuild().getOutputDirectory();

        try
        {
            // run preverify
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec( new String[] {
                cmd.getPath(),
                "-classpath",
                classPath.toString(),
                "-d",
                target,
                target } );

            // get error stream
            InputStream err = process.getErrorStream();

            // wait for termination
            int exitCode = process.waitFor();

            // read error stream
            byte[] errBuffer = new byte[err.available()];
            err.read( errBuffer );

            // check return code
            if ( exitCode != 0 )
            {
                log.error( "error output :\n" + new String( errBuffer ) );
                throw new MojoFailureException( "the preverify command returned: " + exitCode );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "the preverify command failed to execute", e );
        }
        catch ( InterruptedException e )
        {
            throw new MojoExecutionException( "the preverify command failed to execute", e );
        }

        log.debug( "finished plugin" );
    }

    private void validateSdk()
        throws MojoFailureException
    {
        log.debug( "sdkPath = " + sdkPath );

        // test, if the sdk path is valid
        if ( sdkPath.exists() == false )
        {
            throw new MojoFailureException( "the path to the sdk cannot be found" );
        }

        // test, if the sdk path is a directory
        if ( sdkPath.isDirectory() == false )
        {
            throw new MojoFailureException( "the path to the sdk is not a directory" );
        }
    }

    private File getBinPath( File sdkPath )
        throws MojoFailureException
    {
        // get the bin path
        File binPath = new File( sdkPath.getPath() + File.separator + "bin" );
        log.debug( "binPath = " + binPath );

        // test, if bin path is valid
        if ( ( binPath.exists() == false ) || ( binPath.isDirectory() == false ) )
        {
            throw new MojoFailureException( "the sdk contains no bin directory" );
        }

        return binPath;
    }

    private File getPreverifyCmd( File binPath )
        throws MojoFailureException
    {
        // get os identification string
        String osName = System.getProperty( "os.name" );
        log.debug( "osName = " + osName );

        // preset preverify command name
        String preverifyCommand = PREVERIFY;

        // test, if os is windows based
        boolean isWindowsBased = osName.startsWith( "Windows" );
        log.debug( "os is windows based: " + isWindowsBased );

        // adjust command name to meet windows naming conventions
        if ( isWindowsBased )
        {
            preverifyCommand = preverifyCommand + ".exe";
            log.debug( "adjusted preverify command to meet windows naming convetions" );
        }

        // get the verify command
        File cmd = new File( binPath.getPath() + File.separator + preverifyCommand );
        log.debug( "cmd = " + cmd );

        // test, if verify command is valid
        if ( ( cmd.exists() == false ) || ( cmd.isFile() == false ) )
        {
            throw new MojoFailureException( "the preverify command cannot be found" );
        }
        if ( cmd.canRead() == false )
        {
            throw new MojoFailureException( "you have no access rights to the preverify command" );
        }

        return cmd;
    }

    private String getClassPath( MavenProject project )
        throws MojoExecutionException
    {
        try
        {
            // create buffer receiving full classpath
            StringBuffer classPath = new StringBuffer();

            // loop through classpath elements
            List artifacts = project.getRuntimeClasspathElements();
            for ( int idx = 0; idx < artifacts.size(); idx++ )
            {
                // get next dependency
                String classpathElement = (String) artifacts.get( idx );

                // add path seperator if necessary
                if ( classPath.length() > 0 )
                {
                    classPath.append( File.pathSeparator );
                }

                // add path to dependency
                classPath.append( classpathElement );
            }

            log.debug( "classpath: " + classPath );
            return classPath.toString();
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( "faild to resolve dependency", e );
        }
    }
}
