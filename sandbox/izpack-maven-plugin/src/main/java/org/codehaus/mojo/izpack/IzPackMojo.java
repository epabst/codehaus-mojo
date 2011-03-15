package org.codehaus.mojo.izpack;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.izforge.izpack.ant.IzPackTask;
import com.izforge.izpack.compiler.CompilerConfig;
import com.izforge.izpack.compiler.CompilerException;

/**
 * Build an IzPack installer
 * 
 * @goal izpack
 * @phase package
 * @requiresDependencyResolution package
 * @version $Id:  $
 * @author Miguel Griffa
 */
public class IzPackMojo
    extends AbstractMojo
{

    /**
     * The izpack default configuration file
     * @parameter  default-value="${basedir}/src/izpack/install.xml"
     * @optional
     */
    private File izpackConfig;

    /**
     * Name of the installer configuration file to be generated
     * @parameter default-value="${project.build.directory}/${project.build.finalName}.jar"
     * @optional
     */
    private String installerFile;

    /**
     * Basedir for the the izpack project
     * @parameter default-value="${basedir}/src/izpack"
     * @optional
     */
    private File basedir;

    /**
     * kind argument for izpack, standard or web
     * @parameter expression="standard" default-value="standard"
     * @optional
     */
    private String kind;

    /**
     * @parameter expression="${project.compileClasspathElements}"
     */
    private List classpathElements;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        IzPackTask task = new IzPackTask();
        task.setInput( izpackConfig.getAbsolutePath() );
        task.setOutput( installerFile );
        task.setBasedir( izpackConfig.getParentFile().getAbsolutePath() );

        checkOutputDirectory( installerFile );
        task.setBasedir( basedir.getAbsolutePath() );

        // TODO it would be nice to pass properties to compiler, somehow
        buildInstaller();
    }

    private void checkOutputDirectory( String file )
        throws MojoFailureException
    {
        File f = new File( file );
        File dir = f.getParentFile();
        if ( !dir.exists() )
        {
            if ( !dir.mkdirs() )
            {
                throw new MojoFailureException( "Could not create directory " + dir );
            }
        }
    }

    private void buildInstaller()
        throws MojoExecutionException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( getClassLoader( classLoader ) );

        try
        {
            // else use external configuration referenced by the input attribute
            CompilerConfig c = new CompilerConfig( this.izpackConfig.getAbsolutePath(), this.basedir.getAbsolutePath(),
                                                   this.kind, this.installerFile );

            c.executeCompiler();

            if ( ! c.wasSuccessful() )
            {
                throw new MojoExecutionException( "IzPack compilation ERROR" );
            }
        }
        catch ( Exception ce )
        {
            throw new MojoExecutionException( "IzPack compilation ERROR", ce );
        }
        finally
        {
            if ( classLoader != null )
            {
                Thread.currentThread().setContextClassLoader( classLoader );
            }

        }
    }

    private ClassLoader getClassLoader( ClassLoader classLoader )
        throws MojoExecutionException
    {
        List classpathURLs = new ArrayList();

        for ( int i = 0; i < classpathElements.size(); i++ )
        {
            String element = (String) classpathElements.get( i );
            try
            {
                File f = new File( element );
                URL newURL = f.toURI().toURL();
                classpathURLs.add( newURL );
                getLog().debug( "Added to classpath " + element );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Error parsing classpath " + element + " " + e.getMessage() );
            }
        }

        URL[] urls = (URL[]) classpathURLs.toArray( new URL[classpathURLs.size()] );
        
        return new URLClassLoader( urls, classLoader );
    }

}
