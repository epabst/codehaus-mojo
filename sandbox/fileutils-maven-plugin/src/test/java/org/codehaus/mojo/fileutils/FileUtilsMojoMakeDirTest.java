package org.codehaus.mojo.fileutils;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author John Tolentino <jtolentino@gmail.com>
 */
public class FileUtilsMojoMakeDirTest
    extends AbstractMojoTestCase
{
    FileUtilsMojo mojo;

    private static File makeDirectoryPom =
        new File( getBasedir(), "target/test-classes/unit/basic-test/mkdir-config.xml" );

    private static File makeNestedDirectoryPom =
        new File( getBasedir(), "target/test-classes/unit/basic-test/mkdirnested-config.xml" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * tests the make directory script
     *
     * @throws Exception
     */
    public void testMakeDir()
        throws Exception
    {

        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoMakeDirTest.makeDirectoryPom );

        assertNotNull( mojo );

        deleteDirectory( "target/test" );

        mojo.execute();

        assertTrue( "can't create target/test", checkExists( "target/test" ) );

    }

    /**
     * tests the make directory script with a non-existing parent directory
     *
     * @throws Exception
     */
    public void testMakeDirNested()
        throws Exception
    {

        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoMakeDirTest.makeNestedDirectoryPom );

        assertNotNull( mojo );

        deleteDirectory( "target/test" );

        mojo.execute();

        assertTrue( "can't create target/test", checkExists( "target/test/nested" ) );

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
