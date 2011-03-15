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
import weblogic.ant.taskdefs.webservices.servicegen.ServiceGenTask;

import java.io.File;

/**
 * Runs Service Gen on a given WSDL.
 *
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 * @description This mojo will run service gen on an ear file.
 * @goal servicegen
 * @requiresDependencyResolution compile
 */
public class ServiceGenMojo
    extends AbstractWeblogicMojo
{

    /**
     * The full path to the artifact to be compiled. It can be an EAR, War or
     * Jar. If the project packaging is ejb then the .ejb suffix will be
     * replaced with .jar if needed.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     */
    private String inputArtifactPath;

    /**
     * The directory to output the geneated code to.
     *
     * @parameter default-value="${basedir}/src/main/java"
     */
    private String outputDir;

    /**
     * The package name of the output code.
     *
     * @parameter default-value="com.test.webservice"
     */
    private String packageName;

    /**
     * The service configurations to generate
     *
     * @parameter
     */
    private Service services[];

    /**
     * The context uri for the service
     *
     * @parameter
     * @required
     */
    private String contextUri;

    /**
     * this is the name of the war to execute the service gen against.
     *
     * @parameter
     * @required
     */
    private String warName;

    /**
     * This method will run client gen on the given WSDL.
     *
     * @throws MojoExecutionException Thrown if we fail to obtain the WSDL.
     */
    public void execute()
        throws MojoExecutionException
    {
        super.execute();
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic service gen beginning " );
        }
        inputArtifactPath = WeblogicMojoUtilities.updateArtifactName( inputArtifactPath, "ear" );

        try
        {
            final Project project = new Project();
            project.setName( "servicegen" );
            final Path path = new Path( project, WeblogicMojoUtilities
                .getDependencies( this.getArtifacts() ) );
            final ServiceGenTask servicegen = new ServiceGenTask();
            addToolsJar( ClassLoader.getSystemClassLoader() );
            servicegen.setProject( project );
            if ( getLog().isInfoEnabled() )
            {
                getLog().info( "Weblogic service gen using classpath: " + path );
            }
            servicegen.setClasspath( path );
            servicegen.setWarName( this.warName );
            servicegen.setContextURI( this.contextUri );
            final File source = new File( this.inputArtifactPath );
            servicegen.setDestear( source );
            servicegen.setKeepGenerated( true );

            // setup the services for generation

            for ( int i = 0; i < services.length; i++ )
            {
                if ( getLog().isInfoEnabled() )
                {
                    getLog().info( "Configuring service " + services[i].getServiceName() );
                }
                weblogic.ant.taskdefs.webservices.servicegen.Service service = servicegen
                    .createService();
                service.setIncludeEjbs( services[i].getIncludeEJBs() );
                service.setServiceName( services[i].getServiceName() );
                service.setServiceURI( services[i].getServiceUri() );
                service.setTargetNamespace( services[i].getTargetNamespace() );
                service.setGenerateTypes( services[i].isGenerateTypes() );
                service.setExpandMethods( services[i].isExpandMethods() );
                service.setEjbJar( WeblogicMojoUtilities
                    .getEjbJarFileName( getArtifacts() ) );

            }
            servicegen.execute();
        }
        catch ( Exception ex )
        {
            getLog().error( "Exception encountered during service gen ", ex );
            throw new MojoExecutionException( "Exception encountered during listapps", ex );
        }
        finally
        {
            WeblogicMojoUtilities.unsetWeblogicProtocolHandler();
        }

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic service gen successful " );
        }
    }

    /**
     * Getter for property output dir.
     *
     * @return The value of output dir.
     */
    public String getOutputDir()
    {
        return this.outputDir;
    }

    /**
     * Setter for the output dir.
     *
     * @param inOutputDir The value of output dir.
     */
    public void setOutputDir( final String inOutputDir )
    {
        this.outputDir = inOutputDir;
    }

    /**
     * Getter for property package name.
     *
     * @return The value of package name.
     */
    public String getPackageName()
    {
        return this.packageName;
    }

    /**
     * Setter for the package name.
     *
     * @param inPackageName The value of package name.
     */
    public void setPackageName( String inPackageName )
    {
        this.packageName = inPackageName;
    }

    /**
     * Getter for the services
     *
     * @return the list of services
     */
    public Service[] getServices()
    {
        return services;
    }

    /**
     * Setter for the services
     *
     * @param services the services
     */
    public void setServices( Service[] services )
    {
        this.services = services;
    }

    /**
     * toString method: creates a String representation of the object
     *
     * @return the String representation
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "ServiceGenMojo[" );
        buffer.append( "warName = " ).append( warName );
        buffer.append( ", outputDir = " ).append( outputDir );
        buffer.append( ", packageName = " ).append( packageName );
        buffer.append( "]" );
        return buffer.toString();
    }

    /**
     * Getter for the war name.
     *
     * @return the war name in side the ear that contains the services
     */
    public String getWarName()
    {
        return warName;
    }

    /**
     * Getter for the input artifact path
     *
     * @return the inputArtifactPath
     */
    public String getInputArtifactPath()
    {
        return inputArtifactPath;
    }

    /**
     * Setter for the input artifact path
     *
     * @param inputArtifactPath the input artifact path
     */
    public void setInputArtifactPath( String inputArtifactPath )
    {
        this.inputArtifactPath = inputArtifactPath;
    }

    /**
     * Setter for the war name.
     *
     * @param warName the war name
     */
    public void setWarName( String warName )
    {
        this.warName = warName;
    }

    /**
     * Getter for the context uri
     *
     * @return the contextUri
     */
    public String getContextUri()
    {
        return contextUri;
    }

    /**
     * Setter for the context uri
     *
     * @param contextUri the contextUri
     */
    public void setContextUri( String contextUri )
    {
        this.contextUri = contextUri;
    }
}
