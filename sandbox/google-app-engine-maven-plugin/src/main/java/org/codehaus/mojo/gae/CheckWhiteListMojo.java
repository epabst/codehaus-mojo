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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.jvnet.animal_sniffer.ClassFileVisitor;
import org.jvnet.animal_sniffer.PackageListBuilder;
import org.jvnet.animal_sniffer.SignatureChecker;

/**
 * Check source code for compatibility with GAE runtime white list. Another option is to use siganture chercker plugin
 * (http://weblogs.java.net/blog/kohsuke/archive/2008/11/compiling_with.html) with a GAE dedicated signature file.
 * 
 * @goal check
 * @phase process-sources
 * @requiresDependencyResolution compile
 * @see http://code.google.com/intl/fr-FR/appengine/docs/java/jrewhitelist.html
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class CheckWhiteListMojo
    extends AbstractGoogleAppEngineMojo
{
    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * Project classpath.
     * 
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    protected List<String> classpathElements;

    /**
     * Animal-sniffer signature file to use. Detected based on the SDK version if not set.
     * 
     * @parameter
     */
    private File signatureFile;

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            // Mostly a copy/paste from
            // https://animal-sniffer.dev.java.net/svn/animal-sniffer/trunk/animal-sniffer/src/main/java/org/jvnet/animal_sniffer/maven/CheckSignatureMojo.java

            InputStream sig;
            if ( signatureFile == null )
            {
                String signature = "whitelist-" + getSDKVersion() + ".sig.gz";
                sig = getClass().getResourceAsStream( signature );
            }
            else
            {
                sig = new FileInputStream( signatureFile );
            }

            getLog().info( "Checking Google App Engine white list" );
            final boolean[] hadError = new boolean[1];

            // just check code from this module
            new SignatureChecker( sig, buildPackageList() )
            {
                protected void reportError( String msg )
                {
                    hadError[0] = true;
                    getLog().error( msg );
                }

                protected void process( String name, InputStream image )
                    throws IOException
                {
                    getLog().debug( name );
                    super.process( name, image );
                }
            }.process( outputDirectory );

            if ( hadError[0] )
            {
                throw new MojoExecutionException(
                    "Signature errors found. Verify them and put @IgnoreJRERequirement on them." );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to check signatures", e );
        }
    }

    /**
     * List of packages defined in the application.
     */
    private Set buildPackageList()
        throws IOException
    {
        PackageListBuilder plb = new PackageListBuilder();
        apply( plb );
        return plb.packages;
    }

    private void apply( ClassFileVisitor v )
        throws IOException
    {
        v.process( outputDirectory );
        for ( Iterator<String> itr = classpathElements.iterator(); itr.hasNext(); )
        {
            String path = (String) itr.next();
            v.process( new File( path ) );
        }
    }
}
