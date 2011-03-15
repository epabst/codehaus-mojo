package org.codehaus.mojo.naturaldocs;

/*
 * The MIT License
 * 
 * Copyright (c) 2008, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

/**
 * Tests for the Natural Docs Mojo
 * 
 * @author <a href="mailto:timothy.astle@caris.com">Tim Astle</a>
 */
public class NaturalDocsMojoTest
{
    /**
     * A path to use in testing. FIXME: Should you assume a Windows path here?
     */
    private final String naturalDocsScriptPath = "C:\\naturaldocs-1.5\\NaturalDocs";

    /**
     * A directory to use in testing.
     */
    private final File tmpdir = new File( System.getProperty( "java.io.tmpdir" ) );

    /**
     * Generate a dummy Natural Docs command for use in testing.
     * 
     * @return A basic Natural Docs command
     */
    private String basicCommand()
    {
        return String.format( "perl \"%s\" -i \"%s\" -o HTML \"%s\" -p \"%s\"", this.naturalDocsScriptPath,
                              this.tmpdir, this.tmpdir, this.tmpdir );
    }

    /**
     * Generate a dummy Natural Docs command for use in testing.
     * 
     * @return A complex Natural Docs command
     */
    private String complexCommand()
    {
        return String.format( this.basicCommand()
                                  + " -xi \"%s\" -img \"%s\" -s abc.css xyz.css -r -ro -t 5 -hl All -do -oft -nag -cs UTF-8 -q",
                              this.tmpdir,
                              this.tmpdir );
    }

    /**
     * Verify that an exception is thrown when the highlight is correct.
     */
    @Test
    public void testCheckConfigHighlightCorrect()
    {
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = this.tmpdir;
        ndmj.input = this.tmpdir;
        ndmj.project = this.tmpdir;
        ndmj.outputFormat = "HTML";
        ndmj.highlight = "Off";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            fail( "Exception should not have been thrown." );
        }
        ndmj.highlight = "Code";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            fail( "Exception should not have been thrown." );
        }
        ndmj.highlight = "All";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            fail( "Exception should not have been thrown." );
        }
    }

    /**
     * Verify that an exception is thrown when the highlight is incorrect.
     */
    @Test
    public void testCheckConfigHighlightIncorrect()
    {
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = this.tmpdir;
        ndmj.input = this.tmpdir;
        ndmj.project = this.tmpdir;
        ndmj.outputFormat = "HTML";
        ndmj.highlight = "The Lost Skeleton of Cadavera";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            return;
        }
        fail( "Expected exception." );
    }

    /**
     * Verify that an exception is thrown when the input directory is not a directory.
     */
    @Test
    public void testCheckConfigInputIsNotADirectory()
        throws IOException
    {
        File testFile = File.createTempFile( "lost", "skeleton" );
        testFile.deleteOnExit();
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = this.tmpdir;
        ndmj.input = testFile;
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            return;
        }
        fail( "Expected exception." );
    }

    /**
     * Verify that an exception is thrown when the Natural Docs directory is not a directory.
     */
    @Test
    public void testCheckConfigNaturalDocsHomeIsNotADirectory()
        throws IOException
    {
        File testFile = File.createTempFile( "lost", "skeleton" );
        testFile.deleteOnExit();
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = testFile;
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            return;
        }
        fail( "Expected exception." );
    }

    /**
     * Verify that an exception is thrown when the output format is correct.
     */
    @Test
    public void testCheckConfigOutputFormatCorrect()
    {
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = this.tmpdir;
        ndmj.input = this.tmpdir;
        ndmj.project = this.tmpdir;
        ndmj.outputFormat = "HTML";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            fail( "Exception should not have been thrown." );
        }
        ndmj.outputFormat = "FramedHTML";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            fail( "Exception should not have been thrown." );
        }
    }

    /**
     * Verify that an exception is thrown when the output format is incorrect.
     */
    @Test
    public void testCheckConfigOutputFormatIncorrect()
    {
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = this.tmpdir;
        ndmj.input = this.tmpdir;
        ndmj.project = this.tmpdir;
        ndmj.outputFormat = "The Lost Skeleton of Cadavera";
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            return;
        }
        fail( "Expected exception." );
    }

    /**
     * Verify that an exception is thrown when the project directory is not a directory.
     */
    @Test
    public void testCheckConfigProjectIsNotADirectory()
        throws IOException
    {
        File testFile = File.createTempFile( "lost", "skeleton" );
        testFile.deleteOnExit();
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.naturalDocsHome = this.tmpdir;
        ndmj.input = new File( System.getProperty( "java.io.tmpdir" ) );
        ndmj.project = testFile;
        try
        {
            ndmj.checkConfig();
        }
        catch ( MojoExecutionException e )
        {
            return;
        }
        fail( "Expected exception." );
    }

    /**
     * Test a complex natural docs command.
     */
    @Test
    public void testComplexCreateNaturalDocsCommand()
    {
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.input = this.tmpdir;
        ndmj.output = this.tmpdir;
        ndmj.project = this.tmpdir;
        ndmj.outputFormat = "HTML";
        ndmj.excludeImport = this.tmpdir;
        ndmj.images = this.tmpdir;
        ndmj.style = new ArrayList<String>();
        ndmj.style.add( "abc.css" );
        ndmj.style.add( "xyz.css" );
        ndmj.rebuild = true;
        ndmj.rebuildOutput = true;
        ndmj.tabLength = 5;
        ndmj.highlight = "All";
        ndmj.documentedOnly = true;
        ndmj.onlyFileTitles = true;
        ndmj.noAutoGroup = true;
        ndmj.characterSet = "UTF-8";
        ndmj.quiet = true;

        // TODO: Use assertEquals instead - easier to debug when checking equality.
        assertTrue( this.complexCommand().equals( ndmj.createNaturalDocsCommand( naturalDocsScriptPath ) ) );
    }

    /**
     * Test a basic natural docs command.
     */
    @Test
    public void testSimpleCreateNaturalDocsCommand()
    {
        NaturalDocsMojo ndmj = new NaturalDocsMojo();
        ndmj.input = this.tmpdir;
        ndmj.output = this.tmpdir;
        ndmj.project = this.tmpdir;
        ndmj.outputFormat = "HTML";

        // TODO: Use assertEquals instead - easier to debug when checking equality.
        assertTrue( this.basicCommand().equals( ndmj.createNaturalDocsCommand( naturalDocsScriptPath ) ) );
    }
}
