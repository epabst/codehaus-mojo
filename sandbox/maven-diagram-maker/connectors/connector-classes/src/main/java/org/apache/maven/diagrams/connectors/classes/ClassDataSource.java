package org.apache.maven.diagrams.connectors.classes;

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
import java.io.InputStream;

import org.apache.maven.diagrams.connectors.classes.model.ClassModel;

/**
 * Interface for all datasources (sources of informations about single class). There could be many different
 * implementation (reflection, asm, javassist) of such a datasource
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public interface ClassDataSource
{

    /**
     * Gets the information from given class object
     * 
     * @param c -
     *            class to get information about it
     * @return
     * @throws ClassDataSourceException
     */
    @SuppressWarnings( "unchecked" )
    public abstract ClassModel translateToClassModel( Class c ) throws ClassDataSourceException;

    /**
     * Gets the information from given inputstream of class's bytecode.
     * 
     * @param is
     * @return
     * @throws ClassDataSourceException
     */
    public abstract ClassModel translateToClassModel( InputStream is ) throws ClassDataSourceException;

    /**
     * Gets the information about given class name (full "dot" qualified name)
     * 
     * @param className
     * @return
     * @throws ClassDataSourceException
     */
    public abstract ClassModel translateToClassModel( String className ) throws ClassDataSourceException;

    /**
     * Gets the information about given class name (full "dot" qualified name), using given classloader
     * 
     * @param className
     * @return
     * @throws ClassDataSourceException
     */
    public abstract ClassModel translateToClassModel( ClassLoader classLoader, String className )
        throws ClassDataSourceException;

}