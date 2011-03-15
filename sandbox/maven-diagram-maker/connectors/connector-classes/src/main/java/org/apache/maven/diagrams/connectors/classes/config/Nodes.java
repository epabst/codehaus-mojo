package org.apache.maven.diagrams.connectors.classes.config;

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
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class Nodes
{
    /**
     * Should field,getter and optionally setter be transformed into single &lt;&lt;property&gt;&gt; scope.
     */
    private Boolean compressJavaBeanProperties = true;

    /**
     * Should be inherited (public and protected) methods added to the class's node.
     */
    private Boolean propagateInheritedMethods = false;

    /**
     * Should be inherited (public and protected) fields added to the class's node.
     */
    private Boolean propagateInheritedFields = false;

    /**
     * Should field,getter and optionally setter be transformed into single &lt;&lt;property&gt;&gt; scope.
     */
    public Boolean getCompressJavaBeanProperties()
    {
        return compressJavaBeanProperties;
    }

    /**
     * Should field,getter and optionally setter be transformed into single &lt;&lt;property&gt;&gt; scope.
     */
    public void setCompressJavaBeanProperties( Boolean compressJavaBeanProperties )
    {
        this.compressJavaBeanProperties = compressJavaBeanProperties;
    }

    /**
     * Should be inherited (public and protected) methods added to the class's node.
     */
    public Boolean getPropagateInheritedMethods()
    {
        return propagateInheritedMethods;
    }

    /**
     * Should be inherited (public and protected) methods added to the class's node.
     */
    public void setPropagateInheritedMethods( Boolean propagateInheritedMethods )
    {
        this.propagateInheritedMethods = propagateInheritedMethods;
    }

    /**
     * Should be inherited (public and protected) fields added to the class's node.
     */
    public Boolean getPropagateInheritedFields()
    {
        return propagateInheritedFields;
    }

    /**
     * Should be inherited (public and protected) fields added to the class's node.
     */
    public void setPropagateInheritedFields( Boolean propagateInheritedFields )
    {
        this.propagateInheritedFields = propagateInheritedFields;
    }

}
