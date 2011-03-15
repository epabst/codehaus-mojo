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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.fitnesse.log.FileConsumer;
import org.codehaus.mojo.fitnesse.log.FitnesseStreamConsumer;
import org.codehaus.mojo.fitnesse.log.LogConsumer;
import org.codehaus.mojo.fitnesse.log.MultipleConsumer;
import org.codehaus.mojo.fitnesse.plexus.FCommandLineException;
import org.codehaus.mojo.fitnesse.plexus.FCommandLineUtils;
import org.codehaus.mojo.fitnesse.plexus.FCommandline;
import org.codehaus.mojo.fitnesse.runner.ClassPathBuilder;

/**
 * This goal uses the <code>fitnesse.runner.TestRunner</code> class for calling a remote FitNesse web page and
 * executes the <i>tests</i> or <i>suites</i> locally into a forked JVM. It's possible to define several pages and/or
 * servers.
 * 
 * @goal run
 * @requiresDependencyResolution runtime
 * @aggregator
 */
public class FitnesseRunnerMojo
    extends FitnesseAbstractMojo
{
    /**
     * This property defines how the plugin will create the classpath for running fixtures. It accepts a couple of
     * value: "fitnesse" (default) or "maven".<BR/> With "fitnesse" mode, the classpath is downloaded from the FitNesse
     * server page. Then classpath of the plugin is appended (for providing the good FitNesse implementation).<BR/>
     * With "maven" mode the classpath is only defined with the one of the project (POM).
     * 
     * @parameter default-value="fitnesse"
     */
    private String classPathProvider;

    /**
     * @component
     * @readonly
     */
    private ArtifactMetadataSource metadataSource;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     */
    private List remoteRepositories;

    /**
     * The Maven project instance for the executing project.
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    private MavenProject project;

    /**
     * Use shorter classpath. On Windows, the Win32 api doesn't allow to create a process via CMD.EXE that contains more
     * than 8192 chars. When fitnesse-maven-plugin calls the page, the classpath that refers to the maven repository may
     * easilly contains more than 8192 chars. <a
     * href="http://blogs.msdn.com/oldnewthing/archive/2003/12/10/56028.aspx">see article</a> Activating this property
     * ask the plugin to copy all the dependency in the target folder. This may create an overhead but decrease the
     * classpath size. For exemple the string
     * [E:/Maven/repository/org/springframework/spring-beans/1.2.8/spring-beans-1.2.8.jar] (81 chars) became
     * [lib/spring-beans-1.2.8.jar] (26 chars). That doesn't solve the problems but allows to use bigger classpath.
     * 
     * @parameter default-value="false"
     */
    boolean copyDependencies;

    /**
     * List of all artifacts for this plugin provided by Maven. This is used internally to get the FitnesseRunner.
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @parameter expression="${plugin.artifacts}"
     * @readonly
     * @required
     */
    private List pluginArtifacts;

    /**
     * The set of dependencies required by the project
     * 
     * @parameter default-value="${project.dependencies}"
     * @required
     * @readonly
     */
    private java.util.List dependencies;

    /**
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * Artifact resolver used to find clovered artifacts (artifacts with a clover classifier).
     * 
     * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
     * @required
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * Local maven repository.
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${plugin.artifactId}"
     * @required
     * @readonly
     */
    private String pluginArtifactId;

    /**
     * @parameter expression="${plugin.groupId}"
     * @required
     * @readonly
     */
    private String pluginGroupId;

    /**
     * @parameter expression="${plugin.version}"
     * @required
     * @readonly
     */
    private String pluginVersion;

    /**
     * Java executable.
     * 
     * @parameter default-value="java"
     */
    private String jdk;

    /**
     * Jvm arguments.
     * 
     * @parameter default-value=""
     */
    private String jdkOpts;

    /**
     * Redirect FitNesse output into Maven2 log.
     * 
     * @parameter default-value=false
     */
    private boolean displayOutput;

    /**
     * Run FitnesseRunner with debug option.
     * 
     * @parameter default-value="false"
     */
    private boolean debug;

    /**
     * Generate Xml file with results. Needed for integrating with CruiseControl
     * 
     * @parameter default-value="false"
     */
    private boolean generateXml;

    /**
     * Fitnesse runner class.
     * 
     * @parameter default-value="fitnesse.runner.TestRunner"
     */
    private String fitnesseRunnerClass;

    /**
     * List of Classpath substitution. Substitutions allow to change the FitNesse class path.<BR/> It should be usefull
     * when the server classpath is in a Unix syntaxe, or when libs are not located in the same folder on the server and
     * on the developer desktop.<BR/> The order of substitutions is guaranteed to be the same than the definition.
     * <BR/> The substitutions use String replacements (not patterns). <code>
     * &lt;classPathSubstitutions&gt;<BR/>
     * &#160;&#160;&lt;classPathSubstitution&gt;<BR/>
     * &#160;&#160;&#160;&#160;&lt;search&gt;The key that will be replace&lt;/search&gt;<BR/>
     * &#160;&#160;&#160;&#160;&lt;replaceWith&gt;The value that should use to replace the key&lt;/replaceWith&gt;<BR/>
     * &#160;&#160;&lt;/classPathSubstitution&gt;<BR/>
     * &#160;&#160;... <BR/>
     * &lt;/classPathSubstitutions&gt;:<BR/>
     * </code>
     * This parameter can only be use if param <i>classPathProvider</i> has <i>fitnesse</i> value.
     * 
     * @parameter
     */
    private List classPathSubstitutions = new ArrayList();

    /**
     * Command for the execution of the FitRunner
     */
    private FCommandline mCmd = new FCommandline();

    /** Technical Maven resources. */
    private Artifact pluginArtifact;

    /**
     * Main Mojo method.
     * 
     * @throws MojoExecutionException If the method can't be executed.
     * @throws MojoFailureException If there is fitnesse tests failures.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        new File( this.workingDir ).mkdirs();
        checkConfiguration();

        try
        {
            FitnesseReportMojo.copyAllResources( new File( this.workingDir ), getLog(), getClass().getClassLoader() );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException( "Unable to copy resources", e.getCause() );
        }

        getLog().info( "Found " + getFitnesseSize() + " Fitnesse configuration." );
        for ( int i = 0; i < getFitnesseSize(); i++ )
        {
            Fitnesse tServer = getFitnesse( i );
            callFitnesse( tServer );
            transformResultPage( tServer );
        }
    }

    /*******************************************************************************************************************
     * Change the fitnesse result page to a Maven site format.
     * 
     * @param pServer FitNesse server configuration.
     * @throws MojoExecutionException If the result page can't be found.
     */
    void transformResultPage( Fitnesse pServer )
        throws MojoExecutionException
    {
        FileInputStream tIn = null;
        try
        {
            String tSrcFile = getTmpFileName( pServer );
            tIn = new FileInputStream( tSrcFile );
            File tDestFile = new File( getFinalFileName( pServer ) );
            if ( tDestFile.exists() )
            {
                tDestFile.delete();
            }
            FileWriter tWriter = new FileWriter( tDestFile );
            FitnessePage tResult = new FitnessePage( new File( tSrcFile ) );
            transformHtml( tIn, tWriter, getOutputUrl( pServer ), tResult.getStatus() );
            tIn.close();
            tIn = null;
            if ( !new File( tSrcFile ).delete() )
            {
                getLog().error( "Unable to delete tmp file [" + tSrcFile + "]" );
            }
        }
        catch ( FileNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to tranform html", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to tranform html", e );
        }
        finally
        {
            if ( tIn != null )
            {
                try
                {
                    tIn.close();
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Unable to close tmp file stream", e );
                }
            }
        }

    }

    /**
     * Get the classpath to use for running the fitnesse tests. The classpath is found using the fitnesse
     * <code>path</code> property or using Maven dependancies, according to the POM configuration. The classpath is
     * adapted to the local server configuration depending on the local folders.
     * 
     * @param tServer The FitNesse configuration.
     * @return The ClassPath to use.
     * @throws MojoExecutionException If the classpath can't be found.
     */
    private String getClassPath( Fitnesse tServer )
        throws MojoExecutionException
    {
        String tResult;
        if ( "fitnesse".equals( classPathProvider ) )
        {
            StringBuffer tBuffer = new StringBuffer();
            ClassPathBuilder tBuilder =
                new ClassPathBuilder( tServer.getHostName(), tServer.getPort(), tServer.getPageName(), getLog() );
            tBuffer.append( tBuilder.getPath( classPathSubstitutions, getLog() ) );
            Artifact curArt;
            for ( Iterator tIt = pluginArtifacts.iterator(); tIt.hasNext(); )
            {
                curArt = (Artifact) tIt.next();
                if ( !curArt.getScope().equals( Artifact.SCOPE_PROVIDED )
                    && !curArt.getScope().equals( Artifact.SCOPE_TEST ) )
                {
                    tBuffer.append( File.pathSeparatorChar ).append( curArt.getFile().getAbsolutePath() );
                }
            }
            tBuffer.append( File.pathSeparatorChar ).append( resolvePlugin().getFile().getAbsolutePath() );
            getLog().info( "Try to download classpath from FitNesse server..." );
            tResult = tBuffer.toString();
        }
        else
        {
            tResult = getMavenClassPath();
        }
        if ( copyDependencies )
        {
            tResult = copyDependenciesLocally( tResult );
        }

        return tResult;
    }

    /**
     * Copy all the jar in a local folder. That allows to have a lot of dependencies on a Windows plateform. On Windows
     * the Command Line can't be longer than 8192 character. With that mecanism, the local path are shorter than the
     * full one. For example: "lib/myJar-1.0.jar" is shorter than "d:\maven\repo\com\myCompany\myJar\1.0\myJar-1.0.jar".
     * The method copy the jar and compute the new classpath using these local jars.
     * 
     * @param pClasspath The absolute classpath provided by Maven or FitNesse.
     * @return The new local classpath.
     * @throws MojoExecutionException If the classpath can't be found.
     */
    String copyDependenciesLocally( String pClasspath )
        throws MojoExecutionException
    {
        String tPathSep = System.getProperty( "path.separator" );
        String tFileSep = System.getProperty( "file.separator" );
        File tFolder = new File( workingDir + tFileSep + "lib" );
        if ( !tFolder.exists() )
        {
            tFolder.mkdirs();
        }
        StringTokenizer tToken = new StringTokenizer( pClasspath, tPathSep );
        String tFileName, tShortFileName;
        File tFile;
        FileInputStream tFileInput;
        StringBuffer tBuffer = new StringBuffer();
        File tResultFile;
        try
        {
            while ( tToken.hasMoreTokens() )
            {
                tFileName = tToken.nextToken();
                tFile = new File( tFileName );
                if ( tFile.exists() )
                {
                    tFileInput = new FileInputStream( tFile );
                    int tIndex = tFileName.lastIndexOf( tFileSep );
                    tShortFileName = tFileName.substring( tIndex + 1 );
                    tResultFile = new File( workingDir + tFileSep + "lib" + tFileSep + tShortFileName );
                    FitnesseReportMojo.copyFile( getLog(), tFileInput, tResultFile );
                    tBuffer.append( "lib" + tFileSep + tShortFileName + tPathSep );
                }
                else
                {
                    getLog().warn( "Unable to find the file [" + tFileName + "], skipping this file" );
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            throw new MojoExecutionException( "File not found", e );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException( "File not found", e );
        }
        return tBuffer.toString();
    }

    /**
     * Get the Maven classpath using the POM dependencies.
     * 
     * @return The classpath.
     * @throws MojoExecutionException If the classpath can't be found.
     */
    String getMavenClassPath()
        throws MojoExecutionException
    {
        StringBuffer tBuffer = new StringBuffer();
        Set tArtifacts = transitivelyResolvePomDependencies();
        if ( tArtifacts != null && !tArtifacts.isEmpty() )
        {
            for ( Iterator it = tArtifacts.iterator(); it.hasNext(); )
            {
                Artifact curArtififact = (Artifact) it.next();
                tBuffer.append( curArtififact.getFile().getAbsolutePath() ).append( File.pathSeparatorChar );
            }
        }

        tBuffer.append( project.getBuild().getOutputDirectory() ).append( File.pathSeparatorChar );
        tBuffer.append( project.getBuild().getTestOutputDirectory() ).append( File.pathSeparatorChar );
        String tMavenClassPath = tBuffer.toString();
        getLog().info( "Use Maven Classpath = " + tMavenClassPath );
        return tMavenClassPath;
    }

    /**
     * Create the transitive classpath.
     * 
     * @return The dependent artifacts.
     * @throws MojoExecutionException If the classpath can't be found.
     */
    public Set transitivelyResolvePomDependencies()
        throws MojoExecutionException
    {
        // make Artifacts of all the dependencies
        Set dependencyArtifacts;
        try
        {
            dependencyArtifacts = MavenMetadataSource.createArtifacts( artifactFactory, dependencies, null, null, null );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new MojoExecutionException( "Invalid dependency", e );
        }

        // not forgetting the Artifact of the project itself
        dependencyArtifacts.add( project.getArtifact() );

        List listeners = Collections.EMPTY_LIST;

        // resolve all dependencies transitively to obtain a comprehensive list
        // of jars
        ArtifactResolutionResult result;
        try
        {
            result =
                artifactResolver.resolveTransitively( dependencyArtifacts, project.getArtifact(),
                                                      Collections.EMPTY_MAP, localRepository, remoteRepositories,
                                                      metadataSource, null, listeners );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Unable to resolve Artifact.", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to resolve Artifact.", e );
        }

        return result.getArtifacts();
    }

    /**
     * Call a Fitnesse server page.
     * 
     * @param pServer The Fitnesse configuration.
     * @throws MojoFailureException If a failure occurs.
     * @throws MojoExecutionException If an error occurs.
     */
    void callFitnesse( Fitnesse pServer )
        throws MojoFailureException, MojoExecutionException
    {
        getLog().info( "Call result of the server," + pServer );
        FCommandline tCmd = prepareCommandLine( pServer, getClassPath( pServer ) );
        executeCommand( pServer, tCmd );
    }

    /**
     * Run locally the FitNesse tests for one configuration.
     * 
     * @param pServer The FitNesse configuration.
     * @param pCmd The Command.
     * @throws MojoFailureException If the is a FitNesse failure.
     * @throws MojoExecutionException If the FitNesse tests can't be runned.
     */
    void executeCommand( Fitnesse pServer, FCommandline pCmd )
        throws MojoFailureException, MojoExecutionException
    {
        FitnesseStreamConsumer tInfoConsumer = null;
        int tResult;
        try
        {
            tInfoConsumer = getStandardConsumer( pServer );
            tResult = FCommandLineUtils.executeCommandLine( pCmd, tInfoConsumer, getErrorConsumer( tInfoConsumer ) );
        }
        catch ( FCommandLineException e )
        {
            getLog().error( "Unable to start fitnesse [" + pCmd.toString() + "]", e );
            throw new MojoExecutionException( "Unable to start fitnesse [" + pCmd.toString() + "]", e );
        }
        finally
        {
            if ( tInfoConsumer != null )
            {
                closeConsumer( tInfoConsumer );
            }
        }
        if ( tResult != 0 )
        {
            if ( tInfoConsumer.hasGeneratedResultFile() )
            {
                if ( isFailOnError() )
                {
                    throw new MojoFailureException( "Fitnesse command ended with errors, exit code:" + tResult );
                }
                else
                {
                    getLog().info(
                                   "Fitnesse command ended with errors, exit code:" + tResult
                                       + ", but failOnError is configure to \"false\""
                                       + " change your configuration if you want to fail your build" );
                }
            }
            else
            {
                throw new MojoExecutionException( "Unable to run Fitnesse, exit code [" + tResult + "]" );
            }
        }
        getLog().info( "Fitnesse invocation ended with result code [" + tResult + "]" );
    }

    /**
     * Close reserved resources.
     * 
     * @param pInfoConsumer The resource to release.
     */
    private void closeConsumer( FitnesseStreamConsumer pInfoConsumer )
    {
        if ( pInfoConsumer instanceof MultipleConsumer )
        {
            ( (MultipleConsumer) pInfoConsumer ).getFileConsumer().close();
        }
        else
        {
            ( (FileConsumer) pInfoConsumer ).close();
        }

    }

    /**
     * Get the error stream of the external Process.
     * 
     * @param pConsumer The resources associated to the external process.
     * @return The error stream.
     */
    FitnesseStreamConsumer getErrorConsumer( FitnesseStreamConsumer pConsumer )
    {

        if ( displayOutput )
        {
            MultipleConsumer tMultiConsumer = (MultipleConsumer) pConsumer;
            return new MultipleConsumer( new LogConsumer( getLog(), Level.SEVERE ), tMultiConsumer.getFileConsumer() );
        }
        else
        {
            return (FileConsumer) pConsumer;
        }
    }

    /**
     * Get the standard stream of the external Process.
     * 
     * @param pServer The FitNesse configuration.
     * @return The standard stream.
     */
    FitnesseStreamConsumer getStandardConsumer( Fitnesse pServer )
    {
        File tOutputFile = new File( getOutputFileName( pServer ) );
        if ( tOutputFile.exists() )
        {
            tOutputFile.delete();
        }
        FileConsumer tFileConsumer = new FileConsumer( tOutputFile );

        if ( displayOutput )
        {
            return new MultipleConsumer( new LogConsumer( getLog(), Level.INFO ), tFileConsumer );
        }
        else
        {
            return tFileConsumer;
        }
    }

    /**
     * Check the whole configuration of this Mojo.
     * 
     * @throws MojoExecutionException If an error occurs.
     */
    void checkConfiguration()
        throws MojoExecutionException
    {
        super.checkConfiguration();
        try
        {
            Class tClass = Class.forName( fitnesseRunnerClass );
            tClass.getMethod( "main", new Class[] { String[].class } );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "The class [" + fitnesseRunnerClass
                + "] could not be found, check your maven-fitnesse-plugin configuration and the plugin documentation." );
        }
        catch ( SecurityException e )
        {
            throw new MojoExecutionException( "The class [" + fitnesseRunnerClass
                + "] doesn't have a \"main\" accessible method.", e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new MojoExecutionException( "The class [" + fitnesseRunnerClass
                + "] doesn't have a \"main\" accessible method.", e );
        }
        if ( ( !"fitnesse".equals( classPathProvider ) ) && ( !"maven".equals( classPathProvider ) ) )
        {
            throw new MojoExecutionException( "classPathProvider accepts only \"fitnesse\" ou \"maven\" values. ["
                + classPathProvider + "] is not valid." );
        }
    }

    /**
     * Create the Command object fot running the tests locally.
     * 
     * @param pServer The fitnesse configuration.
     * @param pClassPath The classpath.
     * @return A ready to use command object.
     */
    FCommandline prepareCommandLine( Fitnesse pServer, String pClassPath )
    {
        mCmd.clear();

        mCmd.setExecutable( jdk );
        if ( jdkOpts != null && jdkOpts.length() > 0 )
        {
            StringTokenizer tTok = new StringTokenizer( jdkOpts, " " );
            while ( tTok.hasMoreTokens() )
            {
                mCmd.createArgument().setValue( tTok.nextToken() );
            }
        }
        mCmd.createArgument().setValue( "-cp" );
        mCmd.createArgument().setValue( pClassPath );
        mCmd.createArgument().setValue( fitnesseRunnerClass );
        mCmd.createArgument().setValue( "-v" );
        if ( debug )
        {
            mCmd.createArgument().setValue( "-debug" );
        }
        mCmd.createArgument().setValue( "-html" );
        String tFileName = getTmpFileName( pServer );
        File tFile = new File( tFileName );
        if ( tFile.exists() )
        {
            tFile.delete();
        }
        mCmd.createArgument().setValue( tFileName );
        if ( generateXml )
        {
            mCmd.createArgument().setValue( "-xml" );
            String tXmlFileName = getResultFileName( pServer, "", "xml" );
            File tXmlFile = new File( tXmlFileName );
            if ( tXmlFile.exists() )
            {
                tXmlFile.delete();
            }
            mCmd.createArgument().setValue( tXmlFileName );
        }
        mCmd.createArgument().setValue( "-nopath" );
        if ( pServer.getSuiteFilter() != null && pServer.getSuiteFilter().length() > 0 )
        {
            mCmd.createArgument().setValue( "-suiteFilter" );
            mCmd.createArgument().setValue( pServer.getSuiteFilter() );
        }

        mCmd.createArgument().setValue( pServer.getHostName() );

        mCmd.createArgument().setValue( "" + pServer.getPort() );
        mCmd.createArgument().setValue( pServer.getPageName() );
        mCmd.setWorkingDirectory( workingDir );
        getLog().info( "Execute =" + mCmd.toString() );
        getLog().info( "From =" + mCmd.getWorkingDirectory() );
        return mCmd;
    }

    /**
     * Accessor.
     * 
     * @param pFitnesseRunnerClass The main class fot running FitNesse.
     */
    public void setFitnesseRunnerClass( String pFitnesseRunnerClass )
    {
        fitnesseRunnerClass = pFitnesseRunnerClass;
    }

    /**
     * Accessor.
     * 
     * @param pJdk The Jdk path.
     */
    public void setJdk( String pJdk )
    {
        jdk = pJdk;
    }

    /**
     * Accessor.
     * 
     * @param pWorkingDir The working directory fot the fitnesse execution.
     */
    public void setWorkingDir( String pWorkingDir )
    {
        workingDir = pWorkingDir;
    }

    /**
     * Accessor.
     * 
     * @param pDebug The debug level.
     */
    void setDebug( boolean pDebug )
    {
        debug = pDebug;
    }

    /**
     * Accessor.
     * 
     * @param pGenerateXml Set to true to generate Xml output file.
     */
    void setGenerateXml( boolean pGenerateXml )
    {
        generateXml = pGenerateXml;
    }

    /**
     * Accessor.
     * 
     * @param pCmd The commandLine for running FitNesse.
     */
    void setCmd( FCommandline pCmd )
    {
        this.mCmd = pCmd;
    }

    /**
     * Accessor.
     * 
     * @param jdkOpts The Jsk options to use when runinng fitnesse tests.
     */
    void setJdkOpts( String jdkOpts )
    {
        this.jdkOpts = jdkOpts;
    }

    /**
     * Accessor.
     * 
     * @param pluginArtifacts The Maven resource.
     */
    public void setPluginArtifacts( List pluginArtifacts )
    {
        this.pluginArtifacts = pluginArtifacts;
    }

    /**
     * Resolve plugin artifacts.
     * 
     * @return The plugin.
     * @throws MojoExecutionException If the plun gin can't be found.
     */
    public Artifact resolvePlugin()
        throws MojoExecutionException
    {
        if ( pluginArtifact == null )
        {
            Artifact tPluginArtifact =
                this.artifactFactory.createArtifactWithClassifier( pluginGroupId, pluginArtifactId, pluginVersion,
                                                                   "maven-plugin", "" );
            try
            {
                this.artifactResolver.resolve( tPluginArtifact, new ArrayList(), localRepository );
            }
            catch ( ArtifactResolutionException e )
            {
                throw new MojoExecutionException( "Unable to resolve artificat", e );
            }
            catch ( ArtifactNotFoundException e )
            {
                throw new MojoExecutionException( "Unable to resolve artificat", e );
            }
            return tPluginArtifact;
        }
        else
        {
            return pluginArtifact;
        }
    }

    /**
     * For testing purpose. Record plugin information for the {@link FitnesseRunnerMojo#resolvePlugin()}.
     * 
     * @param pGroupId The groupId of the plugin.
     * @param pArtifactId The artifactI of the plugin.
     * @param pVersion The version of the plugin.
     */
    void setPluginArtifactInfo( String pGroupId, String pArtifactId, String pVersion )
    {
        pluginGroupId = pGroupId;
        pluginArtifactId = pArtifactId;
        pluginVersion = pVersion;
    }

    /**
     * For testing purpose
     */
    public void setPluginArtifact( Artifact pArtifact )
    {
        this.pluginArtifact = pArtifact;
    }

    public void setClassPathSubstitions( List classPathSubstitions )
    {
        this.classPathSubstitutions = classPathSubstitions;
    }

    public void setClassPathProvider( String classPathProvider )
    {
        this.classPathProvider = classPathProvider;
    }

    public void setDisplayOutput( boolean displayOutput )
    {
        this.displayOutput = displayOutput;
    }

    String getOutputFileName( Fitnesse pServer )
    {
        return getResultFileName( pServer, FitnesseAbstractMojo.OUTPUT_EXTENSION, "txt" );
    }

    String getOutputUrl( Fitnesse pServer )
    {
        return FITNESSE_RESULT_PREFIX + "_" + pServer.getHostName() + "_" + pServer.getPageName() + "_output.txt";
    }

    public void setCopyDependencies( boolean copyDependencies )
    {
        this.copyDependencies = copyDependencies;
    }

}
