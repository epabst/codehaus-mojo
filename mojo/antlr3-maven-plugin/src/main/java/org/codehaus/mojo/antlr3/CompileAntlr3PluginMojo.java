package org.codehaus.mojo.antlr3;

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

/**
 * Generate source code from ANTLRv3 grammar specifications.
 *
 * @author <a href="mailto:kenny@kmdconsulting.ca">Kenny MacDermid</a>
 * @goal antlr
 * @phase generate-sources
 */
public class CompileAntlr3PluginMojo extends Antlr3PluginMojo
{

    /**
     * Specifies the Antlr directory containing grammar files.
     *
     * @parameter default-value="${basedir}/src/main/antlr"
     * @required
     */
    private File sourceDirectory;

    /**
     * Location for generated Java files.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/antlr"
     * @required
     */
    private File outputDirectory;

    /**
     * Location for imported files, e.g. <code>.tokens</code>.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/antlr"
     */
    private File libDirectory;

    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public File getLibDirectory()
    {
        return libDirectory;
    }

    void addSourceRoot( File outputDir )
    {
        project.addCompileSourceRoot( outputDir.getPath() );
    }
}
