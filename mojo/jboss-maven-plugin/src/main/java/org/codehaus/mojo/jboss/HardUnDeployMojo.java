package org.codehaus.mojo.jboss;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Delete files from <code>$JBOSS_HOME/server/[serverName]/deploy</code> directory.
 * 
 * @author <a href="mailto:bjkuczynski@gmial.com">Bartek 'Koziolek' Kuczynski</a>
 * @goal hard-undeploy
 * @since 1.4
 */
public class HardUnDeployMojo
    extends AbstractJBossServerMojo
{
    /**
     * The names of the files or directories to undeploy. If this is set, the fileName parameter will be ignored.
     * 
     * @parameter
     * @since 1.4.1
     */
    protected File[] fileNames;

    /**
     * The name of the file or directory to undeploy.
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     */
    protected File fileName;

    /**
     * Main plugin execution.
     * 
     * @throws MojoExecutionException
     */
    public void execute()
        throws MojoExecutionException
    {
        checkConfig();

        if ( fileNames == null || fileNames.length == 0 )
        {
            fileNames = new File[1];
            fileNames[0] = fileName;
        }

        for ( int i = 0; i < fileNames.length; ++i )
        {
            File nextFile = new File( jbossHome + "/server/" + serverName + "/deploy/" + fileNames[i].getName() );
            getLog().debug( "Undeploy file: " + nextFile.getName() );
            if ( !nextFile.exists() )
            {
                getLog().debug( "File " + nextFile.getAbsolutePath() + " doesn't exist." );
                return;
            }
            if ( nextFile.isFile() )
            {
                if ( nextFile.delete() )
                {
                    getLog().info( "File " + nextFile.getName() + " undeployed." );
                }
                else
                {
                    getLog().warn( "Unable to delete file: " + nextFile );
                }
            }
            else if ( nextFile.isDirectory() )
            {
                try
                {
                    FileUtils.deleteDirectory( nextFile );
                    getLog().info( "Directory " + nextFile.getName() + " undeployed." );
                }
                catch ( IOException e )
                {
                    getLog().warn( "Unable to delete directory: " + nextFile );
                    getLog().warn( e.getMessage() );
                }
            }
        }
    }
}
