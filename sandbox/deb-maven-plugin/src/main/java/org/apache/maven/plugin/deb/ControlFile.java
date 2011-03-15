package org.apache.maven.plugin.deb;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple bean representing the <code>DEBIAN/control</code> file.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ControlFile
{
    private static final String EOL = System.getProperty( "line.separator" );

    private String groupId;

    private String artifactId;

    private String upstreamVersion;

    private String timestamp;

    private boolean snapshot;

    private Set dependencies;

    // Generic

    private String packageName;

    private String packageVersion;

    private String description;

    private String shortDescription;

    // TODO: Rename to contact?
    private String maintainer;

    // This may be too Debian specific
    private int maintainerRevision;

    // Debian specific

    private String architecture;

    private String priority;

    private String section;

    public static class Version
    {
        String upstreamVersion;

        int maintainerRevision;

        String timestamp;
    }

    public ControlFile(String groupId, String artifactId, String upstreamVersion, String timestamp, boolean snapshot,
                       Set dependencies, String packageName, String packageVersion, String description,
                       String shortDescription, String maintainer, int maintainerRevision, String architecture,
                       String priority, String section)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.upstreamVersion = upstreamVersion;
        this.timestamp = timestamp;
        this.snapshot = snapshot;
        this.dependencies = dependencies;
        this.packageName = packageName;
        this.packageVersion = packageVersion;
        this.description = description;
        this.shortDescription = shortDescription;
        this.maintainer = maintainer;
        this.maintainerRevision = maintainerRevision;
        this.architecture = architecture;
        this.priority = priority;
        this.section = section;
    }

// -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public String getDepends()
    {
        if ( dependencies == null || dependencies.size() <= 0 )
        {
            return null;
        }

        String depends = "";

        for ( Iterator it = dependencies.iterator(); it.hasNext(); )
        {
            DebianDependency debianDependency = (DebianDependency) it.next();

            if ( depends.length() > 0 )
            {
                depends += ", ";
            }

            // This will happen if this is an extra dependency
            if ( StringUtils.isNotEmpty( debianDependency.getGroupId() ) )
            {
                depends += debianDependency.getGroupId() + "-";
            }

            depends += debianDependency.getArtifactId() + " ";

            // This will happen if this is an extra dependency
            if ( StringUtils.isNotEmpty( debianDependency.getVersion() ) )
            {
                depends += "(" + debianDependency.getVersion() + ")";
            }
        }

        return depends;
    }

    public ControlFile.Version getVersion()
    {
        getField( "upstreamVersion", upstreamVersion );

        // If the maintainer revision is set, we're all good
        if ( maintainerRevision != 0 )
        {
            ControlFile.Version version = new ControlFile.Version();
            version.upstreamVersion = upstreamVersion;
            version.maintainerRevision = maintainerRevision;
            version.timestamp = timestamp;
            return version;
        }

        // Try to extract the maintainerRevision from the pom

        if ( upstreamVersion.endsWith( "-SNAPSHOT" ) )
        {
            upstreamVersion = upstreamVersion.substring( 0, upstreamVersion.length() - 9 );
        }

        int index = upstreamVersion.lastIndexOf( '-' );

        if ( index == -1 )
        {
            ControlFile.Version version = new ControlFile.Version();
            version.upstreamVersion = upstreamVersion;

            if ( snapshot )
            {
                version.timestamp = getField( "timestamp", timestamp );
            }

            return version;
        }

        try
        {
            ControlFile.Version version = new ControlFile.Version();
            version.upstreamVersion = upstreamVersion.substring( 0, index );
            version.maintainerRevision = Integer.parseInt( upstreamVersion.substring( index + 1 ) );

            if ( snapshot )
            {
                version.timestamp = getField( "timestamp", timestamp );
            }
            return version;
        }
        catch ( NumberFormatException e )
        {
            throw new RuntimeException( "Unable to parse maintainer revision out of version string. Please " +
                "either set maintainerRevision or use a version number like this: '1.1-2' or '1.0-1-SNAPSHOT'." );
        }
    }

    public String getPackageName()
    {
        if ( packageName != null )
        {
            return packageName;
        }

        if ( StringUtils.isEmpty(groupId) || StringUtils.isEmpty( artifactId ) )
        {
            throw new RuntimeException( "Both group id and artifact id has to be set." );
        }

        String name = groupId + "-" + artifactId;

        name = name.toLowerCase();

        return name;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public void toFile( File basedir )
        throws IOException
    {
        File debian = new File( basedir, "DEBIAN" );

        if ( !debian.exists() && !debian.mkdirs() )
        {
            throw new IOException( "Could not make directory: " + debian.getAbsolutePath() );
        }

        File control = new File( debian, "control" );

        StringWriter string = new StringWriter();
        PrintWriter output = new PrintWriter( string );

        output.println( "Section: " + getField( "section", section ) );
        output.println( "Priority: " + getField( "priority", priority ) );
        output.println( "Maintainer: " + getField( "maintainer", maintainer ) );
        output.println( "Package: " + getPackageName() );
        output.println( "Version: " + getDebianVersionString() );
        output.println( "Architecture: " + getField( "architecture", architecture ) );
        String depends = getDepends();
        if ( depends != null )
        {
            output.println( "Depends: " + depends );
        }
        output.println( "Description: " + getDebianDescription() );

        FileUtils.fileWrite( control.getAbsolutePath(), string.toString() );
    }

    public String getDebianVersionString()
    {
        if ( packageVersion != null )
        {
            return packageVersion;
        }

        Version version = getVersion();

        String debianVersionString = version.upstreamVersion;

        if ( version.maintainerRevision > 0 )
        {
            debianVersionString += "-" + version.maintainerRevision;
        }

        if ( version.timestamp != null )
        {
            debianVersionString += "-" + version.timestamp;
        }

        return debianVersionString;
    }

    public String getDebianDescription()
    {
        // ----------------------------------------------------------------------
        // If the short description is set, use it. If not, synthesize one.
        // ----------------------------------------------------------------------

        String sd = StringUtils.clean( shortDescription );

        String d = StringUtils.clean( description );

        if ( sd.length() == 0 )
        {
            int index = d.indexOf( '.' );

            if ( index > 0 )
            {
                sd = d.substring( 0, index + 1 );

                d = d.substring( index + 1 );
            }
        }

        sd = sd.trim();
        d = d.trim();

        if ( d.length() > 0 )
        {
            d = sd + EOL + d;
        }
        else
        {
            d = sd;
        }

        // ----------------------------------------------------------------------
        // Trim each line, replace blank lines with " ."
        // ----------------------------------------------------------------------

        String debianDescription;

        try
        {
            BufferedReader reader = new BufferedReader( new StringReader( d.trim() ) );

            String line;

            debianDescription = reader.readLine();

            line = reader.readLine();

            if ( line != null )
            {
                debianDescription += EOL + " " + line.trim();

                line = reader.readLine();
            }

            while ( line != null )
            {
                line = line.trim();

                if ( line.equals( "" ) )
                {
                    debianDescription += EOL + ".";
                }
                else
                {
                    debianDescription += EOL + " " + line;
                }

                line = reader.readLine();
            }
        }
        catch ( IOException e )
        {
            // This won't happen.
            throw new RuntimeException( "Internal error", e );
        }

        return debianDescription;
    }

    private String getField(String field, String value)
    {
        if ( StringUtils.isEmpty( value ) )
        {
            throw new RuntimeException( "Missing required field: " + field );
        }

        return value;
    }
}
