package org.apache.maven.portlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class QuotePortlet extends GenericPortlet {
    private List quotes;
    private Random random;

    public void init() throws PortletException {
        random = new Random();

        Reader reader = new InputStreamReader( getClass().getClassLoader().getResourceAsStream( "quotes.xml" ) );
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read( reader );
            List list = document.getRootElement().selectNodes( "/quote/*" );

            quotes = new ArrayList();
            for( Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                String quote = ( (Element)iterator.next() ).getTextTrim();
                quotes.add( quote );
            }
        }
        catch( DocumentException e ) {
            e.printStackTrace();
        }
    }

    public void doView( RenderRequest request, RenderResponse response )
        throws PortletException, IOException {

        if( WindowState.MINIMIZED.equals( request.getWindowState() ) ) {
            return;
        }

        int number = random.nextInt( quotes.size() + 1 );
        request.setAttribute( "quote", (String)quotes.get( number ) );

        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher( "/view.jsp" );

        dispatcher.include( request, response );
    }
}
