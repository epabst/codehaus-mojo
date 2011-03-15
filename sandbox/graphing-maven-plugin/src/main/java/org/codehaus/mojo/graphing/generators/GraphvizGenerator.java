package org.codehaus.mojo.graphing.generators;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.codehaus.mojo.graphing.model.Edge;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.Node;

/**
 * Generator for a Graphviz.
 *  
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class GraphvizGenerator
    implements GraphGenerator
{

    public void generate( GraphModel model, String filename )
    throws IOException
    {
        FileWriter writer = new FileWriter(filename);
        PrintWriter dot = new PrintWriter(writer);
        
        dot.println("digraph " + getID(model.getCenterNode()) + "_graph {");
        dot.println("  rankdir=LR;");
        dot.println("  size=\"10,8\";");
        dot.println("  node [ shape = box ];");
        
        Iterator it = model.getEdgesIterator();
        while(it.hasNext())
        {
            Edge edge = (Edge) it.next();
            dot.println("  " + getID(edge.getNode1()) + " -> " + getID(edge.getNode2()) + ";");
        }
        
        dot.println("}");
        
        dot.flush();
        writer.flush();
        writer.close();
    }

    private String getID(Node node) {
        String id = node.getId();
        StringBuffer sb = new StringBuffer();
        int len = id.length();
        for(int i=0; i<len; i++) {
            char c = id.charAt(i);
            if(Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                sb.append("_");
            }
        }
        return sb.toString();
    }
}
