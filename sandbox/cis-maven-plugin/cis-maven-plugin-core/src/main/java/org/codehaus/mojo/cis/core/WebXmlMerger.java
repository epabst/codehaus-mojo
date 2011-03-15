package org.codehaus.mojo.cis.core;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class is used to merge two or more web.xml files.
 */
public class WebXmlMerger
{
    private boolean isEmpty( String pValue )
    {
        return pValue == null  ||  pValue.length() == 0;
    }

    private boolean isCommentNode( Node pNode )
    {
        if ( pNode == null )
        {
            return false;
        }
        switch ( pNode.getNodeType() )
        {
            case Node.COMMENT_NODE:
                return true;
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                String value = pNode.getNodeValue();
                for ( int i = 0;  i < value.length();  i++ )
                {
                    char c = value.charAt( i );
                    if ( !Character.isWhitespace( c ) )
                    {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }

    private Node getNodeOrFragment( Node pNode )
    {
        Node first = pNode;
        for (;;)
        {
            Node prev = first.getPreviousSibling();
            if ( !isCommentNode( prev ) )
            {
                break;
            }
            first = prev;
        }
        if ( first == pNode )
        {
            return pNode;
        }
        
        DocumentFragment df = pNode.getOwnerDocument().createDocumentFragment();
        for (;;)
        {
            df.appendChild( first.cloneNode( true ) );
            if ( first == pNode )
            {
                return df;
            }
            first = first.getNextSibling();
        }
    }

    private Node findElement( Document[] pDocuments, String pElementName )
    {
        for ( int i = 0;  i < pDocuments.length;  i++ )
        {
            for ( Node child = pDocuments[i].getDocumentElement().getFirstChild();
                  child != null;
                  child = child.getNextSibling() )
            {
                if ( child.getNodeType() == Node.ELEMENT_NODE
                     &&  isEmpty( child.getNamespaceURI() )
                     &&  pElementName.equals( child.getLocalName() ) )
                {
                    return getNodeOrFragment( child );
                }
            }
        }
        return null;
    }

    private void appendOptionalNode( Document[] pDocuments, Element webApp, String pElementName )
    {
        final Node iconNode = findElement( pDocuments, pElementName );
        if ( iconNode != null )
        {
            webApp.appendChild( webApp.getOwnerDocument().importNode( iconNode, true ) );
        }
    }

    private String getAtomicText( Node pNode )
    {
        StringBuffer sb = new StringBuffer();
        for ( Node child = pNode.getFirstChild();  child != null;  child = child.getNextSibling() )
        {
            switch ( child.getNodeType() )
            {
                case Node.CDATA_SECTION_NODE:
                case Node.TEXT_NODE:
                    sb.append( child.getNodeValue() );
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

    private void appendNodes( Document[] pDocuments, Element webApp, String pElementName,
                              String[] pNameElementNames )
        throws CisCoreErrorMessage
    {
        final Set names = new HashSet();
        for ( int i = 0;  i < pDocuments.length;  i++ )
        {
            Document doc = pDocuments[i];

            for ( Node child = doc.getDocumentElement().getFirstChild();
                  child != null;
                  child = child.getNextSibling() )
            {
                if ( child.getNodeType() == Node.ELEMENT_NODE
                     &&  isEmpty( child.getNamespaceURI() )
                     &&  pElementName.equals( child.getLocalName() ) )
                {
                    if ( pNameElementNames != null )
                    {
                        String name = getNodeName( pElementName, pNameElementNames, child );
                        if ( names.contains( name ) )
                        {
                            continue;
                        }
                    }
                    webApp.appendChild( webApp.getOwnerDocument().importNode( getNodeOrFragment( child ), true ) );
                }
            }
        }
    }

    private String getNodeName( String pElementName, String[] pNameElementNames, Node node )
        throws CisCoreErrorMessage
    {
        for ( Node nameNode = node.getFirstChild();
              nameNode != null;
              nameNode = nameNode.getNextSibling() )
        {
            if ( nameNode.getNodeType() == Node.ELEMENT_NODE
                 &&  isEmpty( nameNode.getNamespaceURI() ) )
            {
                for ( int i = 0;  i < pNameElementNames.length;  i++ )
                {
                    if ( pNameElementNames[i].equals( nameNode.getLocalName() ) )
                    {
                        return String.valueOf( i ) + ":" + getAtomicText( nameNode ).trim();
                    }
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        for ( int i = 0;  i < pNameElementNames.length;  i++ )
        {
            if ( i > 0 )
            {
                sb.append( "|" );
            }
            sb.append( pNameElementNames[i] );
        }
        throw new CisCoreErrorMessage( "Invalid web.xml document: "
                                       + " Expected " + sb
                                       + " element within " + pElementName );
    }

    /**
     * Returns a web.xml file, which is obtained by merging the input
     * files.
     * @param pDocuments A set of web.xml files, which are being merged.
     * @return The result document.
     */
    public Document merge(Document[] pDocuments)
        throws ParserConfigurationException, CisCoreErrorMessage
    {
        if ( pDocuments == null  ||  pDocuments.length == 0)
        {
            return null;
        }
        if ( pDocuments.length == 1 )
        {
            return pDocuments[0];
        }

        final Document result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final Element webApp = result.createElement( "web-app" );
        result.appendChild( webApp );

        /**
         * Find the first "icon" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "icon" );

        /**
         * Find the first "display-name" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "display-name" );

        /**
         * Find the first "description" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "description" );

        /**
         * Find the first "distributable" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "distributable" );

        /**
         * Find the various "context-param" elements.
         */
        appendNodes( pDocuments, webApp, "context-param", new String[]{"param-name"} );

        /**
         * Find the various "filter" elements.
         */
        appendNodes( pDocuments, webApp, "filter", new String[]{"filter-name"} );

        /**
         * Find the various "filter-mapping" elements.
         */
        appendNodes( pDocuments, webApp, "filter-mapping", new String[]{"url-pattern", "servlet-name"} );

        /**
         * Find the various "listener" elements.
         */
        appendNodes( pDocuments, webApp, "listener", null );

        /**
         * Find the various "servlet" elements.
         */
        appendNodes( pDocuments, webApp, "servlet", new String[]{"servlet-name"} );

        /**
         * Find the various "servlet-mapping" elements.
         */
        appendNodes( pDocuments, webApp, "servlet-mapping", new String[]{"url-pattern", "servlet-name"} );

        /**
         * Find the first "session-config" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "session-config" );

        /**
         * Find the various "mime-mapping" elements.
         */
        appendNodes( pDocuments, webApp, "mime-mapping", new String[]{"extension"} );

        /**
         * Find the first "welcome-file-list" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "welcome-file-list" );

        /**
         * Find the various "error-page" elements.
         */
        appendNodes( pDocuments, webApp, "error-page", new String[]{"error-code", "exception-type"} );

        /**
         * Find the various "taglib" elements.
         */
        appendNodes( pDocuments, webApp, "taglib", new String[]{"taglib-uri"} );

        /**
         * Find the various "resource-env-ref" elements.
         */
        appendNodes( pDocuments, webApp, "resource-env-ref", new String[]{"resource-env-ref-name"} );

        /**
         * Find the various "resource-ref" elements.
         */
        appendNodes( pDocuments, webApp, "resource-ref", new String[]{"res-ref-name"} );

        /**
         * Find the various "security-constraint" elements.
         */
        appendNodes( pDocuments, webApp, "security-constraint", null );

        /**
         * Find the first "login-config" element, if any.
         */
        appendOptionalNode( pDocuments, webApp, "login-config" );

        /**
         * Find the various "security-role" elements.
         */
        appendNodes( pDocuments, webApp, "security-role", new String[]{"role-name"} );

        /**
         * Find the various "env-entry" elements.
         */
        appendNodes( pDocuments, webApp, "env-entry", new String[]{"env-entry-name"} );

        /**
         * Find the various "ejb-ref" elements.
         */
        appendNodes( pDocuments, webApp, "ejb-ref", new String[]{"ejb-ref-name"} );

        /**
         * Find the various "ejb-local-ref" elements.
         */
        appendNodes( pDocuments, webApp, "ejb-local-ref", new String[]{"ejb-ref-name"} );

        return result;
    }
}
