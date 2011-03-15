/*
 * Copyright (c) 2007, Ounce Labs, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY OUNCE LABS, INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OUNCE LABS, INC. BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.codehaus.mojo.ounce.core;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @plexus.component role="org.codehaus.mojo.ounce.core.OunceCore" role-hint="console"
 */
public class OunceCoreConsole
    implements OunceCore
{
    /*
     * (non-Javadoc)
     * 
     * @see com.ouncelabs.plugins.OunceCoreInterface#createApplication(java.lang.String, java.io.File, java.util.List,
     *      boolean, boolean)
     */
    public void createApplication( String baseDir, String theName, String theApplicationRoot, List theProjects,
                                   Map ounceOptions, Log log )
        throws OunceCoreException
    {
        System.out.println( "Create Application Params:" );
        System.out.println( "Basedir: " + baseDir );
        System.out.println( "Name: " + theName );
        System.out.println( "Root: " + theApplicationRoot );
        System.out.println( "Projects: " + theProjects );
        System.out.println( "OunceOptions: " + ounceOptions );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ouncelabs.plugins.OunceCoreInterface#createProject(java.lang.String, java.io.File, java.util.List,
     *      java.io.File, java.lang.String, java.lang.String, boolean, com.ouncelabs.plugins.CompilerOptions)
     */
    public void createProject( String baseDir, String theName, String theProjectRoot, List theSourceRoots,
                               String theWebRoot, String theClassPath, String theJdkName, String theCompilerOptions,
                               String packaging, Map ounceOptions, 
                               boolean analyzeStrutsFramework, boolean importStrutsValidation, Log log )
        throws OunceCoreException
    {
        System.out.println( "Create Project Params:" );
        System.out.println( "Basedir: " + baseDir );
        System.out.println( "Name: " + theName );
        System.out.println( "Root: " + theProjectRoot );
        System.out.println( "JDK Name: " + theJdkName );
        System.out.println( "SourceRoots: " + theSourceRoots );
        System.out.println( "ClassPath: " + theClassPath );
        System.out.println( "Webroot: " + theWebRoot );
        System.out.println( "Packaging: " + packaging );
        System.out.println( "CompilerOptions: " + theCompilerOptions );
        System.out.println( "OunceOptions: " + ounceOptions );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ouncelabs.plugins.OunceCoreInterface#scan()
     */
    public void scan( String applicationFile, String assessmentName, String assessmentOutput, String caller,
                      String reportType, String reportOutputType, String reportOutputLocation, boolean publish,
                      Map ounceOptions, String installDir, boolean wait, Log log )
        throws OunceCoreException
    {
        System.out.println( "ApplicationFile: " + applicationFile );
        System.out.println( "AssessmentName: " + assessmentName );
        System.out.println( "AssessmentOutput: " + assessmentOutput );
        System.out.println( "Caller: " + caller );
        System.out.println( "ReportType: " + reportType );
        System.out.println( "ReportOutputType: " + reportOutputType );
        System.out.println( "ReportOutputLocation: " + reportOutputLocation );
        System.out.println( "Publish:" + publish );
        System.out.println( "OunceOptions: " + ounceOptions );
        System.out.println( "OunceInstallDir: " + installDir );
        System.out.println( "Wait: " + wait );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.mojo.ounce.core.OunceCore#readApplication(java.lang.String)
     */
    public OunceCoreApplication readApplication( String thePath, Log log )
        throws OunceCoreException
    {
        log.info( "Read Application: Method not supported." );
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.mojo.ounce.core.OunceCore#readProject(java.lang.String)
     */
    public OunceCoreProject readProject( String thePath, Log log )
        throws OunceCoreException
    {
        log.info( "Read Project: Method not supported." );
        return null;
    }

    public void report( Log log )
        throws OunceCoreException
    {
        log.info( "Report: Method not supproted." );
    }

    public void createPathVariables( Map pathVariableMap, String installDir, Log log )
        throws OunceCoreException
    {
        log.info( "Create Path Variables: Method not supported." );
    }

}
