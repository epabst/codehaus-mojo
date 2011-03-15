package org.codehaus.mojo.delicious;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BookmarkGroup
{
    private HashMap bookmarksMap;

    private Vector bookmarks;

    public BookmarkGroup()
    {
        bookmarksMap = new HashMap();
        bookmarks = new Vector();
    }

    /**
     * Adds any links found in the given page to this group as bookmarks.
     * The page can be file:// or http:// url.
     * @param linksPage
     * @throws IOException 
     */
    public void addLinks( String linksPage )
        throws IOException
    {
        addLinks( Util.getReader( linksPage ) );
    }

    /**
     * Adds any links found in the given page to this group as bookmarks.
     * 
     * @param linksPage
     * @throws IOException
     */
    public void addLinks( File linksPage )
        throws IOException
    {
        addLinks( new FileReader(linksPage) );
    }

    /**
     * Adds any links found in the given page to this group as bookmarks.
     * 
     * @param linksPage
     * @throws IOException
     */
    public void addLinks( Reader linksPage )
        throws IOException
    {
        new BookmarkParser().parse( linksPage, this );
    }

    /**
     * Adds the given bookmark.
     * If a bookmark already exists with the same location it is
     * replaced and the old one returned.
     * @param bookmark
     * @return
     */
    public Object addBookmark( Bookmark bookmark )
    {
        bookmarks.remove( bookmark );
        Object oldBookmark = bookmarksMap.put( bookmark.getLocation(), bookmark );
        if ( oldBookmark != null )
        {
            bookmarks.remove( oldBookmark );
        }
        bookmarks.add( bookmark );
        return oldBookmark;
    }

    /**
     * Removes the given bookmark.
     * @param bookmark
     * @return
     */
    public Object removeBookmark( Bookmark bookmark )
    {
        bookmarks.remove( bookmark );
        return bookmarksMap.remove( bookmark.getLocation() );
    }

    /**
     * Adds the given tag to all those bookmarks that don't already
     * include it.
     * @param tag
     */
    public void addTag( String tag )
    {
        Iterator allBookmarks = bookmarks.iterator();
        while ( allBookmarks.hasNext() )
        {
            Bookmark bookmark = (Bookmark) allBookmarks.next();
            bookmark.addTag( tag );
        }
    }

    public String toString()
    {
        String toString = "";
        Iterator allBookmarks = bookmarks.iterator();
        while ( allBookmarks.hasNext() )
        {
            Bookmark bookmark = (Bookmark) allBookmarks.next();
            toString += bookmark.toString() + "\n\n";
        }
        return toString;
    }

    public List getBookmarks()
    {
        return bookmarks;
    }

    public int size()
    {
        return bookmarks.size();
    }

    public Bookmark get( int i )
    {
        return (Bookmark) bookmarks.get( i );
    }
}
