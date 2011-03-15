package org.codehaus.mojo.antlr.metadata;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.antlr.Environment;
import org.codehaus.mojo.antlr.proxy.Helper;
import org.codehaus.plexus.util.StringUtils;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class MetadataExtracter
{
    private final Helper helper;

    private final Environment environment;

    private final Class antlrHierarchyClass;

    public MetadataExtracter( Environment environment, Helper helper )
        throws MojoExecutionException
    {
        this.environment = environment;
        this.helper = helper;
        this.antlrHierarchyClass = helper.getAntlrHierarchyClass();
    }

    public XRef processMetadata( org.codehaus.mojo.antlr.options.Grammar[] grammars )
        throws MojoExecutionException
    {
        Object hierarchy;
        Method readGrammarFileMethod;
        Method getFileMethod;
        try
        {
            Object antlrTool = helper.getAntlrToolClass().newInstance();
            Constructor ctor = antlrHierarchyClass.getConstructor( new Class[] { helper.getAntlrToolClass() } );
            hierarchy = ctor.newInstance( new Object[] { antlrTool } );

            readGrammarFileMethod = antlrHierarchyClass.getMethod( "readGrammarFile", Helper.STRING_ARG_SIGNATURE );
            getFileMethod = antlrHierarchyClass.getMethod( "getFile", Helper.STRING_ARG_SIGNATURE );
        }
        catch ( Throwable t )
        {
            throw new MojoExecutionException( "Unable to instantiate Antlr preprocessor tool", causeToUse( t ) );
        }

        ArrayList files = new ArrayList();
        for ( int i = 0; i < grammars.length; i++ )
        {
            String grammarName = grammars[i].getName().trim();
            if ( StringUtils.isEmpty( grammarName ) )
            {
                environment.getLog().info( "Empty grammar in the configuration; skipping." );
                continue;
            }

            File grammar = new File( environment.getSourceDirectory(), grammarName );

            if ( !grammar.exists() )
            {
                throw new MojoExecutionException( "The grammar '" + grammar.getAbsolutePath() + "' doesnt exist." );
            }

            String grammarFilePath = grammar.getPath();
            GrammarFile grammarFile =
                new GrammarFile(
                                 grammarName,
                                 grammarFilePath,
                                 StringUtils.isNotEmpty( grammars[i].getGlib() ) ? StringUtils.split( grammars[i].getGlib(),
                                                                                                      ":," )
                                                 : new String[0] );

            // :( antlr.preprocessor.GrammarFile's only access to package is through a protected field :(
            try
            {
                BufferedReader in = new BufferedReader( new FileReader( grammar ) );
                try
                {
                    String line;
                    while ( ( line = in.readLine() ) != null )
                    {
                        line = line.trim();
                        if ( line.startsWith( "package" ) && line.endsWith( ";" ) )
                        {
                            grammarFile.setPackageName( line.substring( 8, line.length() - 1 ) );
                            break;
                        }
                    }
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch ( IOException ioe )
                    {
                    }
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

            files.add( grammarFile );

            try
            {
                readGrammarFileMethod.invoke( hierarchy, new Object[] { grammarFilePath } );
            }
            catch ( Throwable t )
            {
                throw new MojoExecutionException( "Unable to use Antlr preprocessor to read grammar file",
                                                  causeToUse( t ) );
            }
        }

        XRef xref = new XRef( hierarchy );
        Iterator itr = files.iterator();
        while ( itr.hasNext() )
        {
            final GrammarFile gf = (GrammarFile) itr.next();
            String grammarFilePath = gf.getFileName();
            try
            {
                Object antlrGrammarFileDef = getFileMethod.invoke( hierarchy, new Object[] { grammarFilePath } );
                intrepretMetadata( gf, antlrGrammarFileDef );
                xref.addGrammarFile( gf );
            }
            catch ( Throwable t )
            {
                throw new MojoExecutionException( "Unable to build grammar metadata", causeToUse( t ) );
            }
        }

        return xref;
    }

    private void intrepretMetadata( GrammarFile gf, Object antlrGrammarFileDef )
        throws MojoExecutionException
    {
        // try {
        // Field headerActionField = helper.getAntlrGrammarFileClass().getDeclaredField( "headerAction" );
        // headerActionField.setAccessible( true );
        // String header = ( String ) headerActionField.get( antlrGrammarFileDef );
        // if ( StringUtils.isNotEmpty( header ) ) {
        // // locate the package declaration...
        // StringTokenizer tokenizer = new StringTokenizer( header, System.getProperty("line.separator") );
        // while ( tokenizer.hasMoreTokens() ) {
        // final String token = tokenizer.nextToken().trim();
        // if ( token.startsWith( "package " ) && token.endsWith( ";" ) ) {
        // gf.setPackageName( token.substring( 8, token.length() -1 ) );
        // }
        // }
        // }
        // }
        // catch ( Throwable t ) {
        // throw new MojoExecutionException( "Error attempting to locate grammar package name", t );
        // }

        try
        {
            Object grammarsVector =
                helper.getAntlrGrammarFileClass().getMethod( "getGrammars", Helper.NO_ARG_SIGNATURE ).invoke( antlrGrammarFileDef,
                                                                                                              Helper.NO_ARGS );

            Enumeration grammars =
                (Enumeration) helper.getAntlrIndexedVectorClass().getMethod( "elements", Helper.NO_ARG_SIGNATURE ).invoke( grammarsVector,
                                                                                                                           Helper.NO_ARGS );
            while ( grammars.hasMoreElements() )
            {
                Grammar grammar = new Grammar( gf );
                intrepret( grammar, grammars.nextElement() );
            }
        }
        catch ( Throwable t )
        {
            throw new MojoExecutionException( "Error attempting to access grammars within grammar file", t );
        }
    }

    private void intrepret( Grammar grammar, Object antlrGrammarDef )
        throws MojoExecutionException
    {
        try
        {
            Method getNameMethod = helper.getAntlrGrammarClass().getDeclaredMethod( "getName", Helper.NO_ARG_SIGNATURE );
            getNameMethod.setAccessible( true );
            String name = (String) getNameMethod.invoke( antlrGrammarDef, Helper.NO_ARGS );
            grammar.setClassName( name );

            Method getSuperGrammarNameMethod =
                helper.getAntlrGrammarClass().getMethod( "getSuperGrammarName", Helper.NO_ARG_SIGNATURE );
            getSuperGrammarNameMethod.setAccessible( true );
            String superGrammarName = (String) getSuperGrammarNameMethod.invoke( antlrGrammarDef, Helper.NO_ARGS );
            grammar.setSuperGrammarName( superGrammarName );

            Method getOptionsMethod = helper.getAntlrGrammarClass().getMethod( "getOptions", Helper.NO_ARG_SIGNATURE );
            getOptionsMethod.setAccessible( true );
            Object options = getOptionsMethod.invoke( antlrGrammarDef, Helper.NO_ARGS );

            Method getElementMethod =
                helper.getAntlrIndexedVectorClass().getMethod( "getElement", new Class[] { Object.class } );
            getElementMethod.setAccessible( true );

            Method getRHSMethod = helper.getAntlrOptionClass().getMethod( "getRHS", Helper.NO_ARG_SIGNATURE );
            getRHSMethod.setAccessible( true );

            Object importVocabOption = getElementMethod.invoke( options, new Object[] { "importVocab" } );
            if ( importVocabOption != null )
            {
                String importVocab = (String) getRHSMethod.invoke( importVocabOption, Helper.NO_ARGS );
                if ( importVocab != null )
                {
                    importVocab = importVocab.trim();
                    if ( importVocab.endsWith( ";" ) )
                    {
                        importVocab = importVocab.substring( 0, importVocab.length() - 1 );
                    }
                    grammar.setImportVocab( importVocab );
                }
            }

            Object exportVocabOption = getElementMethod.invoke( options, new Object[] { "exportVocab" } );
            if ( exportVocabOption != null )
            {
                String exportVocab = (String) getRHSMethod.invoke( exportVocabOption, Helper.NO_ARGS );
                if ( exportVocab != null )
                {
                    exportVocab = exportVocab.trim();
                    if ( exportVocab.endsWith( ";" ) )
                    {
                        exportVocab = exportVocab.substring( 0, exportVocab.length() - 1 );
                    }
                }
                grammar.setExportVocab( exportVocab );
            }
        }
        catch ( Throwable t )
        {
            throw new MojoExecutionException( "Error accessing  Antlr grammar metadata", t );
        }
    }

    private Throwable causeToUse( Throwable throwable )
    {
        if ( throwable instanceof InvocationTargetException )
        {
            return ( (InvocationTargetException) throwable ).getTargetException();
        }
        else
        {
            return throwable;
        }
    }
}
