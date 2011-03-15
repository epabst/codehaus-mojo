package org.codehaus.mojo.axistools;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.axistools.axis.AxisPluginException;
import org.codehaus.mojo.axistools.java2wsdl.DefaultJava2WSDLPlugin;

/**
 * A Plugin for generating WSDL files using Axis Java2WSDL.
 *
 * @author jesse <jesse.mcconnell@gmail.com>
 * @version $Id$
 * @goal java2wsdl
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @description Java2WSDL plugin
 */
public class Java2WSDLMojo
    extends AbstractMojo
{
    /**
     * The directory the compile objects will be located for java2wsdl to source from.
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File classesDirectory;

    /**
     * Directory for generated content.
     *
     * Corresponds to the <code>-o, --output</code> option in the Java2WSDL command line tool,
     * together with the {@linkplain #filename} parameter.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/axistools/java2wsdl"
     */
    private File outputDirectory;

    /**
     * Indicates the name of the output WSDL file.
     *
     * Corresponds to the <code>-o, --output</code> option in the Java2WSDL command line tool,
     * together with the {@linkplain #outputDirectory} parameter.
     *
     * @parameter expression="${fileName}"
     * @required
     */
    private String filename;

    /**
     * The class-of-portType.
     *
     * @parameter expression="${classOfPortType}"
     */
    private String classOfPortType;

    /**
     * Optional parameter that indicates the name of the input wsdl file. 
     * The output wsdl file will contain everything from the input wsdl file plus the new constructs. 
     * If a new construct is already present in the input wsdl file, it is not added. 
     * This option is useful for constructing a wsdl file with multiple ports, bindings, or portTypes.
     *
     * Corresponds to the <code>-I, --input</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${input}"
     */
    private String input;

    /**
     * Indicates the url of the location of the service. 
     * The name after the last slash or backslash is the name of the service port 
     * (unless overridden by the servicePortName option). 
     * The service port address location attribute is assigned the specified value.
     *
     * Corresponds to the <code>-l, --location</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${location}"
     */
    private String location;

    /**
     * Indicates the name to use for the portType element. If not specified, the
     * {@linkplain #classOfPortType} name is used.
     *
     * Corresponds to the <code>-P, --portTypeName</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${portTypeName}"
     */
    private String portTypeName;

    /**
     * Indicates the name to use for the binding element. 
     * If not specified, the value of the {@linkplain #servicePortName} + "SoapBinding" is used.
     *
     * Corresponds to the <code>-b, --bindingName</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${bindingName}"
     */
    private String bindingName;

    /**
     * Service element name (defaults to servicePortName value + "Service").
     *
     * Corresponds to the <code>-S, --serviceElementName</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${serviceElementName}"
     */
    private String serviceElementName;

    /**
     * Indicates the name of the service port. 
     * If not specified, the service port name is derived from the location value. 
     *
     * Corresponds to the <code>-s, --servicePortName</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${servicePortName}"
     */
    private String servicePortName;

    /**
     * Indicates the name of the target namespace of the WSDL.
     *
     * Corresponds to the <code>-n, --namespace</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${namespace}"
     */
    private String namespace;

    /**
     * Package=namespace, name value pair.
     * The plugin currently only supports one name value pair.
     *
     * Corresponds to the <code>-p, --PkgtoNS</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${packageToNamespace}"
     */
    private String packageToNamespace;

    /**
     * Methods to export.
     *
     * Corresponds to the <code>-m, --methods</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${methods}"
     */
    private ArrayList methods;

    /**
     * Look for allowed methods in inherited class.
     *
     * Corresponds to the <code>-a, --all</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="false"
     */
    private boolean all;

    /**
     * Indicates the kind of WSDL to generate. Accepted values are:
     * <ul>
     *   <li>All --- (default) Generates wsdl containing both interface and
     *       implementation WSDL constructs.</li>
     *   <li>Interface --- Generates a WSDL containing the interface constructs
     *       (no service element).</li>
     *   <li>Implementation -- Generates a WSDL containing the implementation.
     *       The interface WSDL is imported via the {@linkplain #locationImport}
     *       option.</li>
     * </ul>
     *
     * Corresponds to the <code>-w, --outputWsdlMode</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${outputWSDLMode}"
     */
    private String outputWSDLMode;

    /**
     * Used to indicate the location of the interface WSDL when generating an implementation WSDL.
     *
     * Corresponds to the <code>-L, --locationImport</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${locationImport}"
     */
    private String locationImport;

    /**
     * Namespace of the implementation WSDL.
     *
     * Corresponds to the <code>-N, --namespaceImpl</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${namespaceImpl}"
     */
    private String namespaceImpl;

    /**
     * Use this option to indicate the name of the output implementation WSDL file. 
     * If specified, Java2WSDL will produce interface and implementation WSDL files. 
     * If this option is used, the {@linkplain #outputWSDLMode} option is ignored.
     *
     * Corresponds to the <code>-O, --outputImpl</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${outputImpl}"
     */
    private String outputImpl;

    /**
     * Sometimes extra information is available in the implementation class file. 
     * Use this option to specify the implementation class.
     *
     * Corresponds to the <code>-i, --implClass</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${implClass}"
     */
    private String implClass;

    /**
     * List of methods not to export.
     *
     * Corresponds to the <code>-x, --exclude</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${exclude}"
     */
    private ArrayList excludes;

    /**
     * List of classes which stop the Java2WSDL inheritance search.
     *
     * Corresponds to the <code>-c, --stopClasses</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${stopClasses}"
     */
    private ArrayList stopClasses;

    /**
     * Choose the default type mapping registry to use. Either 1.1 or 1.2.
     *
     * Corresponds to the <code>-T, --typeMappingVersion</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${typeMappingVersion}"
     */
    private String typeMappingVersion;

    /**
     * The value of the operations soapAction field. 
     * Values are DEFAULT, OPERATION or NONE. 
     * OPERATION forces soapAction to the name of the operation. 
     * DEFAULT causes the soapAction to be set according to the operation's meta data (usually ""). 
     * NONE forces the soapAction to "". The default is DEFAULT.
     *
     * Corresponds to the <code>-A, --soapAction</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${soapAction}"
     */
    private String soapAction;

    /**
     * The style of the WSDL document: RPC, DOCUMENT or WRAPPED. 
     * The default is RPC. If RPC is specified, an rpc wsdl is generated. 
     * If DOCUMENT is specified, a document wsdl is generated. 
     * If WRAPPED is specified, a document/literal wsdl is generated using the wrapped approach. 
     * Wrapped style forces the use attribute to be literal.
     *
     * Corresponds to the <code>-y, --style</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${style}"
     */
    private String style;

    /**
     * The use of the WSDL document: LITERAL or ENCODED. 
     * If LITERAL is specified, the XML Schema defines the representation of the XML for the request. 
     * If ENCODED is specified, SOAP encoding is specified in the generated WSDL.
     *
     * Corresponds to the <code>-u, --use</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${use}"
     */
    private String use;

    /**
     * Specify a list of class names which should be included in the types
     * section of the WSDL document.
     * This is useful in the case where your service interface references a base
     * class and you would like your WSDL to contain XML Schema type definitions
     * for these other classes. 
     *
     * Corresponds to the <code>-e, --extraClasses</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${extraClasses}"
     */
    private ArrayList extraClasses;

    /**
     * A file or URL to an XML Schema that should be physically imported into the generated WSDL.
     *
     * Corresponds to the <code>-C, --importSchema</code> option in the Java2WSDL command line tool.
     *
     * @parameter expression="${importSchema}"
     */
    private String importSchema;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        DefaultJava2WSDLPlugin plugin = new DefaultJava2WSDLPlugin();
        
        String classpath = getCompileClasspath();

        plugin.setAll( all );
        plugin.setBindingName( bindingName );
        plugin.setClasspath( classpath );
        plugin.setClassOfPortType( classOfPortType );
        plugin.setExcludes( excludes );
        plugin.setExtraClasses( extraClasses );
        plugin.setFilename( filename );
        plugin.setImplClass( implClass );
        plugin.setImportSchema( importSchema );
        plugin.setInput( input );
        plugin.setLocation( location );
        plugin.setLocationImport( locationImport );
        plugin.setMethods( methods );
        plugin.setNamespace( namespace );
        plugin.setNamespaceImpl( namespaceImpl );
        plugin.setOutputDirectory( outputDirectory );
        plugin.setOutputImpl( outputImpl );
        plugin.setOutputWSDLMode( outputWSDLMode );
        plugin.setPackageToNamespace( packageToNamespace );
        plugin.setPortTypeName( portTypeName );
        plugin.setServiceElementName( serviceElementName );
        plugin.setServicePortName( servicePortName );
        plugin.setSoapAction( soapAction );
        plugin.setStopClasses( stopClasses );
        plugin.setStyle( style );
        plugin.setTypeMappingVersion( typeMappingVersion );
        plugin.setUse( use );
        plugin.setLog( getLog() );
        plugin.setProjectHelper( projectHelper );
        plugin.setProject( project );

        try
        {
            plugin.execute();
        }
        catch ( AxisPluginException e )
        {
            throw new MojoExecutionException( "Error executing creating WSDL from the Java code.", e );
        }
    }
    
    /**
     * Computes the runtime classpath.
     * 
     * @return A representation of the computed runtime classpath.
     * @throws MojoExecutionException in case of dependency resolution failure
     */
    private String getCompileClasspath()
        throws MojoExecutionException
    {
        try
        {
            // get the union of compile- and runtime classpath elements
            Set dependencySet = new LinkedHashSet();
            dependencySet.addAll( project.getCompileClasspathElements() );
            dependencySet.add( classesDirectory.getAbsolutePath() );
            String compileClasspath = StringUtils.join( dependencySet, File.pathSeparator );

            return compileClasspath;
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

}
