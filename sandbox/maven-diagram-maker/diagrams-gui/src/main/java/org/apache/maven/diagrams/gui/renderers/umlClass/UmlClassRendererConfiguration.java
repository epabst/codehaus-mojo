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
import org.apache.maven.diagrams.gui.renderers.AbstractRendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.RendererConfigurationItemImpl;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class UmlClassRendererConfiguration extends AbstractRendererConfiguration
{
    static public final String ATT_NAME = "name";

    static public final String ATT_PROPERTIES = "properties";

    static public final String ATT_PUBLIC_FIELDS = "public fields";

    static public final String ATT_PUBLIC_METHODS = "public methods";

    static public final String ATT_PROTECTED_FIELDS = "protected fields";

    static public final String ATT_PROTECTED_METHODS = "protected methods";

    static public final String ATT_PRIVATE_FIELDS = "private fields";

    static public final String ATT_PRIVATE_METHODS = "private methods";

    public UmlClassRendererConfiguration()
    {
        getRenderConfigurationItems().put( ATT_NAME, new UmlClassRendererConfigurationItem( ATT_NAME, true, true ) );
        getRenderConfigurationItems().put( ATT_PROPERTIES, new RendererConfigurationItemImpl( ATT_PROPERTIES, true ) );
        getRenderConfigurationItems().put( ATT_PUBLIC_FIELDS,
                                           new RendererConfigurationItemImpl( ATT_PUBLIC_FIELDS, true ) );
        getRenderConfigurationItems().put( ATT_PUBLIC_METHODS,
                                           new RendererConfigurationItemImpl( ATT_PUBLIC_METHODS, true ) );
        getRenderConfigurationItems().put( ATT_PROTECTED_FIELDS,
                                           new RendererConfigurationItemImpl( ATT_PROTECTED_FIELDS, false ) );
        getRenderConfigurationItems().put( ATT_PROTECTED_METHODS,
                                           new RendererConfigurationItemImpl( ATT_PROTECTED_METHODS, false ) );
        getRenderConfigurationItems().put( ATT_PRIVATE_FIELDS,
                                           new RendererConfigurationItemImpl( ATT_PRIVATE_FIELDS, false ) );
        getRenderConfigurationItems().put( ATT_PRIVATE_METHODS,
                                           new RendererConfigurationItemImpl( ATT_PRIVATE_METHODS, false ) );
    }
}
