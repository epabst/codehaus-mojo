package org.codehaus.mojo.apt.it;

/*
 * The MIT License
 *
 * Copyright 2006-2008 The Codehaus.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.codehaus.mojo.apt.CollectionUtils;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * Runs the integration tests for an apt mojo.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public abstract class AbstractAptMojoTest extends PlexusTestCase
{
    // tests ------------------------------------------------------------------

    public void testAptBasic() throws VerificationException
    {
        executeProject( "apt-basic-test" );
        assertFileExists( "apt-basic-test", getTargetPath() + getClassPrefix() + "Class.txt" );
    }

    public void testAptSource() throws VerificationException
    {
        executeProject( "apt-source-test" );
        assertFileExists( "apt-source-test", getTargetSourcePath() + getClassPrefix() + "ClassApt.java" );
    }

    public void testAptIncludes() throws VerificationException
    {
        executeProject( "apt-includes-test" );
        assertFileExists( "apt-includes-test", getTargetPath() + getClassPrefix() + "Class.txt" );
        assertNotFileExists( "apt-includes-test", getTargetPath() + getClassPrefix() + "ClassExcluded.txt" );
    }
    
    public void testAptAdditionalSourceRoots() throws VerificationException
    {
        executeProject( "apt-source-roots-test" );
        assertFileExists( "apt-source-roots-test", getTargetPath() + getClassPrefix() + "Class.txt" );
        assertFileExists( "apt-source-roots-test", getTargetPath() + getClassPrefix() + "Class2.txt" );
    }
    
    public void testAptStaleSuffix() throws VerificationException
    {
        testAptStale( "apt-stale-suffix-test", getTargetPath() + getClassPrefix() + "Class.txt" );
    }
    
    public void testAptStalePath() throws VerificationException, FileNotFoundException, IOException
    {
        testAptStale( "apt-stale-path-test", getTargetPath() + "generated.txt" );

        // ensure all source files processed, not just stale ones
        File targetFile = getProjectFile( "apt-stale-path-test", getTargetPath() + "generated.txt" );
        assertLine( getClassPrefix() + "Class", targetFile );
        assertLine( getClassPrefix() + "Class2", targetFile );
    }
    
    public void testAptForked() throws VerificationException
    {
        executeProject( "apt-fork-test" );
        assertFileExists( "apt-fork-test", getTargetPath() + getClassPrefix() + "Class.txt" );
    }

    public void testAptPluginDependency() throws VerificationException
    {
        executeProject( "apt-plugin-dependency-test" );
        assertFileExists( "apt-plugin-dependency-test", getTargetPath() + getClassPrefix() + "Class.txt" );
    }

    public void testAptPluginDependencyWithExclusionOfToolsJar() throws VerificationException
    {
        executeProject( "apt-plugin-dependency-with-exclusion" );
        assertFileExists( "apt-plugin-dependency-with-exclusion", getTargetPath() + getClassPrefix() + "Class.txt" );
    }

    public void testAptSkip() throws VerificationException
    {
        executeProject( "apt-skip-test" );
        assertNotFileExists( "apt-skip-test", getTargetPath() + getClassPrefix() + "Class.txt" );
    }

    // protected methods ------------------------------------------------------

    protected abstract String getGoal();

    protected abstract String getSourcePath();
    
    protected abstract String getTargetPath();
    
    protected abstract String getTargetSourcePath();
    
    protected abstract String getClassPrefix();
    
    // private methods --------------------------------------------------------

    private void testAptStale( String projectName, String targetPath ) throws VerificationException
    {
        File basedir = getProjectFile( projectName );

        Verifier verifier = new Verifier( basedir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.executeGoals( Arrays.asList( "clean", getGoal() ) );
        verifier.verifyErrorFreeLog();

        File targetFile = getProjectFile( projectName, targetPath );
        assertFileExists( targetFile );
        long lastModified = targetFile.lastModified();

        // ensure target unmodified
        verifier.executeGoal( getGoal() );
        verifier.verifyErrorFreeLog();
        assertTrue( "Expected output file to be unmodified", targetFile.lastModified() == lastModified );

        // touch source
        File sourceFile = getProjectFile( projectName, getSourcePath() + getClassPrefix() + "Class.java" );
        sourceFile.setLastModified( lastModified + 1000 );

        // ensure target modified
        verifier.executeGoal( getGoal() );
        verifier.verifyErrorFreeLog();
        assertFalse( "Expected output file to be modified", targetFile.lastModified() == lastModified );

        verifier.resetStreams();
    }
    
    private void executeProject( String projectName ) throws VerificationException
    {
        File basedir = getProjectFile( projectName );

        Verifier verifier = new Verifier( basedir.getAbsolutePath() );
        verifier.executeGoal( getGoal() );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

    private static void assertFileExists( String projectName, String path )
    {
        assertFileExists( getProjectFile( projectName, path ) );
    }

    private static void assertFileExists( File file )
    {
        assertTrue( "Expected file: " + file, file.exists() );
    }

    private static void assertNotFileExists( String projectName, String path )
    {
        assertNotFileExists( getProjectFile( projectName, path ) );
    }

    private static void assertNotFileExists( File file )
    {
        assertFalse( "Unexpected file: " + file, file.exists() );
    }
    
    private static void assertLine( String line, File file ) throws FileNotFoundException, IOException
    {
        List<String> lines = CollectionUtils.genericList( FileUtils.loadFile( file ), String.class );

        assertTrue( "Expected line '" + line + "' in file: " + file, lines.contains( line ) );
    }

    private static File getProjectFile( String projectName )
    {
        return getProjectFile( projectName, "" );
    }
    
    private static File getProjectFile( String projectName, String path )
    {
        return getTestFile( "target/test-classes/it/projects/" + projectName + "/" + path );
    }
}
