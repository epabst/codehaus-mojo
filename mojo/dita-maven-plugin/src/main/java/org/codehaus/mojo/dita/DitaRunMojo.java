package org.codehaus.mojo.dita;

/*
 * Copyright 2000-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * <p>
 * Execute DITA Open Toolkit's Ant command line to transform DITA files to desired output format.
 * </p>
 * <p>
 * Behind the scene, <i>antProperties</i> are temporarily stored under
 * ${dita.temp.dir}/properties.temp to be used with
 * </p>
 * <p>
 * ant -f ${dita.dir}/build.xml -propertyFile ${dita.temp.dir}/properties.temp
 * </p>
 * 
 * 
 * @goal run
 * @requiresProject false
 * @requiresDependencyResolution compile
 */
public class DitaRunMojo
    extends AbstractDitaMojo
{
    private static final String DITA_BUILD_DIR = "basedir";

    private static final String DITA_TMP_DIR = "dita.temp.dir";

    private static final String DITA_OUT_DIR = "output.dir";

    private static final String DITA_LOG_DIR = "args.log";

    private static final String DITA_MAP = "args.input";

    private static final String DITA_TRANSTYPE = "transtype";
    
    /**
     * Use DITA Open Toolkit's tools/ant
     * 
     * @parameter expression="${dita.useDitaAnt}" default-value="true"
     * @since since 1.0-beta-1
     * 
     */
    private boolean useDitaAnt;

    /**
     * Point to Ant installation directory. If given, it will override <i>useDitaAnt</i> and
     * ${env.ANT_HOME}
     * 
     * @parameter expression="${dita.antHome}" 
     * @since since 1.0-beta-1
     * 
     */
    private File antHome;

    /**
     * If given, will be added to Ant command line. Use this for trouble shooting purpose
     * 
     * @parameter expression="${dita.antArguments}"
     * @since since 1.0-beta-1
     * 
     */
    private String antArguments;

    /**
     * Equivalent with ANT_OPTS environment. This parameter overrides the current env.ANT_OPTS if given. 
     * Typical usage is to setup JVM's heap space ( example -Xmx500m )
     * 
     * @parameter expression="${dita.antOpts}"
     * @since since 1.0-beta-1
     * 
     */
    private String antOpts;

    /**
     * Controls whether this goal tries to compress the output directory and attach compressed
     * output file to the project for install and deploy purposes.
     * 
     * @parameter expression="${dita.attach}" default-value="false"
     * @since since 1.0-beta-1
     */
    private boolean attach = false;

    /**
     * Output file classifier to be attached to the project.
     * 
     * @parameter expression="${dita.attachClassifier}"
     * @since since 1.0-beta-1
     */
    private String attachClassifier;

    /**
     * Output file extension to be attached to the project. When transtype is one of the pdf types or
     * <i>htmlhelp</i>, the attachType will be hard coded to <i>pdf</i> and <i>chm</i> respectively.
     * 
     * @parameter expression="${dita.attachType}" default-value="jar"
     * @since since 1.0-beta-1
     */
    private String attachType;

    // /////////////////////////////////////////////////////////////////////////

    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
            return;
        }

        initialize();

        Commandline cl = new Commandline();

        cl.setExecutable( getAntExecutable().getAbsolutePath() );
        cl.setWorkingDirectory( project.getBasedir() );

        setupClasspathEnv( cl );

        setupAntEnv( cl );

        setupAntArguments( cl );

        executeCommandline( cl );

        if ( attach )
        {
            attachTheOuput();
        }

    }

    protected void setupDefaultAntDirectory( String antPropertyName, File defaultDirectory )
    {
        setupDefaultAntProperty( antPropertyName, defaultDirectory.getAbsolutePath() );
    }

    protected void setupDefaultAntProperty( String antPropertyName, String value )
    {
        if ( antProperties.get( antPropertyName ) == null )
        {
            antProperties.put( antPropertyName, value );
        }
    }

    private void initialize()
        throws MojoExecutionException
    {
        setupDitaDirectory();

        setupAntDirectory();
        
        setupDefaultAntDirectory( DITA_BUILD_DIR, project.getBasedir() );

        File ditaBuildDir = new File( project.getBuild().getDirectory(), "dita" );

        setupDefaultAntDirectory( DITA_OUT_DIR, new File( ditaBuildDir, "out" ) );
        setupDefaultAntDirectory( DITA_TMP_DIR, new File( ditaBuildDir, "temp" ) );
        setupDefaultAntDirectory( DITA_LOG_DIR, new File( ditaBuildDir, "log" ) );

        File defaultDitaMapFile = new File( project.getBasedir(), "/src/main/dita/" + project.getArtifactId()
            + ".ditamap" );

        setupDefaultAntProperty( DITA_MAP, defaultDitaMapFile.getAbsolutePath() );

    }

    protected void setupAntDirectory()
        throws MojoExecutionException
    {
        if ( this.antHome == null )
        {
            if ( useDitaAnt )
            {
                antHome = new File( this.ditaDirectory, "tools/ant" );
            }
            else
            {
                String antPath = System.getenv( "ANT_HOME" );
                if ( antPath == null )
                {
                    throw new MojoExecutionException( "env.ANT_HOME not found." );
                }
                
                antHome = new File( antPath );
                
            }
        }
        
        if ( !antHome.isDirectory() )
        {
            throw new MojoExecutionException( this.getAntPath() + " ditaDirectory not found. " );
        }
        
    }

    private void setupAntEnv( Commandline cl )
        throws MojoExecutionException
    {
        cl.addEnvironment( "ANT_HOME", this.getAntPath() );

        if ( antOpts != null )
        {
            cl.addEnvironment( "ANT_OPTS", antOpts );
            this.getLog().debug( "ANT_OPT=" + antOpts );
        }

    }

    private void setupAntArguments( Commandline cl )
        throws MojoExecutionException
    {
        if ( !StringUtils.isBlank( antArguments ) )
        {
            cl.addArguments( StringUtils.split( antArguments ) );
        }

        cl.createArg().setValue( "-f" );
        cl.createArg().setValue( getDitaBuildXmlPath() );

        cl.createArg().setValue( "-propertyfile" );
        cl.createArg().setValue( setupAntProperties() );
    }

    /**
     * 
     * @return canonical path to temporary properties ant file
     * @throws MojoExecutionException
     */
    private String setupAntProperties()
        throws MojoExecutionException
    {
        Properties inputProperties = new Properties();

        for ( Iterator<String> i = antProperties.keySet().iterator(); i.hasNext(); )
        {
            String key = i.next();
            String value = antProperties.get( key );
            if ( value != null )
            {
                inputProperties.put( key, value );
            }
        }

        FileOutputStream os = null;
        try
        {
            File tmpFile = new File( antProperties.get( "dita.temp.dir" ), "properties.temp" );
            tmpFile.getParentFile().mkdirs();

            os = new FileOutputStream( tmpFile );
            inputProperties.store( os, null );

            return tmpFile.getCanonicalPath();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( os );
        }
    }

    private String getDitaBuildXmlPath()
        throws MojoExecutionException
    {
        try
        {
            return new File( ditaDirectory, "build.xml" ).getCanonicalPath();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private String getAntPath()
        throws MojoExecutionException
    {
        try
        {
            return antHome.getCanonicalPath();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private File getAntExecutable()
        throws MojoExecutionException
    {
        String antFileName = "ant";

        if ( Os.isFamily( "windows" ) )
        {
            antFileName += ".bat";
        }

        return new File( this.getAntPath(), "bin/" + antFileName );

    }

    protected void executeCommandline( Commandline cl )
        throws MojoExecutionException
    {
        int ok;

        try
        {
            AntOutputConsumer stdout = new AntOutputConsumer();

            AntOutputConsumer stderr = new AntOutputConsumer();;

            this.getLog().debug( cl.toString() );

            ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

            if ( stdout.isFailure() || stderr.isFailure() )
            {
                throw new MojoExecutionException( "BUILD FAILED" );
            }
        }
        catch ( CommandLineException ecx )
        {
            throw new MojoExecutionException( "Error executing command line", ecx );
        }

        if ( ok != 0 )
        {
            throw new MojoExecutionException( "Error executing command line. Exit code:" + ok );
        }

    }

    private void attachTheOuput()
        throws MojoExecutionException
    {
        File outputDir = new File( antProperties.get( DITA_OUT_DIR ) );

        String transtype = antProperties.get( DITA_TRANSTYPE );

        if ( "pdf".equals( transtype ) || "pdf2".equals( transtype ) || "legacypdf".equals( transtype ) )
        {
            attachSingleOutput( attachClassifier, "pdf", outputDir );
        }
        else if ( "htmlhelp".equals( transtype ) )
        {
            attachSingleOutput( attachClassifier, "chm", outputDir );
        }
        else
        {
            this.archiveAndAttachTheOutput( outputDir, attachClassifier, attachType );
        }
    }

    private void attachSingleOutput( String classifier, String type, File outputDir )
        throws MojoExecutionException
    {
        File ditamap = new File( antProperties.get( DITA_MAP ) );

        String[] tokens = StringUtils.split( ditamap.getName(), "." );
        String fileName = "";
        for ( int i = 0; i < tokens.length - 1; ++i )
        {
            fileName += tokens[i] + ".";
        }
        fileName += type;

        File ditaOutputFile = new File( outputDir, fileName );
        isAttachYet( ditaOutputFile );
        attachArtifact( classifier, type, ditaOutputFile );

    }

}
