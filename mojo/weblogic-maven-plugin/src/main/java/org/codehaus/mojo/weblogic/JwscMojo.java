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
import org.apache.tools.ant.types.PatternSet;
import org.codehaus.mojo.weblogic.util.WeblogicMojoUtilities;
import weblogic.wsee.tools.anttasks.JwsFileSet;
import weblogic.wsee.tools.anttasks.JwsModule.Descriptor;
import weblogic.wsee.tools.anttasks.JwscTask;
import weblogic.wsee.tools.anttasks.MultipleJwsModule;

import java.io.File;
import java.util.Iterator;

/**
 * Runs the JWSC compiler task for web service enabled code.
 *
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 * @description This mojo will run the JSWC ant task
 * @goal jwsc
 * @requiresDependencyResolution compile
 * @since weblogic-maven-plugin-2.9.1-SNAPSHOT
 */
public class JwscMojo
    extends AbstractWeblogicMojo
{

    /**
     * The directory to output the generated code to.
     *
     * @parameter default-value="${project.build.directory}"
     */
    private String outputDir;

    /**
     * The directory to find the jwsc enabled files.
     *
     * @parameter default-value="${basedir}/src/main/java"
     */
    private String inputDir;

    /**
     * The name of the war to use when evaluating the ear file.
     *
     * @required
     * @parameter default-value="${project.artifactId}-${project.version}"
     */
    private String outputName;

    /**
     * The flag to set when desiring verbose output
     *
     * @parameter default-value="false"
     */
    private boolean verbose;

    /**
     * The flag to set when debugging the process
     *
     * @parameter default-value="false"
     */
    private boolean debug;

    /**
     * The flag to set when wanting exploded output. Defaults to true.
     *
     * @parameter default-value="true"
     */
    private boolean explode;

    /**
     * The flag to set when requiring optimization. Defaults to true.
     *
     * @parameter default-value="true"
     */
    private boolean optimize;

    /**
     * The deployed context of the web service.
     *
     * @parameter
     * @required
     */
    private String contextPath;

    /**
     * The web.xml descriptor to use if a new one should not be generated. The
     * path should be fully qualified.
     * <p>If there is more than one descriptor, use a
     * comma ',' separated list.</p>
     *
     * @parameter
     */
    private String descriptor;

    /**
     * The explicit includes list for the file set
     *
     * @parameter default-value="**\/*.java" expression="${weblogic.jwsc.includes}"
     */
    private String sourceIncludes;

    /**
     * The explicit includes list for the file set
     *
     * @parameter expression="${weblogic.jwsc.excludes}"
     */
    private String sourceExcludes;

    /**
     * The keep generated flag for the mojo
     *
     * @parameter default-value="false" expression="${weblogic.jwsc.keepGenerated}"
     */
    private boolean keepGenerated;

    /**
     * This method will run the jswc on the target files
     *
     * @throws MojoExecutionException Thrown if we fail to obtain the WSDL.
     */
    public void execute()
        throws MojoExecutionException
    {
        super.execute();
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Weblogic jwsc beginning for output " + this.outputName );
        }
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "inputDir=" + this.inputDir + "  contextPath=" + this.contextPath );
        }
        if ( this.contextPath == null )
        {
            getLog().warn( "Context path is null. It will be required if " + "more than one web service is present." );
        }
        try
        {
            Iterator iter = getDependencies().iterator();
            while ( iter.hasNext() )
            {
                getLog().debug( iter.next().toString() );
            }
            final JwscTask task = new JwscTask();
            addToolsJar( ClassLoader.getSystemClassLoader() );
            final Project project = new Project();
            project.addBuildListener( getDefaultLogger() );
            project.setName( "jwsc" );
            final Path path = new Path( project, WeblogicMojoUtilities
                .getDependencies( this.getArtifacts(), this.getPluginArtifacts() ) );
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Path=" + path.toString() );
            }
            task.setProject( project );
            task.setTaskName( project.getName() );
            task.setNowarn( false );
            // Set the class path
            task.setClasspath( path );
            task.setDestdir( new File( this.outputDir ) );
            task.setVerbose( this.verbose );
            task.setOptimize( this.optimize );
            task.setDebug( this.debug );
            task.setSrcdir( new File( this.inputDir ) );
            task.setKeepGenerated( this.keepGenerated );
            final MultipleJwsModule module = task.createModule();
            final JwsFileSet jwsFileSet = module.createJwsFileSet();
            jwsFileSet.setProject( project );
            jwsFileSet.setSrcdir( new Path( project, this.inputDir ) );
            PatternSet ps = null;
            if ( this.sourceIncludes != null && this.sourceIncludes.length() > 0 ) {
                if ( getLog().isDebugEnabled() ) {
                    getLog().debug("Using source includes " + this.sourceIncludes );
                }
                ps = jwsFileSet.createPatternSet();
                ps.setIncludes( this.sourceIncludes );
            }
            if ( this.sourceExcludes != null && this.sourceExcludes.length() > 0 ) {
                if ( getLog().isDebugEnabled() ) {
                    getLog().debug("Using source excludes " + this.sourceExcludes );
                }
                if ( ps == null ) {
                    ps = jwsFileSet.createPatternSet();
                }
                ps.setExcludes( this.sourceExcludes );
            }
            if ( getLog().isInfoEnabled() )
            {
                getLog().info( "fileset=" + jwsFileSet.getSrcdir().toString() );
            }
            if ( this.descriptor != null )
            {
                final String[] descriptors = this.descriptor.split( "," );
                for ( int i=0;i<descriptors.length;i++ )  {
                    final File file = new File( descriptors[i] );
                    if ( file.exists() ) {
                        final Descriptor d = module.createDescriptor();
                        d.setFile( file );
                    } else {
                        getLog().warn( "Descriptor file " + file + " does not exist. Ignoring this file." );
                    }
                }
            }
            module.setName( this.outputName );
            module.setExplode( this.explode );
            module.setContextPath( this.contextPath );
            task.execute();
        }
        catch ( Exception ex )
        {
            getLog().error( "Exception encountered during jwsc", ex );
            throw new MojoExecutionException( "Exception encountered during jwsc", ex );
        }
        finally
        {
            WeblogicMojoUtilities.unsetWeblogicProtocolHandler();
        }
    }

    /**
     * @return the outputName
     */
    public String getOutputName()
    {
        return outputName;
    }

    /**
     * @param outputName the outputName to set
     */
    public void setOutputName( String outputName )
    {
        this.outputName = outputName;
    }

    /**
     * @return the outputDir
     */
    public String getOutputDir()
    {
        return outputDir;
    }

    /**
     * @param outputDir the outputDir to set
     */
    public void setOutputDir( String outputDir )
    {
        this.outputDir = outputDir;
    }

    /**
     * Getter for inputDir
     *
     * @return the inputDir
     */
    public String getInputDir()
    {
        return inputDir;
    }

    /**
     * Setter for inputDir
     *
     * @param inputDir the inputDir to set
     */
    public void setInputDir( String inputDir )
    {
        this.inputDir = inputDir;
    }

    /**
     * Getter for verbose
     *
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * @param verbose the verbose to set
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /**
     * @return the debug
     */
    public boolean isDebug()
    {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    /**
     * @return the explode
     */
    public boolean isExplode()
    {
        return explode;
    }

    /**
     * @param explode the explode to set
     */
    public void setExplode( boolean explode )
    {
        this.explode = explode;
    }

    /**
     * @return the optimize
     */
    public boolean isOptimize()
    {
        return optimize;
    }

    /**
     * @param optimize the optimize to set
     */
    public void setOptimize( boolean optimize )
    {
        this.optimize = optimize;
    }

    /**
     * @return the contextPath
     */
    public String getContextPath()
    {
        return contextPath;
    }

    /**
     * @param contextPath the contextPath to set
     */
    public void setContextPath( String contextPath )
    {
        this.contextPath = contextPath;
    }

    /**
     * @return the descriptor
     */
    public String getDescriptor()
    {
        return descriptor;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor( String descriptor )
    {
        this.descriptor = descriptor;
    }

    /**
     * The setter for source inlcudes
     *
     * @param sourceIncludes - defaults to **\/*.java
     */
    public void setSourceIncludes(String sourceIncludes) {
        this.sourceIncludes = sourceIncludes;
    }

    /**
     * The setter for source excludes
     *
     * @param sourceExcludes - defaults to null
     */
    public void setSourceExcludes(String sourceExcludes) {
        this.sourceExcludes = sourceExcludes;
    }

    /**
     * The keep generated setter
     *
     * @param keepGenerated true to keep the generated code
     */
    public void setKeepGenerated(boolean keepGenerated)
    {
        this.keepGenerated = keepGenerated;
    }

    /**
     * Meaningful toString implementation
     *
     * @return the string representation of this object
     */
    public String toString() {
        return "JwscMojo{" +
                "outputDir='" + outputDir + '\'' +
                ", inputDir='" + inputDir + '\'' +
                ", outputName='" + outputName + '\'' +
                ", verbose=" + verbose +
                ", debug=" + debug +
                ", explode=" + explode +
                ", optimize=" + optimize +
                ", contextPath='" + contextPath + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", sourceIncludes='" + sourceIncludes + '\'' +
                ", sourceExcludes='" + sourceExcludes + '\'' +
                ", keepGenerated=" + keepGenerated +
                '}';
    }
}
