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
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

public class BookmarkParserTest
    extends TestCase
{

    public void testSampleFile()
        throws Exception
    {
        assertEquals( getExpectedFileBookmarks(), getActualFileBookmarks() );
    }

    /**
     * Checks that the number of bookmarks obtained from sample pages
     * is within a certain range. This metric should last longer than actual
     * url content.
     * @throws Exception
     */
    public void TODOtestUrls()
        throws IOException
    {
        // good test, but relies on net connection
        checkUrl( "http://java.sun.com", 80, 200 );
        checkUrl( "http://www.google.com", 2, 20 );
    }

    /**
     * Tets that the url contains between min and max bookmarks.
     * @param url
     * @param minBookmarks
     * @param maxBookmarks
     * @throws IOException 
     */
    private void checkUrl( String url, int minBookmarks, int maxBookmarks )
        throws IOException
    {
        List actualBookmarks = new BookmarkParser().parse( Util.getReader( url ), new BookmarkGroup() ).getBookmarks();
        int bookmarksSize = actualBookmarks.size();
        assertTrue( "expecting less than " + maxBookmarks + " bookmarks, not " + bookmarksSize,
                    bookmarksSize < maxBookmarks );
        assertTrue( "expecting more than " + minBookmarks + " bookmarks, not " + bookmarksSize,
                    bookmarksSize > minBookmarks );
    }

    private List getActualFileBookmarks()
        throws IOException
    {
        Reader linksPage = Util.getResourceReader( "/links.xml" );
        return new BookmarkParser().parse( linksPage, new BookmarkGroup() ).getBookmarks();
    }

    private List getExpectedFileBookmarks()
    {
        Vector bookmarks = new Vector();
        addBookmark( bookmarks, "http://xbox.ign.com/", "xbox gaming ign.com" );
        addBookmark( bookmarks, "http://ps2.ign.com/", "ps2 gaming ign.com" );
        addBookmark( bookmarks, "http://ps3.ign.com/", "ps3 gaming ign.com" );
        addBookmark( bookmarks, "http://xbox360.ign.com/", "360 gaming ign.com" );
        addBookmark( bookmarks, "http://www.gamespot.com/", "gaming gamespot.com" );
        addBookmark( bookmarks,
                     "http://www.multimap.com/map/aproute.cgi?input_rt=aproute_pan&startcountry=GB&endcountry=GB",
                     "route planner map multimap.com" );
        addBookmark( bookmarks, "http://www.imdb.com/find?q=***;s=all", "imdb search imdb.com" );
        addBookmark( bookmarks, "http://cube.ign.com/", "game cube gaming ign.com" );
        return bookmarks;
    }

    private boolean addBookmark( Vector bookmarks, String url, String title )
    {
        return bookmarks.add( new Bookmark( url, title, title ) );
    }
}
