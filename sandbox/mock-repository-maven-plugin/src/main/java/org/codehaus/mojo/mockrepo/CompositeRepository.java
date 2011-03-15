package org.codehaus.mojo.mockrepo;

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

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A composite repository.
 */
public class CompositeRepository
    implements Repository
{
    private final Repository[] repositories;

    private final Log log;

    public CompositeRepository( Repository[] repositories, Log log )
    {
        this.repositories = repositories;
        this.log = log;
    }

    public Metadata getMetadata( String path )
    {
        Metadata result = null;
        for ( int i = 0; i < repositories.length; i++ )
        {
            Metadata metadata = repositories[i].getMetadata( path );
            if ( metadata == null )
            {
                continue;
            }
            if ( result == null )
            {
                result = new Metadata();
            }
            if ( metadata.getGroupId() != null && metadata.getArtifactId() != null )
            {
                result.setGroupId( metadata.getGroupId() );
                result.setArtifactId( metadata.getArtifactId() );
            }
            result.merge( metadata );
        }
        return result;
    }

    public Content getContent( String path )
    {
        for ( int i = 0; i < repositories.length; i++ )
        {
            log.debug( "Checking repository " + repositories[i] + " for path " + path );
            Content content = repositories[i].getContent( path );
            if ( content != null )
            {
                return content;
            }
        }
        return null;
    }

    public List getChildPaths( String path )
    {
        log.debug( "Listing child paths of " + path );
        Set result = new TreeSet();
        for ( int i = 0; i < repositories.length; i++ )
        {
            result.addAll( repositories[i].getChildPaths( path ) );
        }
        return Collections.unmodifiableList( new ArrayList( result ) );
    }
}
