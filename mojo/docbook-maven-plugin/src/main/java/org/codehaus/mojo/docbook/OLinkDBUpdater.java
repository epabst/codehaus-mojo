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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @author jgonzalez
 */
public class OLinkDBUpdater
{
    protected Log log;

    protected File sourceDirectory;

    protected File databaseDirectory;

    protected URI stylesheetLocation;

    protected Collection artifacts;

    /**
     * @param log
     * @param sourceDirectory
     * @param databaseDirectory
     * @param artifacts
     */
    public OLinkDBUpdater( Log log, File sourceDirectory, File databaseDirectory, URI stylesheetLocation,
                           Collection artifacts )
    {
        this.log = log;
        this.sourceDirectory = sourceDirectory;
        this.databaseDirectory = databaseDirectory;
        this.stylesheetLocation = stylesheetLocation;
        this.artifacts = artifacts;
    }

    public void update()
    {
        StaleSourceScanner scanner = new StaleSourceScanner( 0, Collections.singleton( "**/*.xml" ),
                                                             Collections.EMPTY_SET );
        scanner.addSourceMapping( new SuffixMapping( ".xml", ".xml.db" ) );

        Set staleDocbookFiles;
        try
        {
            staleDocbookFiles = (Set) scanner.getIncludedSources( this.sourceDirectory, this.databaseDirectory );
        }
        catch ( InclusionScanException e )
        {
            throw new RuntimeException( "Error scanning sources in " + sourceDirectory, e );
        }

        if ( !staleDocbookFiles.isEmpty() )
        {
            DirectoryScanner docbookScanner = new DirectoryScanner();
            docbookScanner.setBasedir( this.sourceDirectory );
            docbookScanner.setFollowSymlinks( true );
            docbookScanner.setIncludes( new String[] { "**/*.xml" } );
            docbookScanner.scan();
            String[] docbookFiles = docbookScanner.getIncludedFiles();

            this.prepareFileSystem( docbookFiles );
            this.updateOLinkDatabase( staleDocbookFiles );
            this.createMasterOLinkDatabase( docbookFiles );
        }
        else
        {
            this.log.info( "olink database up to date" );
        }
    }

    /**
     * @param docbookFiles
     */
    protected void prepareFileSystem( String[] docbookFiles )
    {
        log.debug( "Creating database directories for the following files - "
            + Arrays.asList( docbookFiles ).toString() );
        // TODO: This should be a bit smarter also, shouldn't it?
        for ( int fileIndex = 0; fileIndex < docbookFiles.length; fileIndex++ )
        {
            String docbookFile = docbookFiles[fileIndex];
            int lastFileSeparator = docbookFile.lastIndexOf( File.separator );
            if ( lastFileSeparator > 0 )
            {
                File directory = new File( this.databaseDirectory, docbookFile.substring( 0, lastFileSeparator ) );
                directory.mkdirs();
            }
        }
    }

    /**
     * @param docbookFiles
     */
    protected void updateOLinkDatabase( Set docbookFiles )
    {
        this.log.info( "Loading olink database generation stylesheet" );
        TransformerFactory tf = TransformerFactory.newInstance();
        MojoURIResolver resolver = new MojoURIResolver( artifacts );
        String styleSheetSourceLocation = this.stylesheetLocation.toASCIIString() + "xhtml/";

        Source docbookStyleSheetSource;
        try
        {
            docbookStyleSheetSource = resolver.resolve( "docbook.xsl", styleSheetSourceLocation );
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Unable to resolve " + styleSheetSourceLocation + "/docbook.xsl", e );
        }

        tf.setURIResolver( resolver );
        if ( tf.getFeature( SAXSource.FEATURE ) )
        {
            SAXTransformerFactory stf = ( (SAXTransformerFactory) tf );
        }

        Transformer olinkDBGenerator;
        try
        {
            olinkDBGenerator = tf.newTransformer( docbookStyleSheetSource );
        }
        catch ( TransformerConfigurationException e )
        {
            throw new RuntimeException( "Unable to get a transformer instance from source "
                + docbookStyleSheetSource.getSystemId(), e );
        }

        olinkDBGenerator.setParameter( "collect.xref.targets", "only" );
        olinkDBGenerator.setParameter( "generate.toc", "" );

        this.log.info( "Creating olink database for " + docbookFiles.size() + " Docbook stale file(s)" );
        Iterator filesIterator = docbookFiles.iterator();
        while ( filesIterator.hasNext() )
        {
            File docbookFile = (File) filesIterator.next();
            this.log.debug( "Processing " + this.sourceDirectory + File.separator + docbookFile );

            String relativePath = docbookFile.getAbsolutePath().substring(
                                                                           (int) this.sourceDirectory.getAbsolutePath()
                                                                               .length() );
            File databaseFile = new File( this.databaseDirectory, relativePath + ".db" );
            Source source = new StreamSource( docbookFile );
            Result result = new StreamResult( new OLinkDBUpdater.NullWriter() );

            olinkDBGenerator.setParameter( "targets.filename", databaseFile.getAbsolutePath() );
            try
            {
                olinkDBGenerator.transform( source, result );
            }
            catch ( TransformerException e )
            {
                throw new RuntimeException( "Unable to transform from source " + source.getSystemId() + " into "
                    + result.getSystemId(), e );
            }
            this.log.debug( "Generated " + this.databaseDirectory + File.separator + docbookFile );
        }
    }

    /**
     * @param docbookFiles
     */
    protected void createMasterOLinkDatabase( String[] docbookFiles )
    {
        File file = new File( this.databaseDirectory + System.getProperty( "file.separator" ) + "olinkdb.xml" );
        this.log.info( "Creating master olink database file " + file );
        try
        {
            BufferedWriter masterOlinkDBFile = new BufferedWriter( new FileWriter( file ) );

            // Write header
            masterOlinkDBFile.write( "<?xml version=\"1.0\" encoding=\"utf-8\"?>" );
            masterOlinkDBFile.newLine();
            masterOlinkDBFile.write( "<!DOCTYPE targetset SYSTEM \"" );
            masterOlinkDBFile.write( this.stylesheetLocation.resolve( "common/targetdatabase.dtd" ).toString() );
            masterOlinkDBFile.write( "\" >" );
            masterOlinkDBFile.newLine();

            masterOlinkDBFile.write( "<targetset>" );
            masterOlinkDBFile.newLine();
            masterOlinkDBFile.write( "  <sitemap>" );
            masterOlinkDBFile.newLine();
            masterOlinkDBFile.write( "    <dir name=\"root\">" );
            masterOlinkDBFile.newLine();

            this.writeDirectoryTagBody( masterOlinkDBFile, 1, "", docbookFiles );

            masterOlinkDBFile.write( "    </dir>" );
            masterOlinkDBFile.newLine();
            masterOlinkDBFile.write( "  </sitemap>" );
            masterOlinkDBFile.newLine();
            masterOlinkDBFile.write( "</targetset>" );
            masterOlinkDBFile.newLine();

            masterOlinkDBFile.close();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Error creating OLink database " + file, e );
        }
    }

    /**
     * @param writer
     * @param level
     * @param currentDirectory
     * @param files
     * @throws IOException
     */
    protected void writeDirectoryTagBody( BufferedWriter writer, int level, String currentDirectory, String[] files )
        throws IOException
    {
        int currentDirectoryLength = currentDirectory.length();
        String lastRelativeDirectory = "";
        List subdirectory = new LinkedList();

        for ( int fileIndex = 0; fileIndex < files.length; fileIndex++ )
        {
            String file = files[fileIndex];
            String relativeFile = file.substring( currentDirectoryLength );
            if ( relativeFile.indexOf( File.separator ) == -1 )
            {
                String fileID = OLinkDBUpdater.computeFileID( file );
                writer.write( OLinkDBUpdater.indenting( level ) + "<document targetdoc=\"" );
                writer.write( fileID );
                writer.write( "\" baseuri=\"" );
                writer.write( relativeFile.substring( 0, relativeFile.lastIndexOf( "." ) ) + ".html\">" );
                writer.write( "<xi:include xmlns:xi=\"http://www.w3.org/2003/XInclude\" href=\"" );
                writer.write( file.replace( File.separatorChar, '/' ) + ".db\"/>" );
                writer.write( "</document>" );
                writer.newLine();
            }
            else
            {
                String relativeDirectory = relativeFile.substring( 0, relativeFile.indexOf( File.separator ) );
                if ( !relativeDirectory.equals( lastRelativeDirectory ) )
                {
                    if ( !subdirectory.isEmpty() )
                    {
                        writer.write( OLinkDBUpdater.indenting( level ) + "<dir name=\"" + lastRelativeDirectory
                            + "\">" );
                        writer.newLine();
                        this.writeDirectoryTagBody( writer, level + 1, currentDirectory + File.separator
                            + lastRelativeDirectory, (String[]) subdirectory.toArray( new String[] {} ) );
                        writer.write( OLinkDBUpdater.indenting( level ) + "</dir>" );
                        writer.newLine();
                    }
                    lastRelativeDirectory = relativeDirectory;
                    subdirectory.clear();
                }
                subdirectory.add( file );
            }
        }

        if ( !subdirectory.isEmpty() )
        {
            writer.write( OLinkDBUpdater.indenting( level ) + "<dir name=\"" + lastRelativeDirectory + "\">" );
            writer.newLine();
            this.writeDirectoryTagBody( writer, level + 1, currentDirectory + File.separator + lastRelativeDirectory,
                                        (String[]) subdirectory.toArray( new String[] {} ) );
            writer.write( OLinkDBUpdater.indenting( level ) + "</dir>" );
            writer.newLine();
        }
    }

    /**
     * @param docbookFileName
     * @return
     */
    public static String computeFileID( String docbookFileName )
    {
        String fileID = docbookFileName;
        if ( docbookFileName.indexOf( File.separator ) == 0 )
        {
            fileID = docbookFileName.substring( File.separator.length() );
        }

        return fileID.replace( File.separatorChar, '-' );
    }

    /**
     * @param level
     * @return
     */
    protected static String indenting( int level )
    {
        StringBuffer indent = new StringBuffer( "      " );
        for ( int currentLevel = 1; currentLevel < level; currentLevel++ )
        {
            indent.append( "  " );
        }
        return indent.toString();
    }

    private static class NullWriter
        extends Writer
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#write(char[], int, int)
         */
        // @Override
        public void write( char[] cbuff, int off, int len )
            throws IOException
        {
            // Do nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#flush()
         */
        // @Override
        public void flush()
            throws IOException
        {
            // Do nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#close()
         */
        // @Override
        public void close()
            throws IOException
        {
            // Do nothing
        }
    }
}
