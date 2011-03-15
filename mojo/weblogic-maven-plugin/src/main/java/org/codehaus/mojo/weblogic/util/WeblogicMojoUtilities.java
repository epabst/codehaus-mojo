package org.codehaus.mojo.weblogic.util;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class contains some utilities that are useful during use of the Weblogic Mojo.
 *
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 */
public class WeblogicMojoUtilities
{

    /**
     * Creates a new WeblogicMojoUtilities object.
     */
    private WeblogicMojoUtilities()
    {
        super();
    }

    /**
     * Unsets the weblogic protocol handlers to avoid wagon https issues
     */
    public static void unsetWeblogicProtocolHandler()
    {
        if ( "weblogic.utils".equals(System.getProperty("java.protocol.handler.pkgs") ) )
        {
            System.clearProperty( "java.protocol.handler.pkgs" );
        }
    }

    /**
     * This method will contstruct the Admin URL to the given server.
     *
     * @param inProtocol   The protocol to contact the server with (i.e. t3 or http)
     * @param inServerName The name of the server to contact.
     * @param inServerPort The listen port for the server to contact.
     * @return The value of admin url.
     */
    public static String getAdminUrl( final String inProtocol, final String inServerName, final String inServerPort )
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( inProtocol ).append( "://" );
        buffer.append( inServerName );
        buffer.append( ":" ).append( inServerPort );

        return buffer.toString();
    }

    /**
     * This method will make sure there is a type appended to the file name and if it is the appropriate type for the
     * project packaging. If the project packaging is ear the artifact must end in .ear. If the project packaging is war
     * then the artifact must end in .war. If the project packaging is ejb then the artifact must end in .jar.
     *
     * @param inName             The name of the artifact.
     * @param inProjectPackaging The type of packaging for this project.
     * @return The updated artifact name.
     */
    public static String updateArtifactName( final String inName, final String inProjectPackaging )
    {
        String newName = inName;
        // If project type is ear then artifact name must end in .ear
        if ( inProjectPackaging.equalsIgnoreCase( "ear" ) )
        {
            if ( !inName.endsWith( ".ear" ) )
            {
                newName = inName.concat( ".ear" );
            }
        }
        // If project type is war then artifact name must end in .war
        else if ( inProjectPackaging.equalsIgnoreCase( "war" ) )
        {
            if ( !inName.endsWith( ".war" ) )
            {
                newName = inName.concat( ".war" );
            }
        }

        // If project type is ejb then artifact name must end in .jar
        else if ( inProjectPackaging.equalsIgnoreCase( "ejb" ) )
        {
            if ( inName.endsWith( ".ejb" ) )
            {
                newName = inName.replaceAll( "\\.ejb", ".jar" );
            }
            else if ( !inName.endsWith( ".jar" ) )
            {
                newName = inName.concat( ".jar" );
            }
        }
        // Unsupported project type
        else
        {
            throw new IllegalArgumentException( "Unsupported project packaging " + inProjectPackaging );
        }
        return newName;

    }

    /**
     * This method will get the dependencies from the pom and construct a classpath string to be used to run a mojo
     * where a classpath is required.
     *
     * @param inArtifacts The Set of artifacts for the pom being run.
     * @return A string representing the current classpath for the pom.
     */
    public static String getDependencies( final Set inArtifacts )
    {

        if ( inArtifacts == null || inArtifacts.isEmpty() )
        {
            return "";
        }
        // Loop over all the artifacts and create a classpath string.
        Iterator iter = inArtifacts.iterator();

        StringBuffer buffer = new StringBuffer();
        if ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            buffer.append( artifact.getFile() );

            while ( iter.hasNext() )
            {
                artifact = (Artifact) iter.next();
                buffer.append( System.getProperty( "path.separator" ) );
                buffer.append( artifact.getFile() );
            }
        }

        return buffer.toString();
    }

    /**
     * Returns the fully qualified path to an ear file int the artifact list.
     *
     * @param inArtifacts - the set of artifacts
     * @return the fully qualified path to an ear file int the artifact list.
     */
    public static File getEarFileName( final Set inArtifacts )
    {
        if ( inArtifacts == null || inArtifacts.isEmpty() )
        {
            throw new IllegalArgumentException( "EAR not found in artifact list." );
        }

        final Iterator iter = inArtifacts.iterator();
        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( "ear".equals( artifact.getType() ) )
            {
                return artifact.getFile();
            }
        }
        throw new IllegalArgumentException( "EAR not found in artifact list." );
    }

    /**
     * Returns the fully qualified path to a war file in the artifact list.
     *
     * @param inArtifacts - the set of artifacts
     * @return the fully qualified path to an war file in the artifact list.
     * @throws IllegalArgumentException - when a war is not found
     */
    public static File getWarFileName( final Set inArtifacts )
    {
        if ( inArtifacts == null || inArtifacts.isEmpty() )
        {
            throw new IllegalArgumentException( "WAR not found in artifact list." );
        }

        final Iterator iter = inArtifacts.iterator();
        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( "war".equals( artifact.getType() ) )
            {
                return artifact.getFile();
            }
        }
        throw new IllegalArgumentException( "WAR not found in artifact list." );
    }

    /**
     * Returns the fully qualified path to an war file in the artifact list.
     *
     * @param inArtifacts - the set of artifacts
     * @param fileName    - the file name we are looking for in the aftifact list
     * @return the fully qualified path to an war file in the artifact list.
     */
    public static File getWarFileName( final Set inArtifacts, String fileName )
    {
        if ( inArtifacts == null || inArtifacts.isEmpty() )
        {
            throw new IllegalArgumentException( "WAR not found in artifact list." );
        }

        final Iterator iter = inArtifacts.iterator();
        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();

            if ( "war".equals( artifact.getType() ) && artifact.getFile().getName().contains( fileName ) )
            {
                return artifact.getFile();
            }
        }
        throw new IllegalArgumentException( "WAR not found in artifact list." );
    }

    /**
     * Returns the ejb file type from the artifact list
     *
     * @param inArtifacts - the dependency artifacts
     * @return the File object corresponding to the ejb jar type from the artifact list
     */
    public static File getEjbJarFileName( final Set inArtifacts )
    {
        if ( inArtifacts == null || inArtifacts.isEmpty() )
        {
            throw new IllegalArgumentException( "EJB jar not found in artifact list." );
        }

        final Iterator iter = inArtifacts.iterator();
        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( "ejb".equals( artifact.getType() ) )
            {
                return artifact.getFile();
            }
        }
        throw new IllegalArgumentException( "EJB jar not found in artifact list." );
    }

    /**
     * This method will get the PLUGIN dependencies from the pom and construct a
     * classpath string to be used to run a mojo where a classpath is required.
     * <p>The plugin dependencies are placed after the project dependencies in
     * the classpath.</p>
     *
     * @param artifacts       The Set of artifacts for the pom being run.
     * @param pluginArtifacts the plugin artifacts
     * @return A string representing the current classpath for the pom.
     */
    public static String getDependencies( final Set artifacts, final List pluginArtifacts )
    {

        if ( ( artifacts == null || artifacts.isEmpty() ) &&
            ( pluginArtifacts == null || pluginArtifacts.size() == 0 ) )
        {
            return "";
        }
        // Loop over all the artifacts and create a classpath string.
        final Iterator iter = artifacts.iterator();

        final StringBuffer buffer = new StringBuffer( 1024 );
        if ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            buffer.append( artifact.getFile() );

            while ( iter.hasNext() )
            {
                artifact = (Artifact) iter.next();
                buffer.append( System.getProperty( "path.separator" ) );
                buffer.append( artifact.getFile() );
            }
        }
        //now get the plugin artifacts into the list
        final Iterator pluginIter = pluginArtifacts.iterator();
        if ( pluginIter.hasNext() )
        {
            Artifact artifact = (Artifact) pluginIter.next();
            if ( buffer.length() > 0 )
            {
                buffer.append( System.getProperty( "path.separator" ) );
            }
            buffer.append( artifact.getFile() );

            while ( pluginIter.hasNext() )
            {
                artifact = (Artifact) pluginIter.next();
                buffer.append( System.getProperty( "path.separator" ) );
                buffer.append( artifact.getFile() );
            }
        }

        return buffer.toString();
    }

}
