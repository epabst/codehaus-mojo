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
package org.codehaus.mojo.pde.updatesite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.osgi.Maven2OsgiConverter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.internal.core.isite.ISiteFeature;
import org.eclipse.pde.internal.core.isite.ISiteModel;
import org.eclipse.pde.internal.core.site.Site;
import org.eclipse.pde.internal.core.site.SiteDescription;
import org.eclipse.pde.internal.core.site.SiteFeature;
import org.eclipse.pde.internal.core.site.WorkspaceSiteModel;

/**
 * Generate an Eclipse update site file from the dependency list
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * @requiresDependencyResolution runtime
 * @goal update-site
 */
public class UpdateSiteMojo
    extends AbstractMojo
{

    public static final String ECLIPSE_FEATURE_TYPE = "eclipse-feature";

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
     * @parameter expression="${project.build.directory}/site"
     * @required
     */
    private File outputDirectory;

    /**
     * Whether to add dependencies as imports or include them as part of the feature. If <code>true</code>, all Maven
     * dependencies that are not in the same groupId as this project will be added as Eclipse plugin dependencies.
     * 
     * @parameter
     */
    private boolean useImports = false;

    /**
     * @component
     */
    private Maven2OsgiConverter maven2OsgiConverter;

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
        Site site = createSite();
        writeSite( site );
    }

    private Site createSite()
        throws MojoExecutionException, MojoFailureException
    {
        Site site = new Site();

        try
        {
            ISiteModel model = new WorkspaceSiteModel( null );

            File siteFile = new File( getOutputDirectory(), "site.xml" );

            if ( siteFile.exists() )
            {
                loadSite( model, siteFile );
            }

            site.setModel( model );

            SiteDescription siteDescription = new SiteDescription();
            siteDescription.setModel( model );
            siteDescription.setLabel( getProject().getName() );
            siteDescription.setText( getProject().getDescription() );

            site.setDescription( siteDescription );
            site.setLabel( getProject().getName() );

            Map features = new HashMap();
            ISiteFeature[] existingFeatures = model.getSite().getFeatures();
            for ( int i = 0; i < existingFeatures.length; i++ )
            {
                features.put( existingFeatures[i].getURL(), existingFeatures[i] );
            }

            for ( Iterator it = getArtifacts().iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                if ( !ECLIPSE_FEATURE_TYPE.equals( artifact.getType() ) )
                {
                    continue;
                }

                File featuresDirectory = new File( getOutputDirectory(), "features" );
                SiteFeature siteFeature = new SiteFeature();
                siteFeature.setModel( model );
                siteFeature.setId( maven2OsgiConverter.getBundleSymbolicName( artifact ) );
                siteFeature.setVersion( maven2OsgiConverter.getVersion( artifact ) );
                siteFeature.setURL( featuresDirectory.getName() + "/" +
                    maven2OsgiConverter.getBundleFileName( artifact ) );
                features.put( siteFeature.getURL(), siteFeature );

                /* copy the feature jar to the features folder */
                File from = artifact.getFile();
                File to = new File( getOutputDirectory(), siteFeature.getURL() );
                if ( to.exists() )
                {
                    to.delete();
                }
                try
                {
                    FileUtils.copyFile( from, to );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Unable to copy file from " + from.getAbsolutePath() + " to " +
                        to.getAbsolutePath(), e );
                }
            }

            site.addFeatures( (ISiteFeature[]) features.values().toArray( new ISiteFeature[features.size()] ) );
        }
        catch ( CoreException e )
        {
            throw new MojoExecutionException( "Error creating the feature", e );
        }

        return site;
    }

    private void loadSite( ISiteModel model, File siteFile )
        throws MojoExecutionException, CoreException
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream( siteFile );
            model.load( is, true );
        }
        catch ( FileNotFoundException e )
        {
            throw new MojoExecutionException( "File was not found", e );
        }
        finally
        {
            IOUtil.close( is );
        }
    }

    private void writeSite( Site site )
        throws MojoExecutionException, MojoFailureException
    {
        File siteFile = new File( getOutputDirectory(), "site.xml" );

        siteFile.getParentFile().mkdirs();
        PrintWriter writer;
        try
        {
            writer = new PrintWriter( WriterFactory.newXmlWriter( siteFile ) );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create update site file: " + siteFile, e );
        }

        site.write( "", writer );
        writer.close();
    }
}
