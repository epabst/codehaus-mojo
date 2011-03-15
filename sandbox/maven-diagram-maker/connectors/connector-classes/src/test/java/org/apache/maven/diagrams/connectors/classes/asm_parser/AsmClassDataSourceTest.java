package org.apache.maven.diagrams.connectors.classes.asm_parser;

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
import java.io.IOException;
import java.util.EnumSet;

import junit.framework.TestCase;

import org.apache.maven.diagrams.connectors.classes.ClassDataSource;
import org.apache.maven.diagrams.connectors.classes.ClassDataSourceException;
import org.apache.maven.diagrams.connectors.classes.model.ClassModel;
import org.apache.maven.diagrams.connectors.classes.model.ModifierModel;
import org.apache.maven.diagrams.connectors.classes.test_classes.Color;
import org.apache.maven.diagrams.connectors.classes.test_classes.Triangle;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class AsmClassDataSourceTest extends TestCase
{
    public void testColor() throws IOException, ClassDataSourceException
    {
        ClassDataSource ds = new AsmClassDataSource();
        ClassModel classModel = ds.translateToClassModel( Color.class );
        assertEquals( Color.class.getName(), classModel.getClassifiedName() );
        assertEquals( Color.class.getSuperclass().getName(), classModel.getSuperClassName() );

        /* Interfaces */

        assertEquals( 0, classModel.getInterfaces().size() );

        /* Fields */

        assertEquals( 3, classModel.getFields().size() );

        assertEquals( "r", classModel.getFields().get( 0 ).getName() );
        assertEquals( "int", classModel.getFields().get( 0 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 0 ).getModifiers() );

        assertEquals( "g", classModel.getFields().get( 1 ).getName() );
        assertEquals( "int", classModel.getFields().get( 1 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 1 ).getModifiers() );

        assertEquals( "b", classModel.getFields().get( 2 ).getName() );
        assertEquals( "int", classModel.getFields().get( 2 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 2 ).getModifiers() );

        /* Methods */
        assertEquals( 4, classModel.getMethods().size() );

        assertEquals( "Color", classModel.getMethods().get( 0 ).getName() );
        assertEquals( "void", classModel.getMethods().get( 0 ).getType() );
        assertEquals( 3, classModel.getMethods().get( 0 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 0 ).getModifiers() );

        assertEquals( "getRed", classModel.getMethods().get( 1 ).getName() );
        assertEquals( "int", classModel.getMethods().get( 1 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 1 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 1 ).getModifiers() );

        assertEquals( "getGreen", classModel.getMethods().get( 2 ).getName() );
        assertEquals( "int", classModel.getMethods().get( 2 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 2 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 2 ).getModifiers() );

        assertEquals( "getBlue", classModel.getMethods().get( 3 ).getName() );
        assertEquals( "int", classModel.getMethods().get( 3 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 3 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 3 ).getModifiers() );
    }

    public void testShape() throws IOException, ClassDataSourceException
    {
        ClassDataSource ds = new AsmClassDataSource();
        ClassModel classModel =
            ds.translateToClassModel( "org.apache.maven.diagrams.connectors.classes.test_classes.Shape" );
        /* Interfaces */

        assertEquals( 0, classModel.getInterfaces().size() );

        /* Fields */

        assertEquals( 1, classModel.getFields().size() );

        assertEquals( "color", classModel.getFields().get( 0 ).getName() );
        assertEquals( "org.apache.maven.diagrams.connectors.classes.test_classes.Color",
                      classModel.getFields().get( 0 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 0 ).getModifiers() );

        /* Methods */
        assertEquals( 4, classModel.getMethods().size() );

        assertEquals( "Shape", classModel.getMethods().get( 0 ).getName() );
        assertEquals( "void", classModel.getMethods().get( 0 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 0 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 0 ).getModifiers() );

        assertEquals( "countAreaSize", classModel.getMethods().get( 1 ).getName() );
        assertEquals( "double", classModel.getMethods().get( 1 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 1 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 1 ).getModifiers() );

        assertEquals( "getColor", classModel.getMethods().get( 2 ).getName() );
        assertEquals( "org.apache.maven.diagrams.connectors.classes.test_classes.Color",
                      classModel.getMethods().get( 2 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 2 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 2 ).getModifiers() );

        assertEquals( "setColor", classModel.getMethods().get( 3 ).getName() );
        assertEquals( "void", classModel.getMethods().get( 3 ).getType() );
        assertEquals( 1, classModel.getMethods().get( 3 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 3 ).getModifiers() );

    }

    public void testTriangle() throws IOException, ClassDataSourceException
    {
        ClassDataSource ds = new AsmClassDataSource();
        ClassModel classModel = ds.translateToClassModel( Triangle.class );
        assertEquals( 0, classModel.getInterfaces().size() );

        /* Fields */

        assertEquals( 3, classModel.getFields().size() );

        assertEquals( "a", classModel.getFields().get( 0 ).getName() );
        assertEquals( "java.lang.Integer", classModel.getFields().get( 0 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 0 ).getModifiers() );

        assertEquals( "b", classModel.getFields().get( 1 ).getName() );
        assertEquals( "java.lang.Integer", classModel.getFields().get( 1 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 1 ).getModifiers() );

        assertEquals( "c", classModel.getFields().get( 2 ).getName() );
        assertEquals( "java.lang.Integer", classModel.getFields().get( 2 ).getType() );
        assertEquals( EnumSet.of( ModifierModel.PRIVATE ), classModel.getFields().get( 2 ).getModifiers() );

        /* Methods */
        assertEquals( 5, classModel.getMethods().size() );

        assertEquals( "Triangle", classModel.getMethods().get( 0 ).getName() );
        assertEquals( "void", classModel.getMethods().get( 0 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 0 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 0 ).getModifiers() );

        assertEquals( "Triangle", classModel.getMethods().get( 1 ).getName() );
        assertEquals( "void", classModel.getMethods().get( 1 ).getType() );
        assertEquals( 1, classModel.getMethods().get( 1 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 1 ).getModifiers() );

        assertEquals( "Triangle", classModel.getMethods().get( 2 ).getName() );
        assertEquals( "void", classModel.getMethods().get( 2 ).getType() );
        assertEquals( 3, classModel.getMethods().get( 2 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 2 ).getModifiers() );

        assertEquals( "countAreaSize", classModel.getMethods().get( 3 ).getName() );
        assertEquals( "double", classModel.getMethods().get( 3 ).getType() );
        assertEquals( 0, classModel.getMethods().get( 3 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.PUBLIC ), classModel.getMethods().get( 3 ).getModifiers() );

        assertEquals( "heron", classModel.getMethods().get( 4 ).getName() );
        assertEquals( "double", classModel.getMethods().get( 4 ).getType() );
        assertEquals( 3, classModel.getMethods().get( 4 ).getParams().size() );
        assertEquals( EnumSet.of( ModifierModel.STATIC, ModifierModel.PROTECTED ),
                      classModel.getMethods().get( 4 ).getModifiers() );
        ;

    }
}
