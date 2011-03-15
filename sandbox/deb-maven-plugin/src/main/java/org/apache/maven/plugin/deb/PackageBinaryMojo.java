package org.apache.maven.plugin.deb;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * @description A Maven 2 mojo which creates a Debian package from a Maven2 project.
 *
 * @goal package-binary
 * @phase package
 * @requiresDependencyResolution runtime
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PackageBinaryMojo
    extends AbstractDebMojo
{
    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * All files that are to be a part of the package has to be in this directory before the prototype file is
     * generated.
     *
     * @parameter expression="${project.build.directory}/debian/assembled-pkg"
     * @optional
     */
    private File packageRoot;

    /**
     * @parameter expression="${project.build.directory}"
     * @optional
     * @readonly
     */
    private File outputDirectory;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        ControlFile controlFile = getDebTool().generateControlFile();

        try
        {
            controlFile.toFile( packageRoot );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error while generating the control file", e );
        }

        String debFileName = getDebTool().getDebFileName();

        File debFile = new File( outputDirectory, debFileName );

        new Dpkg().buildPackage( packageRoot, debFile, useFakeroot );

        if ( getClassifier() == null )
        {
            getArtifact().setFile( new File( outputDirectory, getDebTool().getDebFileName() ) );
        }
        else
        {
            projectHelper.attachArtifact( project, debFile, getClassifier() );
        }
    }
}
