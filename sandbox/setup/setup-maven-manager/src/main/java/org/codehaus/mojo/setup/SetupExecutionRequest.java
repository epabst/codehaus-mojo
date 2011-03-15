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
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;

/**
 * @author Robert Scholte
 * @since 1.0.0
 */
public interface SetupExecutionRequest
{
    /**
     * All types for merging:
     * <ul>
     *  <li>none: don't merge, just keep the old one</li>
     *  <li>expand: keep the values of existing elements, only add new ones</li>
     *  <li>update: overwrite existing values with the new one if available</li>
     *  <li>overwrite: remove all old entries and use the new one</li>
     * </li>
     * 
     * @author Robert Scholte
     * @since 1.0.0
     */
    public enum MergeType
    {
        NONE( "none" ), EXPAND( "expand" ), UPDATE( "update" ), OVERWRITE( "overwrite" );

        private final String value;

        MergeType( String value )
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    /**
     * Get the Maven Session
     * @return the Maven Session
     */
    MavenSession getSession();

    /**
     * Set the Maven Session 
     * 
     * @param session
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setSession( MavenSession session );

    /**
     * Get the template File
     * 
     * @return
     */
    File getTemplateFile();

    /**
     * Set the template file
     * 
     * @param templateFile
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setTemplateFile( File templateFile );

    /**
     * 
     * @return
     */
    List < String > getPropertyFilenames();

    /**
     * 
     * @param propertyFilenames
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setPropertyFilenames( List < String > propertyFilenames );

    Properties getAdditionalProperties();

    /**
     * 
     * @param properties
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setAdditionalProperties( Properties properties );

    /**
     * 
     * @param type
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setMergeType( MergeType type );

    MergeType getMergeType();

    /**
     * 
     * @param encoding
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setEncoding( String encoding );

    String getEncoding();
    
    /**
     * 
     * @param dryRun
     * @return the SetupExecutionRequest itself
     */
    SetupExecutionRequest setDryRun( boolean dryRun );
    
    boolean isDryRun();
    
}