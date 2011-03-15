/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest.components;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.types.Path;

/**
 * Run a give ANT script
 */
public class AntExecutor
{
    /**
     * Run an ANT script within the JVM..
     *
     * @param antFile the ANT scripts to be executed
     * @param userProperties the properties to be set for the ANT script
     * @param mavenProject the current maven project
     * @param artifacts the list of dependencies
     * @param target the ANT target to be executed
     * @throws DependencyResolutionRequiredException not dependencies were resolved
     * @throws BuildException the build failed
     */
    public AntExecutor( File antFile, Properties userProperties, MavenProject mavenProject, List artifacts, String target )
        throws BuildException, DependencyResolutionRequiredException
    {
        File antBaseDir = antFile.getParentFile();

        Project antProject = new Project();

        antProject.init();
        antProject.addBuildListener( this.createLogger() );
        antProject.setBaseDir( antBaseDir );

        ProjectHelper2.configureProject(antProject, antFile);
        // ProjectHelper2 projectHelper = new ProjectHelper2();
        // projectHelper.parse( antProject, antFile );

        Enumeration propertyKeys = userProperties.keys();
        while ( propertyKeys.hasMoreElements() )
        {
            String key = ( String ) propertyKeys.nextElement();
            String value = userProperties.getProperty( key );
            antProject.setUserProperty( key, value );
        }

        // NOTE: from maven-antrun-plugin

        Path p = new Path( antProject );
        p.setPath( StringUtils.join( mavenProject.getCompileClasspathElements().iterator(), File.pathSeparator ) );

        /* maven.dependency.classpath it's deprecated as it's equal to maven.compile.classpath */
        antProject.addReference( "maven.dependency.classpath", p );
        antProject.addReference( "maven.compile.classpath", p );

        p = new Path( antProject );
        p.setPath( StringUtils.join( mavenProject.getRuntimeClasspathElements().iterator(), File.pathSeparator ) );
        antProject.addReference( "maven.runtime.classpath", p );

        p = new Path( antProject );
        p.setPath( StringUtils.join( mavenProject.getTestClasspathElements().iterator(), File.pathSeparator ) );
        antProject.addReference( "maven.test.classpath", p );

        /* set maven.plugin.classpath with plugin dependencies */
        antProject.addReference( "maven.plugin.classpath", getPathFromArtifacts( artifacts, antProject ) );

        antProject.executeTarget( target );
    }

    /**
     * @return a logger to capture the ANT ouptut
     */
    private BuildLogger createLogger()
    {
        BuildLogger logger = new DefaultLogger();
        logger.setMessageOutputLevel( Project.MSG_INFO );
        logger.setOutputPrintStream( System.out );
        logger.setErrorPrintStream( System.err );
        logger.setEmacsMode( false );
        return logger;
    }

    /**
     * Build a class path based on a list of artifacts. Please
     * not that this is copy & wasted from maven-antrun-plugin.
     *
     * @param artifacts the artifacts constituting the class path
     * @param antProject the ant project
     * @return a path
     * @throws DependencyResolutionRequiredException not dependencies were resolved
     */
    public Path getPathFromArtifacts( Collection artifacts, Project antProject )
        throws DependencyResolutionRequiredException
    {
        if ( artifacts == null )
        {
            return new Path( antProject );
        }

        List list = new ArrayList( artifacts.size() );
        for ( Iterator i = artifacts.iterator(); i.hasNext(); )
        {
            Artifact a = ( Artifact ) i.next();
            File file = a.getFile();
            if ( file == null )
            {
                throw new DependencyResolutionRequiredException( a );
            }
            list.add( file.getPath() );
        }

        Path p = new Path( antProject );
        p.setPath( StringUtils.join( list.iterator(), File.pathSeparator ) );

        return p;
    }
}
