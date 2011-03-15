/*
 * Copyright 2008 Codehaus
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

package org.codehaus.mojo.nsis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.nsis.io.ProcessOutputConsumer;
import org.codehaus.mojo.nsis.io.ProcessOutputHandler;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Compile the <code>setup.nsi</code> into an installer executable.
 *
 * @author <a href="mailto:joakime@apache.org">Joakim Erdfelt</a>
 * @version $Id$
 * @goal compile
 * @phase compile
 */
public class NsisCompileMojo extends AbstractMojo implements
        ProcessOutputConsumer {

    /**
     * The binary to execute for makensis.
     * 
     * Default assumes that the makensis can be found in the path.
     * 
     * @parameter expression="${nsis.makensis.bin}" default-value="makensis"
     * @required
     */
    private String makensisBin;

    /**
     * The main setup script.
     * 
     * @parameter expression="${nsis.setup.nsi}" default-value="setup.nsi"
     * @required
     */
    private String setupScript;
    
    /**
     * The generated installer exe output file.
     * 
     * @parameter expression="${nsis.output.file}" 
     *            default-value="${project.build.directory}/${project.build.finalName}.exe"
     * @required
     */
    private String outputFile;
    
    /**
     * The maven project itself.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * Internal project helper component.
     * @component
     */
    private MavenProjectHelper projectHelper;

    private boolean isWindows;
    
    public NsisCompileMojo() {
        isWindows = (System.getProperty( "os.name" ).startsWith("Windows"));
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        validate();
        List<String> commands = new ArrayList<String>();
        commands.add( makensisBin ); // The makensis binary
        
        File targetFile = FileUtils.resolveFile( new File( project.getBuild().getDirectory() ), outputFile );
        
        File targetDirectory = targetFile.getParentFile();
        
        //be sure the target directory exists
        if(!targetDirectory.exists()) 
        {
            try
            {
                FileUtils.forceMkdir( targetDirectory );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Can't create target directory " + targetDirectory.getAbsolutePath(), e );
            }
        }
        
        String optPrefix = (isWindows) ? "/" : "-";
        commands.add( optPrefix + "X" + "OutFile " + StringUtils.quoteAndEscape( targetFile.getAbsolutePath(), '\'' ) ); // The installer output file
        commands.add( optPrefix + "V2" ); // Verboseness Level
        commands.add( setupScript ); // The setup script file
        
        ProcessBuilder builder = new ProcessBuilder( commands );
        builder.directory( project.getBasedir() ); // The working directory
        builder.redirectErrorStream(true);

        if(getLog().isDebugEnabled()) {
            getLog().debug( "directory:  " + builder.directory().getAbsolutePath() );
            getLog().debug( "commands  " + builder.command().toString() );
        }
        try {
            Process process = builder.start();
            ProcessOutputHandler output =
                new ProcessOutputHandler(process.getInputStream(), this);
            output.startThread();

            int status;
            try {
                status = process.waitFor();
            } catch (InterruptedException e) {
                status = process.exitValue();
            }

            output.setDone(true);

            if (status != 0) {
                throw new MojoExecutionException(
                    "Execution of makensis compiler failed. See output above for details.");
            }
            
            // Attach the exe to the install tasks.
            projectHelper.attachArtifact( project, "exe", null, targetFile );
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to execute makensis", e);
        }
    }

    public void consumeOutputLine(String line) {
        getLog().info("[MAKENSIS] " + line);
    }
    
    private void validate() throws MojoFailureException
    {
        // check if the setup-file contains the property 'OutFile'
        // this will write the outputFile relative to the setupScript, no matter if it's configured otherwise in the pom
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader( setupScript ) );
            for ( String line = reader.readLine(); line != null; line = reader.readLine() )
            {
                if ( line.trim().startsWith( "OutFile " ) )
                {
                    getLog().warn( "setupScript contains the property 'OutFile'. "
                                       + "Please move this setting to the plugin-configuration" );
                }
            }
        }
        catch ( IOException e )
        {
            // we can't find and/or read the file, but let nsis throw an exception
        }
    }
}
