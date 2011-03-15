package org.apache.maven.plugin.deb;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultControlFileGenerator
{
/*
    private static final String EOL = System.getProperty( "line.separator" );

    private Set dependencies;

    private String groupId;

    private String artifactId;

    private String upstreamVersion;

    private String description;

    private String shortDescription;

    private String architecture;

    private String maintainer;

    private String packageName;

    private String priority;

    private String section;

    private int maintainerRevision;

    private String timestamp;

    private String debFileName;

    // ----------------------------------------------------------------------
    // ControlFileGenerator Implementation
    // ----------------------------------------------------------------------

    public void generateControl( File basedir )
        throws MojoFailureException, MojoExecutionException
    {
        File debian = new File( basedir, "DEBIAN" );

        if ( !debian.exists() && !debian.mkdirs() )
        {
            throw new MojoExecutionException( "Could not make directory: " + debian.getAbsolutePath() );
        }

        File control = new File( debian, "control" );

        StringWriter string = new StringWriter();
        PrintWriter output = new PrintWriter( string );

        output.println( "Section: " + getSection() );
        output.println( "Priority: " + getPriority() );
        output.println( "Maintainer: " + getMaintainer() );
        output.println( "Package: " + getDebianPackageName() );
        output.println( "Version: " + getDebianVersion() );
        output.println( "Architecture: " + getArchitecture() );
        String depends = getDepends();
        if( depends != null )
        {
            output.println( "Depends: " + depends);
        }
        output.println( "Description: " + getDebianDescription() );

        try
        {
            FileUtils.fileWrite( control.getAbsolutePath(), string.toString() );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not write to control file: '" + control + "'.", e );
        }
    }

    public String getDebFileName()
        throws MojoFailureException
    {
        if ( debFileName != null )
        {
            return debFileName;
        }

        return getGroupId() + "-" + getArtifactId() + "-" + getDebianVersionString() +  ".deb";
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setDependencies( Set dependencies )
    {
        this.dependencies = dependencies;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = StringUtils.clean( groupId );
    }

    public String getGroupId()
        throws MojoFailureException
    {
        if ( groupId.length() == 0 )
        {
            throw new MojoFailureException( "Missing required field group id." );
        }

        return groupId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = StringUtils.clean( artifactId );
    }

    public String getArtifactId()
        throws MojoFailureException
    {
        if ( artifactId.length() == 0 )
        {
            throw new MojoFailureException( "Missing required field artifact id." );
        }

        return artifactId;
    }

    public void setProjectVersion( String upstreamVersion )
    {
        this.upstreamVersion = StringUtils.clean( upstreamVersion );
    }

    public String getArchitecture()
        throws MojoFailureException
    {
        if ( architecture.length() == 0 )
        {
            throw new MojoFailureException( "Missing required field architecture." );
        }

        return architecture;
    }

    public void setDescription( String description )
    {
        this.description = StringUtils.clean( description );
    }

    public void setShortDescription( String shortDescription )
    {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription()
    {
        return StringUtils.clean( shortDescription );
    }

    public void setArchitecture( String architecture )
    {
        this.architecture = StringUtils.clean( architecture );
    }

    public String getMaintainer()
        throws MojoFailureException
    {
        if ( maintainer == null )
        {
            throw new MojoFailureException( "Missing required configuration 'maintainer'." );
        }

        return maintainer;
    }

    public void setMaintainer( String maintainer )
    {
        this.maintainer = StringUtils.clean( maintainer );
    }

    public void setPackageName( String packageName )
    {
        this.packageName = StringUtils.clean( packageName );
    }

    public String getPriority()
    {
        if ( priority == null )
        {
            return "standard";
        }

        return priority;
    }

    public void setPriority( String priority )
    {
        this.priority = priority;
    }

    public String getSection()
    {
        return section;
    }

    public void setSection( String section )
    {
        this.section = section;
    }

    public void setMaintainerRevision( int maintainerRevision )
    {
        this.maintainerRevision = maintainerRevision;
    }

    public void setTimestamp( String timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setDebFileName( String debFileName )
    {
        this.debFileName = debFileName;
    }

    // ----------------------------------------------------------------------
    // Synthetic Getters
    // ----------------------------------------------------------------------

    public String getDepends()
    {
        if ( dependencies == null || dependencies.size() <= 0 )
        {
            return null;
        }

        String depends = "";

        for( Iterator it = dependencies.iterator(); it.hasNext(); )
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

    public Version getVersion() throws MojoFailureException {
        if ( StringUtils.isEmpty(upstreamVersion) )
        {
            throw new MojoFailureException( "Missing required field: version." );
        }

        // If the maintainer revision is set, we're all good
        if ( maintainerRevision != 0 )
        {
            Version version = new Version();
            version.upstreamVersion = upstreamVersion;
            version.maintainerRevision = maintainerRevision;
            version.timestamp = timestamp;
            return version;
        }

        // Try to extract the maintainerRevision from the pom

        if( upstreamVersion.endsWith("-SNAPSHOT") )
        {
            upstreamVersion = upstreamVersion.substring(0, upstreamVersion.length() - 9);
        }

        int index = upstreamVersion.lastIndexOf('-');

        if( index == -1 )
        {
            Version version = new Version();
            version.upstreamVersion = upstreamVersion;
            version.timestamp = timestamp;
            return version;
        }

        try
        {
            Version version = new Version();
            version.upstreamVersion = upstreamVersion.substring( 0, index );
            version.maintainerRevision = Integer.parseInt( upstreamVersion.substring( index + 1 ) );
            version.timestamp = timestamp;
            return version;
        }
        catch (NumberFormatException e)
        {
            throw new MojoFailureException("Unable to parse maintainer revision out of version string. Please " +
                "either set maintainerRevision or use a version number like this: '1.1-2' or '1.0-1-SNAPSHOT'.");
        }
    }

    public String getDebianVersion()
        throws MojoFailureException
    {
        return getDebianVersionString().toLowerCase();
    }

    private String getDebianVersionString()
            throws MojoFailureException
    {
        Version version = getVersion();

        String debianVersion = version.upstreamVersion;

        if ( version.maintainerRevision > 0 )
        {
            debianVersion += "-" + version.maintainerRevision;
        }

        if ( version.timestamp != null )
        {
           debianVersion += "-" + version.timestamp;
        }

        return debianVersion;
    }

    public String getDebianPackageName()
        throws MojoFailureException
    {
        packageName = StringUtils.clean( packageName );

        if ( packageName.length() == 0 )
        {
            String name = getGroupId() + "-" + getArtifactId();

            name = name.toLowerCase();

            return name;
        }
        else
        {
            return packageName;
        }
    }

    public String getDebianDescription()
        throws MojoFailureException
    {
        // ----------------------------------------------------------------------
        // If the short description is set, use it. If not, synthesize one.
        // ----------------------------------------------------------------------

        String sd = getShortDescription();

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
*/
}
