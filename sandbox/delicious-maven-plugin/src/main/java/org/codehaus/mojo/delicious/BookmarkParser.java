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

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Responsible for converting a page of links into a list of Bookmark objects.
 */
public class BookmarkParser
{
    public BookmarkParser()
    {
    }

    /**
     * Adds any links found in the given page to the given group.
     * For convenience the same group is returned.
     * @param linksPage
     * @param group
     * @return
     * @throws IOException
     */
    public BookmarkGroup parse( Reader linksPage, BookmarkGroup group )
        throws IOException
    {
        new ParserDelegator().parse( linksPage, getParserCallback( group ), true );
        return group;
    }

    /**
     * The handler returned scans for anchor tags in an xml file and creates corresponding Bookmark objects.
     * @param filter TODO
     * @return
     */
    private ParserCallback getParserCallback( final BookmarkGroup group )
    {
        return new ParserCallback()
        {
            private Bookmark bookmark = null;

            public void handleStartTag( Tag t, MutableAttributeSet a, int pos )
            {
                if ( t.equals( Tag.A ) )
                {
                    bookmark = new Bookmark();
                    bookmark.setLocation( (String) a.getAttribute( Attribute.HREF ) );
                }
            }

            public void handleEndTag( Tag t, int pos )
            {
                if ( t.equals( Tag.A ) )
                {
                    group.addBookmark( bookmark );
                    bookmark = null;
                }
            }

            public void handleText( char[] data, int pos )
            {
                if ( bookmark != null )
                {
                    String string = new String( data );
                    bookmark.setTitle( string );
                    bookmark.setTags( string );
                }
            }
        };
    }
}
