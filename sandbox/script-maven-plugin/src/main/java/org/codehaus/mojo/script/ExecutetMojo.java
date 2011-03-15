package org.codehaus.mojo.script;
/*
 * Copyright 2006 Tomasz Pik.
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
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * A Maven 2 plugin runs a script using BSF.
 *
 * @goal execute
 * @requiresDependencyResolution
 * @description Runs a script
 * @author <a href="mailto:tompik@gmail.com">Tomasz Pik</a>
 * @version $Id$
 */
public class ExecutetMojo extends AbstractMojo
{

    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The location of the script to be executed.
     *
     * @parameter
     */
    private String scriptFile;

    /**
     * The location in-line script to be executed.
     *
     * @parameter
     */
    private String script;

    /**
     * Pass the project object in as a property to your script.
     *
     * @parameter default-value="false"
     */
    private boolean passProjectAsProperty;

    /**
     * Name for project object as a property in your script.
     *
     * @parameter default-value="project"
     */
    private String nameOfProjectProperty;

    /**
     * Name of language.
     *
     * @parameter
     * @required
     */
    private String language;

    /**
     * Optional engine class name.
     *
     * <tt>engine</tt> must be a name of class, that implements {@link org.apache.bsf.BSFEngine}.
     *
     * @parameter
     */
    private String engine;

    /**
     * Loads and executes a script.
     *
     * @throws MojoExecutionException if script cannot be loaded or executed.
     */
    public void execute() throws MojoExecutionException
    {
        final String code = getScriptString();
        try
        {
            ClassLoader parent = getClass().getClassLoader();
            List classpathFiles = project.getCompileClasspathElements();
            URL[] urls = new URL[classpathFiles.size() + 1];
            for ( int i = 0; i < classpathFiles.size(); ++i )
            {
                urls[i] = new File( ( String ) classpathFiles.get( i ) ).toURL();
            }

            urls[classpathFiles.size()] = new File( project.getBuild().getOutputDirectory() ).toURL();
            URLClassLoader cl = new URLClassLoader( urls, parent );
            BSFManager manager = new BSFManager();
            manager.setClassLoader( cl );

            if ( engine != null )
            {
                BSFManager.registerScriptingEngine( language, engine, new String[] {} );
            }
            if ( passProjectAsProperty )
            {
                manager.declareBean( nameOfProjectProperty, project, project.getClass() );
            }

            String scriptName = scriptFile;
            if ( scriptName == null )
            {
                scriptName = "inline";
            }

            manager.exec( language, scriptName, 0, 0, code );

        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    /**
     * Returns code to be executed.
     *
     * Checks, if <tt>script</tt> parameter has been configured,
     * if no, loads script using value of <tt>scriptFile</tt> parameter.
     *
     * @return code to be executed.
     * @throws MojoExecutionException if one of parameters is not configured or script cannot be loaded.
     */
    private String getScriptString() throws MojoExecutionException
    {
        if ( script != null )
        {
            return script;
        }
        if ( scriptFile != null )
        {
            try
            {
                FileReader reader = new FileReader( scriptFile );
                String code = IOUtils.getStringFromReader( reader );
                reader.close();
                return code;
            }
            catch ( IOException ioe )
            {
                throw new MojoExecutionException( "Cannot read file " + scriptFile, ioe );
            }
        }
        throw new MojoExecutionException( "one of (script, scriptFile) parameters must be set" );
    }
}
