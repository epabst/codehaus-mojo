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

import org.apache.maven.shared.model.fileset.FileSet
import org.apache.maven.shared.model.fileset.util.FileSetManager

/**
 * Cleans generated output from super helpful integration tests.
 *
 * @goal clean
 * @phase validate
 * @since 1.0-alpha-1
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class CleanMojo
    extends ShittyMojoSupport
{
    /**
     * Extra files to be deleted in addition to the default directories.
     * Defaults to all <tt>target</tt> directories and <tt>build.log</tt> files under <tt>src/it</tt>.
     *
     * @parameter
     */
    FileSet[] filesets
    
    //
    // Mojo
    //
    
    void execute() {
        def fsm = new FileSetManager(log, log.debugEnabled)
        
        // Then if given delete the additional files specified by the filesets
        getFilesets().each { fileset ->
            fileset = resolveFileSet(fileset)
            
            fsm.delete(fileset)
        }
    }
    
    private FileSet[] getFilesets() {
        // If no filesets were configured, then setup the default
        if (!filesets) {
            def fileset = new FileSet(directory: 'src/it')
            fileset.addInclude('**/target')
            fileset.addInclude('**/build.log')
            
            return [ fileset ] as FileSet[]
        }
        
        return filesets
    }
}
