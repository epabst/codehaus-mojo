/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.mojo.cis.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.HTMLGeneratorBean;


/**
 * Ant task for running the HTMLGenerator.
 */
public class HTMLGeneratorTask extends AbstractCisTask
{
    private String projectName;
    private File xmlDir, htmlDir, logDir, accessPathDir, projectDir;

    /**
     * Returns the optional project name. If present, this is used in
     * conjunction with the {@link #getCisHomeDir() CIS home directory}
     * to calculate the project directory.
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * Sets the optional project name. If present, this is used in
     * conjunction with the {@link #getCisHomeDir() CIS home directory}
     * to calculate the project directory.
     */
    public void setProjectName( String pProjectName )
    {
        projectName = pProjectName;
    }

    /**
     * Returns the optional directory for reading the XML layouts.
     * If missing, the directory is derived from the project directory.
     */
    public File getXmlDir()
    {
        return xmlDir;
    }

    /**
     * Sets the optional directory for reading the XML layouts.
     * If missing, the directory is derived from the project directory.
     */
    public void setXmlDir( File pXmlDir )
    {
        xmlDir = pXmlDir;
    }

    /**
     * Returns the HTML directory, where the generated files are being
     * created.
     */
    public File getHtmlDir()
    {
        return htmlDir;
    }

    /**
     * Sets the HTML directory, where the generated files are being
     * created.
     */
    public void setHtmlDir( File pHtmlDir )
    {
        htmlDir = pHtmlDir;
    }

    /**
     * Returns the log directory, where the log files are being
     * created.
     */
    public File getLogDir()
    {
        return logDir;
    }

    /**
     * Sets the log directory, where the log files are being
     * created.
     */
    public void setLogDir( File pLogDir )
    {
        logDir = pLogDir;
    }

    /**
     * Returns the access path directory, where the acesspath files are being
     * created.
     */
    public File getAccessPathDir()
    {
        return accessPathDir;
    }

    /**
     * Sets the access path directory, where the acesspath files are being
     * created.
     */
    public void setAccessPathDir( File pAccessPathDir )
    {
        accessPathDir = pAccessPathDir;
    }

    /**
     * Returns the optional project directory. If the project directory is set,
     * then it is used to provide defaults for the {@link #getXmlDir()
     * XML directory}, {@link #getHtmlDir() HTML directory},
     * {@link #getLogDir() log directory}, and the {@link #getAccessPathDir()}
     * access path directory.
     */
    public File getProjectDir() {
        return projectDir;
    }

    /**
     * Sets the project directory. If the project directory is set,
     * then it is used to provide defaults for the {@link #getXmlDir()
     * XML directory}, {@link #getHtmlDir() HTML directory},
     * {@link #getLogDir() log directory}, and the {@link #getAccessPathDir()}
     * access path directory.
     */
    public void setProjectDir( File pProjectDir ) {
        projectDir = pProjectDir;
    }

    public void execute() throws BuildException
    {
        File projectDirectory = getProjectDir();
        File cisHomeDirectory = getCisHomeDir();
        if ( projectDirectory == null  &&  cisHomeDirectory != null)
        {
            final String projName = getProjectName();
            if ( projName != null )
            {
                projectDirectory = new File( cisHomeDirectory, projName );
            }
        }
        File xmlDirectory = getXmlDir();
        if ( xmlDirectory == null  &&  projectDirectory != null )
        {
            xmlDirectory = new File( projectDirectory, "xml" );
        }
        File htmlDirectory = getHtmlDir();
        if ( htmlDirectory == null  &&  projectDirectory != null ) {
            htmlDirectory = projectDirectory;
        }
        File logDirectory = getLogDir();
        if ( logDirectory == null  &&  projectDirectory != null ) {
            logDirectory = new File( projectDirectory, "log" );
        }
        File accessPathDirectory = getAccessPathDir();
        if ( accessPathDirectory == null  &&  projectDirectory != null )
        {
            accessPathDirectory = new File( projectDirectory, "accesspath" );
        }
        HTMLGeneratorBean htmlGenerator = new HTMLGeneratorBean();
        htmlGenerator.setAccessPathDir( accessPathDirectory );
        htmlGenerator.setHtmlDir( htmlDirectory );
        htmlGenerator.setLogDir( logDirectory );
        htmlGenerator.setXmlDir( xmlDirectory );
        htmlGenerator.setCisUtils( newCisUtils() );
        htmlGenerator.setCisHomeDir( cisHomeDirectory );
        try
        {
            htmlGenerator.execute();
        }
        catch ( CisCoreException e )
        {
            throw new BuildException( e.getMessage(), e.getCause() );
        }
    }
}
