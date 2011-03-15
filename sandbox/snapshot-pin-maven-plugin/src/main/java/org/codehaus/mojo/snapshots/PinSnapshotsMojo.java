package org.codehaus.mojo.snapshots;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Pins all snapshot artifacts to their resolved versions.
 *
 * @goal pin-snapshots
 * @requiresDependencyResolution test
 */
public class PinSnapshotsMojo
    extends AbstractMojo
{

    /**
     * File to backup the original POM to. If null, no backups are done.
     *
     * @parameter expression="${backupPom}"
     */
    private File backupPom;

    /**
     * The resolved dependency artifacts.
     * Uses testArtifacts to ensure that we get all project artifacts.
     *
     * @parameter default-value="${project.testArtifacts}"
     * @required
     * @readonly
     */
    private List artifacts;

    /**
     * The pom.xml file (by default; this could be whatever POM is in use).
     *
     * @parameter default-value="${project.file}"
     * @required
     * @readonly
     */
    private File pomFile;

    public void execute()
        throws MojoExecutionException
    {
        Model pom = readPom();

        Map artifactsByVersionlessId = ArtifactUtils.artifactMapByVersionlessId( artifacts );

        getLog().info( "Pinning POM Dependencies." );
        int changed = pin( pom.getDependencies(), artifactsByVersionlessId );

        DependencyManagement depMgmt = pom.getDependencyManagement();
        if ( depMgmt != null )
        {
            getLog().info( "Pinning DependencyManagement (where possible)." );
            changed += pin( depMgmt.getDependencies(), artifactsByVersionlessId );
        }

        if ( changed == 0 )
        {
            getLog().info( "No dependency versions could be pinned. You may need to clean out your local repository and try again." );
        }
        else
        {
            write( pom );
        }
    }

    private void write( Model pom ) throws MojoExecutionException
    {
        if ( backupPom != null )
        {
            backupPom.getAbsoluteFile().getParentFile().mkdirs();

            try
            {
                FileUtils.copyFile( pomFile, backupPom );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Failed to make a backup copy of pom: " + pomFile + " to: " + backupPom, e );
            }
        }

        FileWriter pomWriter = null;
        try
        {
            StringWriter sWriter = new StringWriter();
            new MavenXpp3Writer().write( sWriter, pom );

            getLog().debug( "New POM is:\n\n" + sWriter.toString() + "\n\n" );

            pomWriter = new FileWriter( pomFile );
            new MavenXpp3Writer().write( pomWriter, pom );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to rewrite pom: " + pomFile + " with pinned dependency versions.", e );
        }
        finally
        {
            IOUtil.close( pomWriter );
        }
    }

    private int pin( List dependencies, Map artifactsByVersionlessId )
    {
        if ( dependencies == null )
        {
            return 0;
        }

        int changed = 0;

        for ( Iterator it = dependencies.iterator(); it.hasNext(); )
        {
            Dependency dep = (Dependency) it.next();
            if ( dep.getVersion() == null )
            {
                continue;
            }

            String key = ArtifactUtils.versionlessKey( dep.getGroupId(), dep.getArtifactId() );

            Artifact resolved = (Artifact) artifactsByVersionlessId.get( key );
            if ( resolved != null )
            {
                resolved.isSnapshot();

                if ( !dep.getVersion().equals( resolved.getVersion() ) )
                {
                    getLog().info( "Pinning version: " + dep.getVersion() + " of dependency: " + key + " to: " + resolved.getVersion() );
                    dep.setVersion( resolved.getVersion() );
                    changed++;
                }
            }
            else
            {
                getLog().warn(
                               "Cannot pin version for: " + key + ". This dependency may be listed in the local POM's "
                                               + "dependencyManagement section, but not used. Only used "
                                               + "dependencies have been resolved for this operation." );
            }
        }

        return changed;
    }

    private Model readPom()
        throws MojoExecutionException
    {
        FileReader pomReader = null;
        try
        {
            pomReader = new FileReader( pomFile );
            return new MavenXpp3Reader().read( pomReader );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to read pom from file: " + pomFile, e );
        }
        catch ( XmlPullParserException e )
        {
            throw new MojoExecutionException( "Failed to parse pom from file: " + pomFile, e );
        }
        finally
        {
            IOUtil.close( pomReader );
        }
    }
}
