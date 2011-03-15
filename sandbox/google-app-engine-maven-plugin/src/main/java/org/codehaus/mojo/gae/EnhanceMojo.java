package org.codehaus.mojo.gae;

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
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Post-compilation "enhancement" to associate data classes with the JPA implementation.
 * 
 * @goal enhance
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @see http://code.google.com/intl/fr/appengine/docs/java/datastore/usingjpa.html#Enhancing_Data_Classes
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class EnhanceMojo
    extends AbstractGoogleAppEngineMojo
{

    /**
     * persistence API used : "JDO" or "JPA"
     *
     * @parameter default-value="JDO"
     */
    private String api;

    /**
     * Name of the ClassEnhancer to use. Options ASM
     *
     * @parameter default-value="ASM"
     */
    private String enhancer;

    /**
     * @parameter
     */
    private String[] includes;

    /**
     * @parameter
     */
    private String[] excludes;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @readonly
     * @required
     */
    private File outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
        if ( includes == null )
        {
            includes = new String[] { "**/*.class" };
        }

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( outputDirectory );
        scanner.setIncludes( includes );
        if ( excludes != null )
        {
            scanner.setExcludes( excludes );
        }
        scanner.scan();
        String[] included = scanner.getIncludedFiles();
        List<String> args = new LinkedList<String>();
        if ( getLog().isDebugEnabled() )
        {
            args.add( "-v" );
        }

        args.add( "-d" );
        args.add( outputDirectory.getAbsolutePath() );
        args.add( "-enhancerName" );
        args.add( enhancer );
        args.add( "-api" );
        args.add( api );
        for ( String relativePath : included )
        {
            File clazz = new File( outputDirectory, relativePath );
            args.add( clazz.getAbsolutePath() );
        }
        executeSDKClass( "org.datanucleus.enhancer.DataNucleusEnhancer",
            (String[]) args.toArray( new String[args.size()] ), true );
    }

}
