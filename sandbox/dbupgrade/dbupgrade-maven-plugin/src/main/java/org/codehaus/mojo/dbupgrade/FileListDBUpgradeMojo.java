package org.codehaus.mojo.dbupgrade;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.dbupgrade.file.FileDBUpgradeLifecycle;
import org.codehaus.mojo.dbupgrade.file.FileListDBUpgradeConfiguration;

/*
 * Copyright 2000-2010 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


/**
 * This class hooks up user's sql upgrade script locations contained in a text file ( ie the text file contains a list of SQL script paths ). 
 * After a SQL script is executed, its names is stored in your configurable database version table. DBUpgrade uses
 * database version's value ( a SQL script name ) to pickup the next upgrade script, if any. * 
 * 
 * @goal filelist-upgrade
 * @requiresProject false
 */
public class FileListDBUpgradeMojo
    extends AbstractDBUpgradeMojo
{
    /**
     * Necessary configuration to run database upgrade. 
     * @parameter
     * @required
     */
    private FileListDBUpgradeConfiguration config;
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            FileDBUpgradeLifecycle dbupgrade = new FileDBUpgradeLifecycle( config );
            dbupgrade.upgrade();
            this.getLog().info( "Database upgrade using file list method: " + config.getUpgradeFile() );
        }
        catch ( DBUpgradeException e )
        {
            throw new MojoExecutionException( getExceptionMessages( e ) );
        }
    }
}
