package org.codehaus.mojo.dashboard.report.plugin.configuration;

/*
 * Copyright 2007 David Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

/**
 * Interface of Dashboard Configuration Service .
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public interface IConfigurationService
{
    public String getConfigFile() throws ConfigurationServiceException;

    public void setConfigFile( String configFile ) throws ConfigurationServiceException;

    public boolean isValidConfig() throws ConfigurationServiceException;

    public List getWarningMessages() throws ConfigurationServiceException;

    public Configuration getConfiguration() throws ConfigurationServiceException;

}
