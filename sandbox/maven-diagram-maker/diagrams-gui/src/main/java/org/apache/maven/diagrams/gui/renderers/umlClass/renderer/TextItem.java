package org.apache.maven.diagrams.gui.renderers.umlClass.renderer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import prefuse.util.FontLib;
import prefuse.util.StrokeLib;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class TextItem implements RendererListItem
{
    private static final String FONT_NAME = "Arial";

    private static final double FONT_SIZE = 3.0;

    private Boolean bold;

    private Boolean underline;

    private Boolean center;

    private String text;

    private Font font;

    private Rectangle2D bounds;

    public void setUnderline( Boolean underline )
    {
        this.underline = underline;
        bounds = null;
    }

    public Boolean getUnderline()
    {
        return underline;

    }

    public void setBold( Boolean bold )
    {
        bounds = null;
        this.bold = bold;
    }

    public Boolean getBold()
    {
        return bold;
    }

    public Boolean getCenter()
    {
        return center;
    }

    public void setCenter( Boolean center )
    {
        this.center = center;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
        bounds = null;
    }

    public Font getFont()
    {
        if ( font == null )
        {
            font = FontLib.getFont( FONT_NAME, bold ? Font.BOLD : Font.PLAIN, FONT_SIZE );
        }
        return font;
    }

    public Rectangle2D getBounds( FontRenderContext context )
    {
        if ( bounds == null )
        {
            Font f = getFont();
            bounds = f.getStringBounds( text, context );
        }
        return bounds;
    }

    public Double getWidth( FontRenderContext context )
    {
        return getBounds( context ).getWidth();
    }

    public Double getHeight( FontRenderContext context )
    {
        return getBounds( context ).getHeight() * 1.25;
    }

    public void draw( double x, double y, double max_width, Graphics2D canvas )
    {
        Font f = getFont();
        canvas.setFont( f );
        Rectangle2D bounds = getBounds( canvas.getFontRenderContext() );
        if ( center )
        {
            canvas.drawString( text, (float) ( x + ( max_width - bounds.getWidth() ) / 2 ),
                               (float) ( y + bounds.getHeight() ) );
        }
        else
        {
            canvas.drawString( text, (float) ( x + 1.0 ), (float) ( y + bounds.getHeight() ) );
        }
        canvas.setStroke( StrokeLib.getStroke( (float) 0.1 ) );

        if ( underline )
            canvas.draw( new Line2D.Double( x + 1, y + bounds.getHeight() + 0.2, x + bounds.getWidth() * 0.9, y
                            + bounds.getHeight() + 0.2 ) );
    }

    public TextItem( String text, Boolean bold, Boolean underline, Boolean center )
    {
        super();
        this.text = text;
        this.bold = bold;
        this.underline = underline;
        this.center = center;
    }
}
