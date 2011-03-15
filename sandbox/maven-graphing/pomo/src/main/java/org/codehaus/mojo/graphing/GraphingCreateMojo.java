package org.codehaus.mojo.graphing;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.codehaus.mojo.graphing.generators.GraphGenerator;
import org.codehaus.mojo.graphing.generators.GraphvizGenerator;
import org.codehaus.mojo.graphing.generators.XmlGraphModelGenerator;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.factory.GraphModelFactory;
import org.codehaus.mojo.graphing.model.factory.OldGraphModelFactory;

/**
 * Create a graph of an arbitrary project. 
 *
 * @author Joakim Erdfelt
 * @since Nov 30, 2005
 * @goal create
 * @requiresProject false
 */
public class GraphingCreateMojo
    extends AbstractMojo
{
    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * Project Builder, used to create a dummy project.
     *
     * @component role="org.apache.maven.project.MavenProjectBuilder"
     * @required
     * @readonly
     */
    private MavenProjectBuilder mavenProjectBuilder;

    /**
     * Artifact resolver, needed to download source jars for inclusion in classpath.
     *
     * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
     * @required
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * Artifact factory, needed to download source jars for inclusion in classpath.
     *
     * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * Artifact Repository Factory.
     *
     * @component role="org.apache.maven.artifact.repository.ArtifactRepositoryFactory"
     * @required
     * @readonly
     */
    private ArtifactRepositoryFactory artifactRepositoryFactory;

    /**
     * Maven Metasource
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource"
     * @required
     * @readonly
     */
    private MavenMetadataSource mavenMetadataSource;

    /**
     * Artifact to show in GUI. (format "groupId:artifactId:version") 
     * 
     * @parameter expression="${artifact}"
     * @required
     */
    private String artifact;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        OldGraphModelFactory graphFactory = new OldGraphModelFactory( getLog() );
        // graphFactory.setVerbose( true );
        graphFactory.setArtifactFactory( artifactFactory );
        graphFactory.setArtifactRepositoryFactory( artifactRepositoryFactory );
        graphFactory.setArtifactResolver( artifactResolver );
        graphFactory.setLocalRepository( localRepository );
        // graphFactory.setMavenMetadataSource( mavenMetadataSource );

        MavenProject dummyProject;
        try
        {
            dummyProject = mavenProjectBuilder.buildStandaloneSuperProject( localRepository );
        }
        catch ( ProjectBuildingException e )
        {
            throw new MojoExecutionException( "Unable to create dummy project.", e );
        }
        graphFactory.setProject( dummyProject );

        Pattern pat = Pattern.compile( "^([^:]*):([^:]*):(.*)$" );
        Matcher mat = pat.matcher( this.artifact );
        if ( !mat.matches() )
        {
            throw new MojoExecutionException( "Format of artifact " + this.artifact
                + " is incorrect.  Use groupId:artifactId:version please." );
        }
        
        String groupId = mat.group( 1 );
        String artifactId = mat.group( 2 );
        String version = mat.group( 3 );

        GraphModel model = graphFactory.getGraphModel( groupId, artifactId, version );
        getLog().info( "Got resolved model: " + model );

        GraphGenerator generators[] = new GraphGenerator[] {
            new GraphvizGenerator(),
            new XmlGraphModelGenerator()
        };

        try
        {
            for(int g=0; g<generators.length; g++) {
                generators[g].generate(model, generators[g].getOutputName());
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to write graph file.", e );
        }
    }
}
