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
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.diagrams.connectors.classes.graph.ClassNode;
import org.apache.maven.diagrams.connectors.classes.model.FieldModel;
import org.apache.maven.diagrams.connectors.classes.model.MethodModel;
import org.apache.maven.diagrams.connectors.classes.model.ModifierModel;
import org.apache.maven.diagrams.gui.renderers.RendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.umlClass.UmlClassRendererConfiguration;
import org.apache.maven.diagrams.gui.renderers.umlClass.UmlClassRendererConfigurationItem;

import prefuse.visual.VisualItem;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class ClassesUMLRenderer extends ListRenderer
{
    private EnumSet<AccessModifier> methodAccessModifiers;

    private EnumSet<AccessModifier> fieldAccessModifiers;

    public ClassesUMLRenderer()
    {
        super();
        methodAccessModifiers = EnumSet.of( AccessModifier.PUBLIC );
        fieldAccessModifiers = EnumSet.of( AccessModifier.PUBLIC );
    }

    protected int getFillColor( VisualItem arg1 )
    {
        if ( ( (ClassNode) arg1.get( "node" ) ).isInterface() )
            return Color.YELLOW.getRGB();
        else
            return arg1.getFillColor();
    }

    public ClassesUMLRenderer( EnumSet<AccessModifier> methodAccessModifiers,
                               EnumSet<AccessModifier> fieldAccessModifiers )
    {
        super();
        this.methodAccessModifiers = methodAccessModifiers;
        this.fieldAccessModifiers = fieldAccessModifiers;
    }

    @SuppressWarnings( "unchecked" )
    protected List<RendererListItem> getList( VisualItem vi )
    {
        List<RendererListItem> list;
        // = (List<RendererListItem>) vi.get( "vcache" );
        // if ( list == null )
        {
            list = new LinkedList<RendererListItem>();

            ClassNode node = (ClassNode) vi.get( "node" );
            UmlClassRendererConfigurationItem name_config =
                (UmlClassRendererConfigurationItem) getConfiguration().getRenderConfigurationItems().get(
                                                                                                          UmlClassRendererConfiguration.ATT_NAME );
            if ( name_config.isVisible() )
            {
                list.add( new TextItem( name_config.getFull_class_names() ? node.getFull_name() : node.getSimpleName(),
                                        true, false, true ) );
            }
            list.add( new SeparatorItem() );

            for ( FieldModel field : node.getFields() )
            {
                if ( AccessModifier.hasOneOfSuchModifiers( fieldAccessModifiers, field.getModifiers() ) )
                    list.add( new TextItem( field.toUMLString( true ), false,
                                            field.getModifiers().contains( ModifierModel.STATIC ), false ) );
            }
            for ( FieldModel field : node.getProperties() )
            {
                // if ( AccessModifier.hasOneOfSuchModifiers( fieldAccessModifiers, field.getModifiers() ) )
                if ( getConfiguration().isVisible( UmlClassRendererConfiguration.ATT_PROPERTIES ) )
                    list.add( new TextItem( field.toUMLString( true ) + " <<property>>", false,
                                            field.getModifiers().contains( ModifierModel.STATIC ), false ) );
            }
            list.add( new SeparatorItem() );

            for ( MethodModel method : node.getMethods() )
            {
                if ( AccessModifier.hasOneOfSuchModifiers( methodAccessModifiers, method.getModifiers() ) )
                    list.add( new TextItem( method.toUMLString( true ), false,
                                            method.getModifiers().contains( ModifierModel.STATIC ), false ) );
            }
            // vi.set( "vcache", list );
        }

        return list;
    }

    @Override
    public void setConfiguration( RendererConfiguration newRendererConfiguration )
    {
        super.setConfiguration( newRendererConfiguration );
        methodAccessModifiers = EnumSet.noneOf( AccessModifier.class );
        fieldAccessModifiers = EnumSet.noneOf( AccessModifier.class );
        RendererConfiguration ucrm = (RendererConfiguration) newRendererConfiguration;

        if ( ucrm.isVisible( UmlClassRendererConfiguration.ATT_PRIVATE_FIELDS ) )
            fieldAccessModifiers.add( AccessModifier.PRIVATE );
        if ( ucrm.isVisible( UmlClassRendererConfiguration.ATT_PUBLIC_FIELDS ) )
            fieldAccessModifiers.add( AccessModifier.PUBLIC );
        if ( ucrm.isVisible( UmlClassRendererConfiguration.ATT_PROTECTED_FIELDS ) )
            fieldAccessModifiers.add( AccessModifier.PROTECTED );

        if ( ucrm.isVisible( UmlClassRendererConfiguration.ATT_PRIVATE_METHODS ) )
            methodAccessModifiers.add( AccessModifier.PRIVATE );
        if ( ucrm.isVisible( UmlClassRendererConfiguration.ATT_PUBLIC_METHODS ) )
            methodAccessModifiers.add( AccessModifier.PUBLIC );
        if ( ucrm.isVisible( UmlClassRendererConfiguration.ATT_PROTECTED_METHODS ) )
            methodAccessModifiers.add( AccessModifier.PROTECTED );

    }
}
