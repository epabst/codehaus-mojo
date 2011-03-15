package org.codehaus.mojo.repositorytools.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @plexus.component role="org.codehaus.mojo.repositorytools.validation.ArtifactValidator"
 *                   role-hint="contents"
 * @author tom
 * 
 */
public class PomContentsValidator extends AbstractValidator
{

	public List validateArtifact(Artifact artifact,
			List remoteRepositories,
			ArtifactRepository localRepository) throws RepositoryToolsException
	{
		try
		{
			List result = new ArrayList();
			MavenProject project = createProject(artifact, remoteRepositories,
					localRepository);

			if (StringUtils.isEmpty(project.getDescription()))
			{
				result.add(new ValidationMessage(ValidationMessage.ERROR,
						"Project description is missing"));
			}
			if (StringUtils.isEmpty(project.getModel().getName()))
			{
				result.add(new ValidationMessage(ValidationMessage.ERROR,
						"Project name is missing"));
			}
			if (StringUtils.isEmpty(project.getUrl()))
			{
				result.add(new ValidationMessage(ValidationMessage.WARNING,
						"Project URL is missing"));
			}
			if (project.getScm() == null)
			{
				result.add(new ValidationMessage(ValidationMessage.WARNING,
						"Project SCM information is missing"));
			} else
			{
				if (StringUtils.isEmpty(project.getScm().getConnection()))
				{
					result.add(new ValidationMessage(ValidationMessage.WARNING,
							"Project SCM connection is missing"));
				}
				if (StringUtils.isEmpty(project.getScm().getUrl()))
				{
					result.add(new ValidationMessage(ValidationMessage.WARNING,
							"Project SCM url is missing"));
				}
			}
			if (project.getOrganization() == null)
			{
				result.add(new ValidationMessage(ValidationMessage.INFO,
						"Project organization is missing"));
			} else
			{
				if (StringUtils.isEmpty(project.getOrganization().getName()))
				{
					result.add(new ValidationMessage(ValidationMessage.INFO,
							"Project organization name is missing"));
				}
			}
			return result;
		}
		catch (ProjectBuildingException e)
		{
			throw new RepositoryToolsException("Could not build project", e);
		}

	}

	public String getDescription()
	{
		return "Validation of required POM elements";
	}

	public boolean canValidate(Artifact artifact)
	{
		return artifact.getType().equals("pom");
	}

}
