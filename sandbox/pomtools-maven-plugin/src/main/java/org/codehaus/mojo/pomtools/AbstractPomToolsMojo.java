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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.pomtools.config.PomToolsConfig;
import org.codehaus.mojo.pomtools.config.io.xpp3.PomToolsPluginXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class AbstractPomToolsMojo
    extends AbstractMojo
{
private static final String CONFIG_FILE = "pomtools-config.xml";
    
    /**
     * The List Dependencies screen will warn if it was unable to 
     * parse any of the dependency versions. This is disabled by default.
     *
     * @parameter expression="${model.showUnparsedVersions}" default-value="false"
     * @readonly
     */
    private boolean showUnparsedVersions = false;
    
    /**
     * The current build session instance. 
     * 
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;
    
    /**
     * Contains the full list of projects in the reactor.
     * 
     * @parameter expression="${reactorProjects}"
     * @required
     * @readonly
     */
    private List reactorProjects;

    /**
     * Remote repositories which will be searched for source attachments.
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List remoteArtifactRepositories;

    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    private PomToolsConfig config;

    public AbstractPomToolsMojo()
    {
        super();
    }

    protected void initialize()
        throws MojoExecutionException
    {
        this.config = readConfiguration();

        PomToolsPluginContext modelContext = new PomToolsPluginContext( session, remoteArtifactRepositories,
                                               localRepository, config, showUnparsedVersions,
                                               getLog() );
        
        PomToolsPluginContext.setInstance( modelContext );
        
        try
        {
            modelContext.loadProjects( reactorProjects );
        }
        catch ( PomToolsException e )
        {
            throw new MojoExecutionException( "Unable to load projects", e );
        }
    }
        
    static PomToolsConfig readConfiguration()
        throws MojoExecutionException
    {
        InputStream in = null;
        try
        {
            PomToolsPluginXpp3Reader reader = new PomToolsPluginXpp3Reader();

            in = PomToolsConfig.class.getClassLoader().getResourceAsStream( CONFIG_FILE );
            
            if ( in == null )
            {
                throw new MojoExecutionException( "Unable to locate plugin configuration file: " + CONFIG_FILE );
            }
            
            PomToolsConfig tmpConfig = reader.read( new InputStreamReader( in ) );
            
            return tmpConfig;
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to read plugin configuration", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new MojoExecutionException( "XML error while reading plugin configuration", e );
        }
        finally
        {
            if ( in != null )
            {
                try
                {
                    in.close();
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "XML error while closing plugin configuration input stream", e );
                }
            }
        }

    }
        
}
