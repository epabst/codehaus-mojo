package org.codehaus.mojo.fileutils;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author John Tolentino <jtolentino@gmail.com>
 */
public class FileUtilsMojoDeleteTest
    extends AbstractMojoTestCase
{
    FileUtilsMojo mojo;

    private static File deleteFilePom =
        new File( getBasedir(), "target/test-classes/unit/basic-test/deletefile-config.xml" );

    private static File deleteDirPom =
        new File( getBasedir(), "target/test-classes/unit/basic-test/deletedir-config.xml" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * tests the delete file script
     *
     * @throws Exception
     */
    public void testDeleteFiles()
        throws Exception
    {

        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoDeleteTest.deleteFilePom );

        assertNotNull( mojo );

        assertTrue( "can't find target/test-classes/unit/basic-test/test-files/sample1.txt",
                    checkExists( "target/test-classes/unit/basic-test/test-files/sample1.txt" ) );
        assertTrue( "can't find target/test-classes/unit/basic-test/test-files/sample2.txt",
                    checkExists( "target/test-classes/unit/basic-test/test-files/sample2.txt" ) );

        FileUtils.copyDirectory( new File( getBasedir(), "target/test-classes/unit/basic-test/test-files" ),
                                 new File( getBasedir(), "target/test" ) );

        assertTrue( "can't find target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertTrue( "can't find target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

        mojo.execute();

        assertFalse( "can't delete target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertFalse( "can't delete target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

    }

    /**
     * tests the delete directory script
     *
     * @throws Exception
     */
    public void testDeleteDirectoryFiles()
        throws Exception
    {

        mojo = (FileUtilsMojo) lookupMojo( "fileutils", FileUtilsMojoDeleteTest.deleteDirPom );

        assertNotNull( mojo );

        assertTrue( "can't find target/test-classes/unit/basic-test/test-files/sample1.txt",
                    checkExists( "target/test-classes/unit/basic-test/test-files/sample1.txt" ) );
        assertTrue( "can't find target/test-classes/unit/basic-test/test-files/sample2.txt",
                    checkExists( "target/test-classes/unit/basic-test/test-files/sample2.txt" ) );

        FileUtils.copyDirectory( new File( getBasedir(), "target/test-classes/unit/basic-test/test-files" ),
                                 new File( getBasedir(), "target/test" ) );

        assertTrue( "can't find target/test/sample1.txt", checkExists( "target/test/sample1.txt" ) );
        assertTrue( "can't find target/test/sample2.txt", checkExists( "target/test/sample2.txt" ) );

        mojo.execute();

        assertFalse( "can't delete target/test", checkExists( "target/test" ) );

    }

    private boolean checkExists( String target )
    {
        return ( new File( getBasedir(), target ).exists() );
    }

}
