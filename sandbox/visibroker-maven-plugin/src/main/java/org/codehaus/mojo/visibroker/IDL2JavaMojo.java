package org.codehaus.mojo.visibroker;

/*
 * Copyright 2006 The Codehaus.
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

import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.FileUtils;


/**
 * Generate Java source files from a set of Visibroker IDL files.
 * @goal idl2java
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */

public class IDL2JavaMojo
    extends AbstractIDL2XXXMojo
{

    /**
     * A list generated Java files to be removed.
     * @parameter 
     * @optional
     * @description 
     */
    private String[] excludeOuputFiles;

    public void execute()
        throws MojoExecutionException
    {
        super.execute();

        this.removeUnwantedGeneratedFiles();

        this.project.addCompileSourceRoot( outputDirectory.getPath() );
    }

    private void removeUnwantedGeneratedFiles()
        throws MojoExecutionException
    {
        if ( this.excludeOuputFiles != null )
        {
            for ( int i = 0; i < this.excludeOuputFiles.length; ++i )
            {
                String toBeDeletedFile = this.outputDirectory.getAbsolutePath() + "/" + this.excludeOuputFiles[i];

                this.getLog().info( "Remove file: " + toBeDeletedFile );

                FileUtils.fileDelete( toBeDeletedFile );
            }
        }
    }

    protected void setupVisiBrokerToolSpecificArgs( Commandline cl )
    {
        cl.createArgument().setValue( "idl2java" );
        cl.createArgument().setValue( "-fe" );
        cl.createArgument().setValue( "com.inprise.vbroker.compiler.tools.idl2XXX" );
        cl.createArgument().setValue( "-be" );
        cl.createArgument().setValue( "com.inprise.vbroker.compiler.backends.java.JavaBackend" );
    }

}
