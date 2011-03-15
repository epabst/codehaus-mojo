/*
 *  Copyright 2005-2006 Brian Fox (brianefox@gmail.com)
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

package org.codehaus.mojo.kodo;

import java.io.IOException;

import kodo.enhance.JDOEnhancer;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal that enhances persistant classes
 * 
 * @requiresDependancyResolution test
 * @goal enhance
 * 
 * @phase compile
 */
public class Enhance
    extends KodoMojoParent

{
    public Enhance()
    {
        super();
    }

    public void execute()
        throws MojoExecutionException
    {

        String[] params = buildParams();
        setupClassloader();

        try
        {
            JDOEnhancer.main( params );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Exception executing enhancer:", e );
        }
    }

    /**
     * Build Parameter list
     * 
     * @return
     * @throws MojoExecutionException
     */
    public String[] buildParams()
    {
        String[] files = getIncludedFiles( searchDir );

        for ( int i = 0; i < files.length; i++ )
        {
            files[i] = searchDir + "\\" + files[i];
            log.info( "Found file:" + files[i] );
        }
        return files;
    }



}
