package org.codehaus.mojo.graphing.model.factory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.graphing.model.Edge;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.Node;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class StaticGraphModelFactory
{
    private Log log;

    public StaticGraphModelFactory( Log log )
    {
        this.log = log;
    }

    public GraphModel getGraphModel( String filename )
    {
        GraphModel model = new GraphModel();

        try
        {
            InputStreamReader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));
            XmlPullParser parser = new MXParser();

            parser.setInput( reader );
            int event;
            while ( ( event = parser.next() ) != XmlPullParser.END_DOCUMENT )
            {
                if ( event == XmlPullParser.START_TAG )
                {
                    if ( "edge".equals( parser.getName() ) )
                    {
                        Node parent = new Node( parser.getAttributeValue( null, "parent" ) );
                        Node child = new Node( parser.getAttributeValue( null, "child" ) );
                        if ( StringUtils.isEmpty( parent.getId() ) || StringUtils.isEmpty( child.getId() ) )
                        {
                            continue;
                        }
                        Edge edge = new Edge( parent, child );
                        model.addEdge( edge );
                    }
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            log.error( "Unable to find file: " + filename, e );
        }
        catch ( XmlPullParserException e )
        {
            log.error( "Unable to parse file: " + filename, e );
        }
        catch ( IOException e )
        {
            log.error( "IOException: " + filename, e );
        }

        return model;
    }
}
