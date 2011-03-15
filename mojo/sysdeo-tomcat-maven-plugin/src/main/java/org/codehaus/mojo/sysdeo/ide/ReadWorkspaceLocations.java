package org.codehaus.mojo.sysdeo.ide;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.internal.localstore.SafeChunkyInputStream;

/**
 * Copy/paste from the maven-eclipse-plugin
 * <p>
 * Scan the eclipse workspace and create a array with {@link IdeDependency} for all found artefacts.
 *
 * @author Richard van Nieuwenhoven
 * @version $Id$
 */
public class ReadWorkspaceLocations
{

    private static final String BINARY_LOCATION_FILE = ".location";

    private static final String METADATA_PLUGINS_ORG_ECLIPSE_CORE_RESOURCES_PROJECTS =
        ".metadata/.plugins/org.eclipse.core.resources/.projects";

    private static final String[] PARENT_GROUP_ID = new String[] { "parent", "groupId" };

    private static final String[] PARENT_VERSION = new String[] { "parent", "version" };

    private static final String[] PARENT_CLASSIFIER = new String[] { "parent", "classifier" };

    private static final String[] PACKAGING = new String[] { "packaging" };

    private static final String[] ARTEFACT_ID = new String[] { "artifactId" };

    private static final String[] GROUP_ID = new String[] { "groupId" };

    private static final String[] VERSION = new String[] { "version" };

    private static final String[] CLASSIFIER = new String[] { "classifier" };


    /**
     * Get the project location for a project in the eclipse metadata.
     *
     * @param workspaceLocation the location of the workspace
     * @param project the project subdirectory in the metadata
     * @return the full path to the project.
     */
    private String getProjectLocation( File workspaceLocation, File project )
    {
        String projectLocation = null;
        File location = new File( project, ReadWorkspaceLocations.BINARY_LOCATION_FILE );
        if ( location.exists() )
        {
            try
            {
                SafeChunkyInputStream fileInputStream = new SafeChunkyInputStream( location );
                DataInputStream dataInputStream = new DataInputStream( fileInputStream );
                String file = dataInputStream.readUTF().trim();

                if ( file.length() > 0 )
                {
                    file = file.substring( file.indexOf( ':' ) + 1 );
                    while ( !Character.isLetterOrDigit( file.charAt( 0 ) ) )
                    {
                        file = file.substring( 1 );
                    }
                    if ( file.indexOf( ':' ) < 0 )
                    {
                        file = File.separator + file;
                    }
                    projectLocation = file;
                }

            }
            catch ( FileNotFoundException e )
            {
                projectLocation = "unknown";
            }
            catch ( IOException e )
            {
                projectLocation = "unknown";
            }
        }
        if ( projectLocation == null )
        {
            File projectBase = new File( workspaceLocation, project.getName() );
            if ( projectBase.isDirectory() )
            {
                projectLocation = projectBase.getAbsolutePath();
            }
        }
        return projectLocation;
    }

    /**
     * get a value from a dom element.
     *
     * @param element the element to get a value from
     * @param elementNames the sub elements to get
     * @param defaultValue teh default value if the value was null or empty
     * @return the value of the dome element.
     */
    private String getValue( Xpp3Dom element, String[] elementNames, String defaultValue )
    {
        String value = null;
        Xpp3Dom dom = element;
        for ( int index = 0; dom != null && index < elementNames.length; index++ )
        {
            dom = dom.getChild( elementNames[index] );
        }
        if ( dom != null )
        {
            value = dom.getValue();
        }
        if ( value == null || value.trim().length() == 0 )
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }

    /**
     * Read the artefact information from the pom in the project location and the eclipse project name from the .project
     * file.
     *
     * @param projectLocation the location of the project
     * @param logger the logger to report errors and debug info.
     * @return an {@link IdeDependency} or null.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private IdeDependency readArtefact( String projectLocation, Log logger )
        throws XmlPullParserException, IOException
    {
        File baseDir = new File( projectLocation );
        File projectFile = new File( baseDir, ".project" );
        String eclipseProjectName = baseDir.getName();
        if ( projectFile.exists() )
        {
            Xpp3Dom project = Xpp3DomBuilder.build( new FileReader( projectFile ) );
            eclipseProjectName = getValue( project, new String[] { "name" }, eclipseProjectName );
        }
        File pomFile = new File( baseDir, "pom.xml" );
        if ( pomFile.exists() )
        {
            Xpp3Dom pom = Xpp3DomBuilder.build( new FileReader( pomFile ) );

            String artifact = getValue( pom, ARTEFACT_ID, null );
            String group = getValue( pom, GROUP_ID, getValue( pom, PARENT_GROUP_ID, null ) );
            String version = getValue( pom, VERSION, getValue( pom, PARENT_VERSION, null ) );
            String classifier = getValue( pom, CLASSIFIER, getValue( pom, PARENT_CLASSIFIER, null ) );
            String packaging = getValue( pom, PACKAGING, "jar" );

            logger.debug( "found workspace artefact " + group + ":" + artifact + ":" + version + " " + packaging + " ("
                + eclipseProjectName + ")" + " -> " + projectLocation );

            String output = getOutputDirectory( baseDir );

            return new IdeDependency( group, artifact, version, classifier, null, true, false, null,
                packaging, output, null );
        }
        else
        {
            logger.debug( "ignored workspace project NO pom available " + projectLocation );
            return null;
        }
    }

    public static String getOutputDirectory( File baseDir )
    {
        String output = null;
        try
        {
            File dotClasspath = new File( baseDir, ".classpath" );
            if ( dotClasspath.exists() )
            {
                Xpp3Dom cp = Xpp3DomBuilder.build( new FileReader( dotClasspath ) );

                int count = cp.getChildCount();
                for ( int i = 0; i < count; i++ )
                {
                    Xpp3Dom entry = cp.getChild( i );
                    if ( "output".equals( entry.getAttribute( "kind" ) ) )
                    {
                        output = entry.getAttribute( "path" );
                        break;
                    }
                }
            }
        }
        catch ( Exception e )
        {
            // Failed to parse .classpath
        }
        return output;
    }

    /**
     * Scan the eclipse workspace and create a array with {@link IdeDependency} for all found artifacts.
     *
     * @param workspaceLocation the location of the eclipse workspace.
     * @param logger the logger to report errors and debug info.
     */
    public List readWorkspace( File workspacePath, Log logger )
    {
        ArrayList dependencys = new ArrayList();
        if ( workspacePath != null )
        {
            File workspace =
                new File( workspacePath, ReadWorkspaceLocations.METADATA_PLUGINS_ORG_ECLIPSE_CORE_RESOURCES_PROJECTS );

            File[] directories = workspace.listFiles();
            for ( int index = 0; directories != null && index < directories.length; index++ )
            {
                File project = directories[index];
                if ( project.isDirectory() )
                {
                    try
                    {
                        String projectLocation = getProjectLocation( workspacePath, project );
                        if ( projectLocation != null )
                        {
                            IdeDependency ideDependency = readArtefact( projectLocation, logger );
                            if ( ideDependency != null )
                            {
                                logger.debug( "Read workspace project " + ideDependency );
                                ideDependency.setIdeProjectName( project.getName() );
                                dependencys.add( ideDependency );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        logger.warn( "could not read workspace project:" + project, e );
                    }
                }
            }
        }
        return dependencys;
    }
}
