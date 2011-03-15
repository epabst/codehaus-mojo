package org.codehaus.mojo.freeform.mojo;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.freeform.FreeformConstants;
import org.codehaus.mojo.freeform.FreeformPluginException;
import org.codehaus.mojo.freeform.analyser.Analyser;
import org.codehaus.mojo.freeform.project.FreeformProject;
import org.codehaus.mojo.freeform.project.Property;
import org.codehaus.mojo.freeform.writer.IdeFileTargetsWriter;
import org.codehaus.mojo.freeform.writer.MavencallWriter;
import org.codehaus.mojo.freeform.writer.ProjectWriter;
import org.codehaus.mojo.freeform.writer.PropertyWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;


/**
 * Integrate the use of Maven 2 with Netbeans 4.x.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 *
 * @requiresDependencyResolution test
 * @execute phase="generate-sources"
 * @goal generate-netbeans-project
 */
public class GenerateNetbeansProjectMojo
    extends AbstractMojo
{
    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * The currently executed project.
     *
     * @parameter expression="${executedProject}"
     * @required
     */
    protected MavenProject executedProject;

    /**
     * The project whose project files to create.
     *
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    /**
     * A comma separated list of additional files to view.
     *
     * @parameter expression="${freeform.additionalFiles}"
     */
    protected String additionalFiles;

    /**
     * A comma separated list of additionnal folders to view.
     *
     * @parameter expression="${freeform.additionalFolders}"
     */
    protected String additionalFolders;

    /**
     * A comma separated list of additional goals to call from Netbeans.
     *
     * @parameter expression="${freeform.additionalGoals}"
     */
    protected String additionalGoals;

    /**
     * The path to the Maven executable.
     *
     * @parameter expression="${freeform.mavenpath}"
     */
    protected String mavenpath;

    /**
     * The directory where to write the nbproject directory and Netbeans 
     * project files. Defaults to the directory where the pom.xml file reside,
     * if this parameter is left empty.
     *
     * @parameter expression="${freeform.outputDirectory}"
     */
    protected String outputDirectory;

    /**
     * Setter for the outputDirectory property.
     *
     * @param outputDirectory The new outputDirectory.
     */
    public void setOutputDirectory( String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Setter for the additionalFiles property.
     *
     * @param additionalFiles The new additionalFiles.
     */
    public void setAdditionalFiles( String additionalFiles )
    {
        this.additionalFiles = additionalFiles;
    }

    /**
     * Setter for the additionalFolders property.
     *
     * @param additionalFolders The new additionalFolders.
     */
    public void setAdditionalFolders( String additionalFolders )
    {
        this.additionalFolders = additionalFolders;
    }

    /**
     * Setter for the additionalGoals property.
     *
     * @param additionalGoals The new additionalGoals.
     */
    public void setAdditionalGoals( String additionalGoals )
    {
        this.additionalGoals = additionalGoals;
    }

    /**
     * Setter for property executedProject.
     *
     * @param executedProject New value of property executedProject.
     */
    public void setExecutedProject( MavenProject executedProject )
    {
        this.executedProject = executedProject;
    }

    /**
     * Setter for property localRepository.
     *
     * @param localRepository New value of property localRepository.
     */
    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    /**
     * Setter for property project.
     *
     * @param project New value of property project.
     */
    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    /**
     * Setter for property mavenpath.
     *
     * @param mavenpath New value of property mavenpath.
     */
    public void setMavenPath( String mavenpath )
    {
        this.mavenpath = mavenpath;
    }

    /**
     * This method is the starting method of this class. It is used by Maven 2
     * to call the this Mojo.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          if something goes wrong. That Exception will be wrapped on any
     *          FreeformPluginException thrown during the plugin execution.
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            Analyser analyser = analyse();

            getLog().info(
                "The project '" + project.getName() + "' is analysed."
            );

            addAdditionalTargets( analyser );

            addAdditionalActions( analyser );

            addAdditionalFiles( analyser );

            addAdditionalFolders( analyser );

            File netbeansProjectDirectory = createNetbeansProjectDirectory();

            writeProjectFile(
                netbeansProjectDirectory,
                analyser.getProject()
            );

            getLog().info( "The file 'nbproject/project.xml' is created." );

            writeMavencallFile(
                netbeansProjectDirectory,
                (this.outputDirectory != null),
                analyser.getTargets()
            );

            getLog().info( "The file 'nbproject/mavencall.xml' is created." );

            writePropertyFile(
                netbeansProjectDirectory,
                localRepository.getBasedir()
            );

            getLog().info(
                "The file 'nbproject/project.properties' is created."
            );
            
            // Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
            
            writeIdeFileTargetsFile(netbeansProjectDirectory, analyser.getProject());
            getLog().info(
                    "The file 'nbproject/ide-file-targets.xml' is created."
                );            
            
        }
        catch ( FreeformPluginException fpe )
        {
            throw new MojoExecutionException(
                fpe.getMessage(),
                fpe
            );
        }
    }

    /**
     * This method adds the List of actions defined in the additionalGoals
     * attribute to the given analyser.
     *
     * @param analyser The analyser to add the actions in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void addAdditionalActions( final Analyser analyser )
        throws FreeformPluginException
    {
        if ( additionalGoals != null )
        {
            analyser.addAdditionalActions( tokenizeString( additionalGoals ) );
        }
    }

    /**
     * This method adds the List of files defined in the additionalFiles
     * attribute to the given analyser.
     *
     * @param analyser The analyser to add the files in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void addAdditionalFiles( final Analyser analyser )
        throws FreeformPluginException
    {
        if ( additionalFiles != null )
        {
            analyser.addAdditionalFiles( tokenizeString( additionalFiles ),
                (this.outputDirectory != null) );
        }
    }
    
    /**
     * This method adds the List of folders defined in the additionalFolders
     * attribute to the given analyser.
     *
     * @param analyser The analyser to add the folders in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void addAdditionalFolders( final Analyser analyser )
        throws FreeformPluginException
    {
        if ( additionalFolders != null )
        {
            analyser.addAdditionalFolders( tokenizeString( additionalFolders ),
                (this.outputDirectory != null) );
        }
    }

    /**
     * This method adds the List of ant targets defined in the additionalGoals
     * attribute to the given analyser.
     *
     * @param analyser The analyser to add the tergets in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void addAdditionalTargets( final Analyser analyser )
        throws FreeformPluginException
    {
        if ( additionalGoals != null )
        {
            analyser.addAdditionalTargets( tokenizeString( additionalGoals ) );
        }
    }

    /**
     * This method creates the Analyser for the MavenProject then calls
     * analyseProject and analyseTargets on it.
     *
     * @return The analyser which analysed the MavenProject with the computed
     *         FreeformProject and List of AntTargets.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected Analyser analyse()
        throws FreeformPluginException
    {
        Analyser analyser =
            Analyser.getAnalyser(
                project,
                executedProject,
                localRepository,
                getLog(),
                mavenpath
            );
        analyser.analyseProject(outputDirectory != null);
        analyser.analyseTargets(outputDirectory != null);

        return analyser;
    }

    /**
     * This methods creates the nbproject directory that will hold the netbeans
     * descriptor file and ant script.
     *
     * @return The nbproject directory as a File.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected File createNetbeansProjectDirectory()
        throws FreeformPluginException
    {
        File netbeansProjectDirectory =
            new File(
                (this.outputDirectory == null ? 
                    project.getBasedir() : 
                    new File(this.outputDirectory)),
                "nbproject"
            );

        if (
            netbeansProjectDirectory.exists()
            && !netbeansProjectDirectory.isDirectory()
            )
        {
            throw new FreeformPluginException(
                "'nbproject' is not a directory"
            );
        }

        if ( !netbeansProjectDirectory.exists() )
        {
            netbeansProjectDirectory.mkdirs();
        }

        return netbeansProjectDirectory;
    }

    /**
     * This method write the mavencall.xml file given the directory where to
     * save the file and the list of AntTarget to write in.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @param netbeansProjectDirectory The directory to write the mavencall.xml
     *                                 file.
     * @param antTargets The list of AntTarget to write in the file.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    protected void writeMavencallFile(
        final File netbeansProjectDirectory,
        final boolean useOutputDirectory,
        final List antTargets
    )
        throws FreeformPluginException
    {
        File mavencallFile =
            new File( netbeansProjectDirectory, "mavencall.xml" );
        MavencallWriter mavencallWriter =
            new MavencallWriter(
                antTargets,
                mavencallFile,
                project,
                useOutputDirectory,
                getLog()
            );
        mavencallWriter.write();
    }

    /**
     * This method write the project.xml file given the directory where to save
     * the file and the FreeformProject to write in.
     *
     * @param netbeansProjectDirectory The directory to write the mavencall.xml
     *                                 file.
     * @param freeformProject          The FreeformProject to write in the file.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeProjectFile(
        final File netbeansProjectDirectory,
        final FreeformProject freeformProject
    )
        throws FreeformPluginException
    {
        File projectFile = new File( netbeansProjectDirectory, "project.xml" );
        ProjectWriter projectWriter =
            new ProjectWriter(
                freeformProject,
                projectFile,
                project,
                getLog()
            );
        projectWriter.write();
    }

    /**
     * This method write the project.properties file given the directory where
     * to save the file and the FreeformProject to write in.
     *
     * @param repositoryBasedir        Not used.
     * @param netbeansProjectDirectory The directory to write the mavencall.xml
     *                                 file.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     * @todo remove the repositoryBasedir parameter.
     */
    protected void writePropertyFile(
        final File netbeansProjectDirectory,
        final String repositoryBasedir
    )
        throws FreeformPluginException
    {
        File propertyFile =
            new File( netbeansProjectDirectory, "project.properties" );
        PropertyWriter propertyWriter =
            new PropertyWriter( 
                propertyFile, getLog(), localRepository, project, mavenpath
            );

        propertyWriter.write();
    }

    /**
     * This method tokenize a String into a List of Strings,
     * using comma as the separator.
     *
     * @param string The String to tokenize.
     * @return The list of Strings.
     */
    private List tokenizeString( String string )
    {
        StringTokenizer stringTokenizer = new StringTokenizer( string, "," );
        List tokenizedString = new ArrayList();

        while ( stringTokenizer.hasMoreTokens() )
        {
            tokenizedString.add( stringTokenizer.nextToken().trim() );
        }

        return ( ( tokenizedString.size() != 0 )
                 ? tokenizedString
                 : null );
    }
    
    
    
    //-----------------------------------------------------------
    //~ Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
    //-----------------------------------------------------------
    
    /**
     * This method writes the ide-file-targets.xml file given the directory where to save
     * the file and the FreeformProject to write in.
     *
     * @param netbeansProjectDirectory The directory to write the ide-file-targets.xml
     *                                 file.
     * @param freeformProject          The FreeformProject to write in the file.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeIdeFileTargetsFile(
        final File netbeansProjectDirectory,
        final FreeformProject freeformProject
    )
        throws FreeformPluginException
    {
        File file = new File( netbeansProjectDirectory, FreeformConstants.CUSTOM_BUILD_SCRIPT );
        IdeFileTargetsWriter writer =
            new IdeFileTargetsWriter(freeformProject, file, netbeansProjectDirectory, getLog(), executedProject.getName());
        writer.write();
    }    
    
    
    
}
