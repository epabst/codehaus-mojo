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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * Transforms a set of Docbook files into XHTML and PDF output.
 * 
 * @author jgonzalez
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @goal transform
 * @description Transform Docbook files into XHTML output
 */
public class TransformMojo
    extends AbstractMojo
{
    private static String XERCES_PARSER_CONFIG = "org.apache.xerces.xni.parser.XMLParserConfiguration";

    private static String XERCES_XINCLUDE_PARSER = "org.apache.xerces.parsers.XIncludeParserConfiguration";

    private static String XERCES_RESOLVER_CONFIG = "org.apache.xerces.xni.parser.XMLEntityResolver";

    private static String DOCBOOK_MOJO_RESOVER = "org.codehaus.mojo.docbook.MojoURIResolver";

    private static String TRANSFORMER_FACTORY_PROPERTY_NAME = "javax.xml.transform.TransformerFactory";

    private static String TRANSFORMER_FACTORY_CLASS = "org.apache.xalan.processor.TransformerFactoryImpl";

    /**
     * Directory where the source Docbook files are located.
     * 
     * @parameter expression="${basedir}/src/docbook"
     * @required
     */
    private File sourceDirectory;

    /**
     * Directory where the resource files are located.
     * 
     * @parameter expression="${basedir}/src/docbook/resources"
     * @required
     */
    private File resourceDirectory;

    /**
     * Work directory where the olink database will be generated.
     * 
     * @parameter expression="${project.build.directory}/docbook"
     * @required
     */
    private File databaseDirectory;

    /**
     * Target directory where the resulting files will be placed.
     * 
     * @parameter expression="${project.build.directory}/site/docbook"
     * @required
     */
    private File outputDirectory;

    /**
     * Specifies the output encoding.
     * 
     * @parameter expression="${outputEncoding}" default-value="${project.build.sourceEncoding}"
     */
    private String outputEncoding;

    /**
     * Specifies the list of desired output formats. Example:
     * &lt;outputFormats&gt; &lt;param&gt;xhtml&lt;/param&gt;
     * &lt;param&gt;pdf&lt;/param&gt; &lt;/outputFormats&gt;
     * 
     * @parameter
     */
    private String[] outputFormats = new String[] { "xhtml", "pdf" };

    /**
     * <p>
     * Specifies a list of customization stylesheets for the specified output
     * formats. The default value is to use no customizations, but you can point
     * this configuration parameter to your own stylesheets that set parameters
     * or override templates.
     * </p>
     * <p>
     * An example configuration that changes the pdf stylesheet:
     * </p>
     * 
     * <pre>
     * &lt;customizations&gt;
     *   &lt;pdf&gt;src/main/resources/docbook2pdf.xsl&lt;/pdf&gt;
     * &lt;/customizations&gt;
     * </pre>
     * 
     * <p>
     * All relative URLs are resolved against resourcePath
     * </p>
     * 
     * @parameter
     */
    private Map customizations = new HashMap();

    /**
     * <p>
     * Specifies a list of transformation profiles, e.g.
     * </p>
     * 
     * <pre>
     * &lt;profiles&gt;
     *  &lt;transformProfile&gt;
     *    &lt;id&gt;linux&lt;/id&gt;
     *    &lt;os&gt;linux&lt;/os&gt;
     *  &lt;/transformProfile&gt;
     *  &lt;transformProfile&gt;
     *    &lt;id&gt;windows&lt;/id&gt;
     *    &lt;os&gt;windows&lt;/os&gt;
     *  &lt;/transformProfile&gt;
     *  &lt;transformProfile&gt;
     *    &lt;id&gt;nonlinux&lt;/id&gt;
     *    &lt;operatingsystems&gt;
     *      &lt;os&gt;windows&lt;/os&gt;
     *      &lt;os&gt;macosx&lt;/os&gt;
     *    &lt;/operatingsystems&gt;
     *  &lt;/transformProfile&gt;
     * &lt;/profiles&gt;
     * </pre>
     * 
     * <p>
     * This examples creates three profiles: one containing only content marked
     * for linux, one containing only content marked for windows and a third
     * with content marked for either windows or mac os x. The id element is
     * required for each profile, as the resulting files will have the pattern
     * $filename.$profile.$ext. You can combine profile conditions e.g. having
     * an os element and an arch element in place and you can profile for
     * multiple values as shown in the nonlinux profile.
     * </p>
     * <p>
     * All available filtering elements are:
     * </p>
     * <ul>
     * <li>arch/architectures</li>
     * <li>condition/conditions</li>
     * <li>conformance/conformances</li>
     * <li>lang/languages</li>
     * <li>os/operatingsystems</li>
     * <li>revision/revisions</li>
     * <li>security/securities</li>
     * <li>userlevel/userlevels</li>
     * <li>vendor/vendors</li>
     * </ul>
     * <p>
     * Additionally there is support for attribute-based filtering, which
     * requires the use of attribute and value elements. For more information
     * about DocBook profiling, see <a
     * href="http://www.sagehill.net/docbookxsl/Profiling.html">Chapter 25.
     * Profiling (conditional text)</a> in <a
     * href="http://www.sagehill.net/docbookxsl/">Bob Stayton's DocBook XSL: The
     * Complete Guide</a>.
     * </p>
     * 
     * @parameter
     */
    private TransformProfile[] profiles = new TransformProfile[] {};

    /**
     * Specifies the stylesheet location, useful if you want to use a local copy
     * or a specific version instead of the current release from the maven
     * repository. Note that original docbook stylesheets are in the root of the jar
     * so to avoid clashing yours should be in a subfolder like
     * <code>resource://com.mycompany/docbook</code>
     * 
     * @parameter expression="${stylesheetLocation}"
     *            default-value="resource://docbook/docbook-xsl/docbook-xsl-1.67.2/"
     */
    private String stylesheetLocation;

    /**
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;

    /**
     * @parameter expression="${plugin.artifacts}"
     */
    private Collection artifacts;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Log log = this.getLog();

        // System.setProperty( TRANSFORMER_FACTORY_PROPERTY_NAME, TRANSFORMER_FACTORY_CLASS );

        Proxy activeProxy = this.settings.getActiveProxy();
        String httpProxyHost = System.getProperty( "http.proxyHost" );
        String httpProxyPort = System.getProperty( "http.proxyPort" );
        String httpNonProxyHosts = System.getProperty( "http.nonProxyHosts" );
        if ( activeProxy != null )
        {
            System.setProperty( "http.proxyHost", activeProxy.getHost() );
            System.setProperty( "http.proxyPort", new Integer( activeProxy.getPort() ).toString() );
            System.setProperty( "http.nonProxyHosts", activeProxy.getNonProxyHosts() );
        }

        // Set XInclude Xerces parser so we're able to process master olink database file
        String xercesParser = System.getProperty( TransformMojo.XERCES_PARSER_CONFIG );
        System.setProperty( TransformMojo.XERCES_PARSER_CONFIG, TransformMojo.XERCES_XINCLUDE_PARSER );
        String entityResolver = System.getProperty( TransformMojo.XERCES_RESOLVER_CONFIG );
        System.setProperty( TransformMojo.XERCES_RESOLVER_CONFIG, TransformMojo.DOCBOOK_MOJO_RESOVER );

        URI stylesheetLocationURI;
        try
        {
            stylesheetLocationURI = new URI( this.stylesheetLocation );
        }
        catch ( URISyntaxException exc )
        {
            throw new MojoExecutionException( "Unable to parse stylesheet location " + stylesheetLocation, exc );
        }

        try
        {
            if ( this.sourceDirectory.exists() )
            {

                OLinkDBUpdater olinkDBUpdater = new OLinkDBUpdater( log, this.sourceDirectory, this.databaseDirectory,
                                                                    stylesheetLocationURI, this.artifacts );
                olinkDBUpdater.update();

                DocumentTransformer documentTransformer = new DocumentTransformer( log, this.sourceDirectory,
                                                                                   this.resourceDirectory,
                                                                                   this.databaseDirectory,
                                                                                   this.outputDirectory,
                                                                                   stylesheetLocationURI,
                                                                                   this.customizations, this.artifacts );
                for ( int i = 0; i < outputFormats.length; i++ )
                {
                    documentTransformer.enableOutputFormat( outputFormats[i] );
                }

                documentTransformer.transform();

                if ( this.profiles.length != 0 )
                {
                    for ( int i = 0; i < profiles.length; i++ )
                    {
                        documentTransformer.transform( this.profiles[i] );
                    }
                }
            }
        }
        finally
        {
            resetProperties( xercesParser, httpProxyHost, httpProxyPort, httpNonProxyHosts, entityResolver );
        }
    }

    private void resetProperties( String xercesParser, String httpProxyHost, String httpProxyPort,
                                  String httpNonProxyHosts, String entityResolver )
    {
        // Reset XInclude Xerces parser to previous value
        if ( xercesParser != null )
        {
            System.setProperty( TransformMojo.XERCES_PARSER_CONFIG, xercesParser );
            System.setProperty( TransformMojo.XERCES_RESOLVER_CONFIG, entityResolver );
        }
        else
        {
            // In 1.4 there's no clear property method... is this correct?
            System.setProperty( TransformMojo.XERCES_PARSER_CONFIG, "" );
            System.setProperty( TransformMojo.XERCES_RESOLVER_CONFIG, "" );
        }

        if ( httpProxyHost != null )
        {
            System.setProperty( "http.proxyHost", httpProxyHost );
            System.setProperty( "http.proxyPort", httpProxyPort );
            System.setProperty( "http.nonProxyHosts", httpNonProxyHosts );
        }
        else
        {
            System.setProperty( "http.proxyHost", "" );
            System.setProperty( "http.proxyPort", "" );
            System.setProperty( "http.nonProxyHosts", "" );
        }
    }
}
