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

package org.codehaus.mojo.ianal

import org.codehaus.groovy.maven.mojo.GroovyMojo

import java.util.zip.ZipFile
import java.util.zip.ZipException

import org.apache.maven.project.MavenProject

/**
 * Verify that legal files are in all attached zip-encoded artifacts.
 *
 * @goal verify-legal-files
 * @phase verify
 * @since 1.0-alpha-1
 *
 * @version $Id$
 */
class VerifyLegalFilesMojo
    extends GroovyMojo
{
    /**
     * Set to true to disable verification.
     *
     * @parameter default-value="false"
     * @since 1.0-alpha-1
     */
    boolean skip
    
    /**
     * When set to true, fail the build when no legal files are found.
     *
     * @parameter default-value="false"
     * @since 1.0-alpha-1
     */
    boolean strict
    
    /**
     * The list of paths to search for legal files.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    String[] searchPaths
    
    /**
     * The list of required legal files.  Default is <tt>LICENSE</tt> and <tt>NOTICE</tt>.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    String[] requiredFiles = [ 'LICENSE', 'NOTICE' ]
    
    /**
     * the list of alternative acceptable file extentions for legal files.
     *
     * @parameter
     * @since 1.0-alpha-1
     */
    String[] acceptableExtentions = [ 'txt', 'rtf' ]
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    void execute() {
        if (!searchPaths) {
            searchPaths = [ 'META-INF', project.build.finalName ]
        }
        
        def artifacts = []
        
        artifacts << project.artifact
        artifacts.addAll(project.attachedArtifacts)
        
        if (skip) {
            def method = log.&warn
            
            // Use debug logging if this is a pom w/no attached artifacts
            if (project.packaging == 'pom' && project.attachedArtifacts.empty) {
                method = log.&debug
            }
            
            method("Skipping verification of legal files in artifacts:")
            artifacts.each {
                method("    $it")
            }
            return
        }
        
        artifacts.each { artifact ->
            // Some artifacts might not have files, so skip them
            if (artifact.file == null) {
                log.debug("Skipping artifact; no attached file: $artifact")
            }
            else {
                try {
                    def zip = new ZipFile(artifact.file)
                    // If not a zip file, then an exception would have been thrown
                    
                    log.info("Checking legal files in: ${artifact.file.name}")
                    
                    def containsLegalFiles = { basedir ->
                        boolean found = false
                        
                        for (name in requiredFiles) {
                            def filename = "${basedir}/${name}"
                            
                            log.debug("Inspecting for legal files: $filename")
                            
                            found = zip.getEntry(filename) != null
                            
                            if (!found) {
                                // Check for acceptable extentions
                                for (ext in acceptableExtentions) {
                                    found = zip.getEntry("${filename}.${ext}") != null
                                    if (found) {
                                        break
                                    }
                                }
                            }
                        }
                        
                        return found
                    }
                    
                    def checkLegalFiles = {
                        boolean found = false
                        
                        for (path in searchPaths) {
                            found = containsLegalFiles(path)
                            if (found) {
                                break
                            }
                        }
                        
                        return found
                    }
                    
                    if (!checkLegalFiles()) {
                        String msg = "Artifact does not contain any legal files: ${artifact.file.name}"
                        if (strict) {
                            fail(msg)
                        }
                        else {
                            log.warn(msg)
                        }
                    }
                }
                catch (ZipException e) {
                    log.debug("Failed to check file for legal muck; ignoring: ${artifact.file}", e)
                }
            }
        }
    }
}
