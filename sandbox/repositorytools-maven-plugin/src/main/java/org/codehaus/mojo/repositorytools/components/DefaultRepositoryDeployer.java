package org.codehaus.mojo.repositorytools.components;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.archiva.discoverer.DiscovererException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.deployer.ArtifactDeployer;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.GroupRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @plexus.component role="org.codehaus.mojo.repositorytools.components.RepositoryDeployer"
 *                   role-hint="default"
 * @author tom
 * 
 */
public class DefaultRepositoryDeployer extends AbstractLogEnabled implements
		RepositoryDeployer
{

	/**
	 * @plexus.requirement
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @plexus.requirement
	 */
	private WagonManager wagonManager;

	/**
	 * @plexus.requirement
	 */
	private ArtifactDeployer deployer;

	/**
	 * @plexus.requirement role="org.apache.maven.archiva.discoverer.ArtifactDiscoverer" role-hint="wagon"
	 */
	private ArtifactDiscoverer discoverer;

	/**
	 * @plexus.requirement
	 */
	private RepositoryMetadataManager repositoryMetadataManager;

	private File[] findMetadataFiles(String path, ArtifactRepository local)
	{
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(local.getBasedir());
		scanner.setIncludes(new String[]
		{ path + "maven-metadata-*.xml", path + "maven-metadata.xml" });
		scanner.setExcludes(new String[]
		{ path + "maven-metadata-remote.xml" });
		scanner.scan();
		String[] paths = scanner.getIncludedFiles();

		File[] result = new File[paths.length];
		for (int i = 0; i < paths.length; i++)
		{
			result[i] = new File(local.getBasedir(), paths[i]);
		}

		return result;
	}

	public Set deployArtifacts(Set artifacts,
			ArtifactRepository local, ArtifactRepository remote)
			throws RepositoryToolsException
	{
		List failed = new ArrayList(); // <Artifact>
		Set success = new HashSet(); // <Artifact>
		for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
			Artifact artifact = (Artifact) iterator.next();
			try
			{
				if (artifact.getType().equals("pom"))
				{
					loadMetadata(artifact, local, remote);
				}

				deployer.deploy(artifact.getFile(), artifact, remote, local);

				Artifact signature = createSignatureArtifact(artifact);
				File signatureFile = new File(local.getBasedir(), local
						.pathOf(signature));
				if (signatureFile.exists())
				{
					deployer.deploy(signatureFile, signature, remote, local);
				}

				success.add(artifact);
			}
			catch (ArtifactDeploymentException e)
			{
				failed.add(artifact);
				getLogger().debug("Could not deploy artifact " + artifact, e);
			}
		}

		for (Iterator iterator = success.iterator(); iterator.hasNext();) {
			Artifact a = (Artifact) iterator.next();
			getLogger().info("Artifact " + a + " deployed");
		}
		for (Iterator iterator = failed.iterator(); iterator.hasNext();) {
			Artifact a = (Artifact) iterator.next();
			getLogger().warn("Artifact " + a + " could not be deployed");
		}

		if (!failed.isEmpty())
		{
			throw new RepositoryToolsException(
					"Some artifacts could not be deployed");
		}

		return success;
	}

	private void loadMetadata(Artifact artifact, ArtifactRepository source,
			ArtifactRepository remote) throws RepositoryToolsException
	{
//		artifact.addMetadata(new ProjectArtifactMetadata(artifact, artifact
//				.getFile()));
		String path = artifact.getGroupId().replace('.', File.separatorChar)
				+ File.separator + artifact.getArtifactId() + File.separator;
		ArtifactRepositoryMetadata artifactMetadata = new ArtifactRepositoryMetadata(
				artifact);

		loadMetadata(path, artifactMetadata, source, remote);
		artifact.addMetadata(artifactMetadata);

		GroupRepositoryMetadata groupMetadata = new GroupRepositoryMetadata(
				artifact.getGroupId());
		path = artifact.getGroupId().replace('.', File.separatorChar)
				+ File.separator;
		loadMetadata(path, groupMetadata, source, remote);
		List plugins = groupMetadata.getMetadata().getPlugins();
		Iterator it = plugins.iterator();
		while (it.hasNext()) {
			Plugin plugin = (Plugin) it.next();
			if (!plugin.getArtifactId().equals(artifact.getArtifactId())) {
				it.remove();
			}
		}
		artifact.addMetadata(groupMetadata);
	}

	private Artifact createSignatureArtifact(Artifact artifact)
	{
		Artifact signature;
		if (artifact.getClassifier() != null)
		{
			signature = artifactFactory.createArtifactWithClassifier(artifact
					.getGroupId(), artifact.getArtifactId(), artifact
					.getVersion(), "asc", artifact.getClassifier() + "."
					+ artifact.getArtifactHandler().getExtension());
		} else
		{
			signature = artifactFactory.createArtifactWithClassifier(artifact
					.getGroupId(), artifact.getArtifactId(), artifact
					.getVersion(), artifact.getArtifactHandler().getExtension()
					+ ".asc", null);
		}
		return signature;
	}

	private void removeExistingArtifacts(List artifacts,
			ArtifactRepository remote, Wagon wagon)
			throws RepositoryToolsException
	{
		for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
			Artifact artifact = (Artifact) iterator.next();

			try
			{
				File temp = File.createTempFile("DeployMojo-", ".tmp");
				temp.deleteOnExit();
				String remotePath = remote.pathOf(artifact);
				wagon.get(remotePath, temp);

				if (!FileUtils.contentEquals(artifact.getFile(), temp))
				{
					throw new TransferFailedException("Remote file found for "
							+ artifact
							+ ", but it is different from the local file!");
				}

				// if we get to here, we could retrieve an existing file,
				// and it it the same as the current file
				// -> we won't deploy

				iterator.remove();
			}
			catch (TransferFailedException e)
			{
				throw new RepositoryToolsException(
						"Could not authenticate to target repository", e);
			}
			catch (ResourceDoesNotExistException e)
			{
				// expected exception
				// the resource doesn't exist, so we can deploy
			}
			catch (AuthorizationException e)
			{
				throw new RepositoryToolsException(
						"Could not authenticate to target repository", e);
			}
			catch (IOException e)
			{
				throw new RepositoryToolsException(e);
			}
		}
	}

	private void loadMetadata(String path, RepositoryMetadata repoMetadata,
			ArtifactRepository source, ArtifactRepository remote)
			throws RepositoryToolsException
	{
		if (!remote.getProtocol().equals("file"))
		{
			try
			{
				repositoryMetadataManager.resolveAlways(repoMetadata, source,
						remote);
			}
			catch (RepositoryMetadataResolutionException e)
			{
				throw new RepositoryToolsException(
						"Could not get remote metadata");
			}
		} else
		{
			String targetMetadata = "maven-metadata-" + remote.getId() + ".xml";

			File[] metadataFiles = findMetadataFiles(path, source);
			try
			{
				for (int i = 0; i < metadataFiles.length; i++) {
					File metadataFile = metadataFiles[i];
					if (!metadataFile.getName().equals(targetMetadata))
					{
						Reader fileReader = new FileReader(metadataFile);
						MetadataXpp3Reader mappingReader = new MetadataXpp3Reader();
						Metadata metadata = mappingReader.read(fileReader);
						if (repoMetadata.getMetadata() != null)
						{
							repoMetadata.getMetadata().merge(metadata);
						} else
						{
							repoMetadata.setMetadata(metadata);
						}
					} else
					{
						// ignore metadata for the target repository
						// this will be merged on deployment
					}
				}
			}
			catch (IOException e)
			{
				throw new RepositoryToolsException("Could not load metadata", e);
			}
			catch (XmlPullParserException e)
			{
				throw new RepositoryToolsException("Could not load metadata", e);
			}
		}
	}

	public Set getDeployableArtifacts(ArtifactRepository local,
			ArtifactRepository target, boolean checkExisting)
			throws RepositoryToolsException
	{
		try
		{
			List artifacts = discoverer.discoverArtifacts(local,
					new ArrayList(), new ArtifactFilter()
					{

						public boolean include(Artifact artifact)
						{
							if (artifact.getFile().getName().endsWith(".tmp"))
							{
								return false;
							}
							return true;
						}
					});

			String protocol = target.getProtocol();
			Wagon wagon = wagonManager.getWagon(protocol);

			wagon.connect(new Repository(target.getId(), target.getUrl()),
					wagonManager.getAuthenticationInfo(target.getId()),
					wagonManager.getProxy(protocol));

			if (checkExisting)
			{
				removeExistingArtifacts(artifacts, target, wagon);
			}

			wagon.disconnect();
			return new HashSet(artifacts);
		}
		catch (UnsupportedProtocolException e)
		{
			throw new RepositoryToolsException("Protocol not supported: "
					+ target.getProtocol());
		}
		catch (ConnectionException e)
		{
			throw new RepositoryToolsException("Could not connect to remote: "
					+ target.getUrl());
		}
		catch (AuthenticationException e)
		{
			throw new RepositoryToolsException(
					"Could not authenticate to remote repository");
		}
		catch (DiscovererException e)
		{
			throw new RepositoryToolsException(
					"Could not discover artifacts in local repository");
		}

	}
}
