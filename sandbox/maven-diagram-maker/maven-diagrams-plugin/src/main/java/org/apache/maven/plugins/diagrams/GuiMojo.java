package org.apache.maven.plugins.diagrams;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.diagrams.connector_api.DiagramConnector;
import org.apache.maven.diagrams.gui.controller.DiagramGuiException;
import org.apache.maven.diagrams.gui.controller.MainController;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * <p>
 * Diagrams gui mojo
 * </p>
 * 
 * @goal gui
 * @requiresDependencyResolution compile
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public class GuiMojo extends AbstractMojo
{
    /**
     * @parameter expression="${connector}" default-value="connector-classes"
     */
    private String connectorName;

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository artifactRepository;

    /**
     * @component role="org.apache.maven.diagrams.connector_api.DiagramConnector"
     */
    private Map/* <String, DiagramConnector> */connectors;

    public void execute() throws MojoExecutionException
    {
        DiagramConnector connectorClass = (DiagramConnector) connectors.get( connectorName );

        if ( connectorClass != null )
        {

            MainController controller;
            try
            {
                connectorClass.setMavenProject( project );
                connectorClass.setArtifactRepository( artifactRepository );
                controller = new MainController( connectorClass );
            }
            catch ( DiagramGuiException e )
            {
                throw new MojoExecutionException( "Cannot initiate gui: ", e );
            }
            controller.run();
            controller.getView().waitUntilClosed();
        }
        else
            throw new MojoExecutionException( "Connector: '" + connectorName + "' has not been found" );
    }
}
