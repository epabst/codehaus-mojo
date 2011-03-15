package org.codehaus.mojo.repositorytools.components;

import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;

public interface RepositoryDeployer
{

	String ROLE = RepositoryDeployer.class.getName();

	/**
	 * Checks which local, non-snapshot versions are not available on the remote
	 * repository
	 * 
	 * @param local
	 * @param remote
	 * @return a set of Artifact
	 * @throws RepositoryToolsException
	 */
	public Set getDeployableArtifacts(ArtifactRepository local,
			ArtifactRepository remote, boolean checkExisting) throws RepositoryToolsException;

	public Set deployArtifacts(Set artifacts,
			ArtifactRepository local, ArtifactRepository remote)
			throws RepositoryToolsException;
}
