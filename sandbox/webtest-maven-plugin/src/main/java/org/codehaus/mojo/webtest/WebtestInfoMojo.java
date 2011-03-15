/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Prints the configuration settings.
 *
 * @goal info
 * @phase test
 */
public class WebtestInfoMojo extends AbstractWebtestMojo
{
    /**
     * Print a configuration summary.
     *
     * @see org.apache.maven.plugin.AbstractMojo#execute() 
     * @throws MojoExecutionException the execution failed
     */
    public void execute() throws MojoExecutionException
    {
        getLog().info( "plugin.version      = " + WebtestConstants.PLUGIN_VERSION );
        getLog().info( "webtest.build       = " + WebtestConstants.IMPLEMENTATION_VERSION );
        getLog().info( "autorefresh         = " + this.isAutorefresh() );
        getLog().info( "haltonerror         = " + this.isHaltonerror() );
        getLog().info( "haltonfailure       = " + this.isHaltonerror() );
        getLog().info( "host                = " + this.getHost() );
        getLog().info( "port                = " + this.getPort() );
        getLog().info( "protocol            = " + this.getProtocol() );
        getLog().info( "reportdirectory     = " + this.getReportdirectory() );
        getLog().info( "resultpath          = " + this.getResultpath() );
        getLog().info( "saveresponse        = " + this.isSaveResponse() );
        getLog().info( "sourcedirectory     = " + this.getSourcedirectory() );
        getLog().info( "sourcefile          = " + this.getSourcefile() );
        getLog().info( "target              = " + this.getTarget() );
        getLog().info( "sourceurl           = " + this.getSourceUrl() );
        getLog().info( "basepath            = " + this.getBasepath() );
        getLog().info( "propertyPrefix      = " + this.getPropertyPrefix() );
        getLog().info( "resultreporterclass = " + this.getResultReporterClass() );
    }
}
