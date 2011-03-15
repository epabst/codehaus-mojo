package org.codehaus.mojo.visibroker;

/*
 * Copyright 2006 The Codehaus.
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
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.SourceDependencyAnalyzer;
import org.codehaus.mojo.natives.parser.CParser;
import org.codehaus.mojo.natives.parser.Parser;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * 
 * @author dtran
 *
 */
public abstract class AbstractIDL2XXXMojo
    extends AbstractVisiBrokerMojo
{
    /**
     * A list of IDL files relative to project's basedir to generate Java files
     * @parameter 
     * @required
     * @description
     */
    protected File[] sources;

    /**
     * List of include directories relative to project basedir.
     * This is -I's include path arugments
     * @parameter 
     */
    protected File[] includePaths;

    protected abstract void setupVisiBrokerToolSpecificArgs( Commandline cl );

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

    protected void setupCommandLineIncludePaths( Commandline cl )
    {
        if ( this.includePaths != null )
        {
            for ( int i = 0; i < this.includePaths.length; ++i )
            {
                cl.createArgument().setValue( "-I" + this.includePaths[i].getPath() );
            }
        }
    }

    protected boolean isIDLSourceStaled( File idlFile, File timeStampFile )
        throws MojoExecutionException
    {
        Parser parser = new CParser();

        try
        {
            if ( !SourceDependencyAnalyzer.isStaled( idlFile, timeStampFile, parser, null ) )
            {
                this.getLog().info( idlFile.getPath() + " is up to date." );
                return false;
            }
        }
        catch ( NativeBuildException ioe )
        {
            throw new MojoExecutionException( "Error analyzing " + idlFile.getPath() + " dependencies.", ioe );
        }

        return true;
    }

    public void execute()
        throws MojoExecutionException
    {
        outputDirectory.mkdirs();

        for ( int i = 0; i < this.sources.length; ++i )
        {
            File timeStampFile = new File( this.getTimeStampFile( sources[i] ) );

            if ( !this.isIDLSourceStaled( sources[i], timeStampFile ) )
            {
                continue;
            }

            Commandline cl = new Commandline();

            cl.setExecutable( "java" );

            setupCommandLineVisiBrokerSystemProperties( cl );

            addCommandLineClassPath( cl );

            cl.createArgument().setValue( "com.inprise.vbroker.compiler.tools.tool" );

            setupVisiBrokerToolSpecificArgs( cl );

            setupCommandLineUserOptions( cl );

            setupCommandLineIncludePaths( cl );

            cl.createArgument().setValue( "-root_dir" );

            cl.createArgument().setValue( this.outputDirectory.getPath() );

            cl.createArgument().setValue( this.sources[i].getPath() );

            executeCommandline( cl, this.getLog() );

            try
            {
                FileUtils.fileWrite( timeStampFile.getPath(), new String() );
            }
            catch ( IOException ioe )
            {
                String message = "Unable to create temporary timestamp file: " + timeStampFile;
                throw new MojoExecutionException( message, ioe );
            }
        }

    }
}
