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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.GroupRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A proxy repository that uses maven's repositories to resolve artifacts.
 */
public class ProxyRepository
    implements Repository
{
    private final RepositoryMetadataManager repositoryMetadataManager;

    private final List remoteArtifactRepositories;

    private final List remotePluginRepositories;

    private final ArtifactRepository localRepository;

    private final ArtifactFactory artifactFactory;

    private final List remoteRepositories;

    private final VersionRange anyVersion;

    private final ArtifactResolver artifactResolver;

    private final Log log;

    public ProxyRepository( RepositoryMetadataManager repositoryMetadataManager, List remoteArtifactRepositories,
                            List remotePluginRepositories, ArtifactRepository localRepository,
                            ArtifactFactory artifactFactory, Log log, ArtifactResolver artifactResolver )
        throws InvalidVersionSpecificationException
    {
        this.repositoryMetadataManager = repositoryMetadataManager;
        this.remoteArtifactRepositories = remoteArtifactRepositories;
        this.remotePluginRepositories = remotePluginRepositories;
        this.localRepository = localRepository;
        this.artifactFactory = artifactFactory;
        this.log = log;
        this.artifactResolver = artifactResolver;
        remoteRepositories = new ArrayList();
        remoteRepositories.addAll( remoteArtifactRepositories );
        remoteRepositories.addAll( remotePluginRepositories );
        anyVersion = VersionRange.createFromVersionSpec( "[0,]" );
    }

    public Metadata getMetadata( String path )
    {
        if ( StringUtils.isEmpty( path ) || path.length() < 13 )
        {
            return null;
        }
        String coordPath = StringUtils.chompLast( path, "/metadata.xml" );

        int index = coordPath.lastIndexOf( '/' );

        String artifactId = index == -1 ? null : coordPath.substring( index + 1 );
        String groupId = index == -1 ? null : coordPath.substring( 0, index );
        String pluginGroupId = coordPath.replace( '/', '.' );

        Metadata metadata = new Metadata();

        // is this path a groupId:artifactId pair?
        if ( !StringUtils.isEmpty( artifactId ) && !StringUtils.isEmpty( groupId ) )
        {
            final Artifact artifact =
                artifactFactory.createDependencyArtifact( groupId, artifactId, anyVersion, "pom", null, "compile" );
            final ArtifactRepositoryMetadata artifactRepositoryMetadata = new ArtifactRepositoryMetadata( artifact );
            try
            {
                repositoryMetadataManager.resolve( artifactRepositoryMetadata, remoteRepositories, localRepository );

                final Metadata artifactMetadata = artifactRepositoryMetadata.getMetadata();
                if ( artifactMetadata.getVersioning() != null )
                {
                    metadata.setGroupId( groupId );
                    metadata.setArtifactId( artifactId );
                    metadata.merge( artifactMetadata );
                }
            }
            catch ( RepositoryMetadataResolutionException e )
            {
                log.debug( e );
            }
        }

        // if this path a groupId on its own?
        final GroupRepositoryMetadata groupRepositoryMetadata = new GroupRepositoryMetadata( pluginGroupId );
        try
        {
            repositoryMetadataManager.resolve( groupRepositoryMetadata, remotePluginRepositories, localRepository );
            metadata.merge( groupRepositoryMetadata.getMetadata() );
        }
        catch ( RepositoryMetadataResolutionException e )
        {
            log.debug( e );
        }

        return metadata;
    }

    public Content getContent( String path )
    {
        // ok, decode the path... it must be formed like so
        // groupId.replace('.','/')+'/'+artifactId+'/'+version+'/'+artifactId+'-'+version
        // +(classifier!=null?'-'+classifier:"")+'.'+type

        String groupId;
        String artifactId;
        String version;
        String classifier;
        String type;

        int index1;
        int index2;
        int index3;

        index3 = path.lastIndexOf( '/' );
        if ( index3 == -1 || index3 + 1 >= path.length() )
        {
            // not a valid content path, so nothing at this path
            return null;
        }
        index2 = path.lastIndexOf( '/', index3 - 1 );
        if ( index2 == -1 )
        {
            // not a valid content path, so nothing at this path
            return null;
        }
        version = path.substring( index2 + 1, index3 );
        index1 = path.lastIndexOf( '/', index2 - 1 );
        if ( index1 == -1 )
        {
            // not a valid content path, so nothing at this path
            return null;
        }
        artifactId = path.substring( index1 + 1, index2 );
        groupId = path.substring( 0, index1 ).replace( '/', '.' );

        String name = path.substring( index3 + 1 );
        if ( !name.startsWith( artifactId + '-' + version ) )
        {
            // not a valid content path, so nothing at this path
            return null;
        }

        String classifierAndType = name.substring( artifactId.length() + version.length() + 1 );
        // the problem is that both the classifier and the type could legally contain a '.'
        // so we need to try all posibilities
        int index = -1;
        while ( -1 != ( index = classifierAndType.indexOf( '.', index + 1 ) ) )
        {
            if ( index == 0 )
            {
                classifier = null;
            }
            else
            {
                classifier = classifierAndType.substring( 0, index );
            }
            type = classifierAndType.substring( index + 1 );
            Artifact artifact =
                artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
            try
            {
                artifactResolver.resolve( artifact, remoteRepositories, localRepository );
                final File file = artifact.getFile();
                if ( file != null && file.isFile() )
                {
                    return new FileContent( file );
                }
            }
            catch ( ArtifactResolutionException e )
            {
                log.debug( e );
            }
            catch ( ArtifactNotFoundException e )
            {
                log.debug( e );
            }
        }

        return null;  // couldn't find the artifact
    }

    public List getChildPaths( String path )
    {
        return Collections.EMPTY_LIST;
    }
}
