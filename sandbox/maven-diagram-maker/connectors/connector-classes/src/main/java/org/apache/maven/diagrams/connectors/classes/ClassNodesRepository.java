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
import java.util.Map;

import org.apache.maven.diagrams.connectors.classes.graph.ClassNode;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public interface ClassNodesRepository
{
    public static String ROLE = ClassNodesRepository.class.getName();

    /**
     * The method checks if the classNode for given className ((fully qualified, dot separated) already exists (and
     * returns it). If not - the method calculates it and stores in the cache the result.
     */
    public abstract ClassNode getClassNode( String className ) throws ClassDataSourceException;

    /**
     * Returns the current state of the cache (as a map from ClassName to ClassNode)
     * 
     * @return
     */
    public abstract Map<String, ClassNode> getMap();

}