package org.codehaus.mojo.delicious;

/*
 * Copyright 2005 Ashley Williams.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.xml.sax.SAXException;

/**
 * A high level service access class for Delicious bookmarks.
 * @author ashley
 *
 */
public class DeliciousService
{
    private static Logger logger = Logger.getLogger( DeliciousService.class );

    private DeliciousConnection connection;

    private String serviceUnavailableMessage = "delicious service is unavailable";

    private DeliciousListener listener;

    private String userName;

    private String password;

    private int code;

    /**
     * Returns true if the given exception represents the service unavailable exception.
     * @param e
     * @return
     */
    public boolean isServiceUnavailableException( RuntimeException e )
    {
        return serviceUnavailableMessage.equals( e.getMessage() );
    }

    /**
     * Plugin an alternative connection - probably for testing.
     */
    public DeliciousService( DeliciousConnection connection )
    {
        this.connection = connection;
        this.listener = createDefaultListener();
    }

    /**
     * Creates a service that can connect to the REST api.
     */
    public DeliciousService()
    {
        this( new LiveConnection() );
    }

    /**
     * Creates the default listener that just logs as info.
     * @return
     */
    private DeliciousListener createDefaultListener()
    {
        return new DeliciousListener()
        {
            public void serviceDone( int code, Reader responseBody )
                throws IOException
            {
                logger.info( new Integer( code ) );
                Util.logStream( logger, Priority.INFO, responseBody );
            }
        };
    }

    /**
     * Gets the currently set listener.
     * 
     * @return
     */
    public DeliciousListener getListener()
    {
        return listener;
    }

    /**
     * Currently sets the one and only listener.
     * @param listener
     */
    public void setListener( DeliciousListener listener )
    {
        this.listener = listener;
    }

    /**
     * The code of the most recent server communication.
     * @return
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Adds the links found at the given reader to the delicious service.
     * The reader is assumed to be for a valid xml file containing anchor
     * tags.
     * 
     * @param links
     * @param replace
     * @throws IOException
     * @throws InterruptedException
     */
    public void addBookmarks( Reader links, Boolean replace )
        throws IOException, InterruptedException
    {
        addBookmarks( new BookmarkParser().parse( links, new BookmarkGroup() ), replace );
    }

    /**
     * Adds the given group of bookmarks.
     * @param group
     * @param replace
     * @throws IOException
     * @throws InterruptedException
     */
    public void addBookmarks( BookmarkGroup group, Boolean replace )
        throws IOException, InterruptedException
    {
        addBookmarks( group.getBookmarks(), replace );
    }

    /**
     * Adds the given list of bookmars to the delicious service.
     * @param bookmarks
     * @param replace
     * @throws InterruptedException 
     * @throws IOException 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws InterruptedException
     */
    public void addBookmarks( List bookmarks, Boolean replace )
        throws IOException, InterruptedException
    {
        Iterator allBookmarks = bookmarks.iterator();
        while ( allBookmarks.hasNext() )
        {
            Bookmark bookmark = (Bookmark) allBookmarks.next();
            addBookmark( bookmark, replace );
        }
    }

    /**
     * Adds the given bookmark to the delicious service.
     * @param bookmark
     * @param replace TODO
     * @throws IOException
     * @throws InterruptedException
     */
    public void addBookmark( Bookmark bookmark, Boolean replace )
        throws IOException, InterruptedException
    {
        addPost( bookmark.getLocation(), bookmark.getTitle(), bookmark.getTags(), bookmark.getComments(), replace );
    }

    /**
     * Adds the links found at the given path, that can be a local file or net URL.
     * Convenience method.
     * @param replace
     * @param links
     * @throws IOException
     * @throws InterruptedException
     */
    public void addBookmarks( String linksPage, Boolean replace )
        throws IOException, InterruptedException
    {
        addBookmarks( Util.getReader( linksPage ), replace );
    }

    /**
     * Sets the credential to be used for this service.
     * @param userName
     * @param password
     */
    public void setUser( String userName, String password )
    {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Fetches a list of dates with the number of posts at each date.
     * @param tags
     * @throws IOException
     * @throws InterruptedException
     */
    public void fetchDates( String tags )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        if ( tags != null )
        {
            formFields.put( "tag", tags );
        }

        doService( "posts", "dates", formFields );
    }

    /**
     * Fetches a list of posts with the given search criteria.
     * Either specify a tags/date combination, just a date or just a url.
     * @param tags
     * @throws IOException
     * @throws InterruptedException
     */
    public void fetchPosts( String tags, String date, String url )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        if ( tags != null )
        {
            formFields.put( "tag", tags );
        }
        if ( date != null )
        {
            formFields.put( "dt", date );
        }
        if ( url != null )
        {
            formFields.put( "url", url );
        }

        doService( "posts", "get", formFields );
    }

    /**
     * Fetches a list of most recent posts, possibly filtered by tag, maxes out at 100.
     */
    public void fetchRecentPosts( String tags, String count )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        if ( tags != null )
        {
            formFields.put( "tag", tags );
        }
        if ( count != null )
        {
            formFields.put( "count", count );
        }
        doService( "posts", "recent", formFields );
    }

    /**
     * Fetches all posts. use sparingly. call update function first to see if you need to fetch this at all.
     */
    public void fetchAllPosts( String tags )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        if ( tags != null )
        {
            formFields.put( "tag", tags );
        }
        doService( "posts", "all", formFields );
    }

    /**
     * Gets the time of the last update.
     */
    public void fetchUpdateTime()
        throws IOException, InterruptedException
    {
        doService( "posts", "update", null );
    }

    /**
     * Adds the post with the given information.
     * @param url
     * @param description
     * @param tags
     * @param extended
     * @param replace
     * @throws IOException
     * @throws InterruptedException
     */
    public void addPost( String url, String description, String tags, String extended, Boolean replace )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        formFields.put( "url", url );
        formFields.put( "description", description );
        formFields.put( "extended", extended );
        formFields.put( "tags", tags );
        //		Todo
        //		formFields.put("dt", new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ").format(new Date()));
        formFields.put( "replace", replace.toString() );
        doService( "posts", "add", formFields );
    }

    /**
     * Deletes the post at the given url.
     * @param url
     * @throws IOException
     * @throws InterruptedException
     */
    public void deletePost( String url )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        formFields.put( "url", url );
        doService( "posts", "delete", formFields );
    }

    /**
     * Fetches the tags used by this account.
     * @throws IOException
     * @throws InterruptedException
     */
    public void fetchTags()
        throws IOException, InterruptedException
    {
        doService( "tags", "get", null );
    }

    /**
     * Renames the given tag.
     * @param url
     * @throws IOException
     * @throws InterruptedException
     */
    public void renameTag( String oldName, String newName )
        throws IOException, InterruptedException
    {
        HashMap formFields = new HashMap();
        formFields.put( "old", oldName );
        formFields.put( "new", newName );
        doService( "tags", "rename", formFields );
    }

    /**
     * Invokes the delicous service defined by the supplied url and query.
     * The username and password are supplied for http-auth basic authorisation.
     * According to the guidelines at http://del.icio.us/doc/api this method:
     * <ul>
     * <li>waits 1 second before properly executing</li>
     * <li>bails out on receipt of a 503 error</li>
     * </ul>
     * For an ordinairy internal server error this method will try up to
     * a maximum number of times before bailing out.
     * @param category TODO
     * @param command
     * @param formFields
     * @throws IOException
     * @throws InterruptedException
     */
    public void doService( String category, String command, HashMap formFields )
        throws IOException, InterruptedException
    {
        int tryCount = 0;
        boolean shouldTry = true;
        while ( shouldTry )
        {
            tryCount++;
            try
            {
                doServiceImpl( category, command, formFields );
                shouldTry = false;
            }
            catch ( RuntimeException e )
            {
                if ( code == HttpStatus.SC_INTERNAL_SERVER_ERROR )
                {
                    logger.warn("got an internal server error");
                    if ( tryCount == 3 )
                    {
                        logger.warn("giving up");
                        throw e;
                    }
                    logger.warn("will try again");
                }
                else
                {
                    throw e;
                }
            }
        }
    }

    private void doServiceImpl( String category, String command, HashMap formFields )
        throws InterruptedException, IOException, HttpException
    {
        Thread.sleep( Messages.getCourtesyTime().longValue() );
        HttpClient client = new HttpClient();
        client.getState().setCredentials( new AuthScope( Messages.getDeliciousHost(), 80 ),
                                          new UsernamePasswordCredentials( userName, password ) );
        GetMethod httpMethod = new GetMethod( getServiceUrl( category, command ) );
        if ( formFields != null && formFields.size() > 0 )
        {
            httpMethod.setQueryString( getQuery( formFields ) );
        }
        code = connection.executeMethod( client, httpMethod );
        notifyListener( httpMethod, code );
        httpMethod.releaseConnection();
        if ( code != HttpStatus.SC_OK )
        {
            throw new RuntimeException( serviceUnavailableMessage );
        }
    }

    private void notifyListener( GetMethod httpMethod, int code )
        throws IOException
    {
        // all this to cope with the fact the response body may be null for the test connection
        Reader reader;
        InputStream responseBodyAsStream = httpMethod.getResponseBodyAsStream();

        if ( responseBodyAsStream != null )
        {
            reader = new InputStreamReader( responseBodyAsStream );
        }
        else
        {
            reader = new StringReader( "" );
        }
        getListener().serviceDone( code, reader );
    }

    /**
     * Gets the service url for the given name.
     * @param category TODO
     * @param command
     * @return
     */
    private String getServiceUrl( String category, String command )
    {
        return Messages.getDeliciousUrl() + "/" + category + "/" + command;
    }

    /**
     * Converts the given hashmap of field key-value pairs into the equivalent array.
     * @param formFields
     * @return
     */
    private NameValuePair[] getQuery( HashMap formFields )
    {
        NameValuePair[] query = new NameValuePair[formFields.size()];
        int i = 0;

        Iterator allFields = formFields.entrySet().iterator();
        while ( allFields.hasNext() )
        {
            Map.Entry field = (Map.Entry) allFields.next();
            query[i++] = new NameValuePair( (String) field.getKey(), (String) field.getValue() );
        }

        return query;
    }
}
