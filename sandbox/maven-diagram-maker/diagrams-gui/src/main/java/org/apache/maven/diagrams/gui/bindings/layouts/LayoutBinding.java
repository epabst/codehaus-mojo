package org.apache.maven.diagrams.gui.bindings.layouts;

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
import org.apache.maven.diagrams.gui.layouts.AbstractLayoutConfigurationPanel;

import prefuse.action.layout.Layout;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class LayoutBinding<LCC extends AbstractLayoutConfiguration>
{
    private Class<? extends Layout> layoutClass;

    private String name;

    private String description;

    private Class<LCC> configurationClass;

    private Class<? extends AbstractLayoutConfigurationPanel<LCC>> editingPanelClass;

    public Class<? extends Layout> getLayoutClass()
    {
        return layoutClass;
    }

    public void setLayoutClass( Class<? extends Layout> layoutClass )
    {
        this.layoutClass = layoutClass;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public Class<LCC> getConfigurationClass()
    {
        return configurationClass;
    }

    public void setConfigurationClass( Class<LCC> configurationClass )
    {
        this.configurationClass = configurationClass;
    }

    public Class<? extends AbstractLayoutConfigurationPanel<LCC>> getEditingPanelClass()
    {
        return editingPanelClass;
    }

    public void setEditingPanelClass( Class<? extends AbstractLayoutConfigurationPanel<LCC>> editingPanelClass )
    {
        this.editingPanelClass = editingPanelClass;
    }

}
