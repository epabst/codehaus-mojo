package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.repositorytools.components.CLITools;
import org.codehaus.mojo.repositorytools.components.LocalRepositoryBuilder;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

/**
 * Creates a local repository with all the transitive dependencies of a
 * specified artifact.
 * 
 * @goal add-artifact
 * @requiresProject false
 * @author tom
 */

public class AddArtifactMojo extends AbstractAddMojo
{

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private LocalRepositoryBuilder builder;

	/**
	 * The artifact that should be resolved, in the form
	 * groupId:artifactId:version. For artifacts (like plugins) that have release information
	 * in their metadata, the version string RELEASE can be
	 * used to retrieve the most recent release version.
	 * 
	 * @parameter expression="${artifact}"
	 * @required
	 */
	private String artifact;


	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private CLITools cliTools;

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			Artifact a = cliTools.createArtifact(artifact,
					"pom");
			ArtifactRepository localRepository = cliTools.createLocalRepository(new File(local));
			List remoteRepositories = cliTools.createRemoteRepositories(remote); 
			builder.addArtifact(a, localRepository, remoteRepositories,
					transitive);
		}
		catch (RepositoryToolsException e)
		{
			throw new MojoExecutionException(
					"An error occurred while deploying: " + e.getMessage(), e);
		}

	}

}
