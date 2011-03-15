package org.codehaus.mojo.delicious;

import junit.framework.TestCase;

public class BookmarkTest
    extends TestCase
{
    /**
     * Tests that unique bookmarks get added ok.
     *
     */
    public void testAddUnique() {
        Bookmark b1 = new Bookmark("http://acme.b1.com", "b1", "red green blue");
        Bookmark b2 = new Bookmark("http://acme.b2.com", "b2", "red green blue");
        BookmarkGroup group = new BookmarkGroup();
        group.addBookmark(b1);
        group.addBookmark(b2);
        assertEquals(2, group.size());
        assertEquals(b1.getTitle(), group.get(0).getTitle());
        assertEquals(b2.getTitle(), group.get(1).getTitle());
    }

    /**
     * Tests that an added bookmark replaces an existing bookmark where its location already exists.
     *
     */
    public void testAddDuplicate() {
        Bookmark b1 = new Bookmark("http://acme.com", "b1", "red green blue");
        Bookmark b2 = new Bookmark("http://acme.com", "b2", "red green blue");
        BookmarkGroup group = new BookmarkGroup();
        group.addBookmark(b1);
        group.addBookmark(b2);
        assertEquals(1, group.size());
        assertEquals(b2.getTitle(), group.get(0).getTitle());
    }

    /**
     * Tests that a tag gets added across the board.
     * Where a tag already exists it shouldn't get added.
     */
    public void testAddTag() {
        String b1Tags = "red green blue";
        String b2Tags = "green red blue";
        String b3Tags = "green blue red";
        String b4Tags = "green blue";
        String b5Tags = "green blue redred";
        Bookmark b1 = new Bookmark("http://acme.b1.com", "b1", b1Tags);
        Bookmark b2 = new Bookmark("http://acme.b2.com", "b2", b2Tags);
        Bookmark b3 = new Bookmark("http://acme.b3.com", "b3", b3Tags);
        Bookmark b4 = new Bookmark("http://acme.b4.com", "b4", b4Tags);
        Bookmark b5 = new Bookmark("http://acme.b5.com", "b5", b5Tags);
        BookmarkGroup group = new BookmarkGroup();
        group.addBookmark(b1);
        group.addBookmark(b2);
        group.addBookmark(b3);
        group.addBookmark(b4);
        group.addBookmark(b5);
        group.addTag("red");
        assertEquals(b1Tags, b1.getTags());
        assertEquals(b2Tags, b2.getTags());
        assertEquals(b3Tags, b3.getTags());
        assertEquals(b4Tags + " red", b4.getTags());
        //TODO see addTag method
//        assertEquals(b5Tags + " red", b5.getTags());
    }
}
