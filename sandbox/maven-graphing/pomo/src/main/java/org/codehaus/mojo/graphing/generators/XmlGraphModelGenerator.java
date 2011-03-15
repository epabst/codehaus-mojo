package org.codehaus.mojo.graphing.generators;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.codehaus.mojo.graphing.model.Edge;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.Node;

public class XmlGraphModelGenerator
    implements GraphGenerator
{

    public void generate( GraphModel model, String filename )
        throws IOException
    {
        FileWriter writer = new FileWriter(filename);
        PrintWriter dot = new PrintWriter(writer);
        
        dot.println("<?xml version=\"1.0\" ?>");
        dot.println("<graphmodel>");
        
        Iterator it = model.getEdgesIterator();
        while(it.hasNext())
        {
            Edge edge = (Edge) it.next();
            dot.println("  <edge scope=\"" + edge.getType() + "\"");
            dot.println("        parent=\"" + getID(edge.getNode1()) + "\"");
            dot.println("        child=\"" + getID(edge.getNode2()) + "\" />");
        }
        
        dot.println("</graphmodel>");
        
        dot.flush();
        writer.flush();
        writer.close();
    }

    private String getID(Node node) {
        return node.getId();
    }

    public String getOutputName()
    {
        return "graphmodel.xml";
    }    
}
