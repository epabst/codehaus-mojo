/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.mojo.shitty

import org.apache.maven.project.artifact.ProjectArtifactMetadata

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.factory.ArtifactFactory
import org.apache.maven.artifact.installer.ArtifactInstallationException
import org.apache.maven.artifact.installer.ArtifactInstaller
import org.apache.maven.artifact.repository.ArtifactRepository

import org.codehaus.plexus.digest.Digester
import org.codehaus.plexus.digest.DigesterException
import org.codehaus.plexus.digest.Md5Digester
import org.codehaus.plexus.digest.Sha1Digester

import java.security.NoSuchAlgorithmException

/**
 * Installs project artifacts in local repository for super helpful integration testing.
 *
 * This is <strong>NOT</strong> intended to replace the normal <tt>maven-install-plugin</tt>,
 * it is only here to allow the SHITTY test invocations to be configured with a known
 * set of versioned artifacts to reduce any chances of testing the wrong bits.
 *
 * @goal install
 * @phase pre-integration-test
 * @since 1.0-alpha-1
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class InstallMojo
    extends ShittyMojoSupport
{
    /**
     * Enable or disable artifact checksum creation.
     *
     * @parameter expression="${createChecksum}" default-value="true"
     */
    boolean createChecksum
    
    /**
     * The version which will be used to install artifacts for integration testing.
     *
     * @parameter expression="${version}" default-value="testing"
     */
    String version
    
    //
    // Components
    //

    /**
     * @component
     * @required
     * @readonly
     */
    ArtifactFactory artifactFactory

    /**
     * @component
     * @required
     * @readonly
     */
    ArtifactInstaller installer

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    ArtifactRepository localRepository

    /**
     * @component role-hint="md5"
     */
    Digester md5Digester

    /**
     * @component role-hint="sha1"
     */
    Digester sha1Digester
    
    //
    // Mojo
    //
    
    void execute() {
        def pomFile = project.file
        def artifact = project.artifact
        
        if (version) {
            artifact = createArtifactWithVersion(artifact, version)
        }
        
        try {
            if (project.packaging == 'pom') {
                installer.install(pomFile, artifact, localRepository)
            }
            else {
                def metadata = new ProjectArtifactMetadata(artifact, pomFile)
                artifact.addMetadata(metadata)

                if (artifact.file && !artifact.file.isDirectory()) {
                    installer.install(artifact.file, artifact, localRepository)

                    if (createChecksum) {
                        def pom = new File(localRepository.basedir, localRepository.pathOfLocalRepositoryMetadata(metadata, localRepository))
                        
                        installCheckSum(pom, null, true)
                        installCheckSum(artifact.file, artifact, false)
                    }
                }
                else {
                    fail('The packaging for this project did not assign a file to the build artifact')
                }
            }
            
            // Install all attached artifacts
            project.attachedArtifacts.each { attached ->
                if (version) {
                    attached = createArtifactWithVersion(attached, version)
                }
                
                installer.install(attached.file, attached, localRepository)

                if (createChecksum) {
                    installCheckSum(attached.file, attached, false)
                }
            }
        }
        catch (ArtifactInstallationException e) {
            fail(e)
        }
    }
    
    private Artifact createArtifactWithVersion(Artifact originalArtifact, String version) {
        version = version.trim()
        
        log.debug("Using version '$version' for artifact: $originalArtifact")
        
        // Clone the artifact with a new version
        Artifact artifact = artifactFactory.createArtifactWithClassifier(
            originalArtifact.groupId,
            originalArtifact.artifactId,
            version,
            originalArtifact.type,
            originalArtifact.classifier
        )
        
        // Propagate the artifact file if there is one
        artifact.file = originalArtifact.file
        
        return artifact
    }

    protected void installCheckSum(File file, Artifact artifact, boolean isPom) {
        assert file
        // artifact may be null
        
        try {
            def destination
            if (isPom) {
                destination = file
            }
            else {
                def localPath = localRepository.pathOf(artifact)
                destination = new File(localRepository.basedir, localPath)
            }
            
            def install = { algo ->
                // Create the checksum value
                def checksum = getChecksum(file, algo)
                
                // Make sure the install path exists
                if (!destination.parentFile.exists()) {
                    destination.parentFile.mkdirs()
                }
                
                // Figure out what the suffix should be (strip off "-" and make lower)
                def suffix = (algo - '-').toLowerCase()
                
                // Install the checksum file
                def tmp = new File("${destination}.${suffix}")
                tmp << checksum
            }
            
            log.debug("Installing checksum for: $destination")
            
            [ 'MD5', 'SHA-1' ].each {
                install(it)
            }
        }
        catch (Exception e) {
            fail('Failed to create or install checksum', e)
        }
    }

    protected String getChecksum(File file, String algo) {
        assert file
        assert algo
        
        switch (algo) {
            case 'MD5':
                return md5Digester.calc(file)
            
            case 'SHA-1':
                return sha1Digester.calc(file)
            
            default:
                throw new NoSuchAlgorithmException("No support for algorithm: $algo")
        }
    }
}
