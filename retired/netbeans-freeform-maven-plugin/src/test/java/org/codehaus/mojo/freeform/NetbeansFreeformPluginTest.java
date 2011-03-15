package org.codehaus.mojo.freeform;

/*
* Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.mojo.freeform.mojo.GenerateNetbeansProjectMojo;
import org.codehaus.plexus.PlexusTestCase;


/**
 * This class holds the unit tests of the Netbeans Freeform Plugin.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public class NetbeansFreeformPluginTest
    extends PlexusTestCase
{
    /**
     * Holds the fake local repository.
     */
    private ArtifactRepository localRepository;

    /**
     * Holds the base directory of the tested project
     */
    private File basedir;

    /**
     * Holds the mojo to test.
     */
    private GenerateNetbeansProjectMojo generateNetbeansProjectMojo;

    /**
     * Holds the fake executed maven project.
     */
    private MavenProject executedProject;

    /**
     * Holds the maven project of the tested project.
     */
    private MavenProject project;

    /**
     * This test a pom without any dependency
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject1()
        throws Exception
    {
        prepareProject( "project-1" );
        executeProject();

        assertFiles();
    }

    /**
     * This test a pom with some dependency
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject2()
        throws Exception
    {
        prepareProject( "project-2" );
        executeProject();

        assertFiles();
    }

    /**
     * This test an execution with additional actions/targets
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject3()
        throws Exception
    {
        prepareProject( "project-3" );
        generateNetbeansProjectMojo.setAdditionalGoals(
            "plugin:xdoc site:site"
        );
        executeProject();

        assertFiles();
    }

    /**
     * This test an execution with additional folders/files
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject4()
        throws Exception
    {
        prepareProject( "project-4" );
        generateNetbeansProjectMojo.setAdditionalFiles(
            "src/main/mdo/FreeformProject.mdo"
        );
        generateNetbeansProjectMojo.setAdditionalFolders( "src/test/projects" );
        executeProject();

        assertFiles();
    }

    /**
     * This test an execution with outputDirectory parameter
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject5()
        throws Exception
    {
        prepareProject( "project-5" );
        generateNetbeansProjectMojo.setOutputDirectory(this.getBasedir() + "/target/project-5");
        executeProject();

        assertFiles(this.getBasedir() + "/target/project-5");
    }

    /**
     * This test a pom with maven-plugin packaging
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject6()
        throws Exception
    {
        prepareProject( "project-6" );
        
        executeProject();

        assertFiles();
    }

    /**
     * This test a pom with parent which have dependency
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject7()
        throws Exception
    {
        prepareProject( "project-7" );
        
        executeProject();

        assertFiles();
    }

    /**
     * This test a pom with parent which have a test dependency
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject8()
        throws Exception
    {
        prepareProject( "project-8" );
        
        executeProject();

        assertFiles();
    }

    /**
     * This test a pom with parent which both have dependency
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject9()
        throws Exception
    {
        prepareProject( "project-9" );
        
        executeProject();

        assertFiles();
    }

    /**
     * This test a pom with parent which both have test dependency
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject10()
        throws Exception
    {
        prepareProject( "project-10" );
        
        executeProject();

        assertFiles();
    }

    /**
     * This test a pom without any dependency but custom Maven path
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    public void testProject11()
        throws Exception
    {
        prepareProject( "project-11" );
        generateNetbeansProjectMojo.setMavenPath( "/junk/path/to/mvn" );
        executeProject();

        assertFiles();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * This method gives the list of String in a file.
     *
     * @param mavenRepo Not used.
     * @param file      The file to be read.
     * @return The list of the lines of the file.
     * @throws java.io.IOException if something goes wrong.
     */
    private List getLines(
        String mavenRepo,
        File file
    )
        throws IOException
    {
        List lines = new ArrayList();

        BufferedReader reader = new BufferedReader( new FileReader( file ) );

        String line;

        while ( ( line = reader.readLine() ) != null )
        {
            lines.add(
                line ); //StringUtils.replace( line, "#ArtifactRepositoryPath#", mavenRepo.replace( '\\', '/' ) ) );
        }

        return lines;
    }

    /**
     * This method asserts that the two given files are equals in their
     * content.
     *
     * @param mavenRepo    Not used.
     * @param expectedFile The file that is expected.
     * @param actualFile   The file that is.
     * @throws java.io.IOException if something goes wrong.
     */
    private void assertFileEquals(
        String mavenRepo,
        File expectedFile,
        File actualFile
    )
        throws IOException
    {
        List expectedLines = getLines( mavenRepo, expectedFile );

        List actualLines = getLines( mavenRepo, actualFile );

        for ( int i = 0; i < expectedLines.size(); i++ )
        {
            String expected = expectedLines.get( i ).toString();

            if ( actualLines.size() < i )
            {
                fail(
                    "Too few lines in the actual file. Was "
                    + actualLines.size() + ", expected: "
                    + expectedLines.size()
                );
            }

            String actual = actualLines.get( i ).toString();

            assertEquals( "Checking line #" + ( i + 1 ), expected, actual );
        }

        assertTrue(
            "Unequal number of lines.",
            expectedLines.size() == actualLines.size()
        );
    }

    /**
     * This method asserts that the tested files are equals.
     *
     * @throws java.io.IOException if something goes wrong.
     */
    private void assertFiles()
        throws IOException
    {
        assertFileEquals(
            localRepository.getBasedir(),
            new File( basedir, "project" ),
            new File( basedir, "nbproject/project.xml" )
        );

        assertFileEquals(
            localRepository.getBasedir(),
            new File( basedir, "mavencall" ),
            new File( basedir, "nbproject/mavencall.xml" )
        );
    }

    /**
     * This method asserts that the tested files are equals.
     *
     * @param outputDirectory The directory where to find the geenrated files.
     *
     * @throws java.io.IOException if something goes wrong.
     */
    private void assertFiles(String outputDirectory)
        throws IOException
    {
        assertFileEquals(
            localRepository.getBasedir(),
            new File( basedir, "project" ),
            new File( new File(outputDirectory), "nbproject/project.xml" )
        );

        assertFileEquals(
            localRepository.getBasedir(),
            new File( basedir, "mavencall" ),
            new File( new File(outputDirectory), "nbproject/mavencall.xml" )
        );
    }

    /**
     * This method executes the plugin mojo.
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    private void executeProject()
        throws Exception
    {
        generateNetbeansProjectMojo.execute();
    }

    /**
     * This method prepares the plugin mojo for the given project.
     *
     * @param projectName The name of the project (its directory).
     * @throws java.lang.Exception if something goes wrong.
     */
    private void prepareProject( final String projectName )
        throws Exception
    {
        basedir = getTestFile( "src/test/projects/" + projectName );

        new File( basedir, "nbproject/project.xml" ).delete();
        new File( basedir, "nbproject/project.properties" ).delete();
        new File( basedir, "nbproject/mavencall.xml" ).delete();
        new File( basedir, "nbproject" ).delete();

        MavenProjectBuilder builder =
            (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        File repo = getTestFile( "src/test/repository" );

        ArtifactRepositoryLayout localRepositoryLayout =
            (ArtifactRepositoryLayout) lookup(
                ArtifactRepositoryLayout.ROLE, "default"
            );

        localRepository =
            new DefaultArtifactRepository(
                "local", "file://" + repo.getAbsolutePath(),
                localRepositoryLayout
            );

        project =
            builder.buildWithDependencies(
                new File( basedir, "pom.xml" ),
                localRepository,
                null
            );

        for ( Iterator it = project.getArtifacts().iterator();
              it.hasNext();
            )
        {
            Artifact artifact = (Artifact) it.next();
            artifact.setFile(
                new File(
                    localRepository.getBasedir(),
                    localRepository.pathOf( artifact )
                )
            );
        }

        // It is assumed that the executed project is a copy of the project 
        // where each test method set some specific values.
        executedProject =
            builder.buildWithDependencies(
                new File( basedir, "pom.xml" ),
                localRepository,
                null
            );

        for (
            Iterator it = executedProject.getArtifacts().iterator();
            it.hasNext();
            )
        {
            Artifact artifact = (Artifact) it.next();
            artifact.setFile(
                new File(
                    localRepository.getBasedir(),
                    localRepository.pathOf( artifact )
                )
            );
        }

        generateNetbeansProjectMojo = new GenerateNetbeansProjectMojo();

        generateNetbeansProjectMojo.setProject( project );

        generateNetbeansProjectMojo.setExecutedProject( executedProject );

        generateNetbeansProjectMojo.setLocalRepository( localRepository );
    }
}
