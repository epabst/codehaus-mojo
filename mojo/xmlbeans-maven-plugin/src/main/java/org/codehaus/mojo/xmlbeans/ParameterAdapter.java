package org.codehaus.mojo.xmlbeans;

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

import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xmlbeans.impl.tool.SchemaCompiler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:kris.bravo@corridor-software.us">Kris Bravo</a>
 * @version $Id$
 */
public final class ParameterAdapter
{

    /**
     * Creates a new instance of ParameterAdapter.
     */
    private ParameterAdapter()
    {
    }

    /**
     * Returns a parameter set appropriate for the SchemaCompiler.
     *
     * @param properties XML Bean Plugin properties.
     * @return Parameters for the schema compiler
     * @throws DependencyResolutionRequiredException
     *          Maven Dependencies were
     *          not resolved.
     * @number MXMLBEANS-3
     * @throws XmlBeansException if we cannot translate the compiler parameters
     */
    static SchemaCompiler.Parameters getCompilerParameters( final PluginProperties properties )
            throws DependencyResolutionRequiredException, XmlBeansException
    {
        SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();

        params.setBaseDir( properties.getBaseDir() );
        params.setXsdFiles( properties.getXsdFiles() );
        params.setWsdlFiles( properties.getWsdlFiles() );
        params.setJavaFiles( properties.getJavaFiles() );
        params.setConfigFiles( properties.getConfigFiles() );
        params.setClasspath( properties.getClasspath() );
        params.setOutputJar( properties.getOutputJar() );
        params.setName( properties.getName() );
        params.setSrcDir( properties.getGeneratedSourceDirectory() );
        params.setClassesDir( properties.getGeneratedClassesDirectory() );
        params.setCompiler( properties.getCompiler() );
        params.setMemoryInitialSize( properties.getMemoryInitialSize() );
        params.setMemoryMaximumSize( properties.getMemoryMaximumSize() );
        params.setNojavac( properties.isNoJavac() );
        params.setQuiet( properties.isQuiet() );
        params.setVerbose( properties.isVerbose() );
        params.setDownload( properties.isDownload() );
        params.setNoUpa( properties.isNoUpa() );
        params.setNoPvr( properties.isNoPvr() );
        params.setNoAnn( properties.isNoAnn() );
        params.setNoVDoc( properties.isNoVDoc() );
        params.setDebug( properties.isDebug() );
        if ( properties.hasCatalogFile() )
        {
            CatalogManager catalogManager = CatalogManager.getStaticManager();
            catalogManager.setCatalogFiles( properties.getCatalogFile() );
            EntityResolver entityResolver = new CatalogResolver();
            entityResolver = new PassThroughResolver( entityResolver );
            params.setEntityResolver( entityResolver );
        }
        params.setErrorListener( properties.getErrorListeners() );
        params.setRepackage( properties.getRepackage() );
        params.setExtensions( properties.getExtensions() );
        params.setMdefNamespaces( properties.getMdefNamespaces() );
        params.setJavaSource( properties.getJavaSource() );
        return params;
    }

    private static class PassThroughResolver implements EntityResolver
    {
        private final EntityResolver delegate;

        public PassThroughResolver( EntityResolver delegate )
        {
            this.delegate = delegate;
        }

        public InputSource resolveEntity( String publicId,
                                          String systemId )
                throws SAXException, IOException
        {
            if ( delegate != null )
            {
                InputSource is = delegate.resolveEntity( publicId, systemId );
                if ( is != null )
                {
                    return is;
                }
            }
            System.out.println( "Could not resolve publicId: " + publicId + ", systemId: " + systemId 
                                + " from catalog, looking in current directory" );
            return new InputSource( systemId );
        }

    }

}
