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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.mojo.delicious.Bookmark;
import org.codehaus.mojo.delicious.DeliciousConnection;
import org.codehaus.mojo.delicious.DeliciousService;

public class DeliciousServiceTest
    extends TestCase
{
    private static class FailingConnection
        implements DeliciousConnection
    {
        private int failCount = 0;

        private final int max;

        /**
         * Create with the number of times the connection should fail before
         * it should start succeeding.
         * Value of -1 means it will always fail.
         * @param max
         */
        public FailingConnection( int max )
        {
            this.max = max;

        }

        public int executeMethod( HttpClient client, GetMethod httpMethod )
            throws IOException, HttpException
        {
            failCount++;
            int code = ( failCount <= max ) || ( max == -1 ) ? HttpStatus.SC_INTERNAL_SERVER_ERROR : HttpStatus.SC_OK;
            return code;
        }
    }

    /**
     * Adds bookmarks and ensures that the time taken is at least 1 seconds per bookmark.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    public void testTimeCommand()
        throws FileNotFoundException, IOException, InterruptedException
    {
        DeliciousService service = new DeliciousService( new TestConnection() );
        Bookmark[] bookmarks = { new Bookmark(), new Bookmark(), new Bookmark() };
        long startTime = getTime();
        service.setUser( "", "" );
        service.addBookmarks( Arrays.asList( bookmarks ), Boolean.TRUE );
        long actualTime = getTime() - startTime;
        long expectedTime = bookmarks.length * 1000L;
        assertTrue( "test was too quick, should have been (ms) at least " + expectedTime + " but was " + actualTime,
                    actualTime > expectedTime );
    }

    /**
     * Inserts an http connector that only reports service-unavailable.
     * @throws InterruptedException 
     * @throws IOException 
     *
     */
    public void testServiceUnavailable()
        throws IOException, InterruptedException
    {
        DeliciousService service = new DeliciousService( new DeliciousConnection()
        {
            public int executeMethod( HttpClient client, GetMethod httpMethod )
                throws IOException, HttpException
            {
                return HttpStatus.SC_SERVICE_UNAVAILABLE;
            }
        } );
        try
        {
            service.setUser( "", "" );
            service.addBookmark( new Bookmark(), Boolean.TRUE );
        }
        catch ( RuntimeException e )
        {
            assertEquals( service.getCode(), HttpStatus.SC_SERVICE_UNAVAILABLE );
        }
    }

    /**
     * Inserts an http connector that only reports internal server error.
     * @throws InterruptedException 
     * @throws IOException 
     *
     */
    public void testInternalServerError()
        throws IOException, InterruptedException
    {
        DeliciousService service = new DeliciousService( new FailingConnection( -1 ) );
        try
        {
            service.setUser( "", "" );
            service.addBookmark( new Bookmark(), Boolean.TRUE );
        }
        catch ( RuntimeException e )
        {
            assertEquals( service.getCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR );
        }
    }

    /**
     * Inserts an http connector that recovers from an internal server error.
     * @throws InterruptedException 
     * @throws IOException 
     *
     */
    public void testInternalServerErrorRecover()
        throws IOException, InterruptedException
    {
        DeliciousService service = new DeliciousService( new FailingConnection( 2 ) );
        service.setUser( "", "" );
        service.addBookmark( new Bookmark(), Boolean.TRUE );
        assertTrue( true );
    }

    private long getTime()
    {
        return System.currentTimeMillis();
    }
}
