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
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class SeparatorItem implements RendererListItem
{
    double SIZE = 2.0;

    /**
     * Can return null ( don't modify current box width)
     * 
     * @param fontMetrics
     * @return
     */
    public Double getWidth( FontRenderContext context )
    {
        return null;
    }

    /**
     * Can not return null.
     * 
     * @param fontMetrics
     * @return
     */

    public Double getHeight( FontRenderContext context )
    {
        return SIZE;
    }

    public void draw( double x, double y, double max_width, Graphics2D convas )
    {
        convas.draw( new Line2D.Double( x, y + SIZE / 2, x + max_width, y + SIZE / 2 ) );
    }

}
