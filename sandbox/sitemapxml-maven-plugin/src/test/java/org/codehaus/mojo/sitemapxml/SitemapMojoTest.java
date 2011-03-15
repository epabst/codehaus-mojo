package org.codehaus.mojo.sitemapxml;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * Test for SitemapMojo
 * <p>
 * Documentation on testing plugins: http://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html
 * <p>
 * 
 * @author Bernhard Grünewaldt
 *
 */
public class SitemapMojoTest extends AbstractMojoTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        // required
        super.setUp();        
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        // required
        super.tearDown();
    }

    /**
     * @throws Exception if any
     */
    public void testSomething()
        throws Exception
    {
        // Read test pom and load Mojo with goal "generate"
        File pom = getTestFile( "src/test/resources/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );
        SitemapMojo testMojo = (SitemapMojo) lookupMojo( "generate", pom );
        assertNotNull( testMojo ); 
        
        // -----------------------------------
        
        // Now execute the Mojo
        testMojo.execute();
        
        // -----------------------------------
        
        // Now check if files exist and validate them
        File sitemapXml = getTestFile( "target/project-to-test/sitemap.xml" );
        assertTrue( sitemapXml.exists() );

        File sitemapXmlGz = getTestFile( "target/project-to-test/sitemap.xml.gz" );
        assertTrue( sitemapXmlGz.exists() );      


    }
}
