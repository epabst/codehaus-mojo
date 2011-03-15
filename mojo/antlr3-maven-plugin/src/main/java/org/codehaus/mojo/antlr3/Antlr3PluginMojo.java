package org.codehaus.mojo.antlr3;

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

import org.antlr.Tool;
import org.antlr.analysis.DFA;
import org.antlr.codegen.CodeGenerator;
import org.antlr.tool.BuildDependencyGenerator;
import org.antlr.tool.Grammar;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Generate source code from ANTLRv3 grammar specifications.
 *
 * @author <a href="mailto:dave@badgers-in-foil.co.uk">David Holroyd</a>
 * @version $Id $
 */
public abstract class Antlr3PluginMojo extends AbstractMojo
{
    /**
     * A set of patterns matching files from the sourceDirectory that
     * should be processed as grammars.
     *
     * @parameter
     */
    protected Set includes = new HashSet();

    /**
     * A set of exclude patterns.
     *
     * @parameter
     */
    protected Set excludes = new HashSet();

    /**
     * Enables ANTLR-specific network debugging. Requires a tool able to
     * talk this protocol e.g. ANTLRWorks.
     *
     * @parameter default-value="false"
     */
    protected boolean debug;

    /**
     * Generate a parser that logs rule entry/exit messages.
     *
     * @parameter default-value="false"
     */
    protected boolean trace;

    /**
     * Generate a parser that computes profiling information.
     *
     * @parameter default-value="false"
     */
    protected boolean profile;

    private Tool tool;


    /**
     * The number of milliseconds ANTLR will wait for analysis of each
     * alternative in the grammar to complete before giving up.
     *
     * @parameter default-value="0"
     */
    private int conversionTimeout;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    abstract File getSourceDirectory();
    abstract File getOutputDirectory();
    abstract File getLibDirectory();
    abstract void addSourceRoot( File outputDir );

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException
    {
        File outputDir = getOutputDirectory();

        if ( !outputDir.exists() )
        {
            outputDir.mkdirs();
        }

        tool = new Tool();
        DFA.MAX_TIME_PER_DFA_CREATION = conversionTimeout;

        File libFile = getLibDirectory();
        if ( libFile != null )
        {
            if ( !libFile.exists() )
            {
                libFile.mkdirs(); // create dir if it did not allready exist
            }
            tool.processArgs(
                new String[] { "-lib", libFile.getAbsolutePath() }
            );
        }

        File srcDir = getSourceDirectory();
        try
        {
            processGrammarFiles( srcDir, outputDir );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "", e );
        }

        if ( project != null )
        {
            addSourceRoot( outputDir );
        }
    }

    private void processGrammarFiles( File sourceDirectory, File outputDirectory )
        throws TokenStreamException, RecognitionException, IOException, InclusionScanException
    {
        SourceMapping mapping = new SuffixMapping( "g", Collections.EMPTY_SET );
        Set includes = getIncludesPatterns();
        SourceInclusionScanner scan = new SimpleSourceInclusionScanner( includes, excludes );
        scan.addSourceMapping( mapping );
        Set grammarFiles = scan.getIncludedSources( sourceDirectory, null );
        if ( grammarFiles.isEmpty() )
        {
            if ( getLog().isInfoEnabled() )
            {
                getLog().info( "No grammars to process" );
            }
        }
        else
        {
            List grammars = loadGrammarDependencies( grammarFiles, sourceDirectory, outputDirectory );
            sortIntoBuildOrder( grammars );
            boolean built = false;
            for ( Iterator i = grammars.iterator(); i.hasNext(); )
            {
                built |= processGrammarFile( ( (GrammarInfo) i.next() ) );
            }
            if ( !built && getLog().isInfoEnabled() )
            {
                getLog().info( "No grammars processed; generated files are up to date" );
            }
        }
    }

    private void sortIntoBuildOrder( List grammars )
    {
        Collections.sort( grammars, new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                GrammarInfo a = (GrammarInfo) o1;
                GrammarInfo b = (GrammarInfo) o2;
                if ( a.dependsOn( b ) )
                {
                    return 1;
                }
                else if ( b.dependsOn( a ) )
                {
                    return -1;
                }
                return 0;
            }
        } );
    }

    private List loadGrammarDependencies( Set grammarFiles, File sourceDirectory, File outputDirectory )
        throws TokenStreamException, RecognitionException, IOException
    {
        List result = new ArrayList();
        for ( Iterator i = grammarFiles.iterator(); i.hasNext(); )
        {
            String grammarFileName = ( (File) i.next() ).getPath();
            // Hack the output dir such that the output hierarchy will match the
            // source hierarchy.  This way, grammar authors can arrange their
            // grammars in a structure that matches the package (assuming Java
            // output), and the generated files will not produce warnings/errors
            // from javac due to the path-prefix not matching the package-prefix.
            // (ANTLR sort-of does this itself, but only when grammar file names
            // are specified relative to $PWD)
            String sourceSubdir = findSourceSubdir( sourceDirectory, grammarFileName );
            File outputSubdir = new File( outputDirectory, sourceSubdir );
            tool.setOutputDirectory( outputSubdir.getPath() );
            BuildDependencyGenerator dep = new BuildDependencyGenerator( tool, grammarFileName );
            
            result.add( new GrammarInfo( dep, grammarFileName ) );
        }
        return result;
    }

    public Set getIncludesPatterns()
    {
        if ( includes == null || includes.isEmpty() )
        {
            return Collections.singleton( "**/*.g" );
        }
        return includes;
    }

    private boolean processGrammarFile( GrammarInfo grammarInfo )
        throws TokenStreamException, RecognitionException, IOException
    {
        List outputFiles = grammarInfo.getBuildDependency().getGeneratedFileList();
        if ( AntlrHelper.buildRequired( grammarInfo.getGrammarFileName(), outputFiles ) )
        {
            generate( grammarInfo.getGrammarFileName() );
            return true;
        }
        return false;
    }

    private String findSourceSubdir( File sourceDirectory, String grammarFileName )
    {
        String srcPath = sourceDirectory.getPath();
        if ( !grammarFileName.startsWith( srcPath ) )
        {
            throw new IllegalArgumentException( "expected " + grammarFileName
                                               + " to be prefixed with "
                                               + sourceDirectory );
        }
        File unprefixedGrammarFileName = new File( grammarFileName.substring( srcPath.length() ) );
        return unprefixedGrammarFileName.getParent();
    }

    private void generate( String grammarFileName ) throws TokenStreamException, RecognitionException, IOException
    {
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Processing grammar " + grammarFileName );
        }
        Grammar grammar = tool.getGrammar( grammarFileName );
        processGrammar( grammar );

        // now handle the lexer if one was created for a merged spec
        String lexerGrammarStr = grammar.getLexerGrammar();
        if ( grammar.type == Grammar.COMBINED && lexerGrammarStr != null )
        {
            String lexerGrammarFileName = grammar.getImplicitlyGeneratedLexerFileName();
            Writer w = tool.getOutputFile( grammar, lexerGrammarFileName );
            w.write( lexerGrammarStr );
            w.close();
            StringReader sr = new StringReader( lexerGrammarStr );
            Grammar lexerGrammar = new Grammar();
            lexerGrammar.setTool( tool );
            File lexerGrammarFullFile = new File( tool.getFileDirectory( lexerGrammarFileName ), lexerGrammarFileName );
            lexerGrammar.setFileName( lexerGrammarFullFile.toString() );
            lexerGrammar.importTokenVocabulary( grammar );
            lexerGrammar.setGrammarContent( sr );
            sr.close();
            processGrammar( lexerGrammar );
        }
    }

    private void processGrammar( Grammar grammar )
    {
        String language = (String) grammar.getOption( "language" );
        if ( language != null )
        {
            CodeGenerator generator = new CodeGenerator( tool, grammar, language );
            grammar.setCodeGenerator( generator );
            generator.setDebug( debug );
            generator.setProfile( profile );
            generator.setTrace( trace );
            generator.genRecognizer();
        }
    }
}
