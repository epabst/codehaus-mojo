package org.codehaus.mojo.pde;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.pde.descriptor.DescriptorUtil;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Clean Eclipse PDE generated files. Supports cleaning of features and products only.
 * 
 * TODO: Add support for cleaning of fragments, plugins.
 * 
 * @goal clean
 * @phase clean
 * @requiresProject false
 * @version $Id:$ *
 * @author dtran@gmail.com
 */

public class EclipsePDECleanMojo
    extends AbstractEclipsePDEMojo
{

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File buildXml = new File( this.pdeDirectory, "build.xml" );

        if ( buildXml.exists() )
        {
            super.execute();

            try
            {
                clean();
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Failed to clean", e );
            }

            clean( buildXml );

            // remove build.xml if it is a generated one
            File buildPropertiesFile = new File( this.pdeDirectory, "build.properties" );
            if ( buildPropertiesFile.exists() )
            {
                Properties properties = new Properties();
                try
                {
                    properties.load( new FileInputStream( buildPropertiesFile ) );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Error loading: " + buildPropertiesFile );
                }
                if ( properties.getProperty( "custom", "false" ).equals( "false" ) )
                {
                    buildXml.delete();
                }
            }
        }
    }

    /**
     * Clean the pde project artifacts that an ant clean does not.
     * 
     * @throws MojoExecutionException build failures.
     * @throws IOException build failures.
     * 
     */
    private void clean()
        throws MojoExecutionException, IOException
    {
        String id = this.descriptor.getId();
        File pdeBuildDirectory = this.getPDEBuildDirectory();

        if ( "feature".equals( DescriptorUtil.getPDEType( descriptor ) ) )
        {
            File file = new File( pdeBuildDirectory, "package." + id + ".all.xml" );
            file.delete();

            file = new File( pdeBuildDirectory, "package." + id + ".xml" );
            file.delete();

            file = new File( pdeBuildDirectory, "assemble." + id + ".all.xml" );
            file.delete();

            file = new File( pdeBuildDirectory, "assemble." + id + ".xml" );
            file.delete();
        }
        else if ( "product".equals( DescriptorUtil.getPDEType( descriptor ) ) )
        {
            String buildLabel = buildConfigurationProperties.getString( "buildLabel" );
            String config = convertPdeConfigsToFilenameSuffix( buildConfigurationProperties.getString( "configs" ) );
            String configWithUnderscores = config.replace( '.', '_' );

            File file = new File( pdeBuildDirectory, "assemble.org.eclipse.pde.build.container.feature.all.xml" );
            file.delete();

            file = new File( pdeBuildDirectory, "assemble.org.eclipse.pde.build.container.feature." + config + ".xml" );
            file.delete();

            file = new File( pdeBuildDirectory, "finalFeaturesVersions.properties" );
            file.delete();

            file = new File( pdeBuildDirectory, "finalFeaturesVersions." + configWithUnderscores + ".properties" );
            file.delete();

            file = new File( pdeBuildDirectory, "finalPluginsVersions.properties" );
            file.delete();

            file = new File( pdeBuildDirectory, "finalPluginsVersions." + configWithUnderscores + ".properties" );
            file.delete();

            file = new File( pdeBuildDirectory, "package.org.eclipse.pde.build.container.feature.all.xml" );
            file.delete();

            file = new File( pdeBuildDirectory, "package.org.eclipse.pde.build.container.feature." + config + ".xml" );
            file.delete();

            file = new File( pdeBuildDirectory, buildLabel );
            FileUtils.deleteDirectory( file );
            
            file = new File( pdeDirectory, "temp.folder" );
            FileUtils.deleteDirectory( file );

            file = new File( pdeDirectory, "javaCompiler...args" );
            file.delete();            
        }
    }

    /**
     * Run the clean target for the specified ant file
     * 
     * @param antFile the build file to use
     * @throws MojoExecutionException build failures.
     */
    private void clean( File antFile )
        throws MojoExecutionException
    {
        Commandline cl = this.createCommandLine( antFile, "clean" );
        this.executeCommandLine( cl );
    }

}
