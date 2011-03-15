package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.repositorytools.components.CLITools;
import org.codehaus.mojo.repositorytools.components.LocalRepositoryBuilder;
import org.codehaus.mojo.repositorytools.components.RepositoryDeployer;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

/**
 * Copies an artifact from one repository to another.
 * 
 * @goal copy-artifact
 * @requiresProject false
 * @author tom
 * 
 */
public class CopyArtifactMojo extends AbstractMojo
{

	/**
	 * The remote repository to deploy to. Format id::layout::url
	 * 
	 * @parameter expression="${target}"
	 * @required
	 */
	protected String target;

	/**
	 * The remote repository to copy from. Format id::layout::url
	 * 
	 * @parameter expression="${source}"
	 * @required
	 */
	protected String source;

	/**
	 * Perform the deploy. If false, the artifacts will be processed but the
	 * actual deploy will not occur.
	 * 
	 * @parameter expression="${deploy}" default-value="false"
	 */
	private boolean deploy;

	/**
	 * If true, artifacts that already exist on the remote repository will not 
	 * be redeployed. 
	 * 
	 * @parameter expression="${checkExisting}" default-value="true"
	 */
	private boolean checkExisting;

	/**
	 * The local repository to create or add to. Defaults to a 'local'
	 * subdirectory of the working directory.
	 * 
	 * @parameter expression="${local}" default-value="local"
	 * 
	 */
	protected String local;

	/**
	 * @component
	 */
	protected LocalRepositoryBuilder localRepositoryBuilder;

	/**
	 * @component
	 */
	protected RepositoryDeployer remoteRepositoryBuilder;

	/**
	 * The artifact that should be resolved, in the form
	 * groupId:artifactId:version. For artifacts (like plugins) that have
	 * release information in their metadata, the version string RELEASE can be
	 * used to retrieve the most recent release version.
	 * 
	 * @parameter expression="${artifact}"
	 * @required
	 */
	private String artifact;

	/**
	 * @component
	 */
	private CLITools cliTools;

	/**
	 * Also copy transitive dependencies.
	 * 
	 * @parameter expression="${transitive}" default-value="false"
	 */
	private boolean transitive;

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			List sourceRepositories = cliTools
					.createRemoteRepositories(source);
			ArtifactRepository targetRepository = cliTools
					.createRemoteRepository(target);
			ArtifactRepository localRepository = cliTools
					.createLocalRepository(new File(local));

			Artifact pomArtifact = cliTools.createArtifact(artifact, "pom");
			Set resolvedArtifacts = localRepositoryBuilder
					.addArtifact(pomArtifact, localRepository,
							sourceRepositories, transitive);

			Set artifacts = new HashSet();
			for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
				Artifact a = (Artifact) iterator.next();
				if (a.getGroupId().equals(pomArtifact.getGroupId())
						&& a.getArtifactId()
								.equals(pomArtifact.getArtifactId())
						&& a.getVersion().equals(pomArtifact.getVersion()))
				{
					artifacts.add(a);
				}
			}

			artifacts = remoteRepositoryBuilder.getDeployableArtifacts(
					localRepository, targetRepository, checkExisting);

			if (deploy)
			{
				remoteRepositoryBuilder.deployArtifacts(artifacts,
						localRepository, targetRepository);
			} else
			{
				getLog().info("These artifacts would be deployed");
				for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
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
	}

}
