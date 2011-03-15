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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.setup.SetupExecutionRequest.MergeType;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract Mojo for all setup goals
 * 
 * @requiresProject false
 * @author Robert Scholte
 * @since 1.0.0
 */
public abstract class AbstractSetupMojo
    extends AbstractMojo implements Contextualizable, Initializable
{
 
    // set during contextualization 
    /**
     * RuntimeInformation to discover the Maven version
     */
    private RuntimeInformation rti;

    /**
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    private MavenSession session;

    /**
     * Base directory of the project.
     * 
     * @parameter expression="${basedir}" //never project.basedir, because pom is not required
     * @readonly
     */
    private File baseDirectory;

    /**
     * Location of the settingstemplate, can be an URL, a relative or absolute path to the file
     * 
     * @parameter alias="templateFile" expression="${templateFile}"
     */
    private String templateFilename;

    /**
     * Define how to merge. Valid values are: none, update, expand and overwrite No default value, so we can detect if
     * it is set by commandline
     * 
     * @parameter expression="${merge}"
     */
    private String merge;

    /**
     * Specify the encoding to use when specifying a templateFile
     * 
     * @parameter expression="${encoding}"
     */
    private String encoding;
    
    /**
     * With dryRun the execution will run as normal, but the target-file won't change.
     * Instead a folder will be created with the same name as the directory-name containing the target file.
     * Although there will be a backup-file, this way you can check the result before really changing the file.  
     * 
     * @parameter expression="${dryRun}"
     * 
     */
    private boolean dryRun = false;

    /**
     * Setup execution request to use for the setup manager 
     */
    private SetupExecutionRequest setupRequest = new DefaultSetupExecutionRequest();

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    public void contextualize ( Context context ) throws ContextException
    {
        PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
        try
        {
            rti = (RuntimeInformation) container.lookup( RuntimeInformation.class.getName() );
        }
        catch ( ComponentLookupException e )
        {
            getLog().info( "Could not retrieve RuntimeInformation to check maven version." );
        }
    }
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    public void initialize()
        throws InitializationException
    {
        try
        {
            if ( !isRequiredMavenVersion( getMavenVersionRange() ) )
            {
                throw new InitializationException( "\nThis goal is not supported. You're using Maven " 
                                                   + rti.getApplicationVersion() + ", it must be within this range: " 
                                                   + getMavenVersionRange().toString() );
            }
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw new InitializationException( e.getMessage() );
        }
    }
    
    // protected getters for subclasses
    /**
     * @return the MavenSession
     * @since 1.0.0
     */
    protected MavenSession getSession()
    {
        return session;
    }
    
    /**
     * 
     * @return the baseDirectory as File
     * @since 1.0.0
     */
    protected File getBaseDirectory()
    {
        return baseDirectory;
    }

    /**
     * 
     * @return the template filename
     * @since 1.0.0
     */
    protected String getTemplateFilename()
    {
        return templateFilename;
    }

    /**
     * 
     * @return the setup request
     * @since 1.0.0
     */
    protected SetupExecutionRequest getSetupRequest()
    {
        return setupRequest;
    }

    // protected abstract getters, which subclasses must provide

    /**
     * @return the default-template filename
     * @since 1.0.0
     */
    protected abstract String getDefaultTemplateFilename();

    /**
     * 
     * @return the properties filename
     * @since 1.0.0
     */
    protected abstract String getPropertiesFilename();

    /**
     * 
     * @return the setup manager
     * @since 1.0.0
     */
    protected abstract SetupManager getSetupManager();

    // helper methods

    /**
     * Exceptionless method to check is the value is a valid URL
     * 
     * @param value the potential URL
     * @return true is it's a URL, otherwise false
     */
    private boolean isUrl( String value )
    {
        boolean result = true;

        try
        {
            new URL( value );
        }
        catch ( MalformedURLException e )
        {
            result = false;
        }

        return result;
    }

    /**
     * The prototype is an (xml)-file which contains comments on the tags to use.
     * 
     * @return the prototype as InputStream, since it may be part of a jar
     * @throws IOException if input stream can't be resolved
     * @since 1.0.0
     */
    protected InputStream getPrototypeInputStream()
        throws IOException
    {
        return getSetupManager().getPrototypeInputStream();
    }

    /**
     * Get the templatefile, either a custom or the default template file
     * 
     * @return resolved templateFile or null
     * @since 1.0.0
     */
    protected File resolveTemplateFile()
    {
        File result = null;

        if ( StringUtils.isNotEmpty( getTemplateFilename() ) )
        {
            if ( isUrl( getTemplateFilename() ) )
            {
                try
                {
                    result = FileUtils.toFile( new URL( getTemplateFilename() ) );
                }
                catch ( MalformedURLException e )
                {
                    // nop
                }
            }
            else
            {
                result = FileUtils.resolveFile( getBaseDirectory(), getTemplateFilename() );
            }
        }
        else
        {
            File templateFile = new File( getBaseDirectory(), getDefaultTemplateFilename() );

            if ( templateFile.exists() )
            {
                result = templateFile;

                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "using " + templateFile );
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        preProcess();
        try
        {
            File settingsTemplate = resolveTemplateFile();

            if ( settingsTemplate != null || !getSetupRequest().getAdditionalProperties().isEmpty() )
            {
                // Using merge as String, because translation to MergeType requires a switch/case or a valueOf(), which
                // might break;
                getSetupRequest().setSession( getSession() )
                    .setTemplateFile( resolveTemplateFile() )
                    .setPropertyFilenames( getPropertyFiles() )
                    .setDryRun( dryRun )
                    .setEncoding( encoding );
                
                if ( merge != null )
                {
                    getSetupRequest().setMergeType( MergeType.valueOf( merge.toUpperCase() ) );
                }
                getSetupManager().process( getSetupRequest() );
            }
            else if ( merge == null )
            { // copy prototype as default template to baseDirectory
                IOUtil.copy( getPrototypeInputStream(),
                    new FileWriter( FileUtils.resolveFile( getBaseDirectory(), getDefaultTemplateFilename() ) ) );

                if ( getLog().isInfoEnabled() )
                {
                    getLog().info( getDefaultTemplateFilename() + " has been created in the current directory." );
                    getLog().info( "Change this file for your own configuration and rerun the last maven-goal." );
                    getLog().info( "This way it will try to copy the file to the proper location." );
                }
            }
            else
            {
                throw new MojoFailureException( "merge-property was set, but there was no template available" );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( SetupExecutionException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        postProcess();
    }

    /**
     * Use VersionRange.createFromVersionSpec( String ) to define the range for which the goal can be used
     * 
     * @return the versionRange
     * @throws InvalidVersionSpecificationException The specification can't be converted to a range
     * @since 1.0.0
     */
    protected abstract VersionRange getMavenVersionRange() throws InvalidVersionSpecificationException;

    /**
     * Can be overridden, if you want to add extra info before processing. For instance: setup:security-settings uses
     * this method to encrypt the master-password
     * 
     * @throws MojoExecutionException possible executionException
     * @throws MojoFailureException possible failureException
     * @since 1.0.0
     */
    protected void preProcess()
        throws MojoExecutionException, MojoFailureException
    {
    }

    /**
     * Can be overridden, if you want to take actions after processing
     * 
     * @throws MojoExecutionException possible executionException
     * @throws MojoFailureException possible failureException
     * @since 1.0.0
     */
    protected void postProcess()
        throws MojoExecutionException, MojoFailureException
    {

    }

    /**
     * Return list of propertyFiles as String, because that's how MavenFileFilter expects them
     * 
     * @return List of propertyFilePaths or null
     * @since 1.0.0
     */
    protected List < String > getPropertyFiles()
    {
        List < String > result = null;
        File propertyFile = FileUtils.resolveFile( getBaseDirectory(), getPropertiesFilename() );
        if ( propertyFile.exists() )
        {
            result = Collections.singletonList( propertyFile.getAbsolutePath() );
        }
        return result;
    }
    
    /**
     * During the initializingPhase we can check if the required Maven version is used.
     * 
     * @param range the version range of Maven for which this goal is executable. 
     * @return true is version is within range, otherwise false.
     */
    private boolean isRequiredMavenVersion( VersionRange range ) 
    {
        return range.containsVersion( rti.getApplicationVersion() );
    }
}
