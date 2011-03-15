package com.williams1000.trading.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.williams1000.trading.ejb.TradeEntry;
import com.williams1000.trading.ejb.TradeEntryHome;

/**
 * @web.servlet
 *     name="Trading"
 *     
 * @web.servlet-mapping
 *     url-pattern="/trade"
 * 
 * @web.ejb-ref
 *     name="ejb/trading/TradeEntry"
 *     type="Session"
 *     home="com.williams1000.trading.ejb.TradeEntryHome"
 *     remote="com.williams1000.trading.ejb.TradeEntry"
 *     
 * @jboss.ejb-ref-jndi
 *     ref-name="ejb/trading/TradeEntry"
 *     jndi-name="ejb/trading/TradeEntry"
 */
public class TradingServlet
    extends HttpServlet
{

    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        try
        {
            int count = getCount();
            response.setContentType( "text/html" );
            PrintWriter out = response.getWriter();
            out.println( "<html>" );
            out.println( "<body>" );
            out.println( "<h1>The count is " + count + "</h1>" );
            out.println( "</body>" );
            out.println( "</html>" );
            out.close();
        }
        catch ( RemoteException e )
        {
            throw new ServletException( e );
        }
        catch ( CreateException e )
        {
            throw new ServletException( e );
        }
        catch ( NamingException e )
        {
            throw new ServletException( e );
        }
        catch ( FinderException e )
        {
            throw new ServletException( e );
        }
        catch ( RemoveException e )
        {
            throw new ServletException( e );
        }
    }

    private int getCount()
        throws RemoteException, CreateException, NamingException, FinderException, RemoveException
    {
        TradeEntryHome home = (TradeEntryHome) fetchHome( "java:comp/env/ejb/trading/TradeEntry", TradeEntryHome.class );
        TradeEntry tradeEntry = home.create();
        tradeEntry.createCoupon( "132456788" );
        tradeEntry.createBond( "123456789" );
        tradeEntry.removeAllBonds();
        return tradeEntry.getBookmarkCount();
    }

    private EJBHome fetchHome( String name, Class homeClass )
        throws NamingException
    {
        System.out.println( "finding: " + name );
        EJBHome home;
        Object lookup = new InitialContext().lookup( name );
        home = (EJBHome) PortableRemoteObject.narrow( lookup, homeClass );
        System.out.println( "well I made it here " + name );
        return home;
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        doGet( request, response );
    }

}
