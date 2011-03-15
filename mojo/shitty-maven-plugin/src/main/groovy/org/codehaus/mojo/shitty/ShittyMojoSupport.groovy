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

import org.codehaus.groovy.maven.mojo.GroovyMojo

import org.apache.maven.project.MavenProject

import org.apache.maven.shared.model.fileset.FileSet

/**
 * Support for super helpful integration test mojos.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
abstract class ShittyMojoSupport
    extends GroovyMojo
{
    //
    // Components
    //
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    //
    // Support
    //
    
    /**
     * The file-management API is kinda dumb doesn't actually use files for holding data
     * and does not properly resolve them relatively to the Maven project, so we have to
     * do that here to make it work in the reactor.
     */
    protected FileSet resolveFileSet(FileSet fileset) {
        assert fileset
        
        def basedir = new File(fileset.directory)
        if (!basedir.absolute) {
            basedir = new File(project.basedir, fileset.directory)
            
            // Update the fileset so the FSM can properly resolve crap
            fileset.directory = basedir.canonicalPath
        }
        
        return fileset
    }
}
