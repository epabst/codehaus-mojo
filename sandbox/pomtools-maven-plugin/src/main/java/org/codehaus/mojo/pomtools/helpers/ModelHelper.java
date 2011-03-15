package org.codehaus.mojo.pomtools.helpers;

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

import java.lang.reflect.Constructor;
import java.util.Iterator;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.ModelVersionRange;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class ModelHelper
{
    public static final String NULL = "(null)";
    
    public static final String UNKNOWN = "(unknown)";
    
    public static final String GROUP_ID = "groupId";
    
    public static final String ARTIFACT_ID = "artifactId";
    
    public static final String VERSION = "version";
    
    public static final ToStringStyle TO_STRING_STYLE = new ModelToStringStyle();
    
    private ModelHelper()
    {
    }

    public static Constructor getConstructor( Class clazz, Class[] signature )
    {
        Constructor con = ConstructorUtils.getAccessibleConstructor( clazz, signature );
        
        if ( con == null )
        {
            throw new ModelReflectionException( "Unable to locate the constructor for: " 
                                                   + clazz.getName() + " with the signature " 
                                                   + String.valueOf( signature ) );
        }  
        
        return con;
    }
    
    
    public static String buildFullName( String parentName, String myName )
    {
        StringBuffer sb = new StringBuffer();
        
        if ( parentName != null )
        {
            sb.append( parentName );
            sb.append( ObjectWrapper.FIELD_PATH_SEPARATOR );
        }
        
        sb.append( myName );
        
        return sb.toString();
    }

    public static String versionlessKey( ObjectWrapper obj )
    {
        return versionlessKey( (String) obj.getFieldValue( GROUP_ID ),
                               (String) obj.getFieldValue( ARTIFACT_ID ) );
    }
    
    public static String versionlessKey( Artifact artifact )
    {
        return versionlessKey( artifact.getGroupId(), artifact.getArtifactId() );
    }
    
    public static String versionlessKey( String groupId, String artifactId )
    {
        return ArtifactUtils.versionlessKey( StringUtils.defaultString( groupId, UNKNOWN ), 
                                             StringUtils.defaultString( artifactId, UNKNOWN ) );
    }
    
    public static String versionedKey( Artifact artifact )
    {
        return versionedKey( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion() );
    }
    
    public static String versionedKey( String groupId, String artifactId, String version )
    {
        return StringUtils.defaultString( groupId, UNKNOWN )
               + ":" 
               + StringUtils.defaultString( artifactId, UNKNOWN )
               + ":" 
               + StringUtils.defaultString( version, UNKNOWN );
    }

    public static boolean isParsableVersion( String strVersion )
    {
        try
        {
            ModelVersionRange.createFromVersionSpec( strVersion );
            return true;
        }
        catch ( InvalidVersionSpecificationException e )
        {
            return false;
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
    }
    
    /** Adds an exclusion to the supplied dependency if it doesn't already contain
     * an exclusion for that groupId/artifactId.  
     * 
     * @param dependency
     * @param groupId
     * @param artifactId
     * @return boolean whether the item was added or not.
     */
    public static boolean addExclusion( ObjectWrapper dependency, String groupId, String artifactId )
    {
        ListWrapper exclusions = (ListWrapper) dependency.getFieldValue( "exclusions" );
        
        // Make sure exclusions doesn't already contain one for this groupId, artifactId
        for ( Iterator iter = exclusions.getItems().iterator(); iter.hasNext(); )
        {
            ObjectWrapper exclusion = (ObjectWrapper) iter.next();
            
            if ( StringUtils.equals( (String) exclusion.getFieldValue( GROUP_ID ), groupId )
                && StringUtils.equals( (String) exclusion.getFieldValue( ARTIFACT_ID ), artifactId ) )
            {
                // This dependency already has this artifact excluded
                return false;
            }
        }
        
        ObjectWrapper obj = exclusions.createItem( null );
        
        obj.setFieldValue( GROUP_ID, groupId );
        obj.setFieldValue( ARTIFACT_ID, artifactId );
        
        return true;
    }
    
    /** Adds an exclusion to the dependency which transitively imports the
     * artifact specified by this node.
     * 
     * @param info
     * @throws PomToolsException
     */
    public static boolean addExclusionForNode( Artifact dependencyArtifact, String groupId, String artifactId )
        throws PomToolsException
    {
        PomToolsPluginContext context = PomToolsPluginContext.getInstance();
        
        ObjectWrapper dependency = context.getActiveProject().findDependency( dependencyArtifact );
        
        return addExclusion( dependency, groupId, artifactId );
    }
}
