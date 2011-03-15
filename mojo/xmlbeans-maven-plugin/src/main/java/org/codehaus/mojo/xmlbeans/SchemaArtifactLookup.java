package org.codehaus.mojo.xmlbeans;

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

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;

public class SchemaArtifactLookup
{

    private Log logger;

    private Map artifacts;

    public SchemaArtifactLookup( Map projectArtifacts, Log log )
    {
        artifacts = projectArtifacts;
        logger = log;

    }

    /**
     * Finds an artifact in the list of project artifacts and
     * returns a casted version of it with extra helper methods.
     *
     * @param string
     * @return
     */
    public Artifact find( String string ) throws XmlBeansException
    {
        Artifact result = null;

        if ( artifacts.containsKey( string ) )
        {
            result = ( Artifact ) artifacts.get( string );
        }
        else
        {
            throw new XmlBeansException( XmlBeansException.INVALID_ARTIFACT_REFERENCE, string );
        }

        return result;
    }

    private class ArtifactReference
    {
        private String groupId;
        private String artifactId;

        private ArtifactReference( String path ) throws XmlBeansException
        {
            StringTokenizer tokens = new StringTokenizer( path, ":" );
            if ( tokens.countTokens() == 2 )
            {
                groupId = tokens.nextToken();
                artifactId = tokens.nextToken();
            }
            else
            {
                throw new XmlBeansException( XmlBeansException.INVALID_ARTIFACT_REFERENCE, path );
            }
        }

        public boolean equals( Object candidate )
        {
            boolean outcome = false;
            if ( candidate instanceof Artifact )
            {
                Artifact artifact = ( Artifact ) candidate;
                outcome = groupId.equals( artifact.getGroupId() ) && artifactId.equals( artifact.getArtifactId() );
            }
            return outcome;
        }


        public int hashCode()
        {
            final int multiplier = 31;
            int result;
            result = ( groupId != null ? groupId.hashCode() : 0 );
            result = multiplier * result + ( artifactId != null ? artifactId.hashCode() : 0 );
            return result;
        }
    }
}
