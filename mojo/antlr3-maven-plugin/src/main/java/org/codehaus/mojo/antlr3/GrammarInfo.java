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

import java.util.ArrayList;
import java.util.List;

import org.antlr.tool.BuildDependencyGenerator;


/**
 * A wrapper around a BuildDependencyGenerator and its associated grammar file
 * name.  Knows how to calculate dependency relationships between grammars,
 * using the {@link #dependsOn(GrammarInfo)} method.
 */
class GrammarInfo
{
    private BuildDependencyGenerator dep;

    private String grammarFileName;

    public GrammarInfo( BuildDependencyGenerator dep, String grammarFileName )
    {
        super();
        this.setBuildDependency( dep );
        this.setGrammarFileName( grammarFileName );
    }

    public boolean dependsOn( GrammarInfo other )
    {
        if ( getBuildDependency().getDependenciesFileList() == null )
        {
            return false;
        }
        return !disjoint( getBuildDependency().getDependenciesFileList(),
                          other.getBuildDependency().getGeneratedFileList() );
    }

    /**
     * Backwards compatibility for JDK1.4, which lacks Collections.disjoint()
     */
    private static boolean disjoint( List a, List b )
    {
        ArrayList list = new ArrayList( a );
        list.retainAll( b );
        return list.isEmpty();
    }

    public void setBuildDependency( BuildDependencyGenerator dep )
    {
        this.dep = dep;
    }

    public BuildDependencyGenerator getBuildDependency()
    {
        return dep;
    }

    public void setGrammarFileName( String grammarFileName )
    {
        this.grammarFileName = grammarFileName;
    }

    public String getGrammarFileName()
    {
        return grammarFileName;
    }
}