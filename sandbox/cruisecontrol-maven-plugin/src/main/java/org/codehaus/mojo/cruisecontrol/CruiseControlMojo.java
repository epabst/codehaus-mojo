package org.codehaus.mojo.cruisecontrol;

/**
 * Copyright 2006 The Codehaus.
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Notifier;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.cruisecontrol.configelement.EmailPublisher;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Generates a new CruiseControl config.xml file based on
 * the poms.
 * Reads the pom, and all modules spesified.
 * 
 * @goal cruisecontrol
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 * 
 */
public class CruiseControlMojo
    extends AbstractMojo
{

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The basedir of the project.
     * 
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * The basedir of the project.
     * 
     * @parameter expression="${reactorProjects}"
     * @required
     * @readonly
     */
    private List reactorProjects;

    /**
     * The number of concurrent builds.
     * 
     * @parameter expression="${cruisecontrol.numberOfBuildThreads}
     */
    private String numberOfBuildThreads;

    /**
     * The number of concurrent builds.
     * 
     * @parameter expression="${maven.home}
     */
    private String mavenHome;

    /**
     * Email/Html email publisher settings to
     * be added as global plugin settings.
     * 
     * @parameter
     */
    private EmailPublisher globalEmailSettings;

    private static HashSet buildProjects = new HashSet();

    /**
     * 
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( buildProjects.contains( project ) )
        {
            getLog().debug( "skipping... Config for project already made" );
            return;
        }

        PrintWriter printWriter = getPrintWriter();
        PrettyPrintXMLWriter writer = new PrettyPrintXMLWriter( printWriter );

        writer.startElement( "cruisecontrol" );
        addGlobalSettings( writer );
        addGlobalPlugins( writer );
        Iterator iter = reactorProjects.iterator();
        while ( iter.hasNext() )
        {
            MavenProject reactorProject = (MavenProject) iter.next();
            writer.startElement( "project" );
            writer.addAttribute( "name", reactorProject.getName() );
            addLog( writer, reactorProject );
            CruiseControlModificationSetConfigurer.addModificationSet( writer, reactorProject );
            addSchedule( writer, reactorProject );
            addPublishers( writer, reactorProject );
            writer.endElement();
            buildProjects.add( reactorProject );

        }
        writer.endElement();
        printWriter.flush();
        printWriter.close();

    }

    private void addGlobalPlugins( PrettyPrintXMLWriter writer )
    {
        if ( null != globalEmailSettings )
        {
            CruiseControlPluginConfigurer.addEmailPlugin( writer, globalEmailSettings, project );
        }
        CruiseControlPluginConfigurer.addProjectPlugin( writer );

    }

    private PrintWriter getPrintWriter()
        throws MojoExecutionException
    {
        File configFile = new File( basedir, "config.xml" );
        PrintWriter printWriter;
        try
        {
            printWriter = new PrintWriter( configFile );
        }
        catch ( FileNotFoundException e1 )
        {
            throw new MojoExecutionException( "Could not open stream" );
        }
        return printWriter;
    }

    private void addGlobalSettings( PrettyPrintXMLWriter writer )
    {
        if ( null != numberOfBuildThreads )
        {
            writer.startElement( "system" );
            {
                writer.startElement( "configuration" );
                {
                    writer.startElement( "threads" );
                    writer.addAttribute( "count", numberOfBuildThreads );
                    writer.endElement();
                }
                writer.endElement();
            }
            writer.endElement();
        }

    }

    /** 
     * <ciManagement>
     *    <notifiers>
     *    </notifiers>
     *  </ciManagement> 
     */
    private void addPublishers( XMLWriter writer, MavenProject reactorProject )
    {
        writer.startElement( "publishers" );
        HashSet globalSuccesses = new HashSet();
        HashSet globalFailures = new HashSet();
        EmailPublisher config = new EmailPublisher( false );

        if ( null != globalEmailSettings.getSuccesses() )
        {
            for ( int i = 0; i < globalEmailSettings.getSuccesses().length; i++ )
            {
                String address = globalEmailSettings.getSuccesses()[i];
                globalSuccesses.add( address );

            }
        }

        if ( null != globalEmailSettings.getFailures() )
        {
            for ( int i = 0; i < globalEmailSettings.getFailures().length; i++ )
            {
                String address = globalEmailSettings.getFailures()[i];
                globalFailures.add( address );

            }
        }

        Iterator notifiers = project.getCiManagement().getNotifiers().iterator();
        List successes = new ArrayList();
        List failures = new ArrayList();
        while ( notifiers.hasNext() )
        {
            Notifier notifier = (Notifier) notifiers.next();
            if ( "htmlemail".equalsIgnoreCase( notifier.getType() ) )
            {
                if ( notifier.isSendOnSuccess() )
                {
                    String address = notifier.getConfiguration().getProperty( "address" );
                    if ( !globalSuccesses.contains( address ) )
                    {
                        successes.add( address );
                    }
                }
                if ( notifier.isSendOnError() || notifier.isSendOnFailure() || notifier.isSendOnWarning() )
                {
                    String address = notifier.getConfiguration().getProperty( "address" );
                    if ( !globalFailures.contains( address ) )
                    {
                        failures.add( address );
                    }
                }
            }
        }
        if ( !successes.isEmpty() )
        {
            config.setSuccesses( (String[]) successes.toArray( new String[0] ) );
        }
        if ( !failures.isEmpty() )
        {
            config.setFailures( (String[]) failures.toArray( new String[0] ) );
        }
        CruiseControlEmailConfigurer.addHtmlEMailPublisher( writer, config, reactorProject );

        writer.endElement();

    }

    private void addSchedule( XMLWriter writer, MavenProject reactorProject )
        throws MojoExecutionException
    {
        writer.startElement( "schedule" );
        {
            writer.startElement( "maven2" );
            {
                writer.addAttribute( "mvnhome", mavenHome );
                writer.addAttribute( "goal", "-N -B scm:update|-N -B clean install" );
                writer.addAttribute( "pomfile", "checkout/"
                    + toRelativeAndFixSeparator( basedir, reactorProject.getFile() ) );
            }
            writer.endElement();
        }
        writer.endElement();

    }

    private void addLog( XMLWriter writer, MavenProject reactorProject )
        throws MojoExecutionException
    {
        writer.startElement( "log" );
        {
            File sureFireDir = new File( reactorProject.getBuild().getDirectory() + "/surefire-reports" );
            writer.startElement( "merge" );
            writer.addAttribute( "dir", "checkout/" + toRelativeAndFixSeparator( basedir, sureFireDir ) );
            writer.endElement();

        }
        writer.endElement();

    }

    private static String toRelativeAndFixSeparator( File basedir, File fileToAdd )
        throws MojoExecutionException
    {
        String basedirpath;
        String absolutePath;

        try
        {
            basedirpath = basedir.getCanonicalPath();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not make relative path", e );
        }

        try
        {
            absolutePath = fileToAdd.getCanonicalPath();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not make relative path", e );
        }

        String relative;

        if ( absolutePath.equals( basedirpath ) )
        {
            relative = ".";
        }
        else if ( absolutePath.startsWith( basedirpath ) )
        {
            relative = absolutePath.substring( basedirpath.length() + 1 );
        }
        else
        {
            relative = absolutePath;
        }

        relative = StringUtils.replace( relative, "\\", "/" );

        return relative;
    }

}