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
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.freeform.FreeformConstants;
import org.codehaus.mojo.freeform.FreeformPluginException;
import org.codehaus.mojo.freeform.project.AntTarget;
import org.codehaus.mojo.freeform.project.CompilationUnit;
import org.codehaus.mojo.freeform.project.CustomisedContextMenuItem;
import org.codehaus.mojo.freeform.project.FileProperty;
import org.codehaus.mojo.freeform.project.FileViewItem;
import org.codehaus.mojo.freeform.project.FolderViewItem;
import org.codehaus.mojo.freeform.project.IdeContextMenuItem;
import org.codehaus.mojo.freeform.project.NamedNetbeansAction;
import org.codehaus.mojo.freeform.project.NamedProperty;
import org.codehaus.mojo.freeform.project.NetbeansAction;
import org.codehaus.mojo.freeform.project.Property;
import org.codehaus.mojo.freeform.project.SourceFolder;
import org.codehaus.mojo.freeform.project.Subproject;
import org.codehaus.mojo.freeform.project.ViewItem;


/**
 * this abstract class contais the base methods for injecting basic
 * functionnalities of a FreeformProject.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
abstract class BaseAnalyser
    extends Analyser
{
    /**
     * This method role is to create the FreeformProject using
     * the mavenProject, the mavenExecutedProject and the localRepository.
     * <p/>
     * This method override the Analyser's one.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    public void analyseProject(boolean useOutputDirectory)
        throws FreeformPluginException
    {
        createProject(useOutputDirectory);

        setFreeformProjectName();

        log.debug( "project name set" );

        setFreeformProperties();

        log.debug( "project properties set" );

        setFreeformSourceFolders();

        log.debug( "source folders set" );

        setFreeformIdeActions();

        log.debug( "ide actions set" );

        setFreeformViewItems();

        log.debug( "project views set" );

        setFreeformContextMenuItems();

        log.debug( "context menu set" );

        setFreeformSubprojects();

        log.debug( "subprojects set" );

        setFreeformCompilationUnits();

        log.debug( "compilation units set" );
    }

    /**
     * This method role is to create the List of AntTarget using
     * the mavenProject, the mavenExecutedProject and the localRepository.
     * <p/>
     * This method override the Analyser's one.
     * @param useOutputDirectory whether the project elements contains reference to the project directory or not.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException if something goes wrong.
     */
    public void analyseTargets(boolean useOutputDirectory)
        throws FreeformPluginException
    {
        createTargets();

        setTargetBuild();

        log.debug( "build target set" );

        setTargetClean();

        log.debug( "clean target set" );

        setTargetJavadoc();

        log.debug( "javadoc target set" );

        setTargetRun();

        log.debug( "run target set" );

        setTargetTest();

        log.debug( "test target set" );

        setTargetJalopy();

        log.debug( "jalopy target set" );

        setTargetRefreshProject();

        log.debug( "refresh project target set" );
    }

    /**
     * Sets the build ide action.
     */
    protected void setActionBuild()
    {
        // ide-action-build = package
        addIdeAction( "build", "build" );
    }

    /**
     * Sets the clean ide action.
     */
    protected void setActionClean()
    {
        // ide-action-clean = clean:clean
        addIdeAction( "clean", "clean" );
    }

    /**
     * Sets the javadoc ide action.
     */
    protected void setActionJavadoc()
    {
        // ide-action-javadoc = site:site
        addIdeAction( "javadoc", "javadoc" );
    }

    /**
     * Sets the rebuild ide action.
     */
    protected void setActionRebuild()
    {
        // ide-action-rebuild = clean:clean package
        addIdeAction( "rebuild", "clean build" );
    }

    /**
     * Sets the run ide action.
     */
    protected void setActionRun()
    {
        // ide-action-run = install
        addIdeAction( "run", "run" );
    }

    /**
     * Sets the test ide action.
     */
    protected void setActionTest()
    {
        // ide-action-test = test
        addIdeAction( "test", "test" );
    }

    /**
     * Sets all the compilation units for the project.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong
     */
    protected void setFreeformCompilationUnits()
        throws FreeformPluginException
    {
        addMainCompilationUnits();

        log.debug( "main compilation unit added" );

        addTestCompilationUnits();

        log.debug( "test compilation unit added" );
    }

    /**
     * Sets the whole context menu for the project.
     */
    protected void setFreeformContextMenuItems()
    {
        setMenuRefreshProject();

        log.debug( "set refresh project context menu element" );

        setMenuBuild();

        log.debug( "set build context menu element" );

        setMenuClean();

        log.debug( "set clean context menu element" );

        setMenuRebuild();

        log.debug( "set rebuld context menu element" );

        setMenuJavadoc();

        log.debug( "set javadoc context menu element" );

        setMenuRun();

        log.debug( "set run context menu element" );

        setMenuTest();

        log.debug( "set test context menu element" );

        setMenuJalopy();

        log.debug( "set jalopy context menu element" );
    }

    /**
     * Sets all the ide actions for the project.
     */
    protected void setFreeformIdeActions()
    {
        setActionBuild();

        log.debug( "set build ide action" );

        setActionClean();

        log.debug( "set clean ide action" );

        setActionRebuild();

        log.debug( "set rebuild ide action" );

        setActionJavadoc();

        log.debug( "set javadoc ide action" );

        setActionRun();

        log.debug( "set run ide action" );

        setActionTest();

        log.debug( "set test ide action" );
    }

    /**
     * This method sets the name of the FreeformProject using the one of the
     * MavenProject.
     */
    protected void setFreeformProjectName()
    {
        freeformProject.setName( mavenProject.getName() );
    }

    /**
     * Sets all the properties form the project.
     */
    protected void setFreeformProperties()
    {
        // script-path
        addMavencallScript();
        
        log.debug( "maven call reference property added" );
        
        //Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
        addCustomScript(); 
        log.debug( "ide-file-targets.xml reference property added" );
        
        // property file
        addPropertyFile();

        log.debug( "project proerties file added" );
    }

    /**
     * Sets all the source folders for the project.
     */
    protected void setFreeformSourceFolders()
    {
        // source-roots (foreach)
        addSourceFolders();

        log.debug( "main sources added" );

        // test-source-roots (foreach)
        addTestSourceFolders();

        log.debug( "test sources added" );
    }

    /**
     * This method adds the modules of the MavenProject to the FreeformProject
     * subprojects.
     */
    protected void setFreeformSubprojects()
    {
        if (
            ( mavenProject != null ) && ( mavenProject.getModules() != null )
            )
        {
            Iterator subprojects = mavenProject.getModules().iterator();

            while ( subprojects.hasNext() )
            {
                String subprojectName = (String) subprojects.next();

                addSubproject( subprojectName );

                log.debug( "subproject " + subprojectName + " added" );
            }
        }
    }

    /**
     * Sets all the view items for the project.
     */
    protected void setFreeformViewItems()
    {
        setViewPom();
        log.debug( "pom view set" );

        setViewMavencall();
        log.debug( "mavencall view set" );

        addCompileSourceRoots();
        log.debug( "main sources views set" );

        addTestCompileSourceRoots();
        log.debug( "test sources views set" );

        addResources();
        log.debug( "main resources views set" );

        addTestResources();
        log.debug( "test resources views set" );

        addSiteDirectoy();
        log.debug( "site view set" );

        addTargetDirectory();
        log.debug( "target view set" );
    }

    /**
     * Sets the build menu item.
     */
    protected void setMenuBuild()
    {
        // context-menu-build
        addIdeContextItem( "build" );
    }

    /**
     * Sets the clean menu item.
     */
    protected void setMenuClean()
    {
        // context-menu-clean
        addIdeContextItem( "clean" );
    }

    /**
     * Sets the format code menu item.
     */
    protected void setMenuJalopy()
    {
        // context-menu-custon-jalopy
        addCustomisedContextMenuItem( "Format Code", "jalopy" );
    }

    /**
     * Sets the javadoc menu item.
     */
    protected void setMenuJavadoc()
    {
        // context-menu-javadoc
        addIdeContextItem( "javadoc" );
    }

    /**
     * Sets the rebuild menu item.
     */
    protected void setMenuRebuild()
    {
        // context-menu-rebuild
        addIdeContextItem( "rebuild" );
    }

    /**
     * Sets the refresh project menu item.
     */
    protected void setMenuRefreshProject()
    {
        // context-menu-custon-refresh-project (nb-ff:g-nb-p)
        addCustomisedContextMenuItem( "Refresh Project", "refresh-project" );
    }

    /**
     * Sets the run menu item.
     */
    protected void setMenuRun()
    {
        // context-menu-run
        addIdeContextItem( "run" );
    }

    /**
     * Sets the test menu item.
     */
    protected void setMenuTest()
    {
        // context-menu-test
        addIdeContextItem( "test" );
    }

    /**
     * Sets the build ant target.
     */
    protected void setTargetBuild()
    {
        // ide-action-build = package
        addAntTarget( "build", "package", "Build" );
    }

    /**
     * Sets the clean ant target.
     */
    protected void setTargetClean()
    {
        // ide-action-clean = clean:clean
        addAntTarget( "clean", "clean:clean", "Clean" );
    }

    /**
     * Sets the jalopy ant target.
     */
    protected void setTargetJalopy()
    {
        // context-menu-custon-jalopy
        addAntTarget( "jalopy", "jalopy:format", "Format Code" );
    }

    /**
     * Sets the javadoc ant target.
     */
    protected void setTargetJavadoc()
    {
        // ide-action-javadoc = site:site
        addAntTarget( "javadoc", "site:site", "Javadoc" );
    }

    /**
     * Sets the refresh project ant target.
     */
    protected void setTargetRefreshProject()
    {
        // context-menu-custon-refresh-project (netbeans-freeform:generate-netbeans-project)
        if (mavenpath == null)
        {
            addAntTarget(
                "refresh-project",
                "netbeans-freeform:generate-netbeans-project",
                "Refresh Project"
            );
        }
        else
        {
            addAntTarget(
                "refresh-project",
                "netbeans-freeform:generate-netbeans-project",
                "-Dfreeform.mavenpath=" + mavenpath,
                "Refresh Project"
            );
        }
    }

    /**
     * Sets the run ant target.
     */
    protected void setTargetRun()
    {
        // ide-action-run = install
        addAntTarget( "run", "install", "Install Locally" );
    }

    /**
     * Sets the test ant target.
     */
    protected void setTargetTest()
    {
        // ide-action-test = test
        addAntTarget( "test", "test", "Test Project" );
    }

    /**
     * Sets the pom file view item.
     */
    protected void setViewPom()
    {
        // view-pom
        addFileViewItem( "pom", "pom.xml" );
    }

    /**
     * This method adds an AntTarget to the list of ant target for generating
     * the mavencall.xml file.
     *
     * @param comment The description of the target in the ant file.
     * @param name    The name and only goal of the AntTarget.
     */
    protected void addAntTarget(
        final String name,
        final String comment
    )
    {
        AntTarget antTarget = new AntTarget();
        antTarget.setName( name );
        antTarget.setComment( comment );
        antTarget.addGoal( name );
        antTargets.add( antTarget );
    }

    /**
     * This method adds an AntTarget to the list of ant target for generating
     * the mavencall.xml file.
     *
     * @param comment   The description of the target in the ant file.
     * @param name      The name of the AntTarget.
     * @param mavenGoal The only goal of the AntTarget.
     */
    protected void addAntTarget(
        final String name,
        final String mavenGoal,
        final String comment
    )
    {
        AntTarget antTarget = new AntTarget();
        antTarget.setName( name );
        antTarget.setComment( comment );
        antTarget.addGoal( mavenGoal );
        antTargets.add( antTarget );
    }

    /**
     * This method adds an AntTarget to the list of ant target for generating
     * the mavencall.xml file.
     *
     * @param option    The option line of the AntTarget
     * @param comment   The description of the target in the ant file.
     * @param name      The name of the AntTarget.
     * @param mavenGoal The only goal of the AntTarget.
     */
    protected void addAntTarget(
        final String name,
        final String mavenGoal,
        final String option,
        final String comment
    )
    {
        AntTarget antTarget = new AntTarget();
        antTarget.setName( name );
        antTarget.setComment( comment );
        antTarget.addGoal( mavenGoal );
        antTarget.addOption( option );
        antTargets.add( antTarget );
    }

    /**
     * This method adds a CompilationUnit to the FreeformProject.
     *
     * @param mavenProjectBasedir     The basedir of the maven project.
     * @param localRepositoryBasedir  The basedir of the local repository.
     * @param relativeSourceRoots     The list of source roots relative to the pom.
     * @param relativeOutputDirectory The output directory relative to the pom.
     * @param unitTest                If the CompilationUnit is an unit test one.
     * @param classpathElements       The classpath elements for this CompilationUnit.
     */
    protected void addCompilationUnit(
        final String relativeOutputDirectory,
        final boolean unitTest,
        final List relativeSourceRoots,
        final List classpathElements,
        final File mavenProjectBasedir,
        final File localRepositoryBasedir
    )
    {
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageRoot( relativeSourceRoots );
        compilationUnit.setBuiltTo( relativeOutputDirectory );
        compilationUnit.setSourceLevel( "1.4" );
        compilationUnit.setUnitTest( unitTest );
        compilationUnit.setClasspath(
            toRelativeArtifactPaths(
                classpathElements, mavenProjectBasedir, localRepositoryBasedir
            )
        );
        freeformProject.addCompilationUnit( compilationUnit );
    }

    /**
     * This method adds the compile source roots of the MavenProject and the
     * generated source roots of the mavenExecutedProject to the
     * FreeformProject.
     */
    protected void addCompileSourceRoots()
    {
        if (
            ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getCompileSourceRoots() != null )
            )
        {
            Iterator compileSourceRoots =
                mavenExecutedProject.getCompileSourceRoots().iterator();

            while ( compileSourceRoots.hasNext() )
            {
                String sourceRoot = (String) compileSourceRoots.next();
                addSourceFolderViewItem( sourceRoot );
            }
        }
    }

    /**
     * This method adds a CustomisedContextMenuItem to the FreeformProject
     * ContextMenu.
     *
     * @param name    The name (visible) of the ContextMenuItem.
     * @param targets The list of AntTarget names for the ContextMenuItem.
     */
    protected void addCustomisedContextMenuItem(
        final String name,
        final String targets
    )
    {
        CustomisedContextMenuItem customisedContextMenuItem =
            new CustomisedContextMenuItem();
        customisedContextMenuItem.setName( name );

        StringTokenizer stringTokenizer = new StringTokenizer( targets, " " );
        List targetList = new ArrayList();

        while ( stringTokenizer.hasMoreTokens() )
        {
            targetList.add( stringTokenizer.nextToken() );
        }

        customisedContextMenuItem.setTarget( targetList );
        customisedContextMenuItem.setScript( "${ant.script}" );
        freeformProject.addContextMenuItem( customisedContextMenuItem );
    }

    /**
     * This method adds a FileViewItem to the FreeformProject views.
     *
     * @param label    The label (visible) of the file.
     * @param location The location of the file relative to the pom.
     */
    protected void addFileViewItem(
        final String label,
        final String location
    )
    {
        FileViewItem fileViewItem = new FileViewItem();
        fileViewItem.setLabel( label );
        fileViewItem.setLocation( 
            ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
            location );
        freeformProject.addViewItem( fileViewItem );
    }

    /**
     * This adds a NamedNetbeansAction to the FreeformProject NetbeansActions.
     *
     * @param name    The name of the NetbeansAction.
     * @param targets The list of AntTarget for the NetbeansAction.
     */
    protected void addIdeAction(
        final String name,
        final String targets
    )
    {
        NamedNetbeansAction netbeansAction = new NamedNetbeansAction();
        netbeansAction.setName( name );
        netbeansAction.setScript( "${ant.script}" );

        StringTokenizer stringTokenizer = new StringTokenizer( targets, " " );
        List targetList = new ArrayList();

        while ( stringTokenizer.hasMoreTokens() )
        {
            targetList.add( stringTokenizer.nextToken() );
        }

        netbeansAction.setTarget( targetList );
        freeformProject.addNetbeansAction( netbeansAction );
    }

    /**
     * This method adds an IdeContextMenuItem to the FreeformProject
     * ContextMenu.
     *
     * @param name The name of the NamedNetbeansAction for the MenuItem.
     */
    protected void addIdeContextItem( final String name )
    {
        IdeContextMenuItem ideContextMenuItem = new IdeContextMenuItem();
        ideContextMenuItem.setName( name );
        freeformProject.addContextMenuItem( ideContextMenuItem );
    }

    /**
     * This method adds the CompilationUnit for the source roots of the
     * MavenProject and for the generated source roots of the
     * mavenExecutedProject to the FreeformProject.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void addMainCompilationUnits()
        throws FreeformPluginException
    {
        if (
            ( mavenProject != null ) && ( mavenProject.getBasedir() != null )
            && ( mavenProject.getBuild() != null )
            && ( mavenProject.getBuild().getOutputDirectory() != null )
            && ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getCompileSourceRoots() != null )
            )
        {
            String outputDirectory =
                mavenProject.getBuild().getOutputDirectory();
            String relativeOutputDirectory =
                ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
                toRelative(
                    mavenProject.getBasedir(),
                    outputDirectory
                );
            boolean unitTest = false;
            Iterator sourceRoots =
                mavenExecutedProject.getCompileSourceRoots().iterator();

            List relativeSourceRoots = new ArrayList();

            while ( sourceRoots.hasNext() )
            {
                String sourceRoot = (String) sourceRoots.next();
                String relativeSourceRoot =
                    ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
                    toRelative(
                        mavenProject.getBasedir(),
                        sourceRoot
                    );
                relativeSourceRoots.add( relativeSourceRoot );
            }

            List classpathElements = new ArrayList();

            Iterator iterator = mavenProject.getRuntimeArtifacts().iterator();

            while ( iterator.hasNext() )
            {
                Artifact artifact = (Artifact) iterator.next();
                classpathElements.add( artifact.getFile().getAbsolutePath() );
            }

            classpathElements.remove( relativeOutputDirectory );

            addCompilationUnit(
                relativeOutputDirectory,
                unitTest,
                relativeSourceRoots,
                classpathElements,
                mavenProject.getBasedir(),
                new File( localRepository.getBasedir() )
            );
        }
    }

    /**
     * This method adds the "ant.script" property leading to the mavencall.xml
     * file.
     */
    protected void addMavencallScript()
    {
        NamedProperty property = new NamedProperty();
        property.setName( "ant.script" );
        property.setValue( "nbproject/mavencall.xml" );
        freeformProject.addProperty( property );
    }

    /**
     * This method adds the reference to the property file for the netbeans
     * project.
     */
    protected void addPropertyFile()
    {
        FileProperty property = new FileProperty();
        property.setLocation( "nbproject/project.properties" );
        freeformProject.addProperty( property );
    }

    /**
     * This method adds a FolderViewItem with tree style to the FreeformProject
     * views.
     *
     * @param resourceFolder The name (visible) and location, relative to the
     *                       pom, of the FolderViewItem.
     */
    protected void addResourceFolderViewItem( final String resourceFolder )
    {
        String relativeResourceFolder =
            toRelative(
                mavenProject.getBasedir(),
                resourceFolder
            );
        FolderViewItem folderViewItem = new FolderViewItem();
        folderViewItem.setLabel( relativeResourceFolder );
        folderViewItem.setLocation( 
            ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
            relativeResourceFolder );
        folderViewItem.setStyle( "tree" );
        freeformProject.addViewItem( folderViewItem );
    }

    /**
     * This method adds the resource folders to the FreeformProject views.
     */
    protected void addResources()
    {
        if (
            ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getBuild() != null )
            && ( mavenExecutedProject.getBuild().getResources() != null )
            )
        {
            Iterator resources =
                mavenExecutedProject.getBuild().getResources().iterator();

            while ( resources.hasNext() )
            {
                String resource =
                    ( (Resource) resources.next() ).getDirectory();
                addResourceFolderViewItem( resource );
            }
        }
    }

    /**
     * This method adds the site folder to the FreeformProject views.
     */
    protected void addSiteDirectoy()
    {
        addResourceFolderViewItem( "src/site" );
    }

    /**
     * This method adds a SourceFolder to the FreeformProject SourceFolders.
     *
     * @param sourceRoot The name and location, relative to the pom, of the
     *                   SourceFolder.
     */
    protected void addSourceFolder( final String sourceRoot )
    {
        String relativeSourceRoot =
            toRelative(
                mavenProject.getBasedir(),
                sourceRoot
            );
        SourceFolder sourceFolder = new SourceFolder();
        sourceFolder.setType( "java" );
        sourceFolder.setLabel( relativeSourceRoot );
        sourceFolder.setLocation( 
            ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
            relativeSourceRoot );
        freeformProject.addSourceFolder( sourceFolder );
    }

    /**
     * This method adds a FolderViewItem with packages style to the
     * FreeformProject views.
     *
     * @param sourceFolder The name (visible) and location, relative to the pom,
     *                     of the FolderViewItem.
     */
    protected void addSourceFolderViewItem( final String sourceFolder )
    {
        String relativeSourceFolder =
            toRelative(
                mavenProject.getBasedir(),
                sourceFolder
            );
        FolderViewItem folderViewItem = new FolderViewItem();
        folderViewItem.setLabel( relativeSourceFolder );
        folderViewItem.setLocation( 
            ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
            relativeSourceFolder );
        folderViewItem.setStyle( "packages" );
        freeformProject.addViewItem( folderViewItem );
    }

    /**
     * This method adds the mavenExecutedProject source roots to the
     * FreeformProject source folders.
     */
    protected void addSourceFolders()
    {
        if (
            ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getCompileSourceRoots() != null )
            )
        {
            Iterator compileSourceRoots =
                mavenExecutedProject.getCompileSourceRoots().iterator();

            while ( compileSourceRoots.hasNext() )
            {
                String sourceRoot = (String) compileSourceRoots.next();
                addSourceFolder( sourceRoot );
            }
        }
    }

    /**
     * This method adds a Subprojectto the FreeformProject required projects.
     *
     * @param subprojectName The location of the subproject relative to the pom.
     */
    protected void addSubproject( final String subprojectName )
    {
        Subproject subproject = new Subproject();
        subproject.setSubproject( subprojectName );
        freeformProject.addSubproject( subproject );
    }

    /**
     * This method adds the target folder to the FreeformProject views.
     */
    protected void addTargetDirectory()
    {
        addResourceFolderViewItem( "target" );
    }

    /**
     * This method adds the CompilationUnit for the test source roots of the
     * MavenProject and for the generated test source roots of the
     * mavenExecutedProject to the FreeformProject.
     *
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void addTestCompilationUnits()
        throws FreeformPluginException
    {
        if (
            ( mavenProject != null ) && ( mavenProject.getBasedir() != null )
            && ( mavenProject.getBuild() != null )
            && ( mavenProject.getBuild().getTestOutputDirectory() != null )
            && ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getTestCompileSourceRoots() != null )
            )
        {
            String outputDirectory =
                mavenProject.getBuild().getTestOutputDirectory();
            String relativeOutputDirectory =
                ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
                toRelative(
                    mavenProject.getBasedir(),
                    outputDirectory
                );
            boolean unitTest = true;
            Iterator sourceRoots =
                mavenExecutedProject.getTestCompileSourceRoots().iterator();

            List relativeSourceRoots = new ArrayList();

            while ( sourceRoots.hasNext() )
            {
                String sourceRoot = (String) sourceRoots.next();
                String relativeSourceRoot =
                    ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
                    toRelative(
                        mavenProject.getBasedir(),
                        sourceRoot
                    );
                relativeSourceRoots.add( relativeSourceRoot );
            }

            List classpathElements = new ArrayList();

            classpathElements.add(
                mavenProject.getBuild().getOutputDirectory()
            );

            Iterator iterator = mavenProject.getTestArtifacts().iterator();

            while ( iterator.hasNext() )
            {
                Artifact artifact = (Artifact) iterator.next();
                classpathElements.add( artifact.getFile().getAbsolutePath() );
            }

            addCompilationUnit(
                relativeOutputDirectory,
                unitTest,
                relativeSourceRoots,
                classpathElements,
                mavenProject.getBasedir(),
                new File( localRepository.getBasedir() )
            );
        }
    }

    /**
     * This method adds the test source roots of the MavenProject and the
     * generated test source roots of the mavenExecutedProject to the
     * FreeformProject.
     */
    protected void addTestCompileSourceRoots()
    {
        if (
            ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getTestCompileSourceRoots() != null )
            )
        {
            Iterator testCompilesourceRoots =
                mavenExecutedProject.getTestCompileSourceRoots().iterator();

            while ( testCompilesourceRoots.hasNext() )
            {
                String resource = (String) testCompilesourceRoots.next();
                addSourceFolderViewItem( resource );
            }
        }
    }

    /**
     * This method adds the test resource folders to the FreeformProject views.
     */
    protected void addTestResources()
    {
        if (
            ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getBuild() != null )
            && ( mavenExecutedProject.getBuild().getTestResources() != null )
            )
        {
            Iterator testResources =
                mavenExecutedProject.getBuild().getTestResources().iterator();

            while ( testResources.hasNext() )
            {
                String testResource =
                    ( (Resource) testResources.next() ).getDirectory();
                addResourceFolderViewItem( testResource );
            }
        }
    }

    /**
     * This method adds the mavenExecutedProject test source roots to the
     * FreeformProject source folders.
     */
    protected void addTestSourceFolders()
    {
        if (
            ( mavenExecutedProject != null )
            && ( mavenExecutedProject.getTestCompileSourceRoots() != null )
            )
        {
            Iterator testCompileSourceRoots =
                mavenExecutedProject.getTestCompileSourceRoots().iterator();

            while ( testCompileSourceRoots.hasNext() )
            {
                String sourceRoot = (String) testCompileSourceRoots.next();
                addSourceFolder( sourceRoot );
            }
        }
    }

    /**
     * This method, given a list of classpath elements with absobute paths,
     * returns the same list of path elements with paths relative to the
     * mavenProjectBasedir or the localRepositoryBasedir.
     *
     * @param classpathElements      The list of classpath elements.
     * @param mavenProjectBasedir    The basedir of the maven project.
     * @param localRepositoryBasedir The basedir of the local repository.
     * @return The rlativised list of classpath elements.
     */
    protected List toRelativeArtifactPaths(
        final List classpathElements,
        final File mavenProjectBasedir,
        final File localRepositoryBasedir
    )
    {
        List relativeclasspathElements = new ArrayList();
        Iterator iterator = classpathElements.iterator();

        while ( iterator.hasNext() )
        {
            String classpathElement = (String) iterator.next();

            String relativeclasspathElement =
                toRelative( localRepositoryBasedir, classpathElement );

            if (
                relativeclasspathElement.length() == classpathElement.length()
                )
            {
                relativeclasspathElement =
                    ( freeformProject.isUseOutputDirectory() ? "${project.directory}/" : "" ) +
                    toRelative( mavenProjectBasedir, classpathElement );
            }
            else
            {
                relativeclasspathElement =
                    "${local.repository}/" + relativeclasspathElement;
            }

            relativeclasspathElements.add( relativeclasspathElement );
        }

        return relativeclasspathElements;
    }

    /**
     * Sets the mavencall file view item.
     */
    private void setViewMavencall()
    {
        // view-mavencall
        addFileViewItem( "mavencall", "nbproject/mavencall.xml" );
    }
    
    //-----------------------------------------------------------
    //~ Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
    //-----------------------------------------------------------
 
    /**
     * This method adds the "custom.script" property that references the ide-file-targets.xml
     * file.
     */
    protected void addCustomScript()
    {
        NamedProperty property = new NamedProperty();
        property.setName( FreeformConstants.CUSTOM_SCRIPT );
        property.setValue( "nbproject/ide-file-targets.xml" );
        freeformProject.addProperty( property );
    }    
    
    
    
    
}
