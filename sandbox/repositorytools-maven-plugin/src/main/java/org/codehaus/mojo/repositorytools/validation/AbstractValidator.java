package org.codehaus.mojo.repositorytools.validation;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public abstract class AbstractValidator extends AbstractLogEnabled implements ArtifactValidator
{

	/**
	 * @plexus.requirement
	 */
	protected MavenProjectBuilder projectBuilder;

	/**
	 * @plexus.requirement
	 */
	protected ArtifactFactory artifactFactory;

	protected MavenProject createProject(Artifact artifact, List remoteRepositories, ArtifactRepository localRepository) throws ProjectBuildingException
	{
		Artifact pom = artifactFactory.createArtifactWithClassifier(artifact
				.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
				"pom", null);
		MavenProject pomProject = projectBuilder.buildFromRepository(pom,
				remoteRepositories, localRepository);
		return pomProject;
	}
	

}
