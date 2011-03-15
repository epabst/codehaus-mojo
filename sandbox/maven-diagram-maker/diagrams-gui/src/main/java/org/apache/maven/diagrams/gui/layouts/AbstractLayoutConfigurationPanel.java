package org.apache.maven.diagrams.gui.layouts;

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
import javax.swing.JPanel;

import org.apache.maven.diagrams.gui.controller.LayoutController;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public abstract class AbstractLayoutConfigurationPanel<AbstractLayoutConfigurationSubclass extends AbstractLayoutConfiguration>
    extends JPanel
{
    private LayoutController controller;

    public abstract AbstractLayoutConfigurationSubclass getCurrentConfiguration();

    public abstract void setCurrentConfiguration( AbstractLayoutConfigurationSubclass configuration );

    public void setController( LayoutController controller )
    {
        this.controller = controller;
    }

    public LayoutController getController()
    {
        return controller;
    }

    public void apply()
    {
        AbstractLayoutConfigurationSubclass abstractLayoutConfigurationSubclass = getCurrentConfiguration();
        controller.updateLayoutConfiguration( abstractLayoutConfigurationSubclass );
    }

    public boolean isAutoApply()
    {
        return controller.isAutoApplyMode();
    }
}
