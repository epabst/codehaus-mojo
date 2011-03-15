package org.codehaus.mojo.hibernate3;

/*
 * Copyright 2005 Johann Reyes.
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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.hibernate3.configuration.ComponentConfiguration;
import org.codehaus.mojo.hibernate3.exporter.Component;
import org.hibernate.tool.hbm2x.Exporter;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;

/**
 * Base class for the different hibernate3 goals based on the Ant tasks of hibernate tools.
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @author <a href="mailto:tobrien@codehaus.org">Tim O'Brien</a>
 * @version $Id$
 * @requiresDependencyResolution test
 */
public abstract class HibernateExporterMojo
    extends AbstractMojo
    implements ExporterMojo
{
// ------------------------------ FIELDS ------------------------------

    /**
     * Parameter that holds components definitions specified by the user.
     *
     * @parameter
     * @noinspection MismatchedQueryAndUpdateOfCollection
     */
    private List components = new ArrayList();

    /**
     * Map holding the default component values for this goal.
     */
    private Map defaultComponents = new HashMap();

    /**
     * Parameter that holds component properties defined by the user. More information can be found at the
     * <a href="componentproperties.html">Component Properties Configuration</a> page.
     *
     * @parameter
     * @noinspection MismatchedQueryAndUpdateOfCollection
     */
    private Map componentProperties = new HashMap();

    /**
     * Spefic components configuration. More information can be found at the
     * <a href="components.html">Components Configuration</a> page.
     *
     * @component role="org.codehaus.mojo.hibernate3.configuration.ComponentConfiguration"
     * @noinspection MismatchedQueryAndUpdateOfCollection
     */
    private List componentConfigurations = new ArrayList();

    /**
     * <i>Maven Internal</i>: Project to interact with.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private MavenProject project;

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * @see ExporterMojo#getProject()
     */
    public MavenProject getProject()
    {
        return project;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ExporterMojo ---------------------


    /**
     * @see ExporterMojo#getComponentProperty(String)
     */
    public String getComponentProperty( String key )
    {
        return getComponentProperty( key, null );
    }

    /**
     * @see ExporterMojo#getComponentProperty(String,boolean)
     */
    public boolean getComponentProperty( String key, boolean defaultValue )
    {
        String s = getComponentProperty( key );
        if ( s == null )
        {
            return defaultValue;
        }
        else
        {
            //noinspection UnnecessaryUnboxing
            return Boolean.valueOf( s ).booleanValue();
        }
    }

// --------------------- Interface Mojo ---------------------

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Thread currentThread = Thread.currentThread();
        ClassLoader oldClassLoader = currentThread.getContextClassLoader();

        try
        {
            currentThread.setContextClassLoader( getClassLoader() );
            if ( getComponentProperty( "skip", false ) )
            {
                getLog().info( "skipping hibernate3 execution" );
            }
            else
            {
                doExecute();
            }
        }
        finally
        {
            currentThread.setContextClassLoader( oldClassLoader );
        }
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Adds a default goal.
     *
     * @param outputDirectory Default output directory
     * @param implementation  Default configuration implementation
     * @param jdk5            Is this goal being setup for jdk15?
     * @noinspection unchecked
     */
    protected void addDefaultComponent( String outputDirectory, String implementation, boolean jdk5 )
    {
        Component component = new Component();
        component.setName( getName() );
        component.setOutputDirectory( outputDirectory );
        component.setImplementation( implementation );
        defaultComponents.put( ( jdk5 ) ? "jdk15" : "jdk14", component );
    }

    /**
     * Configures the Exporter.
     *
     * @param exporter Exporter to configure
     * @return Exporter
     * @throws MojoExecutionException if there is an error configuring the exporter
     * @noinspection unchecked
     */
    protected Exporter configureExporter( Exporter exporter )
        throws MojoExecutionException
    {
        String implementation = getComponentProperty( "implementation", getComponent().getImplementation() );

        ComponentConfiguration componentConfiguration = getComponentConfiguration( implementation );
        getLog().info( "using " + componentConfiguration.getName() + " task." );

        Properties properties = new Properties();
        properties.putAll( componentProperties );

        exporter.setProperties( properties );
        exporter.setConfiguration( componentConfiguration.getConfiguration( this ) );
        exporter.setOutputDirectory( new File( getProject().getBasedir(), getComponent().getOutputDirectory() ) );

        File outputDir = new File( getProject().getBasedir(), getComponent().getOutputDirectory() );
        if ( getComponent().isCompileSourceRoot() )
        {
            // add output directory to compile roots
            getProject().addCompileSourceRoot( outputDir.getPath() );
        }

        // now let's set the template path for custom templates if the directory exists
        // template path would need to be found inside the project directory
        String templatePath = getComponentProperty( "templatepath", "/src/main/config/templates" );
        File templatePathDir = new File( getProject().getBasedir(), templatePath );
        if ( templatePathDir.exists() && templatePathDir.isDirectory() )
        {
            getLog().info( "Exporter will use templatepath : " + templatePathDir.getPath() );
            exporter.setTemplatePath( new String[]{templatePathDir.getPath()} );
        }

        return exporter;
    }

    /**
     * Returns the ComponentConfiguration for this maven goal.
     *
     * @param name Configuration task name
     * @return ComponentConfiguration
     * @throws MojoExecutionException if there is an error finding the ConfigurationTask
     * @noinspection ForLoopReplaceableByForEach
     */
    protected ComponentConfiguration getComponentConfiguration( String name )
        throws MojoExecutionException
    {
        for ( Iterator it = componentConfigurations.iterator(); it.hasNext(); )
        {
            ComponentConfiguration componentConfiguration = (ComponentConfiguration) it.next();
            if ( componentConfiguration.getName().equals( name ) )
            {
                return componentConfiguration;
            }
        }
        throw new MojoExecutionException( "Could not get ConfigurationTask." );
    }

    /**
     * @see ExporterMojo#getComponentProperty(String,String)
     */
    public String getComponentProperty( String key, String defaultValue )
    {
        String value = (String) componentProperties.get( key );
        if ( value == null || "".equals( value.trim() ) )
        {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the hibernate tool exporter based on the goal that is being called.
     *
     * @return Goal exporter
     */
    protected abstract Exporter createExporter();

    /**
     * Executes the plugin in an isolated classloader.
     *
     * @throws MojoExecutionException When there is an erro executing the plugin
     */
    protected void doExecute()
        throws MojoExecutionException
    {
        configureExporter( createExporter() ).start();
    }

    /**
     * Returns the an isolated classloader.
     *
     * @return ClassLoader
     * @noinspection unchecked
     */
    private ClassLoader getClassLoader()
    {
        try
        {
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add( project.getBuild().getOutputDirectory() );
            classpathElements.add( project.getBuild().getTestOutputDirectory() );
            URL urls[] = new URL[classpathElements.size()];
            for ( int i = 0; i < classpathElements.size(); ++i )
            {
                urls[i] = new File( (String) classpathElements.get( i ) ).toURL();
            }
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        }
        catch ( Exception e )
        {
            getLog().debug( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }

    /**
     * Returns the parsed goal to the exporter.
     *
     * @return Component
     * @noinspection ForLoopReplaceableByForEach
     */
    protected Component getComponent()
    {
        Component defaultGoal = (Component) defaultComponents.get( HibernateUtils.getJavaVersion() );
        if ( !components.isEmpty() )
        {
            // add an alias to the report goal
            String name = getName();
            if ( "report".equals( name ) )
            {
                name = "hbm2doc";
            }

            // now iterate throught the goals
            for ( Iterator it = components.iterator(); it.hasNext(); )
            {
                Component component = (Component) it.next();
                if ( name.equals( component.getName() ) )
                {
                    if ( component.getImplementation() == null )
                    {
                        component.setImplementation( defaultGoal.getImplementation() );
                    }
                    if ( component.getOutputDirectory() == null )
                    {
                        component.setOutputDirectory( defaultGoal.getOutputDirectory() );
                    }
                    return component;
                }
            }
        }
        return defaultGoal;
    }
}