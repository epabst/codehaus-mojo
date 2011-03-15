/* 
 * Copyright 2009 Bernhard Grünewaldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *    
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.codehaus.mojo.sitemapxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * SitemapMojo will produce a sitemap.xml and a zipped version sitemap.xml.gz file following the standard
 * http://www.sitemaps.org/protocol.php.
 * <p>
 * The parameters are:<br>
 * <b>input</b> - The path to the site.xml file. Example: /home/foo/bar/site.xml<br>
 * <b>domainbase</b> - The base for all relative urls from site.xml. Example: http://www.foo.bar/, will be the prefix
 * for all relative URLs.<br>
 * <b>target</b> - The target dir to put the sitemap files. Should be the base dir of the site. Example:
 * /home/foo/bar/target/site/.<br>
 * <p>
 * Run From Eclipse with m2eclipse plugin <br>
 * <b>GOAL</b>: <code>
 * org.codehaus.mojo:sitemapxml-maven-plugin:generate
 * </code>
 * <p>
 * <b>pom.xml</b>: <br>
 * {@code <plugin> <groupId>org.codehaus.mojo</groupId> <artifactId>sitemapxml-maven-plugin</artifactId> <configuration>
 * <input>$ basedir}/src/site/site.xml</input> <domainbase>http://www.foo.bar/</domainbase>
 * <target>${project.reporting.outputDirectory}</target> </configuration> </plugin> * }
 * <p>
 * Required is only domainbase. The other parameters have default values. <br>
 * Before running the plugin you should install it locally with "mvn install".
 * 
 * Generates a sitemap.xml and a sitemap.xml.gz file inside of <b>target</b> dir.
 * 
 * @author Bernhard Grünewaldt
 * @goal generate 
 * 
 */
public class SitemapMojo
    extends AbstractMojo
{

    /**
     * Filename Parameter for site.xml
     * 
     * @parameter expression="${basedir}/src/site/site.xml"
     * @required
     */
    private File input;

    /**
     * Filename for target sitemap.xml
     * 
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private File target;

    /**
     * Base domain for relative urls from site.xml. Should be "http://www.foo.bar/". Will be the prefix for all relative urls.
     * 
     * @parameter
     * @required
     */
    private String domainbase;

    /**
     * Generating the sitemap.xml
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Collection duplicateUrlCheck = new ArrayList();
        
        if (target != null ) {
            if (! target.isDirectory()) {
                target.mkdirs();
            }
            target = new File( target.getAbsolutePath() + System.getProperty( "file.separator" ) + "sitemap.xml" );
        }
        try
        {
            if ( input == null || !input.exists() )
            {
                getLog().error( "Xml file does not exist: " + input );
                throw new MojoExecutionException( "goal failed, input xml file does not exist" );
            }

            DOMParser parser = new DOMParser();
            parser.parse( input.toString() );
            Document doc = parser.getDocument();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try
            {
                db = dbf.newDocumentBuilder();
            }
            catch ( ParserConfigurationException e )
            {
                throw new MojoExecutionException( "ParserConfigurationException", e );

            }
            Document xmldoc = db.newDocument();
            Element root = xmldoc.createElement( "urlset" );
            root.setAttribute( "xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9" );

            NodeList nodes = doc.getElementsByTagName( "item" );
            for ( int i = 0; i < nodes.getLength(); i++ )
            {
                NamedNodeMap nmap = nodes.item( i ).getAttributes();

                Element url = xmldoc.createElement( "url" );

                Element location = xmldoc.createElement( "loc" );
                String nodeurl = nmap.getNamedItem( "href" ).getNodeValue().toString();
                // If the url is absolute do not prepend the domainbase
                if ( !( nodeurl.startsWith( "https://" ) || nodeurl.startsWith( "http://" ) || nodeurl.startsWith( "ftp://" ) ) )
                {
                    nodeurl = domainbase + nodeurl;
                } else {
                    // Do not add external URLS to sitemap!
                    break;
                }
                location.appendChild( xmldoc.createTextNode( nodeurl ) );

                Element lastmod = xmldoc.createElement( "lastmod" );
                SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
                lastmod.appendChild( xmldoc.createTextNode( df.format( new Date() ) ) );

                // Element changefreq = xmldoc.createElement("changefreq");
                // changefreq.appendChild( xmldoc.createTextNode("monthly") );

                // Element priority = xmldoc.createElement("priority");
                // priority.appendChild( xmldoc.createTextNode("0.8") );

                url.appendChild( location );
                url.appendChild( lastmod );
                // url.appendChild( changefreq );
                // url.appendChild( priority );
                
                if (! duplicateUrlCheck.contains( nodeurl )) {
                    root.appendChild( url );
                    duplicateUrlCheck.add( nodeurl );
                }
                
            }

            xmldoc.appendChild( root );
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = null;
            try
            {
                transformer = tf.newTransformer();
            }
            catch ( TransformerConfigurationException e )
            {
                throw new MojoExecutionException( "goal failed", e );
            }

            DOMSource source = new DOMSource( xmldoc );
            StreamResult result = new StreamResult( target );

            try
            {
                transformer.transform( source, result );

            }
            catch ( TransformerException e )
            {
                throw new MojoExecutionException( "goal failed", e );
            }

            // now gzip the file
            gzipFile( target );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Invalid file URL to site.xml", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }
        catch ( SAXException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

    }

    /**
     * This method generates a gzipped file of the source file. <br>
     * The gzipped file is named sourceFilename.gz.<br>
     * It will be generated in the same directory.
     * 
     * @param source File that should get gzipped
     */
    private void gzipFile( File source )
        throws MojoExecutionException
    {
        File gzipfile = new File( source.getAbsoluteFile() + ".gz" );
        GZIPOutputStream gzipoutputstream = null;
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        // Create GZIP Stream
        try
        {
            FileOutputStream outputstream = new FileOutputStream( gzipfile );
            gzipoutputstream = new GZIPOutputStream( outputstream );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

        // GZIP the file
        try
        {
            FileInputStream inputstream = new FileInputStream( source );
            int length;
            while ( ( length = inputstream.read( buffer, 0, bufferSize ) ) != -1 )
            {
                gzipoutputstream.write( buffer, 0, length );
            }
            inputstream.close();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }
        // Close Stream
        try
        {
            gzipoutputstream.close();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

    }

}
