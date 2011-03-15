package org.codehaus.mojo.repositorytools.components;

import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

public interface LocalRepositoryBuilder
{

	String ROLE = LocalRepositoryBuilder.class.getName();

	/**
	 * Adds a given artifact and (optionally) all its transitive dependencies to
	 * a local repository
	 * 
	 * @param artifact
	 * @param remote
	 * @param transitive
	 * @throws RepositoryToolsException
	 * @return the artifacts that were added (or already existed)
	 */
	Set addArtifact(Artifact artifact, ArtifactRepository localRepository,
			List remoteRepositories, boolean transitive)
			throws RepositoryToolsException;

	/**
	 * Add a maven plugin to the local repository. 
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param local
	 * @param remote
	 * @param allRemote
	 * @param releaseOnly true only downloads the release version, false downloads all versions
	 * @param transitive true also downloads all dependencies of these plugins
	 */
	public void addPlugin(String groupId, String artifactId,
			ArtifactRepository local, ArtifactRepository remote,
			List allRemote, boolean releaseOnly, boolean transitive)
			throws RepositoryToolsException;

	public void addPluginGroup(String groupId,
			ArtifactRepository localRepository,
			List remoteRepositories, boolean releaseOnly,
			boolean transitive) throws RepositoryToolsException;

	public Set addRepository(String path,
			ArtifactRepository localRepository,
			List remoteRepositories, boolean releaseOnly) 
			throws RepositoryToolsException;
}
