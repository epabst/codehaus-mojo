package org.codehaus.mojo.dita;

/*
 * Copyright 2000-2006 The Apache Software Foundation
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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Commandline;


/**
 * Display DITA Open Toolkit's common Ant properties usage.
 * This can be used a reference for <i>antProperties</i> used by dita:run goal.
 * 
 * @goal print-help
 * @requiresProject false
 */
public class DitaShowHelpMojo
    extends AbstractDitaMojo
{

    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
            return;
        }
        
        setupDitaDirectory();        

        Commandline cl = new Commandline( "java" );
        
        cl.setWorkingDirectory( project.getBasedir() );
        
        setupDitaMainClass( cl );
        
        setupDitaArguments( cl );
        
        setupClasspathEnv( cl );
        
        executeCommandline( cl );
    }

    
    private void setupDitaArguments( Commandline cl )
        throws MojoExecutionException
    {
        cl.createArg().setValue( "-h" );
        cl.createArg().setValue( "/ditadir:" + this.ditaDirectory );
    }
    
}
