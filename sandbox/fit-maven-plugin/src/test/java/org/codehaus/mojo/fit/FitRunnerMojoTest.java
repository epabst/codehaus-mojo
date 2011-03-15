package org.codehaus.mojo.fit;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

public class FitRunnerMojoTest
    extends TestCase
{
    protected String testSrcDir;

    private String sourceDirectory;

    private String outputDirectory;

    private FitRunnerMojo mojo;

    public void setUp()
        throws Exception
    {
        setTestDir();
        sourceDirectory = testSrcDir + "/org/codehaus/mojo/fit/input";
        outputDirectory = "target/fit";
        mojo = new FitRunnerMojo();
        mojo.sourceDirectory = sourceDirectory;
        mojo.sourceIncludes="**/*.html";
        mojo.outputDirectory = outputDirectory;
    }

    protected void setTestDir()
    {
        testSrcDir = System.getProperty( "test.src.dir" );
        if ( testSrcDir == null )
        {
            testSrcDir = "src/test/java";
        }
        else if ( !testSrcDir.endsWith( File.separator ) )
        {
            testSrcDir = testSrcDir + File.separator;
        }
    }

    public void testGoalCanBeExecuted()
        throws Exception
    {
        mojo.execute();
    }

    public void testCanRunWithFilePaths()
        throws Exception
    {
        String out = outputDirectory + "/Fit.html";
        mojo.run( sourceDirectory + "/Fit.html", out );
        assertOutputFile( new File( out ) );
    }

    public void testCanRunWithFiles()
        throws Exception
    {
        File out = new File( outputDirectory + "/Fit.html" );
        mojo.run( new File( sourceDirectory + "/Fit.html" ), out );
        assertOutputFile( out );
    }

    private void assertOutputFile( File out )
    {
        assertTrue( out.exists() );
        assertTrue( out.length() > 0 );
    }

    public void testFilesCanBeFiltered()
        throws Exception
    {
        String[] files = mojo.listFiles( sourceDirectory, true, "**/*.html", "**/Acc*" );
        assertEquals( Arrays.asList( new String[] { "Fit.html" } ), Arrays.asList( files ) );
    }

    public void testPathCanBeDetermined()
        throws Exception
    {
        String output = mojo.toPath( outputDirectory, "Fit.html" );
        assertEquals( outputDirectory + "/Fit.html", output.replaceAll("\\\\", "/" ) );
    }
}
