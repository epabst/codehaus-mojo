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
package org.codehaus.mojo.pde.feature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.model.License;
import org.apache.maven.model.Organization;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.osgi.Maven2OsgiConverter;
import org.codehaus.mojo.pde.updatesite.UpdateSiteMojo;
import org.codehaus.plexus.util.WriterFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.internal.core.feature.Feature;
import org.eclipse.pde.internal.core.feature.FeatureImport;
import org.eclipse.pde.internal.core.feature.FeatureInfo;
import org.eclipse.pde.internal.core.feature.FeaturePlugin;
import org.eclipse.pde.internal.core.feature.WorkspaceFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureImport;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;

/**
 * Generate an Eclipse feature file from the dependency list
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * @requiresDependencyResolution runtime
 * @goal feature
 */
public class FeatureMojo
    extends AbstractMojo
{

    public static final String FEATURE_NAME_PROPERTY = "featureName";

    public static final String PROVIDER_NAME_PROPERTY = "providerName";

    public static final String DESCRIPTION_PROPERTY = "description";

    public static final String COPYRIGHT_PROPERTY = "copyright";

    public static final String LICENSE_URL_PROPERTY = "licenseURL";

    public static final String LICENSE_PROPERTY = "license";

    /**
     * The project we are executing.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The list of resolved dependencies from the current project. Since we're not resolving the dependencies by hand
     * here, the build will fail if some of these dependencies do not resolve.
     * 
     * @parameter default-value="${project.artifacts}"
     * @required
     * @readonly
     */
    private Collection artifacts;

    /**
     * Directory where the generated files will be saved
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Whether to add dependencies as imports or include them as part of the feature. If <code>true</code>, all Maven
     * dependencies that are not in the same groupId as this project will be added as Eclipse plugin dependencies.
     * Dependencies of type "eclipse-feature" are always imported.
     * 
     * @parameter
     */
    private boolean useImports = false;

    /**
     * @component
     */
    private Maven2OsgiConverter maven2OsgiConverter;

    /**
     * @component
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * Local repository.
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     */
    private ArtifactCollector collector;

    /**
     * @component
     */
    private ArtifactFactory factory;

    void setProject( MavenProject project )
    {
        this.project = project;
    }

    private MavenProject getProject()
    {
        return project;
    }

    void setArtifacts( Collection artifacts )
    {
        this.artifacts = artifacts;
    }

    private Collection getArtifacts()
    {
        return artifacts;
    }

    void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    private File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Feature feature = createFeature();
        writeFeature( feature );
        Properties properties = createProperties();
        writeProperties( properties );
    }

    private Feature createFeature()
        throws MojoExecutionException, MojoFailureException
    {
        Feature feature;

        feature = new Feature();

        try
        {
            IFeatureModel model = new WorkspaceFeatureModel();
            feature.setModel( model );

            feature.setId( maven2OsgiConverter.getBundleSymbolicName( getProject().getArtifact() ) );
            feature.setLabel( '%' + FEATURE_NAME_PROPERTY );
            feature.setVersion( maven2OsgiConverter.getVersion( getProject().getVersion() ) );
            feature.setProviderName( '%' + PROVIDER_NAME_PROPERTY );

            FeatureInfo description = new FeatureInfo( IFeature.INFO_DESCRIPTION );
            description.setModel( model );
            description.setDescription( '%' + DESCRIPTION_PROPERTY );
            feature.setFeatureInfo( description, IFeature.INFO_DESCRIPTION );

            FeatureInfo license = new FeatureInfo( IFeature.INFO_LICENSE );
            license.setModel( model );
            license.setURL( '%' + LICENSE_URL_PROPERTY );
            license.setDescription( '%' + LICENSE_PROPERTY );
            feature.setFeatureInfo( license, IFeature.INFO_LICENSE );

            FeatureInfo copyright = new FeatureInfo( IFeature.INFO_COPYRIGHT );
            copyright.setModel( model );
            copyright.setDescription( '%' + COPYRIGHT_PROPERTY );
            feature.setFeatureInfo( copyright, IFeature.INFO_COPYRIGHT );

            ArrayList plugins = new ArrayList( getArtifacts().size() );
            ArrayList imports = new ArrayList( getArtifacts().size() );

            DependencyNode dependencyTree;
            try
            {
                dependencyTree =
                    dependencyTreeBuilder.buildDependencyTree( project, localRepository, factory,
                                                               artifactMetadataSource, null, collector );
            }
            catch ( DependencyTreeBuilderException e )
            {
                throw new MojoExecutionException( "Unable to build dependency tree", e );
            }

            processNode( dependencyTree, model, plugins, imports );

            feature.addPlugins( (IFeaturePlugin[]) plugins.toArray( new IFeaturePlugin[plugins.size()] ) );
            feature.addImports( (IFeatureImport[]) imports.toArray( new IFeatureImport[imports.size()] ) );
        }
        catch ( CoreException e )
        {
            throw new MojoExecutionException( "Error creating the feature", e );
        }

        return feature;
    }

    private void processNode( DependencyNode dependencyNode, IFeatureModel model, List plugins, List imports )
        throws CoreException, MojoExecutionException
    {
        for ( Iterator it = dependencyNode.getChildren().iterator(); it.hasNext(); )
        {
            DependencyNode node = (DependencyNode) it.next();

            if ( node.getState() != DependencyNode.INCLUDED )
            {
                continue;
            }

            Artifact artifact = node.getArtifact();

            if ( Artifact.SCOPE_SYSTEM.equals( artifact.getScope() ) )
            {
                getLog().debug( "Ignoring system scoped artifact " + artifact );
                continue;
            }

            /* do not add children of features, Eclipse will handle that, and features are always imported */
            if ( UpdateSiteMojo.ECLIPSE_FEATURE_TYPE.equals( artifact.getType() ) )
            {
                imports.add( createFeatureImport( model, node ) );
                continue;
            }
            else

            /* if it's not a feature we can include it or depend on it */
            if ( !useImports || artifact.getGroupId().equals( getProject().getGroupId() ) )
            {
                plugins.add( createFeaturePlugin( model, node ) );
            }
            else
            {
                imports.add( createFeatureImport( model, node ) );
            }

            /* continue with the children */
            processNode( node, model, plugins, imports );
        }
    }

    private IFeaturePlugin createFeaturePlugin( IFeatureModel model, DependencyNode node )
        throws CoreException, MojoExecutionException
    {
        FeaturePlugin featurePlugin = new FeaturePlugin();
        featurePlugin.setModel( model );
        featurePlugin.setId( maven2OsgiConverter.getBundleSymbolicName( node.getArtifact() ) );
        featurePlugin.setVersion( maven2OsgiConverter.getVersion( node.getArtifact() ) );
        File file = node.getArtifact().getFile();
        if ( file == null )
        {
            /* the file was already resolved, it's just that is not available in the dependency node */
            file = new File( this.localRepository.getBasedir(), this.localRepository.pathOf( node.getArtifact() ) );
        }
        long size = file.length() / 1024;
        featurePlugin.setInstallSize( size );
        featurePlugin.setDownloadSize( size );
        featurePlugin.setUnpack( false );
        return featurePlugin;
    }

    private IFeatureImport createFeatureImport( IFeatureModel model, DependencyNode node )
        throws CoreException
    {
        FeatureImport featureImport = new FeatureImport();
        featureImport.setModel( model );
        featureImport.setId( maven2OsgiConverter.getBundleSymbolicName( node.getArtifact() ) );
        if ( UpdateSiteMojo.ECLIPSE_FEATURE_TYPE.equals( node.getArtifact().getType() ) )
        {
            featureImport.setType( IFeatureImport.FEATURE );
        }
        else
        {
            featureImport.setType( IFeatureImport.PLUGIN );
        }

        String version = node.getArtifact().getVersion();
        featureImport.setVersion( maven2OsgiConverter.getVersion( version ) );
        featureImport.setMatch( IFeatureImport.COMPATIBLE );

        return featureImport;
    }

    private void writeFeature( Feature feature )
        throws MojoExecutionException, MojoFailureException
    {
        File featureFile = new File( getOutputDirectory(), "feature.xml" );
        featureFile.getParentFile().mkdirs();
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter( WriterFactory.newXmlWriter( featureFile ) );
            feature.write( "", writer );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create feature file: " + featureFile, e );
        }
        finally
        {
            if ( writer != null )
            {
                writer.close();
            }
        }
    }

    private void put( Properties properties, String key, String value )
    {
        if ( value != null )
        {
            properties.put( key, value );
        }
    }

    private Properties createProperties()
        throws MojoExecutionException, MojoFailureException
    {
        Properties properties = new Properties();

        put( properties, FEATURE_NAME_PROPERTY, getProject().getName() );

        Organization organization = getProject().getOrganization();
        if ( organization != null )
        {
            put( properties, PROVIDER_NAME_PROPERTY, organization.getName() );
            StringBuffer sb = new StringBuffer();
            sb.append( "Copyright " );
            sb.append( organization.getName() );
            if ( organization.getUrl() != null )
            {
                sb.append( " " );
                sb.append( organization.getUrl() );
            }
            put( properties, COPYRIGHT_PROPERTY, sb.toString() );
        }

        put( properties, DESCRIPTION_PROPERTY, getProject().getDescription() );

        List licenses = getProject().getLicenses();
        if ( licenses.isEmpty() )
        {
            throw new MojoFailureException( "No license in pom. This feature will not work in an update site." );
        }
        else if ( licenses.size() > 1 )
        {
            throw new MojoFailureException( "There is more than one license in the pom, "
                + "features can only have one license" );
        }
        else
        {
            License license = (License) getProject().getLicenses().get( 0 );
            put( properties, LICENSE_URL_PROPERTY, license.getUrl() );
            put( properties, LICENSE_PROPERTY, license.getName() );
        }

        return properties;
    }

    private void writeProperties( Properties properties )
        throws MojoExecutionException, MojoFailureException
    {
        File propertiesFile = new File( getOutputDirectory(), "feature.properties" );
        propertiesFile.getParentFile().mkdirs();

        OutputStream out = null;
        try
        {
            out = new FileOutputStream( propertiesFile );

            properties.store( out, "" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create properties file: " + propertiesFile, e );
        }
        finally
        {
            if ( out != null )
            {
                try
                {
                    out.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}
