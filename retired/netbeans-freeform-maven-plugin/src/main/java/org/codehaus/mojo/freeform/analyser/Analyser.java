package org.codehaus.mojo.freeform.analyser;

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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.freeform.FreeformPluginException;
import org.codehaus.mojo.freeform.project.AntTarget;
import org.codehaus.mojo.freeform.project.CustomisedContextMenuItem;
import org.codehaus.mojo.freeform.project.FileViewItem;
import org.codehaus.mojo.freeform.project.FolderViewItem;
import org.codehaus.mojo.freeform.project.FreeformProject;


/**
 * This abstract class is the parent class of all the <packaging>Analysers.
 * <p/>
 * The Analyser purpose is to analyse the projects and repository for
 * creating a FreeformProject object and a List of AntTarget.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public abstract class Analyser
{
    /**
     * The local repository of the Maven2 execution.
     */
    protected ArtifactRepository localRepository;

    /**
     * The freeform project that will be created.
     */
    protected FreeformProject freeformProject;

    /**
     * The list of ant targets that will be created.
     */
    protected List antTargets;

    /**
     * The logging system.
     */
    protected Log log;

    /**
     * The maven project resulting of the phase prerequisite.
     */
    protected MavenProject mavenExecutedProject;

    /**
     * The maven project.
     */
    protected MavenProject mavenProject;

    /**
     * The path to the Maven executable.
     */
    protected String mavenpath;

    /**
     * This method role is to create the FreeformProject using
     * the mavenProject, the mavenExecutedProject and the localRepository.
     * <p/>
     * This method must be overrided by each specific <packaging>Analyser.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    public final void analyseProject()
        throws FreeformPluginException
    {
        analyseProject(false);
    }

    /**
     * This method role is to create the List of AntTarget using
     * the mavenProject, the mavenExecutedProject and the localRepository.
     * <p/>
     * This method must be overrided by each specific <packaging>Analyser.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    public final void analyseTargets()
        throws FreeformPluginException
    {
        analyseTargets(false);
    }

    /**
     * This method role is to create the FreeformProject using
     * the mavenProject, the mavenExecutedProject and the localRepository.
     * <p/>
     * This method must be overrided by each specific <packaging>Analyser.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    public abstract void analyseProject(boolean useOutputDirectory)
        throws FreeformPluginException;

    /**
     * This method role is to create the List of AntTarget using
     * the mavenProject, the mavenExecutedProject and the localRepository.
     * <p/>
     * This method must be overrided by each specific <packaging>Analyser.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    public abstract void analyseTargets(boolean useOutputDirectory)
        throws FreeformPluginException;

    /**
     * This method gives the <packaging>Analyser associated to the mavenProject
     * packaging. The returned Analyser is injected withe the given parameters.
     *
     * @param mavenProject    The maven project which defines the packaging.
     * @param executedProject The maven prokect resulting of the phases
     *                        prerequisites.
     * @param localRepository The local repository of the Maven2 execution.
     * @param log             The logging system.
     * @param mavenpath       The path to the maven executable.
     * @return the <packaging>Analyser which is the one defined for the
     *         packaging of the given mavenProject.
     */
    public static Analyser getAnalyser(
        final MavenProject mavenProject,
        final MavenProject executedProject,
        final ArtifactRepository localRepository,
        final Log log,
        final String mavenpath
    )
    {
        Analyser analyser = null;

        // choose the <packaging>Analyser for the given project.
        if (
            ( mavenProject.getPackaging() != null )
            && mavenProject.getPackaging().equalsIgnoreCase( "jar" )
            )
        {
            analyser = new JarAnalyser();
        }
        else if (
            ( mavenProject.getPackaging() != null )
            && mavenProject.getPackaging().equalsIgnoreCase( "maven-plugin" )
            )
        {
            analyser = new MavenPluginAnalyser();
        }
        else
        {
            analyser = new JarAnalyser();
        }

        // inject the mavenProject, the executedProject, the localRepository,
        // the log, and the maven path to the choosen analyser.
        analyser.setMavenProject( mavenProject );
        analyser.setMavenExecutedProject( executedProject );
        analyser.setLocalRepository( localRepository );
        analyser.setLog( log );
        analyser.setMavenPath( mavenpath );

        log.info( analyser + " found" );

        return analyser;
    }

    /**
     * The localRepository setter.
     *
     * @param localRepository The ArtifactRepository to be set.
     */
    public void setLocalRepository( final ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    /**
     * The log setter.
     *
     * @param log The Log to be set.
     */
    public void setLog( final Log log )
    {
        this.log = log;
    }

    /**
     * The mavenExecutedProject setter.
     *
     * @param executedProject The MavenProject to be set.
     */
    public void setMavenExecutedProject( final MavenProject executedProject )
    {
        this.mavenExecutedProject = executedProject;
    }

    /**
     * The mavenProject setter.
     *
     * @param mavenProject The MavenProject to be set.
     */
    public void setMavenProject( final MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    /**
     * The maven path setter.
     *
     * @param mavenpath The MavenPath to be set.
     */
    public void setMavenPath( final String mavenpath )
    {
        this.mavenpath = mavenpath;
    }

    /**
     * This method returns the created FreeformProject.
     *
     * @return the created FreeformProject.
     */
    public FreeformProject getProject()
    {
        return this.freeformProject;
    }
    
    /**
     * this method returns the created List of AntTargets.
     *
     * @return the created List.
     */
    public List getTargets()
    {
        return this.antTargets;
    }

    /**
     * This method permits to add some additional actions to the Netbeans
     * context menu.
     *
     * @param actions a list of actions to add. (String encoded)
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    public void addAdditionalActions( final List actions )
        throws FreeformPluginException
    {
        if ( this.freeformProject == null )
        {
            log.error( "The project is not analysed" );

            throw new FreeformPluginException( "The project is not analysed" );
        }
        else
        {
            Iterator iterator = actions.iterator();

            while ( iterator.hasNext() )
            {
                String action = (String) iterator.next();

                CustomisedContextMenuItem contextMenuItem =
                    new CustomisedContextMenuItem();
                contextMenuItem.setName( action );
                contextMenuItem.setScript( "${ant.script}" );
                contextMenuItem.addTarget( action );

                this.freeformProject.addContextMenuItem( contextMenuItem );

                log.debug( "action" + action + " added" );
            }
        }

        log.info( "actions added" );
    }

    /**
     * This method permits to add some additional files to the Netbeans
     * project view.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @param files a list of files to add. (String encoded)
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    public void addAdditionalFiles( final List files, final boolean useOutputDirectory )
        throws FreeformPluginException
    {
        if ( this.freeformProject == null )
        {
            log.error( "The project is not analysed" );

            throw new FreeformPluginException( "The project is not analysed" );
        }
        else
        {
            Iterator iterator = files.iterator();

            while ( iterator.hasNext() )
            {
                String file = (String) iterator.next();

                FileViewItem viewItem = new FileViewItem();
                viewItem.setLabel(
                    file.substring( file.lastIndexOf( '/' ) + 1 )
                );
                viewItem.setLocation( 
                    ( useOutputDirectory ? "${project.directory}/" : "" ) +
                    file );

                this.freeformProject.addViewItem( viewItem );

                log.debug( "viewItem" + file + " added" );
            }
        }

        log.info( "files added" );
    }

    /**
     * This method permits to add some additional folders to the Netbeans
     * project view.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @param folders a list of folders to add. (String encoded)
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    public void addAdditionalFolders( final List folders, final boolean useOutputDirectory )
        throws FreeformPluginException
    {
        if ( this.freeformProject == null )
        {
            log.error( "The project is not analysed" );

            throw new FreeformPluginException( "The project is not analysed" );
        }
        else
        {
            Iterator iterator = folders.iterator();

            while ( iterator.hasNext() )
            {
                String folder = (String) iterator.next();

                FolderViewItem viewItem = new FolderViewItem();
                viewItem.setLabel( folder );
                viewItem.setLocation( 
                    ( useOutputDirectory ? "${project.directory}/" : "" ) +
                    folder );
                viewItem.setStyle( "tree" );

                this.freeformProject.addViewItem( viewItem );

                log.debug( "viewItem" + folder + " added" );
            }
        }

        log.info( "folders added" );
    }

    /**
     * This method permits to add some additionnal ant targets to the list
     * of AntTargets.
     *
     * @param targets a list of ant target to add. (String encoded)
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    public void addAdditionalTargets( final List targets )
        throws FreeformPluginException
    {
        if ( this.antTargets == null )
        {
            log.error( "The targets are not analysed" );

            throw new FreeformPluginException( "The targets are not analysed" );
        }
        else
        {
            Iterator iterator = targets.iterator();

            while ( iterator.hasNext() )
            {
                String target = (String) iterator.next();

                AntTarget antTarget = new AntTarget();
                antTarget.setName( target );
                antTarget.addGoal( target );

                this.antTargets.add( antTarget );

                log.debug( "target" + target + " added" );
            }
        }

        log.info( "targets added" );
    }

    /**
     * This method returns the classname of the <packaging>Analyser.
     * This is not the full classname.
     *
     * @return The computed classname.
     */
    public String toString()
    {
        String fullName = this.getClass().getName();

        return fullName.substring( fullName.lastIndexOf( '.' ) + 1 );
    }

    /**
     * The default constructor is set to protected to force the
     * creation of an Analyser object only for use in the getAnalyser method.
     */
    protected void Analyser()
    {
    }

    /**
     * This method creates an empty FreeformProject.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     */
    protected void createProject(boolean useOutputDirectory)
    {
        this.freeformProject = new FreeformProject();
        this.freeformProject.setUseOutputDirectory( useOutputDirectory );
    }

    /**
     * This method creates an empty AntTarget List.
     */
    protected void createTargets()
    {
        this.antTargets = new ArrayList();
    }

    /**
     * This method computes a relative path given a base directory and
     * an absolute path located in the tree of the basedir.
     *
     * @param basedir      The base directory to compute relative path from.
     * @param absolutePath The absolute path to make relative to the base
     *                     directory.
     * @return The computed relative path.
     */
    protected static String toRelative(
        final File basedir,
        final String absolutePath
    )
    {
        String relative;

        String path = absolutePath.replace( '\\', '/' );
        String basedirPath = basedir.getAbsolutePath().replace( '\\', '/' );

        if ( path.startsWith( basedirPath ) )
        {
            relative = path.substring( basedirPath.length() + 1 );
        }
        else
        {
            relative = path;
        }

        return relative;
    }
}
