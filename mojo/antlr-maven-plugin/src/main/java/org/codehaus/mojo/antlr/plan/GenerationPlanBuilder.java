package org.codehaus.mojo.antlr.plan;

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

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.codehaus.mojo.antlr.Environment;
import org.codehaus.mojo.antlr.metadata.XRef;
import org.codehaus.mojo.antlr.metadata.GrammarFile;
import org.codehaus.mojo.antlr.metadata.Grammar;
import org.codehaus.plexus.util.StringUtils;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class GenerationPlanBuilder
{
    private final Environment environment;

    private final LinkedHashMap generationPlans = new LinkedHashMap();

    private XRef metadataXRef;

    public GenerationPlanBuilder( Environment environment )
    {
        this.environment = environment;
    }

    public synchronized List buildGenerationPlans( XRef metadataXRef )
    {
        this.metadataXRef = metadataXRef;

        Iterator grammarFiles = metadataXRef.iterateGrammarFiles();
        while ( grammarFiles.hasNext() )
        {
            final GrammarFile grammarFile = (GrammarFile) grammarFiles.next();
            // NOTE : loacteOrBuildGenerationPlan populates the generationPlans map
            loacteOrBuildGenerationPlan( grammarFile );
        }

        metadataXRef = null;
        return new ArrayList( generationPlans.values() );
    }

    private GenerationPlan loacteOrBuildGenerationPlan( GrammarFile grammarFile )
    {
        GenerationPlan generationPlan = (GenerationPlan) generationPlans.get( grammarFile.getId() );
        if ( generationPlan == null )
        {
            generationPlan = buildGenerationPlan( grammarFile );
        }
        return generationPlan;
    }

    private GenerationPlan buildGenerationPlan( GrammarFile grammarFile )
    {
        File generationDirectory =
            StringUtils.isEmpty( grammarFile.getPackageName() ) ? environment.getOutputDirectory()
                            : new File( environment.getOutputDirectory(),
                                        grammarFile.getPackageName().replace( '.', File.separatorChar ) );

        GenerationPlan generationPlan =
            new GenerationPlan( grammarFile.getId(), new File( grammarFile.getFileName() ), generationDirectory,
                                grammarFile.getGlibs() );

        File leastRecentGrammarOutput = locateLeastRecentlyModifiedOutputFile( generationDirectory );

        // see if the grammar is out-of-date by way super-grammars from user defined glib options
        for ( int i = 0; i < grammarFile.getGlibs().length; i++ )
        {
            final GrammarFile superGrammarGrammarFile = metadataXRef.getGrammarFileById( grammarFile.getGlibs()[i] );
            final GenerationPlan superGrammarGenerationPlan = loacteOrBuildGenerationPlan( superGrammarGrammarFile );
            if ( superGrammarGenerationPlan.isOutOfDate() )
            {
                generationPlan.markOutOfDate();
            }
            else if ( superGrammarGenerationPlan.getSource().lastModified() > leastRecentGrammarOutput.lastModified() )
            {
                generationPlan.markOutOfDate();
            }
        }

        Iterator grammars = grammarFile.getGrammars().iterator();
        while ( grammars.hasNext() )
        {
            final Grammar grammar = (Grammar) grammars.next();
            final File generatedParserFile =
                new File( environment.getOutputDirectory(), grammar.determineGeneratedParserPath() );

            if ( !generatedParserFile.exists() )
            {
                generationPlan.markOutOfDate();
            }
            else if ( generatedParserFile.lastModified() < generationPlan.getSource().lastModified() )
            {
                generationPlan.markOutOfDate();
            }

            // see if the grammar if out-of-date by way of its super-grammar(s) as gleaned from parsing the grammar file
            if ( StringUtils.isNotEmpty( grammar.getSuperGrammarName() ) )
            {
                final GrammarFile superGrammarGrammarFile =
                    metadataXRef.getGrammarFileByClassName( grammar.getSuperGrammarName() );
                if ( superGrammarGrammarFile != null )
                {
                    final GenerationPlan superGrammarGenerationPlan =
                        loacteOrBuildGenerationPlan( superGrammarGrammarFile );
                    generationPlan.addSuperGrammarId( superGrammarGenerationPlan.getId() );
                    if ( superGrammarGenerationPlan.isOutOfDate() )
                    {
                        generationPlan.markOutOfDate();
                    }
                    else if ( superGrammarGenerationPlan.getSource().lastModified() > generatedParserFile.lastModified() )
                    {
                        generationPlan.markOutOfDate();
                    }
                }
            }

            // see if the grammar if out-of-date by way of its importVocab
            if ( StringUtils.isNotEmpty( grammar.getImportVocab() ) )
            {
                final GrammarFile importVocabGrammarFile =
                    metadataXRef.getGrammarFileByExportVocab( grammar.getImportVocab() );
                if ( importVocabGrammarFile == null )
                {
                    environment.getLog().warn( "unable to locate grammar exporting specifcied import vocab ["
                                                   + grammar.getImportVocab() + "]" );
                }
                else if ( importVocabGrammarFile.getId().equals( grammarFile.getId() ) )
                {

                }
                else
                {
                    final GenerationPlan importVocabGrammarGenerationPlan =
                        loacteOrBuildGenerationPlan( importVocabGrammarFile );
                    generationPlan.setImportVocabTokenTypesDirectory( importVocabGrammarGenerationPlan.getGenerationDirectory() );
                    if ( importVocabGrammarGenerationPlan.isOutOfDate() )
                    {
                        generationPlan.markOutOfDate();
                    }
                    else if ( importVocabGrammarGenerationPlan.getSource().lastModified() > generatedParserFile.lastModified() )
                    {
                        generationPlan.markOutOfDate();
                    }
                }
            }
        }

        generationPlans.put( generationPlan.getId(), generationPlan );
        return generationPlan;
    }

    private static File locateLeastRecentlyModifiedOutputFile( File directory )
    {
        if ( !directory.exists() )
        {
            return null;
        }

        File[] contents = directory.listFiles();
        if ( contents.length == 0 )
        {
            return null;
        }

        File oldest = contents[0];
        for ( int i = 1; i < contents.length; i++ )
        {
            if ( contents[i].lastModified() < oldest.lastModified() )
            {
                oldest = contents[i];
            }
        }

        return oldest;
    }
}