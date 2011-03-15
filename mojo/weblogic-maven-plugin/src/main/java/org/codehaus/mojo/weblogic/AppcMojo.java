package org.codehaus.mojo.weblogic;

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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.codehaus.mojo.weblogic.util.WeblogicMojoUtilities;
import org.codehaus.plexus.util.ReflectionUtils;

import weblogic.ant.taskdefs.j2ee.Appc;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Run the weblogic appc compiler against an artifact.
 *
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @version $Id$
 * @description Run APPC compiler on an artifact (war, ear, etc).
 * @goal appc
 * @requiresDependencyResolution compile
 */
public class AppcMojo
    extends AbstractWeblogicMojo
{

    /**
     * The full path to the artifact to be compiled. It can be an EAR, War or Jar. If the project packaging is ejb then
     * the .ejb suffix will be replaced with .jar if needed.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     */
    private String inputArtifactPath;

    /**
     * The project packaging used to check the suffix on the artifact.
     *
     * @parameter expression="${project.packaging}"
     */
    private String projectPackaging;

    /**
     * If this is set to true then verbose output for the process will be generated.
     *
     * @parameter default-value="false"
     */
    private boolean verbose;

    /**
     * If set to true the generated source files will be kept.
     *
     * @parameter default-value="true"
     */
    private boolean keepGenerated;

    /**
     * The full path to the output artifact. By default it is not used and defaults to be the same as the input.
     *
     * @parameter default-value=""
     */
    private String outputArtifactPath;

    /**
     * If set to true the generation of JSP and EJB s will be forced to happen.
     *
     * @parameter default-value="true"
     */
    private boolean forceGeneration;

    /**
     * If set to true then line numbers will be added to classes for debugging.
     *
     * @parameter default-value="false"
     */
    private boolean lineNumbers;

    /**
     * If set to true the basic client jar will be created without descriptors, etc.
     *
     * @parameter default-value="false"
     */
    private boolean basicClientJar;

    /**
     * The full path to create the generated client jar file.
     *
     * @parameter default-value="${project.build.directory}"
     */
    private String clientJarOutputDir;

    /**
     * If set to false the plugin stops after if compilation of a JSP file fails.
     *
     * @parameter default-value="true"
     */
    private boolean continueCompilation;

    /**
     * Getter for property input artifact path.
     *
     * @return The value of input artifact path.
     */
    public String getInputArtifactPath()
    {
        return inputArtifactPath;
    }

    /**
     * Setter for the input artifact path.
     *
     * @param inInputArtifactPath The value of input artifact path.
     */
    public void setInputArtifactPath( final String inInputArtifactPath )
    {
        this.inputArtifactPath = inInputArtifactPath;
    }

    /**
     * This method will perform the appc compilation of artifact.
     * Calls super.execute()
     *
     * @throws MojoExecutionException Thrown if we fail to access the complier or the compilation fails.
     */
    public void execute()
        throws MojoExecutionException
    {
        //be sure to call super so weblogic.home will be set.
        super.execute();

        final String classPath =
            WeblogicMojoUtilities.getDependencies( this.getArtifacts(), this.getPluginArtifacts() );

        // Process the input artifact for proper suffixes
        inputArtifactPath = WeblogicMojoUtilities.updateArtifactName( this.inputArtifactPath, this.projectPackaging );

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic APPC processing beginning for artifact " + this.getInputArtifactPath() );
            getLog().info( " Detailed Appc settings information " + this.toString() );
            getLog().info( "Using Classpath " + classPath );
        }

        try
        {
            System.setProperty( "com.sun.xml.namespace.QName.useCompatibleSerialVersionUID", "1.0" );
            // Create the Ant task, pass in the options and execute it
            Appc appc = new Appc();
            File source = new File( this.inputArtifactPath );
            appc.setSource( source.getAbsolutePath() );

            if ( this.outputArtifactPath != null )
            {
                File destination = new File( this.outputArtifactPath );
                appc.setOutput( destination.getAbsolutePath() );
            }
            appc.setVerbose( this.verbose );
            appc.setKeepGenerated( this.keepGenerated );
            appc.setBasicClientJar( this.basicClientJar );

            // use reflection here to maintain compatibility with previous WebLogic versions
//            appc.setClientJarOutputDir( this.clientJarOutputDir );
			Method method = ReflectionUtils.getSetter("clientJarOutputDir", appc.getClass());
			if (method != null) {
				method.invoke(appc, new Object[] { this.clientJarOutputDir });
			}

			appc.setForceGeneration( this.forceGeneration );
            appc.setLineNumbers( this.lineNumbers );
            appc.setContinueCompilation( this.continueCompilation );

            Project antProject = new Project();
            antProject.setName( "appc" );
            appc.setProject( antProject );
            appc.setClasspath( new Path( antProject, classPath ) );
            appc.execute();
        }
        catch ( Exception ex )
        {
            getLog().error( "Exception encountered during APPC processing ", ex );
            throw new MojoExecutionException( "Exception encountered during APPC processing", ex );
        }
        finally
        {
            WeblogicMojoUtilities.unsetWeblogicProtocolHandler();
        }

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic APPC processing of " + this.getInputArtifactPath() + " successful " );
        }
    }

    /**
     * Getter for property verbose.
     *
     * @return The value of verbose.
     */
    public boolean isVerbose()
    {
        return this.verbose;
    }

    /**
     * Setter for the verbose.
     *
     * @param inVerbose The value of verbose.
     */
    public void setVerbose( final boolean inVerbose )
    {
        this.verbose = inVerbose;
    }

    /**
     * Getter for property basic client jar.
     *
     * @return The value of basic client jar.
     */
    public boolean isBasicClientJar()
    {
        return this.basicClientJar;
    }

    /**
     * Setter for the basic client jar.
     *
     * @param inBasicClientJar The value of basic client jar.
     */
    public void setBasicClientJar( final boolean inBasicClientJar )
    {
        this.basicClientJar = inBasicClientJar;
    }

    /**
     * Setter for the path to place the client jar.
     *
     * @param clientJarOutputDir The path to place the client jar.
     */
    public void setClientJarOutputDir( final String clientJarOutputDir )
    {
        this.clientJarOutputDir = clientJarOutputDir;
    }

    /**
     * Getter for property force generation.
     *
     * @return The value of force generation.
     */
    public boolean isForceGeneration()
    {
        return this.forceGeneration;
    }

    /**
     * Setter for the force generation.
     *
     * @param inForceGeneration The value of force generation.
     */
    public void setForceGeneration( final boolean inForceGeneration )
    {
        this.forceGeneration = inForceGeneration;
    }

    /**
     * Getter for property keep generated.
     *
     * @return The value of keep generated.
     */
    public boolean isKeepGenerated()
    {
        return this.keepGenerated;
    }

    /**
     * Setter for the keep generated.
     *
     * @param inKeepGenerated The value of keep generated.
     */
    public void setKeepGenerated( final boolean inKeepGenerated )
    {
        this.keepGenerated = inKeepGenerated;
    }

    /**
     * Getter for property line numbers.
     *
     * @return The value of line numbers.
     */
    public boolean isLineNumbers()
    {
        return this.lineNumbers;
    }

    /**
     * Setter for the line numbers.
     *
     * @param inLineNumbers The value of line numbers.
     */
    public void setLineNumbers( final boolean inLineNumbers )
    {
        this.lineNumbers = inLineNumbers;
    }

    /**
     * Getter for property output artifact path.
     *
     * @return The value of output artifact path.
     */
    public String getOutputArtifactPath()
    {
        return outputArtifactPath;
    }

    /**
     * Setter for the output artifact path.
     *
     * @param inOutputArtifactPath The value of output artifact path.
     */
    public void setOutputArtifactPath( final String inOutputArtifactPath )
    {
        this.outputArtifactPath = inOutputArtifactPath;
    }

    /**
     * Getter for property project packaging.
     *
     * @return The value of project packaging.
     */
    public String getProjectPackaging()
    {
        return projectPackaging;
    }

    /**
     * Setter for the project packaging.
     *
     * @param inProjectPackaging The value of project packaging.
     */
    public void setProjectPackaging( String inProjectPackaging )
    {
        this.projectPackaging = inProjectPackaging;
    }

    /**
     * This method creates a String representation of this object.
     *
     * @return The String representation of this object.
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "AppcMojo[" );
        buffer.append( "\n basicClientJar = " ).append( basicClientJar );
        buffer.append( "\n clientJarOutputDir = " ).append( clientJarOutputDir );
        buffer.append( "\n forceGeneration = " ).append( forceGeneration );
        buffer.append( "\n keepGenerated = " ).append( keepGenerated );
        buffer.append( "\n lineNumbers = " ).append( lineNumbers );
        buffer.append( "\n inputArtifactPath = " ).append( inputArtifactPath );
        buffer.append( "\n outputArtifactPath = " ).append( outputArtifactPath );
        buffer.append( "\n artifacts = " ).append( getArtifacts() );
        buffer.append( "\n project Packaging = " ).append( projectPackaging );
        buffer.append( "\n verbose = " ).append( verbose );
        buffer.append( "\n continueCompilation = " ).append( continueCompilation );
        buffer.append( "]" );

        return buffer.toString();
    }
}
