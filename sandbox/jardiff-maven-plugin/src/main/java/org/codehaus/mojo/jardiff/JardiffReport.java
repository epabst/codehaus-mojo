/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.codehaus.mojo.jardiff;

import java.io.File;
import java.util.Locale;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.osjava.jardiff.DiffException;
import org.osjava.jardiff.JarDiff;
import org.osjava.jardiff.SimpleDiffCriteria;

/**
 * This plugin creates a jardiff report.
 * @goal jardiff
 * @description 
 * */
public class JardiffReport extends AbstractMavenReport
{

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;


	/**
	 * @component
	 */
	private Renderer siteRenderer;


	/**
	 * @parameter expression="${project.reporting.outputDirectory}/jardiff"
	 * @required
	 * @readonly
	 */
	private File outputDirectory;

	/**
	 * Artifact resolver, needed to download source jars.
	 * 
	 * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
	 * @required
	 * @readonly
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * Artifact factory, needed to download source jars.
	 * 
	 * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
	 * @required
	 * @readonly
	 */
	private ArtifactFactory artifactFactory;    

	/**
	 * Location of the local repository.
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	private ArtifactRepository artifactRepository;

	/**
	 * Location of the local repository.
	 * @parameter
	 */
	private ArtifactConfiguration[] artifacts;

	/**
	 * Comparison method
	 * @parameter default-value="incremental"
	 */
	private String comparison;
	
	/**
	 * List of Remote Repositories used by the resolver
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	private java.util.List remoteArtifactRepositories;

	public void executeReport( Locale locale ) throws MavenReportException
	{
		if ( !outputDirectory.exists() && !outputDirectory.mkdirs()) {
			throw new MavenReportException( "Folder " + outputDirectory + " could not be created." );
		}

		if (artifacts == null || artifacts.length == 0) {
			// provide default version
			throw new MavenReportException("nothing to do");
		}

		final Artifact[][] comparisons;
		
		if ("incremental".equalsIgnoreCase(comparison)) {
			comparisons = getIncrementalComparisons();			
		} else if ("individual".equalsIgnoreCase(comparison)) {
			comparisons = getIndividualComparisons();						
		} else {
			throw new MavenReportException("unknown comparison method " + comparison);
		}
			

		final Sink sink = getSink();
		
		sink.head();
		sink.title();
		sink.text(getName(locale));
		sink.title_();
		sink.head_();
		
		sink.body();

		for (int i = 0; i < comparisons.length; i++) {
			final Artifact oldArtifact = comparisons[i][0];
			final Artifact newArtifact = comparisons[i][1];

			getLog().info("diff " + oldArtifact + " vs " + newArtifact);

			resolveArtifact(oldArtifact);
			resolveArtifact(newArtifact);

			final JarDiff diff = new JarDiff();

			diff.setOldVersion(oldArtifact.getVersion());
			diff.setNewVersion(newArtifact.getVersion());

			
			try {
				diff.loadOldClasses(oldArtifact.getFile());
				diff.loadNewClasses(newArtifact.getFile());

				diff.diff(new SinkDiffHandler(getSink()), new SimpleDiffCriteria());

			} catch (DiffException e) {
				throw new MavenReportException("" ,e);
			}
			
		}

		sink.body_();
		sink.flush();
		sink.close();
		
	}

	private String getOrDefault( String value, String def ) {
		if (value == null) {
			return def;
		}
		return value;
	}

	
	private Artifact getArtifact( ArtifactConfiguration config )
	{
		final Artifact projectArtifact = project.getArtifact();

		final Artifact artifact = artifactFactory.createArtifact(
				getOrDefault(config.groupId, projectArtifact.getGroupId()),
				getOrDefault(config.artifactId, projectArtifact.getArtifactId()),
				getOrDefault(config.version, projectArtifact.getVersion()),
				null,
				getOrDefault(config.type, projectArtifact.getType())
		);
	
		return artifact;
	}

	private void resolveArtifact( Artifact artifact ) throws MavenReportException
	{
		try
		{
			artifactResolver.resolve( artifact, remoteArtifactRepositories, artifactRepository );
		}
		catch ( ArtifactResolutionException e )
		{
			throw new MavenReportException( "Unable to resolve artifact.", e );
		}
		catch ( ArtifactNotFoundException e )
		{
			throw new MavenReportException( "Unable to find artifact.", e );
		}
		
		if (artifact.getFile() == null || !artifact.getFile().exists()) {
			throw new MavenReportException( "Unable to find artifact file " + artifact);
		}

	}
	
	private Artifact[][] getIncrementalComparisons() 
	{		
		final Artifact[][] comparisons = new Artifact[artifacts.length][2];

		comparisons[0][0] = getArtifact(artifacts[0]);
        comparisons[0][1] = project.getArtifact();

		for (int i = 1; i < artifacts.length; i++) {
			comparisons[i][0] = getArtifact(artifacts[i]);
            comparisons[i][1] = comparisons[i-1][0];
        }

		return comparisons;
	}

	private Artifact[][] getIndividualComparisons() 
	{		
		final Artifact[][] comparisons = new Artifact[artifacts.length][2];

		for (int i = 0; i < artifacts.length; i++) {
			comparisons[i][0] = project.getArtifact(); 
			comparisons[i][1] = getArtifact(artifacts[i]);
		}

		return comparisons;
	}
	
	public String getOutputName()
	{
		return "jardiff";
	}

	protected String getOutputDirectory()
	{
		return this.outputDirectory.getAbsoluteFile().toString();
	}

	protected Renderer getSiteRenderer()
	{
		return this.siteRenderer;
	}

	protected MavenProject getProject()
	{
		return this.project;
	}

	public String getName( Locale locale )
	{
		return "Jardiff Report";
	}

	public String getDescription( Locale locale )
	{
		return "";
	}

	public boolean canGenerateReport()
	{
		final ArtifactHandler artifactHandler = this.project.getArtifact().getArtifactHandler();
		return "java".equals( artifactHandler.getLanguage() );
	}
}
