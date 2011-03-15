package org.codehaus.mojo.pde;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Invoke test.xml if present
 * 
 * @version $Id:$
 * @goal test
 * @phase test
 * @requiresDependencyResolution test
 * @author dtran@gmail.com
 */

public class EclipsePDETestMojo
    extends EclipsePDEMojo
{

    /**
     * Test File to be invoked if present
     * @parameter expression="${testXml}" default-value="${basedir}/test.xml"
     */

    private File testXml;
    
    /**
     * Error out if test fails
     * @parameter expression="${failOnError}" default-value="true"
     */
    private boolean failOnError;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( testXml.exists() && System.getProperty( "maven.test.skip" ) == null )
        {
            super.execute();

            Commandline cl = this.createCommandLine( this.testXml, null );
            
            if ( ! failOnError )
            {
                try 
                {
                    this.executeCommandLine( cl );
                }
                catch ( MojoExecutionException e )
                {
                    this.getLog().warn( "Test failure: " + e.getMessage() );
                }
            }
            else
            {
                this.executeCommandLine( cl ); 
            }
        }
    }

}
