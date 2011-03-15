package org.apache.maven.diagrams.gui.layouts.forces;

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
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.force.DragForce;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class ForcesLayoutConfiguration extends AbstractLayoutConfiguration<ForceDirectedLayout>
{
    private ForceSimulator forceSimulator;

    public ForceSimulator getInstanceOfForceSimulator()
    {
        return cloneForceSimulator( forceSimulator );
    }

    @Override
    public void setDefaultConfiguration()
    {
        forceSimulator = new ForceSimulator();
        NBodyForce nBodyForce = new NBodyForce();
        SpringForce springForce = new SpringForce();
        DragForce dragForce = new DragForce();

        nBodyForce.setParameter( 0, (float) -9.758 );
        nBodyForce.setParameter( 1, (float) -1.0 );
        nBodyForce.setParameter( 2, (float) 0.9 );

        dragForce.setParameter( 0, (float) 0.00645 );

        springForce.setParameter( 0, (float) 0.00001 );
        springForce.setParameter( 1, (float) 160.0 );

        forceSimulator.addForce( nBodyForce );
        forceSimulator.addForce( springForce );
        forceSimulator.addForce( dragForce );
    }

    public void setInstanceOfForceSimulator( ForceSimulator source )
    {
        forceSimulator = cloneForceSimulator( source );
    }

    @Override
    public void readFromLayout( ForceDirectedLayout l )
    {
        forceSimulator = cloneForceSimulator( l.getForceSimulator() );
    }

    @Override
    public void updateLayout( ForceDirectedLayout l )
    {
        l.setForceSimulator( cloneForceSimulator( forceSimulator ) );
    }

    private static ForceSimulator cloneForceSimulator( ForceSimulator oldForceSimulator )
    {
        if ( oldForceSimulator == null )
            return null;

        ForceSimulator newForceSimulator = new ForceSimulator();
        newForceSimulator.setIntegrator( oldForceSimulator.getIntegrator() );
        newForceSimulator.setSpeedLimit( oldForceSimulator.getSpeedLimit() );
        for ( Force f : oldForceSimulator.getForces() )
        {
            newForceSimulator.addForce( duplicateForce( f ) );
        }
        return newForceSimulator;

    }

    private static Force duplicateForce( Force f )
    {
        try
        {
            Force new_force = f.getClass().newInstance();
            for ( int i = 0; i < f.getParameterCount(); i++ )
            {
                new_force.setParameter( i, f.getParameter( i ) );
            }
            return new_force;
        }
        catch ( InstantiationException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        catch ( IllegalAccessException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean canUpdateLayout( Layout layout )
    {
        return ForceDirectedLayout.class.isInstance( layout );
    }
}
