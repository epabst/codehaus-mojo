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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.diagrams.connectors.classes.asm_parser.AsmClassDataSource;
import org.apache.maven.diagrams.connectors.classes.model.ClassModel;

/**
 * The class represent something like classLoader. You can initialize the object with the classPath and then ask it for
 * {@link ClassModel}s of any class contained on the classpath.
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class ClassModelsRepository
{
    private ClassLoader classLoader;

    private Map<String, ClassModel> classModelsMap;

    private ClassDataSource classDataSource;

    /**
     * 
     * @param classpath
     */
    public ClassModelsRepository( URL[] classpath )
    {
        classLoader = new URLClassLoader( classpath );
        classModelsMap = new HashMap<String, ClassModel>();
        classDataSource = new AsmClassDataSource();
    }

    /**
     * Returns classModel for given fully-dot-qualified className
     * 
     * @param className
     * @return
     * @throws ClassDataSourceException
     */
    public ClassModel getClassModel( String className ) throws ClassDataSourceException
    {
        ClassModel res = classModelsMap.get( className );
        if ( res == null )
        {
            res = classDataSource.translateToClassModel( classLoader, className );
            classModelsMap.put( className, res );
        }
        return res;
    }
}
