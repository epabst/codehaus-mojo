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
import java.io.InputStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.diagrams.connectors.classes.ClassDataSource;
import org.apache.maven.diagrams.connectors.classes.ClassDataSourceException;
import org.apache.maven.diagrams.connectors.classes.model.ClassModel;
import org.apache.maven.diagrams.connectors.classes.model.FieldModel;
import org.apache.maven.diagrams.connectors.classes.model.MethodModel;
import org.apache.maven.diagrams.connectors.classes.model.ModifierModel;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Implementation ClassDataSource that uses Asm library to parse the files and to find interclass dependencies
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class AsmClassDataSource implements ClassDataSource
{

    @SuppressWarnings( "unchecked" )
    public ClassModel translateToClassModel( Class c ) throws ClassDataSourceException
    {
        ClassReader classReader;
        try
        {
            classReader =
                new ClassReader( AsmClassDataSource.class.getResourceAsStream( "/" + c.getName().replace( ".", "/" )
                                + ".class" ) );
        }
        catch ( IOException e )
        {
            throw new ClassDataSourceException( "Cannot instante class reader for:" + c.getName(), e );
        }
        ClassNode classNode = new ClassNode();
        classReader.accept( classNode, 0 );
        return translateClassNodeToClassModel( classNode );
    }

    public ClassModel translateToClassModel( InputStream is ) throws ClassDataSourceException
    {
        try
        {
            ClassReader classReader = new ClassReader( is );
            ClassNode classNode = new ClassNode();
            classReader.accept( classNode, 0 );
            return translateClassNodeToClassModel( classNode );
        }
        catch ( IOException e )
        {
            throw new ClassDataSourceException( "Cannot instante class reader for given inputStream.", e );
        }

    }

    public ClassModel translateToClassModel( String className ) throws ClassDataSourceException
    {
        try
        {
            ClassReader classReader =
                new ClassReader( AsmClassDataSource.class.getResourceAsStream( "/" + className.replace( ".", "/" )
                                + ".class" ) );
            ClassNode classNode = new ClassNode();
            classReader.accept( classNode, 0 );
            return translateClassNodeToClassModel( classNode );
        }
        catch ( IOException e )
        {
            throw new ClassDataSourceException( "Cannot instante class reader for:" + className, e );
        }

    }

    public ClassModel translateToClassModel( ClassLoader classLoader, String className )
        throws ClassDataSourceException
    {
        try
        {
            String res = className.replace( ".", "/" ) + ".class";
            InputStream is = classLoader.getResourceAsStream( res );
            if ( is == null )
                throw new ClassDataSourceException( "Cannot find resource :" + res );
            ClassReader classReader = new ClassReader( is );
            ClassNode classNode = new ClassNode();
            classReader.accept( classNode, 0 );
            return translateClassNodeToClassModel( classNode );
        }
        catch ( IOException e )
        {
            throw new ClassDataSourceException( "Cannot instante class reader for:" + className, e );
        }
    }

    /* ==================================================================== */

    /**
     * ClassNode is internal (asm's) model. This function translates all available information form the classNode into
     * classModel.
     */
    private ClassModel translateClassNodeToClassModel( ClassNode classNode ) throws ClassDataSourceException
    {
        ClassModel classModel = new ClassModel();
        classModel.setClassifiedName( classifiedNameToDotName( classNode.name ) );
        if ( classNode.superName != null )
            classModel.setSuperClassName( classifiedNameToDotName( classNode.superName ) );
        else
            classModel.setSuperClassName( null );
        classModel.setFields( translateFields( classNode ) );
        classModel.setMethods( translateMethods( classNode ) );
        classModel.setInterfaces( translateInterfaces( classNode ) );
        classModel.setInterface( ( classNode.access & Opcodes.ACC_INTERFACE ) > 0 );
        return classModel;
    }

    /**
     * The method translates informations about interfaces from classNode into list of qualified interface names (dots
     * as separator)
     * 
     * 
     * @param classNode -
     *            source asm's classNode
     * @return list of qualified interface names.
     */
    @SuppressWarnings( "unchecked" )
    private List<String> translateInterfaces( ClassNode classNode )
    {
        List<String> result = new LinkedList<String>();
        for ( String _interface : (List<String>) classNode.interfaces )
        {
            result.add( classifiedNameToDotName( _interface ) );
        }
        return result;
    }

    /**
     * The method get's all informations about methods from classNode and builds list of MethodModel objects.
     * 
     * @param classNode
     * @return list of MethodModel objects (about all methods in classNode class)
     * 
     * @throws ClassDataSourceException
     */
    @SuppressWarnings( "unchecked" )
    private List<MethodModel> translateMethods( ClassNode classNode ) throws ClassDataSourceException
    {
        List<MethodModel> result = new LinkedList<MethodModel>();
        for ( MethodNode method : (List<MethodNode>) classNode.methods )
        {
            try
            {
                result.add( translateMethodNode( method, simpleClassName( classNode.name ) ) );
            }
            catch ( ParseException e )
            {
                throw new ClassDataSourceException( "Cannot translate MethodNode to MethodModel", e );
            }
        }
        return result;
    }

    /**
     * Translates methodNode (asm's) into MethodModel objects. Translates also &lt;init&gt; methods into constructors
     * (using simpleClassName).
     * 
     * @param simpleClassName -
     *            not qualified name of class that constains the methodNode method
     */
    private MethodModel translateMethodNode( MethodNode method, String simpleClassName ) throws ParseException
    {
        MethodModel methodModel = new MethodModel();
        methodModel.setName( method.name.equals( "<init>" ) ? simpleClassName : method.name );
        methodModel.setModifiers( ModifierModel.accessContantsToModifiers( method.access ) );
        DescriptionParser descriptionParser = new DescriptionParser( method.desc );
        methodModel.setParams( descriptionParser.readParamsList() );
        methodModel.setType( descriptionParser.readType() );
        return methodModel;
    }

    /**
     * The method get's all informations about fields from classNode and builds list of FieldModel objects.
     * 
     * @param classNode
     * @return list of FieldModel objects (about all fields in classNode class)
     * 
     * @throws ClassDataSourceException
     */
    @SuppressWarnings( "unchecked" )
    private List<FieldModel> translateFields( ClassNode classNode ) throws ClassDataSourceException
    {
        List<FieldModel> result = new LinkedList<FieldModel>();
        for ( FieldNode field : (List<FieldNode>) classNode.fields )
        {
            try
            {
                result.add( translateFieldNode( field ) );
            }
            catch ( ParseException e )
            {
                throw new ClassDataSourceException( "Cannot translate MethodNode to MethodModel", e );
            }
        }
        return result;
    }

    /**
     * Translates single FieldNode (asm's) into FieldModel
     * 
     * @param field
     * @return
     * @throws ParseException
     */
    private FieldModel translateFieldNode( FieldNode field ) throws ParseException
    {
        FieldModel fieldModel = new FieldModel();
        fieldModel.setName( field.name );
        fieldModel.setModifiers( ModifierModel.accessContantsToModifiers( field.access ) );
        DescriptionParser descriptionParser = new DescriptionParser( field.desc );
        fieldModel.setType( descriptionParser.readType() );
        return fieldModel;
    }

    /**
     * Translates the "/" notation of class name into "." notation. For example java/lang/String is transalted into
     * java.lang.String
     * 
     * @param nam
     *            to be translated
     * @return
     */
    protected String classifiedNameToDotName( String name )
    {
        return name.replace( '/', '.' );
    }

    /**
     * Translates qualified (by slashes) class name into simpleClassName
     * 
     * @param name
     * @return
     */
    protected String simpleClassName( String name )
    {
        int last = name.lastIndexOf( '/' ) + 1;
        return name.substring( last );
    }
}
