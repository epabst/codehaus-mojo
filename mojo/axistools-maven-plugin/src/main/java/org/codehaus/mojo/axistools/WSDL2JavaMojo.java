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

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.axistools.axis.AxisPluginException;
import org.codehaus.mojo.axistools.wsdl2java.DefaultWSDL2JavaPlugin;
import org.codehaus.mojo.axistools.wsdl2java.WSDL2JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A Plugin for generating stubs for WSDL files using Axis WSDL2Java.
 *
 * @author jesse <jesse.mcconnell@gmail.com>
 * @author Christoph Schoenfeld <christophster@gmail.com>
 * @version $Id$
 * @requiresDependencyResolution test
 * @goal wsdl2java
 * @phase generate-sources
 * @description WSDL2Java plugin
 */
public class WSDL2JavaMojo
    extends AbstractMojo
{
    /**
     * List of URLs to process.
     *
     * @parameter expression=""
     */
    private ArrayList urls;

    /**
     * List of WSDL files from {@link #sourceDirectory} to process.
     * The files will be processed in the order they appear in your configuration.
     *
     * @parameter expression=""
     */
    private ArrayList wsdlFiles;

    /**
     * List of source dependencies in the format groupId:artifactId:version:file.
     *
     * @parameter expression=""
     */
    private ArrayList sourceDependencies;

    /**
     * Cache directory for WSDLs from URLs.
     *
     * @parameter default-value="${project.build.directory}/axistools/wsdl2java/urlDownloads"
     */
    private File urlDownloadDirectory;

    /**
     * Cache directory for WSDLs from sourceDependencies.
     *
     * @parameter default-value="${project.build.directory}/axistools/wsdl2java/sourceDependencies"
     */
    private File sourceDependencyDirectory;

    /**
     * Use the Emitter for generating the java files as opposed to the commandline wsdl2java tool.
     *
     * @parameter default-value="false"
     */
    private boolean useEmitter;

    /**
     * Mappings of &lt;namespace&gt; to &lt;targetPackage&gt;.
     *
     * @parameter expression=""
     */
    private ArrayList mappings;

    /**
     * Emit server-side bindings for web service.
     *
     * Corresponds to the <code>-s, --server-side</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${serverSide}"
     */
    private boolean serverSide;

    /**
     * Package to create the java files under, for example <code>com.company.wsdl</code>.
     *
     * Corresponds to the <code>-p, --package</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${packageSpace}"
     */
    private String packageSpace;

    /**
     * See what the tool is generating as it is generating it.
     *
     * Corresponds to the <code>-v, --verbose</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${verbose}"
     */
    private boolean verbose;

    /**
     * Generate the test cases.
     *
     * Corresponds to the <code>-t, --testCase</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${testCases}"
     */
    private boolean testCases;

    /**
     * Copy the generated test cases to a generated-sources test directory to be
     * compiled and run as normal Surefire unit tests.
     *
     * @parameter default-value="false"
     */
    private boolean runTestCasesAsUnitTests;

    /**
     * Generate code for all elements, even unreferenced ones.
     * By default, WSDL2Java only generates code for those elements in the WSDL file that are referenced.
     * A note about what it means to be referenced.
     * We cannot simply say: start with the services, generate all bindings referenced by the service,
     * generate all portTypes referenced by the referenced bindings, etc.
     * What if we're generating code from a WSDL file that only contains portTypes, messages, and types?
     * If WSDL2Java used service as an anchor, and there's no service in the file, then nothing will be generated.
     * So the anchor is the lowest element that exists in the WSDL file in the order:
     * <ol>
     *   <li>types
     *   <li>portTypes
     *   <li>bindings
     *   <li>services
     * </ol>
     * For example, if a WSDL file only contained types, then all the listed
     * types would be generated. But if a WSDL file contained types and a
     * portType, then that portType will be generated and only those types that
     * are referenced by that portType. Note that the anchor is searched for in
     * the WSDL file appearing on the command line, not in imported WSDL files.
     * This allows one WSDL file to import constructs defined in another WSDL
     * file without the nuisance of having all the imported WSDL file's
     * constructs generated.
     *
     * Corresponds to the <code>-a, --all</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${allElements}"
     */
    private boolean allElements;

    /**
     * Print debug information, which currently is WSDL2Java's symbol table.
     * Note that this is only printed after the symbol table is complete, ie., after the WSDL is parsed successfully.
     *
     * Corresponds to the <code>-D, --Debug</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="false"
     */
    private boolean debug;

    /**
     * Timeout in seconds (default is 45, specify -1 to disable).
     *
     * Corresponds to the <code>-O, --timeout</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${timeout}"
     */
    private Integer timeout;

    /**
     * Only generate code for the immediate WSDL document.
     *
     * Corresponds to the <code>-n, --noImports</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="false"
     */
    private boolean noImports;

    /**
     * Turn off support for "wrapped" document/literal.
     *
     * Corresponds to the <code>-W, --noWrapped</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="false"
     */
    private boolean noWrapped;

    /**
     * Prefer generating JavaBean classes like "ArrayOfString" for certain
     * schema array patterns.
     *
     * Corresponds to the <code>-w, --wrapArrays</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="true"
     * NJS 6 July 2006
     */
    private boolean wrapArrays;

    /**
     * Deploy skeleton (true) or implementation (false) in deploy.wsdd.
     *
     * Corresponds to the <code>-S, --skeletonDeploy</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="false"
     */
    private boolean skeletonDeploy;

    /**
     * Mapping of namespace to package.
     * This is only used when <code>useEmitter</code> is set to <code>true</code>.
     * If <code>useEmitter</code> is set to <code>false</code> you should use {@linkplain #mappings} instead.
     *
     * Corresponds to the <code>-N, --NStoPkg</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${namespaceToPackage}"
     */
    private String namespaceToPackage;

    /**
     * File containing namespace to package mappings.
     *
     * Corresponds to the <code>-f, --fileNStoPkg</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${fileNamespaceToPackage}"
     */
    private File fileNamespaceToPackage;

    /**
     * Add scope to deploy.xml: "Application", "Request", "Session".
     *
     * Corresponds to the <code>-d, --deployScope</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${deployScope}"
     */
    private String deployScope;

    /**
     * Indicate either 1.1 or 1.2, where 1.1 means SOAP 1.1 JAX-RPC compliant
     * and 1.2 indicates SOAP 1.1 encoded.
     *
     * Corresponds to the <code>-T, --typeMappingVersion</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${typeMappingVersion}" default-value="1.1"
     */
    private String typeMappingVersion;

    /**
     * Name of a custom class that implements GeneratorFactory interface
     * (for extending Java generation functions).
     *
     * Corresponds to the <code>-F, --factory</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${factory}"
     */
    private String factory;

    /**
     * Namescape to specifically include in the generated code (defaults to
     * all namespaces unless specifically excluded with the {@linkplain #nsExcludes} option).
     *
     * Corresponds to the <code>-i, --nsInclude</code> option in the WSDL2Java command line tool.
     *
     * @parameter
     */
    private ArrayList nsIncludes;

    /**
     * Namespace to specifically exclude from the generated code (defaults to
     * none excluded until first namespace included with {@linkplain #nsIncludes} option).
     *
     * Corresponds to the <code>-x, --nsExclude</code> option in the WSDL2Java command line tool.
     *
     * @parameter
     */
    private ArrayList nsExcludes;

    /**
     * Emits separate Helper classes for meta data.
     *
     * Corresponds to the <code>-H, --helperGen</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="false"
     */
    private boolean helperGen;

    /**
     * Username to access the WSDL-URI.
     *
     * Corresponds to the <code>-U, --user</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${username}"
     */
    private String username;

    /**
     * Password to access the WSDL-URI.
     *
     * Corresponds to the <code>-P, --password</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${password}"
     */
    private String password;

    /**
     * Use this as the implementation class.
     *
     * Corresponds to the <code>-c, --implementationClassName</code> option in the WSDL2Java command line tool.
     *
     * @parameter expression="${implementationClassName}"
     */
    private String implementationClassName;

    /**
     * load.wsdl would further subpackage into load.*
     *
     * @parameter expression="${subPackageByFileName}"
     */
    private boolean subPackageByFileName;

    /**
     * Location to place generated test source files.
     *
     * @parameter default-value="${project.build.directory}/generated-test-sources/wsdl"
     */
    private File testSourceDirectory;

    /**
     * Source directory that contains .wsdl files.
     *
     * @parameter default-value="${basedir}/src/main/wsdl"
     */
    private File sourceDirectory;

    /**
     * Location to place generated java source files.
     *
     * Corresponds to the <code>-o, --output</code> option in the WSDL2Java command line tool.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/axistools/wsdl2java"
     * @required
     */
    private File outputDirectory;

    /**
     * Directory used when evaluating whether files are up to date or stale.
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File timestampDirectory;

    /**
     * The granularity in milliseconds of the last modification
     * date for testing whether a source needs recompilation.
     *
     * @parameter expression="${lastModGranularityMs}" default-value="0"
     */
    private int staleMillis;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    private List pluginArtifacts;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        WSDL2JavaPlugin plugin = new DefaultWSDL2JavaPlugin();

        plugin.setAllElements( allElements );
        plugin.setDebug( debug );
        plugin.setDeployScope( deployScope );
        plugin.setFactory( factory );
        plugin.setFileNamespaceToPackage( fileNamespaceToPackage );
        plugin.setHelperGen( helperGen );
        plugin.setImplementationClassName( implementationClassName );
        plugin.setMappings( mappings );
        plugin.setNamespaceToPackage( namespaceToPackage );
        plugin.setNoImports( noImports );
        plugin.setNoWrapped( noWrapped );
        plugin.setWrapArrays( wrapArrays );
        plugin.setNsExcludes( nsExcludes );
        plugin.setNsIncludes( nsIncludes );
        plugin.setPackageSpace( packageSpace );
        plugin.setPassword( password );
        plugin.setRunTestCasesAsUnitTests( runTestCasesAsUnitTests );
        plugin.setServerSide( serverSide );
        plugin.setSkeletonDeploy( skeletonDeploy );
        plugin.setSourceDependencies( sourceDependencies );
        plugin.setSourceDependencyDirectory( sourceDependencyDirectory );
        plugin.setStaleMillis( staleMillis );
        plugin.setSubPackageByFileName( subPackageByFileName );
        plugin.setTestCases( testCases );
        plugin.setTestSourceDirectory( testSourceDirectory );
        plugin.setTimeout( timeout );
        plugin.setTypeMappingVersion( typeMappingVersion );
        plugin.setUrlDownloadDirectory( urlDownloadDirectory );
        plugin.setUrls( urls );
        plugin.setWsdlFiles( wsdlFiles );
        plugin.setUseEmitter( useEmitter );
        plugin.setUsername( username );
        plugin.setVerbose( verbose );
        plugin.setProject( project );
        plugin.setOutputDirectory( outputDirectory );
        plugin.setSourceDirectory( sourceDirectory );
        plugin.setTimestampDirectory( timestampDirectory );
        plugin.setLocalRepository( localRepository );
        plugin.setArtifactFactory( artifactFactory );
        plugin.setPluginArtifacts( pluginArtifacts );
        plugin.setLog( getLog() );

        try
        {
            plugin.execute();
        }
        catch ( AxisPluginException e )
        {
            throw new MojoExecutionException( "Error generating Java code from WSDL.", e );
        }
    }
}
