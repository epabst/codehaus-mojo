package org.codehaus.mojo.repositorytools.validation;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

public interface ArtifactValidator
{
	String ROLE = ArtifactValidator.class.getName();

	/**
	 * Performs some kind of validation and returns a list of error messages
	 * 
	 * @param artifact
	 * @param remoteRepositories
	 * @param localRepository
	 * @return a list of ValidationMessage
	 * @throws RepositoryToolsException
	 *             if the validation could not be performed
	 */
	public List validateArtifact(Artifact artifact,
			List remoteRepositories,
			ArtifactRepository localRepository) throws RepositoryToolsException;

	/**
	 * Describes the validation that is performed
	 */
	String getDescription();
	
	/**
	 * Determines if the validator can validate this artifact
	 */
	boolean canValidate(Artifact artifact);
}
