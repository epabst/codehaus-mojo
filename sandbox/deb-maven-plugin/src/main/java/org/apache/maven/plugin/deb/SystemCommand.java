package org.apache.maven.plugin.deb;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes a system command.
 * <p/>
 * Typical usage:
 * <pre>
 * new SystemCommand()
 *     .setBaseDir(directory)
 *     .setCommand("dpkg")
 *     .addArgument("-b")
 *     .addArgument("deb")
 *     .addArgument(debFileName)
 *     .execute();
 * </pre>
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SystemCommand
{
    private String baseDir;

    private String command;

    private List arguments;

    private List enviroment;

    public SystemCommand()
    {
        arguments = new ArrayList();
        enviroment = new ArrayList();
    }

    public SystemCommand setCommand( String command )
    {
        this.command = command;

        return this;
    }

    public SystemCommand setBaseDir( String baseDir )
    {
        this.baseDir = baseDir;

        return this;
    }

    public SystemCommand addArgument( String argument )
    {
        arguments.add( argument );

        return this;
    }

    public SystemCommand addEnviroment( String variable )
    {
        enviroment.add( variable );

        return this;
    }

    public void execute()
        throws Exception
    {
        Runtime runtime;
        String[] args, env;
        File base = null;
        Process process;

        if ( command == null )
        {
            throw new Exception( "Missing field command" );
        }

        arguments.add( 0, command );
        args = (String[]) arguments.toArray( new String[ arguments.size() ] );
        env = (String[]) enviroment.toArray( new String[ enviroment.size() ] );

        if ( baseDir != null )
        {
            base = new File( baseDir );

            if ( !base.isDirectory() )
            {
                throw new Exception( "The basedir must be a directory: '" + base + "'.");
            }
        }

        System.out.println( "Basedir: " + base );
        System.out.println( "Command: " + StringUtils.join(args, " "));

        runtime = Runtime.getRuntime();
        process = runtime.exec( args, env, base );

        process.waitFor();

        dumpStream( process.getInputStream(), System.out );
        dumpStream( process.getErrorStream(), System.err );

        if ( process.exitValue() != 0 )
        {
            throw new Exception( "Error while creating debian package." );
        }
    }

    private void dumpStream( InputStream input, PrintStream output )
        throws IOException
    {
        String line;
        BufferedReader in;

        in = new BufferedReader( new InputStreamReader( input ) );

        while ( ( line = in.readLine() ) != null )
        {
            output.println( line );
            output.flush();
        }
    }
}
