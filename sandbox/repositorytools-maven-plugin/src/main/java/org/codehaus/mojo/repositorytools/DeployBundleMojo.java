package org.codehaus.mojo.repositorytools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.archiva.discoverer.DiscovererException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;
import org.codehaus.mojo.repositorytools.util.RepositoryUtils;
import org.codehaus.mojo.repositorytools.validation.ArtifactValidationManager;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * Deploy a collection of artifacts that are packaged in a bundle. Supported
 * bundle formats:
 * <ul>
 * <li>A single project, with pom, sources and apidoc in the root of the
 * bundle, as created by the repository plugin (NYI)</li>
 * <li>A zipped local repository</li>
 * </ul>
 * 
 * @goal deploy-bundle
 * @requiresProject false
 * @author tom
 * 
 */
public class DeployBundleMojo extends AbstractDeployMojo
{
	/**
	 * @parameter expression="${bundleURL}"
	 * @required
	 */
	private String bundleURL;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private WagonManager wagonManager;

	/**
	 * @parameter default-value="bundle" expression="${site}"
	 */
	private String siteId;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private ArchiverManager archiverManager;

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
	private Prompter prompter;
	
	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactDiscoverer discoverer;

	public ArtifactRepository createLocalRepository()
			throws RepositoryToolsException
	{
		URL url;
		try
		{
			url = new URL(bundleURL);
		}
		catch (MalformedURLException e)
		{
			throw new RepositoryToolsException("Invalid bundle URL: "
					+ bundleURL);
		}

		// extract bundle
		try
		{
			URL base = new URL(url.getProtocol(), url.getHost(), url.getPort(),
					"/");
			String file = url.getFile();

			Wagon wagon = wagonManager.getWagon(url.getProtocol());
			wagon.connect(new Repository(siteId, base.toExternalForm()));
			File dest = File.createTempFile("DeployBundle", ".jar");
			dest.deleteOnExit();
			wagon.get(file, dest);
			wagon.disconnect();

			// check format
			// assume directory structure for now

			// use the temporary file without extension as a directory
			// not unbreakable, but how do you make a unique temporary directory
			// ?
			String dirName = dest.getName().substring(0,
					dest.getName().indexOf('.'));
			File destDir = new File(dest.getParent(), dirName);
			destDir.mkdirs();

			UnArchiver unArchiver = archiverManager.getUnArchiver(dest);
			unArchiver.setDestDirectory(destDir);
			unArchiver.setSourceFile(dest);
			unArchiver.extract();

			// create single artifact or multiple artifacts

			ArtifactRepository localRepository = cliTools.createLocalRepository(destDir);

			List artifacts = discoverer.discoverArtifacts(localRepository, Collections.EMPTY_LIST, null);

			// validate artifacts
			for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
				Artifact artifact = (Artifact) iterator.next();
				Map msgs = validationManager
						.validateArtifact(artifact, cliTools.createRemoteRepositories(target),
								localRepository);

				RepositoryUtils.printValidation(getLog(), msgs);
			}
			
			String answer= prompter.prompt("Continue ?", Arrays.asList(new String[] {"Y", "N"}), "N");
			
			if (!answer.equalsIgnoreCase("Y")) {
				throw new RepositoryToolsException("Deployment interrupted by user.");
			}
			
			return localRepository;
		}
		catch (UnsupportedProtocolException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (ConnectionException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (AuthenticationException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (MalformedURLException e)
		{
			// should not happen
			throw new RepositoryToolsException("", e);
		}
		catch (TransferFailedException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (ResourceDoesNotExistException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (AuthorizationException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (IOException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (NoSuchArchiverException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (ArchiverException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (RepositoryToolsException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (PrompterException e)
		{
			throw new RepositoryToolsException("", e);
		}
		catch (DiscovererException e)
		{
			throw new RepositoryToolsException("", e);
		}

	}

}
