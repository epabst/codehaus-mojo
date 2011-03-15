/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest.components;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Some helper methods for working with XOM.
 */
public class XomHelper
{
    /**
     * Constructor.
     */
    public XomHelper()
    {
    }

    /**
     * Parse a list of XML files.
     *
     * @param xmlFiles the files to be parsed
     * @return the resulting documents
     * @throws ParsingException parsing the XML file failed
     * @throws IOException reading the input file failed
     */
    public Document[] parse( File[] xmlFiles )
        throws ParsingException, IOException
    {
        Document[] result = new Document[xmlFiles.length];

        for ( int i = 0; i < xmlFiles.length; i++ )
        {
            result[i] = this.parse( xmlFiles[i] );
        }

        return result;
    }

    /**
     * Parse a single XML file.
     *
     * @param xmlFile the file to be parsed
     * @return the resulting documents
     * @throws ParsingException parsing the XML file failed
     * @throws IOException reading the input file failed
     */
    public Document parse( File xmlFile )
        throws ParsingException, IOException
    {
        return ( new Builder().build( xmlFile ) );
    }

    /**
     * Save the document to a file.
     *
     * @param document the document to save
     * @param file the target file
     * @throws IOException saving the document failed
     */
    public void toFile( Document document, File file )
        throws IOException
    {
        FileOutputStream fos = new FileOutputStream( file );
        Serializer serializer = new Serializer( fos );
        serializer.write( document );
        fos.close();
    }

    /**
     * Obviously doing something very unusual - append a document
     * to an element.
     *
     * @param element the new parent of the 'document'
     * @param document the document to add to 'element'
     */
    public void appendDocument( Element element, Document document )
    {
        Element documentRoot = document.getRootElement();
        Element childElement = new Element( documentRoot.getLocalName() );

        // copy attributes of root element
        while ( documentRoot.getAttributeCount() > 0 )
        {
            Attribute currAttribute = documentRoot.getAttribute( 0 );
            currAttribute.detach();
            childElement.addAttribute( currAttribute );
        }

        // copy all child nodes of the root element
        Elements documentRootChildElements = documentRoot.getChildElements();
        for ( int j = 0; j < documentRootChildElements.size(); j++ )
        {
            Element currChildElement = documentRootChildElements.get( j );
            currChildElement.detach();
            childElement.appendChild( currChildElement );
        }

        element.appendChild( childElement );
    }
}