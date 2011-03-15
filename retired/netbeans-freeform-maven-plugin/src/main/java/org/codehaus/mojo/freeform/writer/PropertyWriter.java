package org.codehaus.mojo.freeform.writer;

/*
* Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.freeform.FreeformPluginException;


/**
 * This class represents the Writer for a List of Properties.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public class PropertyWriter
{
    /**
     * Holds value of property localRepository.
     */
    private ArtifactRepository localRepository;
    
    /**
     * Holds value of property mavenProject.
     */
    private MavenProject mavenProject;

    /**
     * Holds the properties descriptor file to write.
     */
    private File propertyFile;

    /**
     * The maven plugin logger.
     */
    private Log log;

    /**
     * The path to the Maven executable.
     */
    private String mavenpath;

    /**
     * Creates a new instance of PropertyWriter.
     * @param mavenProject The maven project.
     * @param propertyFile The file to write the properties in.
     * @param log The maven plugin logger.
     * @param localRepository The local repository.
     * @param mavenpath The path to the Maven executable
     */
    public PropertyWriter(
        final File propertyFile,
        final Log log,
        final ArtifactRepository localRepository,
        final MavenProject mavenProject,
        final String mavenpath
    )
    {
        this.propertyFile = propertyFile;
        this.log = log;
        this.localRepository = localRepository;
        this.mavenProject = mavenProject;
        this.mavenpath = mavenpath;
    }

    /**
     * This is the main method called on the Class for writing the property
     * file. It creates the XMLWriter for the property file.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    public void write()
        throws FreeformPluginException
    {
        Properties properties = new Properties();
        properties.put(
            "local.repository",
            localRepository.getBasedir()
        );
        log.debug("using local repository : " + localRepository.getBasedir());
        
        properties.put(
            "project.directory",
            mavenProject.getBasedir().getAbsolutePath()
        );
        log.debug(
            "using project directory : " + 
            mavenProject.getBasedir().getAbsolutePath());

        String path = mavenpath;
        if (path == null)
        {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
            {
                path = "mvn.bat";
            }
            else
            {
                path = "mvn";
            }
        }
        
        properties.put("mvn.path", path);
        log.debug("using maven path : " + path);
        
        try
        {
            properties.store(
                new FileOutputStream( propertyFile ),
                "The netbeans freeform property file"
            );
        }
        catch ( IOException e )
        {
            log.error( "The property file can not be writed.", e );
            throw new FreeformPluginException(
                "The property file can not be writed", e
            );
        }
    }
}
