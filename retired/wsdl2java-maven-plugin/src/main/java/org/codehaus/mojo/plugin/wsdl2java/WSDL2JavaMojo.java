package org.codehaus.mojo.plugin.wsdl2java;

/*
 * Copyright 2005 The Codehaus.
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

import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A Plugin for generating stubs for WSDL files using Axis WSDL2Java.
 * 
 * @goal generate
 * @phase generate-sources
 * @description WSDL2Java plugin
 * @author jesse <jesse.mcconnell@gmail.com>
 * @version $Id$
 */
public class WSDL2JavaMojo
    extends AbstractMojo
{
    /**
     * list of urls to process
     * 
     * @parameter expression=""
     */
    private List urls;

    /**
     * source directory that contains .wsdl files
     * 
     * @parameter expression="${basedir}/src/main/wsdl"
     */
    private File sourceDirectory;

    /**
     * @parameter expression="${project.build.directory}/generated-sources/wsdl"
     *
     */
    private File outputDirectory;

    /**
     * @parameter expression="${basedir}/target"
     *
     */
    private String timestampDirectory;

    /**
     * @parameter expression="${serverSide}"
     *
     */
    private boolean serverSide;

    /**
     * package to create the java files under
     * 
     * @parameter expression="${packageSpace}"
     * 
     */
    private String packageSpace;

    /**
     * @parameter expression="${verbose}"
     * 
     */
    private boolean verbose;

    /**
     * @parameter expression="${testCases}"
     * 
     */
    private boolean testCases;
    
    /**
     * @parameter expression="false"
     */
    private boolean runTestCasesAsUnitTests;

    /**
     * @parameter expression="${allElements}"
     * 
     */
    private boolean allElements;

    /**
     * load.wsdl would further subpackage into load.*
     * 
     * @parameter expression="$subPackageByFileName"
     */
    private boolean subPackageByFileName;

    /**
     * location to place generated test source
     *
     * @parameter expression="${project.build.directory}/generated-test-sources/wsdl"
     */
    private File testSourceDirectory;

    /**
     * The granularity in milliseconds of the last modification
     *  date for testing whether a source needs recompilation
     *
     * @parameter expression="${lastModGranularityMs}" default-value="0"
     */
    private int staleMillis;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {

        if ( !outputDirectory.exists() )
        {
            outputDirectory.mkdirs();
        }

        getLog().info( "about to add compile source root" );

        if ( project != null )
        {
            project.addCompileSourceRoot( outputDirectory.getAbsolutePath() );
        }

        // process urls if they are present, by their nature they 
        // will be regenerated every time.
        if ( urls != null )
        {

            for ( Iterator i = urls.iterator(); i.hasNext(); )
            {
                String url = (String) i.next();
                getLog().info( "processing wsdl location: " + url );

                try
                {
                    MojoWSDL2Java wsdlMojo = new MojoWSDL2Java();
                    wsdlMojo.execute( generateWSDLArgumentList( url ) );
                }
                catch ( Throwable t )
                {
                    throw new MojoExecutionException( "WSDL2Java execution failed", t );
                }
            }
        }
        else
        {

            Set wsdlSet = computeStaleWSDLs();

            for ( Iterator i = wsdlSet.iterator(); i.hasNext(); )
            {

                File wsdl = (File) i.next();

                getLog().info( "processing wsdl: " + wsdl.toString() );

                try
                {
                    MojoWSDL2Java wsdlMojo = new MojoWSDL2Java();
                    wsdlMojo.execute( generateWSDLArgumentList( wsdl.getAbsolutePath() ) );

                    FileUtils.copyFileToDirectory( wsdl, new File( timestampDirectory ) );
                }
                catch ( Throwable t )
                {
                    throw new MojoExecutionException( "WSDL2Java execution failed", t );
                }

            }
        }

        if (runTestCasesAsUnitTests) {
            migrateTestSource();
        }
    }

    /**
     * move the generated test cases to a suitable location for being picked up by the testing phase
     * 
     */
    private void migrateTestSource()
        throws MojoExecutionException
    {

        if ( !testSourceDirectory.exists() )
        {
            testSourceDirectory.mkdirs();
        }

        Set testSources = locateTestSources();

        for ( Iterator iter = testSources.iterator(); iter.hasNext(); )
        {
            File source = (File) iter.next();

            try
            {
                FileUtils.copyFileToDirectory( source, testSourceDirectory );
                FileUtils.fileDelete( source.getAbsolutePath() );
            }
            catch ( IOException ioe )
            {
                throw new MojoExecutionException( "error copying test sources", ioe );
            }
        }

        project.addTestCompileSourceRoot( testSourceDirectory.getPath() );
    }

    /**
     * generate the parameter String[] to be passed into the main method 
     * 
     * @param wsdl
     * @return
     */
    private String[] generateWSDLArgumentList( String wsdl )
    {

        ArrayList argsList = new ArrayList();
        argsList.add( "-o" );
        argsList.add( outputDirectory.getAbsolutePath() );

        if ( serverSide )
        {
            argsList.add( "-s" );
        }

        if ( verbose )
        {
            argsList.add( "-v" );
        }

        if ( testCases )
        {
            argsList.add( "-t" );
        }

        if ( allElements )
        {
            argsList.add( "-a" );
        }

        if ( packageSpace != null && !subPackageByFileName )
        {
            argsList.add( "-p" );
            argsList.add( packageSpace );
        }
        else if ( packageSpace != null && subPackageByFileName )
        {
            argsList.add( "-p" );
            argsList.add( packageSpace + "." + FileUtils.basename( wsdl, ".wsdl" ) );
        }

        argsList.add( wsdl );

        getLog().debug( "argslist: " + argsList.toString() );

        return (String[]) argsList.toArray( new String[argsList.size()] );
    }

    /**
     * scans for the test cases that might have been generated by the call to wsdl2java
     * 
     * @return Set of test case File objects 
     * @throws MojoExecutionException
     */
    private Set locateTestSources()
        throws MojoExecutionException
    {
        SuffixMapping mapping = new SuffixMapping( "TestCase.java", "TestCase.class" );

        SourceInclusionScanner scanner = new StaleSourceScanner();

        scanner.addSourceMapping( mapping );

        Set testSources = new HashSet();

        try
        {
            testSources.addAll( scanner.getIncludedSources( outputDirectory, testSourceDirectory ) );
        }
        catch ( InclusionScanException e )
        {
            throw new MojoExecutionException( "Error scanning source root: \'" + outputDirectory
                + "\' for stale wsdls to reprocess.", e );
        }

        return testSources;
    }

    private Set computeStaleWSDLs()
        throws MojoExecutionException
    {
        SuffixMapping mapping = new SuffixMapping( ".wsdl", ".wsdl" );

        SourceInclusionScanner scanner = new StaleSourceScanner( staleMillis );

        scanner.addSourceMapping( mapping );

        File outDir = new File( timestampDirectory );

        Set staleSources = new HashSet();

        try
        {
            staleSources.addAll( scanner.getIncludedSources( sourceDirectory, outDir ) );
        }
        catch ( InclusionScanException e )
        {
            throw new MojoExecutionException( "Error scanning source root: \'" + sourceDirectory
                + "\' for stale wsdls to reprocess.", e );
        }

        return staleSources;
    }

}
