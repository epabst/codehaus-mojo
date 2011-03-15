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

import org.apache.maven.plugin.logging.Log;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.util.Map;

/**
 * Run a xsl transformation.
 */
public class XslTransformer
{

    /** The logger passed by the mojo */
    private Log log;


    /**
     * Make an XSL transformation.
     *
     * @param log the logger
     */
    public XslTransformer(Log log)
    {
        this.log = log;
    }

    /**
     * Get the logger passed by the mojo.
     *
     * @return the logger
     */
    public Log getLog()
    {
        return log;
    }

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param xslin The source object that contains the xslt
     * @param xmlin The source object that passes the xml to be transformed
     * @param xmlout The result object for the transformed output
     * @param params A set of parameters that will be forwarded to the XSLT
     * @throws Exception the transformation failed
     */
    public void transform( String xslName, Source xslin, Source xmlin, Result xmlout, Map params )
        throws Exception
    {
        getLog().info( "Using the following XSL : " + xslName);
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Templates templates = transformerFactory.newTemplates( xslin );
        Transformer transformer = templates.newTransformer();

        if ( params != null )
        {
            for (Object obj : params.entrySet())
            {
                Map.Entry entry = (Map.Entry) obj;
                transformer.setParameter(String.valueOf(entry.getKey()), entry.getValue());
            }
        }

        transformer.transform( xmlin, xmlout );
    }
}
