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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.codehaus.mojo.setup.SetupExecutionRequest.MergeType;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author Robert Scholte
 * @since 1.0.0
 */
public abstract class AbstractSetupManager
    implements SetupManager, LogEnabled
{
    /**
     * @plexus.requirement
     */
    private MavenFileFilter mavenFileFilter;

    private Logger log;

    protected final Logger getLog()
    {
        return log;
    }

    public void process( SetupExecutionRequest request )
        throws SetupExecutionException
    {
        validateRequest( request );

        File targetReadFile = getTargetFile( request.getSession() );
                
        File templateFile = null;
        if ( request.getTemplateFile() != null )
        {
            templateFile = filter( request );
        }

        Reader targetSettingsFileReader = null;

        if ( MergeType.EXPAND.equals( request.getMergeType() ) || MergeType.UPDATE.equals( request.getMergeType() ) )
        {
            try
            {
                Reader currentSettingsFileReader = null;
                currentSettingsFileReader = new FileReader( targetReadFile );
                Reader inputSettingsFileReader = null;
                inputSettingsFileReader = new FileReader( templateFile );

                if ( MergeType.EXPAND.equals( request.getMergeType() ) )
                {
                    targetSettingsFileReader = merge( currentSettingsFileReader, inputSettingsFileReader );
                }
                else if ( MergeType.UPDATE.equals( request.getMergeType() ) )
                {
                    targetSettingsFileReader = merge( inputSettingsFileReader, currentSettingsFileReader );
                }
            }
            catch ( FileNotFoundException e )
            {
                throw new SetupExecutionException( e.getMessage(), e );
            }
        }
        else if ( templateFile != null )
        {
            try
            {
                targetSettingsFileReader = new FileReader( templateFile );
            }
            catch ( FileNotFoundException e )
            {
                // nop, already checked
            }
        }
        else if ( targetReadFile.exists() )
        {
            try
            {
                targetSettingsFileReader = new FileReader( targetReadFile );
            }
            catch ( FileNotFoundException e )
            {
                // nop, already checked
            }
        }

        if ( !request.getAdditionalProperties().isEmpty() )
        {
            targetSettingsFileReader = postMerge( targetSettingsFileReader, request );
        }

        File targetWriteFile;
        if ( request.isDryRun() )
        {
            String targetFileName = getTargetFile( request.getSession() ).getName();
            String targetDirectoryName = getTargetFile( request.getSession() ).getParentFile().getName();
            
            targetWriteFile = new File( targetDirectoryName, targetFileName );
        }
        else 
        {
            targetWriteFile = getTargetFile( request.getSession() );
        }
        
        if ( log.isDebugEnabled() ) {
            getLog().debug( "Writing to " + targetWriteFile.getAbsolutePath() );
        }
        
        
        if ( !MergeType.NONE.equals( request.getMergeType() ) )
        {
            if ( targetWriteFile.exists() )
            {
                createBackupFile( targetWriteFile );    
            }
            else 
            {
                targetWriteFile.getParentFile().mkdirs();
            }

            try
            {
                IOUtil.copy( targetSettingsFileReader, new FileWriter( targetWriteFile ) );
            }
            catch ( IOException e )
            {
                throw new SetupExecutionException( e.getMessage(), e );
            }
        }
    }

    /**
     * check if we have enough data to process the setuprequest
     * 
     * @param request
     * @throws SetupExecutionException
     */
    private void validateRequest( SetupExecutionRequest request )
        throws SetupExecutionException
    {
        if ( request == null )
        {
            throw new SetupExecutionException( "request is null" );
        }
        if ( request.getTemplateFile() == null && request.getAdditionalProperties().isEmpty() )
        {
            throw new SetupExecutionException( "missing template or additional properties" );
        }

        if ( request.getTemplateFile() != null && !( request.getTemplateFile().exists() 
                        && request.getTemplateFile().isFile() ) )
        {
            throw new SetupExecutionException( request.getTemplateFile() + " doesn't exist or is not a file" );
        }
        if ( getTargetFile( request.getSession() ).exists() && request.getMergeType() == null )
        {
            throw new SetupExecutionException( "Targetfile exists, so a mergetype is required" );
        }

    }

    /**
     * Give the opportunity to do some extra processing after the merge, based on the request. By default just return
     * the incoming reader
     * 
     * @param targetSettingsFileReader
     * @param request
     * @return
     * @throws SetupMergeException
     */
    protected Reader postMerge( Reader targetSettingsFileReader, SetupExecutionRequest request )
        throws SetupMergeException
    {
        return targetSettingsFileReader;
    }

    /**
     * @param targetFile existing file
     * @throws SetupExecutionException
     */
    @SuppressWarnings( "unchecked" )
    private void createBackupFile( File targetFile )
        throws SetupExecutionException
    {
        String includes = FileUtils.basename( targetFile.getAbsolutePath() ) + ".*";
        try
        {
            List < String > fileNames = FileUtils.getFileNames( targetFile.getParentFile(), includes, null, true );
            int lastBackupIndex = -1;
            for ( String fileName : fileNames )
            {
                String extension = FileUtils.extension( fileName );
                if ( "bak".equals( extension ) )
                {
                    lastBackupIndex++;
                }
                else if ( extension.matches( "bak\\d+" ) )
                {
                    lastBackupIndex = Math.max( lastBackupIndex, Integer.parseInt( extension.substring( 3 ) ) );
                }
            }
            File backupFile =
                FileUtils.getFile( targetFile.getAbsolutePath() + ".bak"
                    + ( lastBackupIndex == -1 ? "" : ++lastBackupIndex ) );
            FileUtils.copyFile( targetFile, backupFile );
        }
        catch ( IOException e )
        {
            throw new SetupExecutionException( " Failed to create backupfile" );
        }
    }

    protected abstract String getPrototypeFilename();

    /**
     * Use Readers to keep most logic in this class
     * 
     * @param dominant
     * @param recessive
     * @return
     * @throws SetupMergeException
     */
    protected abstract Reader merge( Reader dominant, Reader recessive )
        throws SetupMergeException;

    protected abstract File getTargetFile( MavenSession session );

    protected void write( Reader reader, MavenSession session )
        throws IOException
    {
        IOUtil.copy( reader, new FileWriter( getTargetFile( session ) ) );
    }

    /**
     * @param from
     * @param to
     * @param propertiesFilePaths
     * @throws IOException
     */
    @SuppressWarnings( "unchecked" )
    protected File filter( SetupExecutionRequest request )
        throws SetupExecutionException
    {
        File result = FileUtils.createTempFile( request.getTemplateFile().getName(), "-filtered", null );
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Create temporary file: " + result.getAbsolutePath() );
            getLog().debug( "Temporary file won't be deleted while debugging" );
        }
        else
        {
            result.deleteOnExit();
        }
        try
        {
            List < FileUtils.FilterWrapper > filterWrappers =
                mavenFileFilter.getDefaultFilterWrappers( null, request.getPropertyFilenames(), true,
                                                          request.getSession(), null );
            FileUtils.copyFile( request.getTemplateFile(), result, request.getEncoding(),
                                filterWrappers.toArray( new FileUtils.FilterWrapper[0] ), true );
        }
        catch ( MavenFilteringException e )
        {
            throw new SetupExecutionException( e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new SetupExecutionException( e.getMessage() );
        }
        return result;
    }

    public InputStream getPrototypeInputStream()
        throws IOException
    {
        return this.getClass().getClassLoader().getResource( getPrototypeFilename() ).openStream();
    }

    public void enableLogging( Logger logger )
    {
        this.log = logger;
    }

}
