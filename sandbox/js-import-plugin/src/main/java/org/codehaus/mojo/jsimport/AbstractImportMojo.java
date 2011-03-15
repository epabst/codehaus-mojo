package org.codehaus.mojo.jsimport;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Mojo for resolving dependencies either declared using an @import javadoc statement or by declaration of uninitialised
 * variables.
 */
public abstract class AbstractImportMojo
    extends AbstractMojo
{
    /**
     * The current project scope.
     */
    protected enum Scope
    {
        /** */
        COMPILE,
        /** */
        TEST
    };

    /**
     * The project.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The set of dependencies required by the project.
     * 
     * @parameter default-value="${project.dependencies}"
     * @required
     * @readonly
     */
    private List<Dependency> dependencies;

    /**
     * The local repository.
     * 
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * The remote repositories.
     * 
     * @parameter default-value="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List<?> remoteRepositories;

    /**
     * @parameter default-value="**\/*.js"
     * @required
     */
    private String jsFileExtensions;

    /**
     * The project's artifact factory.
     * 
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * The project's meta data source.
     * 
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * The project's artifact resolver.
     * 
     * @component
     */
    private ArtifactResolver resolver;

    /**
     * A map of symbols to the script resource that they are defined in.
     */
    private final Map<String, String> fileAssignedGlobals = new HashMap<String, String>();

    /**
     * A map of symbols to the script resource that they are defined in from a compile scope perspective.
     */
    private final Map<String, String> compileFileAssignedGlobals = new HashMap<String, String>();

    /**
     * A map of symbols to the script resource that they are declared (but not initialised) in. This map is updated once
     * per run for all of the parsed js files that are processed. For example if there are no files to process given
     * that non have been updated since the last run then this map will be empty. If only one file was processed then
     * this map will contain unassigned globals just for that one file. This approach results in
     * processSourceFilesForUnassignedSymbolDeclarations to run efficiently as it is driven by this map.
     */
    private final Map<String, Set<String>> fileUnassignedGlobals = new HashMap<String, Set<String>>();

    /**
     * A graph of filenames and their dependencies.
     */
    private final Map<String, LinkedHashSet<String>> fileDependencies = new HashMap<String, LinkedHashSet<String>>();

    /**
     * The build context so that we can tell Maven certain files have changed if required.
     * 
     * @component
     */
    private BuildContext buildContext;

    /**
     * Build dependency graph from the source files.
     * 
     * @param fileDependencyGraphModificationTime the time the graph read in was updated. Used for comparing file times.
     * @param sourceJsFolder Where the source JS files live.
     * @param processedFiles the files that have been processed as a consequence of this method.
     * @return true if the graph has been updated by this method.
     * @throws MojoExecutionException if something goes wrong.
     */
    private boolean buildDependencyGraphForChangedSourceFiles( long fileDependencyGraphModificationTime,
                                                               File sourceJsFolder, LinkedHashSet<File> processedFiles )
        throws MojoExecutionException
    {
        boolean fileDependencyGraphUpdated = false;
        Scanner scanner = buildContext.newScanner( sourceJsFolder );
        scanner.setIncludes( jsFileExtensions.split( "," ) );
        scanner.scan();
        String[] sources = scanner.getIncludedFiles();
        for ( String source : sources )
        {
            File sourceFile = new File( sourceJsFolder, source );
            if ( processFileForImportsAndSymbols( sourceFile, fileDependencyGraphModificationTime, null ) )
            {
                processedFiles.add( sourceFile );

                getLog().info( "Processed: " + source );

                fileDependencyGraphUpdated = true;
            }
        }

        return fileDependencyGraphUpdated;
    }

    /**
     * Build up the dependency graph and global symbol table by parsing the project's dependencies.
     * 
     * @param scope compile or test.
     * @param fileDependencyGraphModificationTime the time that the dependency graph was updated. Used for file time
     *            comparisons to check the age of them.
     * @param processedFiles an insert-ordered set of files that have been processed.
     * @return true if the dependency graph has been updated.
     * @throws MojoExecutionException if something bad happens.
     */
    private boolean buildDependencyGraphForDependencies( Scope scope, long fileDependencyGraphModificationTime,
                                                         LinkedHashSet<File> processedFiles )
        throws MojoExecutionException
    {
        boolean fileDependencyGraphUpdated = false;

        String scopeStr = ( scope == Scope.COMPILE ? "compile" : "test" );

        for ( Dependency dependency : dependencies )
        {
            // Only process dependencies within the scope we're interested in.
            if ( !dependency.getScope().equals( scopeStr ) )
            {
                continue;
            }

            // Process imports and symbols of this dependencies' transitives
            // first.
            ArtifactResolutionResult result;
            Artifact artifactToResolve =
                artifactFactory.createArtifactWithClassifier( dependency.getGroupId(), dependency.getArtifactId(),
                                                              dependency.getVersion(), dependency.getType(),
                                                              dependency.getClassifier() );
            Set<Artifact> artifactsToResolve = new HashSet<Artifact>();
            artifactsToResolve.add( artifactToResolve );
            try
            {
                result =
                    resolver.resolveTransitively( artifactsToResolve, project.getArtifact(), remoteRepositories,
                                                  localRepository, artifactMetadataSource );
            }
            catch ( ArtifactResolutionException e )
            {
                throw new MojoExecutionException( "Problem resolving dependencies", e );
            }
            catch ( ArtifactNotFoundException e )
            {
                throw new MojoExecutionException( "Problem resolving dependencies", e );
            }
            // Resolve just this dependencies' transitive dependencies first and
            // discount any that have been resolved previously.
            Set<?> transitiveArtifacts = result.getArtifacts();
            transitiveArtifacts.removeAll( processedFiles );
            transitiveArtifacts.remove( artifactToResolve );

            LinkedHashSet<String> transitivesAsImports = new LinkedHashSet<String>( transitiveArtifacts.size() );

            for ( Object transitiveArtifactObject : transitiveArtifacts )
            {
                Artifact transitiveArtifact = (Artifact) transitiveArtifactObject;
                final File transtitiveArtifactFile = transitiveArtifact.getFile();

                // Only process this dependency if we've not done so
                // already.
                if ( !processedFiles.contains( transtitiveArtifactFile ) )
                {
                    if ( processFileForImportsAndSymbols( transtitiveArtifactFile, fileDependencyGraphModificationTime,
                                                          transitiveArtifacts ) )
                    {

                        processedFiles.add( transtitiveArtifactFile );

                        fileDependencyGraphUpdated = true;
                    }
                }

                // Add transitives to the artifacts set of dependencies -
                // as if they were @import statements themselves.
                transitivesAsImports.add( transtitiveArtifactFile.getPath() );
            }

            // Now deal with the pom specified dependency.
            File artifactFile = artifactToResolve.getFile();
            String artifactPath = artifactFile.getAbsolutePath();

            // Process imports and symbols of this dependency if we've not
            // already done so.
            if ( !processedFiles.contains( artifactFile ) )
            {
                if ( processFileForImportsAndSymbols( artifactFile, fileDependencyGraphModificationTime, null ) )
                {
                    processedFiles.add( artifactFile );

                    fileDependencyGraphUpdated = true;
                }
            }

            // Add in our transitives to the dependency graph if they're not
            // already there.
            LinkedHashSet<String> existingImports = fileDependencies.get( artifactPath );
            if ( existingImports.addAll( transitivesAsImports ) )
            {
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Using transitives as import: " + transitivesAsImports + " for file: "
                                        + artifactPath );
                }
                fileDependencyGraphUpdated = true;
            }

        }

        return fileDependencyGraphUpdated;
    }

    /**
     * Perform the goal of this mojo.
     * 
     * @param sourceJsFolder the folder where the source js files reside.
     * @param workFolder the folder where our work files can be found.
     * @param scope scope the scope of the dependencies we are to search for.
     * @throws MojoExecutionException if there is an execution failure.
     */
    public void doExecute( File sourceJsFolder, File workFolder, Scope scope )
        throws MojoExecutionException
    {

        // Load in any existing dependency graph - we only build what we need
        // to.
        long fileDependencyGraphModificationTime =
            FileDependencyPersistanceUtil.readFileDependencyGraph( workFolder, fileDependencies, fileAssignedGlobals );
        int fileDependencyGraphHashCode = fileDependencies.hashCode();

        // If we are in test scope then also load in the compile scoped dependency information as we need to resolve
        // against this also.
        if ( scope == Scope.TEST )
        {
            Map<String, LinkedHashSet<String>> compileFileDependencies = new HashMap<String, LinkedHashSet<String>>();
            FileDependencyPersistanceUtil.readFileDependencyGraph( new File( workFolder.getParentFile(), "main" ),
                                                                   compileFileDependencies, //
                                                                   compileFileAssignedGlobals );
        }

        // Build dependency graph and symbol table against each js file declared
        // as a dependency.

        LinkedHashSet<File> processedFiles = new LinkedHashSet<File>();
        boolean fileDependencyGraphUpdated =
            buildDependencyGraphForDependencies( scope, fileDependencyGraphModificationTime, processedFiles );

        // Process all of our JS files and build their dependency
        // graphs and symbol tables.
        if ( buildDependencyGraphForChangedSourceFiles( fileDependencyGraphModificationTime, sourceJsFolder,
                                                        processedFiles ) )
        {
            fileDependencyGraphUpdated = true;
        }

        // Given that we now have all of the symbols mapped by file we
        // now need to go through our artifacts (dependencies and source files)
        // again looking for those that reference them. We add to the file
        // dependencies as a result.
        processSourceFilesForUnassignedSymbolDeclarations();

        // We have have a complete dependency graph. We will now persist the
        // graph so that other phases can utilise it (if things have truly changed).
        if ( fileDependencyGraphUpdated && fileDependencies.hashCode() != fileDependencyGraphHashCode )
        {
            FileDependencyPersistanceUtil.writeFileDependencyGraph( workFolder, fileDependencies, fileAssignedGlobals );
        }
    }

    /**
     * @return property.
     */
    public org.apache.maven.artifact.factory.ArtifactFactory getArtifactFactory()
    {
        return artifactFactory;
    }

    /**
     * @return property.
     */
    public ArtifactMetadataSource getArtifactMetadataSource()
    {
        return artifactMetadataSource;
    }

    /**
     * @return property.
     */
    public List<Dependency> getDependencies()
    {
        return dependencies;
    }

    /**
     * @return property.
     */
    public Map<String, String> getFileAssignedGlobals()
    {
        return fileAssignedGlobals;
    }

    /**
     * @return property.
     */
    public Map<String, LinkedHashSet<String>> getFileDependencies()
    {
        return fileDependencies;
    }

    /**
     * @return property.
     */
    public Map<String, Set<String>> getFileUnassignedGlobals()
    {
        return fileUnassignedGlobals;
    }

    /**
     * @return property.
     */
    public String getJsFileExtensions()
    {
        return jsFileExtensions;
    }

    /**
     * @return property.
     */
    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    /**
     * @return property.
     */
    public MavenProject getProject()
    {
        return project;
    }

    /**
     * @return property.
     */
    public List<?> getRemoteRepositories()
    {
        return remoteRepositories;
    }

    /**
     * @return property.
     */
    public ArtifactResolver getResolver()
    {
        return resolver;
    }

    /**
     * Match a group and artifact against our list of dependencies.
     * 
     * @param groupId the group id.
     * @param artifactId the artifact id.
     * @return the dependency or null if one cannot be found.
     */
    private Dependency matchDirectDependency( String groupId, String artifactId )
    {
        Dependency dependencyFound = null;
        for ( Dependency dependency : dependencies )
        {
            if ( dependency.getGroupId().equalsIgnoreCase( groupId )
                && dependency.getArtifactId().equalsIgnoreCase( artifactId )
                && dependency.getType().equalsIgnoreCase( "js" ) )
            {
                dependencyFound = dependency;
                break;
            }
        }

        return dependencyFound;
    }

    /**
     * Find a dependency in our set of transitive dependencies.
     * 
     * @param groupId the group to match.
     * @param artifactId the artifact to match.
     * @param transitiveArtifacts artifacts to match against.
     * @return an artifact that has been matched or null if none can be found.
     */
    private Artifact matchTransitiveDependency( String groupId, String artifactId, Set<?> transitiveArtifacts )
    {
        Artifact artifactFound = null;
        for ( Object transitiveArtifactObject : transitiveArtifacts )
        {
            Artifact transitiveArtifact = (Artifact) transitiveArtifactObject;
            if ( transitiveArtifact.getGroupId().equalsIgnoreCase( groupId )
                && transitiveArtifact.getArtifactId().equalsIgnoreCase( artifactId )
                && transitiveArtifact.getType().equalsIgnoreCase( "js" ) )
            {
                artifactFound = transitiveArtifact;
                break;
            }
        }

        return artifactFound;
    }

    /**
     * Process a file for import declarations and for the symbols used.
     * 
     * @param artifactFile the file to process.
     * @param fileDependencyGraphModificationTime the last time the dependency graph was updated or 0 if we do not have
     *            one.
     * @param transitiveArtifacts any transititive artifacts to match imports against, or null if no matching is to be
     *            done.
     * @return true if processing occurred.
     * @throws MojoExecutionException if something goes wrong.
     */
    protected boolean processFileForImportsAndSymbols( File artifactFile, long fileDependencyGraphModificationTime,
                                                       Set<?> transitiveArtifacts )
        throws MojoExecutionException
    {

        String artifactPath = artifactFile.getPath();

        // Quickly jump out if this particular artifact has not been updated
        // recently.
        if ( artifactFile.lastModified() <= fileDependencyGraphModificationTime )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().info( "Skipping unchanged JS file: " + artifactPath );
            }
            return false;
        }

        if ( getLog().isDebugEnabled() )
        {
            getLog().info( "Parsing JS file: " + artifactPath );
        }

        try
        {
            // Tokenise the JS file resulting in collections of assigned and
            // unassigned globals, and import statements.
            CharStream cs = new ANTLRFileStream( artifactFile.getPath() );

            ECMAScriptLexer lexer = new ECMAScriptLexer( cs );

            CommonTokenStream tokenStream = new CommonTokenStream();
            tokenStream.setTokenSource( lexer );
            tokenStream.getTokens();

            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Assigned globals: " + lexer.getAssignedGlobalVars().toString() );
                getLog().debug( "Unassigned globals: " + lexer.getUnassignedGlobalVars().toString() );
                getLog().debug( "Imports: " + lexer.getImportGavs().toString() );
            }

            // For each assigned variable map it against the file we're dealing
            // with for later reference. An assigned variable indicates that
            // unassigned declarations of the same variable want this file
            // imported.
            for ( String assignedGlobalVar : lexer.getAssignedGlobalVars() )
            {
                fileAssignedGlobals.put( assignedGlobalVar, artifactPath );
            }

            // For each unassigned variable map it against the file we're
            // dealing with for later reference. An unassigned variable
            // indicates that we want to import a file where the variable is
            // assigned.
            Set<String> vars = new HashSet<String>( lexer.getUnassignedGlobalVars() );
            fileUnassignedGlobals.put( artifactPath, vars );

            // For each import found resolve its file name and then note it as a
            // dependency of this particular js file. We update any existing
            // dependency edges if they exist given that we've determined this
            // part of the graph needs re-construction.
            LinkedHashSet<String> importedDependencies = new LinkedHashSet<String>( lexer.getImportGavs().size() );
            fileDependencies.put( artifactPath, importedDependencies );

            for ( ECMAScriptLexer.GAV importGav : lexer.getImportGavs() )
            {
                Artifact artifactFound;
                Dependency dependencyFound = matchDirectDependency( importGav.groupId, importGav.artifactId );
                if ( dependencyFound == null )
                {
                    if ( transitiveArtifacts != null )
                    {
                        artifactFound =
                            matchTransitiveDependency( importGav.groupId, importGav.artifactId, transitiveArtifacts );
                    }
                    else
                    {
                        artifactFound = null;
                    }
                    if ( artifactFound == null )
                    {
                        getLog().error( "Dependency not found: " + importGav.groupId + ":" + importGav.artifactId );
                        throw new MojoExecutionException( "Build stopping given dependency issue." );
                    }
                }
                else
                {
                    artifactFound = resolveArtifact( dependencyFound );
                }

                /**
                 * Store the dependency as an edge against our dependency graph.
                 */
                importedDependencies.add( artifactFound.getFile().getPath() );

                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Found import: " + importGav.groupId + ":" + importGav.artifactId + " ("
                                        + artifactFound.getFile().getName() + ") for file: " + artifactPath );
                }
            }

        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Problem opening file: " + artifactFile.getName(), e );
        }

        return true;
    }

    /**
     * Go through all of unassigned globals and enhance the file dependencies collection given the file that they are
     * declared in.
     * 
     * @throws MojoExecutionException if something goes wrong.
     */
    protected void processSourceFilesForUnassignedSymbolDeclarations()
        throws MojoExecutionException
    {

        // For all of the js files containing unassigned vars...
        Set<Entry<String, Set<String>>> entrySet = fileUnassignedGlobals.entrySet();
        for ( Entry<String, Set<String>> entry : entrySet )
        {

            // For each of the unassigned vars...
            String variableDeclFile = entry.getKey();
            for ( String variableName : entry.getValue() )
            {

                // Resolve the file that contains the var's assignment and throw
                // an exception if it cannot be found.
                String variableAssignedFile = fileAssignedGlobals.get( variableName );
                if ( variableAssignedFile == null && compileFileAssignedGlobals != null )
                {
                    variableAssignedFile = compileFileAssignedGlobals.get( variableName );

                }
                if ( variableAssignedFile == null )
                {
                    getLog().error( "Dependency not found: " + variableName + " in file: " + variableDeclFile );
                    throw new MojoExecutionException( "Build stopping given dependency issue." );

                }

                // Enhance the declaring file's graph of dependencies.
                LinkedHashSet<String> variableDeclFileImports = fileDependencies.get( variableDeclFile );
                if ( variableDeclFileImports == null )
                {
                    variableDeclFileImports = new LinkedHashSet<String>();
                    fileDependencies.put( variableDeclFile, variableDeclFileImports );
                }

                variableDeclFileImports.add( variableAssignedFile );
            }
        }
    }

    /**
     * Resolve an artifact given a dependency.
     * 
     * @param dependency the dependency to resolve.
     * @return the artifact.
     * @throws MojoExecutionException if the dependency cannot be resolved.
     */
    private Artifact resolveArtifact( Dependency dependency )
        throws MojoExecutionException
    {
        Artifact artifact =
            artifactFactory.createArtifactWithClassifier( dependency.getGroupId(), dependency.getArtifactId(),
                                                          dependency.getVersion(), dependency.getType(),
                                                          dependency.getClassifier() );
        try
        {
            resolver.resolve( artifact, remoteRepositories, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( dependency.toString(), e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( dependency.toString(), e );
        }
        return artifact;
    }

    /**
     * @param artifactFactory set property.
     */
    public void setArtifactFactory( ArtifactFactory artifactFactory )
    {
        this.artifactFactory = artifactFactory;
    }

    /**
     * @param artifactMetadataSource set property.
     */
    public void setArtifactMetadataSource( ArtifactMetadataSource artifactMetadataSource )
    {
        this.artifactMetadataSource = artifactMetadataSource;
    }

    /**
     * @param dependencies set property.
     */
    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }

    /**
     * @param jsFileExtensions set property.
     */
    public void setJsFileExtensions( String jsFileExtensions )
    {
        this.jsFileExtensions = jsFileExtensions;
    }

    /**
     * @param localRepository set property.
     */
    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    /**
     * @param project set property.
     */
    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    /**
     * @param remoteRepositories set property.
     */
    public void setRemoteRepositories( List<?> remoteRepositories )
    {
        this.remoteRepositories = remoteRepositories;
    }

    /**
     * @param resolver set property.
     */
    public void setResolver( ArtifactResolver resolver )
    {
        this.resolver = resolver;
    }

}
