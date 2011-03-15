package org.codehaus.mojo.dbupgrade;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.dbupgrade.generic.GenericDBUpgradeConfiguration;
import org.codehaus.mojo.dbupgrade.generic.GenericDBUpgradeLifecycle;

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
 * This class hooks up user's global pre-upgrade, incremental upgrades, and finally global post-upgrade using both java and SQL 
    files through java resources. Each incremental upgrade has an associate version number to be stored in a configurable
    database version table. DBUpgrade uses database version's value to pickup the next upgrade in your java resource, if any.
 * @goal generic-upgrade
 * @requiresProject false
 */
public class GenericDBUpgradeMojo
    extends AbstractDBUpgradeMojo
{
    /**
     * Necessary configuration to run database upgrade. 
     * @parameter
     * @required
     */
    private GenericDBUpgradeConfiguration config;
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            GenericDBUpgradeLifecycle dbupgrade = new GenericDBUpgradeLifecycle( config );
            dbupgrade.upgrade();
        }
        catch ( DBUpgradeException e )
        {
            throw new MojoExecutionException( getExceptionMessages( e ) );
        }
        
    }
}
