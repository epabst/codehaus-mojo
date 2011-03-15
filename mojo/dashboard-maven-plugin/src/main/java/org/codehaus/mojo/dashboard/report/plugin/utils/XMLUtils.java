package org.codehaus.mojo.dashboard.report.plugin.utils;

/*
 * Copyright 2008 David Vicente
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLUtils
{
    private static XMLUtils xMLUtils = null;
    
 
    
    /**
     * Creation forbidden...
     */
    private XMLUtils()
    {
        super();
        
    }
    
    public static XMLUtils getInstance()
    {
        if (xMLUtils == null){
            xMLUtils = new XMLUtils();
        }
        return xMLUtils;
    }
    
    /**
     * @param xmlFilename
     * @return
     */
    public Document getDocument(InputStream stream) throws Exception
    {
        Document doc = null;

        try
        {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            InputSource inputSource = new InputSource(inputStreamReader);

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(inputSource);
        }
        catch (FileNotFoundException e)
        {
            throw new Exception(e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new Exception(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new Exception(e);
        }
        catch (FactoryConfigurationError e)
        {
            throw new Exception(e.getException());
        }
        catch (SAXException e)
        {
            throw new Exception(e);
        }
        catch (IOException e)
        {
            throw new Exception(e);
        }

        return doc;
    }
    
    public Document getDocument( File xmlFilename ) throws Exception
    {
        
        FileInputStream fileInputStream = new FileInputStream( xmlFilename );
        Document doc = getDocument( fileInputStream );
        
        return doc;
    }
}
