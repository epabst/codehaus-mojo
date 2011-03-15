package org.codehaus.mojo.antlr.proxy;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.antlr.Environment;

/**
 * TODO : javadoc
 * 
 * @author Steve Ebersole
 */
public class Tool
{
    private final Environment environment;

    private final Object antlrTool;

    public Tool( Environment environment, Helper helper )
        throws MojoExecutionException
    {
        try
        {
            antlrTool = helper.getAntlrToolClass().newInstance();
        }
        catch ( Throwable t )
        {
            throw new MojoExecutionException( "Unable to instantiate antlr.Tool", t );
        }
        this.environment = environment;
    }

    public Object getAntlrTool()
    {
        return antlrTool;
    }

    public Environment getEnvironment()
    {
        return environment;
    }
}
