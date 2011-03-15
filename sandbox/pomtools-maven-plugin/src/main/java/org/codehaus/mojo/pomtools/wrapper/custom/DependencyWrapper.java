package org.codehaus.mojo.pomtools.wrapper.custom;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.PomToolsVersionException;
import org.codehaus.mojo.pomtools.helpers.MetadataHelper;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.versioning.DefaultVersionInfo;
import org.codehaus.mojo.pomtools.versioning.VersionInfo;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanFields;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class DependencyWrapper 
{
    public static final String VERSION = "version";

    public static final String ARTIFACT_ID = "artifactId";

    public static final String GROUP_ID = "groupId";
    
    public static final String TYPE = "type";
    
    public static final String SCOPE = "scope";
    
    private static final String[] FIELDS = new String[] { GROUP_ID, ARTIFACT_ID, VERSION, TYPE, SCOPE };

    private final ObjectWrapper objWrapper;
    
    /* groupId and artifactId used when fetching metadata */
    private String metadataGroupId;
    private String metadataArtifactId;
    
    private LinkedList versions;
    private LinkedList parsedVersions;
    private LinkedList unparsedVersions;
    
    public DependencyWrapper( ObjectWrapper objWrapper )
    {
        if ( objWrapper == null )
        {
            throw new IllegalArgumentException( "ObjectWrapper cannot be null" );
        }
        
        this.objWrapper = objWrapper;
        
        BeanFields fields = objWrapper.getFields();
        
        // Validate that the object has all of our relevant fields
        for ( int i = 0; i < FIELDS.length; i++ )
        {
            if ( fields.get( FIELDS[i] ) == null )
            {
                throw new ModelReflectionException( "Unable to find field in Dependency: " + FIELDS[i] );
            }
        }
    }

    /** If groupId and artifactId are equal, only one of them is returned.
     * commons-lang:commons-lang => commons-lang
     * 
     */
    public String toString() 
    {
        String groupId = getGroupId(); 
        String artifactId = getArtifactId();
       
        if ( StringUtils.equals( groupId, artifactId ) && groupId != null )
        {
            return groupId;
        } 
        else 
        {
            return ModelHelper.versionlessKey( groupId, artifactId );
        } 
    }
    
    public String getVersion() 
    {
        return (String) objWrapper.getFieldValue( VERSION );
    }
    
    public void setVersion( String version ) 
    {
        if ( !StringUtils.equals( getVersion(), version ) )
        {
            objWrapper.setFieldValue( VERSION, version );
        }
    }
    
    public String getGroupId() 
    {
        return (String) objWrapper.getFieldValue( GROUP_ID );
    }
    
    public String getArtifactId() 
    {
        return (String) objWrapper.getFieldValue( ARTIFACT_ID );
    }
    
    public String getType() 
    {
        return (String) objWrapper.getFieldValue( TYPE );
    }
    
    public String getScope() 
    {
        return (String) objWrapper.getFieldValue( SCOPE );
    }
    
    
    public void setToLatestVersion( boolean includeSnapshots ) 
    {
        String latest = getLatestVersion( includeSnapshots ).getVersionString();
        
        objWrapper.setFieldValue( VERSION, latest );
    }

    public ModelVersionRange getVersionRange( String version )
    {
        if ( version == null )
        {
            return null;
        }
        
        try
        {
            return ModelVersionRange.createFromVersionSpec( version );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw new PomToolsVersionException( "Unable to parse version: " + version, e );
        }
    }
    
    public ModelVersionRange getVersionRange() 
    {
        return getVersionRange( (String) objWrapper.getFieldValue( DependencyWrapper.VERSION ) );
    }
    
    public ModelVersionRange getResolvedVersionRange() 
    {
        return getVersionRange( getResolvedVersion() );
    }
    
    private List getMetadataVersions()
        throws PomToolsVersionException
        {
        metadataGroupId = getGroupId(); 
        metadataArtifactId = getArtifactId();
        
        try        
        {
            PomToolsPluginContext context = PomToolsPluginContext.getInstance();
            
            RepositoryMetadata repoMetadata = context.getMetadataHelper().getMetadata( getArtifact() );

            if ( repoMetadata != null )
            {
                Metadata metadata = repoMetadata.getMetadata();
    
                if ( metadata != null && metadata.getVersioning() != null )
                {
                    return metadata.getVersioning().getVersions();
                }
                else if ( metadata != null && metadata.getVersion() != null )
                {
                    // Non provided jars like javax.mail and javax.servlet:servlet-api 
                    // don't have versioning sections, so create the list from the single version
                    // specified by the artifact.
                    if ( isValidArtifact() )
                    {
                        List metadataVersions = new ArrayList();
        
                        metadataVersions.add( metadata.getVersion() );
        
                        return metadataVersions;
                    }
                }
            }
            
            return null;
        }
        catch ( InvalidArtifactRTException e )
        {
            throw new PomToolsVersionException( e );
        }
        catch ( ArtifactMetadataRetrievalException e )
        {
            throw new PomToolsVersionException( e );
        }
    }
    
    /** We cache the versions so we want to make sure that we aren't using 
     * old information if the groupId or artifactId has changed since we last 
     * fetched the versions
     */
    private boolean isMetadataCurrent()
    {
        return metadataGroupId != null && metadataArtifactId != null
            && StringUtils.equals( metadataGroupId, getGroupId() )
            && StringUtils.equals( metadataArtifactId, getArtifactId() );
        
    }
    
    private void resolveVersions()
    {
        if ( versions == null || !isMetadataCurrent() )
        {
            versions = new LinkedList();
            parsedVersions = new LinkedList();
            unparsedVersions = new LinkedList();

            List metadataVersions = getMetadataVersions();
            if ( metadataVersions != null )
            {
                for ( Iterator i = metadataVersions.iterator(); i.hasNext(); )
                {
                    //ArtifactVersion av = (ArtifactVersion) i.next();
                    DefaultVersionInfo version = new DefaultVersionInfo( (String) i.next() );
                    
                    versions.add( version );
                    if ( version.isParsed() )
                    {
                        parsedVersions.add( version );
                    }
                    else
                    {
                        unparsedVersions.add( version );
                    }
                }

                Collections.sort( parsedVersions );
            }
        }
    }
    
    private Artifact getArtifact()
    {
        MetadataHelper metaHelper = PomToolsPluginContext.getInstance().getMetadataHelper();
        
        return metaHelper.createArtifact( getGroupId(),
                                          getArtifactId(),
                                          getVersion(),
                                          getType(),
                                          getScope() );
    }
    
    public boolean isValidArtifact()
    {
        MetadataHelper metaHelper = PomToolsPluginContext.getInstance().getMetadataHelper();
        
        return metaHelper.isValidDependencyArtifact( getArtifact() ); 
    }
    
    public boolean isValidGroupIdArtifactId()
    {
        MetadataHelper metaHelper = PomToolsPluginContext.getInstance().getMetadataHelper();
        
        return metaHelper.isValidGroupIdArtifactId( getArtifact() ); 
    }
    
    public boolean isValidVersion()
    {
        return isValidVersion( getVersion() );
    }
    
    public boolean isValidVersion( String version )
    {
        if ( StringUtils.isEmpty( version ) )
        {
            return false;
        }
        
        try
        {
            List metadataVersions = getMetadataVersions();
            
            ModelVersionRange range = ModelVersionRange.createFromVersionSpec( version );
            
            if ( range.hasRestrictions() )
            { 
                return metadataVersions != null;
            }
            else 
            {
                return metadataVersions != null && metadataVersions.contains( version );
            }
        }
        catch ( InvalidArtifactRTException e )
        {
            return false;
        }
        catch ( PomToolsVersionException e )
        {
            return false;
        }
        catch ( InvalidVersionSpecificationException e )
        {
            return false;
        }
    }
    
    public boolean setVersionLowerBound( String boundVersionSpec )
        throws PomToolsVersionException
    {
        return setBoundedVersion( boundVersionSpec, true );
    }
    
    public boolean setVersionUpperBound( String boundVersionSpec )
        throws PomToolsVersionException
    {
        return setBoundedVersion( boundVersionSpec, false );
    }
    
    protected boolean setBoundedVersion( String boundVersionSpec, boolean isLower )
        throws PomToolsVersionException
    {
        boolean isUpper = !isLower;

        try
        {
            ModelVersionRange currentRange = getVersionRange();
            ModelVersionRange newRange = ModelVersionRange.createFromVersionSpec( boundVersionSpec );

            if ( currentRange != null && currentRange.hasRestrictions() )
            {
                Restriction curres = (Restriction) currentRange.getRestrictions().get( 0 );
                Restriction newres = (Restriction) newRange.getRestrictions().get( 0 );
                Restriction r = new Restriction( ( isLower || curres.getLowerBound() == null ) ? newres.getLowerBound()
                                                                                              : curres.getLowerBound(),
                                                 ( isLower || curres.getLowerBound() == null ) ? newres
                                                     .isLowerBoundInclusive() : curres.isLowerBoundInclusive(),
                                                 ( isUpper || curres.getUpperBound() == null ) ? newres.getUpperBound()
                                                                                              : curres.getUpperBound(),
                                                 ( isUpper || curres.getUpperBound() == null ) ? newres
                                                     .isUpperBoundInclusive() : curres.isUpperBoundInclusive() );

                currentRange.getRestrictions().clear();
                currentRange.getRestrictions().add( r );
            }
            else
            {
                currentRange = newRange;
            }

            String strVersion = currentRange.toString();
            if ( ModelHelper.isParsableVersion( strVersion ) )
            {
                setVersion( currentRange.toString() );
                return true;
            }
            else
            {
                return false;
            }

        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw new PomToolsVersionException( "Unable to parse version: " + boundVersionSpec, e );
        }
    }
    
    public List getAllVersions()
    {
        resolveVersions();            
        return versions;
    }
    
    public List getParsedVersions()
    {
        resolveVersions();            
        return parsedVersions;
    }
    
    public List getUnparsedVersions() 
    {
        resolveVersions();            
        return unparsedVersions;
    }
    
    /**
     * Its possible that this dependency has a null version
     * because it is inheriting it from the parent.  Return the parent's
     * version of the dependency if this one is null 
     * 
     * @return
     */
    public String getResolvedVersion()
    {
        String version = getVersion();
        
        if ( version != null )
        {
            return version;
        }
        else
        {
            ProjectWrapper project = PomToolsPluginContext.getInstance().getActiveProject();
            
            Dependency tmpDep;
            try
            {
                tmpDep = project.findResolvedDependency( this );
            }
            catch ( PomToolsException e )
            {
                throw new PomToolsVersionException( e );
            }
            
            if ( tmpDep != null )
            {
                return tmpDep.getVersion();
            }
            else
            {
                return null;
            }
        }
    }
    
    public VersionInfo getLatestVersion( boolean includeSnapshots ) 
    {
        try 
        {
            resolveVersions();
        } 
        catch ( PomToolsVersionException e )
        {
            return null;
        }
        
        if ( parsedVersions.isEmpty() )
        {
            return null;
        }
        
        if ( includeSnapshots )
        {
            return (VersionInfo) parsedVersions.getLast();
        }
        else 
        {
            VersionInfo latest = null;
            int index = parsedVersions.size() - 1;
            
            while ( latest == null && index >= 0 )
            {
                VersionInfo tmpInfo = (VersionInfo) parsedVersions.get( index-- );
                if ( !tmpInfo.isSnapshot() )
                {
                    latest = tmpInfo;
                }
            }
            
            return latest;            
        }
    }
}