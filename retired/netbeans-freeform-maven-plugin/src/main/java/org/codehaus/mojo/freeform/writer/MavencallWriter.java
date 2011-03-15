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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.freeform.FreeformPluginException;
import org.codehaus.mojo.freeform.project.AntTarget;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;


/**
 * This class represents the Writer for a List of AntTargets.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public class MavencallWriter
{
    /**
     * Holds the mavencall file to write the targets in.
     */
    private File mavencallFile;

    /**
     * Holds the list of AntTarget to write in the file.
     */
    private List antTargets;

    /**
     * The maven plugin logger.
     */
    private Log log;

    /**
     * The MavenProject used for logging.
     */
    private MavenProject mavenProject;

    /**
     * The if the output directory is used.
     */
    private boolean useOutputDirectory;

    /**
     * Creates a new instance of MavencallWriter.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @param antTargets The list of AntTarget to write in the file.
     * @param mavencallFile The file to write the targets in.
     * @param mavenProject The MavenProject.
     * @param log The maven plugin logger.
     */
    public MavencallWriter(
        final List antTargets,
        final File mavencallFile,
        final MavenProject mavenProject,
        final boolean useOutputDirectory,
        final Log log
    )
    {
        this.mavenProject = mavenProject;
        this.mavencallFile = mavencallFile;
        this.antTargets = antTargets;
        this.log = log;
        this.useOutputDirectory = useOutputDirectory;
    }

    /**
     * This is the main method called on the Class for writing the mavencall
     * file. It creates the XMLWriter for the mavencall file.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    public void write()
        throws FreeformPluginException
    {
        FileWriter fileWriter;

        try
        {
            fileWriter = new FileWriter( mavencallFile );
        }
        catch ( IOException ioe )
        {
            throw new FreeformPluginException(
                "Exception while opening file.", ioe
            );
        }

        XMLWriter xMLWriter = new PrettyPrintXMLWriter( fileWriter );

        writeMavenCallXml( xMLWriter );

        close( fileWriter );

        log.debug( "Wrote mavencall.xml for " + mavenProject.getName() );

        log.debug("Use maven executable : " + getMavenExecutable());
    }

    /**
     * This method write an AntTarget to the given XMLWriter.
     *
     * @param antTarget The AntTarget to write.
     * @param writer    The XMLWriter to write the AntTarget in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeAntTarget(
        final AntTarget antTarget,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "target" );
        writer.addAttribute(
            "name",
            antTarget.getName()
        );

        if ( antTarget.getComment() != null )
        {
            writer.addAttribute(
                "description",
                antTarget.getComment()
            );
        }

        writer.startElement( "exec" );
        writer.addAttribute(
            "dir",
            ( useOutputDirectory ? "${project.directory}" : "${basedir}" ) );
        writer.addAttribute( "executable", getMavenExecutable() );

        writer.startElement( "arg" );

        String line = "";

        Iterator optionIterator = antTarget.getOption().iterator();

        while ( optionIterator.hasNext() )
        {
            line += ( optionIterator.next() + " " );
        }

        Iterator goalIterator = antTarget.getGoal().iterator();

        while ( goalIterator.hasNext() )
        {
            line += ( goalIterator.next() + " " );
        }

        writer.addAttribute(
            "line",
            line.trim()
        );

        writer.endElement(); // arg

        writer.endElement(); // exec

        writer.endElement(); // target

        log.debug( "Ant target " + antTarget.getName() + " wrote" );
    }

    /**
     * Gives the name of the maven executable. Construct it if needed.
     * This name is "mvn.bat" on windows and "mvn" on other systems.
     *
     * @return the name of the maven executable.
     */
    private String getMavenExecutable()
    {
        return "${mvn.path}";
    }

    /**
     * This method write the List of AntTargets in the XMLWriter.
     *
     * @param writer The XMLWriter to write the List of AntTargets in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeMavenCallXml( XMLWriter writer )
        throws FreeformPluginException
    {
        boolean hasAnyTarget = antTargets.size() != 0;

        writer.startElement( "project" );
        writer.addAttribute(
            "name",
            mavenProject.getArtifactId()
        );

        if ( hasAnyTarget )
        {
            writer.addAttribute(
                "default",
                ( (AntTarget) antTargets.get( 0 ) ).getName()
            );
        }

        if (useOutputDirectory)
        {
            writer.startElement( "property" );

            writer.addAttribute( "file", "project.properties" );

            writer.endElement(); // property
        }
        else
        {
            writer.addAttribute( "basedir", ".." );

            writer.startElement( "property" );

            writer.addAttribute( "file", "nbproject/project.properties" );

            writer.endElement(); // property


        }

        Iterator iterator = antTargets.iterator();

        while ( iterator.hasNext() )
        {
            writeAntTarget( (AntTarget) iterator.next(), writer );
        }

        writer.endElement(); // project
    }

    /**
     * This method close the given Writer.
     *
     * @param closeable The Writer to close.
     */
    private void close( final Writer closeable )
    {
        if ( closeable == null )
        {
            return;
        }

        try
        {
            closeable.close();
        }
        catch ( Exception e )
        {
            // ignore
            log.warn(
                "The closure of " + mavencallFile + " can not be done", e
            );
        }
    }
}
