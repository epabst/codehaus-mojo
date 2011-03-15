package org.codehaus.mojo.pde.stubs;

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

import org.apache.maven.model.Build;

/**
 * Stub Build to support native-maven-plugin test harness
 * 
 * @author dtran
 * 
 */
public class BuildStub
    extends Build
{
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * hard code final name
     */
    private String finalName = "some-final-name";

    /**
     * stubbed.
     * @return stubbed
     */
    public String getOutputDirectory()
    {
        return "fake-output-directory";
    }

    /**
     * stubbed.
     * @return stubbed
     */
    public String getFinalName()
    {
        return this.finalName;
    }

    /**
     * stubbed.
     * @param name stubbed
     */
    public void setFinalName( String name )
    {
        this.finalName = name;
    }

    /**
     * stubbed.
     * @return stubbed
     */
    public String getDirectory()
    {
        return "target";
    }
}
