package org.apache.maven.plugin.deb;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * TODO currently there's a strong association from package=jar it'd be nice if this could
 * handle gracefully war and maven plugin packaging too
 *
 * @description A Maven 2 mojo which creates a Debian package from a Maven2 project.
 *
 * @execute phase=package
 * @phase package
 * @goal deb
 * @requiresProject
 * @requiresDependencyResolution runtime
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DebPlugin
    extends AbstractDebMojo
{
    /**
     * @parameter expression="${project.build.directory}"
     * @readonly
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter expression="${project.build.directory}/debian"
     * @readonly
     * @required
     */
    private File assemblyDirectory;

    /**
     * @parameter expression="${project.artifact.type}"
     * @readonly
     * @required
     */
    private String artifactType;

    /**
     * Directory in which JARs will be generated
     * @parameter default-value="usr/share/java/${project.groupId}"
     * @readonly
     * @required
     */
    private String libTargetDirectory;

    /**
     * Includes the JavaDoc in the Debian Package
     * @parameter default-value="true"
     * @readonly
     * @required
     */
    private boolean includeJavaDoc;

    /**
     * Directory in which JavaDocs are to be found.  This plugin assumes that you have bound the javadoc goal of the
     * Javadoc plugin to package in the lifecycle
     * @parameter expression="${basedir}/target/site/apidocs"
     * @readonly
     * @required
     */
    private File docSourceDirectory;

    /**
     * Directory in which JavaDocs will be generated
     * @parameter expression="usr/share/doc/${project.groupId}/${project.artifactId}/api"
     * @readonly
     * @required
     */
    private String docTargetDirectory;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Starting .deb build" );
        createDeb();
        getLog().info( ".deb build done" );
    }

    private void createDeb()
        throws MojoExecutionException, MojoFailureException
    {
        // TODO check possible values or artifact.getType
        if ( !"jar".equals( artifactType ) )
        {
            throw new MojoFailureException( "This plugin can only package jar files." );
        }

        try
        {
            initFileSystem( assemblyDirectory );

            getDebTool().generateControlFile().toFile( assemblyDirectory );

            // Copy the resources
            copyFiles( assemblyDirectory, getArtifact().getArtifactId(), getArtifact().getVersion() );

            // build the package

            File debFile = new File( getDebTool().getDebFileName() );

            new Dpkg().buildPackage( assemblyDirectory, debFile, useFakeroot );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error building deb", e );
        }
    }

    /**
     * Function initializes the filesystem usd as an intermediate staging area for the creation of the
     * Debian package.
     *
     * TODO: Maybe, I'm not familiar enough with the Debian standards, but shouldn't /usr/local/jars be something the user could override?
     * @param debian
     * @throws MojoExecutionException
     * @throws IOException
     */
    private void initFileSystem( File debian )
        throws MojoExecutionException, IOException
    {
        getLog().debug( "Creating filesystem in " + debian );

        FileUtils.deleteDirectory( debian );
        FileUtils.forceMkdir( new File( debian, "DEBIAN" ) );
        FileUtils.forceMkdir( new File( debian, libTargetDirectory ) );
        if( includeJavaDoc ) {
        	FileUtils.forceMkdir( new File( debian, docTargetDirectory ) );
        }
    }

    private void copyFiles( File debian, String artifactId, String version)
        throws Exception
    {
    	// TODO: This plugin assumes that the JAR is going to be created in this location, is there a way to check the finalname and outputdirectory from the jar plugin?
        String jarName = artifactId + "-" + version + ".jar";

        getLog().debug( "Copying the package control files." );

        File jar = new File( outputDirectory, jarName );

        if ( jar.exists() )
        {
            FileUtils.copyFileToDirectory( jar, new File( debian, libTargetDirectory ) );
        }
        else
        {
            throw new Exception( "Could not find the jar file: " + jar );
        }

        if( docSourceDirectory.exists() ) {
        	File[] docFiles = docSourceDirectory.listFiles();
            for (int i = 0; i < docFiles.length; i++) {
                File docFile = docFiles[i];
                if (docFile.isFile()) {
                    FileUtils.copyFileToDirectory(docFile, new File(debian, docTargetDirectory));
                } else {
                    FileUtils.copyDirectory(docFile, new File(debian, docTargetDirectory));
                }
            }
        }


        // TODO: Would there be any benefit in allowing the user to override the permission bit?
        chmod( "755", debian );
    }

    private void chmod( String mode, File file )
        throws Exception
    {
    	// TODO: Wouldn't this require a "-R" flag?
        new SystemCommand().setCommand( "chmod" ).addArgument( mode ).addArgument( file.getPath() ).execute();
    }
}
