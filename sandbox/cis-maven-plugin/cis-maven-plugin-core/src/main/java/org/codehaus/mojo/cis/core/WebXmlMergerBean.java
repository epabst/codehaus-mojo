package org.codehaus.mojo.cis.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * A bean class, which acts as a frontend for the
 * {@link WebXmlMerger}.
 */
public class WebXmlMergerBean extends AbstractCisBean
{
    private URL[] webXmlFiles;
    private File targetFile;
    private File markerFile;

    /**
     * Returns the marker file.
     */
    public File getMarkerFile() {
        if ( markerFile == null )
        {
            final File dir = getCisMarkersDir();
            if ( dir == null )
            {
                return null;
            }
            setMarkerFile( new File( dir, "web.xml.marker" ) );
        }
        return markerFile;
    }

    /**
     * Sets the marker file.
     */
    public void setMarkerFile( File pMarkerFile ) {
        markerFile = pMarkerFile;
    }

    /**
     * Returns the target file.
     */
    public File getTargetFile()
        throws CisCoreErrorMessage
    {
        if ( targetFile == null )
        {
            final File dir = getCisHomeDir();
            if ( dir == null )
            {
                throw new CisCoreErrorMessage( "You must set either the target file or the cis.home directory." );
            }
            return new File( dir, "WEB-INF/web.xml" );
        }
        return targetFile;
    }

    /**
     * Sets the target file.
     */
    public void setTargetFile( File pTargetFile )
    {
        targetFile = pTargetFile;
    }

    /**
     * Returns the location of the custom "web.xml" file.
     */
    public URL[] getWebXmlFiles()
    {
        return webXmlFiles;
    }

    /**
     * Sets the location of the custom "web.xml" file.
     */
    public void setWebXmlFiles( URL[] pWebXmlFiles )
    {
        webXmlFiles = pWebXmlFiles;
    }

    /**
     * Called to merge the given "web.xml" files into the given
     * target file.
     */
    protected void mergeWebXmlFiles( URL[] pWebXmlFiles, File targetWebXml )
        throws CisCoreException
    {
        try
        {
            Document[] documents = new Document[pWebXmlFiles.length];
            DocumentBuilderFactory dbf = newDocumentBuilderFactory();
            for ( int i = 0;  i < documents.length;  i++ )
            {
                URL u = pWebXmlFiles[i];
                try
                {
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    db.setEntityResolver( new CisCoreEntityResolver( getCisUtils() ));
                    final InputSource isource = new InputSource( u.openStream() );
                    isource.setSystemId( u.toExternalForm() );
                    documents[i] = db.parse( isource );
                }
                catch ( SAXParseException e )
                {
                    throw new CisCoreException( "Faile to parse file "
                                                + u.toExternalForm() + " at line " + e.getLineNumber()
                                                + ", column " + e.getColumnNumber() + ": "
                                                + e.getMessage(), e );
                }
                catch ( SAXException e )
                {
                    throw new CisCoreException( "Failed to parse file "
                                                + u.toExternalForm() + ": " + e.getMessage(),
                                                e );
                }
                catch ( IOException e )
                {
                    throw new CisCoreException( "Failed to read file "
                                                + u.toExternalForm() + ": " + e.getMessage(),
                                                e );
                }
            }
            Document doc = new WebXmlMerger().merge( documents );
            TransformerFactory.newInstance().newTransformer().transform( new DOMSource( doc), new StreamResult( targetWebXml ) );
        }
        catch ( ParserConfigurationException e )
        {
            throw new CisCoreException( e.getMessage(), e );
        }
        catch ( TransformerException e )
        {
            throw new CisCoreException( "Failed to create file "
                                        + targetWebXml.getPath() + ": "
                                        + e.getMessage(), e );
        }
    }

    private DocumentBuilderFactory newDocumentBuilderFactory()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating( false );
        dbf.setNamespaceAware( true );
        return dbf;
    }

    public void execute() throws CisCoreException
    {
        URL[] files = getWebXmlFiles();
        File mrkrFile = getMarkerFile();
        File trgtFile = getTargetFile();
        final CisUtils.Resource targetResource = new DefaultResource( mrkrFile == null ? trgtFile : mrkrFile );
        boolean uptodate = true;
        if ( files == null  ||  files.length == 0 )
        {
            getCisUtils().info( "No web.xml files specified." );
            return;
        }
        for ( int i = 0;  uptodate  &&  i < files.length;  i++ )
        {
            final CisUtils.Resource sourceResource = new DefaultResource( files[i] );
            uptodate = getCisUtils().isUpToDate( sourceResource, targetResource, true );
            if ( !uptodate )
            {
                getCisUtils().debug( "File " + trgtFile
                                     + " isn't uptodate compared to "
                                     + files[i].toExternalForm() );
                break;
            }
        }
        if ( !uptodate )
        {
            getCisUtils().debug( "Generating " + trgtFile );
            mergeWebXmlFiles( webXmlFiles, trgtFile );
            if ( mrkrFile != null )
            {
                getCisUtils().touch( mrkrFile );
            }
        }
        else
        {
            getCisUtils().debug( "File " + trgtFile + " is uptodate." );
        }
    }
}
