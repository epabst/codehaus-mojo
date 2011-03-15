package org.apache.maven.plugin.simple;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.Mojo;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;

/**
 * @author Jason van Zyl
 * @version $Revision:$
 */
public class SimpleMojoTestCase
    extends AbstractMojoTestCase
{
    public void testSimpleMojoUsingInstantitaion()
        throws Exception
    {
        File pom = new File( getBasedir(), "src/test/resources/pom.xml" );

        SimpleMojo mojo = new SimpleMojo();

        mojo = (SimpleMojo) configureMojo( mojo, "maven-simple-plugin", pom );

        assertNotNull( mojo );
    }

    public void testSimpleMojoUsingLookupWithExplicitValues()
        throws Exception
    {
        File pom = new File( getBasedir(), "src/test/resources/pom.xml" );

        SimpleMojo mojo = (SimpleMojo) lookupMojo( "org.apache.maven.plugin.simple", "maven-simple-plugin", "1.0-SNAPSHOT", "call", pom );

        assertNotNull( mojo );

        mojo.execute();
    }

    protected Mojo lookupMojo( String groupId, String artifactId, String version, String goal, File pom )
        throws Exception
    {
        PlexusConfiguration pluginConfiguration = extractPluginConfiguration( artifactId, pom );

        return lookupMojo( groupId, artifactId, version, goal, pluginConfiguration );
    }

    public void testSimpleMojoUsingLookupWithGleanedPomValues()
        throws Exception
    {
        File pom = new File( getBasedir(), "src/test/resources/pom.xml" );

        SimpleMojo mojo = (SimpleMojo) lookupMojo( "call", pom );

        assertNotNull( mojo );

        mojo.execute();
    }

}
