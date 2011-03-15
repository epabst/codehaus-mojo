/**
 * 
 */
package org.codehaus.mojo.cis.core;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class CisCoreEntityResolver implements EntityResolver
{
    private final CisUtils cisUtils;

    /**
     * Creates a new instance with the given CIS Utils.
     */
    public CisCoreEntityResolver( CisUtils pCisUtils )
    {
        cisUtils = pCisUtils;
    }

    /**
     * Returns the CIS Utilities.
     */
    public CisUtils getCisUtils() {
        return cisUtils;
    }

    public InputSource resolveEntity( String pPublicId, String pSystemId )
        throws SAXException, IOException
    {
        getCisUtils().debug( "Resolving entity publicId=" + pPublicId
                        + ", systemId=" + pSystemId );
        final String res;
        if ( "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN".equals( pPublicId ) )
        {
            res = "resources/web-app_2_3.dtd";
        }
        else
        {
            return null;
        }
        URL url = getClass().getResource( res );
        if ( url == null )
        {
            throw new SAXException( "Failed to locate resource: " + res );
        }
        InputSource isource = new InputSource( url.openStream() );
        isource.setSystemId( url.toExternalForm() );
        return isource;
    }
}