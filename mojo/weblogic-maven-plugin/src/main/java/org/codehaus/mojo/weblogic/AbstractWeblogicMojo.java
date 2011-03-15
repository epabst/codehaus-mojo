package org.codehaus.mojo.weblogic;
/*
 * Copyright 2008 The Apache Software Foundation.
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

/**
 * This class wraps handling for common functions for all weblogic
 * plugin implementations. Specifically, this class manages the
 * all important 'weblogic.home' property.
 *
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 * @description Abstract base class for weblogic mojo
 */
public abstract class AbstractWeblogicMojo
    extends AbstractMojo
{

    /**
     * Property key for weblogic.home
     */
    public static final String WEBLOGIC_HOME_KEY = "weblogic.home";

    /**
     * Property to set the weblogic home
     *
     * @parameter default-value="${weblogic.home}"
     */
    private String weblogicHome;

    /**
     * This is the set of artifacts that are defined as part of this project's
     * pom which are active for the compile scope. You should not need to
     * override this unless your pom file is incomplete.
     *
     * @parameter expression="${project.artifacts}"
     * @required
     * @readonly
     */
    private Set artifacts;

    /**
     * This is the set of dependencies that are defined as part of this project's
     * pom which are active for the scope. You should not need to
     * override this unless your pom file is incomplete.
     *
     * @parameter expression="${project.dependencies}"
     * @required
     * @readonly
     */
    private List dependencies;

    /**
     * These are the plugin artifacts for the weblogic mojo
     *
     * @parameter expression="${plugin.artifacts}"
     */
    private List pluginArtifacts;

    /**
     * This is the output directory for the artifacts. It defaults to
     * $project.build.directory.
     *
     * @parameter expression="${project.build.directory}"
     */
    private String outputDirectory;


    /**
	 * The the location of tools.jar file. this file must be dynamically added
	 * to the classloader due to some issues with the way maven works and a bug
	 * in jdk 1.6<br/>
     * <i>Note that the java.home location should be to the jre not the jdk</i>
	 *
	 * @parameter expression="${java.home}/../lib/tools.jar"
	 */
	private String toolsJar;

    /**
     * Getter for weblogic.home
     *
     * @return weblogic.home
     */
    public String getWeblogicHome()
    {
        return weblogicHome;
    }

    /**
     * Setter for weblogic.home
     *
     * @param weblogicHome - a fully qualified path to weblogic home directory
     */
    public void setWeblogicHome( String weblogicHome )
    {
        this.weblogicHome = weblogicHome;
    }

    /**
     * Getter for property artifacts.
     *
     * @return The value of artifacts.
     */
    public Set getArtifacts()
    {
        return artifacts;
    }

    /**
     * Setter for the artifacts.
     *
     * @param inArtifacts The value of artifacts.
     */
    public final void setArtifacts( Set inArtifacts )
    {
        this.artifacts = inArtifacts;
    }

    /**
     * Sets system property for weblogic.home
     *
     * @see #weblogicHome
     */
    public void execute()
        throws MojoExecutionException
    {
        setProperties();
        createTargetDirectory();
    }

    /**
     * Sets the weblogic.home property
     *
     * @see #WEBLOGIC_HOME_KEY
     * @see System#setProperty(String, String)
     */
    protected void setProperties()
    {
        if ( System.getProperty( WEBLOGIC_HOME_KEY ) == null ||
            System.getProperty( WEBLOGIC_HOME_KEY ).trim().length() == 0 )
        {
            if ( getLog().isInfoEnabled() )
            {
                getLog().info( " Setting " + WEBLOGIC_HOME_KEY + " = " + this.weblogicHome );
            }
            if ( this.weblogicHome == null && getLog().isWarnEnabled() )
            {
                getLog().warn( " Is the property weblogicHome configured? Users should configure this property " +
                    "to help weblogic functions perform as expected." );
            }
            else if ( this.weblogicHome != null )
            {
                final File home = new File( this.weblogicHome );
                if ( !home.exists() )
                {
                    getLog().warn( "weblogic.home folder '" + this.weblogicHome + "' does not appear to exist. This " +
                        "may cause issues with some weblogic functions." );
                }
                System.setProperty( WEBLOGIC_HOME_KEY, this.weblogicHome );
            }
        }
        else
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug(
                    "weblogicHome property was set externally to '" + System.getProperty( WEBLOGIC_HOME_KEY ) + "'." );
            }
        }
    }

    /**
     * Load tools jar into the selected classloader. We need to do it this way
     * because the addURL method is protected on the classloader. This is for a bug in 1.6
     * tools.jar implementations
     *
     * @param classLoader the classloader to load
     * @throws Exception
     */
    protected void addToolsJar(ClassLoader classLoader) throws Exception {
        if (this.toolsJar == null ||
                this.toolsJar.trim().length() == 0) {
            throw new MojoExecutionException(
                    "toolsJar is required for this mojo.");
        }
        final File f = new File(this.toolsJar);
        if (!f.exists()) {
            throw new MojoExecutionException(
                    "toolsJar was supplied but not found. was java.home correct?");
        }
        final URL u = f.toURI().toURL();
        final Class urlClass = URLClassLoader.class;
        final Method method =
                urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(classLoader, new Object[]{u});
    }


    /**
     * Generate a logger by using the default settings.
     *
     * @return a default logger that logs to System.out and System.err
     */
    protected BuildListener getDefaultLogger()
    {
        final DefaultLogger antLogger = new DefaultLogger();
        antLogger.setOutputPrintStream( System.out );
        antLogger.setErrorPrintStream( System.err );
        antLogger
            .setMessageOutputLevel( getLog().isDebugEnabled() ? Project.MSG_DEBUG : Project.MSG_INFO );
        return antLogger;
    }

    /**
     * Creates the target directory if it is missing from the file system.
     */
    protected void createTargetDirectory()
    {
        final File file = new File( this.outputDirectory );
        if ( !file.exists() )
        {
            file.mkdir();
        }
    }

    /**
     * Getter for the outputDirectory
     *
     * @return - this.outputDirectory
     */
    public String getOutputDirectory()
    {
        return this.outputDirectory;
    }

    /**
     * Setter for the output directory
     *
     * @param outputDirectory - the directory to write to
     */
    public void setOutputDirectory( String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Getter for the list of dependencies
     *
     * @return the dependencies
     */
    public List getDependencies()
    {
        return dependencies;
    }

    /**
     * Setter for the dependencies
     *
     * @param dependencies the dependencies to set
     */
    public void setDependencies( List dependencies )
    {
        this.dependencies = dependencies;
    }

    /**
     * The artifacts managed to the plugin
     *
     * @return the list of plugin artifacts
     */
    public List getPluginArtifacts()
    {
        return pluginArtifacts;
    }

    /**
     * Setter for the plugin artifacts
     *
     * @param pluginArtifacts the artifacts to set
     */
    public void setPluginArtifacts( List pluginArtifacts )
    {
        this.pluginArtifacts = pluginArtifacts;
    }

    /**
     * Getter for the toolsJar value
     *
     * @return the location of toolsJar on disk
     */
    public String getToolsJar() {
        return toolsJar;
    }

    /**
     * the setter for tools jar
     *
     * @param toolsJar the location on disk
     */
    public void setToolsJar(String toolsJar) {
        this.toolsJar = toolsJar;
    }

    /**
     * Meaningful toString
     *
     * @return the string representation of this object
     */
    public String toString()
    {
        return "AbstractWeblogicMojo{" + "weblogicHome='" + weblogicHome + '\'' + ", artifacts=" + artifacts +
            ", dependencies=" + dependencies + ", pluginArtifacts=" + pluginArtifacts + ", outputDirectory='" +
            outputDirectory + '\'' + '}';
    }
}
