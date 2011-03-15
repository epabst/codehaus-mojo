package org.apache.maven.plugin.deb;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.PlexusTestCase;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ControlFileGeneratorTest.java 7208 2008-07-03 10:56:37Z trygvis $
 */
public class DebToolTest
    extends PlexusTestCase
{
    public void testPackageName()
        throws Exception
    {
        ControlFile controlFile = new DebTool().
            mavenProject( "myGroup", "myArtifact", null, "1.0", null, false ).
            generateControlFile();

        assertEquals( "mygroup-myartifact", controlFile.getPackageName() );
    }

    public void testDescription()
        throws Exception
    {
        DebTool debTool = new DebTool();

        // ----------------------------------------------------------------------
        // Description from POM.
        // ----------------------------------------------------------------------

        debTool.description( "Short description. Long description." );

        assertEquals( "Short description.\n" + " Long description.",
                      debTool.generateControlFile().getDebianDescription() );

        // ----------------------------------------------------------------------
        // Short description is set.
        // ----------------------------------------------------------------------

        debTool = new DebTool();

        debTool.shortDescription( "My short description." );

        debTool.description( "Description." );

        assertEquals( "My short description.\n" + " Description.",
                      debTool.generateControlFile().getDebianDescription() );

        // ----------------------------------------------------------------------
        // A long description with blank lines.
        // ----------------------------------------------------------------------

        debTool.description( "Maven was originally started as an attempt to simplify the build \n" +
            "processes in the Jakarta Turbine project. There were several \n" +
            "projects each with their own Ant build files that were all \n" +
            "slightly different and JARs were checked into CVS. We wanted \n" +
            "a standard way to build the projects, a clear definition of \n" +
            "what the project consisted of, an easy way to publish \n" +
            "project information and a way to share JARs across several \n" + "projects.\n" + "\n" +
            "What resulted is a tool that can now be used for building and \n" +
            "managing any Java-based project. We hope that we have \n" +
            "created something that will make the day-to-day work of \n" +
            "Java developers easier and generally help with the \n" + "comprehension of any Java-based project." );

        assertEquals( "My short description.\n" +
            " Maven was originally started as an attempt to simplify the build\n" +
            " processes in the Jakarta Turbine project. There were several\n" +
            " projects each with their own Ant build files that were all\n" +
            " slightly different and JARs were checked into CVS. We wanted\n" +
            " a standard way to build the projects, a clear definition of\n" +
            " what the project consisted of, an easy way to publish\n" +
            " project information and a way to share JARs across several\n" + " projects.\n" + ".\n" +
            " What resulted is a tool that can now be used for building and\n" +
            " managing any Java-based project. We hope that we have\n" +
            " created something that will make the day-to-day work of\n" +
            " Java developers easier and generally help with the\n" + " comprehension of any Java-based project.",
                      debTool.generateControlFile().getDebianDescription() );
    }

    public void testMaintainerRevision()
        throws Exception
    {
        DebTool debTool = new DebTool();

        try
        {
            debTool.generateControlFile().getVersion();
            fail( "Expected RuntimeException." );
        }
        catch ( RuntimeException e )
        {
            assertTrue( e.getMessage().indexOf( "required field: upstreamVersion" ) > 0 );
        }

        assertStuff( new DebTool(), "groupId", "artifactId", null, "1.0", null, "1.0", 0, false, "groupid-artifactid" );
        assertStuff( new DebTool(), "groupId", "artifactId", null, "1.0-2", null, "1.0", 2, false, "groupid-artifactid" );
        assertStuff( new DebTool(), "groupId", "artifactId", null, "1.0-SNAPSHOT", null, "1.0", 0, false, "groupid-artifactid" );
        assertStuff( new DebTool(), "groupId", "artifactId", null, "1.0-2-SNAPSHOT", null, "1.0", 2, false, "groupid-artifactid" );
        debTool = new DebTool();
        debTool.packageName( "yo" );
        assertStuff( debTool, "groupId", "artifactId", null, "1.0-2-SNAPSHOT", "20080703.084400", "1.0", 2, true, "yo" );
    }

    private void assertStuff( DebTool debTool, String groupId, String artifactId, String classifier,
                              String projectVersion, String timestamp, String upstreamVersion, int maintainerRevision,
                              boolean snapshot, String packageName)
    throws MojoFailureException
    {
        ControlFile controlFile = debTool.
            mavenProject(groupId, artifactId, classifier, projectVersion, timestamp, snapshot ).
            generateControlFile();
        assertEquals( upstreamVersion, controlFile.getVersion().upstreamVersion );
        assertEquals( maintainerRevision, controlFile.getVersion().maintainerRevision );
        assertEquals( timestamp, controlFile.getVersion().timestamp );
        assertEquals( packageName, controlFile.getPackageName() );
    }

    public void testDependsGeneration()
        throws Exception
    {
        DebTool debTool = new DebTool();

        assertNull( debTool.generateControlFile().getDepends() );

        VersionRange v1_0 = VersionRange.createFromVersionSpec( "1.0" );

        debTool.dependencies( new HashSet( Arrays.asList( new DebianDependency[]{new DebianDependency(
            new DefaultArtifact( "groupId", "artifactId", v1_0, "runtime", "dpk", "dpkg", null ) )} ) ) );
    }
}
