package org.codehaus.mojo.jpox;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.util.Properties;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JpoxEnhancerMojoTest
    extends PlexusTestCase
{
    // ----------------------------------------------------------------------
    // @todo find a way to test this mojo
    // This doesn't actually do anything, and won't run jpox correctly
    // because of the classpath being empty
    //
    // It's probably wise to split up the mojo into several parts so each
    // part can be tested isolated and without actually running the enhancer
    // tool.
    // ----------------------------------------------------------------------

    public void testProjectWithoutAnyJdoFiles()
        throws Exception
    {
/*
        JpoxEnhancerMojo mojo = new JpoxEnhancerMojo();

        PluginExecutionRequest request = new PluginExecutionRequest( makeParameters() );

        PluginExecutionResponse response = new PluginExecutionResponse();

        mojo.execute( request, response );
*/
    }

    public void testProjectWithAJdoFile()
        throws Exception
    {
/*
        JpoxEnhancerMojo mojo = new JpoxEnhancerMojo();

        PluginExecutionRequest request = new PluginExecutionRequest( makeParameters() );

        PluginExecutionResponse response = new PluginExecutionResponse();

        mojo.execute( request, response );
*/
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Properties makeParameters()
        throws Exception
    {
        Properties parameters = new Properties();

        parameters.put( "basedir", getTestPath( "src/test/projects/project-2" ) );

        parameters.put( "classes", getTestPath( "target/test-classes" ) );

        File output = getTestFile( "target/jpox-output-2" );

        if ( output.exists() )
        {
            FileUtils.deleteDirectory( output );
        }

        assertTrue( output.mkdirs() );

        parameters.put( "output", output.getAbsolutePath() );

        parameters.put( "classpathElements", new String[]{} );

        return parameters;
    }
}
