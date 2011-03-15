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
 * "hbm2cfgxml" generates a hibernate.cfg.xml. Intended to be used together with a "jdbcconfiguration" when performing
 * reverse engineering, but can be used with any kind of configuration. The "hbm2cfgxml" will contain the properties
 * used and adds mapping entries for each mapped class.
 *
 * @goal hbm2cfgxml
 * @execute phase="compile"
 * @requiresDependencyResolution
 */
public final class Hbm2CfgXmlExporterMojo
    extends AbstractHibernateToolMojo
{
// -------------------------- OTHER METHODS --------------------------

    /**
     * @see org.codehaus.mojo.hibernate3.HibernateMojo#getGoalName()
     */
    public String getGoalName()
    {
        return "hbm2cfgxml";
    }
}
