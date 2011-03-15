/*
 * maven-docbook-plugin - Copyright (C) 2005 OPEN input - http://www.openinput.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *   
 * $Id$
 */
package org.codehaus.mojo.docbook;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * A helper class for transforming DocBook documents into different output formats.
 * 
 * @author jgonzalez
 * @author <a href="mailto:lars.trieloff@mindquarry.com">Lars Trieloff</a>
 */
public class DocumentTransformer
{
    private Set sourcePatterns;

    protected Log log;

    protected File sourceDirectory;

    protected File resourceDirectory;

    protected File databaseDirectory;

    protected File outputDirectory;

    protected URI stylesheetLocation;

    private boolean generateHtml;

    private boolean generatePdf;

    private MojoURIResolver mojoResolver;

    private String xslfoCustomization;

    private String xhtmlCustomization;

    /**
     * @param log
     * @param sourceDirectory
     * @param outputDirectory
     * @param customizations
     * @param artifacts
     */
    public DocumentTransformer( Log log, File sourceDirectory, File resourceDirectory, File databaseDirectory,
                                File outputDirectory, URI stylesheetLocation, Map customizations, Collection artifacts )
    {
        this.log = log;
        this.sourceDirectory = sourceDirectory;
        this.resourceDirectory = resourceDirectory;
        this.databaseDirectory = databaseDirectory;
        this.outputDirectory = outputDirectory;
        this.stylesheetLocation = stylesheetLocation;
        this.mojoResolver = new MojoURIResolver( artifacts );

        this.xslfoCustomization = (String) customizations.get( "pdf" );
        this.xhtmlCustomization = (String) customizations.get( "xhtml" );

        this.sourcePatterns = new HashSet( 2 );
        this.sourcePatterns.add( "*.xml" );
        this.sourcePatterns.add( "**/*xml" );
    }

    public void transform()
    {
        this.transform( null );
    }

    /**
     * @param transformProfile
     */
    public void transform( TransformProfile transformProfile )
    {
        StaleSourceScanner scanner = new StaleSourceScanner( 0, this.sourcePatterns, Collections.EMPTY_SET );
        String profile = getFileExtension( transformProfile );
        if ( generateHtml )
        {
            scanner.addSourceMapping( new SuffixMapping( ".xml", profile + "html" ) );
        }
        if ( generatePdf )
        {
            scanner.addSourceMapping( new SuffixMapping( ".xml", profile + "pdf" ) );
        }
        Set staleDocbookFiles;
        try
        {
            staleDocbookFiles = scanner.getIncludedSources( this.sourceDirectory, this.outputDirectory );
        }
        catch ( InclusionScanException e )
        {
            throw new RuntimeException( "Error scanning sources in " + sourceDirectory, e );
        }

        if ( staleDocbookFiles.size() > 0 )
        {
            DirectoryScanner docbookScanner = new DirectoryScanner();
            docbookScanner.setBasedir( this.sourceDirectory );
            docbookScanner.setFollowSymlinks( true );
            docbookScanner.setIncludes( new String[] { "**/*.xml", "*.xml" } );
            docbookScanner.scan();
            String[] docbookFiles = docbookScanner.getIncludedFiles();

            this.prepareFileSystem( docbookFiles );
            this.transformDocuments( staleDocbookFiles, transformProfile );
        }
        else
        {
            this.log.info( "Generated docbook files up to date" );
        }

        if ( this.resourceDirectory.exists() )
        {
            try
            {
                DirectoryScanner resourceScanner = new DirectoryScanner();
                resourceScanner.setBasedir( this.resourceDirectory);
                resourceScanner.setExcludes(DirectoryScanner.DEFAULTEXCLUDES);
                resourceScanner.scan();
                String[] includedFiles = resourceScanner.getIncludedFiles();

                for ( int i = 0;  i < includedFiles.length; i++)
                {
                    String name = includedFiles[i];

                    File sourceFile = new File( this.resourceDirectory, name );

                    File destinationFile = new File( this.outputDirectory, name);

                    if ( !destinationFile.getParentFile().exists() )
                    {
                        destinationFile.getParentFile().mkdirs();
                    }

                    FileUtils.copyFile( sourceFile, destinationFile);
                }
            }
            catch ( IOException e )
            {
                throw new RuntimeException( "Unable to copy directory from " + resourceDirectory + " to "
                    + outputDirectory, e );
            }
        }
        else
        {
            this.outputDirectory.mkdirs();
            this.log.warn( "Specified resource directory does not exist: " + this.resourceDirectory.toString() );
        }
    }

    private String getFileExtension( TransformProfile transformProfile )
    {
        StringBuffer profile = new StringBuffer( "." );
        if ( transformProfile != null )
        {
            profile.append( transformProfile.getId() );
            profile.append( "." );
        }
        return profile.toString();
    }

    /**
     * @param docbookFiles
     */
    protected void prepareFileSystem( String[] docbookFiles )
    {
        this.log.debug( "Creating output directories for the following files - "
            + Arrays.asList( docbookFiles ).toString() );
        this.outputDirectory.mkdirs();
        // TODO: This should be a bit smarter also, shouldn't it?
        for ( int fileIndex = 0; fileIndex < docbookFiles.length; fileIndex++ )
        {
            String docbookFile = docbookFiles[fileIndex];
            int lastFileSeparator = docbookFile.lastIndexOf( File.separator );
            if ( lastFileSeparator > 0 )
            {
                File directory = new File( this.outputDirectory, docbookFile.substring( 0, lastFileSeparator ) );
                directory.mkdirs();
            }
        }
    }

    protected void transformDocuments( Set docbookFiles, TransformProfile transformProfile )
    {
        this.log.info( "Transforming " + docbookFiles.size() + " Docbook stale file(s)" );
        Transformer xhtmlTransformer = null;
        Transformer xslfoTransformer = null;
        FopFactory fopFactory = null;
        URI olinkDBURI = new File( this.databaseDirectory + File.separator + "olinkdb.xml" ).toURI();

        if ( generateHtml )
        {
            xhtmlTransformer = createXHTMLTransformer( olinkDBURI, transformProfile );
        }
        if ( generatePdf )
        {
            xslfoTransformer = createXSLFOTransformer( olinkDBURI, transformProfile );
            fopFactory = FopFactory.newInstance();
            fopFactory.setURIResolver( this.mojoResolver );
        }
        Iterator filesIterator = docbookFiles.iterator();
        while ( filesIterator.hasNext() )
        {
            File docbookFile = (File) filesIterator.next();
            if ( generateHtml )
            {
                transformXhtml( xhtmlTransformer, docbookFile, transformProfile );
            }
            if ( generatePdf )
            {
                transformPdf( xslfoTransformer, docbookFile, fopFactory, transformProfile );
            }
        }
    }

    private Transformer createXSLFOTransformer( URI olinkDBURI, TransformProfile transformProfile )
    {
        Transformer xslfoTransformer;
        TransformerFactory xslfoTf = TransformerFactory.newInstance();
        xslfoTf.setURIResolver( this.mojoResolver );
        Source docbookStyleSheetSource;
        String location = this.stylesheetLocation.toASCIIString() + "fo/";
        String xslt = null;

        if ( ( this.xslfoCustomization == null ) && ( transformProfile == null ) )
        {
            xslt = "docbook.xsl";
        }
        else if ( ( this.xslfoCustomization == null ) && ( transformProfile != null ) )
        {
            xslt = "profile-docbook.xsl";
        }
        else
        {
            location = this.sourceDirectory.toURI().toString();
            xslt = this.xslfoCustomization;
        }

        try
        {
            docbookStyleSheetSource = this.mojoResolver.resolve( xslt, location );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to resolve " + location + "/" + xslt, e );
        }

        try
        {
            xslfoTransformer = xslfoTf.newTransformer( docbookStyleSheetSource );
        }
        catch ( TransformerConfigurationException e )
        {
            throw new RuntimeException( "Unable to create new instance of transformer from source "
                + docbookStyleSheetSource.getSystemId() );
        }
        xslfoTransformer.setParameter( "target.database.document", olinkDBURI.toString() );

        if ( transformProfile != null )
        {
            transformProfile.setParameters( xslfoTransformer );
        }

        this.log.debug( "XSL:FO Style sheet loaded." );
        return xslfoTransformer;
    }

    private Transformer createXHTMLTransformer( URI olinkDBURI, TransformProfile transformProfile )
    {
        Transformer xhtmlTransformer;
        TransformerFactory xhtmlTf = TransformerFactory.newInstance();
        xhtmlTf.setURIResolver( this.mojoResolver );
        Source docbookStyleSheetSource;
        String location = this.stylesheetLocation.toASCIIString() + "xhtml/";
        String xslt = null;

        if ( ( this.xslfoCustomization == null ) && ( transformProfile == null ) )
        {
            xslt = "docbook.xsl";
        }
        else if ( ( this.xslfoCustomization == null ) && ( transformProfile != null ) )
        {
            xslt = "profile-docbook.xsl";
        }
        else
        {
            location = this.sourceDirectory.toURI().toString();
            xslt = this.xhtmlCustomization;
        }

        try
        {
            docbookStyleSheetSource = this.mojoResolver.resolve( xslt, location );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to resolve " + location + "/" + xslt, e );
        }

        try
        {
            xhtmlTransformer = xhtmlTf.newTransformer( docbookStyleSheetSource );
        }
        catch ( TransformerConfigurationException e )
        {
            throw new RuntimeException( "Unable to create new instance of transformer from source "
                + docbookStyleSheetSource.getSystemId() );
        }
        xhtmlTransformer.setParameter( "target.database.document", olinkDBURI.toString() );
        xhtmlTransformer.setParameter( "generate.toc", "" );

        if ( transformProfile != null )
        {
            transformProfile.setParameters( xhtmlTransformer );
        }

        this.log.debug( "XHTML Style sheet loaded." );
        return xhtmlTransformer;
    }

    private void transformXhtml( Transformer documentTransformer, File docbookFile, TransformProfile transformProfile )
    {
        this.log.debug( "Processing " + this.sourceDirectory + File.separator + docbookFile );

        Source source;
        try
        {
            source = this.mojoResolver.resolve( docbookFile.toURI().toString(), null );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to resolve " + docbookFile.toURI().toString(), e );
        }

        String relativePath = docbookFile.getAbsolutePath().substring(
                                                                       (int) this.sourceDirectory.getAbsolutePath()
                                                                           .length() );

        File resultFile = new File( this.outputDirectory, relativePath.substring( 0, relativePath.lastIndexOf( '.' ) )
            + getFileExtension( transformProfile ) + "html" );
        Result result = new StreamResult( resultFile.getAbsolutePath() );

        documentTransformer.setParameter( "current.docid", OLinkDBUpdater.computeFileID( relativePath ) );
        // TODO: Parametrize this !!!!
        documentTransformer.setParameter( "html.stylesheet", this.pathToResources( relativePath ) + "css/xhtml.css" );

        try
        {
            documentTransformer.transform( source, result );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to transform from source " + source.getSystemId() + " into "
                + result.getSystemId(), e );
        }

        this.log.debug( "Generated " + this.databaseDirectory + File.separator + docbookFile );
    }

    private void transformPdf( Transformer documentTransformer, File docbookFile, FopFactory fopFactory,
                               TransformProfile transformProfile )
    {
        this.log.debug( "Processing " + this.sourceDirectory + File.separator + docbookFile );

        Source source;
        try
        {
            source = this.mojoResolver.resolve( docbookFile.toURI().toString(), null );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to resolve " + docbookFile.toURI().toString(), e );
        }

        String relativePath = docbookFile.getAbsolutePath().substring(
                                                                       (int) this.sourceDirectory.getAbsolutePath()
                                                                           .length() );
        File resultFile = new File( this.outputDirectory, relativePath.substring( 0, relativePath.lastIndexOf( '.' ) )
            + getFileExtension( transformProfile ) + "pdf" );
        this.mojoResolver.setDocumentUri( docbookFile.toURI() );
        documentTransformer.setParameter( "current.docid", OLinkDBUpdater.computeFileID( relativePath ) );

        OutputStream out;
        try
        {
            out = new BufferedOutputStream( new FileOutputStream( resultFile.getAbsolutePath() ) );
        }
        catch ( FileNotFoundException e )
        {
            throw new RuntimeException( "File not found " + resultFile.getAbsolutePath(), e );
        }

        Result intermediate;
        try
        {
            Fop fop = fopFactory.newFop( MimeConstants.MIME_PDF, out );
            intermediate = new SAXResult( fop.getDefaultHandler() );
        }
        catch ( FOPException e )
        {
            throw new RuntimeException( "Unable to create FOP instance", e );
        }

        try
        {
            documentTransformer.transform( source, intermediate );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to transform from source " + source.getSystemId() + " into "
                + intermediate.getSystemId(), e );
        }
        finally
        {
            try
            {
                out.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
        this.log.debug( "Generated " + this.databaseDirectory + File.separator + docbookFile );
    }

    protected String pathToResources( String relativePath )
    {
        StringBuffer pathToResources = new StringBuffer();
        int separatorIndex = relativePath.indexOf( File.separator, 1 );
        while ( separatorIndex != -1 )
        {
            pathToResources.append( "../" );
            separatorIndex = relativePath.indexOf( File.separator, separatorIndex + 1 );
        }
        return pathToResources.toString();
    }

    /**
     * Enables a specified output format.
     * 
     * @param format the format
     */
    public void enableOutputFormat( String format )
    {
        if ( "xhtml".equalsIgnoreCase( format ) )
        {
            generateHtml = true;
        }
        if ( "pdf".equalsIgnoreCase( format ) )
        {
            generatePdf = true;
        }

    }

}
