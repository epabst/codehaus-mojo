package org.codehaus.mojo.pomtools.console.widgets;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.VT100Terminal;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableColumn;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.TableLayout;
import org.codehaus.plexus.PlexusTestCase;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TableLayoutTest
    extends PlexusTestCase
{
    private Terminal term;
    
    private static final String LOREM_50 = "Lorem ipsum dolor sit amet, consectetuer volutpat.";

    private static final String LOREM_255 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Sed ultrices "
        + "magna non ligula. Nullam sed neque. Nulla tortor justo, lobortis vitae, "
        + "eleifend non, ultrices ac, nisl. Etiam imperdiet, urna eu pulvinar suscipit, "
        + "neque nulla dignissim pede posuere.";

    
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        term = new VT100Terminal();
    }

    private String generateString( int length ) 
    {
        char[] template = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        char[] chars = new char[length];
        
        for ( int i = 0; i < length; i++ )
        {
            chars[i] = template[i % 10];
        }
        
        return new String( chars );
    }
    
    public void testWrap()
    {
        final int col2Width = 30;
        final int col3Width = 50;
        
        TableColumn col1 = TableColumn.ALIGN_LEFT_COLUMN;
        
        TableColumn col2 = new TableColumn();
        col2.setFixedWidth( col2Width );

        TableColumn col3 = new TableColumn( TableColumn.ALIGN_RIGHT );
        col3.setFixedWidth( col3Width );

        TableLayout tab = new TableLayout( term, new TableColumn[] { col1, col2, col3 } );

        tab.add( new String[] { "First row",  LOREM_50,  LOREM_50  } );
        tab.add( new String[] { "Second row", LOREM_255, LOREM_255 } );
        tab.add( new String[] { "Third row",  LOREM_255, LOREM_255 } );

        System.out.println( tab.getOutput() );

        assertTrue( true );
    }
    
    public void testWrap2()
    {
        final int col1Width = 50;
        
        TableColumn col1 = new TableColumn( TableColumn.ALIGN_RIGHT );
        col1.setFixedWidth( col1Width );
        
        TableLayout tab = new TableLayout( term, new TableColumn[] { col1 } );
        
        tab.add( new String[] { LOREM_255 } );
        
//        System.out.println( tab.getOutput() );
        
        assertTrue( true );
    }
    
    public void testWrap3()
    {
        final int tableWidth = 50;
        
        TableLayout tab = new TableLayout( term, tableWidth );
        
        tab.add( new String[] { generateString( 90 ), generateString( 5 ) } );
        
      System.out.println( tab.getOutput() );
        
        assertTrue( true );
    }
    
    public void testWrap4()
    {
        final int tableWidth = 50;
        
        TableColumn col1 = new TableColumn();
        col1.setFixedWidth( 20 );
        
        TableColumn col2 = new TableColumn();
        
        TableLayout tab = new TableLayout( term, new TableColumn[] { col1, col2 }, tableWidth );
        
        tab.add( new String[] { generateString( 5 ), generateString( 90 ) } );
        
        System.out.println( tab.getOutput() );
        
        assertTrue( true );
    }
}
