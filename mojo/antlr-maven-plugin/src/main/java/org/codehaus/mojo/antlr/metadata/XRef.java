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

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class XRef
{
    private final Object antlrHierarchy;

    private LinkedHashMap filesById = new LinkedHashMap();

    private HashMap filesByExportVocab = new HashMap();

    private HashMap filesByClassName = new HashMap();

    public XRef( Object antlrHierarchy )
    {
        this.antlrHierarchy = antlrHierarchy;
    }

    public Object getAntlrHierarchy()
    {
        return antlrHierarchy;
    }

    void addGrammarFile( GrammarFile grammarFile )
    {
        filesById.put( grammarFile.getId(), grammarFile );
        Iterator itr = grammarFile.getGrammars().iterator();
        while ( itr.hasNext() )
        {
            final Grammar grammar = (Grammar) itr.next();
            filesByClassName.put( grammar.getClassName(), grammarFile );
            if ( grammar.getExportVocab() != null )
            {
                GrammarFile old = (GrammarFile) filesByExportVocab.put( grammar.getExportVocab(), grammarFile );
                if ( old != null && old != grammarFile )
                {
                    System.out.println( "[WARNING] : multiple grammars defined the same exportVocab : "
                        + grammar.getExportVocab() );
                }
            }
        }
    }

    public Iterator iterateGrammarFiles()
    {
        return filesById.values().iterator();
    }

    public GrammarFile getGrammarFileById( String id )
    {
        return (GrammarFile) filesById.get( id );
    }

    public GrammarFile getGrammarFileByClassName( String className )
    {
        return (GrammarFile) filesByClassName.get( className );
    }

    public GrammarFile getGrammarFileByExportVocab( String exportVocab )
    {
        return (GrammarFile) filesByExportVocab.get( exportVocab );
    }
}
