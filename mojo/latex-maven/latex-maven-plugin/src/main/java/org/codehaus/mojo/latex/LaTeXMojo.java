package org.codehaus.mojo.latex;

/*
 * Copyright 2010 INRIA / CITI Laboratory / Amazones Research Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static org.apache.commons.exec.CommandLine.parse;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.iterateFiles;

/**
 * LaTeX documents building goal.
 *
 * @author Julien Ponge
 * @goal latex
 * @phase compile
 */
public class LaTeXMojo
    extends AbstractMojo
{

    /**
     * The documents root.
     *
     * @parameter expression="${latex.docsRoot}" default-value="src/main/latex"
     * @required
     */
    private File docsRoot;

    /**
     * Common files directory inside the documents root (the only directory to be skipped).
     *
     * @parameter expression="${latex.commonsDirName}" default-value="common"
     * @required
     */
    private String commonsDirName;

    /**
     * The Maven build directory.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File buildDir;

    /**
     * The LaTeX builds directory.
     *
     * @parameter expression="${project.latex.build.directory}" default-value="${project.build.directory}/latex"
     * @required
     */
    private File latexBuildDir;

    /**
     * Path to the LaTeX binaries installation.
     *
     * @parameter expression="${latex.binariesPath}" default-value=""
     */
    private String binariesPath;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            final File[] docDirs = getDocDirs();
            final File[] buildDirs = prepareLaTeXBuildDirectories( docDirs );
            buildDocuments( buildDirs );
        }
        catch ( IOException e )
        {
            getLog().error( e );
            throw new MojoFailureException( e.getMessage() );
        }
    }

    private void buildDocuments( File[] buildDirs )
        throws IOException, MojoFailureException
    {
        for ( File dir : buildDirs )
        {
            final File texFile = new File( dir, dir.getName() + ".tex" );
            final File pdfFile = new File( dir, dir.getName() + ".pdf" );
            final File bibFile = new File( dir, dir.getName() + ".bib" );

            if ( requiresBuilding(dir, pdfFile) )
            {

                final CommandLine pdfLaTeX =
                    parse( executablePath( "pdflatex" ) )
                        .addArgument( "-shell-escape" )
                        .addArgument( "--halt-on-error" )
                        .addArgument( texFile.getAbsolutePath() );
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "pdflatex: " + pdfLaTeX );
                }

                final CommandLine bibTeX = parse( executablePath( "bibtex" ) ).addArgument( dir.getName() );
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "bibtex: " + bibTeX );
                }

                execute( pdfLaTeX, dir );
                if ( bibFile.exists() )
                {
                    execute( bibTeX, dir );
                    execute( pdfLaTeX, dir );
                }
                execute( pdfLaTeX, dir );

                copyFile( pdfFile, new File( buildDir, pdfFile.getName() ) );
            }
        }
    }

    private boolean requiresBuilding( File dir, File pdfFile )
        throws IOException
    {
        Collection texFiles = FileUtils.listFiles( dir, new String[]{ "tex", "bib" }, true );
        getLog().info(texFiles.toString());
        if ( pdfFile.exists() )
        {
            boolean upToDate = true;
            Iterator it = texFiles.iterator();
            while( it.hasNext() && upToDate )
            {
                File file = (File) it.next();
                if ( FileUtils.isFileNewer(file, pdfFile ) )
                {
                    if ( getLog().isInfoEnabled() )
                    {
                        getLog().info( "Changes detected on " + file.getAbsolutePath() );
                    }
                    return true;
                }
                if ( getLog().isInfoEnabled() )
                {
                    getLog().info( "No change detected on " + file.getAbsolutePath() );
                }
            }
             if ( getLog().isInfoEnabled() )
             {
                getLog().info( "Skipping: no LaTeX changes detected in " + dir.getCanonicalPath() );
             }
            return false;
        }
        else
        {
            return true;
        }
    }

    private String executablePath( String executable )
    {
        if ( binariesPath == null )
        {
            return executable;
        }
        else
        {
            return new StringBuilder().append( binariesPath ).append( File.separator ).append( executable ).toString();
        }
    }

    private void execute( CommandLine commandLine, File dir )
        throws IOException, MojoFailureException
    {
        final DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory( dir );
        if ( executor.execute( commandLine ) != 0 )
        {
            throw new MojoFailureException( "Error code returned for: " + commandLine.toString() );
        }
    }

    private File[] prepareLaTeXBuildDirectories( File[] docDirs )
        throws IOException
    {
        final File[] buildDirs = new File[docDirs.length];
        final File commonsDir = new File( docsRoot, commonsDirName );

        for ( int i = 0; i < docDirs.length; i++ )
        {
            final File dir = docDirs[i];
            final File target = new File( latexBuildDir, docDirs[i].getName() );
            buildDirs[i] = target;

            copyDirectory( dir, target );
            if ( commonsDir.exists() )
            {
                copyDirectory( commonsDir, target );
            }

            final Iterator iterator = iterateFiles(target, new String[]{ ".svn" }, true);
            while ( iterator.hasNext() )
            {
                FileUtils.deleteDirectory( (File) iterator.next());
            }

        }

        return buildDirs;
    }

    private File[] getDocDirs()
    {
        return docsRoot.listFiles( new FileFilter()
        {
            @Override
            public boolean accept( File pathname )
            {
                return pathname.isDirectory() && !( pathname.getName().equals( commonsDirName ) ) &&
                    !( pathname.isHidden() );
            }
        } );
    }

}
