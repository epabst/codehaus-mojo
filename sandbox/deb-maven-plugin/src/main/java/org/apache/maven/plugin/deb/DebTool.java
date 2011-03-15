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

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import java.util.Set;

/**
 * TODO: Copy everything that is common between RPM, DEB and PKG into a UnixPkgTool.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: ControlFileGenerator.java 7203 2008-07-03 06:48:35Z trygvis $
 */
public class DebTool
{
    // -----------------------------------------------------------------------
    // Common parts for all unix formats
    // -----------------------------------------------------------------------

    // Maventr
    private String groupId;

    private String artifactId;

    private String classifier;

    private String upstreamVersion;

    private String timestamp;

    private boolean snapshot;

    private Set dependencies;

    // Generic

    // Set to override package name generation
    private String packageName;

    // Set to override package version generation
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

    public DebTool mavenProject( String groupId, String artifactId, String classifier, String upstreamVersion,
                                 String timestamp, boolean snapshot )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.classifier = classifier;
        this.upstreamVersion = upstreamVersion;
        this.timestamp = timestamp;
        this.snapshot = snapshot;
        return this;
    }

    public DebTool dependencies( Set dependencies )
    {
        this.dependencies = dependencies;
        return this;
    }

    // -----------------------------------------------------------------------
    // Generic
    // -----------------------------------------------------------------------

    public DebTool packageName( String packageName )
    {
        this.packageName = packageName;
        return this;
    }

    public DebTool description( String description )
    {
        this.description = description;
        return this;
    }

    public DebTool shortDescription( String shortDescription )
    {
        this.shortDescription = shortDescription;
        return this;
    }

    public DebTool maintainer( String maintainer )
    {
        this.maintainer = maintainer;
        return this;
    }

    public DebTool maintainerRevision( int maintainerRevision )
    {
        this.maintainerRevision = maintainerRevision;
        return this;
    }

    public DebTool architecture( String architecture )
    {
        this.architecture = architecture;
        return this;
    }

    public DebTool priority( String priority )
    {
        this.priority = priority;
        return this;
    }

    public DebTool section( String section )
    {
        this.section = section;
        return this;
    }

    ControlFile generateControlFile()
        throws MojoFailureException
    {
        return new ControlFile(groupId, artifactId, upstreamVersion, timestamp, snapshot, dependencies, packageName,
            packageVersion, description, shortDescription, maintainer, maintainerRevision, architecture, priority,
            section);
    }

    public String getDebFileName()
        throws MojoFailureException
    {
        classifier = StringUtils.clean( classifier );

        ControlFile controlFile = generateControlFile();

        return groupId + "-" +
            artifactId +
            (classifier.length() > 0 ? "-" + classifier : "") +
             "-" + controlFile.getDebianVersionString() + ".deb";
    }
}
