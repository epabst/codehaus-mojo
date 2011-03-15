package org.codehaus.mojo.visibroker;

/*
 * Copyright 20056 The Codehaus.
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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

/**
 * 
 * @author dtran
 *
 */
public abstract class AbstractVisiBrokerMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Visibroker command options.  See Your Visibroker documentation for details
     * @parameter 
     * @optional
     * 
     */
    protected String[] options;

    /**
     * Where to place the generated files.
     * @parameter expression="{outputDirectory}" default-value="${project.build.directory}/visibroker"
     */
    protected File outputDirectory;

    /**
     * Where the place the timestamp files.
     * @parameter expression="${project.build.directory}/timestamps"
     * @required
     * @readonly
     */
    protected File timestampDirectory;

    /**
     * Contains Visibroker jara files to be supplied by use in plugin's dependencies
     * @parameter expression="${plugin.artifacts}"
     * @readonly
     */
    private List pluginArtifacts;
    
    protected static void executeCommandline( Commandline cl, Log logger )
        throws MojoExecutionException
    {
        int ok;

        try
        {
            DefaultConsumer stdout = new DefaultConsumer();

            DefaultConsumer stderr = stdout;

            logger.info( cl.toString() );

            ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
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

    protected void addCommandLineClassPath( Commandline cl )
    {
        String classPathSeparator = System.getProperty( "path.separator" );

        StringBuffer classPathValue = new StringBuffer();

        Iterator iter = pluginArtifacts.iterator();

        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            classPathValue.append( artifact.getFile().getPath() ).append( classPathSeparator );
        }

        File toolJar = new File( System.getProperty( "java.home" ), "../lib/tools.jar" );

        classPathValue.append( toolJar.getPath() ).append( classPathSeparator );

        classPathValue.append( project.getBuild().getOutputDirectory() );

        cl.createArgument().setValue( "-classpath" );

        cl.createArgument().setValue( classPathValue.toString() );

    }

    protected static void setupCommandLineVisiBrokerSystemProperties( Commandline cl )
    {
        cl.createArgument().setValue( "-Dorg.omg.CORBA.ORBClass=com.inprise.vbroker.orb.ORB" );
        cl.createArgument().setValue( "-Dorg.omg.CORBA.ORBSingletonClass=com.inprise.vbroker.orb.ORBSingleton" );
        cl.createArgument().setValue( "-Djavax.rmi.CORBA.StubClass=com.inprise.vbroker.rmi.CORBA.StubImpl" );
        cl.createArgument().setValue( "-Djavax.rmi.CORBA.UtilClass=com.inprise.vbroker.rmi.CORBA.UtilImpl" );
        cl
            .createArgument()
            .setValue(
                       "-Djavax.rmi.CORBA.PortableRemoteObjectClass=com.inprise.vbroker.rmi.CORBA.PortableRemoteObjectImpl" );

    }

    protected void setupCommandLineUserOptions( Commandline cl )
    {
        for ( int i = 0; i < this.options.length; ++i )
        {
            if ( this.options[i] == null )
            {
                continue;
            }
            String[] subOptions = StringUtils.split( this.options[i] );

            for ( int k = 0; k < subOptions.length; ++k )
            {
                if ( subOptions[k].trim().length() != 0 )
                {
                    cl.createArgument().setValue( subOptions[k] );
                }
            }
        }

    }

    protected String getTimeStampFile( File source )
    {
        if ( !this.timestampDirectory.exists() )
        {
            this.timestampDirectory.mkdirs();
        }
        return this.timestampDirectory.getPath() + "/" + source.getName() + ".ts";
    }

    
}
