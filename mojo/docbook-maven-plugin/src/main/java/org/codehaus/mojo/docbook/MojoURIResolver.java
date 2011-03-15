/*
 * maven-docbook-plugin - Copyright (C) 2006 Mindquarry GmbH - http://www.mindquarry.com/
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.artifact.Artifact;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class MojoURIResolver
    extends CatalogResolver
    implements URIResolver, EntityResolver
{
    private Collection artifacts;

    private URI documentUri = null;

    public MojoURIResolver( Collection artifacts )
    {
        this.artifacts = artifacts;
    }

    public InputSource resolveEntity( final String href, final String base )
    {
        InputSource result;
        String fullUri = makeFullUri( href, base );
        if ( fullUri.startsWith( "resource://" ) )
        {
            try
            {
                result = new InputSource( getResourceAsStream( fullUri ) );
            }
            catch ( IOException ioe )
            {
                result = null;
            }

        }
        else
        {
            result = super.resolveEntity( href, base );
        }
        if ( result == null )
        {
            result = new InputSource( fullUri );
        }
        result.setSystemId( fullUri );
        return result;
    }

    public Source resolve( final String href, final String base )
        throws TransformerException
    {
        Source result;
        String fullUri = makeFullUri( href, base );
        try
        {
            try
            {
                XMLReader reader;
                // get a SAXParserFactory instance
                SAXParserFactory SAXpf = SAXParserFactory.newInstance();
                // enabling the namespaces processing
                SAXpf.setNamespaceAware( true );
                // get a SAXParser object
                SAXParser SAXparser = SAXpf.newSAXParser();
                // get the XMLReader
                reader = SAXparser.getXMLReader();
                reader.setEntityResolver( this );
                // creating the SAXSource
                InputSource insrc = this.resolveEntity( href, base );
                result = new SAXSource( reader, insrc );
            }
            catch ( javax.xml.parsers.ParserConfigurationException e )
            {
                result = new StreamSource( getResourceAsStream( fullUri ) );
            }
            catch ( org.xml.sax.SAXException e )
            {
                result = new StreamSource( getResourceAsStream( fullUri ) );
            }
            result.setSystemId( fullUri );
            return result;
        }
        catch ( IOException ioe )
        {
            throw new TransformerException( "unable to load resource " + fullUri, ioe );
        }
    }

    private InputStream getResourceAsStream( String fullUri )
        throws IOException
    {
        try
        {
            URI uri = new URI( fullUri );

            String groupId = uri.getAuthority();
            String path = uri.getPath().substring( 1 );
            String artifactId = path.substring( 0, path.indexOf( "/" ) );
            String resource = path.substring( path.indexOf( "/" ) );

            for ( Iterator i = this.artifacts.iterator(); i.hasNext(); )
            {
                Artifact a = (Artifact) i.next();
                if ( ( a.getGroupId().equals( groupId ) ) && ( a.getArtifactId().equals( artifactId ) ) )
                {
                    ZipFile jar = new ZipFile( a.getFile() );
                    for ( Enumeration e = jar.entries(); e.hasMoreElements(); )
                    {
                        ZipEntry je = (ZipEntry) e.nextElement();
                        String name = je.getName();
                        if ( !name.startsWith( "/" ) )
                        {
                            name = "/" + name;
                        }
                        if ( name.equals( resource ) )
                        {
                            return jar.getInputStream( je );
                        }
                    }
                }
            }

            return MojoURIResolver.class.getResourceAsStream( fullUri.substring( 10 ) );
        }
        catch ( URISyntaxException use )
        {
            throw new IOException( use.getLocalizedMessage() );
        }
    }

    private String makeFullUri( final String href, final String base )
    {
        String fullUri = href;
        // if it is no absolute url starting with scheme:/
        if ( ( href != null ) && ( base != null ) )
        {
            try
            {
                fullUri = URI.create( base ).resolve( href ).toString();
            }
            catch ( IllegalArgumentException use )
            {
                fullUri = base;
            }
        }
        else if ( ( href != null ) && ( href.indexOf( ":/" ) != -1 ) )
        {
            fullUri = href;
        }
        else if ( base != null )
        {
            fullUri = base;
        }
        else if ( this.documentUri != null )
        {
            fullUri = documentUri.resolve( href ).toString();
        }
        else
        {
            fullUri = href;
        }
        return fullUri;
    }

    public void setDocumentUri( URI documentUri )
    {
        this.documentUri = documentUri;
    }

}
