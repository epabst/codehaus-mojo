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
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A repository that filters the content of another repository.
 */
public class FilteredRepository
    implements Repository
{

    private static final Pattern SNAPSHOT_PATTERN = Pattern.compile( "(-((\\d{8}\\.\\d{6})-(\\d+))|(SNAPSHOT))$" );

    private final ArtifactFactory artifactFactory;

    private final Repository delegate;

    /**
     * Artifact filter to determine if artifact should be included
     *
     * @since 1.0-alpha-3
     */
    private PatternIncludesArtifactFilter includesFilter;

    /**
     * Artifact filter to determine if artifact should be excluded
     *
     * @since 1.0-alpha-3
     */
    private PatternExcludesArtifactFilter excludesFilter;


    public FilteredRepository( Repository delegate, String[] includes, String[] excludes,
                               ArtifactFactory artifactFactory )
    {
        this.delegate = delegate;
        this.artifactFactory = artifactFactory;
        includesFilter = includes == null ? null : new PatternIncludesArtifactFilter( Arrays.asList( includes ) );
        excludesFilter = excludes == null ? null : new PatternExcludesArtifactFilter( Arrays.asList( excludes ) );
    }

    public Metadata getMetadata( String path )
    {
        Metadata metadata = delegate.getMetadata( path );
        if ( metadata == null )
        {
            return null;
        }
        Metadata result = new Metadata();
        if ( metadata.getGroupId() != null && metadata.getArtifactId() != null && metadata.getVersioning() != null )
        {
            Set versions = new TreeSet();
            Iterator i = metadata.getVersioning().getVersions().iterator();
            while ( i.hasNext() )
            {
                String version = (String) i.next();
                Artifact a =
                    artifactFactory.createArtifactWithClassifier( metadata.getGroupId(), metadata.getArtifactId(),
                                                                  version, "pom", null );
                if ( includesFilter != null && !includesFilter.include( a ) )
                {
                    continue;
                }
                if ( excludesFilter != null && !excludesFilter.include( a ) )
                {
                    continue;
                }
                versions.add( version );
            }
            if ( !versions.isEmpty() )
            {
                ArtifactVersion latestRelease = null;
                ArtifactVersion latest = null;
                result.setGroupId( metadata.getGroupId() );
                result.setArtifactId( metadata.getArtifactId() );
                result.setVersioning( new Versioning() );
                i = versions.iterator();
                while ( i.hasNext() )
                {
                    String version = (String) i.next();
                    result.getVersioning().addVersion( version );
                    ArtifactVersion v = new DefaultArtifactVersion( version );
                    boolean isSnapshot = SNAPSHOT_PATTERN.matcher( version ).matches();
                    if ( latest == null || latest.compareTo( v ) < 0 )
                    {
                        latest = v;
                    }
                    if ( !isSnapshot && ( latestRelease == null || latestRelease.compareTo( v ) < 0 ) )
                    {
                        latestRelease = v;
                    }
                }
                result.getVersioning().setLatest( latest == null ? null : latest.toString() );
                result.getVersioning().setRelease( latestRelease == null ? null : latestRelease.toString() );
                result.getVersioning().setLastUpdated( metadata.getVersioning().getLastUpdated() );
            }
        }

        if ( metadata.getPlugins() != null && !metadata.getPlugins().isEmpty() )
        {
            List plugins = new ArrayList();
            Iterator i = metadata.getPlugins().iterator();
            String groupId = path.replace( '/', '.' );
            while ( i.hasNext() )
            {
                Plugin plugin = (Plugin) i.next();
                Artifact a = new StubPluginArtifact( groupId, plugin );
                if ( includesFilter != null && !includesFilter.include( a ) )
                {
                    continue;
                }
                if ( excludesFilter != null && !excludesFilter.include( a ) )
                {
                    continue;
                }
                plugins.add( plugin );
            }

            if ( !plugins.isEmpty() )
            {
                result.setPlugins( plugins );
            }
        }

        return result;
    }

    public Content getContent( String path )
    {
        Content content = delegate.getContent( path );
        if ( content == null )
        {
            return null;
        }
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

        String classifierAndType = name.substring( artifactId.length() + version.length() + 2 );
        // the problem is that both the classifier and the type could legally contain a '.'
        // so we need to try all posibilities
        int index = -1;
        while (  -1 != (index = classifierAndType.indexOf( '.', index + 1)) )
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
            Artifact a = artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
            if ( includesFilter != null && !includesFilter.include( a ) )
            {
                continue;
            }
            if ( excludesFilter != null && !excludesFilter.include( a ) )
            {
                continue;
            }
            return content;
        }

        return null;  // artifact is excluded
    }

    public List getChildPaths( String path )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static class StubPluginArtifact
        implements Artifact
    {
        private final String groupId;

        private final Plugin plugin;

        public StubPluginArtifact( String groupId, Plugin plugin )
        {
            this.groupId = groupId;
            this.plugin = plugin;
        }

        public String getGroupId()
        {
            return groupId;
        }

        public String getArtifactId()
        {
            return plugin.getArtifactId();
        }

        public String getVersion()
        {
            return null;
        }

        public void setVersion( String version )
        {
        }

        public String getScope()
        {
            return null;
        }

        public String getType()
        {
            return "maven-plugin";
        }

        public String getClassifier()
        {
            return null;
        }

        public boolean hasClassifier()
        {
            return false;
        }

        public File getFile()
        {
            return null;
        }

        public void setFile( File destination )
        {
        }

        public String getBaseVersion()
        {
            return "";
        }

        public void setBaseVersion( String baseVersion )
        {
        }

        public String getId()
        {
            return getDependencyConflictId() + ":";
        }

        public String getDependencyConflictId()
        {
            StringBuffer buffer = new StringBuffer();

            buffer.append( getGroupId() );
            buffer.append( ":" ).append( getArtifactId() );
            buffer.append( ":" ).append( getType() );
            buffer.append( ":" );

            return buffer.toString();
        }

        public void addMetadata( ArtifactMetadata metadata )
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Collection getMetadataList()
        {
            return Collections.EMPTY_LIST;
        }

        public void setRepository( ArtifactRepository remoteRepository )
        {
        }

        public ArtifactRepository getRepository()
        {
            return null;
        }

        public void updateVersion( String version, ArtifactRepository localRepository )
        {
        }

        public String getDownloadUrl()
        {
            return null;
        }

        public void setDownloadUrl( String downloadUrl )
        {
        }

        public ArtifactFilter getDependencyFilter()
        {
            return null;
        }

        public void setDependencyFilter( ArtifactFilter artifactFilter )
        {
        }

        public ArtifactHandler getArtifactHandler()
        {
            return null;
        }

        public List getDependencyTrail()
        {
            return null;
        }

        public void setDependencyTrail( List dependencyTrail )
        {
        }

        public void setScope( String scope )
        {
        }

        public VersionRange getVersionRange()
        {
            return null;
        }

        public void setVersionRange( VersionRange newRange )
        {
        }

        public void selectVersion( String version )
        {
        }

        public void setGroupId( String groupId )
        {
        }

        public void setArtifactId( String artifactId )
        {
        }

        public boolean isSnapshot()
        {
            return false;
        }

        public void setResolved( boolean resolved )
        {
        }

        public boolean isResolved()
        {
            return false;
        }

        public void setResolvedVersion( String version )
        {
        }

        public void setArtifactHandler( ArtifactHandler handler )
        {
        }

        public boolean isRelease()
        {
            return false;
        }

        public void setRelease( boolean release )
        {
        }

        public List getAvailableVersions()
        {
            return null;
        }

        public void setAvailableVersions( List versions )
        {
        }

        public boolean isOptional()
        {
            return false;
        }

        public void setOptional( boolean optional )
        {
        }

        public ArtifactVersion getSelectedVersion()
            throws OverConstrainedVersionException
        {
            return null;
        }

        public boolean isSelectedVersionKnown()
            throws OverConstrainedVersionException
        {
            return false;
        }

        public int compareTo( Object o )
        {
            return 0;
        }
    }
}
