package org.codehaus.mojo.setup.maven3.toolchain;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.Reader;

import org.apache.maven.execution.MavenSession;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.SetupMergeException;

/**
 * @author Robert Scholte
 * @since 1.0.0
 * @plexus.component role="org.codehaus.mojo.setup.SetupManager" role-hint="toolchains"
 */
public final class ToolchainsSetupManager
    extends AbstractSetupManager
{

    @Override
    public File getTargetFile( MavenSession session )
    {
        return session.getRequest().getUserToolchainsFile();
    }

    @Override
    protected String getPrototypeFilename()
    {
        return "toolchains.xml";
    }

    @Override
    protected Reader merge( Reader dominant, Reader recessive )
        throws SetupMergeException
    {
        throw new SetupMergeException( "merge not suppoerted" );
    }
}
