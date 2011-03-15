package org.codehaus.mojo.repositorytools.validation;

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

/**
 * 
 * @author tom
 */
public interface ArtifactValidationManager
{
	String ROLE = ArtifactValidationManager.class.getName();

	/**
	 * Returns all validators.
	 * 
	 * @return a map, key -> validator
	 * @throws RepositoryToolsException 
	 */
	Map getValidators() throws RepositoryToolsException;
	
	/**
	 * Validates the artifact with all available validators
	 * 
	 * @param artifact
	 * @param remoteRepositories
	 * @param localRepository
	 * @return map of ArtifactValidator -> List<ValidationMessage>
	 * @throws RepositoryToolsException
	 */
	public Map validateArtifact(Artifact artifact,
			List remoteRepositories,
			ArtifactRepository localRepository) throws RepositoryToolsException;
	
}
