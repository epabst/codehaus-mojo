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

import java.io.File;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.webtest.components.AntExecutor;

/**
 * Runs a Canoo WebTest in a loop until an error or failure occurs. This
 * is handy when load testing and running the Canoo WebTests in parallel
 * to ensure that the application still works.
 *
 * @goal loop
 * @phase test
 */
public class WebtestLoopMojo extends AbstractWebtestMojo
{
    /**
     * Defines the number of iterations for "webtest:loop"
     *
     * @parameter expression="10"
     * @required
     */
    private int loops;

    /**
     * @see AbstractWebtestMojo#execute()
     * @throws MojoExecutionException the execution failed
     */
    public void execute() throws MojoExecutionException
    {
        File webtestFile = new File( this.getSourcedirectory(), this.getSourcefile() );

        try
        {
            this.getLog().info( "Executing " + webtestFile.getAbsolutePath() );

            // overwrite user settings to force termination on first problem
            Properties userProperties = this.toProperties();
            userProperties.setProperty( "haltonerror", "true" );
            userProperties.setProperty( "haltonfailure", "true" );

            long startTime = System.currentTimeMillis();

            for ( int i = 1; i <= this.loops; i++ )
            {
                long loopStartTime = System.currentTimeMillis();
                new AntExecutor( webtestFile, userProperties, this.getProject(), this.getArtifacts(), this.getTarget() );
                long currentTimeMillis = System.currentTimeMillis();
                long loopDuration = currentTimeMillis - loopStartTime;
                long duration = currentTimeMillis - startTime;
                String msg = "Finshed " + i + "/" + this.loops + " iterations in " + loopDuration + "/" + duration + " ms";
                this.getLog().info( msg );
            }
        }
        catch ( Exception e )
        {
            String msg = "Executing " + webtestFile.getAbsolutePath() + " failed";
            throw new MojoExecutionException( msg, e );
        }
    }
}
