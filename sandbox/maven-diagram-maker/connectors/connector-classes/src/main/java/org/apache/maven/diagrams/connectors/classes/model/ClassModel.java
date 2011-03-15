package org.apache.maven.diagrams.connectors.classes.model;

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
import java.util.List;

/**
 * The class represents all information available in single class file for class diagram
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class ClassModel
{
    /**
     * Full class name with dots as package separators
     */
    private String classifiedName;

    private List<MethodModel> methods;

    private List<FieldModel> fields;

    private String superClassName;

    private List<String> interfaces;

    /**
     * Is the class interface or ordinal "class"
     */
    private Boolean interf;

    public String getClassifiedName()
    {
        return classifiedName;
    }

    public void setClassifiedName( String classifiedName )
    {
        this.classifiedName = classifiedName;
    }

    public List<MethodModel> getMethods()
    {
        return methods;
    }

    public void setMethods( List<MethodModel> methods )
    {
        this.methods = methods;
    }

    public List<FieldModel> getFields()
    {
        return fields;
    }

    public void setFields( List<FieldModel> fields )
    {
        this.fields = fields;
    }

    public String getSuperClassName()
    {
        return superClassName;
    }

    public void setSuperClassName( String superClassName )
    {
        this.superClassName = superClassName;
    }

    public List<String> getInterfaces()
    {
        return interfaces;
    }

    public void setInterfaces( List<String> interfaces )
    {
        this.interfaces = interfaces;
    }

    public Boolean isInterface()
    {
        return interf;
    }

    public void setInterface( Boolean interf )
    {
        this.interf = interf;
    }
}
