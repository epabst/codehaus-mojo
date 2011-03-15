package org.codehaus.mojo.dita;

/*
 * Copyright 2000-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Generate <a href="DITA Open Platform">http://www.dita-op.org</a>'s Eclipse configuration
 *  to allow editing, previewing DITA under Eclipse IDE.
 * 
 * @goal eclipse
 * @requiresProject false
 */
public class DitaEclipseMojo
    extends AbstractProjectMojo
{

    /**
     * If given, will be added to Ant command line
     * 
     * @parameter expression="${dita.projectName}" default-value="${project.artifactId}"
     * @since since 1.0-beta-1
     * 
     */
    private String projectName;
    
    
    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
            return;
        }

        File eclipseProjectFile = new File( this.project.getBasedir(), ".project" );

        if ( !eclipseProjectFile.exists() )
        {
            generateBrandNewEclipseProjectFile( eclipseProjectFile );
            return;
        }

        this.addDitaOpenPlatformToCurrentEclipseProject( eclipseProjectFile );

    }

    private void addDitaOpenPlatformToCurrentEclipseProject( File eclipseProjectFile )
        throws MojoExecutionException
    {
        Document doc = DocumentFactory.getInstance().createDocument();

        try
        {
            SAXReader saxReader = new SAXReader();
            doc = saxReader.read( eclipseProjectFile );

            this.addDitaBuildCommand( doc );

            this.addDitaBuildNature( doc );

        }
        catch ( DocumentException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        this.writeOutEclipseProject( doc, eclipseProjectFile );

    }

    private void addDitaBuildCommand( Document doc )
    {
        Element buildSpec = (Element) doc.selectSingleNode( "//projectDescription/buildSpec" );
        boolean foundDitaBuildCommand = false;
        for ( Iterator i = buildSpec.elementIterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();
            if ( "buildCommand".equals( element.getName() ) )
            {
                for ( Iterator j = element.elementIterator(); j.hasNext(); )
                {
                    Element buildCommandChildElement = (Element) j.next();
                    if ( "name".equals( buildCommandChildElement.getName() ) )
                    {
                        if ( "org.dita_op.editor.DITAFileValidator".equals( buildCommandChildElement.getText() ) )
                        {
                            foundDitaBuildCommand = true;
                        }
                    }
                }
            }
        }
        if ( !foundDitaBuildCommand )
        {
            addDitaBuildCommand( buildSpec );
        }

    }

    private void addDitaBuildCommand( Element buildSpec )
    {
        Element buildCommand = buildSpec.addElement( "buildCommand" );
        buildCommand.addElement( "name" ).setText( "org.dita_op.editor.DITAFileValidator" );
        buildCommand.addElement( "arguments" );
    }
    
    private void addDitaBuildNature( Document doc )
    {
        Element natures = (Element) doc.selectSingleNode( "//projectDescription/natures" );
        boolean foundDitaBuildNature = false;
        for ( Iterator i = natures.elementIterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();
            if ( "nature".equals( element.getName() ) )
            {
                if ( "org.dita_op.editor.DITAProjectNature".equals( element.getText() ) )
                {
                    foundDitaBuildNature = true;
                }
            }
        }
        if ( !foundDitaBuildNature )
        {
            addDitaBuildNature( natures );
        }
    }

    private void generateBrandNewEclipseProjectFile( File eclipseProjectFile )
        throws MojoExecutionException
    {
        Document doc = DocumentFactory.getInstance().createDocument();
        Element root = doc.addElement( "projectDescription" );
        root.addElement( "name" ).setText( projectName );
        root.addElement( "comment" );
        root.addElement( "projects" );
        
        addDitaBuildCommand( root.addElement( "buildSpec" ) );
        addDitaBuildNature ( root.addElement( "natures" ) );

        writeOutEclipseProject( doc, eclipseProjectFile );

    }
    

    private void addDitaBuildNature( Element natures )
    {
        natures.addElement( "nature" ).setText( "org.dita_op.editor.DITAProjectNature" );
    }
    
    private void writeOutEclipseProject( Document doc, File file )
        throws MojoExecutionException
    {
        FileWriter fileWriter = null;

        try
        {
            OutputFormat outformat = OutputFormat.createPrettyPrint();
            fileWriter = new FileWriter( file );
            XMLWriter writer = new XMLWriter( fileWriter, outformat );
            writer.write( doc );
            writer.flush();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( fileWriter );
        }

    }

}
