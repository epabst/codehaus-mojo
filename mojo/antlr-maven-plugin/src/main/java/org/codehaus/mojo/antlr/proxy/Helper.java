package org.codehaus.mojo.antlr.proxy;

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

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class Helper
{
    public static final Class[] NO_ARG_SIGNATURE = new Class[0];

    public static final Class[] STRING_ARG_SIGNATURE = new Class[] { String.class };

    public static final Object[] NO_ARGS = new Object[0];

    private final URLClassLoader antlrClassLoader;

    private final Class antlrToolClass;

    private final Class antlrPreprocessorToolClass;

    private final Class antlrHierarchyClass;

    private final Class antlrGrammarFileClass;

    private final Class antlrGrammarClass;

    private final Class antlrOptionClass;

    private final Class antlrIndexedVectorClass;

    public Helper( Artifact antlrArtifact )
        throws MojoExecutionException
    {
        try
        {
            this.antlrClassLoader =
                new URLClassLoader( new URL[] { antlrArtifact.getFile().toURL() }, ClassLoader.getSystemClassLoader() );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Unable to resolve antlr:antlr artifact url", e );
        }

        antlrToolClass = loadAntlrClass( "antlr.Tool" );
        antlrPreprocessorToolClass = loadAntlrClass( "antlr.preprocessor.Tool" );
        antlrHierarchyClass = loadAntlrClass( "antlr.preprocessor.Hierarchy" );
        antlrGrammarFileClass = loadAntlrClass( "antlr.preprocessor.GrammarFile" );
        antlrGrammarClass = loadAntlrClass( "antlr.preprocessor.Grammar" );
        antlrOptionClass = loadAntlrClass( "antlr.preprocessor.Option" );
        antlrIndexedVectorClass = loadAntlrClass( "antlr.collections.impl.IndexedVector" );
    }

    private Class loadAntlrClass( String className )
        throws MojoExecutionException
    {
        try
        {
            return antlrClassLoader.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "could not load Antlr class :" + className, e );
        }
    }

    public Class getAntlrToolClass()
    {
        return antlrToolClass;
    }

    public Class getAntlrPreprocessorToolClass()
    {
        return antlrPreprocessorToolClass;
    }

    public Class getAntlrHierarchyClass()
    {
        return antlrHierarchyClass;
    }

    public Class getAntlrGrammarFileClass()
    {
        return antlrGrammarFileClass;
    }

    public Class getAntlrGrammarClass()
    {
        return antlrGrammarClass;
    }

    public Class getAntlrOptionClass()
    {
        return antlrOptionClass;
    }

    public Class getAntlrIndexedVectorClass()
    {
        return antlrIndexedVectorClass;
    }
}
