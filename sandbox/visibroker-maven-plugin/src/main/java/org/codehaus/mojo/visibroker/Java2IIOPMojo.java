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

/**
 * Generate Java IIOP files from a set of Java class files.
 * @goal java2iiop
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */

public class Java2IIOPMojo
    extends AbstractVisiBrokerMojo
{

    /**
     * List of compiled java class names to generate java iiop files
     * @parameter 
     * @required
     */
    private String[] classNames;

    private void setupVisiBrokerToolSpecificArgs( Commandline cl )
    {
        cl.createArgument().setValue( "java2iiop" );
        cl.createArgument().setValue( "-fe" );
        cl.createArgument().setValue( "com.inprise.vbroker.compiler.tools.java2XXX" );
        cl.createArgument().setValue( "-be" );
        cl.createArgument().setValue( "com.inprise.vbroker.compiler.backends.java.JavaBackend" );
    }

    public void execute()
        throws MojoExecutionException
    {
        Commandline cl = new Commandline();

        cl.createArgument().setValue( "java" );

        setupCommandLineVisiBrokerSystemProperties( cl );

        addCommandLineClassPath( cl );

        cl.createArgument().setValue( "com.inprise.vbroker.compiler.tools.tool" );

        setupVisiBrokerToolSpecificArgs( cl );

        setupCommandLineUserOptions( cl );

        cl.createArgument().setValue( "-root_dir" );

        cl.createArgument().setValue( this.outputDirectory.getPath() );

        for ( int i = 0; i < this.classNames.length; ++i )
        {
            cl.createArgument().setValue( this.classNames[i] );
        }

        executeCommandline( cl, this.getLog() );

        this.project.addCompileSourceRoot( outputDirectory.getPath() );

    }

}
