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

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DebianPluginTest
    extends PlexusTestCase
{
    public void testDummy()
    {
    }
/*
    public void testTest1()
        throws Exception
    {
        String projectName = "project-1";

        String dir = "src/test/resources/" + projectName + "/target/";

        String artifactName = dir + projectName + "-1.0.jar";

        FileUtils.mkdir( dir );

        FileUtils.fileWrite( artifactName, "foo" );

        testControlFile( projectName, "deb" );
    }

    private void testControlFile( String projectName, String goal )
        throws Exception
    {
        MavenProjectBuilder builder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        DebPlugin plugin = new DebPlugin();

        MavenProject project = builder.build( getTestFile( "src/test/resources/" + projectName + "/project.xml" ) );

        // ----------------------------------------------------------------------
        // Execute the plugin
        // ----------------------------------------------------------------------

        plugin.setProject( project );

        plugin.execute();

        assertEquals( getTestFile( "src/test/resources/" + projectName + "/control" ),
                      getTestFile( "src/test/resources/" + projectName + "/target/debian/DEBIAN/control" ) );

        release( builder );
    }

    private void assertEquals( File expectedFile, File actualFile )
        throws IOException
    {
        List expectedLines = getLines( expectedFile );

        List actualLines = getLines( actualFile );

        for ( int i = 0; i < expectedLines.size(); i++ )
        {
            String expected = expectedLines.get( i ).toString();

            if ( actualLines.size() - 1 < i )
            {
                fail( "Too few lines in the actual file. Was " + actualLines.size() + ", expected: " + expectedLines.size() );
            }

            String actual = actualLines.get( i ).toString();

            assertEquals( "Checking line #" + ( i + 1 ), expected, actual );
        }

        assertEquals( "Unequal number of lines.", expectedLines.size(), actualLines.size() );
    }

    private List getLines( File file )
        throws IOException
    {
        List lines = new ArrayList();

        assertTrue( "The file doesn't exist: " + file.getAbsolutePath(), file.exists() );

        BufferedReader reader = new BufferedReader( new FileReader( file ) );

        String line;

        while ( ( line = reader.readLine() ) != null )
        {
            lines.add( line );
        }

        return lines;
    }
*/
}
