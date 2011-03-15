package org.codehaus.mojo.dbupgrade;

import java.io.File;
import java.sql.SQLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.dbupgrade.sqlexec.DefaultSQLExec;
import org.codehaus.mojo.dbupgrade.sqlexec.SQLExec;
import org.codehaus.mojo.dbupgrade.sqlexec.SQLExecConfig;

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
 * Use this goal to clean your database through a provided SQL Script
 * @goal clean-database
 */
public class CleanDBUpgradeMojo
    extends AbstractDBUpgradeMojo
{
    /**
     * Necessary configuration to run database upgrade. 
     * @parameter
     * @required
     */
    private SQLExecConfig config;
    
    /**
     * Path to a SQL script to clean your database
     * @parameter expression="${cleanScript}
     * @required
     */
    private File cleanScript;
    
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            SQLExec sqlExec = new DefaultSQLExec( config );
            sqlExec.execute( cleanScript );
            sqlExec.commit();
            sqlExec.close();
            this.getLog().info( "Clean database script was invoked via " + cleanScript );
        }
        catch ( SQLException e )
        {
            throw new MojoExecutionException( getExceptionMessages( e ) );
        }
        
    }
}
