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

import org.codehaus.plexus.util.FileUtils

import org.apache.maven.shared.model.fileset.FileSet
import org.apache.maven.shared.model.fileset.util.FileSetManager

/**
 * Retrotranslates jars and classes.
 * 
 * @goal translate
 * @phase process-classes
 *
 * @version $Id$
 */
class TranslateMojo
    extends RetrotranslateMojoSupport
{
    /**
     * The directory to place translated classes.
     * 
     * @parameter expression="${destdir}"
     */
    File destdir

    /**
     * The JAR file to place translated classes.
     * 
     * @parameter expression="${destjar}"
     */
    File destjar

    /**
     * Files to include in the translation.
     *
     * @parameter
     */
    FileSet[] filesets

    /**
     * Jar files to include in the translation.
     *
     * @parameter
     */
    FileSet[] jarfilesets

    /**
     * Directories to include in the translation.
     *
     * @parameter
     */
    FileSet[] dirsets

    protected void configureRetrotranslator(Retrotranslator trans) {
        assert trans
        
        FileSetManager fsm = new FileSetManager(log, log.debugEnabled)

        if (filesets) {
            filesets.each {
                def basedir = new File(it.directory)
                def includes = fsm.getIncludedFiles(it)

                trans.addSourceFiles(basedir, includes.toList())
            }
        }

        if (jarfilesets) {
            jarfilesets.each {
                def basedir = new File(it.directory)
                def includes = fsm.getIncludedFiles(it)
                
                includes.each { filename ->
                    def file = new File(basedir, filename)
                    trans.addSrcjar(file)
                }
            }
        }

        if (dirsets) {
            dirsets.each {
                def basedir = new File(it.directory)
                def includes = fsm.getIncludedDirectories(it)
                
                includes.each { dirname ->
                    def dir = new File(basedir, dirname)
                    trans.addSrcdir(dir)
                }
            }
        }

        if (destdir) {
            FileUtils.forceMkdir(destdir)
            trans.destdir = destdir
        }

        if (destjar) {
            trans.destjar = destjar
        }
    }
}
