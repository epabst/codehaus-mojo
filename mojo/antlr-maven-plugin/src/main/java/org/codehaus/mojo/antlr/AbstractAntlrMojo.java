package org.codehaus.mojo.antlr;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.lang.reflect.InvocationTargetException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.CommandLine;
import org.codehaus.mojo.antlr.options.Grammar;
import org.codehaus.mojo.antlr.proxy.Helper;
import org.codehaus.mojo.antlr.metadata.MetadataExtracter;
import org.codehaus.mojo.antlr.metadata.XRef;
import org.codehaus.mojo.antlr.plan.GenerationPlan;
import org.codehaus.mojo.antlr.plan.GenerationPlanBuilder;
import org.codehaus.plexus.util.StringOutputStream;
import org.codehaus.plexus.util.StringUtils;

/**
 * Base class with majority of Antlr functionalities.
 * 
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractAntlrMojo
    extends AbstractMojo
    implements Environment
{
    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    /**
     * Specifies the Antlr directory containing grammar files.
     * 
     * @parameter default-value="${basedir}/src/main/antlr"
     */
    protected File sourceDirectory;

    /**
     * The Maven Project Object
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * The maven project's helper.
     * 
     * @component role="org.apache.maven.project.MavenProjectHelper"
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    // ----------------------------------------------------------------------
    // Antlr parameters
    // See http://www.antlr2.org/doc/options.html#Command%20Line%20Options
    // ----------------------------------------------------------------------

    /**
     * Specifies the destination directory where Antlr should generate files. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter default-value="${project.build.directory}/generated-sources/antlr"
     */
    protected File outputDirectory;

    /**
     * Comma separated grammar file names or grammar pattern file names present in the <code>sourceDirectory</code>
     * directory. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${grammars}"
     */
    protected String grammars;

    /**
     * Grammar list presents in the <code>sourceDirectory</code> directory. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a> <br/>
     * Example:
     * 
     * <pre>
     * &lt;grammarDefs&gt;<br/>
     *   &lt;grammar&gt;<br/>
     *     &lt;name&gt;myGrammar.g&lt;/name&gt;<br/>
     *     &lt;glib&gt;mySuperGrammar.g;myOtherSuperGrammar.g&lt;/glib&gt;<br/>
     *   &lt;/grammar&gt;<br/>
     * &lt;/grammarDefs&gt;
     * </pre>
     * 
     * @parameter expression="${grammarDefs}"
     */
    protected Grammar[] grammarDefs;

    /**
     * Launch the ParseView debugger upon parser invocation. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${debug}" default-value="false"
     */
    private boolean debug;

    /**
     * Generate a text file from your grammar with a lot of debugging info. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${diagnostic}" default-value="false"
     */
    private boolean diagnostic;

    /**
     * Have all rules call traceIn/traceOut. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${trace}" default-value="false"
     */
    private boolean trace;

    /**
     * Have parser rules call traceIn/traceOut. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${traceParser}" default-value="false"
     */
    private boolean traceParser;

    /**
     * Have lexer rules call traceIn/traceOut. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${traceLexer}" default-value="false"
     */
    private boolean traceLexer;

    /**
     * Have tree rules call traceIn/traceOut. <br/>
     * See <a href="http://www.antlr2.org/doc/options.html#Command%20Line%20Options">Command Line Options</a>
     * 
     * @parameter expression="${traceTreeParser}" default-value="false"
     */
    private boolean traceTreeParser;

    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @throws MojoExecutionException
     */
    protected void executeAntlr()
        throws MojoExecutionException
    {
        validateParameters();

        Artifact antlrArtifact = locateAntlrArtifact();
        MetadataExtracter metadataExtracter = new MetadataExtracter( this, new Helper( antlrArtifact ) );
        XRef metadata = metadataExtracter.processMetadata( getGrammars() );

        Iterator generationPlans = new GenerationPlanBuilder( this ).buildGenerationPlans( metadata ).iterator();
        while ( generationPlans.hasNext() )
        {
            final GenerationPlan plan = (GenerationPlan) generationPlans.next();
            if ( !plan.isOutOfDate() )
            {
                getLog().info( "grammar [" + plan.getId() + "] was up-to-date; skipping" );
                continue;
            }

            getLog().info( "performing grammar generation [" + plan.getId() + "]" );
            performGeneration( plan, antlrArtifact );
        }

        if ( project != null )
        {
            projectHelper.addResource( project, outputDirectory.getAbsolutePath(),
                                       Collections.singletonList( "**/**.txt" ), new ArrayList() );
            project.addCompileSourceRoot( outputDirectory.getAbsolutePath() );
        }
    }

    protected final Artifact locateAntlrArtifact()
        throws NoAntlrDependencyDefinedException
    {
        Artifact antlrArtifact = null;
        if ( project.getCompileArtifacts() != null )
        {
            Iterator projectArtifacts = project.getCompileArtifacts().iterator();
            while ( projectArtifacts.hasNext() )
            {
                final Artifact artifact = (Artifact) projectArtifacts.next();
                if ( "antlr".equals( artifact.getGroupId() )
                    && ( "antlr".equals( artifact.getArtifactId() ) || "antlr-all".equals( artifact.getArtifactId() ) ) )
                {
                    antlrArtifact = artifact;
                    break;
                }
            }
        }
        if ( antlrArtifact == null )
        {
            throw new NoAntlrDependencyDefinedException( "project did not define antlr:antlr depenency" );
        }
        // TODO : enforce specific version range; e.g. [2.7,3.0) ???
        return antlrArtifact;
    }

    protected void performGeneration( GenerationPlan plan, Artifact antlrArtifact )
        throws MojoExecutionException
    {
        if ( !plan.getGenerationDirectory().getParentFile().exists() )
        {
            plan.getGenerationDirectory().getParentFile().mkdirs();
        }

        // ----------------------------------------------------------------------
        // Wrap arguments
        // Note: grammar file should be last
        // ----------------------------------------------------------------------

        List arguments = new LinkedList();
        addArgIf( arguments, debug, "-debug" );
        addArgIf( arguments, diagnostic, "-diagnostic" );
        addArgIf( arguments, trace, "-trace" );
        addArgIf( arguments, traceParser, "-traceParser" );
        addArgIf( arguments, traceLexer, "-traceLexer" );
        addArgIf( arguments, traceTreeParser, "-traceTreeParser" );

        addArgs( arguments );

        arguments.add( "-o" );
        arguments.add( plan.getGenerationDirectory().getPath() );

        if ( plan.getCollectedSuperGrammarIds().size() > 0 )
        {
            arguments.add( "-glib" );
            StringBuffer buffer = new StringBuffer();
            Iterator ids = plan.getCollectedSuperGrammarIds().iterator();
            while ( ids.hasNext() )
            {
                buffer.append( new File( sourceDirectory, (String) ids.next() ) );
                if ( ids.hasNext() )
                {
                    buffer.append( ';' );
                }
            }
            arguments.add( buffer.toString() );
        }

        arguments.add( plan.getSource().getPath() );

        String[] args = (String[]) arguments.toArray( new String[arguments.size()] );

        if ( plan.getImportVocabTokenTypesDirectory() != null
            && !plan.getImportVocabTokenTypesDirectory().equals( plan.getGenerationDirectory() ) )
        {
            // we need to spawn a new process to properly set up PWD
            CommandLine commandLine = new CommandLine( "java" );
            commandLine.addArgument( "-classpath", false );
            commandLine.addArgument( generateClasspathForProcessSpawning( antlrArtifact ), true );
            commandLine.addArgument( "antlr.Tool", false );
            commandLine.addArguments( args, true );
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory( plan.getImportVocabTokenTypesDirectory() );
            try
            {
                executor.execute( commandLine );
            }
            catch ( IOException e )
            {
                getLog().warn( "Error spawning process to execute antlr tool : " + e.getMessage() );
            }

            return;
        }

        // ----------------------------------------------------------------------
        // Call Antlr
        // ----------------------------------------------------------------------

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "antlr args=\n" + StringUtils.join( args, "\n" ) );
        }

        boolean failedSetManager = false;
        SecurityManager oldSm = null;
        try
        {
            oldSm = System.getSecurityManager();
            System.setSecurityManager( NoExitSecurityManager.INSTANCE );
        }
        catch ( SecurityException ex )
        {
            // ANTLR-12
            oldSm = null;
            failedSetManager = true;
            // ignore, in embedded environment the security manager can already be set.
            // in such a case assume the exit call is handled properly..
            getLog().warn( "Cannot set custom SecurityManager. "
                               + "Antlr's call to System.exit() can cause application shutdown "
                               + "if not handled by the current SecurityManager." );
        }

        String originalUserDir = null;
        if ( plan.getImportVocabTokenTypesDirectory() != null )
        {
            originalUserDir = System.getProperty( "user.dir" );
            System.setProperty( "user.dir", plan.getImportVocabTokenTypesDirectory().getPath() );
        }

        PrintStream oldErr = System.err;

        OutputStream errOS = new StringOutputStream();
        PrintStream err = new PrintStream( errOS );
        System.setErr( err );

        try
        {
            executeAntlrInIsolatedClassLoader( (String[]) arguments.toArray( new String[0] ), antlrArtifact );
        }
        catch ( SecurityException e )
        {
            if ( e.getMessage().equals( "exitVM-0" )
                || e.getClass().getName().equals( "org.netbeans.core.execution.ExitSecurityException" ) ) // netbeans
                                                                                                          // IDE Sec
                                                                                                          // Manager.
            {
                // ANTLR-12
                // now basically every secutiry manager could set different message, how to handle in generic way?
                // probably only by external execution
                // / in case of NetBeans SecurityManager, it's not possible to distinguish exit codes, rather swallow
                // than fail.
                getLog().debug( e );
            }
            else
            {
                throw new MojoExecutionException( "Antlr execution failed: " + e.getMessage() + "\n Error output:\n"
                    + errOS, e );
            }
        }
        finally
        {
            if ( originalUserDir != null )
            {
                System.setProperty( "user.dir", originalUserDir );
            }
            if ( !failedSetManager )
            {
                System.setSecurityManager( oldSm );
            }
            System.setErr( oldErr );
            System.err.println( errOS.toString() );
        }
    }

    private String generateClasspathForProcessSpawning( Artifact antlrArtifact )
    {
        // todo : is maven by itself enough for the generation???
        return antlrArtifact.getFile().getPath();
    }

    private void executeAntlrInIsolatedClassLoader( String[] args, Artifact antlrArtifact )
        throws MojoExecutionException
    {
        try
        {
            URLClassLoader classLoader =
                new URLClassLoader( new URL[] { antlrArtifact.getFile().toURL() }, ClassLoader.getSystemClassLoader() );

            Class toolClass = classLoader.loadClass( "antlr.Tool" );
            toolClass.getMethod( "main", new Class[] { String[].class } ).invoke( null, new Object[] { args } );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Unable to resolve antlr:antlr artifact url", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "could not locate antlr.Tool class" );
        }
        catch ( NoSuchMethodException e )
        {
            throw new MojoExecutionException( "error locating antlt.Tool#main", e );
        }
        catch ( InvocationTargetException e )
        {
            throw new MojoExecutionException( "error perforing antlt.Tool#main", e.getTargetException() );
        }
        catch ( IllegalAccessException e )
        {
            throw new MojoExecutionException( "error perforing antlt.Tool#main", e );
        }
    }

    /**
     * Add arguments to be included in Antlr call
     * 
     * @param arguments
     */
    protected abstract void addArgs( List arguments );

    /**
     * Convenience method to add an argument
     * 
     * @param arguments
     * @param b
     * @param value
     */
    protected static void addArgIf( List arguments, boolean b, String value )
    {
        if ( b )
        {
            arguments.add( value );
        }
    }

    /**
     * @param grammar
     * @param outputDir
     * @return generated file
     * @throws IOException
     */
    private File getGeneratedFile( String grammar, File outputDir )
        throws IOException
    {
        String generatedFileName = null;

        String packageName = "";

        BufferedReader in = new BufferedReader( new FileReader( grammar ) );

        String line;

        while ( ( line = in.readLine() ) != null )
        {
            line = line.trim();

            int extendsIndex = line.indexOf( " extends " );

            if ( line.startsWith( "class " ) && extendsIndex > -1 )
            {
                generatedFileName = line.substring( 6, extendsIndex ).trim();

                break;
            }
            else if ( line.startsWith( "package" ) )
            {
                packageName = line.substring( 8 ).trim();
            }
        }

        in.close();

        if ( generatedFileName == null )
        {
            throw new IOException( "Unable to generate the output file name: is the grammar '" + grammar + "' valide?" );
        }

        File genFile = null;

        if ( "".equals( packageName ) )
        {
            genFile = new File( outputDir, generatedFileName + ".java" );
        }
        else
        {
            String packagePath = packageName.replace( '.', File.separatorChar );

            packagePath = packagePath.replace( ';', File.separatorChar );

            genFile = new File( new File( outputDir, packagePath ), generatedFileName + ".java" );
        }

        return genFile;
    }

    /**
     * grammars or grammarDefs parameters is required
     * 
     * @throws MojoExecutionException
     */
    private void validateParameters()
        throws MojoExecutionException
    {
        if ( ( StringUtils.isEmpty( grammars ) ) && ( ( grammarDefs == null ) || ( grammarDefs.length == 0 ) ) )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Antlr plugin parameters are invalid/missing." ).append( '\n' );
            msg.append( "Inside the definition for plugin 'antlr-maven-plugin' specify the following:" ).append( '\n' );
            msg.append( '\n' );
            msg.append( "<configuration>" ).append( '\n' );
            msg.append( "  <grammars>VALUE</grammars>" ).append( '\n' );
            msg.append( "- OR - " ).append( '\n' );
            msg.append( "  <grammarDefs>VALUE</grammarDefs>" ).append( '\n' );
            msg.append( "</configuration>" ).append( '\n' );

            throw new MojoExecutionException( msg.toString() );
        }
    }

    /**
     * Get the list of all grammars to be compiled. The grammars variable can be a list of file or patterns. For
     * instance, one can use *.g instead of a full list of grammar names. Be aware that sometime the grammar order is
     * important, and that patterns won't keep this order, but we can still combine both elements( ordered names first,
     * then the patterns). File name won't be added twice in the list of files.
     * 
     * @return an array of grammar from <code>grammars</code> and <code>grammarDefs</code> variables
     */
    private Grammar[] getGrammars()
    {
        List grammarList = new ArrayList();
        Set grammarSet = new HashSet();

        if ( StringUtils.isNotEmpty( grammars ) )
        {
            StringTokenizer st = new StringTokenizer( grammars, ", " );

            while ( st.hasMoreTokens() )
            {
                String currentGrammar = st.nextToken().trim();

                if ( StringUtils.isNotEmpty( currentGrammar ) )
                {
                    // Check if some pattern has been used
                    if ( ( currentGrammar.indexOf( '*' ) != -1 ) || ( currentGrammar.indexOf( '?' ) != -1 ) )
                    {
                        // We first have to 'protect' the '.', and transform patterns
                        // to regexp, substituting '*' to '.*' and '?' to '.'
                        final String transformedGrammar =
                            currentGrammar.replaceAll( "\\.", "\\\\." ).replaceAll( "\\*", ".*" ).replaceAll( "\\?",
                                                                                                              "." );

                        // Filter the source directory
                        String[] dir = sourceDirectory.list( new FilenameFilter()
                        {
                            public boolean accept( File dir, String s )
                            {
                                return Pattern.matches( transformedGrammar, s );
                            }
                        } );

                        if ( ( dir != null ) && ( dir.length != 0 ) )
                        {
                            for ( int i = 0; i < dir.length; i++ )
                            {
                                // Just add fles which are not in the set
                                // of files already seen.
                                if ( !grammarSet.contains( dir[i] ) )
                                {
                                    Grammar grammar = new Grammar();
                                    grammar.setName( dir[i] );

                                    grammarList.add( grammar );
                                }
                            }
                        }
                    }
                    else
                    {
                        if ( !grammarSet.contains( currentGrammar ) )
                        {
                            Grammar grammar = new Grammar();
                            grammar.setName( currentGrammar );

                            grammarList.add( grammar );
                        }
                    }
                }
            }
        }

        if ( grammarDefs != null )
        {
            grammarList.addAll( Arrays.asList( grammarDefs ) );
        }

        return (Grammar[]) grammarList.toArray( new Grammar[0] );
    }

    public static class NoAntlrDependencyDefinedException
        extends MojoExecutionException
    {
        public NoAntlrDependencyDefinedException( String s )
        {
            super( s );
        }
    }
}
