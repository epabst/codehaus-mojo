package org.codehaus.mojo.fileutils;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author John Tolentino <jtolentino@gmail.com>
 */
public class FileUtilsMojoCopyTest
    extends AbstractMojoTestCase
{
    FileUtilsMojo mojo;

    private static File copyFilePom =
        new File( getBasedir(), "target/test-classes/unit/basic-test/copyfile-config.xml" );

    private static File copyDirPom = new File( getBasedir(), "target/test-classes/unit/basic-test/copydir-config.xml" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * tests the copy file script
     *
     * @throws Exception
     */
    public void testCopyFiles()
        throws Exception
    {

        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoCopyTest.copyFilePom );

        assertNotNull( mojo );

        assertTrue( "can't find target/test-classes/unit/basic-test/test-files/sample1.txt",
                    checkExists( "target/test-classes/unit/basic-test/test-files/sample1.txt" ) );
        assertTrue( "can't find target/test-classes/unit/basic-test/test-files/sample2.txt",
                    checkExists( "target/test-classes/unit/basic-test/test-files/sample2.txt" ) );

        deleteDirectory( "target/test" );

        assertFalse( "can't delete target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertFalse( "can't delete target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

        mojo.execute();

        assertTrue( "can't find target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertTrue( "can't find target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

    }

    /**
     * tests the copy script with directories
     *
     * @throws Exception
     */
    public void testCopyDirectory()
        throws Exception
    {

        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoCopyTest.copyDirPom );

        assertNotNull( mojo );

        deleteDirectory( "target/test" );

        assertFalse( "can't delete target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertFalse( "can't delete target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

        mojo.execute();

        assertTrue( "can't find target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertTrue( "can't find target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

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
