package org.codehaus.mojo.emma;

/*
 * The MIT License
 *
 * Copyright (c) 2007-8, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.emma.task.InstrumentTask;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Offline class instrumentor.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 * @goal instrument
 * @requiresDependencyResolution test
 */
public class EmmaInstrumentMojo
    extends AbstractEmmaMojo
{
    /**
     * Plugin classpath.
     * 
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    protected List pluginClasspath;

    /**
     * Indicates whether the metadata should be merged into the destination <code>metadataFile</code>, if any.
     * 
     * @parameter expression="${emma.merge}" default-value="true"
     */
    protected boolean merge;

    /**
     * Instrumentation filters.
     * 
     * @parameter
     */
    protected String[] filters;

    /**
     * Specifies the instrumentation paths to use.
     * 
     * @parameter
     */
    protected File[] instrumentationPaths;

    /**
     * Location to store class coverage metadata.
     * 
     * @parameter expression="${emma.metadataFile}" default-value="${project.build.directory}/coverage.em"
     */
    protected File metadataFile;

    /**
     * Artifact factory.
     * 
     * @component
     */
    private ArtifactFactory factory;

    /**
     * Checks the parameters before doing the work.
     * 
     * @throws MojoExecutionException if things go wrong.
     * @throws MojoFailureException if things go wrong.
     */
    protected void checkParameters()
        throws MojoExecutionException, MojoFailureException
    {
        super.checkParameters();

        if ( filters == null )
        {
            filters = new String[0];
        }

        if ( instrumentationPaths == null )
        {
            instrumentationPaths = new File[] { new File( project.getBuild().getOutputDirectory() ) };
        }
    }

    /**
     * Does the work.
     * 
     * @throws MojoExecutionException if things go wrong.
     * @throws MojoFailureException if things go wrong.
     */
    protected void doExecute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( getLog().isDebugEnabled() )
        {
            if ( instrumentationPaths != null )
            {
                getLog().debug( "Instrumentation path:" );
                for ( int i = 0; i < instrumentationPaths.length; ++i )
                {
                    getLog().debug( " o " + instrumentationPaths[i].getAbsolutePath() );
                }
            }

            if ( filters != null && filters.length > 0 )
            {
                getLog().debug( "Filters:" );
                for ( int i = 0; i < filters.length; ++i )
                {
                    getLog().debug( " o " + filters[i] );
                }
            }
        }

        final InstrumentTask task = new InstrumentTask();
        task.setOutputDirectory( outputDirectory );
        task.setFilters( filters );
        task.setVerbose( verbose );
        task.setInstrumentationPaths( instrumentationPaths );
        task.setMerge( merge );
        task.setMetadataFile( metadataFile );

        getLog().info( "Instrumenting classes with EMMA" );
        try
        {
            task.execute();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        // prepare test execution by adding EMMA dependencies
        addEmmaDependenciesToTestClasspath();

        final File classesDir = new File( outputDirectory, "classes" );
        project.getBuild().setOutputDirectory( classesDir.getPath() );
    }

    /**
     * Add EMMA dependency to project test classpath. When tests are executed, EMMA runtime dependency is required.
     * 
     * @throws MojoExecutionException if EMMA dependency could not be added
     */
    private void addEmmaDependenciesToTestClasspath()
        throws MojoExecutionException
    {
        // look for EMMA dependency in this plugin classpath
        final Map pluginArtifactMap = ArtifactUtils.artifactMapByVersionlessId( pluginClasspath );
        Artifact emmaArtifact = (Artifact) pluginArtifactMap.get( "emma:emma" );

        if ( emmaArtifact == null )
        {
            // this should not happen
            throw new MojoExecutionException( "Failed to find 'emma' artifact in plugin dependencies" );
        }

        // set EMMA dependency scope to test
        emmaArtifact = artifactScopeToTest( emmaArtifact );

        // add EMMA to project dependencies
        final Set deps = new HashSet();
        if ( project.getDependencyArtifacts() != null )
        {
            deps.addAll( project.getDependencyArtifacts() );
        }
        deps.add( emmaArtifact );
        project.setDependencyArtifacts( deps );
    }

    /**
     * Convert an artifact to a test artifact.
     * 
     * @param artifact to convert
     * @return an artifact with a test scope
     */
    private Artifact artifactScopeToTest( Artifact artifact )
    {
        return factory.createArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                                       Artifact.SCOPE_TEST, artifact.getType() );
    }
}
