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

import java.io.File;

import org.codehaus.plexus.util.StringUtils;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class Grammar
{
    private final GrammarFile grammarFile;

    private String className;

    private String superGrammarName;

    private String importVocab;

    private String exportVocab;

    public Grammar( GrammarFile grammarFile )
    {
        this.grammarFile = grammarFile;
        grammarFile.addGrammar( this );
    }

    public GrammarFile getGrammarFile()
    {
        return grammarFile;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public String getSuperGrammarName()
    {
        return superGrammarName;
    }

    public void setSuperGrammarName( String superGrammarName )
    {
        this.superGrammarName = superGrammarName;
    }

    public String getImportVocab()
    {
        return importVocab;
    }

    public void setImportVocab( String importVocab )
    {
        this.importVocab = importVocab;
    }

    public String getExportVocab()
    {
        return exportVocab;
    }

    public void setExportVocab( String exportVocab )
    {
        this.exportVocab = exportVocab;
    }

    public String getPackageName()
    {
        return getGrammarFile().getPackageName();
    }

    public String determineGeneratedParserPath()
    {
        if ( StringUtils.isEmpty( getPackageName() ) )
        {
            return getClassName() + ".java";
        }
        else
        {
            return getPackageName().replace( '.', File.separatorChar ) + File.separatorChar + getClassName() + ".java";
        }
    }
}
