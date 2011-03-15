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
import org.codehaus.mojo.weblogic.util.WeblogicMojoUtilities;
import weblogic.webservice.tools.clientgen.ClientGen;

import java.io.File;

/**
 * Runs Client Gen on a given WSDL.
 *
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 * @description This mojo will run client gen on a given WSDL.
 * @goal clientgen
 * @requiresDependencyResolution compile
 */
public class ClientGenMojo
    extends AbstractWeblogicMojo
{

    /**
     * The wsdl to client gen from.
     *
     * @parameter default-value="http://localhost:7001"
     */
    private String inputWSDL;

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
     * The name of the service.
     *
     * @parameter default-value="test"
     */
    private String serviceName;

    /**
     * The ear dependency flag. Use this in concert with the {@link #warName} parameter
     * to locate a wsdl inside of the webservice war file.
     *
     * @parameter default-value="false"
     */
    private boolean useEarDependency;

    /**
     * The war name inside of the ear within which to find the wsdl file name.
     *
     * @parameter expression="${pom.artifactId}-${pom.version}.war"
     */
    private String warName;

    /**
     * Set to true if the client gen should copy .class files from the classpath into the
     * target jar file.
     *
     * @parameter default-value="false"
     */
    private boolean useServerTypes;

    /**
     * This method will run client gen on the given WSDL.
     *
     * @throws MojoExecutionException Thrown if we fail to obtain the WSDL.
     */
    public void execute()
        throws MojoExecutionException
    {

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic client gen beginning " );
        }
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Plugin dependencies: " + this.getPluginArtifacts() );
        }
        try
        {
            final ClientGen clientGen = new ClientGen();
            if ( this.useEarDependency )
            {
                clientGen.setEar( WeblogicMojoUtilities
                    .getEarFileName( this.getArtifacts() ) );
                clientGen.setWarName( this.warName );
                if ( getLog().isInfoEnabled() )
                {
                    getLog().info( "Weblogic client gen using ear " + WeblogicMojoUtilities
                        .getEarFileName( this.getArtifacts() ) + " with warName " + this.warName );
                }
            }
            else
            {
                clientGen.setWSDL( this.inputWSDL );
            }
            clientGen.setClientJar( new File( this.outputDir ) );
            clientGen.setClientPackageName( this.packageName );
            clientGen.setServiceName( this.serviceName );
            clientGen.setUseServerTypes( this.useServerTypes );
            // Set the classpath
            clientGen.setClasspath(
                WeblogicMojoUtilities.getDependencies( this.getArtifacts(), this.getPluginArtifacts() ) );
            clientGen.generateClientJar();
        }
        catch ( Exception ex )
        {
            getLog().error( "Exception encountered during client gen ", ex );
            throw new MojoExecutionException( "Exception encountered during listapps", ex );
        }
        finally
        {
            WeblogicMojoUtilities.unsetWeblogicProtocolHandler();
        }

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic client gen successful " );
        }
    }

    /**
     * Getter for property input WSDL.
     *
     * @return The value of input WSDL.
     */
    public String getInputWSDL()
    {
        return this.inputWSDL;
    }

    /**
     * Setter for the input WSDL.
     *
     * @param inInputWSDL The value of input WSDL.
     */
    public void setInputWSDL( final String inInputWSDL )
    {
        this.inputWSDL = inInputWSDL;
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
     * Getter for property service name.
     *
     * @return The value of service name.
     */
    public String getServiceName()
    {
        return this.serviceName;
    }

    /**
     * Setter for the service name.
     *
     * @param inServiceName The value of service name.
     */
    public void setServiceName( final String inServiceName )
    {
        this.serviceName = inServiceName;
    }

    /**
     * Getter for the ear dependency flag
     *
     * @return true if the mojo should look in the ear for the wsdl
     */
    public boolean isUseEarDependency()
    {
        return useEarDependency;
    }

    /**
     * Setter for the ear dependency.
     *
     * @param useEarDependency true if the mojo should look in the ear for the wsdl
     */
    public void setUseEarDependency( boolean useEarDependency )
    {
        this.useEarDependency = useEarDependency;
    }

    /**
     * Getter for the name of the war to look inside for the wsdl file
     *
     * @return the war name inside of the ear
     */
    public String getWarName()
    {
        return warName;
    }

    /**
     * The setter for the war name. Use this in conjuction with the {@link #useEarDependency} flag.
     *
     * @param warName the war name to look inside
     */
    public void setWarName( String warName )
    {
        this.warName = warName;
    }

    /**
     * Getter for the {@link #useServerTypes} parameter. Returns true if the client gen
     * should copy .class files from the class path instead of creating .java and compiling them.
     *
     * @return true if server types should be copied
     */
    public boolean isUseServerTypes()
    {
        return useServerTypes;
    }

    /**
     * Setter for the {@link #useServerTypes} parameter.
     *
     * @param useServerTypes true if the server types should be used
     */
    public void setUseServerTypes( boolean useServerTypes )
    {
        this.useServerTypes = useServerTypes;
    }

    /**
     * toString method: creates a String representation of the object
     *
     * @return the String representation
     */
    public String toString()
    {
        return "ClientGenMojo{" + "inputWSDL='" + inputWSDL + '\'' + ", outputDir='" + outputDir + '\'' +
            ", packageName='" + packageName + '\'' + ", serviceName='" + serviceName + '\'' + ", useEarDependency=" +
            useEarDependency + ", warName='" + warName + '\'' + ", useServerTypes=" + useServerTypes + '}';
    }
}
