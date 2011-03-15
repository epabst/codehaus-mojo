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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;

/**
 * Swing component for configuring the parameters of the Force functions in a given ForceSimulator. Useful for exploring
 * different parameterizations when crafting a visualization.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class MyJForcePanel extends JPanel
{

    /**
     * 
     */
    private static final long serialVersionUID = -7105592284288009238L;

    // private ForcePanelChangeListener lstnr = new ForcePanelChangeListener();

    private ForceSimulator fsim;

    private class SingleParametersControlls implements ChangeListener
    {
        private JLabel label;

        private JSlider slider;

        private JLabel valueLabel;

        private Force force;

        private int paramNb;

        public SingleParametersControlls( Force f, int paramNb )
        {
            this.paramNb = paramNb;
            label = new JLabel( f.getParameterName( paramNb ) );
            slider =
                new JSlider( (int) ( f.getMinValue( paramNb ) * 100000 ), (int) ( f.getMaxValue( paramNb ) * 100000 ) );
            slider.setValue( (int) ( f.getParameter( paramNb ) * 100000 ) );
            slider.addChangeListener( this );
            valueLabel = new JLabel( String.valueOf( f.getParameter( paramNb ) ) );
            force = f;
        }

        private float getValue()
        {
            return (float) ( slider.getValue() / 100000.0 );
        }

        public void setValue( float new_value )
        {
            slider.setValue( (int) new_value * 100000 );
            force.setParameter( paramNb, new_value );
            updateValueLabel();
        }

        public void stateChanged( ChangeEvent e )
        {
            force.setParameter( paramNb, getValue() );
            updateValueLabel();
            if ( changeListener != null )
                changeListener.stateChanged( e );
        }

        public void updateValueLabel()
        {
            valueLabel.setText( String.valueOf( getValue() ) );
        }

        public JLabel getLabel()
        {
            return label;
        }

        public JSlider getSlider()
        {
            return slider;
        }

        public JLabel getValueLabel()
        {
            return valueLabel;
        }

        public Force getForce()
        {
            return force;
        }

        public int getParamNb()
        {
            return paramNb;
        }
    }

    private ChangeListener changeListener;

    private List<SingleParametersControlls> controlls;

    /**
     * Create a new JForcePanel
     * 
     * @param fsim
     *            the ForceSimulator to configure
     */
    public MyJForcePanel( ForceSimulator fsim )
    {
        this.fsim = fsim;
        // sliders = new LinkedList<JSlider>();
        // values = new LinkedList<JLabel>();
        controlls = new LinkedList<SingleParametersControlls>();
        initUI();
    }

    /**
     * Initialize the UI.
     */
    private void initUI()
    {
        controlls.clear();
        this.setBackground( Color.green );

        GridBagLayout gbl_main = new GridBagLayout();
        gbl_main.columnWeights = new double[] { 1.0 };

        this.setLayout( gbl_main );
        GridBagConstraints main_c = new GridBagConstraints();
        main_c.gridx = 0;
        main_c.gridy = 0;
        main_c.gridheight = 1;
        main_c.gridwidth = 1;
        main_c.weightx = 1;
        main_c.weighty = 1;
        main_c.anchor = GridBagConstraints.WEST;

        main_c.fill = GridBagConstraints.HORIZONTAL;

        if ( fsim != null )
        {
            Force[] forces = fsim.getForces();
            for ( int i = 0; i < forces.length; i++ )
            {

                Force f = forces[i];
                // Box v = new Box( BoxLayout.Y_AXIS );
                JPanel v = new JPanel();
                GridBagLayout gbl = new GridBagLayout();
                gbl.columnWeights = new double[] { 0.0, 1.0, 0.0 };
                gbl.columnWidths = new int[] { 10, 60, 50 };
                v.setLayout( gbl );
                GridBagConstraints c = new GridBagConstraints();// 0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new
                // Insets(2,2,2,2),0,0);
                c.gridx = 0;
                c.gridy = 0;
                c.gridheight = 1;
                c.weightx = 1.0;
                c.weighty = 1.0;
                c.anchor = GridBagConstraints.WEST;
                for ( int j = 0; j < f.getParameterCount(); j++ )
                {
                    SingleParametersControlls spc = new SingleParametersControlls( f, j );

                    c.gridx = 0;
                    c.gridwidth = 3;
                    c.fill = GridBagConstraints.NONE;
                    v.add( spc.getLabel(), c );

                    c.gridx = 1;
                    c.gridy++;
                    c.gridwidth = 1;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    v.add( spc.getSlider(), c );

                    c.gridx++;
                    c.fill = GridBagConstraints.NONE;
                    v.add( spc.getValueLabel(), c );

                    c.gridy++;
                }
                String name = f.getClass().getName();
                name = name.substring( name.lastIndexOf( "." ) + 1 );
                v.setBorder( BorderFactory.createTitledBorder( name ) );
                this.add( v, main_c );
                main_c.gridy++;
            }
        }
    }

    public ChangeListener getChangeListener()
    {
        return changeListener;
    }

    public void setChangeListener( ChangeListener changeListener )
    {
        this.changeListener = changeListener;
    }

} // end of class JForcePanel
