package org.codehaus.mojo.pomtools.console.toolkit.widgets;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TableColumn
{
    public static final TextStyle BOLD = new TextStyle( "bold" );
    
    public static final TextStyle UNDERLINE = new TextStyle( "underline" );
    
    
    public static final Alignment ALIGN_LEFT = new Alignment( "L" );

    public static final Alignment ALIGN_RIGHT = new Alignment( "R" );

    public static final Alignment DEFAULT_ALIGN = ALIGN_LEFT;
    

    public static final TableColumn ALIGN_LEFT_COLUMN = new TableColumn( ALIGN_LEFT );

    public static final TableColumn ALIGN_RIGHT_COLUMN = new TableColumn( ALIGN_RIGHT );
    

    public static final boolean DEFAULT_WRAP = true;

    private final Alignment alignment;

    private boolean wrap = DEFAULT_WRAP;
    
    private TextStyle textStyle;

    private int fixedWidth = -1;
    
    private int maxWidth = -1;

    public TableColumn()
    {
        this( DEFAULT_ALIGN );
    }

    public TableColumn( Alignment alignment )
    {
        this.alignment = alignment;
    }
    
    public TableColumn( Alignment alignment, TextStyle textStyle )
    {
        this( alignment );
        this.textStyle = textStyle;
    }

    public TableColumn( int fixedWidth )
    {
        this( DEFAULT_ALIGN, fixedWidth );
    }

    public TableColumn( Alignment alignment, int fixedWidth )
    {
        this.alignment = alignment;
        setFixedWidth( fixedWidth );
    }
    
    private static final class Alignment
    {
        private final String direction;

        private Alignment( String direction )
        {
            this.direction = direction;
        }

        public String toString()
        {
            return direction;
        }
    }
    
    private static final class TextStyle
    {
        private final String style;
        
        private TextStyle( String style )
        {
            this.style = style;
        }
        
        public String toString()
        {
            return style;
        }
    }
    
    
    public TextStyle getTextStyle()
    {
        return textStyle;
    }

    public void setTextStyle( TextStyle textStyle )
    {
        this.textStyle = textStyle;
    }

    public Alignment getAlignment()
    {
        return alignment;
    }

    public int getFixedWidth()
    {
        return fixedWidth;
    }

    public TableColumn setFixedWidth( int columnWidth )
    {
        this.fixedWidth = columnWidth;

        return this;
    }

    public boolean isWrap()
    {
        return wrap;
    }

    public TableColumn setWrap( boolean wrapColumn )
    {
        this.wrap = wrapColumn;

        return this;
    }

    public int getMaxWidth()
    {
        return maxWidth;
    }

    public void setMaxWidth( int maxWidth )
    {
        this.maxWidth = maxWidth;
    }

}
