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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.codehaus.mojo.weblogic.util.WeblogicMojoUtilities;
import weblogic.ant.taskdefs.webservices.wsdlgen.WSDLGen;

import java.io.File;

/**
 * This class generates wsdl from ear/war package
 *
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 * @description Run wsdlgen on an ear.
 * @goal wsdlgen
 * @requiresDependencyResolution compile
 */
public class WsdlGenMojo
    extends AbstractWeblogicMojo
{
    /**
     * The service name to generate the wsdl for
     *
     * @parameter
     * @required
     */
    private String serviceName;

    /**
     * The war name inside of the ear that contains the services
     *
     * @parameter
     * @required
     */
    private String warName;

    /**
     * The wsdl file to output when complete.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}.wsdl"
     */
    private String wsdlFile;

    /**
     * The default endpoint address
     *
     * @parameter default-value="http://localhost:7001/"
     */
    private String defaultEndpoint;

    /**
     * The input ear file to use with the wsdl gen. If this is null the service
     * will look in the dependencies for an ear file to use
     */
    private String earFileName;

    /**
     * Set to false to not overwrite existing resources.
     *
     * @parameter default-value="true"
     */
    private boolean overwrite;

    /**
     * Execute the wsdlgen step to leave the artifacts around
     */
    public void execute()
        throws MojoExecutionException
    {
        super.execute();

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic wsdl gen beginning " );
        }

        File earFile;
        if ( this.earFileName == null )
        {
            earFile = WeblogicMojoUtilities.getEarFileName( getArtifacts() );
        }
        else
        {
            earFile = new File( this.earFileName );
        }
        final String classPath = WeblogicMojoUtilities
            .getDependencies( getArtifacts() );

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic wsdl gen for ear " + earFile );
        }

        try
        {
            final WSDLGen wsdl = new WSDLGen();
            final Project antProject = new Project();
            antProject.setName( "wsdlgen" );
            wsdl.setProject( antProject );
            wsdl.setClasspath( new Path( antProject, classPath ) );
            wsdl.setEar( earFile );
            wsdl.setWarName( this.warName );
            final File wsdlFileFile = new File( this.wsdlFile );
            wsdl.setWsdlFile( wsdlFileFile );
            wsdl.setServiceName( this.serviceName );
            wsdl.execute();
        }
        catch ( Exception ex )
        {
            getLog().error( "Exception encountered during WSDLGen processing ", ex );
            throw new MojoExecutionException( "Exception encountered during WSDLGen processing", ex );
        }
        finally
        {
            WeblogicMojoUtilities.unsetWeblogicProtocolHandler();
        }

    }

    /**
     * Getter for the service name
     *
     * @return the service name
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * Setter for the service name
     *
     * @param serviceName the service name to set
     */
    public void setServiceName( String serviceName )
    {
        this.serviceName = serviceName;
    }

    public String getWarName()
    {
        return warName;
    }

    public void setWarName( String warName )
    {
        this.warName = warName;
    }

    public String getWsdlFile()
    {
        return wsdlFile;
    }

    public void setWsdlFile( String wsdlFile )
    {
        this.wsdlFile = wsdlFile;
    }

    public String getDefaultEndpoint()
    {
        return defaultEndpoint;
    }

    public void setDefaultEndpoint( String defaultEndpoint )
    {
        this.defaultEndpoint = defaultEndpoint;
    }

    public String getEarFileName()
    {
        return earFileName;
    }

    public void setEarFileName( String earFileName )
    {
        this.earFileName = earFileName;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite( boolean overwrite )
    {
        this.overwrite = overwrite;
    }

    /**
     * toString method: creates a String representation of the object
     *
     * @return the String representation
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "ClientGenMojo[" );
        buffer.append( "earFileName = " ).append( earFileName );
        buffer.append( ", warName = " ).append( warName );
        buffer.append( ", serviceName = " ).append( serviceName );
        buffer.append( ", overwrite = " ).append( overwrite );
        buffer.append( "]" );
        return buffer.toString();
    }
}
