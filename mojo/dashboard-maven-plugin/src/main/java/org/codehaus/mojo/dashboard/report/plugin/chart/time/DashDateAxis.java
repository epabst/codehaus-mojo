package org.codehaus.mojo.dashboard.report.plugin.chart.time;

/*
 * Copyright 2008 David Vicente
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author David Vicente
 *
 */
public class DashDateAxis extends DateAxis
{
    private static final long serialVersionUID = -1L;

    double m_angle;

    /**
     * @see org.jfree.chart.axis.DateAxis#refreshTicksHorizontal(java.awt.Graphics2D, java.awt.geom.Rectangle2D,
     *      org.jfree.ui.RectangleEdge)
     */

    protected List refreshTicksHorizontal( Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge )
    {

        if ( getDateTickLabelAngle() == 0.0 )
        {
            return super.refreshTicksHorizontal( g2, dataArea, edge );
        }
        else
        {
            setVerticalTickLabels( true );
            List ticks = super.refreshTicksHorizontal( g2, dataArea, edge );
            List ret = new ArrayList();

            for ( int i = 0; i < ticks.size(); i++ )
            {
                Object tick = ticks.get( i );
                if ( tick instanceof DateTick )
                {
                    DateTick dateTick = (DateTick) tick;
                    ret.add( new DateTick( dateTick.getDate(), dateTick.getText(), dateTick.getTextAnchor(),
                                           dateTick.getRotationAnchor(), getDateTickLabelAngle() ) );
                }
                else
                {
                    ret.add( tick );
                }
            }
            return ret;
        }
    }

    public double getDateTickLabelAngle()
    {
        return m_angle;
    }

    public void setDateTickLabelAngle( double angle )
    {
        m_angle = angle;
    }

}
