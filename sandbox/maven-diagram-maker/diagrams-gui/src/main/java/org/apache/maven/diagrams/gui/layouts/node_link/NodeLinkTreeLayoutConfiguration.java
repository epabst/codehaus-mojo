package org.apache.maven.diagrams.gui.layouts.node_link;

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
import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfiguration;

import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class NodeLinkTreeLayoutConfiguration extends AbstractLayoutConfiguration<NodeLinkTreeLayout>
{
    public static final int ORIENT_LEFT_RIGHT = 0;

    /** A right-to-left layout orientation */
    public static final int ORIENT_RIGHT_LEFT = 1;

    /** A top-to-bottom layout orientation */
    public static final int ORIENT_TOP_BOTTOM = 2;

    /** A bottom-to-top layout orientation */
    public static final int ORIENT_BOTTOM_TOP = 3;

    private int m_orientation; // the orientation of the tree

    private double m_bspace; // the spacing between sibling nodes

    private double m_tspace; // the spacing between subtrees

    private double m_dspace; // the spacing between depth levels

    @Override
    public void readFromLayout( NodeLinkTreeLayout l )
    {
        m_orientation = l.getOrientation();
        m_bspace = l.getBreadthSpacing();
        m_tspace = l.getSubtreeSpacing();
        m_dspace = l.getDepthSpacing();

    }

    @Override
    public void updateLayout( NodeLinkTreeLayout l )
    {
        l.setOrientation( m_orientation );
        l.setBreadthSpacing( m_bspace );
        l.setSubtreeSpacing( m_tspace );
        l.setDepthSpacing( m_dspace );
    }

    @Override
    public void setDefaultConfiguration()
    {
        m_orientation = ORIENT_LEFT_RIGHT;
        m_bspace = 5;
        m_tspace = 5;
        m_dspace = 30;

    }

    /* ==================== Getters and setters ===================== */

    public int getOrientation()
    {
        return m_orientation;
    }

    public void setOrientation( int m_orientation )
    {
        this.m_orientation = m_orientation;
    }

    public double getBspace()
    {
        return m_bspace;
    }

    public void setBspace( double m_bspace )
    {
        this.m_bspace = m_bspace;
    }

    public double getTspace()
    {
        return m_tspace;
    }

    public void setTspace( double m_tspace )
    {
        this.m_tspace = m_tspace;
    }

    public double getDspace()
    {
        return m_dspace;
    }

    public void setDspace( double m_dspace )
    {
        this.m_dspace = m_dspace;
    }

    @Override
    public boolean canUpdateLayout( Layout layout )
    {
        return NodeLinkTreeLayout.class.isInstance( layout );
    }

}
