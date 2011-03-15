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

import org.codehaus.plexus.util.cli.Commandline;

/**
 * Generate cpp files from a set of Visibroker IDL files.
 * @goal idl2cpp
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */

public class IDL2CPPMojo
    extends AbstractIDL2XXXMojo
{

    protected void setupVisiBrokerToolSpecificArgs( Commandline cl )
    {
        cl.createArgument().setValue( "idl2cpp" );
        cl.createArgument().setValue( "-fe" );
        cl.createArgument().setValue( "com.inprise.vbroker.compiler.tools.idl2XXX" );
        cl.createArgument().setValue( "-be" );
        cl.createArgument().setValue( "com.inprise.vbroker.compiler.backends.cpp.CppBackend" );
    }

}
