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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Creates a java descriptor for this J2ME project
 * 
 * @goal create-jad
 * @phase package
 * @description Creates a java descriptor for this J2ME project
 * 
 * @author <a href="frank.seidinger@novity.de">Frank Seidinger</a>
 */

public class JADCreatorMojo
    extends AbstractMojo
{
    /**
     * The Maven project reference.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The midlets name
     * 
     * @parameter expression="${j2me.midlet.name}"
     * @required
     */
    private String midletName;

    /**
     * The midlets vendor
     * 
     * @parameter expression="${j2me.midlet.vendor}"
     * @required
     */
    private String midletVendor;

    /**
     * The midlets version
     * 
     * @parameter expression="${j2me.midlet.version}"
     * @required
     */
    private String midletVersion;

    /**
     * The midlets configuration
     * 
     * @parameter expression="${j2me.midlet.configuration}"
     * @required
     */
    private String j2meConfiguration;

    /**
     * The midlets profile
     * 
     * @parameter expression="${j2me.midlet.profile}"
     * @required
     */
    private String j2meProfile;

    /**
     * The logger to use
     */
    private Log log;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        log = getLog();
        log.debug( "starting plugin" );

        try
        {
            File buildDir = new File( project.getBuild().getDirectory() );
            log.debug( "working in directory: " + buildDir );

            // create jad file
            String jadFileName = project.getArtifactId() + "-" + project.getVersion() + ".jad";
            String jarFileName = project.getArtifactId() + "-" + project.getVersion() + "." + project.getPackaging();
            File jadFile = new File( buildDir, jadFileName );
            File jarFile = new File( buildDir, jarFileName );
            log.debug( "creating jad file: " + jadFileName );

            // create a writer for the jad file
            FileOutputStream jadStream = new FileOutputStream( jadFile );
            PrintWriter jadWriter = new PrintWriter( jadStream );

            // write entries
            jadWriter.println( "MIDlet-Name: " + midletName );
            jadWriter.println( "MIDlet-Vendor: " + midletVendor );
            jadWriter.println( "MIDlet-Version: " + midletVersion );
            jadWriter.println( "MicroEdition-Configuration: " + j2meConfiguration );
            jadWriter.println( "MicroEdition-Profile: " + j2meProfile );
            jadWriter.println( "MIDlet-Jar-URL: " + jarFile.getName() );
            jadWriter.println( "MIDlet-Jar-Size: " + jarFile.length() );

            // close jad file
            jadWriter.close();

        }
        catch ( FileNotFoundException ex )
        {
            log.error( "file not found", ex );
        }

        log.debug( "finished plugin" );
    }
}
