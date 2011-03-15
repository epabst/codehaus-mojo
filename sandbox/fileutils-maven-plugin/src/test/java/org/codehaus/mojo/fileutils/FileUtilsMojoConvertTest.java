package org.codehaus.mojo.fileutils;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author John Tolentino <jtolentino@gmail.com>
 */
public class FileUtilsMojoConvertTest
    extends AbstractMojoTestCase
{
    FileUtilsMojo mojo;

    private static File convertPom =
        new File( getBasedir(), "target/test-classes/unit/conversion-test/SampleProject/m2/conversion-config.xml" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void testSample()
        throws Exception
    {
        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoConvertTest.convertPom );

        assertNotNull( mojo );

        deleteDirectory( "target/test-classes/unit/conversion-test/SampleProject/m2/src" );

        assertFalse( checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src" ) );

        mojo.execute();

        assertTrue( "can't convert java source directory",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/java" ) );

        assertTrue( "can't convert resources directory",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/resources" ) );

        assertTrue( "can't convert webapp directory",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/webapp" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/java/org/apache/maven/portlet/QuotePortlet.java" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/resources/quotes.xml" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/webapp/view.jsp" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/webapp/WEB-INF/tld/portlet.tld" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/webapp/WEB-INF/jetspeed/web.xml" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/webapp/WEB-INF/web.xml" ) );

        assertTrue( "can't copy file",
                    checkExists( "target/test-classes/unit/conversion-test/SampleProject/m2/src/main/webapp/WEB-INF/portlet.xml" ) );
    }

    private boolean checkExists( String target )
    {
        return ( new File( getBasedir(), target ).exists() );
    }

    private void deleteDirectory( String targetFolder )
        throws Exception
    {
        FileUtils.deleteDirectory( new File( getBasedir(), targetFolder ) );
    }

}
