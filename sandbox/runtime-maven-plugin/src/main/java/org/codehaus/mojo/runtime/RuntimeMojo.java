package org.codehaus.mojo.runtime;

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.runtime.builder.MavenRuntimeBuilder;
import org.codehaus.mojo.runtime.builder.MavenRuntimeFactory;
import org.codehaus.mojo.runtime.model.Runtime;
import org.codehaus.mojo.runtime.model.io.xpp3.RuntimeXpp3Reader;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * runtime generation plugin
 *
 * @author jesse <jesse.mcconnell@gmail.com>
 * @version $Id$
 * @goal deploy-runtime
 * @requiresDependencyResolution
 * @description runtime generation plugin
 */
public class RuntimeMojo
    extends AbstractMojo
{

    /**
     * @parameter expression="${staleMillis}" default-value="0"
     */
    private int staleMillis;

    /**
     * @parameter expression="${basedir}/src/main/runtimes"
     * @required
     */
    private String sourceDirectory;

    /**
     * @parameter expression="${project.build.directory}/runtimes"
     * @required
     */
    private String outputDirectory;

    /**
     * @parameter expression="${project.build.directory}/runtimes-timestamps"
     * @required
     */
    private String timeStampDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     *
     */
    public void execute()
        throws MojoExecutionException
    {

        try
        {

            if ( !FileUtils.fileExists( outputDirectory ) )
            {
                FileUtils.mkdir( outputDirectory );
            }

            if ( !FileUtils.fileExists( timeStampDirectory ) )
            {
                FileUtils.mkdir( timeStampDirectory );
            }

            Set staleRuntimes = computeStaleRuntimes();

            if ( staleRuntimes.isEmpty() )
            {
                getLog().info( "Nothing to process - all runtimes are up to date" );
                return;
            }

            for ( Iterator i = staleRuntimes.iterator(); i.hasNext(); )
            {
                File runtimeFile = (File) i.next();
                try
                {
                    //do the work here
                    Runtime runtime = readRuntimeDescriptor( runtimeFile );

                    if ( runtime != null )
                    {

                        MavenRuntimeBuilder builder = MavenRuntimeFactory.getRuntimeBuilder( runtime );
                        getLog().debug( "processing: " + runtimeFile );
                        builder.build( runtime, project, runtimeFile, outputDirectory );

                    }
                    else
                    {
                        getLog().debug( "null runtime object" );
                    }

                    FileUtils.copyFileToDirectory( runtimeFile, new File( timeStampDirectory ) );
                }
                catch ( Exception e )
                {
                    throw new MojoExecutionException( "runtime execution failed", e );
                }
            }

        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "runtime generation failed", e );
        }
    }

    private Runtime readRuntimeDescriptor( File settingsFile )
        throws IOException, XmlPullParserException
    {
        Runtime settings = null;

        if ( settingsFile.exists() && settingsFile.isFile() )
        {
            FileReader reader = null;
            try
            {
                reader = new FileReader( settingsFile );

                RuntimeXpp3Reader modelReader = new RuntimeXpp3Reader();

                settings = modelReader.read( reader );
            }
            finally
            {
                IOUtil.close( reader );
            }
        }

        return settings;
    }


    private Set computeStaleRuntimes()
        throws MojoExecutionException
    {
        SuffixMapping mapping = new SuffixMapping( ".runtime", ".runtime" );
        SuffixMapping mappingCAP = new SuffixMapping( ".runtime", ".runtime" );

        SourceInclusionScanner scanner = new StaleSourceScanner( staleMillis );

        scanner.addSourceMapping( mapping );
        scanner.addSourceMapping( mappingCAP );

        File outDir = new File( timeStampDirectory );

        Set staleSources = new HashSet();

        File sourceDir = new File( sourceDirectory );

        try
        {
            staleSources.addAll( scanner.getIncludedSources( sourceDir, outDir ) );
        }
        catch ( InclusionScanException e )
        {
            throw new MojoExecutionException(
                "Error scanning source root: \'" + sourceDir + "\' for stale grammars to reprocess.", e );
        }

        return staleSources;
    }

}
