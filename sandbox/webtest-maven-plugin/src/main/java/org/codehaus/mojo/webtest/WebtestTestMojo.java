/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.mojo.webtest;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Properties;

import org.codehaus.mojo.webtest.components.AntExecutor;

/**
 * Runs a Canoo WebTest defined in an ANT script.
 *
 * @goal test
 * @phase test
 */
public class WebtestTestMojo extends AbstractWebtestMojo
{    
    /**
     * Defines the number of seconds to wait before starting a test.
     *
     * @parameter expression="0"
     */
    private int sleepbeforestart;

    /**
     * Starts the Canoo WebTest.
     *
     * @throws MojoExecutionException the execution failed
     */
    public void execute() throws MojoExecutionException
    {
        File webtestFile = new File( this.getSourcedirectory(), this.getSourcefile() );

        if ( !webtestFile.exists() )
        {
            String msg = "The webtest file was not found : " + webtestFile.getAbsolutePath();
            this.getLog().error( msg );
            throw new MojoExecutionException( msg );
        }

        try
        {
            this.getLog().info( "Executing " + webtestFile.getAbsolutePath() );
            Thread.sleep( this.sleepbeforestart * 1000 );            
            new AntExecutor( webtestFile, toProperties(), this.getProject(), this.getArtifacts(), this.getTarget() );
        }
        catch ( Exception e )
        {
            String msg = "Executing " + webtestFile.getAbsolutePath() + " failed";
            throw new MojoExecutionException( msg, e );
        }
    }
}
