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

import java.util.List;
import java.util.ArrayList;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class GrammarFile
{
    private final String id;

    private final String fileName;

    private final String[] glibs;

    private String packageName;

    private List grammars = new ArrayList();

    public GrammarFile( String id, String fileName, String[] glibs )
    {
        this.id = id;
        this.fileName = fileName;
        this.glibs = glibs;
    }

    public String getId()
    {
        return id;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String[] getGlibs()
    {
        return glibs;
    }

    public String getPackageName()
    {
        return packageName;
    }

    void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }

    void addGrammar( Grammar grammar )
    {
        grammars.add( grammar );
    }

    public List getGrammars()
    {
        return grammars;
    }
}
