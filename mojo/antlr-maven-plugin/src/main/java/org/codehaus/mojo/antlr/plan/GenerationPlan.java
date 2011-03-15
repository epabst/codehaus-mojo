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

import java.io.File;
import java.util.LinkedHashSet;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class GenerationPlan
{
    private final String id;

    private final File source;

    private final File generationDirectory;

    private File importVocabTokenTypesDirectory;

    private boolean outOfDate;

    private LinkedHashSet collectedSuperGrammarIds = new LinkedHashSet();

    GenerationPlan( String id, File source, File generationDirectory, String[] glibIds )
    {
        this.id = id;
        this.source = source;
        this.generationDirectory = generationDirectory;
        if ( glibIds != null )
        {
            for ( int i = 0; i < glibIds.length; i++ )
            {
                addSuperGrammarId( glibIds[i] );
            }
        }
    }

    public String getId()
    {
        return id;
    }

    public File getSource()
    {
        return source;
    }

    public File getGenerationDirectory()
    {
        return generationDirectory;
    }

    void addSuperGrammarId( String id )
    {
        collectedSuperGrammarIds.add( id );
    }

    public LinkedHashSet getCollectedSuperGrammarIds()
    {
        return collectedSuperGrammarIds;
    }

    public File getImportVocabTokenTypesDirectory()
    {
        return importVocabTokenTypesDirectory;
    }

    void setImportVocabTokenTypesDirectory( File importVocabTokenTypesDirectory )
    {
        this.importVocabTokenTypesDirectory = importVocabTokenTypesDirectory;
    }

    public boolean isOutOfDate()
    {
        return outOfDate;
    }

    void markOutOfDate()
    {
        this.outOfDate = true;
    }
}