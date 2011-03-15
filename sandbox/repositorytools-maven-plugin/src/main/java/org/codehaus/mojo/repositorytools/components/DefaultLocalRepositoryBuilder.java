package org.codehaus.mojo.repositorytools.components;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.archiva.discoverer.DiscovererException;
import org.apache.maven.archiva.discoverer.filter.AcceptAllArtifactFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.GroupRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

/**
 * @plexus.component role="org.codehaus.mojo.repositorytools.components.LocalRepositoryBuilder"
 *                   role-hint="default"
 * @author tom
 * 
 */
public class DefaultLocalRepositoryBuilder extends AbstractLogEnabled implements
		LocalRepositoryBuilder
{

	/**
	 * @plexus.requirement
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @plexus.requirement
	 */
	private MavenProjectBuilder mavenProjectBuilder;

	/**
	 * @plexus.requirement
	 */
	private ArtifactResolver resolver;

	/**
	 * The artifact metadata source used to resolve dependencies
	 * 
	 * @plexus.requirement
	 */
	private ArtifactMetadataSource source;
	
	/**
	 * @plexus.requirement role="org.apache.maven.archiva.discoverer.ArtifactDiscoverer" role-hint="wagon"
	 */
	private ArtifactDiscoverer discoverer;

	/**
	 * @plexus.requirement
	 */
	private RepositoryMetadataManager metadataManager;

	private static String[] DEFAULT_SCOPES =
	{ Artifact.SCOPE_COMPILE, Artifact.SCOPE_RUNTIME };

	public Set addArtifact(Artifact pomArtifact,
			ArtifactRepository local, List remote,
			boolean transitive) throws RepositoryToolsException
	{

		try
		{
			MavenProject pomProject = mavenProjectBuilder.buildFromRepository(
					pomArtifact, remote, local);
			resolver.resolve(pomProject.getArtifact(), remote, local);

			Set result = new HashSet();
			if (transitive)
			{
				ArtifactFilter filter = createArtifactFilter(DEFAULT_SCOPES);

				Set artifacts = pomProject.createArtifacts(artifactFactory,
						null, filter);
				ArtifactResolutionResult arr = resolver.resolveTransitively(
						artifacts, pomArtifact, local, remote, source, filter);

				result.addAll(arr.getArtifacts());
			}
			result.add(pomProject.getArtifact());
			result.add(pomArtifact);

			for (Iterator iterator = result.iterator(); iterator.hasNext();) {
				Artifact a = (Artifact) iterator.next();
				try
				{
					GroupRepositoryMetadata groupMetadata = resolveMetadata(local, remote, a);
				}
				catch (RepositoryMetadataResolutionException e)
				{
					getLogger().warn("Error resolving metadata for " + a, e);
				}
			}

			result = getAdditionalArtifacts(result, local, remote);

			return result;

		}
		catch (ArtifactResolutionException e)
		{
			throw new RepositoryToolsException("Error resolving artifact", e);
		}
		catch (ArtifactNotFoundException e)
		{
			throw new RepositoryToolsException("Artifact not found", e);
		}
		catch (ProjectBuildingException e)
		{
			throw new RepositoryToolsException("Could not build project", e);
		}
		catch (InvalidDependencyVersionException e)
		{
			throw new RepositoryToolsException("Invalid version", e);
		}

	}

	private GroupRepositoryMetadata resolveMetadata(ArtifactRepository local, List remote, Artifact a) throws RepositoryMetadataResolutionException {
		ArtifactRepositoryMetadata artifactmetadata = new ArtifactRepositoryMetadata(
				a);
		metadataManager.resolve(artifactmetadata, remote, local);

		GroupRepositoryMetadata groupMetadata = new GroupRepositoryMetadata(
				a.getGroupId());
		metadataManager.resolveAlways(groupMetadata, local, (ArtifactRepository) remote.get(0));
		return groupMetadata;
	}
	
	private Set getAdditionalArtifacts(Set artifacts, ArtifactRepository local, List remote) {
		Set result = new HashSet(artifacts);

		for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
			Artifact a = (Artifact) iterator.next();
			if (a.getType().equals("pom"))
			{

				Artifact source = artifactFactory
						.createArtifactWithClassifier(a.getGroupId(), a
								.getArtifactId(), a.getVersion(), "jar",
								"sources");
				if (resolveOptional(local, remote, source))
				{
					result.add(source);
				}

				Artifact javadoc = artifactFactory
						.createArtifactWithClassifier(a.getGroupId(), a
								.getArtifactId(), a.getVersion(), "jar",
								"javadoc");
				if (resolveOptional(local, remote, javadoc))
				{
					result.add(javadoc);
				}
			}
		}
		
		Set signatures = new HashSet();
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			Artifact a = (Artifact) iterator.next();
			Artifact sig = createSignatureArtifact(a);
			if (resolveOptional(local, remote, sig))
			{
				signatures.add(sig);
			}
		}

		result.addAll(signatures);
		
		return result;
	}

	/**
	 * Tries to resolve, without throwing errors if not successfull.
	 * 
	 * @param local
	 * @param remote
	 * @param artifact
	 */
	private boolean resolveOptional(ArtifactRepository local,
			List remote, Artifact artifact)
	{
		boolean success = false;
		try
		{
			resolver.resolve(artifact, remote, local);
			getLogger().info("\tFound " + artifact);
			success = true;
		}
		catch (ArtifactResolutionException e)
		{
			getLogger().warn("Could not resolve " + artifact);
		}
		catch (ArtifactNotFoundException e)
		{
			getLogger().warn("Could not find artifact " + artifact);
		}
		return success;
	}

	private static ArtifactFilter createArtifactFilter(final String[] scopes)
	{
		return new ArtifactFilter()
		{
			public boolean include(Artifact artifact)
			{
				for (int i = 0; i < scopes.length; i++) {
					if (StringUtils.isEmpty(artifact.getScope())
							&& scopes[i].equals(Artifact.SCOPE_COMPILE)
							|| artifact.getScope().equals(scopes[i]))
					{
						return true;
					}
				}
				return false;
			}
		};
	}

	private Artifact createSignatureArtifact(Artifact artifact)
	{
		Artifact signature;
		if (artifact.getClassifier() != null)
		{
			signature = artifactFactory.createArtifactWithClassifier(
					artifact.getGroupId(), 
					artifact.getArtifactId(),
					artifact.getVersion(), 
					"asc",
					artifact.getClassifier() + "." + artifact.getArtifactHandler().getExtension());
		} else {
			signature = artifactFactory.createArtifactWithClassifier(
					artifact.getGroupId(), 
					artifact.getArtifactId(),
					artifact.getVersion(), 
					artifact.getArtifactHandler().getExtension() + ".asc",
					null);
		}
		return signature;
	}

	public void addPluginGroup(String groupId,
			ArtifactRepository localRepository,
			List remoteRepositories, boolean releaseOnly,
			boolean transitive)
	{
		GroupRepositoryMetadata metadata = new GroupRepositoryMetadata(groupId);
		for (Iterator iterator = remoteRepositories.iterator(); iterator
				.hasNext();) {
			ArtifactRepository remote = (ArtifactRepository) iterator.next();
			try
			{
				metadataManager
						.resolveAlways(metadata, localRepository, remote);

				for (Iterator iterator2 = metadata.getMetadata().getPlugins().iterator(); iterator2
						.hasNext();) {
					Plugin plugin = (Plugin) iterator2.next();
					try
					{
						addPlugin(groupId, plugin.getArtifactId(),
								localRepository, remote, remoteRepositories,
								releaseOnly, transitive);
					}
					catch (RepositoryToolsException e)
					{
						getLogger().warn(
								"Could not add plugin: "
										+ plugin.getArtifactId(), e);
					}
				}
			}
			catch (RepositoryMetadataResolutionException e)
			{
				getLogger().error(
						"Could not resolve metadata from " + remote.getId());
			}
		}
	}

	public void addPlugin(String groupId, String artifactId,
			ArtifactRepository local, ArtifactRepository remote,
			List allRemote, boolean releaseOnly, boolean transitive)
			throws RepositoryToolsException
	{
		Artifact artifact = artifactFactory.createArtifact(groupId, artifactId,
				Artifact.RELEASE_VERSION, null, "pom");
		ArtifactRepositoryMetadata metadata = new ArtifactRepositoryMetadata(
				artifact);
		try
		{
			metadataManager.resolveAlways(metadata, local, remote);
		}
		catch (RepositoryMetadataResolutionException e)
		{
			throw new RepositoryToolsException(
					"Could not find metadata for plugin " + artifactId);
		}
		Versioning versioning = metadata.getMetadata().getVersioning();
		if (versioning != null)
		{
			List versions;
			if (releaseOnly)
			{
				if (StringUtils.isEmpty(versioning.getRelease()))
				{
					getLogger().warn(
							"No release version found for " + artifactId);
					return;
				} else
				{
					versions = Collections.singletonList(versioning
							.getRelease());
				}
			} else
			{
				versions = versioning.getVersions();
			}
			for (Iterator iterator = versions.iterator(); iterator.hasNext();) {
				String version = (String) iterator.next();
				Artifact pluginPom = artifactFactory
						.createArtifactWithClassifier(groupId, artifactId,
								version, "jar", null);
				addArtifact(pluginPom, local, allRemote, transitive);
			}
		}

	}

	public Set addRepository(String path, ArtifactRepository localRepository, List remote, boolean releaseOnly) throws RepositoryToolsException {
		List artifacts;
		try {
			artifacts = discoverer.discoverArtifacts((ArtifactRepository) remote.get(0), Collections.EMPTY_LIST, new AcceptAllArtifactFilter());
		} catch (DiscovererException e1) {
			throw new RepositoryToolsException("Error discovering artifacts from " + remote.get(0));
		}

		Set result = new HashSet();
		
		for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
			Artifact a = (Artifact) iterator.next();
			try {
				resolver.resolveAlways(a, remote, localRepository);
				result.add(a);

				Artifact signature = createSignatureArtifact(a);
				if (resolveOptional(localRepository, remote, signature)) {
					result.add(signature);
				}
				
				if (a.getType().equals("pom")) {
					resolveMetadata(localRepository, remote, a);
				}
				
			} catch (ArtifactResolutionException e) {
				throw new RepositoryToolsException("Could not resolve " + a, e);
			} catch (ArtifactNotFoundException e) {
				throw new RepositoryToolsException("Could not resolve " + a, e);
			} catch (RepositoryMetadataResolutionException e) {
				throw new RepositoryToolsException("Could not resolve metadata for " + a, e);
			}
		}
		
		return result;
		
	}
}
