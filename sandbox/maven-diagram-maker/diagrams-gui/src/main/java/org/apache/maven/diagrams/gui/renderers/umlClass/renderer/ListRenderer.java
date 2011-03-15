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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.apache.maven.diagrams.gui.renderers.ConfigurableRenderer;
import org.apache.maven.diagrams.gui.renderers.RendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.RendererNodeCache;

import prefuse.util.ColorLib;
import prefuse.util.StrokeLib;
import prefuse.visual.VisualItem;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public abstract class ListRenderer implements ConfigurableRenderer
{
    private RendererConfiguration configuration;

    public ListRenderer()
    {
        super();
    }

    public boolean locatePoint( Point2D arg0, VisualItem arg1 )
    {
        return arg1.getBounds().contains( arg0 );
    }

    private double getAffineTransformationScale( AffineTransform transf )
    {
        Point2D zeroDst = new Point2D.Double();
        Point2D oneDst = new Point2D.Double();
        transf.transform( new Point2D.Double( 0.0, 0.0 ), zeroDst );
        transf.transform( new Point2D.Double( 0.0, 1.0 ), oneDst );
        return oneDst.getY() - zeroDst.getY();
    }

    /**
     * TODO: Temporary. We need rules engine to such a things.
     * 
     * @param arg1
     * @return
     */
    protected int getFillColor( VisualItem arg1 )
    {
        return arg1.getFillColor();
    }

    public void render( Graphics2D arg0, VisualItem arg1 )
    {
        Color c = ColorLib.getColor( getFillColor( arg1 ) );
        arg0.setColor( c );
        arg0.setBackground( c );
        arg0.setStroke( StrokeLib.getStroke( (float) 0.4 ) );
        Rectangle2D bounds = arg1.getBounds();

        arg0.fill( bounds );

        arg0.setColor( new Color( arg1.getStrokeColor() ) );
        arg0.draw( arg1.getBounds() );

        if ( getAffineTransformationScale( arg0.getTransform() ) * arg0.getFontMetrics().getHeight() > 2.0 )
        {
            List<RendererListItem> list = getListInternal( arg1 );
            double y = arg1.getBounds().getMinY();
            for ( RendererListItem rli : list )
            {
                rli.draw( bounds.getMinX(), y, bounds.getWidth(), arg0 );
                y = y + rli.getHeight( arg0.getFontRenderContext() );
            }
        }
    }

    public void setBounds( VisualItem arg0 )
    {
        double width = getWidth( arg0 );
        double height = getHeight( arg0 );
        arg0.setBounds( arg0.getX() - width / 2, arg0.getY() - height / 2, width, height );
    }

    private double getWidth( VisualItem vi )
    {
        RendererNodeCache rnc = provideCacheAboutItem( vi );
        if ( rnc.getNodeWidth() != null )
        {
            return rnc.getNodeWidth();
        }
        else
        {
            List<RendererListItem> list = getListInternal( vi );
            double width = getWidth( list ) + 6.0;
            rnc.setNodeWidth( width );
            return width;
        }

    }

    private double getWidth( List<RendererListItem> list )
    {
        double width = 0;
        for ( RendererListItem rli : list )
        {
            Double item_width = rli.getWidth( DEFAULT_GRAPHICS.getFontRenderContext() );
            if ( ( item_width != null ) && ( item_width > width ) )
                width = item_width;
        }
        return width;
    }

    public double getHeight( VisualItem vi )
    {
        // if ( vi.getFont().getSize() == 10 )
        // return 10;

        RendererNodeCache rnc = provideCacheAboutItem( vi );

        if ( rnc.getNodeHeight() != null )
        {
            return rnc.getNodeHeight();
        }
        else
        {
            double height = getHeight( getListInternal( vi ) );
            rnc.setNodeHeight( height );
            return height;
        }
    }

    public double getHeight( List<RendererListItem> list )
    {
        double height = 0;
        for ( RendererListItem rli : list )
        {
            height = height + rli.getHeight( DEFAULT_GRAPHICS.getFontRenderContext() );
        }
        return height;
    }

    private List<RendererListItem> getListInternal( VisualItem vi )
    {
        ListRendererNodeCache rnc = (ListRendererNodeCache) provideCacheAboutItem( vi );

        if ( rnc.getRendererListItemList() != null )
        {
            return rnc.getRendererListItemList();
        }
        else
        {
            List<RendererListItem> list = getList( vi );
            rnc.setRendererListItemList( list );
            return list;
        }
    }

    abstract protected List<RendererListItem> getList( VisualItem vi );

    public RendererConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( RendererConfiguration newRendererConfiguration )
    {
        configuration = newRendererConfiguration;
    }

    // ======== TODO: To move to abstract renderer ========================================
    protected RendererNodeCache getCacheAboutItem( VisualItem item )
    {
        Object res = item.get( RendererNodeCache.CACHE_ITEM_COLUMN_NAME );
        if ( RendererNodeCache.class.isInstance( res ) )
        {
            return (RendererNodeCache) res;
        }
        else
        {
            return null;
        }
    }

    protected void setCacheAboutItem( VisualItem item, RendererNodeCache rnc )
    {
        item.set( RendererNodeCache.CACHE_ITEM_COLUMN_NAME, rnc );
    }

    protected RendererNodeCache provideCacheAboutItem( VisualItem item )
    {
        RendererNodeCache cache = getCacheAboutItem( item );
        if ( cache == null )
        {
            cache = getCustomCacheImplementation();
            setCacheAboutItem( item, cache );
        }
        item.set( RendererNodeCache.CACHE_ITEM_COLUMN_NAME, cache );
        return cache;
    }

    protected RendererNodeCache getCustomCacheImplementation()
    {
        // return new RendererNodeCacheImpl();
        return new ListRendererNodeCache();
    }

}