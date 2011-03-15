package org.codehaus.mojo.cis.model;

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
 * An instance of this class is used to configure a particular CIS webapp artifact. By default, the artifact is derived
 * from the cis jar file.
 */
public class Artifact
{
    private String groupId, artifactId, version, classifier, type, scope;

    /**
     * Returns the CIS webapps groupId.
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * Sets the CIS webapps groupId.
     */
    public void setGroupId( String pGroupId )
    {
        groupId = pGroupId;
    }

    /**
     * Returns the CIS webapps artifactId.
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * Sets the CIS webapps artifactId.
     */
    public void setArtifactId( String pArtifactId )
    {
        artifactId = pArtifactId;
    }

    /**
     * Returns the CIS webapps version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the CIS webapps version.
     */
    public void setVersion( String pVersion )
    {
        version = pVersion;
    }

    /**
     * Returns the CIS webapps classifier. Defaults to none.
     */
    public String getClassifier()
    {
        return classifier;
    }

    /**
     * Sets the CIS webapps classifier. Defaults to none.
     */
    public void setClassifier( String pClassifier )
    {
        classifier = pClassifier;
    }

    /**
     * Returns the CIS webapps type. Defaults to "war".
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the CIS webapps type. Defaults to "war".
     */
    public void setType( String pType )
    {
        type = pType;
    }

    /**
     * Returns the CIS webapps scope. Defaults to "compile".
     */
    public String getScope()
    {
        return scope;
    }

    /**
     * Sets the CIS webapps scope. Defaults to "compile".
     */
    public void setScope( String pScope )
    {
        scope = pScope;
    }
}
