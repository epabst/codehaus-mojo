package org.codehaus.mojo.jboss;

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

import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Undeploys a directory or file from JBoss via JMX.
 * 
 * @author <a href="mailto:jgenender@apache.org">Jeff Genender</a>
 * @goal undeploy
 */
public class UndeployMojo
    extends AbstractJBossDeployerMojo
{
    
    public static final String DEFAULT_UNDEPLOY_URL = "/jmx-console/HtmlAdaptor?action=invokeOpByName&name=jboss.system:service%3DMainDeployer&methodName=undeploy&argType=java.net.URL&arg0=";

    /**
     * The undeployment path relative to the base server URL.
     * 
     * @parameter
     */
    protected String undeployUrlPath;

    /**
     * Main plugin execution.
     * 
     * @throws MojoExecutionException
     */
    public void doExecute()
        throws MojoExecutionException
    {
        // Note: the url path is set here instead of in the parameter default-value because of a parse error
        // when generating the project site.
        if ( undeployUrlPath == null )
        {
            undeployUrlPath = DEFAULT_UNDEPLOY_URL;
        }

        Iterator iter = fileNames.iterator();
        while ( iter.hasNext() )
        {
            String fileName = (String) iter.next();
            String fixedFile = null;
            if ( fileName.toLowerCase().endsWith( "ejb" ) )
            {
                // Fix the ejb packaging to a jar
                fixedFile = fileName.substring( 0, fileName.length() - 3 ) + "jar";
            }
            else
            {
                fixedFile = fileName;
            }

            getLog().info( "Undeploying " + fixedFile + " from JBoss." );
            String url = "http://" + hostName + ":" + port + undeployUrlPath + fixedFile;
            doURL( url );
        }
    }
}
