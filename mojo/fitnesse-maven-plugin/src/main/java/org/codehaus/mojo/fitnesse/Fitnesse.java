package org.codehaus.mojo.fitnesse;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.apache.maven.plugin.MojoExecutionException;

/**
 * This class contains all information for calling a specific page on a specific FitNesse server.
 * 
 * @author pke
 */
public class Fitnesse
{
    /** Default port for FitNesse.*/
    public static final int DEFAULT_FITNESSE_PORT = 80;

    /** Minimum IP port allowed for FitNesse. */ 
    private static final int MIN_PORT = 0;

    /** Maximum IP port allowed for FitNesse. */ 
    private static final int MAX_PORT = 65535;

    /** Prefix for indicating that a page is a FitNesse <code>Suite</code>. */
    public static final String PAGE_TYPE_SUITE = "suite";

    /** Prefix for indicating that a page is a FitNesse <code>Test</code>. */
    public static final String PAGE_TYPE_TEST = "test";

    /**
     * Constructor with all server details.
     * 
     * @param pHostName Name of the FitNesse server.
     * @param pPort Port of the FitNesse server.
     * @param pPageName Full name of the page to call or run.
     */
    public Fitnesse( String pHostName, int pPort, String pPageName , String pSuiteFilter)
    {
        super();
        this.hostName = pHostName;
        this.port = pPort;
        this.pageName = pPageName;
        this.suiteFilter = pSuiteFilter;
    }

    /**
     * Constructor for testing purpose.
     */
    public Fitnesse()
    {
        super();
    }

    /**
     * FitNesse server name.
     * 
     * @parameter default-value="localhost"
     */
    private String hostName = "localhost";

    /**
     * Fitnesse suiteFilter option
     * 
     * @parameter default-value=""
     */
    private String suiteFilter;

    /**
     * Type of page, Suite or Test. Default value depend of the page name.
     * 
     * @parameter
     */
    private String type;

    /**
     * Server port of fitnesse.
     * 
     * @parameter default-value="80"
     */
    private int port = DEFAULT_FITNESSE_PORT;

    /**
     * Name of the fitnesse page @ required
     * @parameter default-value="MustBeDefinedByProject"
     */
    private String pageName = "MustBeDefinedByProject";

    /**
     * Id of the settings.server, this allow to provide credential fot FitNesse basic authentification.
     * 
     * @parameter
     */
    private String serverId;

    /**
     * Accessor.
     * 
     * @return The name of the FitNesse server.
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * Accessor.
     * 
     * @return The name of the FitNesse page.
     */
    public String getPageName()
    {
        return pageName;
    }

    /**
     * Accessor.
     * 
     * @return The listen port of the FitNesse server.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @Override
     * @return toString() representation.
     */
    public String toString()
    {
        return "Fitnesse address=http://" + hostName + ":" + port + "/" + pageName;
    }

    /**
     * Accessor.
     * 
     * @param pPageName The name of the fitNesse page.
     */
    void setPageName( String pPageName )
    {
        pageName = pPageName;
    }

    /**
     * Return the type of the FitNesse page: <code>test</code> or <code>suite</code>
     * 
     * @return The type of the page.
     * @throws MojoExecutionException When the type of the page can't be found.
     */
    public String getType()
        throws MojoExecutionException
    {
        String tResult;
        String tShortPageName =
            ( pageName.indexOf( "." ) == -1 ? pageName : pageName.substring( pageName.indexOf( "." ) + 1 ) );
        type = ( type == null ? null : type.toLowerCase() );

        if ( type == null || type.length() == 0 )
        {
            if ( tShortPageName.startsWith( "Suite" ) )
            {
                tResult = PAGE_TYPE_SUITE;
            }
            else if ( tShortPageName.startsWith( "Test" ) )
            {
                tResult = PAGE_TYPE_TEST;
            }
            else
            {
                throw new MojoExecutionException( "Parameter 'type' is mandatory when the page name doesn't "
                    + "begin with 'Test' or 'Suite' according to FitNesse convention. FitNesse server is: "
                    + this.toString() );
            }
        }
        else if ( !PAGE_TYPE_SUITE.equals( type ) && !PAGE_TYPE_TEST.equals( type ) )
        {
            throw new MojoExecutionException( "Invalid type [" + type + "] for the server [" + this.toString()
                + "], should be either [suite] or [test]." );
        }
        else
        {
            tResult = type;
        }
        return tResult;
    }

    /**
     * Accessor.
     * 
     * @param pHostName The name of the FitNesse server.
     */
    public void setHostName( String pHostName )
    {
        this.hostName = pHostName;
    }

    /**
     * Accessor.
     * 
     * @param pPort The listen port of the FitNesse server.
     */
    public void setPort( int pPort )
    {
        this.port = pPort;
    }

    /**
     * Accessor.
     * 
     * @return The server identifier, used for providing credentials.
     */
    public String getServerId()
    {
        return serverId;
    }

    /**
     * Accessor.
     * 
     * @param pServerId The server identifier, used for providing credentials.
     */
    public void setServerId( String pServerId )
    {
        this.serverId = pServerId;
    }

    /**
     * Check the whole configuration of the server represented by this instance.
     * 
     * @throws MojoExecutionException When the configuration is invalid.
     */
    public void checkConfiguration()
        throws MojoExecutionException
    {
        if ( hostName == null || hostName.length() == 0 )
        {
            throw new MojoExecutionException( "Fitnesse host is mandatory." );
        }
        if ( port <= MIN_PORT || port > MAX_PORT )
        {
            throw new MojoExecutionException( "The port should be a valid IP port [" + port + "]." );
        }
        if ( pageName == null || pageName.length() == 0 )
        {
            throw new MojoExecutionException( "Fitnesse page name is mandatory." );
        }
    }

    /**
     * Accessor.
     * 
     * @param pType The type of the FitNesse page.
     */
    public void setType( String pType )
    {
        this.type = pType;
    }

    public String getSuiteFilter()
    {
        return suiteFilter;
    }

    public void setSuiteFilter( String suiteFilter )
    {
        this.suiteFilter = suiteFilter;
    }

}
