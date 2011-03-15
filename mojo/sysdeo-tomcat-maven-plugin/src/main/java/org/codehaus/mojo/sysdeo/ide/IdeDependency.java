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


import java.io.File;

import org.apache.maven.artifact.Artifact;

/**
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class IdeDependency
{
    private String ideProjectName;

    /**
     * Is this dependency available in the reactor?
     */
    private boolean referencedProject;

    /**
     * Is this a test dependency?
     */
    private boolean testDependency;

    /**
     * Is this a system scope dependency?
     */
    private boolean systemScoped;

    /**
     * Is this a provided dependency?
     */
    private boolean provided;

    /**
     * Is this dependency added to classpath?
     */
    private boolean addedToClasspath;

    /**
     * Resolved artifact file.
     */
    private File file;

    /**
     * Resolved javadoc file.
     */
    private File javadocAttachment;

    /**
     * Resolved source file.
     */
    private File sourceAttachment;

    /**
     * Group id.
     */
    private String groupId;

    /**
     * Artifact id.
     */
    private String artifactId;

    /**
     * Artifact version.
     */
    private String version;

    /**
     * Artifact type.
     */
    private String type;

    /**
     * Artifact version.
     */
    private String classifier;

    /**
     * The Output directory as configured in the IDE
     */
    private String outputDirectory;

    private String testOutputDirectory;

    public IdeDependency()
    {
        super();
    }

    public IdeDependency( Artifact art, boolean isReactorProject )
    {
        this( art.getGroupId(), art.getArtifactId(), getVersion( art ), art.getClassifier(), art.getScope(),
              isReactorProject, art.getArtifactHandler().isAddedToClasspath(),
              art.getFile(), art.getType(), null, null );
    }

    private static String getVersion( Artifact art )
    {
        // detect TIMESTAMP SNAPSHOTS
        art.isSnapshot();
        return art.getVersion();
    }

    /**
     * @param groupId Group id
     * @param artifactId Artifact id
     * @param version Artifact version
     * @param referencedProject Is this dependency available in the reactor?
     * @param testDependency Is this a test dependency?
     * @param systemScoped Is this a system scope dependency?
     * @param provided Is this a provided dependency?
     * @param addedToClasspath Is this dependency added to classpath?
     * @param file Resolved artifact file
     * @param type Artifact type
     * @param outputDirectory the project output directory (as configured in the IDE)
     * @param testOutputDirectory TODO
     */
    public IdeDependency( String groupId, String artifactId, String version, String classifier, String scope,
                          boolean referencedProject, boolean addedToClasspath,
                          File file, String type, String outputDirectory, String testOutputDirectory )
    {
        // group:artifact:version
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        setScope( scope );

        // flags
        this.referencedProject = referencedProject;
        this.addedToClasspath = addedToClasspath;

        // file and type
        this.file = file;
        this.type = type;

        // IDE output directory
        this.outputDirectory = outputDirectory != null ? outputDirectory : "target/classes";
        this.testOutputDirectory = testOutputDirectory != null ? testOutputDirectory : "target/test-classes";
    }

    public void setScope( String scope )
    {
        this.testDependency = Artifact.SCOPE_TEST.equals( scope );
        this.systemScoped = Artifact.SCOPE_SYSTEM.equals( scope );
        this.provided = Artifact.SCOPE_PROVIDED.equals( scope );

    }

    /**
     * Getter for <code>javadocAttachment</code>.
     *
     * @return Returns the javadocAttachment.
     */
    public File getJavadocAttachment()
    {
        return this.javadocAttachment;
    }

    /**
     * Setter for <code>javadocAttachment</code>.
     *
     * @param javadocAttachment The javadocAttachment to set.
     */
    public void setJavadocAttachment( File javadocAttachment )
    {
        this.javadocAttachment = javadocAttachment;
    }

    /**
     * Getter for <code>artifactId</code>.
     *
     * @return Returns the artifactId.
     */
    public String getArtifactId()
    {
        return this.artifactId;
    }

    /**
     * Setter for <code>artifactId</code>.
     *
     * @param artifactId The artifactId to set.
     */
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    /**
     * Getter for <code>groupId</code>.
     *
     * @return Returns the groupId.
     */
    public String getGroupId()
    {
        return this.groupId;
    }

    /**
     * Setter for <code>groupId</code>.
     *
     * @param groupId The groupId to set.
     */
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    /**
     * Getter for <code>version</code>.
     *
     * @return Returns the version.
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * Setter for <code>version</code>.
     *
     * @param version The version to set.
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * Getter for <code>referencedProject</code>.
     *
     * @return Returns the referencedProject.
     */
    public boolean isReferencedProject()
    {
        return this.referencedProject;
    }

    /**
     * Setter for <code>referencedProject</code>.
     *
     * @param referencedProject The referencedProject to set.
     */
    public void setReferencedProject( boolean referencedProject )
    {
        this.referencedProject = referencedProject;
    }

    /**
     * Getter for <code>sourceAttachment</code>.
     *
     * @return Returns the sourceAttachment.
     */
    public File getSourceAttachment()
    {
        return this.sourceAttachment;
    }

    /**
     * Setter for <code>sourceAttachment</code>.
     *
     * @param sourceAttachment The sourceAttachment to set.
     */
    public void setSourceAttachment( File sourceAttachment )
    {
        this.sourceAttachment = sourceAttachment;
    }

    /**
     * Getter for <code>systemScoped</code>.
     *
     * @return Returns the systemScoped.
     */
    public boolean isSystemScoped()
    {
        return this.systemScoped;
    }

    /**
     * Setter for <code>systemScoped</code>.
     *
     * @param systemScoped The systemScoped to set.
     */
    public void setSystemScoped( boolean systemScoped )
    {
        this.systemScoped = systemScoped;
    }

    /**
     * Getter for <code>testDependency</code>.
     *
     * @return Returns the testDependency.
     */
    public boolean isTestDependency()
    {
        return this.testDependency;
    }

    /**
     * Setter for <code>testDependency</code>.
     *
     * @param testDependency The testDependency to set.
     */
    public void setTestDependency( boolean testDependency )
    {
        this.testDependency = testDependency;
    }

    /**
     * Getter for <code>file</code>.
     *
     * @return Returns the file.
     */
    public File getFile()
    {
        return this.file;
    }

    /**
     * Setter for <code>file</code>.
     *
     * @param file The file to set.
     */
    public void setFile( File file )
    {
        this.file = file;
    }

    /**
     * Getter for <code>artifactId</code>.
     *
     * @return Returns the artifactId.
     */
    public String getId()
    {
        String id = this.groupId + ':' + this.artifactId + ':' + this.version;
        if (classifier != null)
        {
            id += ':' + classifier;
        }
        return id;
    }

    /**
     * Getter for <code>type</code>.
     *
     * @return Returns the type.
     */
    public String getType()
    {
        return type != null ? type : "jar";
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( getId() == null ) ? 0 : getId().hashCode() );
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        IdeDependency other = (IdeDependency) obj;
        if ( !getId().equals( other.getId() ) )
        {
            return false;
        }
        return true;
    }

    /**
     * Setter for <code>type</code>.
     *
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * Getter for <code>addedToClasspath</code>.
     *
     * @return Returns the addedToClasspath.
     */
    public boolean isAddedToClasspath()
    {
        return this.addedToClasspath;
    }

    /**
     * Setter for <code>addedToClasspath</code>.
     *
     * @param addedToClasspath The addedToClasspath to set.
     */
    public void setAddedToClasspath( boolean addedToClasspath )
    {
        this.addedToClasspath = addedToClasspath;
    }

    /**
     * Getter for <code>provided</code>.
     *
     * @return Returns the provided.
     */
    public boolean isProvided()
    {
        return this.provided;
    }

    /**
     * Setter for <code>provided</code>.
     *
     * @param provided The provided to set.
     */
    public void setProvided( boolean provided )
    {
        this.provided = provided;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @param outputDirectory the outputDirectory to set
     */
    public void setOutputDirectory( String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getId();
    }

    /**
     * @return the testOutputDirectory
     */
    public String getTestOutputDirectory()
    {
        return testOutputDirectory;
    }

    /**
     * @param testOutputDirectory the testOutputDirectory to set
     */
    public void setTestOutputDirectory( String testOutputDirectory )
    {
        this.testOutputDirectory = testOutputDirectory;
    }

    /**
     * @return the ideProjectName
     */
    public String getIdeProjectName()
    {
        if ( ideProjectName == null )
        {
            return artifactId;
        }
        return ideProjectName;
    }

    /**
     * @param ideProjectName the ideProjectName to set
     */
    public void setIdeProjectName( String ideProjectName )
    {
        this.ideProjectName = ideProjectName;
    }

    /**
     * @param classifier the classifier to set
     */
    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    /**
     * @return the classifier
     */
    public String getClassifier()
    {
        return classifier;
    }

}
