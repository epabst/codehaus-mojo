package org.codehaus.mojo.setup;

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

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;

/**
 * @author Robert Scholte
 * @since 1.0.0
 */
public class DefaultSetupExecutionRequest
    implements SetupExecutionRequest
{

    private MavenSession session;

    private File templateFile;

    private String encoding;
    
    private boolean dryRun;

    private List < String > propertyFilenames;

    private Properties additionalProperties;

    private MergeType mergeType;

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#getSession()
     */
    public MavenSession getSession()
    {
        return session;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#setSession(org.apache.maven.execution.MavenSession)
     */
    public SetupExecutionRequest setSession( MavenSession session )
    {
        this.session = session;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#getTemplateFile()
     */
    public File getTemplateFile()
    {
        return templateFile;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#setTemplateFile(java.io.File)
     */
    public SetupExecutionRequest setTemplateFile( File templateFile )
    {
        this.templateFile = templateFile;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#getPropertyFilenames()
     */
    public List < String > getPropertyFilenames()
    {
        return propertyFilenames ==  null ? Collections. < String > emptyList() : propertyFilenames;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#setPropertyFilenames(java.util.List)
     */
    public SetupExecutionRequest setPropertyFilenames( List < String > propertyFilenames )
    {
        this.propertyFilenames = propertyFilenames;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#getProperties()
     */
    public Properties getAdditionalProperties()
    {
        if ( additionalProperties == null )
        {
            additionalProperties = new Properties();
        }
        return additionalProperties;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.mojo.setup.SetupExecutionRequest#setProperties(java.util.Properties)
     */
    public SetupExecutionRequest setAdditionalProperties( Properties properties )
    {
        this.additionalProperties = properties;
        return this;
    }

    public MergeType getMergeType()
    {
        return mergeType;
    }

    public SetupExecutionRequest setMergeType( MergeType type )
    {
        this.mergeType = type;
        return this;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public SetupExecutionRequest setEncoding( String encoding )
    {
        this.encoding = encoding;
        return this;
    }
    
    public SetupExecutionRequest setDryRun( boolean dryRun )
    {
        this.dryRun = dryRun;
        return this;
    }
    
    public boolean isDryRun()
    {
        return dryRun;
    }
}
