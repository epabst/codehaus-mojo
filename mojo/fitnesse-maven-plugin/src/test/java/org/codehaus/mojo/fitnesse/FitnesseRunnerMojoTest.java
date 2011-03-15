package org.codehaus.mojo.fitnesse;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.codehaus.mojo.fitnesse.log.FileConsumer;
import org.codehaus.mojo.fitnesse.log.FitnesseStreamConsumer;
import org.codehaus.mojo.fitnesse.log.LogConsumer;
import org.codehaus.mojo.fitnesse.log.MultipleConsumer;
import org.codehaus.mojo.fitnesse.plexus.FCommandLineException;
import org.codehaus.mojo.fitnesse.plexus.FCommandline;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class FitnesseRunnerMojoTest
    extends MockObjectTestCase
{
    private FitnesseRunnerMojo mMojo = null;

    private Mock mMockLog = null;

    public void testCheckConfigurationOk()
        throws MojoFailureException, MojoExecutionException
    {
        mMojo.checkConfiguration();
    }

    public void testCheckConfigurationClassPathProviderOk()
        throws MojoFailureException, MojoExecutionException
    {
        mMojo.setClassPathProvider( "fitnesse" );
        mMojo.checkConfiguration();
        mMojo.setClassPathProvider( "maven" );
        mMojo.checkConfiguration();
    }

    public void testCheckConfigurationClassPathProviderKo()
        throws MojoFailureException, MojoExecutionException
    {

        try
        {
            mMojo.setClassPathProvider( null );
            mMojo.checkConfiguration();
            fail( "Should fail" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "classPathProvider accepts only \"fitnesse\" ou \"maven\" values. [null] is not valid.",
                          e.getMessage() );
        }
        try
        {
            mMojo.setClassPathProvider( "invalid" );
            mMojo.checkConfiguration();
            fail( "Should fail" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "classPathProvider accepts only \"fitnesse\" ou \"maven\" values. [invalid] is not valid.",
                          e.getMessage() );
        }
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        mMojo = getMojo();
    }

    private FitnesseRunnerMojo getMojo()
    {
        mMojo = new FitnesseRunnerMojo();
        mMojo.setFailOnError( false );
        mMojo.setDebug( false );
        mMojo.setFailOnError( false );
        mMojo.setClassPathProvider( "fitnesse" );
        mMojo.setPluginArtifacts( new ArrayList() );
        mMojo.setJdk( "java" );
        mMojo.setWorkingDir( "target/fitnesse" );
        mMojo.setFitnesseRunnerClass( "fitnesse.runner.TestRunner" );
        mMojo.setPluginArtifact( getArtifact() );

        List tFitnesses = new ArrayList();
        tFitnesses.add( new Fitnesse() );
        mMojo.setFitnesses( tFitnesses );

        mMojo.setClassPathSubstitions( new ArrayList() );

        mMockLog = mock( Log.class );
        mMockLog.stubs().method( "info" ).withAnyArguments();
        mMojo.setLog( (Log) mMockLog.proxy() );
        mMojo.setDateFormat( "dd/MM/yyyy HH:mm" );
        return mMojo;
    }

    public void testCheckConfigurationWithoutFitnesseProject()
        throws MojoFailureException
    {
        String errorMessage =
            "Your should configure at least one Fitnesse server. " + "Check your maven-fitnesse-plugin configuration.";
        mMockLog.stubs().method( "error" ).with( eq( errorMessage ) );

        mMojo.setFitnesses( null );
        try
        {
            mMojo.checkConfiguration();
            fail( "Fitnesses addresses are required..." );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( errorMessage, e.getMessage() );
        }
        mMojo.setFitnesses( new ArrayList() );
        try
        {
            mMojo.checkConfiguration();
            fail( "Fitnesses addresses are required..." );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( errorMessage, e.getMessage() );
        }
    }

    public void testCheckConfigurationWithOneFitnesseProject()
        throws MojoFailureException
    {
        try
        {
            mMojo.setFitnesseRunnerClass( "fitnesseRunner" );
            mMojo.checkConfiguration();
            fail( "Should not pass" );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( !e.getMessage().startsWith( "Fitnesses addresses are required" ) );
        }
    }

    public void testCheckConfigurationWithBadRunnerClassName()
        throws MojoFailureException
    {
        mMojo.setFitnesseRunnerClass( "badName" );
        try
        {
            mMojo.checkConfiguration();
            fail( "Should not pass" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals(
                          "The class [badName] could not be found, check your maven-fitnesse-plugin configuration and the plugin documentation.",
                          e.getMessage() );
        }

        mMojo.setFitnesseRunnerClass( this.getClass().getName() );
        try
        {
            mMojo.checkConfiguration();
            fail( "Should not pass" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals(
                          "The class [org.codehaus.mojo.fitnesse.FitnesseRunnerMojoTest] doesn't have a \"main\" accessible method.",
                          e.getMessage() );
        }
    }

    static class TestFitnesseRunner
    {
        public void main( String[] params )
        {
        }
    }

    public void testCheckConfigurationWithGoodRunnerClassName()
        throws MojoFailureException, MojoExecutionException
    {
        mMojo.setFitnesseRunnerClass( TestFitnesseRunner.class.getName() );
        mMojo.checkConfiguration();
    }

    static class MyFile
        extends File
    {
        private static final long serialVersionUID = -4387400221288014456L;

        public MyFile()
        {
            super( "bidon.jar" );
        }

        public String getAbsolutePath()
        {
            return "bidon.jar";
        }
    }

    private Artifact getArtifact()
    {
        Artifact tArtif =
            new DefaultArtifact( "junit", "junit", VersionRange.createFromVersion( "3.8.1" ), "test", "jar", null,
                                 new DefaultArtifactHandler() );
        tArtif.setFile( new MyFile() );
        return tArtif;
    }

    public void testPrepareCommandLine()
        throws MojoExecutionException
    {

        assertEquals(
                      "java -cp bidon.jar"
                          + File.pathSeparatorChar
                          + " fitnesse.runner.TestRunner -v -html "
                          + "target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject_tmp.html -nopath localhost 80 MustBeDefinedByProject",
                      mMojo.prepareCommandLine( mMojo.getFitnesse( 0 ), "bidon.jar" + File.pathSeparatorChar ).toString() );

        mMojo.setDebug( true );
        assertEquals(
                      "java -cp bidon.jar"
                          + File.pathSeparatorChar
                          + " fitnesse.runner.TestRunner -v -debug "
                          + "-html target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject_tmp.html -nopath localhost 80 MustBeDefinedByProject",
                      mMojo.prepareCommandLine( mMojo.getFitnesse( 0 ), "bidon.jar" + File.pathSeparatorChar ).toString() );

        mMojo.setGenerateXml( true );
        assertEquals(
                      "java -cp bidon.jar" + File.pathSeparatorChar + " fitnesse.runner.TestRunner -v -debug "
                          + "-html target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject_tmp.html "
                          + "-xml target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject.xml "
                          + "-nopath localhost 80 MustBeDefinedByProject",
                      mMojo.prepareCommandLine( mMojo.getFitnesse( 0 ), "bidon.jar" + File.pathSeparatorChar ).toString() );
        
        mMojo.getFitnesse( 0 ).setSuiteFilter( "" );
        assertEquals(
                     "java -cp bidon.jar" + File.pathSeparatorChar + " fitnesse.runner.TestRunner -v -debug "
                         + "-html target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject_tmp.html "
                         + "-xml target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject.xml "
                         + "-nopath localhost 80 MustBeDefinedByProject",
                     mMojo.prepareCommandLine( mMojo.getFitnesse( 0 ), "bidon.jar" + File.pathSeparatorChar ).toString() );
        
        mMojo.getFitnesse( 0 ).setSuiteFilter( "MyFilter" );
        assertEquals(
                      "java -cp bidon.jar" + File.pathSeparatorChar + " fitnesse.runner.TestRunner -v -debug "
                          + "-html target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject_tmp.html "
                          + "-xml target/fitnesse/fitnesseResult_localhost_MustBeDefinedByProject.xml "
                          + "-nopath -suiteFilter MyFilter localhost 80 MustBeDefinedByProject",
                      mMojo.prepareCommandLine( mMojo.getFitnesse( 0 ), "bidon.jar" + File.pathSeparatorChar ).toString() );

    }

    public void testExecuteCommandWithFailure()
        throws MojoExecutionException, MojoFailureException
    {
        mMojo.setFailOnError( true );
        new File( "target/fitnesse" ).mkdirs();
        Mock tMockProcess = mock( Process.class );
        byte[] tInputByte =
            ( "TestSimpleClass1 has failures\n"
                + "Test Pages: 0 right, 1 wrong, 0 ignored, 0 exceptions\n"
                + "Assertions: 4 right, 1 wrong, 0 ignored, 0 exceptions\n"
                + "Formatting as html to D:\\SCM\\ProjectSVN\\maven-fitnesse-plugin\\src\\it\\multiproject\\target/fitnesse/fitnesseResultSuiteCoverage2.html" ).getBytes();
        tMockProcess.expects( once() ).method( "getInputStream" ).will(
                                                                        returnValue( new ByteArrayInputStream(
                                                                                                               tInputByte ) ) );
        tMockProcess.expects( once() ).method( "getErrorStream" ).will(
                                                                        returnValue( new ByteArrayInputStream(
                                                                                                               new byte[0] ) ) );
        tMockProcess.expects( once() ).method( "waitFor" ).will( returnValue( 2 ) );
        try
        {
            mMojo.executeCommand( mMojo.getFitnesse( 0 ), new MockCommandLine( (Process) tMockProcess.proxy() ) );
            fail( "Should fail" );
        }
        catch ( MojoFailureException e )
        {
            assertEquals( "Fitnesse command ended with errors, exit code:2", e.getMessage() );
        }
        verify();

        tMockProcess = mock( Process.class );
        tMockProcess.expects( once() ).method( "getInputStream" ).will(
                                                                        returnValue( new ByteArrayInputStream(
                                                                                                               tInputByte ) ) );
        tMockProcess.expects( once() ).method( "getErrorStream" ).will(
                                                                        returnValue( new ByteArrayInputStream(
                                                                                                               new byte[0] ) ) );
        tMockProcess.expects( once() ).method( "waitFor" ).will( returnValue( 2 ) );
        mMojo.setFailOnError( false );
        mMojo.executeCommand( mMojo.getFitnesse( 0 ), new MockCommandLine( (Process) tMockProcess.proxy() ) );
        verify();
    }

    public void testExecuteCommandWithoutError()
        throws MojoExecutionException, MojoFailureException, FCommandLineException
    {
        mMojo.setFailOnError( true );
        new File( "target/fitnesse" ).mkdirs();
        FCommandline tCmd = new FCommandline();
        tCmd.setExecutable( "java" );
        tCmd.createArgument().setValue( "-version" );

        mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
        verify();

        mMojo.setFailOnError( false );
        mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
        verify();

    }

    public void testExecuteCommandWithError()
        throws MojoExecutionException, MojoFailureException, FCommandLineException
    {
        mMojo.setFailOnError( true );
        new File( "target/fitnesse" ).mkdirs();
        FCommandline tCmd = new FCommandline();
        tCmd.setExecutable( "java" );
        tCmd.createArgument().setValue( "totalInvalide" );
        try
        {
            mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
            fail( "Should throw a MojoExecutionException" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Unable to run Fitnesse, exit code [1]", e.getMessage() );
        }
        verify();

        mMojo.setFailOnError( false );
        try
        {
            mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
            fail( "Should throw a MojoExecutionException" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Unable to run Fitnesse, exit code [1]", e.getMessage() );
        }
        verify();
    }

    public void testExecuteCommandWithFileOutputAndNoDisplayAndFailOnError()
        throws MojoExecutionException, MojoFailureException, FCommandLineException
    {
        FCommandline tCmd = createCmdLine();

        mMojo.setDisplayOutput( false );
        mMojo.setFailOnError( true );
        File tFile = new File( mMojo.getOutputFileName( mMojo.getFitnesse( 0 ) ) );

        tFile.delete();
        mMockLog = mock( Log.class );
        assertFalse( tFile.exists() );
        mMockLog.expects( once() ).method( "info" ).withAnyArguments();
        mMojo.setLog( (Log) mMockLog.proxy() );
        mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
        verify();
        assertTrue( tFile.exists() );
        assertTrue( 50 < tFile.length() );
        assertTrue( tFile.delete() );
    }

    public void testExecuteCommandWithFileOutputAndNoDisplayAndNoFailOnError()
        throws MojoExecutionException, MojoFailureException, FCommandLineException
    {
        FCommandline tCmd = createCmdLine();

        mMojo.setDisplayOutput( false );
        mMojo.setFailOnError( false );
        File tFile = new File( mMojo.getOutputFileName( mMojo.getFitnesse( 0 ) ) );
        tFile.delete();
        assertFalse( tFile.exists() );
        mMockLog = mock( Log.class );
        mMockLog.expects( once() ).method( "info" ).withAnyArguments();
        mMojo.setLog( (Log) mMockLog.proxy() );
        mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
        verify();
        assertTrue( tFile.exists() );
        assertTrue( 50 < tFile.length() );
        assertTrue( tFile.delete() );
    }

    public void testExecuteCommandWithFileOutputAndDisplayAndFailOnError()
        throws MojoExecutionException, MojoFailureException, FCommandLineException
    {
        FCommandline tCmd = createCmdLine();

        mMojo.setDisplayOutput( true );
        mMojo.setFailOnError( true );
        File tFile = new File( mMojo.getOutputFileName( mMojo.getFitnesse( 0 ) ) );
        tFile.delete();
        assertFalse( tFile.exists() );
        mMojo.setFailOnError( true );
        mMojo.setDisplayOutput( true );
        mMockLog = mock( Log.class );
        mMockLog.expects( once() ).method( "info" ).withAnyArguments();
        mMockLog.expects( once() ).method( "error" ).with( stringContains( "java version" ) );
        mMockLog.expects( once() ).method( "error" ).with( stringContains( "Java" ) );
        mMojo.setLog( (Log) mMockLog.proxy() );
        mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
        verify();
        assertTrue( tFile.exists() );
        assertTrue( 50 < tFile.length() );
        assertTrue( tFile.delete() );
    }

    public void testExecuteCommandWithFileOutputAndDisplayAndNoFailOnError()
        throws MojoExecutionException, MojoFailureException, FCommandLineException
    {
        FCommandline tCmd = createCmdLine();

        mMojo.setDisplayOutput( true );
        mMojo.setFailOnError( false );
        File tFile = new File( mMojo.getOutputFileName( mMojo.getFitnesse( 0 ) ) );
        assertFalse( tFile.exists() );
        mMojo.setFailOnError( false );
        mMojo.setDisplayOutput( true );
        mMockLog = mock( Log.class );
        mMockLog.expects( once() ).method( "info" ).withAnyArguments();
        mMockLog.expects( once() ).method( "error" ).with( stringContains( "java version" ) );
        mMockLog.expects( once() ).method( "error" ).with( stringContains( "Java" ) );
        mMojo.setLog( (Log) mMockLog.proxy() );
        mMojo.executeCommand( mMojo.getFitnesse( 0 ), tCmd );
        assertTrue( tFile.exists() );
        assertTrue( 50 < tFile.length() );
        assertTrue( tFile.delete() );

        assertFalse( tFile.exists() );
    }

    private FCommandline createCmdLine()
    {
        FCommandline tCmd = new FCommandline();
        tCmd.setExecutable( "java" );
        tCmd.createArgument().setValue( "-version" );
        new File( "target/fitnesse" ).mkdirs();
        return tCmd;
    }

    public void testTransformResultPage()
        throws MojoExecutionException, IOException, URISyntaxException
    {
        URL tUrl =
            this.getClass().getClassLoader().getResource( "multiReport/fitnesse/fitnesseResultSuiteCoverage2.html" );
        File tOriginalFile = new File( tUrl.toURI() );
        assertTrue( tOriginalFile.exists() );
        new File( "target" ).mkdir();
        new File( "target/fitnesse" ).mkdir();
        File tSrcFile = new File( mMojo.getTmpFileName( mMojo.getFitnesse( 0 ) ) );
        if ( tSrcFile.exists() )
        {
            assertTrue( tSrcFile.delete() );
        }
        assertTrue( tSrcFile.createNewFile() );
        Project antProject = new Project();
        antProject.init();
        Copy tCopy = (Copy) antProject.createTask( "copy" );
        tCopy.setFile( tOriginalFile );
        tCopy.setOverwrite( true );
        tCopy.setTofile( tSrcFile );
        tCopy.execute();
        File tDestFile = new File( mMojo.getFinalFileName( mMojo.getFitnesse( 0 ) ) );
        if ( tDestFile.exists() )
        {
            tDestFile.delete();
        }

        assertTrue( tSrcFile.exists() );
        assertTrue( tSrcFile.length() > 100 );
        assertFalse( tDestFile.exists() );
        mMojo.transformResultPage( mMojo.getFitnesse( 0 ) );
        tDestFile = new File( mMojo.getFinalFileName( mMojo.getFitnesse( 0 ) ) );
        assertTrue( tDestFile.exists() );
        assertFalse( tSrcFile.exists() );
    }

    public void testGetStandardConsumer()
    {
        FitnesseRunnerMojo tMojo = getMojo();
        tMojo.setDisplayOutput( false );
        assertEquals( FileConsumer.class, tMojo.getStandardConsumer( getMojo().getFitnesse( 0 ) ).getClass() );

        tMojo.setDisplayOutput( true );
        FitnesseStreamConsumer tConsumer = tMojo.getStandardConsumer( getMojo().getFitnesse( 0 ) );
        assertEquals( MultipleConsumer.class, tConsumer.getClass() );
        MultipleConsumer tCons = (MultipleConsumer) tConsumer;
        assertEquals( LogConsumer.class, tCons.getLogConsumer().getClass() );
        LogConsumer tLogCons = (LogConsumer) tCons.getLogConsumer();
        assertEquals( Level.INFO, tLogCons.getLevel() );
        assertNotNull( tCons.getFileConsumer() );
    }

    public void testGetErrorConsumer()
    {
        FitnesseRunnerMojo tMojo = getMojo();
        tMojo.setDisplayOutput( false );
        FitnesseStreamConsumer tConsuler = tMojo.getStandardConsumer( getMojo().getFitnesse( 0 ) );
        assertEquals( FileConsumer.class, tMojo.getErrorConsumer( tConsuler ).getClass() );

        tMojo.setDisplayOutput( true );
        tConsuler = tMojo.getStandardConsumer( getMojo().getFitnesse( 0 ) );
        FitnesseStreamConsumer tConsumer = tMojo.getErrorConsumer( tConsuler );
        assertEquals( MultipleConsumer.class, tConsumer.getClass() );
        MultipleConsumer tCons = (MultipleConsumer) tConsumer;
        assertEquals( LogConsumer.class, tCons.getLogConsumer().getClass() );
        LogConsumer tLogCons = (LogConsumer) tCons.getLogConsumer();
        assertEquals( Level.SEVERE, tLogCons.getLevel() );
        assertNotNull( tCons.getFileConsumer() );
    }

    public void testGetSameFileConsumer()
    {
        FitnesseRunnerMojo tMojo = getMojo();

        tMojo.setDisplayOutput( false );
        FitnesseStreamConsumer tStdCons = tMojo.getStandardConsumer( tMojo.getFitnesse( 0 ) );
        FitnesseStreamConsumer tErrCons = tMojo.getErrorConsumer( tStdCons );
        assertSame( tStdCons, tErrCons );

        tMojo.setDisplayOutput( true );
        MultipleConsumer tStandard = (MultipleConsumer) tMojo.getStandardConsumer( tMojo.getFitnesse( 0 ) );
        MultipleConsumer tError = (MultipleConsumer) tMojo.getErrorConsumer( tStandard );
        assertNotSame( tStandard, tError );
        assertNotSame( tStandard.getLogConsumer(), tError.getLogConsumer() );
        assertSame( tStandard.getFileConsumer(), tError.getFileConsumer() );
    }

    public void testCopyDependenciesLocally()
        throws MojoExecutionException
    {
        FitnesseRunnerMojo tMojo = getMojo();
        String tPathSep = System.getProperty( "path.separator" );
        String tFileSep = System.getProperty( "file.separator" );

        tMojo.setCopyDependencies( true );
        File tFile = new File( "target/fitnesse/lib/junit-3.8.1.jar" );
        if ( tFile.exists() )
        {
            assertTrue( tFile.delete() );
        }
        tFile = new File( "target/fitnesse/lib/junit-3.8.2.jar" );
        if ( tFile.exists() )
        {
            assertTrue( tFile.delete() );
        }
        String tSrcPath =
            new File( "src/test/resources/jars/junit-3.8.1.jar" ).getAbsolutePath() + tPathSep
                + new File( "src/test/resources/jars/junit-3.8.2.jar" ).getAbsolutePath() + tPathSep;
        mMockLog = mock( Log.class );
        mMockLog.stubs().method( "debug" ).withAnyArguments();
        mMojo.setLog( (Log) mMockLog.proxy() );

        String tResult = tMojo.copyDependenciesLocally( tSrcPath );
        assertEquals(
                      "lib" + tFileSep + "junit-3.8.1.jar" + tPathSep + "lib" + tFileSep + "junit-3.8.2.jar" + tPathSep,
                      tResult );
        mMockLog.verify();

    }

    public void testCopyDependenciesLocallyWithInvalidJar()
        throws MojoExecutionException
    {
        FitnesseRunnerMojo tMojo = getMojo();
        String tPathSep = System.getProperty( "path.separator" );
        String tFileSep = System.getProperty( "file.separator" );

        tMojo.setCopyDependencies( true );
        File tFile = new File( "target/fitnesse/lib/junit-3.8.1.jar" );
        if ( tFile.exists() )
        {
            assertTrue( tFile.delete() );
        }
        tFile = new File( "target/fitnesse/lib/junit-3.8.2.jar" );
        if ( tFile.exists() )
        {
            assertTrue( tFile.delete() );
        }
        String tSrcPath =
            new File( "src/test/resources/jars/junit-3.8.1.jar" ).getAbsolutePath() + tPathSep
                + new File( "src/test/resources/jars/junit-3.8.234.jar" ).getAbsolutePath() + tPathSep;
        mMockLog = mock( Log.class );
        mMockLog.stubs().method( "debug" ).withAnyArguments();
        mMockLog.expects( once() ).method( "warn" ).withAnyArguments();
        mMojo.setLog( (Log) mMockLog.proxy() );

        String tResult = tMojo.copyDependenciesLocally( tSrcPath );
        assertEquals( "lib" + tFileSep + "junit-3.8.1.jar" + tPathSep, tResult );
        mMockLog.verify();
    }

}
