package org.codehaus.mojo.repositorytools.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @plexus.component role="org.codehaus.mojo.repositorytools.validation.ArtifactValidationManager"
 *                   role-hint="default"
 */
public class DefaultArtifactValidationManager extends AbstractLogEnabled
		implements ArtifactValidationManager, Contextualizable
{

	private PlexusContainer container;

	public Map validateArtifact(
			Artifact artifact, List remoteRepositories,
			ArtifactRepository localRepository) throws RepositoryToolsException
	{
		Map result = new HashMap();
		Map validators = getValidators();
		for (Iterator iterator = validators.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			ArtifactValidator validator = (ArtifactValidator) entry.getValue();
			if (validator.canValidate(artifact))
			{
				List messages = validator.validateArtifact(
						artifact, remoteRepositories, localRepository);
				result.put(validator, messages);
			}
		}
		return result;
	}

	public void contextualize(Context context) throws ContextException
	{
		container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
	}

	public String getDescription()
	{
		return "Aggegate validator";
	}

	public Map getValidators()
			throws RepositoryToolsException
	{
		try
		{
			return Collections
					.unmodifiableMap((Map) container
							.lookupMap(ArtifactValidator.ROLE));
		}
		catch (ComponentLookupException e)
		{
			throw new RepositoryToolsException("Could not find any validators",
					e);
		}
	}

}
