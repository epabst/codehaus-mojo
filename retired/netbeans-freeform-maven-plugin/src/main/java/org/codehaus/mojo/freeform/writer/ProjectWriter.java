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
import org.codehaus.mojo.freeform.FreeformConstants;
import org.codehaus.mojo.freeform.FreeformPluginException;
import org.codehaus.mojo.freeform.project.CompilationUnit;
import org.codehaus.mojo.freeform.project.ContextMenuItem;
import org.codehaus.mojo.freeform.project.CustomisedContextMenuItem;
import org.codehaus.mojo.freeform.project.FileProperty;
import org.codehaus.mojo.freeform.project.FileViewItem;
import org.codehaus.mojo.freeform.project.FolderViewItem;
import org.codehaus.mojo.freeform.project.FreeformProject;
import org.codehaus.mojo.freeform.project.IdeContextMenuItem;
import org.codehaus.mojo.freeform.project.NamedNetbeansAction;
import org.codehaus.mojo.freeform.project.NamedProperty;
import org.codehaus.mojo.freeform.project.NetbeansAction;
import org.codehaus.mojo.freeform.project.Property;
import org.codehaus.mojo.freeform.project.SourceFolder;
import org.codehaus.mojo.freeform.project.Subproject;
import org.codehaus.mojo.freeform.project.ViewItem;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;


/**
 * This class represents the Writer for a Netbeans Freeform Project descriptor.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public class ProjectWriter
{
    /**
     * Holds the project descriptor file to write the FreeformProject in.
     */
    private File projectFile;

    /**
     * Holds the FreeformProject to write in the file.
     */
    private FreeformProject freeformProject;

    /**
     * The maven plugin logger.
     */
    private Log log;

    /**
     * The MavenProject used for logging.
     */
    private MavenProject mavenProject;

    /**
     * Creates a new instance of ProjectWriter.
     *
     * @param freeformProject the FreeformProject to write in the file.
     * @param projectFile     the project descriptor file to write the
     *                        FreeformProject in.
     * @param mavenProject    The MavenProject
     * @param log             The maven plugin logger.
     */
    public ProjectWriter(
        final FreeformProject freeformProject,
        final File projectFile,
        final MavenProject mavenProject,
        final Log log
    )
    {
        this.mavenProject = mavenProject;
        this.projectFile = projectFile;
        this.freeformProject = freeformProject;
        this.log = log;
    }

    /**
     * This is the main method called on the Class for writing the project
     * descriptor file. It creates the XMLWriter for the file.
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
            fileWriter = new FileWriter( projectFile );
        }
        catch ( IOException ioe )
        {
            throw new FreeformPluginException(
                "Exception while opening file.", ioe
            );
        }

        XMLWriter xMLWriter = new PrettyPrintXMLWriter( fileWriter );

        writeProjectXml( xMLWriter );

        close( fileWriter );

        log.debug( "Wrote project.xml for " + mavenProject.getName() );
    }

    /**
     * This method write an CompilationUnit to the given XMLWriter.
     *
     * @param compilationUnit The CompilationUnit to write.
     * @param writer          The XMLWriter to write the CompilationUnit in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeCompilationUnit(
        final CompilationUnit compilationUnit,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "compilation-unit" );

        Iterator packageRoots = compilationUnit.getPackageRoot().iterator();

        while ( packageRoots.hasNext() )
        {
            writer.startElement( "package-root" );

            writer.writeText( (String) packageRoots.next() );

            writer.endElement(); // package-root
        }

        if ( compilationUnit.isUnitTest() )
        {
            writer.startElement( "unit-tests" );

            writer.endElement(); // unit-tests
        }

        writer.startElement( "classpath" );
        writer.addAttribute( "mode", "compile" );

        String classpath = "";

        Iterator iterator = compilationUnit.getClasspath().iterator();

        while ( iterator.hasNext() )
        {
            if ( classpath.length() > 0 )
            {
                classpath += ":";
            }

            classpath += (String) iterator.next();
        }

        writer.writeText( classpath );

        writer.endElement(); // classpath

        writer.startElement( "built-to" );

        writer.writeText( compilationUnit.getBuiltTo() );

        writer.endElement(); // built-to

        writer.startElement( "source-level" );

        writer.writeText( compilationUnit.getSourceLevel() );

        writer.endElement(); // source-level

        writer.endElement(); // compilation-unit

        log.debug(
            "compilationUnit " + compilationUnit.getPackageRoot() + " wrote"
        );
    }

    /**
     * This method write a List of CompilationUnits to the given XMLWriter.
     *
     * @param compilationUnits The List of CompilationUnits to write.
     * @param writer           The XMLWriter to write the List in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeCompilationUnits(
        final List compilationUnits,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = compilationUnits.iterator();

        while ( iterator.hasNext() )
        {
            CompilationUnit compilationUnit =
                (CompilationUnit) iterator.next();

            writeCompilationUnit( compilationUnit, writer );
        }
    }

    /**
     * This method write a ContextMenuItem to the given XMLWriter.
     *
     * @param contextMenuItem The ContextMenuItem to write.
     * @param writer          The XMLWriter to write the ContextMenuItem in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeContextMenuItem(
        final ContextMenuItem contextMenuItem,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        log.warn( "contextMenuItem " + contextMenuItem + " unknown" );
    }

    /**
     * This method write an IdeContextMenuItem to the given XMLWriter.
     *
     * @param contextMenuItem The IdeContextMenuItem to write.
     * @param writer          The XMLWriter to write the IdeContextMenuItem in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeContextMenuItem(
        final IdeContextMenuItem contextMenuItem,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "ide-action" );
        writer.addAttribute(
            "name",
            contextMenuItem.getName()
        );

        writer.endElement(); // ide-action

        log.debug( "contextMenuItem " + contextMenuItem.getName() + " wrote" );
    }

    /**
     * This method write a CustomisedContextMenuItem to the given XMLWriter.
     *
     * @param contextMenuItem The CustomisedContextMenuItem to write.
     * @param writer          The XMLWriter to write the CustomisedContextMenuItem in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeContextMenuItem(
        final CustomisedContextMenuItem contextMenuItem,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "action" );

        writer.startElement( "script" );

        writer.writeText( contextMenuItem.getScript() );

        writer.endElement(); // script

        writer.startElement( "label" );

        writer.writeText( contextMenuItem.getName() );

        writer.endElement(); // label

        Iterator iterator = contextMenuItem.getTarget().iterator();

        while ( iterator.hasNext() )
        {
            writer.startElement( "target" );

            writer.writeText( (String) iterator.next() );

            writer.endElement(); // target
        }

        writer.endElement(); // action

        log.debug( "contextMenuItem " + contextMenuItem.getName() + " wrote" );
    }

    /**
     * This method write a List of ContextMenuItems to the given XMLWriter.
     *
     * @param contextMenuItems The List of ContextMenuItems to write.
     * @param writer           The XMLWriter to write the List in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeContextMenuItems(
        final List contextMenuItems,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = contextMenuItems.iterator();

        while ( iterator.hasNext() )
        {
            ContextMenuItem contextMenuItem =
                (ContextMenuItem) iterator.next();

            if ( contextMenuItem instanceof IdeContextMenuItem )
            {
                writeContextMenuItem(
                    (IdeContextMenuItem) contextMenuItem, writer
                );
            }
            else if ( contextMenuItem instanceof CustomisedContextMenuItem )
            {
                writeContextMenuItem(
                    (CustomisedContextMenuItem) contextMenuItem, writer
                );
            }
            else
            {
                writeContextMenuItem( contextMenuItem, writer );
            }
        }
    }

    /**
     * This method write a NetbeansAction to the given XMLWriter.
     *
     * @param netbeansAction The NetbeansAction to write.
     * @param writer         The XMLWriter to write the NetbeansAction in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeIdeAction(
        final NetbeansAction netbeansAction,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        log.warn( "netbeansAction " + netbeansAction + " unknown" );
    }

    /**
     * This method write a NamedNetbeansAction to the given XMLWriter.
     *
     * @param netbeansAction The NamedNetbeansAction to write.
     * @param writer         The XMLWriter to write the NamedNetbeansAction in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeIdeAction(
        final NamedNetbeansAction netbeansAction,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "action" );
        writer.addAttribute(
            "name",
            netbeansAction.getName()
        );

        writer.startElement( "script" );

        writer.writeText( netbeansAction.getScript() );

        writer.endElement(); // script

        Iterator iterator = netbeansAction.getTarget().iterator();

        while ( iterator.hasNext() )
        {
            writer.startElement( "target" );

            writer.writeText( (String) iterator.next() );

            writer.endElement(); // target
        }

        writer.endElement(); // action

        log.debug( "netbeansAction " + netbeansAction.getName() + " wrote" );
    }

    /**
     * This method write a List of NamedNetbeansActions to the given XMLWriter.
     *
     * @param netbeansActions The List of NamedNetbeansActions to write.
     * @param writer          The XMLWriter to write the List of NamedNetbeansActions in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeIdeActions(
        final List netbeansActions,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = netbeansActions.iterator();

        while ( iterator.hasNext() )
        {
            NetbeansAction netbeansAction = (NetbeansAction) iterator.next();

            if ( netbeansAction instanceof NamedNetbeansAction )
            {
                writeIdeAction( (NamedNetbeansAction) netbeansAction, writer );
            }
            else
            {
                writeIdeAction( netbeansAction, writer );
            }
        }
        
        // Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
        
        writeCompileSingleInMain(writer);
        writeCompileSingleInTest(writer);
        writeRunSingleInMain(writer);
        writeRunSingleInTest(writer);
        writeDebugSingleInMain(writer);
        writeDebugSingleInTest(writer);
        
        
    }

    /**
     * This method write the name of the project to the given XMLWriter.
     *
     * @param name   The name of the project to write.
     * @param writer The XMLWriter to write the name of the project in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeName(
        final String name,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "name" );

        writer.writeText( name );

        writer.endElement(); // name

        log.debug( "name " + name + " wrote" );
    }

    /**
     * This method write the FreeformProject in the XMLWriter.
     *
     * @param writer The XMLWriter to write the FreeformProject in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeProjectXml( final XMLWriter writer )
        throws FreeformPluginException
    {
        writer.startElement( "project" );
        writer.addAttribute( "xmlns", "http://www.netbeans.org/ns/project/1" );

        writer.startElement( "type" );
        writer.writeText( "org.netbeans.modules.ant.freeform" );
        writer.endElement(); // type

        writer.startElement( "configuration" );

        writer.startElement( "general-data" );
        writer.addAttribute(
            "xmlns", "http://www.netbeans.org/ns/freeform-project/1"
        );

        writeName(
            freeformProject.getName(),
            writer
        );

        writer.startElement( "properties" );
        writeProperties(
            freeformProject.getProperties(),
            writer
        );
        writer.endElement(); // properties

        writer.startElement( "folders" );
        writeSourceFolders(
            freeformProject.getSourceFolders(),
            writer
        );
        writer.endElement(); // folders

        writer.startElement( "ide-actions" );
        writeIdeActions(
            freeformProject.getNetbeansActions(),
            writer
        );
        writer.endElement(); // ide-actions

        writer.startElement( "view" );

        writer.startElement( "items" );
        writeViewItems(
            freeformProject.getViewItems(),
            writer
        );
        writer.endElement(); // items

        writer.startElement( "context-menu" );
        writeContextMenuItems(
            freeformProject.getContextMenuItems(),
            writer
        );
        writer.endElement(); // context-menu

        writer.endElement(); // view

        writer.startElement( "subprojects" );
        writeSubprojects(
            freeformProject.getSubprojects(),
            writer
        );
        writer.endElement(); // subprojects

        writer.endElement(); // general-data

        writer.startElement( "java-data" );
        writer.addAttribute(
            "xmlns", "http://www.netbeans.org/ns/freeform-project-java/2"
        );

        writeCompilationUnits(
            freeformProject.getCompilationUnits(),
            writer
        );

        writer.endElement(); // java-data

        writer.endElement(); // configuration

        writer.endElement(); // target
    }

    /**
     * This method write a List of property to the given XMLWriter.
     *
     * @param properties The List of property to write.
     * @param writer     The XMLWriter to write the List of property in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeProperties(
        final List properties,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = properties.iterator();

        while ( iterator.hasNext() )
        {
            Property property = (Property) iterator.next();

            if ( property instanceof NamedProperty )
            {
                writeProperty( (NamedProperty) property, writer );
            }
            else if ( property instanceof FileProperty )
            {
                writeProperty( (FileProperty) property, writer );
            }
            else
            {
                writeProperty( property, writer );
            }
        }
    }

    /**
     * This method write a Property to the given XMLWriter.
     *
     * @param property The Property to write.
     * @param writer   The XMLWriter to write the Property in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeProperty(
        final Property property,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        log.warn( "property " + property + " unknown" );
    }

    /**
     * This method write a NamedProperty to the given XMLWriter.
     *
     * @param property The NamedProperty to write.
     * @param writer   The XMLWriter to write the NamedProperty in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeProperty(
        final NamedProperty property,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "property" );
        writer.addAttribute(
            "name",
            property.getName()
        );

        writer.writeText( property.getValue() );

        writer.endElement(); // property

        log.debug( "property " + property.getName() + " wrote" );
    }

    /**
     * This method write a FileProperty to the given XMLWriter.
     *
     * @param property The FileProperty to write.
     * @param writer   The XMLWriter to write the FileProperty in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeProperty(
        final FileProperty property,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "property-file" );

        writer.writeText( property.getLocation() );

        writer.endElement(); // property-file

        log.debug( "property " + property.getLocation() + " wrote" );
    }

    /**
     * This method write a SourceFolder to the given XMLWriter.
     *
     * @param sourceFolder The SourceFolder to write.
     * @param writer       The XMLWriter to write the SourceFolder in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeSourceFolder(
        final SourceFolder sourceFolder,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "source-folder" );

        writer.startElement( "label" );

        writer.writeText( sourceFolder.getLabel() );

        writer.endElement(); // label

        writer.startElement( "type" );

        writer.writeText( sourceFolder.getType() );

        writer.endElement(); // type

        writer.startElement( "location" );

        writer.writeText( sourceFolder.getLocation() );

        writer.endElement(); // location

        writer.endElement(); // source-folder

        log.debug( "sourceFolder " + sourceFolder.getLabel() + " wrote" );
    }

    /**
     * This method write a List of SourceFolders to the given XMLWriter.
     *
     * @param sourceFolders The List of SourceFolders to write.
     * @param writer        The XMLWriter to write the List of SourceFolders in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeSourceFolders(
        final List sourceFolders,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = sourceFolders.iterator();

        while ( iterator.hasNext() )
        {
            SourceFolder sourceFolder = (SourceFolder) iterator.next();

            writeSourceFolder( sourceFolder, writer );
        }
    }

    /**
     * This method write a List of Subproject to the given XMLWriter.
     *
     * @param subprojects The List of Subproject to write.
     * @param writer      The XMLWriter to write the List of SourceFolders in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeSubprojects(
        final List subprojects,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = subprojects.iterator();

        while ( iterator.hasNext() )
        {
            Subproject subproject = (Subproject) iterator.next();

            writer.startElement( "project" );

            writer.writeText( subproject.getSubproject() );

            writer.endElement(); // project

            log.debug( "subproject " + subproject.getSubproject() + " wrote" );
        }
    }

    /**
     * This method write a ViewItem to the given XMLWriter.
     *
     * @param viewItem The ViewItem to write.
     * @param writer   The XMLWriter to write the ViewItem in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeViewItem(
        final ViewItem viewItem,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        log.warn( "viewItem " + viewItem + " unknown" );
    }

    /**
     * This method write a FileViewItem to the given XMLWriter.
     *
     * @param viewItem The FileViewItem to write.
     * @param writer   The XMLWriter to write the FileViewItem in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeViewItem(
        final FileViewItem viewItem,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "source-file" );

        writer.startElement( "label" );

        writer.writeText( viewItem.getLabel() );

        writer.endElement(); // label

        writer.startElement( "location" );

        writer.writeText( viewItem.getLocation() );

        writer.endElement(); // location

        writer.endElement(); // source-file

        log.debug( "viewItem " + viewItem.getLabel() + " wrote" );
    }

    /**
     * This method write a FolderViewItem to the given XMLWriter.
     *
     * @param viewItem The FolderViewItem to write.
     * @param writer   The XMLWriter to write the FolderViewItem in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeViewItem(
        final FolderViewItem viewItem,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        writer.startElement( "source-folder" );
        writer.addAttribute(
            "style",
            viewItem.getStyle()
        );

        writer.startElement( "label" );

        writer.writeText( viewItem.getLabel() );

        writer.endElement(); // label

        writer.startElement( "location" );

        writer.writeText( viewItem.getLocation() );

        writer.endElement(); // location

        writer.endElement(); // source-folder

        log.debug( "viewItem " + viewItem.getLabel() + " wrote" );
    }

    /**
     * This method write a List of ViewItems to the given XMLWriter.
     *
     * @param viewItems The List of ViewItems to write.
     * @param writer    The XMLWriter to write the List of ViewItems in.
     * @throws org.codehaus.mojo.freeform.FreeformPluginException
     *          if something goes wrong.
     */
    protected void writeViewItems(
        final List viewItems,
        final XMLWriter writer
    )
        throws FreeformPluginException
    {
        Iterator iterator = viewItems.iterator();

        while ( iterator.hasNext() )
        {
            ViewItem viewItem = (ViewItem) iterator.next();

            if ( viewItem instanceof FileViewItem )
            {
                writeViewItem( (FileViewItem) viewItem, writer );
            }
            else if ( viewItem instanceof FolderViewItem )
            {
                writeViewItem( (FolderViewItem) viewItem, writer );
            }
            else
            {
                writeViewItem( viewItem, writer );
            }
        }
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
            log.warn( "The closure of " + projectFile + " can not be done", e );
        }
    }
    
    //-----------------------------------------------------------
    //~ Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
    //-----------------------------------------------------------
    
    //TODO generalize this: figure out the source and target dirs from the pom file
    //at this moment the maven defaults are hardwired
    
    public static final String ACTION_TAG = "action";
    public static final String SCRIPT_TAG = "script";
    public static final String TARGET_TAG = "target";
    public static final String CONTEXT_TAG = "context";
    public static final String PROPERTY_TAG = "property";
    public static final String FOLDER_TAG = "folder";    
    public static final String PATTERN_TAG = "pattern";
    public static final String FORMAT_TAG = "format";
    public static final String ARITY_TAG = "arity";
    public static final String SEPARATED_FILES_TAG = "separated-files";
    public static final String ONE_FILE_ONLY_TAG = "one-file-only";
    
    public static final String NAME_ATTR = "name";
    
    /**
     * Writes the custom IDE action to the passed <code>XMLWriter</code> based on the given
     * <code>CustomActionConfig</code>.  
     * @param config The configuration of this custom action
     * @param writer The <code>XMLWriter</code> to be used
     */
    private void writeCustomAction(CustomActionConfig config,  final XMLWriter writer) {
    	
    	final String actionName = config.getActionName();
    	final String script = config.getScript();
    	final String target = config.getTarget();
    	final String contextProperty = config.getContextProperty();
    	final String contextFolder = config.getContextFolder();
    	final String contextPattern = config.getContextPattern();
    	final String contextFormat = config.getContextFormat();
    	final boolean singleOnly = config.isSingleOnly();
    	final String separatedFiles = config.getSeparatedFiles();
    	
    	writer.startElement(ACTION_TAG);
    	writer.addAttribute(NAME_ATTR, actionName);
    	
    	writer.startElement(SCRIPT_TAG);
    	writer.writeText(script);
    	writer.endElement(); //script
    	
    	writer.startElement(TARGET_TAG);
    	writer.writeText(target);
    	writer.endElement(); //target
    	
    	writer.startElement(CONTEXT_TAG);
    	
    	writer.startElement(PROPERTY_TAG);   	
    	writer.writeText(contextProperty);
    	writer.endElement(); //property
    	
    	writer.startElement(FOLDER_TAG);   	
    	writer.writeText(contextFolder);
    	writer.endElement(); //folder
    	
    	writer.startElement(PATTERN_TAG);
    	writer.writeText(contextPattern);
    	writer.endElement(); //pattern
    	
    	writer.startElement(FORMAT_TAG);
    	writer.writeText(contextFormat);
    	writer.endElement(); //format
    	
    	writer.startElement(ARITY_TAG);
    	
    	if(singleOnly == true){
    		writer.startElement(ONE_FILE_ONLY_TAG);
    		writer.endElement(); //one-file-only
    	}else{
    		writer.startElement(SEPARATED_FILES_TAG);
    		writer.writeText(separatedFiles);
    		writer.endElement(); //separated files
    	}
    	    	
    	writer.endElement(); //arity
    	
    	
    	writer.endElement(); //context   	
    	
    	
    	writer.endElement(); //action    	
    }
    
    /**
     * Creates the binding between the compile.single IDE action and the compile-selected-files-in-main
     * Ant target.
     * <b>Note:</b>The bindings can be overloded to use the same IDE action in different
     * contexts that is defined by the context tag. See the next one (<code>writeCompileSingleInTest(XMLWriter writer)</code>)
     * for an example.
     * @param writer The <code>XMLWriter</code> to be used
     */
    private void writeCompileSingleInMain(XMLWriter writer) {
    	CustomActionConfig config = new CustomActionConfig();
    	
    	config.setActionName("compile.single");
    	config.setTarget(FreeformConstants.COMPILE_SELECTED_FILES_IN_MAIN);
    	config.setContextProperty("files");
    	config.setContextFolder("src/main/java");
    	config.setContextPattern("\\.java$");
    	config.setContextFormat("relative-path");
    	config.setSingleOnly(false);
    	
    	writeCustomAction(config, writer);    	
    }
    
    /**
     * Creates the binding between the compile.single IDE action and the compile-selected-files-in-test
     * Ant target.
     * @param writer The <code>XMLWriter</code> to be used
     */
    private void writeCompileSingleInTest(XMLWriter writer) {
    	CustomActionConfig config = new CustomActionConfig();
    	
    	config.setActionName("compile.single");
    	config.setTarget(FreeformConstants.COMPILE_SELECTED_FILES_IN_TEST);
    	config.setContextProperty("files");
    	config.setContextFolder("src/test/java");
    	config.setContextPattern("\\.java$");
    	config.setContextFormat("relative-path");
    	config.setSingleOnly(false);
    	
    	writeCustomAction(config, writer);    	
    }

    /**
     * Creates the binding between the run.single IDE action and the run-selected-files-in-main
     * Ant target.
     * @param writer The <code>XMLWriter</code> to be used
     */    
    private void writeRunSingleInMain(XMLWriter writer) {
    	CustomActionConfig config = new CustomActionConfig();
    	
    	config.setActionName("run.single");
    	config.setTarget(FreeformConstants.RUN_SELECTED_FILES_IN_MAIN);
    	config.setContextProperty("classname");
    	config.setContextFolder("src/main/java");
    	config.setContextPattern("\\.java$");
    	config.setContextFormat("java-name");
    	config.setSingleOnly(true);
    	
    	writeCustomAction(config, writer);    	
    }

    /**
     * Creates the binding between the run.single IDE action and the run-selected-files-in-test
     * Ant target.
     * @param writer The <code>XMLWriter</code> to be used
     */       
    private void writeRunSingleInTest(XMLWriter writer) {
    	CustomActionConfig config = new CustomActionConfig();
    	
    	config.setActionName("run.single");
    	config.setTarget(FreeformConstants.RUN_SELECTED_FILES_IN_TEST);
    	config.setContextProperty("classname");
    	config.setContextFolder("src/test/java");
    	config.setContextPattern("\\.java$");
    	config.setContextFormat("java-name");
    	config.setSingleOnly(true);
    	
    	writeCustomAction(config, writer);    	
    }
    
    /**
     * Creates the binding between the debug.single IDE action and the debug-selected-files-in-main
     * Ant target.
     * @param writer The <code>XMLWriter</code> to be used
     */       
    private void writeDebugSingleInMain(XMLWriter writer) {
    	CustomActionConfig config = new CustomActionConfig();
    	
    	config.setActionName("debug.single");
    	config.setTarget(FreeformConstants.DEBUG_SELECTED_FILES_IN_MAIN);
    	config.setContextProperty("classname");
    	config.setContextFolder("src/main/java");
    	config.setContextPattern("\\.java$");
    	config.setContextFormat("java-name");
    	config.setSingleOnly(true);
    	
    	writeCustomAction(config, writer);    	
    }
    
    /**
     * Creates the binding between the debug.single IDE action and the debug-selected-files-in-test
     * Ant target.
     * @param writer The <code>XMLWriter</code> to be used
     */        
    private void writeDebugSingleInTest(XMLWriter writer) {
    	CustomActionConfig config = new CustomActionConfig();
    	
    	config.setActionName("debug.single");
    	config.setTarget(FreeformConstants.DEBUG_SELECTED_FILES_IN_TEST);
    	config.setContextProperty("classname");
    	config.setContextFolder("src/test/java");
    	config.setContextPattern("\\.java$");
    	config.setContextFormat("java-name");
    	config.setSingleOnly(true);
    	
    	writeCustomAction(config, writer);    	
    }

    
    
    
    
    
    
    
    
    
}
