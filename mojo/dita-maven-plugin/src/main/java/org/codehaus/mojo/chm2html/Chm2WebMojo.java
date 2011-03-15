package org.codehaus.mojo.chm2html;

/*
 * Copyright 2000-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.dita.AbstractProjectMojo;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Convert DITA Open Toolkit's Microsoft CHM output file, produced by htmlhelp transtype, to pure
 * HTML set of files. Require commercial chm2web utility from <a
 * href="http://chm2web.aklabs.com">A!K Research Labs</a>
 * 
 * @goal chm2web
 * @requiresProject false
 */
public class Chm2WebMojo
    extends AbstractProjectMojo
{

    /**
     * @parameter expression="${chm2web.exe}"
     *            default-value="c:\\Program Files\\A!K Research Labs\\chm2web\\chm2web.exe"
     * @since 1.0-beta-1
     */
    private File chm2webExe;

    /**
     * Allow skipping the conversion when chm2web is not available.
     * 
     * @parameter expression="${chm2web.ignoreIfExeNotExist}" default-value="false"
     * @since 1.0-beta-1
     */
    private boolean ignoreIfChm2WebNotExist;

    /**
     * Chm2Web configuration file
     * 
     * @parameter expression="${chm2web.descriptor}"
     *            default-value="${basedir}/src/main/chm2web/${project.artifactId}.chm2web"
     * @since 1.0-beta-1
     */
    private File descriptor;

    /**
     * Controls whether this plugin tries to archive the output directory and attach archive to the
     * project.
     * 
     * @parameter expression="${chm2web.attach}" default-value="false"
     * @since 1.0-beta-1
     */
    private boolean attach = false;

    /**
     * Chm2Web output directory. Must match with the output directory found in your chm2web's
     * descriptor file.
     * 
     * @parameter expression="${chm2web.outputDirectory}"
     *            default-value="${project.build.directory}/chm2web"
     * @since 1.0-beta-1
     */
    private File outputDirectory;

    /**
     * Output file classifier to be attached to the project.
     * 
     * @parameter expression="${chm2web.attachClassifier}" 
     * @since 1.0-beta-1
     */
    private String attachClassifier;

    /**
     * Output file extension to be attached to the project.
     * 
     * @parameter expression="${chm2web.attachType}" default-value="jar"
     * @since 1.0-beta-1
     */
    private String attachType;

    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
            return;
        }

        if ( !chm2webExe.exists() && ignoreIfChm2WebNotExist )
        {
            return;
        }

        Commandline cl = new Commandline();

        cl.setExecutable( chm2webExe.getAbsolutePath() );
        cl.createArg().setFile( descriptor );
        cl.createArg().setValue( "/q" );
        cl.createArg().setValue( "/d" );

        cl.setWorkingDirectory( project.getBasedir() );

        executeCommandline( cl );

        if ( attach )
        {
            archiveAndAttachTheOutput( this.outputDirectory, attachClassifier, attachType );
        }
    }


}
