package org.codehaus.mojo.solaris;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * Generates a System V package <code>prototype</code> file.
 *
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @goal generate-prototype
 */
public class GeneratePrototypeMojo
    extends AbstractSolarisMojo
{
    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

    /**
     * There directory where <code>pkgmk</code> and <code>pkgtrans</code> will be executed. All files that are to be
     * a part of the package has to be in this directory before the prototype file is generated.
     *
     * @parameter expression="${project.build.directory}/solaris/assembled-pkg"
     */
    private File packageRoot;

    /**
     * A collection of prototype entries and entry collections. If two entries match the same path, the latter will
     * override the first.
     *
     * @parameter
     * @see AbstractPrototypeEntry
     * @see DirectoryEntry
     * @see EditableEntry
     * @see FileEntry
     * @see IEntry
     * @see AbstractEntryCollection
     */
    private List generatedPrototype;

    /**
     * The default values used when generating the prototype. The default value can be overridden by setting
     * directoryDefaults or fileDefaults.
     *
     * @parameter
     */
    private Defaults defaults;

    /**
     * The default values for directories used when generating the prototype. Overrides any defaults specified in
     * the defaults field.
     *
     * @parameter
     */
    private Defaults directoryDefaults;

    /**
     * The default values for files used when generating the prototype. Overrides any defaults specified in
     * the defaults field.
     *
     * @parameter
     */
    private Defaults fileDefaults;

    // -----------------------------------------------------------------------
    // Components
    // -----------------------------------------------------------------------

    /**
     * @component
     */
    private PrototypeGenerator prototypeGenerator;

    // -----------------------------------------------------------------------
    // Mojo Implementation
    // -----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File generatedPrototypeFile = new File( packageRoot, "generated-prototype" );

        if ( !packageRoot.isDirectory() )
        {
            getLog().debug( "Package root is not a directory: " + packageRoot.getAbsolutePath() );

            try
            {
                FileUtils.fileWrite( generatedPrototypeFile.getAbsolutePath(), "" );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error while writing empty file: " +
                    "'" + generatedPrototypeFile.getAbsolutePath() + "'." );
            }

            return;
        }

        mkParentDirs( generatedPrototypeFile );

        Defaults directoryDefaults = defaults;
        directoryDefaults = Defaults.merge( directoryDefaults, Defaults.directoryDefaults() );
        directoryDefaults = Defaults.merge( directoryDefaults, this.directoryDefaults );

        getLog().debug( "Directory defaults: " + directoryDefaults );

        Defaults fileDefaults = defaults;
        fileDefaults = Defaults.merge( fileDefaults, Defaults.fileDefaults() );
        fileDefaults = Defaults.merge( fileDefaults, this.fileDefaults );

        getLog().debug( "File defaults: " + fileDefaults );

        Iterator prototypeFile = prototypeGenerator.generatePrototype( packageRoot, generatedPrototype,
            directoryDefaults, fileDefaults );

        writePrototype( generatedPrototypeFile, prototypeFile );
    }

    public static void writePrototype( File generatedPrototype, Iterator prototypeFile )
        throws MojoFailureException, MojoExecutionException
    {
        FileWriter writer = null;
        try
        {
            writer = new FileWriter( generatedPrototype );
            PrintWriter printer = new PrintWriter( writer );

            while ( prototypeFile.hasNext() )
            {
                printer.println( ( (SinglePrototypeEntry) prototypeFile.next() ).getPrototypeLine() );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error while writing generatedPrototype file: " +
                generatedPrototype.getAbsolutePath() + ".", e );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }
}
