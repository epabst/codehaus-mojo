package org.apache.maven.plugin.deb;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.PlexusTestCase;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ControlFileGeneratorTest
    extends PlexusTestCase
{
    public void testPackageName()
        throws Exception
    {
        ControlFileGenerator generator = (ControlFileGenerator) lookup( ControlFileGenerator.ROLE );

        generator.setGroupId( "myGroup" );
        generator.setArtifactId( "myArtifact" );

        assertEquals( "mygroup-myartifact", generator.getDebianPackageName() );
    }

    public void testDescription()
        throws Exception
    {
        ControlFileGenerator generator = (ControlFileGenerator) lookup( ControlFileGenerator.ROLE );

        // ----------------------------------------------------------------------
        // Description from POM.
        // ----------------------------------------------------------------------

        generator.setDescription( "Short description. Long description." );

        assertEquals( "Short description.\n" +
                      " Long description.", generator.getDebianDescription() );

        generator = (ControlFileGenerator) lookup( ControlFileGenerator.ROLE );

        // ----------------------------------------------------------------------
        // Short description is set.
        // ----------------------------------------------------------------------

        generator.setShortDescription( "My short description." );

        generator.setDescription( "Description." );

        assertEquals( "My short description.\n" +
                      " Description.", generator.getDebianDescription() );

        // ----------------------------------------------------------------------
        // A long description with blank lines.
        // ----------------------------------------------------------------------

        generator.setDescription(
            "Maven was originally started as an attempt to simplify the build \n" +
            "processes in the Jakarta Turbine project. There were several \n" +
            "projects each with their own Ant build files that were all \n" +
            "slightly different and JARs were checked into CVS. We wanted \n" +
            "a standard way to build the projects, a clear definition of \n" +
            "what the project consisted of, an easy way to publish \n" +
            "project information and a way to share JARs across several \n" +
            "projects.\n" +
            "\n" +
            "What resulted is a tool that can now be used for building and \n" +
            "managing any Java-based project. We hope that we have \n" +
            "created something that will make the day-to-day work of \n" +
            "Java developers easier and generally help with the \n" +
            "comprehension of any Java-based project." );

        assertEquals( "My short description.\n" +
            " Maven was originally started as an attempt to simplify the build\n" +
            " processes in the Jakarta Turbine project. There were several\n" +
            " projects each with their own Ant build files that were all\n" +
            " slightly different and JARs were checked into CVS. We wanted\n" +
            " a standard way to build the projects, a clear definition of\n" +
            " what the project consisted of, an easy way to publish\n" +
            " project information and a way to share JARs across several\n" +
            " projects.\n" +
            ".\n" +
            " What resulted is a tool that can now be used for building and\n" +
            " managing any Java-based project. We hope that we have\n" +
            " created something that will make the day-to-day work of\n" +
            " Java developers easier and generally help with the\n" +
            " comprehension of any Java-based project.",
                      generator.getDebianDescription() );
    }

    public void testMaintainerRevision() throws Exception {
        ControlFileGenerator generator = (ControlFileGenerator) lookup( ControlFileGenerator.ROLE );

        try
        {
            generator.getVersion();
            fail("Expected MojoFailureException.");
        } catch ( MojoFailureException e )
        {
            // expected
        }

        generator.setProjectVersion( "1.0" );
        assertEquals( "1.0", generator.getVersion().projectVersion );
        assertEquals( 0, generator.getVersion().maintainerRevision );
        assertNull( generator.getVersion().timestamp );

        generator.setProjectVersion( "1.0-2" );
        assertEquals( "1.0", generator.getVersion().projectVersion );
        assertEquals( 2, generator.getVersion().maintainerRevision );
        assertNull( generator.getVersion().timestamp );

        generator.setProjectVersion( "1.0-SNAPSHOT");
        assertEquals( "1.0", generator.getVersion().projectVersion );
        assertEquals( 0, generator.getVersion().maintainerRevision );
        assertNull( generator.getVersion().timestamp );

        generator.setProjectVersion( "1.0-2-SNAPSHOT" );
        assertEquals( "1.0", generator.getVersion().projectVersion );
        assertEquals( 2, generator.getVersion().maintainerRevision );
        assertNull( generator.getVersion().timestamp );

        generator.setProjectVersion( "1.0-2-SNAPSHOT" );
        generator.setTimestamp( "20080703.084400" );
        assertEquals( "1.0", generator.getVersion().projectVersion );
        assertEquals( 2, generator.getVersion().maintainerRevision );
        assertEquals( "20080703.084400", generator.getVersion().timestamp );
    }

    public void testDependsGeneration() throws Exception {
        ControlFileGenerator generator = (ControlFileGenerator) lookup( ControlFileGenerator.ROLE );

        assertNull( generator.getDepends() );

        VersionRange v1_0 = VersionRange.createFromVersionSpec("1.0");

        generator.setDependencies(new HashSet(Arrays.asList(new DebianDependency[] {
            new DebianDependency(new DefaultArtifact("groupId", "artifactId", v1_0, "runtime", "dpk", "dpkg", null))
        })));
    }
}
