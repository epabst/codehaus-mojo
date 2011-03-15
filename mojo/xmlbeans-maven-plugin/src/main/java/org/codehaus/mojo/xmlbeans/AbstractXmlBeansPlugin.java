package org.codehaus.mojo.xmlbeans;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.xmlbeans.impl.tool.SchemaCompiler;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.xml.sax.EntityResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p/>
 * A Maven 2 plugin which parses xsd files and produces a corresponding object
 * model based on the Apache XML Beans parser.
 * </p>
 * <p/>
 * The plugin produces two sets of output files referred to as generated sources
 * and generated classes. The former is then compiled to the build
 * <code>outputDirectory</code>. The latter is generated in this directory.
 * </p>
 * <p/>
 * Note that the descriptions for the goal's parameters have been blatently
 * copied from http://xmlbeans.apache.org/docs/2.0.0/guide/antXmlbean.html for
 * convenience.
 * </p>
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @author <a href="mailto:kris.bravo@corridor-software.us">Kris Bravo</a>
 * @version $Id$
 * @noinspection UnusedDeclaration
 */
public abstract class AbstractXmlBeansPlugin extends AbstractMojo implements PluginProperties
{

    /**
     * Define the name of the jar file created. For instance, "myXMLBean.jar"
     * will output the results of this task into a jar with the same name.
     *
     * @parameter
     */
    private File outputJar;

    /**
     * Define a set of Namespaces for which to ignore duplicate errors.
     * 
     * @parameter
     */
    private Set mdefNamespaces;
    
    /**
     * Set to true to permit the compiler to download URLs for imports and
     * includes. Defaults to false, meaning all imports and includes must be
     * copied locally.
     *
     * @parameter default-value="false"
     */
    private boolean download;

    /**
     * Indicates whether source should be compiled with debug information;
     * defaults to off. If set to off, -g:none will be passed on the command
     * line for compilers that support it (for other compilers, no command line
     * argument will be used). If set to true, the value of the debug level
     * attribute determines the command line argument.
     *
     * @parameter default-value="false"
     */
    private boolean debug;

    /**
     * The initial size of the memory for the underlying VM, if javac is run
     * externally; ignored otherwise. Defaults to the standard VM memory
     * setting. (Examples: 83886080, 81920k, or 80m)
     *
     * @parameter
     */
    private String memoryInitialSize;

    /**
     * The maximum size of the memory for the underlying VM, if javac is run
     * externally; ignored otherwise. Defaults to the standard VM memory
     * setting. (Examples: 83886080, 81920k, or 80m)
     *
     * @parameter
     */
    private String memoryMaximumSize;

    /**
     * The compiler implementation to use. If this attribute is not set, the
     * value of the build.compiler property, if set, will be used. Otherwise,
     * the default compiler for the current VM will be used.
     *
     * @parameter
     */
    private String compiler;

    /**
     * Controls the amount of build message output.
     *
     * @parameter default-value="false"
     */
    private boolean verbose;

    /**
     * Supress the normal amount of console output.
     *
     * @parameter default-value="true"
     */
    private boolean quiet = true;

    /**
     * Do not enforce the unique particle attribution rule.
     *
     * @parameter default-value="false"
     */
    private boolean noUpa;

    /**
     * Do not enforce the particle valid (restriction) rule.
     *
     * @parameter default-value="false"
     */
    private boolean noPvr;

    /**
     * Todo: Unkown use.
     *
     * @parameter default-value="false"
     */
    private boolean jaxb;

    /**
     * Don't compile the generated source files.
     *
     * @parameter default-value="false"
     */
    private boolean noJavac;

    /**
     * Ignore annotations
     *
     * @parameter default-value="false"
     */
    private boolean noAnn;

    /**
     * do not validate documentation elements
     *
     * @parameter default-value="false"
     */
    private boolean noVDoc;

    /**
     * The location of the catalog used to resolve xml entities.
     *
     * @parameter expression="${xmlbeans.catalogLocation}" 
     *     default-value="${basedir}/src/main/catalog/resolver-catalog.xml"
     */
    protected File catalogLocation;

    /**
     * A <code>List</code> of source schema files.
     *
     * @parameter
     */
    private List sourceSchemas;

    /**
     * Configuration files used by the object generator. For more information
     * about the format of these files, see Todo.
     *
     * @parameter
     */
    private List xmlConfigs;

    /**
     * Returns the javasource parameter which specifies an option to the
     * XmlBeans code generator.
     *
     * @parameter
     * @return null.
     */
    private String javaSource;

    /**
     * @parameter expression="${project.artifactMap}"
     * @required
     * @readonly
     */
    private Map artifactMap;

    /**
     * A reference to the Maven Project metadata.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Used to find resources used by the XML compiler. Currently not passed to
     * the compiler, since everything is on the classpath.
     */
    private EntityResolver entityResolver = null;

    /**
     *
     */
    protected static final File[] EMPTY_FILE_ARRAY = new File[0];

    /**
     * Files to parse and generate models for.
     */
    private File[] xsdFiles;

    private File[] wsdlFiles;

    /**
     * Empty constructor for the XML Beans plugin.
     */
    public AbstractXmlBeansPlugin()
    {
    }

    /**
     * <p/>
     * Map the parameters to the schema compilers parameter object, make sure the necessary output directories exist,
     * then call on the schema compiler to produce the java objects and supporting resources.
     * </p>
     * 
     * @throws MojoExecutionException Errors occurred during compile.
     * @number MOJO-270
     */
    public final void execute()
        throws MojoExecutionException
    {

        if ( hasSchemas() )
        {
            try
            {
                SchemaCompiler.Parameters compilerParams = ParameterAdapter.getCompilerParameters( this );
                boolean stale = isOutputStale();
                if ( stale )
                {
                    try
                    {
                        compilerParams.getSrcDir().mkdirs();

                        boolean result = SchemaCompiler.compile( compilerParams );

                        if ( !result )
                        {
                            StringBuffer errors = new StringBuffer();
                            for ( Iterator iter = compilerParams.getErrorListener().iterator(); iter.hasNext(); )
                            {
                                Object o = iter.next();
                                errors.append( "xml Error" ).append( o );
                                errors.append( "\n" );
                            }
                            throw new XmlBeansException( XmlBeansException.COMPILE_ERRORS, errors.toString() );
                        }

                        touchStaleFile();
                    }
                    catch ( IOException ioe )
                    {
                        throw new XmlBeansException( XmlBeansException.STALE_FILE_TOUCH,
                                                     getStaleFile().getAbsolutePath(), ioe );
                    }

                }
                else if ( getLog().isInfoEnabled() )
                {
                    getLog().info( "All schema objects are up to date." );
                }
                updateProject( project, compilerParams, stale );
            }
            catch ( DependencyResolutionRequiredException drre )
            {
                throw new XmlBeansException( XmlBeansException.CLASSPATH_DEPENDENCY, drre );
            }
        }
        else if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Nothing to generate." );
        }
    }

    /**
     * Indicates whether or not there are schemas to compile.
     *
     * @return true if there are schema files in the source or artifacts.
     * @throws XmlBeansException if we cannot determine if there are xsd files
     */
    private boolean hasSchemas() throws XmlBeansException
    {
        int xsds = getXsdFiles().length;
        int wsdls = getWsdlFiles().length;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Number of XSD Files: " + xsds );
            getLog().debug( "Number of WSDL Files: " + wsdls );
        }
        
        return xsds > 0 || wsdls > 0;
    }

    protected abstract void updateProject( MavenProject project, SchemaCompiler.Parameters compilerParams, 
                                           boolean stale )
        throws DependencyResolutionRequiredException, XmlBeansException;

    protected abstract List getXsdJars();

    protected abstract File getGeneratedSchemaDirectory();

    private void touchStaleFile() throws IOException
    {
        File staleFile = getStaleFile();

        if ( !staleFile.exists() )
        {
            staleFile.getParentFile().mkdirs();
            staleFile.createNewFile();
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Stale flag file created." );
            }
        }
        else
        {
            staleFile.setLastModified( System.currentTimeMillis() );
        }
    }

    /**
     * @return True if xsd or wsdl files have been modified since the last build (newer than the 
     *     <code>staleFlag</code> file).
     * @throws XmlBeansException if we cannot locate one of the xsd or wsdl files
     */
    private boolean isOutputStale() throws XmlBeansException
    {
        File staleFile = getStaleFile();
        boolean stale = !staleFile.exists();

        if ( !stale )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Stale flag file exists." );
            }
            long staleMod = staleFile.lastModified();

            // check xsds.
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Comparing to xsd's modification time." );    
            }
            final File[] sourceXsds = getXsdFiles();
            int fileCount = sourceXsds.length;
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( fileCount + " xsd to compare." );
            }
            for ( int i = 0; i < fileCount; i++ )
            {
                if ( sourceXsds[i].lastModified() > staleMod )
                {
                    if ( getLog().isDebugEnabled() )
                    {
                        getLog().debug( sourceXsds[i].getName() + " is newer than the stale flag file." );
                    }
                    stale = true;
                }
            }
            
            // check wsdls
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Comparing to wsdl's modification time." );
            }
            final File[] sourceWsdls = getWsdlFiles();
            fileCount = sourceWsdls.length;
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( fileCount + " wsdl to compare." );
            }
            for ( int i = 0; i < fileCount; i++ )
            {
                if ( sourceWsdls[i].lastModified() > staleMod )
                {
                    if ( getLog().isDebugEnabled() )
                    {
                        getLog().debug( sourceWsdls[i].getName() + " is newer than the stale flag file." );
                    }
                    stale = true;
        }
            }
        }
        return stale;
    }

    public abstract File getBaseDir();

    public abstract File getStaleFile();

    public abstract File getDefaultXmlConfigDir();

    /**
     * Returns the directory where the schemas are located. Note that this is
     * the base directory of the schema compiler, not the maven project.
     *
     * @return The schema directory.
     */
    public abstract File getSchemaDirectory();

    /**
     * Returns a classpath for the compiler made up of artifacts from the
     * project.
     *
     * @return Array of classpath entries.
     * @throws DependencyResolutionRequiredException Plugin wasn't annotated with the right requiresDependencyResolution
     */
    public abstract File[] getClasspath()
        throws DependencyResolutionRequiredException;

    /**
     * Returns null. Currently the compiler preference isn't passwed to the xml
     * beans compiler.
     *
     * @return null.
     */
    public final String getCompiler()
    {
        return compiler;
    }

    /**
     * Returns configuration files identified in the xmlConfigs string passed by
     * the project configuration. If none were identified, a check is made for
     * the default xsd config directory src/xsdconfig.
     *
     * @return An array of configuration files.
     */
    public final File[] getConfigFiles() throws XmlBeansException
    {
        File defaultXmlConfigDir = getDefaultXmlConfigDir();
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Creating a list of config files." );
        }

        try
        {
            if ( xmlConfigs != null )
            {
                return ( File[] ) getFileList( xmlConfigs ).toArray( new File[]{} );
            }
            else if ( defaultXmlConfigDir.exists() )
            {
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Examining " + defaultXmlConfigDir + " for config files." );
                }
                List defaultDir = new ArrayList();
                defaultDir.add( defaultXmlConfigDir );
                return ( File[] ) getFileList( defaultDir ).toArray( EMPTY_FILE_ARRAY );
            }
            else
            {
                return null;
            }
        }
        catch ( XmlBeansException xmlbe )
        {
            throw new XmlBeansException( XmlBeansException.INVALID_CONFIG_FILE, xmlbe );
        }
    }

    /**
     * Recursively travers the file list and it's subdirs and produce a single
     * flat list of the files.
     *
     * @param fileList list of files
     * @return files
     */
    private List getFileList( List fileList ) throws XmlBeansException
    {
        if ( fileList != null )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "A list was given." );
            }
            List files = new ArrayList();

            File nextFile;
            DirectoryScanner scanner = new DirectoryScanner();
//            String[] includes = {"**/*"};
//            scanner.setIncludes(includes);
            scanner.setCaseSensitive( false );
            scanner.addDefaultExcludes();
            for ( Iterator iterator = fileList.iterator(); iterator.hasNext(); )
            {

                nextFile = ( File ) iterator.next();
                if ( nextFile.exists() )
                {
                    if ( nextFile.isDirectory() )
                    {
                        scanner.setBasedir( nextFile );
                        scanner.scan();
                        String[] fileArray = scanner.getIncludedFiles();

                        if ( fileArray != null )
                        {
                            for ( int i = 0; i < fileArray.length; i++ )
                            {
                                if ( getLog().isDebugEnabled() )
                                {
                                    getLog().debug( "Adding " + fileArray[i] );
                                }
                                files.add( new File( nextFile, fileArray[i] ) );
                            }
                        }
                    }
                    else
                    {
                        files.add( nextFile );
                    }
                }
                else
                {
                    throw new XmlBeansException( XmlBeansException.MISSING_FILE, nextFile
                            .getAbsolutePath() );
                }
            }
            return files;
        }
        else
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "No list was given. Returning." );
            }
            return null;
        }

    }


    /**
     * Returns a null entity resolver.
     *
     * @return entityResolver set to null.
     */
    public final EntityResolver getEntityResolver()
    {
        return entityResolver;
    }

    /**
     * Returns an empty collection the compiler will store error message Strings
     * in.
     *
     * @return An empty ArrayList.
     */
    public final Collection getErrorListeners()
    {
        return new ArrayList();
    }

    /**
     * Todo: Not certain of the purpose of this.
     *
     * @return null at this time.
     */
    public final List getExtensions()
    {
        return null;
    }

    /**
     * Used during testing.
     *
     * @param project the maven project we are setting.
     */
    final void setProject( MavenProject project )
    {
        this.project = project;
    }

    /**
     * An array of other source files. Currently an empty array.
     *
     * @return An empty file array.
     */
    public final File[] getJavaFiles()
    {
        return new File[]{};
    }

    /**
     * Returns null at this time. Passed to the schema compiler.
     *
     * @return null.
     */
    public final Set getMdefNamespaces()
    {
        return mdefNamespaces;
    }

    /**
     *
     *
     *
     */
    public final String getJavaSource()
    {
        return javaSource;
    }

    /**
     * Returns the initial size of the memory allocation for the schema compile
     * process.
     *
     * @return The initial memory size value.
     */
    public final String getMemoryInitialSize()
    {
        return memoryInitialSize;
    }

    /**
     * Returns the maximum size of the memory allocation for the schema compile
     * process.
     *
     * @return The max memory size value.
     */
    public final String getMemoryMaximumSize()
    {
        return memoryMaximumSize;
    }

    /**
     * Returns null at this time. This is passed to the schema compiler.
     *
     * @return null.
     */
    public final String getName()
    {
        return null;
    }

    /**
     * Returns the location of the output jar file should one be produced. If
     * it has been set, make sure the directories exist before passing it
     * to the xml beans compiler.
     *
     * @return The jar file location.
     * @number MXMLBEANS-17
     */
    public final File getOutputJar()
    {
        if ( outputJar != null )
        {
            outputJar.getParentFile().mkdirs();
        }
        return outputJar;
    }

    /**
     * Todo: Not certain of the purpose of this.
     *
     * @return null at this time.
     */
    public final String getRepackage()
    {
        return null;
    }

    /**
     * Returns the name of the file used to resolve xml entities.
     *
     * @return The entity resolver catalog file location.
     * @number MXMLBEANS-3
     */
    public final boolean hasCatalogFile()
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "looking for resolver catalog at " + catalogLocation.getAbsolutePath() );
        }
        return catalogLocation.exists();
    }

    /**
     * Returns the name of the file used to resolve xml entities.
     *
     * @return The entity resolver catalog file location.
     * @number MXMLBEANS-3
     */
    public final String getCatalogFile()
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Using resolver catalog." );
        }
        return catalogLocation.getAbsolutePath();
    }

    /**
     * Returns a file array of xsd files to translate to object models.
     *
     * @return An array of schema files to be parsed by the schema compiler.
     * @number MXMLBEANS-21
     */
    public final File[] getXsdFiles() throws XmlBeansException
    {
        xsdFiles = getFiles( xsdFiles, "**/*.xsd" );
        return xsdFiles;
    }

    /**
     * Returns a file array of wsdl files to translate to object models.
     *
     * @return An array of wsdl files to be parsed by the schema compiler.
     */
    public final File[] getWsdlFiles() throws XmlBeansException
        {
        wsdlFiles = getFiles( wsdlFiles, "**/*.wsdl" );
        return wsdlFiles;
    }

    private File[] getFiles( final File[] schemaFiles, final String includeFilter )
        throws XmlBeansException
    {
        // Already got files once before, so no need to do the work again
        if ( schemaFiles != null )
        {
            return schemaFiles;
        }

        final List schemas = new ArrayList();

        File schemaDirectory = getSchemaDirectory();
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "The schema Directory is " + schemaDirectory );
        }

        // if list of schemas to process exists, add schemas from xsdJars and schemaDirectory only
        if ( sourceSchemas != null )
        {
            // collect schemas from artifacts
            Map artifactSchemas = getArtifactSchemas();

            File nextFile;
            for ( Iterator iterator = sourceSchemas.iterator(); iterator.hasNext(); )
            {
                String schemaName = (String) iterator.next();
                String ext = FileUtils.getExtension( schemaName );
                if ( !includeFilter.endsWith( ext ) )
                {
                    continue;
                }

                nextFile = new File( schemaDirectory, schemaName );
                if ( nextFile.exists() )
                {
                    // add schema if it exists in schemaDirectory
                    schemas.add( nextFile );
                }
                else if ( artifactSchemas.containsKey( schemaName ) )
                {
                    // add schema if it's in an artfact
                    schemas.add( artifactSchemas.get( schemaName ) );
                }
                else
                {
                    // throw exception if schema can't be found
                    String[] fields = new String[3];
                    fields[0] = schemaName;
                    fields[1] = schemaDirectory.getAbsolutePath();
                    fields[2] = ( artifactMap.isEmpty() ? "" : " or the schema artifact(s)" );
                    throw new XmlBeansException( XmlBeansException.MISSING_SCHEMA_FILE, fields );
                }
            }
            return (File[]) schemas.toArray( new File[schemas.size()] );
        }

        // if list of schemas to process was not provided

        // add all schemas from xsdJars that match filter
        Map artifactSchemas = getArtifactSchemas();
        if ( !artifactSchemas.isEmpty() )
        {
            File nextFile;
            for ( Iterator fileIterator = artifactSchemas.values().iterator(); fileIterator.hasNext(); )
            {
                nextFile = (File) fileIterator.next();
                String ext = FileUtils.getExtension( nextFile.getName() );
                if ( includeFilter.endsWith( ext ) )
                {
                    schemas.add( nextFile );
                }
            }
        }

        // add all schemas from xsdJars that match filter
        if ( schemaDirectory.exists() )
        {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir( schemaDirectory );
            
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Scanning for " + includeFilter );
            }
            
            String[] includes = {includeFilter};
            scanner.setIncludes( includes );
            scanner.addDefaultExcludes();

            scanner.setCaseSensitive( false );
            scanner.scan();

            String[] files = scanner.getIncludedFiles();
            if ( files != null )
            {
                for ( int i = 0; i < files.length; i++ )
                {
                    if ( getLog().isDebugEnabled() )
                    {
                        getLog().debug( "Adding " + files[i] );
                    }
                    schemas.add( new File( schemaDirectory, files[i] ) );
                }
            }
        }

        return (File[]) schemas.toArray( new File[schemas.size()] );
    }

    /**
     * Sweep through the jar artifacts which contain xsds and produce a list of
     * paths to each xsd within the file. Leave it up to the entity resolver to
     * pass the actual file to the compiler.
     *
     * @return A list of path's to the XSD's in the artifact jars. This doesn't
     *         include the jar paths.
     * @number MXMLBEANS-21
     */
    private Map getArtifactSchemas() throws XmlBeansException
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Artifact count: " + artifactMap.size() );
        }
        SchemaArtifactLookup lookup = new SchemaArtifactLookup( artifactMap, getLog() );
        Map artifactSchemas = new HashMap();
        List xsdJars = getXsdJars();
        File prefix = getGeneratedSchemaDirectory();
        int count = xsdJars.size();

        // Collect the file paths to the actual jars
        Artifact nextArtifact;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "looking for artifact schemas." );
        }

        for ( int i = 0; i < count; i++ )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "resolving " + xsdJars.get( i ) + " into a file path." );
            }
            nextArtifact = lookup.find( ( String ) xsdJars.get( i ) );
            artifactSchemas.putAll( SchemaArtifact.getFilePaths( nextArtifact, getLog(), prefix ) );
        }

        return artifactSchemas;
    }

    /**
     * Returns the state of debuggin.
     *
     * @return true if debug mode.
     */
    public final boolean isDebug()
    {
        return debug;
    }

    /**
     * Returns true if dependencies are to be downloaded by the schema compiler.
     *
     * @return true if resources should be downloaded.
     */
    public final boolean isDownload()
    {
        return download;
    }

    /**
     * Returns true if jaxb is set.
     *
     * @return true if the jaxb flag on the schema compiler should be set.
     */
    public final boolean isJaxb()
    {
        return jaxb;
    }

    public final boolean isNoAnn()
    {
        return noAnn;
    }

    public final boolean isNoVDoc()
    {
        return noVDoc;
    }

    /**
     * Returns True if generated source files are not to be compiled.
     *
     * @return true if no compiling should occur.
     */
    public final boolean isNoJavac()
    {
        return noJavac;
    }

    /**
     * Do not enforce the particle valid (restriction) rule if true.
     *
     * @return true if no enforcement should occur.
     */
    public final boolean isNoPvr()
    {
        return noPvr;
    }

    /**
     * If true, do not enforce the unique particle attribution rule.
     *
     * @return particle attibution enforcement
     */
    public final boolean isNoUpa()
    {
        return noUpa;
    }

    /**
     * Returns true if the schema compiler should reduce verbosity.
     *
     * @return true if message suppression is on.
     */
    public final boolean isQuiet()
    {
        return quiet;
    }

    /**
     * Returns true if the schema compiler should increase verbosity.
     *
     * @return true if verbose mode is on.
     */
    public final boolean isVerbose()
    {
        return verbose;
    }

    /**
     * No validation beyond those done by the maven plugin occur at this time.
     *
     * @throws XmlBeansException Currently not used.
     */
    public final void validate() throws XmlBeansException
    {
    }

}
