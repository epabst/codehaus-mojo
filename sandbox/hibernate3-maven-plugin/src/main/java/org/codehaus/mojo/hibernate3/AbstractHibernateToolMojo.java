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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.hibernate3.util.PlexusConfigurationUtils;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * Abstract class for any implementing "hibernatetool" goal.
 */
public abstract class AbstractHibernateToolMojo
    extends AbstractHibernateMojo
{
// ------------------------------ FIELDS ------------------------------

    /**
     * The "hibernatetool" element containing the plugin configuration.
     *
     * @parameter expression="${hibernatetool}"
     * @required
     */
    protected PlexusConfiguration hibernatetool;

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface HibernateMojo ---------------------

    /**
     * @see HibernateMojo#getConfiguration()
     */
    public PlexusConfiguration getConfiguration()
        throws MojoExecutionException
    {
        try
        {
            // if it isn't the run goal, let's check that the goal exists
            if ( !"run".equals( getGoalName() ) )
            {
                hibernatetool.getChild( getGoalName() );
            }
            return PlexusConfigurationUtils.parseHibernateTool( hibernatetool, getGoalName(), getAntClassLoader(),
                                                                session );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new MojoExecutionException( "There was an error parsing the configuration", e );
        }
    }
}
