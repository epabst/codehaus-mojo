package org.codehaus.mojo.xslt;

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
import org.codehaus.plexus.util.StringUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Transforms XML source files using an XSL style sheet.
 *
 * @goal transform
 * @phase process-sources
 * @author Cameron Taggart
 *
 * copied from JalopyMojo, then modified
 */
public class XsltMojo
    extends AbstractMojo
{
    /**
     * The XSL stylesheet to use.
     *
     * @parameter
     * @required
     */
    private File xslFile;

    /**
     * The directory containing the XML files.
     *
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     */
    private File srcDir;

    /**
     * For Source Directory. Specifies a fileset source file to format.
     * This is a comma- or space-separated list of patterns of files.
     *
     * @parameter default-value="**\/*.xml"
     */
    private String srcIncludes;

    /**
     * For Source Directory. Source files excluded from format.
     * This is a comma- or space-separated list of patterns of files.
     */
    private String srcExcludes;

    /**
     * The destination directory to write the XML files.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File destDir;

    /**
     * A regular expression that will match part of the XML file name for
     * replacement.
     *
     * @parameter
     */
    private String fileNameRegex;

    /**
     * The replacement for the matched regular expression of the XML file name.
     *
     * @parameter
     */
    private String fileNameReplacement;

    /**
     * A Map of parameters to be passed into the style.
     *
     * @parameter
     */
    private Map parameters;


    public void execute()
        throws MojoExecutionException
    {
        try
        {

            // do some input validation
            if ( !xslFile.exists() )
            {
                getLog().error( "XSL file does not exist: " + xslFile );
                return;
            }
            if ( !srcDir.exists() )
            {
                getLog().error( "Source directory does not exist: " + srcDir );
                return;
            }

            if ( !destDir.exists() )
            {
                destDir.mkdirs();
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer( new StreamSource( xslFile ) );

            String[] xmlFiles = getIncludedFiles( srcDir, srcIncludes, srcExcludes );
            getLog().info( "# of XML files: " + xmlFiles.length );

            for ( int i = 0; i < xmlFiles.length; i++ )
            {
                File srcFile = new File( srcDir, xmlFiles[i] );

                String destFileName = xmlFiles[i];
                if ( fileNameRegex != null && fileNameReplacement != null )
                {
                    destFileName = destFileName.replaceAll( fileNameRegex, fileNameReplacement );
                }

                File destFile = new File( destDir, destFileName );

                if ( destFile.exists() && srcFile.lastModified() < destFile.lastModified() )
                {
                    getLog().info( "file up-to-date: " + destFile );
                    continue;
                }

                if ( parameters != null )
                {

                    Set keys = parameters.keySet();

                    for ( Iterator iterator = keys.iterator(); iterator.hasNext(); )
                    {
                        String key = (String) iterator.next();
                        getLog().debug( "Setting Parameter: key=" + key + " value=" + parameters.get( key ) );
                        transformer.setParameter( key, parameters.get( key ) );
                    }
                }

                getLog().info( "transform, srcFile: " + srcFile + ", destFile: " + destFile );
                transformer.transform( new StreamSource( srcFile ), new StreamResult( destFile ) );
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    private String[] getIncludedFiles( File directory, String includes, String excludes )
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( directory );
        scanner.setIncludes( StringUtils.split( includes, "," ) );
        if ( excludes != null )
        {
            scanner.setExcludes( StringUtils.split( excludes, "," ) );
        }
        scanner.scan();

        String[] filesToFormat = scanner.getIncludedFiles();

        return filesToFormat;
    }
}
