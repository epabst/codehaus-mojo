package org.codehaus.mojo.hibernate2;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to resolve external entity definitions for j2ee artifacts.
 * Based on the J2EEEntityResolver from the J2EE plugin
 *
 * @author eric pugh
 * @version $Id$
 */
public class HibernateEntityResolver
    implements EntityResolver
{
    /**
     * map of ids to resource names
     */
    private Map idToResource = new HashMap();

    /**
     * list of j2ee dtds that are being made available
     */
    public static final String[] HIBERNATE_DTDS = new String[]{
        "-//Hibernate/Hibernate Mapping DTD 2.0//EN"
    };

    public static final String[] HIBERNATE_RESOURCES = new String[]{
        "/plugin-resources/hibernate2-mapping-2.0.dtd"
    };

    /**
     * Creates a new instance of EntityResolver
     */
    public HibernateEntityResolver()
    {
        for ( int i = 0; i < HIBERNATE_DTDS.length; i++ )
        {
            idToResource.put( HIBERNATE_DTDS[ i ], HIBERNATE_RESOURCES[ i ] );
        }
    }

    /**
     * resolve the entity given by the provided Ids
     *
     * @param publicId the public id of the entity
     * @param systemId the 'system location' (typically a URL) of the entity
     * @return an {@link InputSource input source} for retrieval of the entity
     * @throws IOException  when an I/O error occurs retrieving the entity
     * @throws SAXException if there are any problems
     */
    public InputSource resolveEntity( String publicId, String systemId )
        throws
        SAXException, IOException
    {
//        LOG.debug("resolving entity with publicId='" + publicId + ", systemId='" + systemId + "'");
        if ( publicId != null )
        {
            String resource = (String) idToResource.get( publicId );
//            LOG.debug("resource found in map ='" + resource + "'" );
            if ( resource != null )
            {
                InputStream in = getClass().getResourceAsStream( resource );
//                LOG.debug("input stream ='" + in + "'" );
                if ( in != null )
                {
                    return new InputSource( in );
                }
            }
        }
        return null;
    }

    /**
     * Getter for publicId to resource name map.
     *
     * @return Value of property idToResource.
     */
    protected Map getIdToResource()
    {
        return idToResource;
    }

    /**
     * Setter for publicId to resource name map.
     *
     * @param idToResource New value of property idToResource.
     */
    protected void setIdToResource( Map idToResource )
    {
        this.idToResource = idToResource;
    }
}
