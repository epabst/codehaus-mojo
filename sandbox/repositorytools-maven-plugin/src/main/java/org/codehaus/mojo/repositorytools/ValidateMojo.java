package org.codehaus.mojo.repositorytools;

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.repositorytools.components.CLITools;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;
import org.codehaus.mojo.repositorytools.util.RepositoryUtils;
import org.codehaus.mojo.repositorytools.validation.ArtifactValidationManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * This goal performs validation on an artifact in the repository. It currently
 * does two types of validation:
 * <ul>
 * <li>Basic validation on required POM elements</li>
 * <li>dependency validation: iterating over all classes and checks if imports
 * are provided by one of the dependencies.</li>
 * </ul>
 * 
 * @goal validate
 * @requiresProject false
 * @author tom
 */
public class ValidateMojo extends AbstractMojo implements Contextualizable
{

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 */
	private List remoteRepositories;

	/**
	 * @parameter expression="${localRepository}"
	 */
	private ArtifactRepository localRepository;

	/**
	 * An artifact in the repository, as groupId:artifactId:version
	 * 
	 * @parameter expression="${artifact}"
	 */
	private String artifact;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactValidationManager validationManager;
	
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
			Artifact a = cliTools.createArtifact(artifact, "jar");
			Map result = validationManager.validateArtifact(a,
					remoteRepositories, localRepository);
			
			RepositoryUtils.printValidation(getLog(), result);
		}
		catch (RepositoryToolsException e)
		{
			throw new MojoExecutionException("Error while validating", e);
		}
	}

	public void contextualize(Context context) throws ContextException
	{
		context.get(PlexusConstants.PLEXUS_KEY);
	}
}
