package org.codehaus.mojo.gae;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * Filter the <code>appengine-web.xml</code>
 * 
 * @goal descriptor
 * @phase generate-resources
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class AppengineWebDescriptorMojo
    extends AbstractGoogleAppEngineMojo
{
    /**
     * @parameter default-value="${basedir}/src/main/webapp"
     */
    // TODO read warSourceDirectory from maven-war-plugin configuration ?
    private File warSourceDirectory;

    /**
     * The character encoding scheme to be applied when filtering resources.
     *
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * The list of additional key-value pairs aside from that of the System,
     * and that of the project, which would be used for the filtering.
     *
     * @parameter expression="${project.build.filters}"
     */
    protected List<String> filters;

    /**
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    protected MavenSession session;

    /**
     * Expression preceded with the String won't be interpolated \${foo} will be replaced with ${foo}
     * 
     * @parameter expression="${maven.resources.escapeString}"
     */
    protected String escapeString;

    /**
     * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" role-hint="default"
     * @required
     */
    protected MavenResourcesFiltering mavenResourcesFiltering;


    public void execute()
        throws MojoExecutionException
    {
        Resource resource = new Resource();
        resource.setDirectory( warSourceDirectory.getAbsolutePath() );
        List<String> includes = new ArrayList<String>(1);
        includes.add( "WEB-INF/appengine-web.xml");
        resource.setIncludes( includes );
        resource.setFiltering( true );
        resource.setTargetPath( getWebappDirectory() );

        List<Resource> resources = new ArrayList<Resource>(1);
        resources.add( resource );

        MavenResourcesExecution mavenResourcesExecution =
            new MavenResourcesExecution( resources, getWebappDirectoryFile(),
                                           getProject(), encoding, filters, Collections.EMPTY_LIST, session );
        mavenResourcesExecution.setEscapeString( escapeString );
        mavenResourcesExecution.setOverwrite( true );
        mavenResourcesExecution.setIncludeEmptyDirs( false );
        try
        {
            mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        }
        catch ( MavenFilteringException e )
        {
            throw new MojoExecutionException( "Failed to filter the GAE deployment descriptor", e);
        }
    }

}
