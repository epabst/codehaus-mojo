package org.codehaus.mojo.setup.settings;

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

import org.codehaus.mojo.setup.SetupManager;

/**
 * Configure a settings.xml file in the User homedirectory according to <a
 * href="http://maven.apache.org/settings.html">Settings Reference</a>
 * 
 * <p>
 * Default template filename: <code>settings.xml</code><br/>
 * Properties filename: <code>settings.properties</code><br/>
 * Requires: <code>Maven 3.0-alpha-1</code>
 * </p>
 * 
 * @goal user-settings
 * @author Robert Scholte
 * @since 1.0.0
 */
public class UserSettingsSetupMojo
    extends AbstractSettingsSetupMojo
{
    /**
     * @component role-hint="user-settings"
     */
    private SetupManager fileProcessor;
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected SetupManager getSetupManager()
    {
        return fileProcessor;
    }
}
