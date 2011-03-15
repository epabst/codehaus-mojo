/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
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

package org.codehaus.mojo.retrotranslator

import net.sf.retrotranslator.transformer.Retrotranslator

import org.apache.maven.archiver.MavenArchiveConfiguration
import org.apache.maven.archiver.MavenArchiver

import org.codehaus.plexus.archiver.war.WarArchiver
import org.codehaus.plexus.util.DirectoryScanner
import org.codehaus.plexus.util.FileUtils

/**
 * Retrotranslates the classes in the war, as well as all jars in WEB-INF/lib.
 * Creates a new war with the specified classifier with these retrotranslations.
 * 
 * @goal translate-war
 * @phase package
 *
 * @version $Id$
 */
class TranslateWarMojo
    extends AttachingMojoSupport
{
    //
    // TODO: Maybe use a FileSet here instead... ?
    //
    
    /**
     * A set of jar files to include in the translation.  Note: any basedir will
     * be ignored and reset to WEB-INF/lib
     *
     * @parameter
     */
    DirectoryScanner jarfileset
    
    File transformedWarDir
    
    //
    // Components
    //
    
    /**
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#war}"
     * @required
     * @readonly
     */
    WarArchiver warArchiver
    
    //
    // Mojo
    //
    
    void execute() {
        if (project.packaging != 'war') {
            log.debug('Not executing on non-WAR project')
            return
        }
        
        // Create a copy of the exploded war directory - we will perform translation on this directory
        def warDir = new File(outputDirectory, project.build.finalName)
        if (!warDir.exists() || !warDir.isDirectory()) {
            fail("Invalid WAR build directory: $warDir")
        }
        
        transformedWarDir = new File(outputDirectory, "${baseName}-${classifier}")
        FileUtils.copyDirectoryStructure(warDir, transformedWarDir)

        // Do the actual translation
        super.execute()

        // Create the transformed war file
        def outWar = new File(outputDirectory, "${baseName}-${classifier}.war")

        def archive = new MavenArchiveConfiguration()
        archive.addMavenDescriptor = true

        def archiver = new MavenArchiver()
        archiver.archiver = warArchiver
        archiver.outputFile = outWar
        warArchiver.addDirectory(transformedWarDir)
        warArchiver.webxml = new File(transformedWarDir, 'WEB-INF/web.xml')
        archiver.createArchive(project, archive)
        
        // if attach specified, attach the artifact
        if (attach) {
            projectHelper.attachArtifact(project, 'war', classifier, outWar)
        }
    }

    protected void configureRetrotranslator(Retrotranslator trans) {
        // add the classes directory
        trans.addSrcdir(new File(transformedWarDir, 'WEB-INF/classes'))

        // if no jarfileset specified, create a default one...including all jar files
        if (jarfileset) {
            jarfileset = new DirectoryScanner()
            jarfileset.setIncludes([ '*.jar' ])
        }
        
        // setup the basedir for the jarfileset
        jarfileset.basedir = new File(transformedWarDir, 'WEB-INF/lib')
        jarfileset.scan()

        def jarFiles = jarfileset.getIncludedFiles()

        // add all of the jars to translator
        jarFiles.each {
            trans.addSrcjar(new File(jarfileset.basedir, it))
        }
    }
}
