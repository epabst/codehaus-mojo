package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * Deploy all artifacts in a repository in the local filesystem to a remote repository.
 * 
 * @goal deploy-repository
 * @requiresProject false
 * @author tom
 * 
 */
public class DeployRepositoryMojo extends AbstractDeployMojo
{

	/**
	 * The local repository to deploy. Defaults to a 'local' subdirectory of the
	 * working directory.
	 * 
	 * @parameter expression="${local}"
	 * @required
	 */
	private File local;

	public ArtifactRepository createLocalRepository() throws MalformedURLException
	{
		return cliTools.createLocalRepository(local);
	}
}
