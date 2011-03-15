package org.codehaus.mojo.jlint;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JlintXmlConfigReader
{
    String configFileName;

    ArrayList<String> rules;

    public JlintXmlConfigReader( String configFileName )
    {
        this.configFileName = configFileName;
    }

    public ArrayList<String> readConfiguration()
    {
        rules = new ArrayList<String>();

        try
        {
            String textVal = null;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse( configFileName );

            Element rootEl = dom.getDocumentElement();

            NodeList nl = rootEl.getElementsByTagName( Constants.XMLCONFIGFILE_RULE_NODE );

            // System.out.println("LOAD CONFIG ++++++++++++++++++++++++++++++++++++++++++");

            if ( nl != null && nl.getLength() > 0 )
            {
                for ( int i = 0; i < nl.getLength(); i++ )
                {
                    Element ruleEl = (Element) nl.item( i );
                    textVal = ruleEl.getFirstChild().getNodeValue();
                    // System.out.println(textVal);
                    rules.add( textVal );
                }
            }

            // System.out.println("LOAD CONFIG ++++++++++++++++++++++++++++++++++++++++++");
        }
        catch ( ParserConfigurationException pce )
        {
            pce.printStackTrace();
        }
        catch ( SAXException se )
        {
            se.printStackTrace();
        }
        catch ( IOException ioe )
        {
            ioe.printStackTrace();
        }

        return rules;
    }

}
