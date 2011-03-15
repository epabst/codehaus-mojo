package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.repositorytools.components.CLITools;
import org.codehaus.mojo.repositorytools.components.LocalRepositoryBuilder;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

/**
 * <p>
 * Copies all plugins of a given group in a remote repository to a local
 * repository.
 * </p>
 * 
 * @goal add-plugin-group
 * @author tom
 */
public class AddPluginGroupMojo extends AbstractAddMojo
{

	/**
	 * The group that should be added
	 * 
	 * @parameter expression="${group}"
	 */
	private String group;

	/**
	 * Only download release versions. If transitive is true, older versions may
	 * be implicitly downloaded.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean releaseOnly;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private LocalRepositoryBuilder builder;

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
			ArtifactRepository localRepository = cliTools.createLocalRepository(new File(local));
			List remoteRepositories = cliTools.createRemoteRepositories(remote);
			builder.addPluginGroup(group, localRepository, remoteRepositories, releaseOnly, transitive);
		}
		catch (RepositoryToolsException e)
		{
			throw new MojoExecutionException("Error adding plugin group", e);
		}
	}

}
