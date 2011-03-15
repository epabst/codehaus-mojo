package org.codehaus.mojo.l10n;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Allows you to do an automated pseudo-localization to test the completeness
 * of your project's internationalization effort. This technique simulates the
 * process of localizing products by prefixing and suffixing all your
 * internationalized messages.
 * <p/>
 * For more information on pseudo-localization, see
 * <a href="http://developers.sun.com/solaris/articles/i18n/I18N_Testing.html">
 * I18N Testing Guidelines and Techniques</a>.
 * <p/>
 * For more general information on localization, see
 * <a href="http://java.sun.com/developer/technicalArticles/Intl/ResourceBundles/">
 * Java Internationalization: Localization with ResourceBundles</a>.
 *
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal pseudo
 * @phase process-classes
 */
public class PseudoLocalizeMojo
    extends AbstractMojo
{

    /**
     * The output directory into which to copy the resources.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File outputDirectory;

    /**
     * The input directory from which we copy the resources.
     * The plugin scans the build output directory by default, in order to have
     * the complete set of resources that end up in the product.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private File inputDirectory;

    /**
     * The list of resources we want to pseudo-localize. If not specified,
     * the default pattern is <code>**&#47;*.properties</code>.
     *
     * @parameter
     */
    private List includes;

    /**
     * The list of resources we don't want to pseudo-localize. By default, no files are excluded.
     *
     * @parameter
     */
    private List excludes;

    private static final String[] DEFAULT_INCLUDES = {"**/*.properties"};

    private static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * Pattern for replacement of localized string values.
     * The plugin iterates over all properties in the property files and replaces the
     * values using {@link java.text.MessageFormat} with this value as a formatting pattern. The
     * pattern is expected to contain this sequence <code>{0}</code> exactly once with a prefix
     * and/or suffix.
     *
     * @parameter default-value="XXX 多少 {0} YYY"
     */
    private String pseudoLocPattern;

    /**
     * Locale name that is used for pseudo-localization.
     * The resulting property files will have the following name:
     * <code>&lt;filename&gt;_&lt;pseudoLocale&gt;.properties</code>.
     *
     * @parameter default-value="xx"
     */
    private String pseudoLocale;

    public void execute()
        throws MojoExecutionException
    {
        if ( pseudoLocPattern.indexOf( "{0}" ) == -1 )
        {
            throw new MojoExecutionException(
                "The pseudoLocPattern parameter with value '" + pseudoLocPattern + "' is misconfigured." );
        }
        generatePseudoLoc();
    }

    protected void generatePseudoLoc()
        throws MojoExecutionException
    {
        if ( !inputDirectory.exists() )
        {
            getLog().info( "Resource input directory does not exist: " + inputDirectory );
            return;
        }

        // this part is required in case the user specified "../something" as destination
        // see MNG-1345
        if ( !outputDirectory.exists() )
        {
            if ( !outputDirectory.mkdirs() )
            {
                throw new MojoExecutionException( "Cannot create resource output directory: " + outputDirectory );
            }
        }

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( inputDirectory );
        if ( includes != null && !includes.isEmpty() )
        {
            scanner.setIncludes( (String[]) includes.toArray( EMPTY_STRING_ARRAY ) );
        }
        else
        {
            scanner.setIncludes( DEFAULT_INCLUDES );
        }

        if ( excludes != null && !excludes.isEmpty() )
        {
            scanner.setExcludes( (String[]) excludes.toArray( EMPTY_STRING_ARRAY ) );
        }

        scanner.addDefaultExcludes();
        scanner.scan();

        List includedFiles = Arrays.asList( scanner.getIncludedFiles() );

        for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
        {
            String name = (String) j.next();

            File source = new File( inputDirectory, name );
            File dest = new File( outputDirectory, name );

            String fileName = "";
            String[] split = StringUtils.split( source.getName(), "." );
            for ( int i = 0; i < split.length - 1; i++ )
            {
                if ( i == split.length - 2 )
                {
                    fileName = fileName + split[i] + "_" + pseudoLocale + ".";
                }
                else
                {
                    fileName = fileName + split[i] + ".";
                }
            }
            fileName = fileName + split[split.length - 1];
            File destinationFile = new File( dest.getParentFile(), fileName );

            getLog().info( "Pseudo-localizing " + name + " bundle file." );

            try
            {
                copyFile( source, destinationFile );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error copying resource " + source, e );
            }
        }
    }

    private void copyFile( File from, final File to )
        throws IOException
    {
        Properties props = new Properties();
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        to.getParentFile().mkdirs();
        try
        {
            in = new BufferedInputStream( new FileInputStream( from ) );
            props.load( in );
            Iterator it = props.keySet().iterator();
            while ( it.hasNext() )
            {
                String key = (String) it.next();
                String val = props.getProperty( key );
                String newVal = MessageFormat.format( pseudoLocPattern, new String[]{val} );
                props.setProperty( key, newVal );
            }
            out = new BufferedOutputStream( new FileOutputStream( to ) );
            props.store( out, "Pseudo Localized bundle file for I18N testing autogenerated by the l10n-maven-plugin." );
        }
        finally
        {
            IOUtil.close( in );
            IOUtil.close( out );
        }
    }
}
