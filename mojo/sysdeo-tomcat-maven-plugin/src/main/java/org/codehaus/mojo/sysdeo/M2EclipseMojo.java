package org.codehaus.mojo.sysdeo;

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

import org.codehaus.mojo.sysdeo.ide.ReadWorkspaceLocations;

/**
 * Custom sysdeo-tomcat plugin configuration generation for m2eclipse environment
 * 
 * @goal m2eclipse
 */
public class M2EclipseMojo
    extends SysdeoMojo
{

    protected boolean setup()
    {
        setUseClasspathVariable( false );
        return super.setup();
    }

    protected String getOutputDirectory()
    {
        // TODO can the m2eclipse pass info about use of target-eclipse ?
        String output = ReadWorkspaceLocations.getOutputDirectory( getProject().getBasedir() );
        return output != null ? output : super.getOutputDirectory();
    }
}
