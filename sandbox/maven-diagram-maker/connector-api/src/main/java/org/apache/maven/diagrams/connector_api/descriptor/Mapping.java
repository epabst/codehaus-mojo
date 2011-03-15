package org.apache.maven.diagrams.connector_api.descriptor;

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

/**
 * This class represents hint to the xstream library during the serialization/deserialization project.
 * 
 * It binds class with tagName. It also (optionally) can bind it to the converter class.
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class Mapping
{

    private Class<?> class_;

    private String tagName;

    /** How to convert the tag content into the clazz instance */
    private Class<?> converter;

    public Mapping( Class<?> clazz, String tagName )
    {
        super();
        this.class_ = clazz;
        this.tagName = tagName;
    }

    public Mapping()
    {
        super();
    }

    public Class<?> getClazz()
    {
        return class_;
    }

    public void setClass_( Class<?> clazz )
    {
        this.class_ = clazz;
    }

    public String getTagName()
    {
        return tagName;
    }

    public void setTagName( String tagName )
    {
        this.tagName = tagName;
    }

    public Class<?> getConverter()
    {
        return converter;
    }

    public void setConverter( Class<?> converter )
    {
        this.converter = converter;
    }

    public void setClassName( String className ) throws ClassNotFoundException
    {
        class_ = Class.forName( className );
    }

    public void setConverterClassName( String className ) throws ClassNotFoundException
    {
        converter = Class.forName( className );
    }

}
