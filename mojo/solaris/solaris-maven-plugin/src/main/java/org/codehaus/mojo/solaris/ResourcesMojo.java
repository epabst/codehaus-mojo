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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;

/**
 * Copies System V-specific resources.
 *
 * This goal will copy the following files from <code>src/main/solaris</code>:
 * <ul>
 *  <li>
 *   <code>pkginfo</code> - The pkginfo file will be read and these values will be interpolated:
 *   <ul>
 *    <li>${project.artifactId}</li>
 *    <li>${project.version}</li>
 *    <li>${project.name}</li>
 *    <li>${project.description}</li>
 *   </ul>
 *  </li>
 *  <li>
 *   <code>prototype</code> - The prototype file will be copied to the directory specified by <code>packageRoot</code>.
 *  </li>
 * </ul>
 *
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @goal resources
 */
public class ResourcesMojo
    extends AbstractSolarisMojo
{
    private static final String[] DEFAULT_INCLUDES = {"**/**"};

    /**
     * Package metadata resources (e.g. prototype and pkginfo) to copy into the package.
     *
     * Will default to <code>src/main/resources-solaris</code>, include all files and then exclude common SCM files.
     * See <a href="http://plexus.codehaus.org/plexus-utils/apidocs/org/codehaus/plexus/util/DirectoryScanner.html">
     * the documentation</a> for the full reference.
     *
     * @parameter
     */
    private Resource solarisResources;

    /**
     * Resources to copy into the package.
     *
     * @parameter expression="${project.resources}"
     * @required
     */
    private List resources;

    /**
     * The <code>pkginfo</code> file to interpolate and copy.
     *
     * @parameter expression="src/main/resources-solaris/pkginfo"
     * @required
     */
    private File pkginfo;

    /**
     * There directory where <code>pkgmk</code> and <code>pkgtrans</code> will be executed. All files that are to be
     * a part of the package has to be in this directory before the prototype file is generated.
     *
     * @parameter expression="${project.build.directory}/solaris/assembled-pkg"
     * @required
     */
    private File packageRoot;

    /**
     * Additional properties to be used when interpolating the <code>pkginfo</code> file.
     *
     * @parameter
     */
    private Properties properties;

    /**
     * The artifact id of the project. Used when interpolating the <code>pkginfo</code> file.
     *
     * @parameter expression="${project.artifactId}"
     * @readonly
     */
    private String artifactId;

    /**
     * The version of the project. Used when interpolating the <code>pkginfo</code> file.
     *
     * @parameter expression="${project.version}"
     * @readonly
     */
    private String version;

    /**
     * The name of the project. Used when interpolating the <code>pkginfo</code> file.
     *
     * @parameter expression="${project.name}"
     * @readonly
     */
    private String name;

    /**
     * The description of the project. Used when interpolating the <code>pkginfo</code> file.
     *
     * @parameter expression="${project.description}"
     * @readonly
     */
    private String description;

    /**
     * @parameter expression="${basedir}"
     * @readonly
     */
    private File basedir;

    // -----------------------------------------------------------------------
    // Mojo Implementation
    // -----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        mkdirs( packageRoot );

        copyResources();
        copySolarisResources();
        copyPkginfo();
    }

    private void copyResources()
        throws MojoExecutionException
    {
        for ( Iterator it = resources.iterator(); it.hasNext(); )
        {
            Resource resource = (Resource) it.next();

            copyResource( resource );
        }
    }

    private void copySolarisResources()
        throws MojoExecutionException
    {
        if ( solarisResources == null )
        {
            solarisResources = new Resource();
            solarisResources.setDirectory( new File( basedir, "src/main/resources-solaris" ).getAbsolutePath() );
        }

        copyResource( solarisResources );
    }

    private void copyPkginfo()
        throws MojoFailureException, MojoExecutionException
    {
        Properties properties = new Properties();

        properties.put( "project.artifactId", artifactId );
        properties.put( "project.version", version );
        properties.put( "project.name", StringUtils.clean( name ) );
        properties.put( "project.description", StringUtils.clean( description ) );

        if ( this.properties != null )
        {
            properties.putAll( this.properties );
        }

        // -----------------------------------------------------------------------
        // Validate
        // -----------------------------------------------------------------------

        if ( !pkginfo.canRead() )
        {
            throw new MojoFailureException( "Can't read template pkginfo file: '" + pkginfo.getAbsolutePath() + "'." );
        }

        File processedPkginfo = new File( packageRoot, "pkginfo" );

        // -----------------------------------------------------------------------
        // Do it!
        // -----------------------------------------------------------------------

        FileWriter pkginfoWriter = null;

        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader( pkginfo );
            InterpolationFilterReader reader = new InterpolationFilterReader( fileReader, properties );

            pkginfoWriter = new FileWriter( processedPkginfo );
            IOUtil.copy( reader, pkginfoWriter );
        }
        catch ( IOException e )
        {
            // All common causes to this should have been removed with previous checks.
            throw new MojoExecutionException( "Error while interpolating pkginfo.", e );
        }
        finally
        {
            IOUtil.close( pkginfoWriter );
            IOUtil.close( fileReader );
        }
    }

    private void copyResource( Resource resource )
        throws MojoExecutionException
    {
        File directory = getFile( resource.getDirectory() );

        if ( directory == null || !directory.isDirectory() )
        {
            getLog().debug( "Not a directory: " + directory );

            return;
        }

        try
        {
            resource.setTargetPath( packageRoot.getAbsolutePath() );
            copy( resource );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not copy files from '" + directory.getAbsolutePath() +
                "' to '" + packageRoot.getAbsolutePath() + "'.", e );
        }
    }

    private void copy( Resource resource )
        throws IOException
    {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( resource.getDirectory() );

        String[] includes = DEFAULT_INCLUDES;

        List includesList = resource.getIncludes();
        if ( includesList != null && !includesList.isEmpty() )
        {
            scanner.setIncludes( (String[]) includesList.toArray( new String[ includesList.size() ] ) );
        }

        scanner.setIncludes( includes );

        String[] excludes = null;

        if ( resource.getExcludes() != null && !resource.getExcludes().isEmpty() )
        {
            excludes = (String[]) resource.getExcludes().toArray( new String[ resource.getExcludes().size() ] );
        }

        scanner.setExcludes( excludes );

        getLog().debug( "Copying resources..." );
        getLog().debug( "  From:     " + resource.getDirectory() );
        getLog().debug( "  To:       " + resource.getTargetPath() );
        getLog().debug( "  Includes: " + StringUtils.join( includes, "," ) );
        if ( excludes != null )
        {
            getLog().debug( "  Excludes: " + StringUtils.join( excludes, "," ) );
        }
        else
        {
            getLog().debug( "  Excludes: none" );
        }

        scanner.addDefaultExcludes();
        scanner.scan();

        List includedFiles = Arrays.asList( scanner.getIncludedFiles() );
        for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
        {
            String name = (String) j.next();

            File source = new File( resource.getDirectory(), name );

            File destinationFile = new File( resource.getTargetPath(), name );

            if ( !destinationFile.getParentFile().exists() )
            {
                destinationFile.getParentFile().mkdirs();
            }

            if ( destinationFile.lastModified() < source.lastModified() )
            {
                FileUtils.copyFile( source, destinationFile );
            }
        }
    }

    private File getFile( String path )
    {
        if ( path == null )
        {
            return null;
        }

        File f = new File( path );

        if ( f.isAbsolute() )
        {
            return f;
        }

        return new File( basedir, path );
    }
}
