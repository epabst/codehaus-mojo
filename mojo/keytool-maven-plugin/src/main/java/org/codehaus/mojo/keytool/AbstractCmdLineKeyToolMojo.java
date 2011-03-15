package org.codehaus.mojo.keytool;

/*
 * Copyright 2005-2008 The Codehaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License" );
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
import java.io.InputStream;
import java.util.List;
import java.util.StringTokenizer;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * @author <a>Juergen Mayrbaeurl</a>
 * @version 1.0 2008-02-03
 */
public abstract class AbstractCmdLineKeyToolMojo
    extends AbstractKeyToolMojo
{

    /**
     * Get the path of jarsigner tool depending the OS.
     * 
     * @return the path of the jarsigner tool
     */
    protected String getKeytoolPath()
    {
        return KeyToolMojoSupport.getJDKCommandPath( "keytool", getLog() );
    }

    // Helper methods. Could/should be shared e.g. with JavadocReport

    /**
     * Convenience method to add an argument to the <code>command line</code> conditionally based on the given flag.
     * 
     * @param arguments the list to which the argument may be added
     * @param b the flag which controls if the argument is added or not.
     * @param value the argument value to be added.
     */
    protected void addArgIf( List arguments, boolean b, String value )
    {
        if ( b )
        {
            arguments.add( value );
        }
    }

    /**
     * Convenience method to add an argument to the <code>command line</code> if the the value is not null or empty.
     * <p/> Moreover, the value could be comma separated.
     * 
     * @param arguments the list to which the argument may be added
     * @param key the argument name.
     * @param value the argument value to be added.
     * @see #addArgIfNotEmpty(java.util.List,String,String,boolean)
     */
    protected void addArgIfNotEmpty( List arguments, String key, String value )
    {
        // FIXME we need to improve this API
        // addArgIfNotEmpty( arguments, key, value, false );
        addArgIfNotEmpty2( arguments, key, value, false );
    }

    /**
     * Convenience method to add an argument to the <code>command line</code> if the the value is not null or empty.
     * <p/> Moreover, the value could be comma separated.
     * 
     * @param arguments the list to which the argument may be added
     * @param key the argument name.
     * @param value the argument value to be added.
     * @param ignored
     */
    protected void addArgIfNotEmpty2( List arguments, String key, String value, boolean ignored )
    {
        if ( !StringUtils.isEmpty( value ) )
        {
            arguments.add( key );

            arguments.add( value );
        }
    }

    /**
     * Convenience method to add an argument to the <code>command line</code> if the the value is not null or empty.
     * <p/> Moreover, the value could be comma separated.
     * 
     * @param arguments the list to which the argument may be added
     * @param key the argument name.
     * @param value the argument value to be added.
     * @param repeatKey repeat or not the key in the command line
     */
    protected void addArgIfNotEmpty( List arguments, String key, String value, boolean repeatKey )
    {
        if ( !StringUtils.isEmpty( value ) )
        {
            arguments.add( key );

            StringTokenizer token = new StringTokenizer( value, "," );
            while ( token.hasMoreTokens() )
            {
                String current = token.nextToken().trim();

                if ( !StringUtils.isEmpty( current ) )
                {
                    arguments.add( current );

                    if ( token.hasMoreTokens() && repeatKey )
                    {
                        arguments.add( key );
                    }
                }
            }
        }
    }

    //
    // methods used for tests purposes - allow mocking and simulate automatic setters
    //

    protected int executeCommandLine( Commandline commandLine, InputStream inputStream, StreamConsumer stream1,
                                      StreamConsumer stream2 )
        throws CommandLineException
    {
        return CommandLineUtils.executeCommandLine( commandLine, inputStream, stream1, stream2 );
    }

    protected void createParentDirIfNecessary( final String file )
    {
        if ( file != null )
        {
            final File fileDir = new File( file ).getParentFile();

            if ( fileDir != null )
            { // not a relative path
                boolean mkdirs = fileDir.mkdirs();
                getLog().debug( "mdkirs: " + mkdirs + " " + fileDir );
            }
        }
    }
}
