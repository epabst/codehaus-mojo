package org.codehaus.mojo.mant;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Wraps a task and applies the given mappings to the task attributes.
 *
 */
public class AntTask
{
    private Element task;

    public AntTask( String task, String[] mappings )
        throws DocumentException
    {
        this.task = createTask( task, mappings );
    }

    public Element getTask()
    {
        return task;
    }

    /**
     * Apply mappings to the given node.
     * Attributes are only created if the parent already exists.
     * @param task TODO
     * @param mappings TODO
     * @param mappings TODO
     * @return 
     * @throws DocumentException 
     */
    private Element createTask( String task, String[] mappings )
        throws DocumentException
    {
        Element node = createElement( task );
        for ( int i = 0; i < mappings.length; i += 2 )
        {
            String mavenProperty = mappings[i + 1];
            String[] components = getComponents( mappings[i] );
            String parentPath = components[0];
            String attributeName = components[1];

            List list = node.selectNodes( parentPath );
            Iterator allMatches = list.iterator();
            while ( allMatches.hasNext() )
            {
                Element match = (Element) allMatches.next();
                match.addAttribute( attributeName, mavenProperty );
            }
        }
        return node;
    }

    /**
     * Creates a node object from the given string representation.
     * @param task
     * @return
     * @throws DocumentException 
     */
    private Element createElement( String task )
        throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read( new StringReader( task ) );
        return (Element) document.getRootElement().clone();
    }

    /**
     * Ensures the xpath expression is of the correct format.
     * It should refer to an attribute and also a ./ is prepended
     * if the first character is an @ so that the parent isn't an
     * empty string when split.
     * @param xpath
     * @return
     */
    private String[] getComponents( String xpath )
    {
        String standardXpath = xpath.startsWith( "@" ) ? "./" + xpath : xpath;
        String[] components = standardXpath.split( "/@" );
        if ( components.length != 2 )
        {
            throw new RuntimeException( "xpath expression must refer to an attribute: " + xpath );
        }
        return components;
    }

}
