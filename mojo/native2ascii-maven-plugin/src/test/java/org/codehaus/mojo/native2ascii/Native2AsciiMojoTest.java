package org.codehaus.mojo.native2ascii;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

public class Native2AsciiMojoTest
    extends TestCase
{
    private Native2AsciiMojo mojo;

    private File testFile;

    protected void setUp()
        throws Exception
    {
        mojo = new Native2AsciiMojo();
        mojo.src = new File( "src/test/data" );
        mojo.dest = new File( "target/native2ascii" );

        testFile = new File( mojo.dest, "messages.properties" );
        FileUtils.fileDelete( testFile.getAbsolutePath() );

        assertFalse( testFile.getAbsolutePath() + " exists.", testFile.exists() );

    }

    public void testDefaultEncoding()
        throws Exception
    {
        mojo.executeAnt();

        assertTrue( testFile.getAbsolutePath() + " exists.", mojo.dest.exists() );

    }

    public void testBadEncoding()
        throws Exception
    {

        mojo.encoding = "UTF18";

        try
        {
            mojo.executeAnt();
            fail( "Expected IOException not found" );
        }
        catch ( MojoFailureException e )
        {
        }

    }

    public void testNative2Ascii()
        throws Exception
    {
        mojo.encoding = "UTF8";
        mojo.executeAnt();

        assertTrue( testFile.getAbsolutePath() + " exists.", mojo.dest.exists() );
    }

    public void testFailonErrorFalse()
        throws Exception
    {
        mojo.failonerror = false;
        mojo.src = new File( "notAvailableSrc" );

        mojo.executeAnt();
    }

    public void testFailonErrorTrue()
        throws Exception
    {
        mojo.src = new File( "notAvailableSrc" );

        try
        {
            mojo.executeAnt();
            fail( "Expected IOException not found" );
        }
        catch ( MojoFailureException e )
        {
        }
    }
}
