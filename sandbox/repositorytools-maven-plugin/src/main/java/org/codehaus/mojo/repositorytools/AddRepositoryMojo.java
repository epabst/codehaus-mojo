package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.repositorytools.components.CLITools;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;

/**
 * @goal add-repository
 *
 */
public class AddRepositoryMojo extends AbstractAddMojo {

	/**
	 * @parameter expression="${source}"
	 */
	private String source;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private CLITools cliTools;
	
	/**
	 * I would like to support partial repositories, but this is not supported by the discoverer api
	 * @parameter expression="${path}" default-value="/"
	 * @readonly
	 */
	private String path;

	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			List remoteRepositories = cliTools.createRemoteRepositories(source);
			ArtifactRepository localRepository = cliTools.createLocalRepository(new File(local));
			
			Set artifacts = builder.addRepository(path, localRepository, remoteRepositories, true);
			
			for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
				Artifact a = (Artifact) iterator.next();
				getLog().info("found and resolved" + a);
			}
			
		} catch (RepositoryToolsException e) {
			throw new MojoExecutionException("", e);
		}
		
	}

}
