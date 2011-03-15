package org.codehaus.mojo.hibernate3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * "hbm2java" is a java code generator. Options for controlling wether JDK 5 syntax can be used and wether the POJO
 * should be annotated with EJB3/Hibernate Annotations.
 *
 * @goal hbm2java
 * @execute phase="process-resources"
 * @requiresDependencyResolution
 */
public final class Hbm2JavaExporterMojo
    extends AbstractHibernateToolMojo
{
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface HibernateMojo ---------------------

    /**
     * @see org.codehaus.mojo.hibernate3.HibernateMojo#getGoalName()
     */
    public String getGoalName()
    {
        return "hbm2java";
    }
}
