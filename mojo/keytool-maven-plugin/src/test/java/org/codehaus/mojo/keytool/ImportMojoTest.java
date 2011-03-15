package org.codehaus.mojo.keytool;

/*
 * Copyright 2005-2008 The Codehaus.
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * These unit tests only check whether the generated command lines are correct. Really running the command would mean
 * checking the results, which is too painful and not really a unit test.
 * 
 * @author Juergen Mayrbaeurl <j.mayrbaeurl@yahoo.de>
 * @version 1.0 2008-02-03
 */
public class ImportMojoTest
    extends TestCase
{

    private MockImportMojo mojo;

    static class MockImportMojo
        extends ImportMojo
    {

        public int executeResult;

        public List commandLines = new ArrayList();

        public String failureMsg;

        public Map systemProperties = new HashMap();

        protected int executeCommandLine( Commandline commandLine, InputStream inputStream, StreamConsumer stream1,
                                          StreamConsumer stream2 )
            throws CommandLineException
        {

            commandLines.add( commandLine );
            if ( failureMsg != null )
            {
                throw new CommandLineException( failureMsg );
            }
            return executeResult;
        }

    }

    public void setUp()
        throws IOException
    {
        mojo = new MockImportMojo();
        mojo.executeResult = 0;
        // it doesn't really matter if the paths are not cross-platform, we don't execute the command lines anyway
        File workingdir = new File( System.getProperty( "java.io.tmpdir" ) );
        mojo.setWorkingDir( workingdir );
        mojo.setKeypass( "secretpassword" );
        mojo.setStorepass( "secretpassword2" );
    }

    public void tearDown()
    {
        mojo = null;
    }

    public void testRunOKMinimumNumberOfParameters()
        throws MojoExecutionException
    {
        mojo.execute();

        String[] expectedArguments =
            { "-import", "-noprompt", "-keypass", "secretpassword", "-storepass", "secretpassword2" };

        checkMojo( expectedArguments );
    }

    public void testRunOKImportJRECaCerts()
        throws MojoExecutionException
    {
        mojo.setFile( "certsfile" );
        mojo.setUseJREcacerts( true );
        File cacertsFile = new File( SystemUtils.getJavaHome() + "/" + "lib/security/cacerts" );

        mojo.execute();

        String[] expectedArguments =
            { "-import", "-noprompt", "-file", "certsfile", "-keystore", cacertsFile.getAbsolutePath(), "-keypass",
                "secretpassword", "-storepass", "secretpassword2" };

        checkMojo( expectedArguments );
    }

    private void checkMojo( String[] expectedCommandLineArguments )
    {
        assertEquals( 1, mojo.commandLines.size() );
        Commandline commandline = (Commandline) mojo.commandLines.get( 0 );
        String[] arguments = commandline.getArguments();

        assertEquals( "Differing number of arguments", expectedCommandLineArguments.length, arguments.length );
        for ( int i = 0; i < arguments.length; i++ )
        {
            assertEquals( expectedCommandLineArguments[i], arguments[i] );
        }
    }
}
