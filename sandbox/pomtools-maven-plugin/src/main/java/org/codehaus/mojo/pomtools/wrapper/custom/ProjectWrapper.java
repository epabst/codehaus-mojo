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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.PomToolsException;
import org.codehaus.mojo.pomtools.PomToolsRTException;
import org.codehaus.mojo.pomtools.helpers.APIWorkaroundHelper;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.validation.ProjectValidationResult;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.modify.AbstractModifiableObject;
import org.codehaus.mojo.pomtools.wrapper.modify.Modifiable;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.validation.DefaultModelValidator;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ProjectWrapper 
    extends AbstractModifiableObject
{
    private final MavenProject mavenProject;
    
    private ObjectWrapper wrappedModel;
    
    private MavenProject tempResolvedProject;
    
    public ProjectWrapper( Modifiable parentMod, MavenProject project )
        throws PomToolsException
    {
        super( parentMod );
        
        this.mavenProject = project;

        initializeModel();
    }

    protected void initializeModel() 
        throws PomToolsException
    {
        // Get our own fresh copy of the model to edit.
        Model mavenModel = readModel( this.mavenProject.getFile() );
        
        this.wrappedModel = new ObjectWrapper( this, null, mavenModel, "project", Model.class );
    }
    
    public ObjectWrapper getWrappedModel()
    {
        return wrappedModel;
    }
    
    
    
    /** Creates a temporary MavenProject from the current contents of our
     * wrappedModel.  This is usefule when performing operations that need
     * full model resolution. For example, dependencies that inherit their versions
     * from the parent pom.
     */
    protected MavenProject createTempResolvedProject( Model model )
        throws PomToolsException, ProjectBuildingException, InvalidProjectModelException
    {
        PomToolsPluginContext context = PomToolsPluginContext.getInstance();
        
        File baseDir = getBaseDir();

        PlexusContainer container = context.getSession().getContainer();

        File tempPomFile = null;
        try
        {
            tempPomFile = File.createTempFile( ".pomtools-plugin-temp", "-pom.xml.", baseDir );
            tempPomFile.deleteOnExit();
            
            ProjectWrapper.writeModel( tempPomFile, model );

            MavenProjectBuilder builder = (MavenProjectBuilder) container.lookup( MavenProjectBuilder.ROLE );

            // TODO: This is not right. I think we need to get a handle to the global ProfileManager
            ProfileManager profileManager = new DefaultProfileManager( container );
            
            APIWorkaroundHelper.clearBuilderCache( builder );

            MavenProject project = builder.build( tempPomFile, 
                                                  context.getMetadataHelper().getLocalRepository(), profileManager );
            
            return project;
        }
        catch ( IOException e )
        {
            throw new PomToolsException( "An error occured while writing temporary pom", e );
        }
        catch ( ComponentLookupException e )
        {
            throw new PomToolsException( "Unable to get the default MavenProjectBuilder", e );
        }
        finally
        {
            if ( tempPomFile != null && tempPomFile.exists() )
            {
                tempPomFile.delete();
            }
        }
    }
    
    public void setModified( boolean modified )
    {
        super.setModified( modified );
        
        if ( modified )
        {
            // clear our cached resolved model
            tempResolvedProject = null;
        }
    }

    public MavenProject getTemporaryResolvedProject()
        throws PomToolsException, InvalidProjectModelException, ProjectBuildingException
    {
        if ( tempResolvedProject == null )
        {
            tempResolvedProject = createTempResolvedProject( (Model) wrappedModel.getWrappedObject() );
        }

        return tempResolvedProject;
    }
    
    public Dependency findResolvedDependency( DependencyWrapper dep )
        throws PomToolsException
    {
        try
        {
            List deps = getTemporaryResolvedProject().getDependencies();
            if ( deps != null )
            {
                for ( Iterator i = deps.iterator(); i.hasNext(); )
                {
                    Dependency tmpDep = (Dependency) i.next();

                    if ( StringUtils.equals( dep.getGroupId(), tmpDep.getGroupId() )
                        && StringUtils.equals( dep.getArtifactId(), tmpDep.getArtifactId() ) )
                    {
                        return tmpDep;
                    }
                }
            }
        }
        catch ( ProjectBuildingException e )
        {
            throw new PomToolsException( "Error building resolved project", e );
        }

        return null;
    }
    
    public String getGroupId()
    {
        return (String) wrappedModel.getFieldValue( ModelHelper.GROUP_ID );
    }
    
    public String getArtifactId()
    {
        return (String) wrappedModel.getFieldValue( ModelHelper.ARTIFACT_ID );
    }
    
    public String getVersion()
    {
        return (String) wrappedModel.getFieldValue( ModelHelper.VERSION );
    }
    
    public String getValueLabel() 
    {
        String name = getArtifactId();
        
        if ( name == null )
        {
            name = ModelHelper.versionlessKey( getGroupId(), getArtifactId() );
        }
        
        return name; 
    }
    
    public File getBaseDir()
    {
        return mavenProject.getBasedir();
    }
    
    public File getFile()
    {
        return mavenProject.getFile();
    }
    
    public ProjectValidationResult validateModel()
        throws ProjectBuildingException, PomToolsException
    {
        try
        {
            return validateModel( getTemporaryResolvedProject().getModel() );
        }
        catch ( InvalidProjectModelException e )
        {
            return new ProjectValidationResult( this, e.getValidationResult() );
        }        
    }
  
    public ProjectValidationResult validateModel( Model model )
    {
        try 
        {
            return new ProjectValidationResult( this, new DefaultModelValidator().validate( model ) );
        }
        catch ( Exception e )
        {
            throw new PomToolsRTException( e );
        }
    }
    
    public void save()
        throws IOException
    {
        if ( isModified() )
        {
            Model newModel = (Model) this.wrappedModel.getWrappedObject();
            
            writeModel( mavenProject.getFile(), newModel );
            
            setModified( false );            
        }    
    }
    
    public void revert()
        throws PomToolsException
    {
        if ( isModified() )
        {
            this.initializeModel();
            
            setModified( false );
            
            tempResolvedProject = null;
        }    
    }
    
    public static void writeModel( File pomFile, Model model )
        throws IOException
    {
        Writer writer = null;
        
        try
        {
            writer = new FileWriter( pomFile );
            
            MavenXpp3Writer pomWriter = new MavenXpp3Writer();
            
            pomWriter.write( writer, model );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }
    
    protected Model readModel( File pomFile )
        throws PomToolsException
    {
        FileReader fileReader = null;
        
        try
        {
            fileReader = new FileReader( pomFile );
            
            return new MavenXpp3Reader().read( fileReader );
        }
        catch ( IOException e )
        {
            throw new PomToolsException( "Unable to read project file: " + pomFile.getAbsolutePath(), e );
        }
        catch ( XmlPullParserException e )
        {
            throw new PomToolsException( "XML error while reading project configuration", e );
        }
        finally
        {
            if ( fileReader != null )
            {
                IOUtil.close( fileReader );
            }
        }        
    }
    
    public ObjectWrapper findDependency( Artifact artifact )
    {
        ListWrapper dependencies = (ListWrapper) wrappedModel.getFieldValue( "dependencies" );
        
        for ( Iterator iter = dependencies.getItems().iterator(); iter.hasNext(); )
        {
            ObjectWrapper dependency = (ObjectWrapper) iter.next();
            
            if ( StringUtils.equals( (String) dependency.getFieldValue( ModelHelper.GROUP_ID ), 
                                     artifact.getGroupId() )
                && StringUtils.equals( (String) dependency.getFieldValue( ModelHelper.ARTIFACT_ID ), 
                                       artifact.getArtifactId() )
                && StringUtils.equals( (String) dependency.getFieldValue( ModelHelper.VERSION ), 
                                       artifact.getVersion() )
               )
            {
                return dependency;
            }
        }
        
        return null;
    }
}
