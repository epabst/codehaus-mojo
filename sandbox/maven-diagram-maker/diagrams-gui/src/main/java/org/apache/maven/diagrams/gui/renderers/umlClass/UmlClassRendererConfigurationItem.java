package org.apache.maven.diagrams.gui.renderers.umlClass;

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
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationItemImpl;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class UmlClassRendererConfigurationItem extends RendererConfigurationItemImpl
{
    private boolean full_class_names;

    public UmlClassRendererConfigurationItem( String a_name, boolean a_visible, boolean a_full_class_names )
    {
        super( a_name, a_visible );
        full_class_names = a_full_class_names;
    }

    public void setFull_class_names( boolean full_class_names )
    {
        this.full_class_names = full_class_names;
    }

    public boolean getFull_class_names()
    {
        return full_class_names;
    }
}
