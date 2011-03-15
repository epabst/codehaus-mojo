package org.codehaus.mojo.pomtools;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.config.PomToolsConfig;
import org.codehaus.mojo.pomtools.helpers.MetadataHelper;
import org.codehaus.mojo.pomtools.validation.ProjectValidationResult;
import org.codehaus.mojo.pomtools.wrapper.custom.ProjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.modify.AbstractModifiableObject;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class PomToolsPluginContext extends AbstractModifiableObject
{
    private static ThreadLocal threadLocal = new ThreadLocal();

    private final MavenSession session;
    
    private List reactorProjects;
    
    private List projects = new ArrayList();

    private int activeProjectIndex = -1;

    private MetadataHelper metadataHelper;
    
    private final List remoteArtifactRepositories;
    private final ArtifactRepository localRepository;
    
    private final Log log;
    
    private final Map fieldConfigurationMap = new HashMap();
    
    private final Map fieldNamePatternMap = new HashMap();
    
    private final boolean showUnparsedVersions;

    public PomToolsPluginContext( MavenSession session, List remoteArtifactRepositories, 
                               ArtifactRepository localRepository, 
                               PomToolsConfig config, boolean showUnparsedVersions, Log log )
        throws MojoExecutionException
    {
        super( null );
        
        this.session = session;
        this.remoteArtifactRepositories = remoteArtifactRepositories;
        this.localRepository = localRepository;
        this.log = log;
        
        loadFieldConfiguration( config );
        
        this.showUnparsedVersions = showUnparsedVersions;
    }
    
    private void loadFieldConfiguration( PomToolsConfig config )
        throws MojoExecutionException
    {
        for ( Iterator i = config.getFieldConfigurations().iterator(); i.hasNext(); )
        {
            FieldConfiguration fieldConf = (FieldConfiguration) i.next();
            
            Object existing = null;
            
            if ( fieldConf.getFieldNamePattern() != null )
            {
                Pattern namePattern = Pattern.compile( fieldConf.getFieldNamePattern() );
                
                if ( this.fieldNamePatternMap.put( namePattern, fieldConf ) != null )
                {
                    throw new MojoExecutionException( "Duplicate field configuration found for: " 
                                                      + fieldConf.getFieldNamePattern() );
                }

                // Now merge our values into any matching fields.  Its important to put the patterns last 
                // in the config file
                for ( Iterator iter = fieldConfigurationMap.entrySet().iterator(); iter.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry) iter.next();
                    
                    if ( namePattern.matcher( (String) entry.getKey() ).find() )
                    {
                        ( (FieldConfiguration) entry.getValue() ).merge( fieldConf );
                    }
                }
            }
            
            if ( fieldConf.getFieldName() != null )
            { 
                // If fieldName is specified, just add it to the list for the processing below
                fieldConf.getFieldNames().add( fieldConf.getFieldName() );
            }
            
            for ( Iterator iter = fieldConf.getFieldNames().iterator(); iter.hasNext(); )
            {
                String fieldName = (String) iter.next();
                
                existing = this.fieldConfigurationMap.put( fieldName, fieldConf );
                
                if ( existing != null )
                {
                    throw new MojoExecutionException( "Duplicate field configuration found for: " + fieldName );
                }
            }
        }
    }
  
    void loadProjects( List reactorProjectList )
        throws PomToolsException
    {
        this.reactorProjects = reactorProjectList;
        
        for ( Iterator i = reactorProjects.iterator(); i.hasNext(); )
        {
            projects.add( new ProjectWrapper( this, (MavenProject) i.next() ) );
        }
        
        if ( !projects.isEmpty() )
        {
            activeProjectIndex = 0;
        }
    }
    
    /** Returns the ThreadLocal instance of PomToolsPluginContext. You must
     * call {@link #setInstance(PomToolsPluginContext)} before calling this function.
     * @throws IllegalStateException if the current connection is not set
     */
    public static PomToolsPluginContext getInstance() 
    {
        PomToolsPluginContext ctx = (PomToolsPluginContext) threadLocal.get();
        if ( ctx == null ) 
        {
            throw new IllegalStateException( "PomToolsPluginContext in the thread-local variable was null, " 
                                            + "please call setInstance() first" );
        }
        return ctx;
    }

    /** Sets the ThreadLocal PomToolsPluginContext instance.  See {@see #getCurrent} for more information. */
    public static void setInstance( PomToolsPluginContext ctx ) 
    { 
        threadLocal.set( ctx ); 
    }

    public static boolean hasCurrent() 
    {
        return threadLocal.get() != null;
    }
    
    public FieldConfiguration getFieldConfiguration( String fieldName )
    {
        // Always look for a direct match first
        FieldConfiguration config = (FieldConfiguration) fieldConfigurationMap.get( fieldName );
        
        // Now look in our patterns for a match
        if ( config == null )
        {
            for ( Iterator iter = fieldNamePatternMap.keySet().iterator(); iter.hasNext(); )
            {
                Pattern p = (Pattern) iter.next();
                
                if ( p.matcher( fieldName ).matches() )
                {
                    config = (FieldConfiguration) fieldNamePatternMap.get( p );
                    
                    //Cache it for future use
                    fieldConfigurationMap.put( fieldName, config );                    
                }
            }
        }
        
        return config;
    }
    
    public MetadataHelper getMetadataHelper()
    {
        if ( metadataHelper == null )
        {
            metadataHelper = new MetadataHelper( session, remoteArtifactRepositories, localRepository );
        }
        return metadataHelper;
    }
    
    public Log getLog()
    {
        return this.log;
    }

    public List getProjects()
    {
        return projects;
    }

    public ProjectWrapper getActiveProject()
    {
        return (ProjectWrapper) projects.get( activeProjectIndex );
    }

    public void setActiveProjectIndex( int index )
    {
        this.activeProjectIndex = index;
    }

    public boolean isShowUnparsedVersions()
    {
        return showUnparsedVersions;
    }

    /** Saves all projects if they pass the model validation.
     * 
     * @return A List of {@link ProjectValidationResult}
     * @throws PomToolsException
     * @throws ProjectBuildingException 
     */
    public List saveAllProjects()
        throws PomToolsException, ProjectBuildingException
    {
        List saveResults = new ArrayList();
        
        boolean success = true;

        for ( Iterator i = getProjects().iterator(); i.hasNext(); )
        {
            ProjectWrapper project = (ProjectWrapper) i.next();

            if ( project.isModified() )
            {
                try
                {
                    ProjectValidationResult valResult = project.validateModel();
                    
                    saveResults.add( valResult );
                    
                    if ( valResult.isValid() )
                    {
                        project.save();
                        project.setModified( false );
                    }
                    else
                    {
                        success = false;
                    }
                }
                catch ( IOException e )
                {
                    throw new PomToolsException( "An error occurred while saving project: "
                        + project.getValueLabel(), e );
                }
            }
        }
        
        if ( success )
        {
            this.setModified( false );
        }

        return saveResults;
    }
    
    /** Reverts all projects to their unmodified state.
     * 
     * @return A List of {@link ProjectWrapper} that were reverted
     * @throws PomToolsException
     */
    public List revertAllProjects()
        throws PomToolsException
    {
        List projectsReverted = new ArrayList();
        
        for ( Iterator i = getProjects().iterator(); i.hasNext(); )
        {
            ProjectWrapper project = (ProjectWrapper) i.next();
            
            if ( project.isModified() )
            {
                project.revert();
                
                projectsReverted.add( project );
            }
        }

        this.setModified( false );
        
        return projectsReverted;
    }

    public MavenSession getSession()
    {
        return session;
    }
}
