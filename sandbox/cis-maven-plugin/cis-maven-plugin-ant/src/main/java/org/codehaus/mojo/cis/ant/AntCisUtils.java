/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.mojo.cis.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Touch;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.mojo.cis.core.AbstractCisUtils;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.CisUtils;


/**
 * Ant specific implementation of the {@link CisUtils}.
 */
public class AntCisUtils extends AbstractCisUtils
{
    private final Task task;
    private File tempDir;

    /**
     * Creates a new instance with the given task. The task
     * is used for logging and similar purposes.
     */
    public AntCisUtils( Task pTask )
    {
        task = pTask;
    }

    /**
     * Returns the Ant project.
     */
    protected Project getProject()
    {
        return task.getProject();
    }

    public void debug( String pMessage )
    {
        task.log( pMessage, Project.MSG_DEBUG );
    }

    public void info( String pMessage )
    {
        task.log( pMessage, Project.MSG_INFO );
    }

    public void copy( File pSourceFile, File pTargetFile ) throws CisCoreException
    {
        try
        {
            FileUtils.getFileUtils().copyFile( pSourceFile, pTargetFile );
        }
        catch ( IOException e )
        {
            throw new CisCoreException( "Copying source file " + pSourceFile.getPath()
                                        + " to target file " + pTargetFile.getPath()
                                        + " failed: " + e.getMessage(), e );
        }
    }

    public File getTempDir()
    {
        if ( tempDir == null )
        {
            tempDir = new File( System.getProperty( "java.io.tmpdir" ) );
        }
        return tempDir;
    }

    public File getProjectFile()
    {
        return null;
    }

    public void touch( File pMrkrFile ) throws CisCoreException
    {
        Touch tsk = (Touch) getProject().createTask( "touch" );
        tsk.setFile( pMrkrFile );
        tsk.execute();
    }
}
