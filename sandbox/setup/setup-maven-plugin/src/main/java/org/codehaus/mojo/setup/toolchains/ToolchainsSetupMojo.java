package org.codehaus.mojo.setup.toolchains;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.mojo.setup.AbstractSetupMojo;
import org.codehaus.mojo.setup.SetupManager;

/**
 * Configure a toolchains.xml according to 
 * <a href="http://maven.apache.org/guides/mini/guide-using-toolchains.html">miniguide using toolchains</a>
 * 
 * <p>
 * Default template filename: <code>toolchains.xml</code><br/>
 * Properties filename: <code>toolchains.properties</code><br/>
 * Requires: <code>Maven 3.0-alpha-1</code>
 * </p>
 * 
 * @goal toolchains
 * @author Robert Scholte
 * @since 1.0.0
 */
public class ToolchainsSetupMojo
    extends AbstractSetupMojo
{

    /**
     * @component role-hint="toolchains"
     */
    private SetupManager fileProcessor;

    protected static final String DEFAULT_TEMPLATE_FILENAME = "toolchains.xml";

    protected static final String PROPERTIES_FILENAME = "toolchains.properties";

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected VersionRange getMavenVersionRange() throws InvalidVersionSpecificationException
    {
        return VersionRange.createFromVersionSpec( "[3.0,)" );
    } 

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected String getPropertiesFilename()
    {
        return PROPERTIES_FILENAME;
    }

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected String getDefaultTemplateFilename()
    {
        return DEFAULT_TEMPLATE_FILENAME;
    }
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    public SetupManager getSetupManager()
    {
        return fileProcessor;
    }
}
