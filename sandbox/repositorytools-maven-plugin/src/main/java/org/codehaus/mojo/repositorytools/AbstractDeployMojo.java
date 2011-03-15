package org.codehaus.mojo.repositorytools;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.repositorytools.components.CLITools;
import org.codehaus.mojo.repositorytools.components.RepositoryDeployer;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

/**
 * An abstract mojo for deploying local artifacts to a remote repository.
 * 
 * @author tom
 * 
 */
public abstract class AbstractDeployMojo extends AbstractMojo
{
	/**
	 * Perform the deploy. If false, the artifacts will be processed but the
	 * actual deploy will not occur.
	 * 
	 * @parameter expression="${deploy}" default-value="false"
	 */
	private boolean deploy;

	/**
	 * Check if the artifacts already exist on the remote repository.
	 * 
	 * @parameter expression="${checkExisting}" default-value="true"
	 */
	private boolean checkExisting;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected ArtifactRepositoryLayout defaultLayout;

	/**
	 * @component
	 */
	protected RepositoryDeployer repositoryDeployer;
	
	/**
	 * @parameter expression="${target}"
	 * @required
	 */
	protected String target;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	protected CLITools cliTools;
	
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			ArtifactRepository remoteRepository = cliTools.createRemoteRepository(target);

			ArtifactRepository localRepository = createLocalRepository();

			Set artifacts = repositoryDeployer
			.getDeployableArtifacts(localRepository,
					remoteRepository, checkExisting);
			
			if (deploy)
			{
				repositoryDeployer.deployArtifacts(artifacts, localRepository, 
						remoteRepository);
			} else
			{

				getLog().info("These artifacts would be deployed");
				for (Iterator iterator = artifacts.iterator(); iterator
						.hasNext();) {
					Artifact a = (Artifact) iterator.next();
					getLog().info(a.toString());
				}
			}
		}
		catch (RepositoryToolsException e)
		{
			throw new MojoExecutionException(
					"Not all artifacts could be deployed", e);
		}
		catch (MalformedURLException e)
		{
			throw new MojoExecutionException("Error parsing url: "
					+ e.getMessage(), e);
		}
	}

	public abstract ArtifactRepository createLocalRepository()
			throws MalformedURLException, RepositoryToolsException;
	
}
