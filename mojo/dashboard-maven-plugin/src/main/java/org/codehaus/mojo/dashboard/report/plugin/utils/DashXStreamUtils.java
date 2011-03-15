package org.codehaus.mojo.dashboard.report.plugin.utils;

/*
 * Copyright 2008 David Vicente
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleError;
import org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject;
import org.codehaus.mojo.dashboard.report.plugin.beans.XRefPackageBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author A315941
 * @version %version : 1 %
 */

public class DashXStreamUtils
{
    private static DashXStreamUtils xstreamUtils = null;
    
    //Instanciation de la classe XStream
    XStream xstream = new XStream( new DomDriver() );
    
    /**
     * Creation forbidden...
     */
    private DashXStreamUtils()
    {
        super();
        xstream.setMode( XStream.NO_REFERENCES );
        // Convertion du contenu de l'objet DashBoardReportBean en XML
        xstream.alias( "MavenProject", DashBoardMavenProject.class );
        xstream.alias( "xrefpackage", XRefPackageBean.class );
        xstream.alias( "checkstyleerror", CheckstyleError.class );
        xstream.useAttributeFor( "id", Long.class );
        xstream.useAttributeFor( "artifactId", String.class );
        xstream.useAttributeFor( "groupId", String.class );
        xstream.useAttributeFor( "version", String.class );
        xstream.useAttributeFor( "projectName", String.class );
        xstream.useAttributeFor( "generatedDate", Date.class );
        xstream.useAttributeFor( "averageAfferentCoupling", String.class );
        xstream.useAttributeFor( "nbPackages", String.class );
        xstream.useAttributeFor( "sumAC", String.class );
    }
    
    public static DashXStreamUtils getInstance()
    {
        if (xstreamUtils == null){
            xstreamUtils = new DashXStreamUtils();
        }
        return xstreamUtils;
    }
    
    /**
     * 
     * @param project
     * @param dashboardDataFile
     * @return
     * @throws DashXStreamUtilsException
     */
    public DashBoardMavenProject readXMLDashBoardReport( MavenProject project, String dashboardDataFile ) throws DashXStreamUtilsException
    {
        DashBoardMavenProject mavenProject = null;
        try
        {

            
            // Instanciation d'un fichier
            File fichier = new File( project.getBuild().getDirectory(), dashboardDataFile );

            // Redirection du fichier /target/dashboard-report.xml vers un flux
            // d'entrée fichier
            FileInputStream fis = new FileInputStream( fichier );

            try
            { 
                // Désérialisation du fichier /target/dashboard-multi-report.xml vers un nouvel
                // objet DashBoardReportBean
                mavenProject = (DashBoardMavenProject) xstream.fromXML( fis );

            }
            finally
            {
                // On s'assure de fermer le flux quoi qu'il arrive
                fis.close();
            }
        }
        catch ( FileNotFoundException e )
        {
            System.out.println( "readXMLDashBoardReport() for project " + project.getName() + " failed :"
                            + e.getMessage() );
            throw new DashXStreamUtilsException("readXMLDashBoardReport() for project " + project.getName() + " failed .",e.fillInStackTrace());
        }
        catch ( IOException ioe )
        {
            throw new DashXStreamUtilsException("readXMLDashBoardReport() for project " + project.getName() + " failed .",ioe.fillInStackTrace());
        }
        return mavenProject;
    }

    /**
     * 
     * @param project
     * @param mavenProject
     * @param dashboardDataFile
     * @throws DashXStreamUtilsException
     */
    public void saveXMLDashBoardReport( MavenProject project, DashBoardMavenProject mavenProject,
                                           String dashboardDataFile )throws DashXStreamUtilsException
    {
        try
        {
            // Instanciation d'un fichier
            File dir = new File( project.getBuild().getDirectory() );
            if ( !dir.exists() )
            {
                dir.mkdirs();
            }
            File fichier = new File( dir, dashboardDataFile );
            // Instanciation d'un flux de sortie fichier vers le xml
            FileOutputStream fos = new FileOutputStream( fichier );
            OutputStreamWriter output = new OutputStreamWriter( fos, "UTF-8" );
            try
            {
                output.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
                // Sérialisation de l'objet dashBoardReport dans /target/dashboard-report.xml
                xstream.toXML( mavenProject, output );
            }
            finally
            {
                // On s'assure de fermer le flux quoi qu'il arrive
                fos.close();
            }

        }
        catch ( FileNotFoundException e )
        {
            throw new DashXStreamUtilsException("saveXMLDashBoardReport() for project " + project.getName() + " failed .",e.fillInStackTrace());
        }
        catch ( IOException ioe )
        {
            throw new DashXStreamUtilsException("saveXMLDashBoardReport() for project " + project.getName() + " failed .",ioe.fillInStackTrace());
        }
    }
}
