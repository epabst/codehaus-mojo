package org.codehaus.mojo.repositorytools.discoverer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.archiva.discoverer.DiscovererException;
import org.apache.maven.archiva.discoverer.DiscovererPath;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;

/*******************************************************************************
 * 
 * @author Tom
 * @plexus.component role="org.apache.maven.archiva.discoverer.ArtifactDiscoverer"
 *                   role-hint="wagon"
 * 
 */
public class WagonDiscoverer extends AbstractLogEnabled implements
		ArtifactDiscoverer {

	private List kickedOutPaths = new ArrayList();

	/**
	 * @plexus.requirement
	 */
	protected ArtifactFactory artifactFactory;

	private List excludedPaths = new ArrayList();

	/**
	 * @plexus.configuration default-value="true"
	 */
	private boolean trackOmittedPaths;

	/**
	 * @plexus.requirement
	 * 
	 */
	private WagonManager wagonManager;

	/**
	 * Add a path to the list of files that were kicked out due to being
	 * invalid.
	 * 
	 * @param path
	 *            the path to add
	 * @param reason
	 *            the reason why the path is being kicked out
	 */
	protected void addKickedOutPath(String path, String reason) {
		if (trackOmittedPaths) {
			kickedOutPaths.add(new DiscovererPath(path, reason));
		}
	}

	/**
	 * Add a path to the list of files that were excluded.
	 * 
	 * @param path
	 *            the path to add
	 * @param reason
	 *            the reason why the path is excluded
	 */
	protected void addExcludedPath(String path, String reason) {
		excludedPaths.add(new DiscovererPath(path, reason));
	}

	/**
	 * Returns an iterator for the list if DiscovererPaths that were found to
	 * not represent a searched object
	 * 
	 * @return Iterator for the DiscovererPath List
	 */
	public Iterator getKickedOutPathsIterator() {
		//		assert trackOmittedPaths;
		return kickedOutPaths.iterator();
	}

	private void scan(Wagon wagon, String basePath, List collected) {
		for (int i = 0; i < STANDARD_DISCOVERY_EXCLUDES.length; i++) {
			if (SelectorUtils.matchPath(STANDARD_DISCOVERY_EXCLUDES[i], basePath)) {
				return;
			}
		}

		try {
			List files = wagon.getFileList(basePath);

			if (files.isEmpty()) {
				collected.add(basePath);
			} else {
				basePath = basePath + "/";
				for (Iterator iterator = files.iterator(); iterator.hasNext();) {
					String file = (String) iterator.next();
					scan(wagon, basePath + file, collected);
				}
			}
		} catch (TransferFailedException e) {
			throw new RuntimeException(e);
		} catch (ResourceDoesNotExistException e) {
			// is thrown when calling getFileList on a file
			collected.add(basePath);
		} catch (AuthorizationException e) {
			throw new RuntimeException(e);
		}

	}

	protected List scanForArtifactPaths(ArtifactRepository repository,
			List blacklistedPatterns, String[] includes, String[] excludes) {

		List collected;
		try {
			Wagon wagon = wagonManager.getWagon(repository.getProtocol());
			Repository artifactRepository = new Repository(repository.getId(),
					repository.getUrl());
			wagon.connect(artifactRepository);
			collected = new ArrayList();
			scan(wagon, "/", collected);
			wagon.disconnect();

			return collected;

		} catch (UnsupportedProtocolException e) {
			throw new RuntimeException(e);
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		} catch (AuthenticationException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Returns an iterator for the list if DiscovererPaths that were not
	 * processed because they are explicitly excluded
	 * 
	 * @return Iterator for the DiscovererPath List
	 */
	public Iterator getExcludedPathsIterator() {
		// assert trackOmittedPaths;
		return excludedPaths.iterator();
	}

	public void setTrackOmittedPaths(boolean trackOmittedPaths) {
		this.trackOmittedPaths = trackOmittedPaths;
	}

	public Artifact buildArtifact(String path) throws DiscovererException {
		List pathParts = new ArrayList();
		StringTokenizer st = new StringTokenizer(path, "/\\");
		while (st.hasMoreTokens()) {
			pathParts.add(st.nextToken());
		}

		Collections.reverse(pathParts);

		Artifact artifact;
		if (pathParts.size() >= 4) {
			// maven 2.x path

			// the actual artifact filename.
			String filename = (String) pathParts.remove(0);

			// the next one is the version.
			String version = (String) pathParts.remove(0);

			// the next one is the artifactId.
			String artifactId = (String) pathParts.remove(0);

			// the remaining are the groupId.
			Collections.reverse(pathParts);
			String groupId = StringUtils.join(pathParts.iterator(), ".");

			String remainingFilename = filename;
			if (remainingFilename.startsWith(artifactId + "-")) {
				remainingFilename = remainingFilename.substring(artifactId
						.length() + 1);

				String classifier = null;

				// TODO: use artifact handler, share with legacy discoverer
				String type;
				if (remainingFilename.endsWith(".tar.gz")) {
					type = "distribution-tgz";
					remainingFilename = remainingFilename.substring(0,
							remainingFilename.length() - ".tar.gz".length());
				} else if (remainingFilename.endsWith(".zip")) {
					type = "distribution-zip";
					remainingFilename = remainingFilename.substring(0,
							remainingFilename.length() - ".zip".length());
				} else if (remainingFilename.endsWith("-test-sources.jar")) {
					type = "java-source";
					classifier = "test-sources";
					remainingFilename = remainingFilename.substring(0,
							remainingFilename.length()
									- "-test-sources.jar".length());
				} else if (remainingFilename.endsWith("-sources.jar")) {
					type = "java-source";
					classifier = "sources";
					remainingFilename = remainingFilename.substring(0,
							remainingFilename.length()
									- "-sources.jar".length());
				} else {
					int index = remainingFilename.lastIndexOf(".");
					if (index >= 0) {
						type = remainingFilename.substring(index + 1);
						remainingFilename = remainingFilename.substring(0,
								index);
					} else {
						throw new DiscovererException(
								"Path filename does not have an extension");
					}
				}

				Artifact result;
				if (classifier == null) {
					result = artifactFactory.createArtifact(groupId,
							artifactId, version, Artifact.SCOPE_RUNTIME, type);
				} else {
					result = artifactFactory.createArtifactWithClassifier(
							groupId, artifactId, version, type, classifier);
				}

				if (result.isSnapshot()) {
					// version is *-SNAPSHOT, filename is *-yyyyMMdd.hhmmss-b
					int classifierIndex = remainingFilename.indexOf('-',
							version.length() + 8);
					if (classifierIndex >= 0) {
						classifier = remainingFilename
								.substring(classifierIndex + 1);
						remainingFilename = remainingFilename.substring(0,
								classifierIndex);
						result = artifactFactory.createArtifactWithClassifier(
								groupId, artifactId, remainingFilename, type,
								classifier);
					} else {
						result = artifactFactory.createArtifact(groupId,
								artifactId, remainingFilename,
								Artifact.SCOPE_RUNTIME, type);
					}

					// poor encapsulation requires we do this to populate base
					// version
					if (!result.isSnapshot()) {
						throw new DiscovererException(
								"Failed to create a snapshot artifact: "
										+ result);
					} else if (!result.getBaseVersion().equals(version)) {
						throw new DiscovererException(
								"Built snapshot artifact base version does not match path version: "
										+ result
										+ "; should have been version: "
										+ version);
					} else {
						artifact = result;
					}
				} else if (!remainingFilename.startsWith(version)) {
					throw new DiscovererException(
							"Built artifact version does not match path version");
				} else if (!remainingFilename.equals(version)) {
					if (remainingFilename.charAt(version.length()) == '-') {
						classifier = remainingFilename.substring(version
								.length() + 1);
						artifact = artifactFactory
								.createArtifactWithClassifier(groupId,
										artifactId, version, type, classifier);
					} else {
						throw new DiscovererException(
								"Path version does not corresspond to an artifact version");
					}
				} else {
					artifact = result;
				}
			} else {
				throw new DiscovererException(
						"Path filename does not correspond to an artifact");
			}
		} else {
			throw new DiscovererException(
					"Path is too short to build an artifact from");
		}

		return artifact;
	}

	/**
	 * Standard patterns to exclude from discovery as they are not artifacts.
	 */
	private static final String[] STANDARD_DISCOVERY_EXCLUDES = { "bin/**",
			"reports/**", ".index", ".reports/**", ".maven/**", "**/*.md5",
			"**/*.MD5", "**/*.sha1", "**/*.SHA1", "**/*snapshot-version",
			"*/website/**", "*/licenses/**", "*/licences/**", "**/.htaccess",
			"**/*.html", "**/*.asc", "**/*.txt", "**/*.xml", "**/README*",
			"**/CHANGELOG*", "**/KEYS*" };

	private List scanForArtifactPaths(ArtifactRepository repository,
			List blacklistedPatterns) {
		return scanForArtifactPaths(repository, blacklistedPatterns, null,
				STANDARD_DISCOVERY_EXCLUDES);
	}

	public List discoverArtifacts(ArtifactRepository repository,
			List blacklistedPatterns, ArtifactFilter filter)
			throws DiscovererException {
		// if ( !"file".equals( repository.getProtocol() ) )
		// {
		// throw new UnsupportedOperationException( "Only filesystem
		// repositories are supported" );
		// }

		List artifacts = new ArrayList();

		List artifactPaths = scanForArtifactPaths(repository,
				blacklistedPatterns);

		for (Iterator i = artifactPaths.iterator(); i.hasNext();) {
			String path = (String) i.next();

			try {
				Artifact artifact = buildArtifactFromPath(path, repository);

				if (filter.include(artifact)) {
					artifacts.add(artifact);
				} else {
					addExcludedPath(path, "Omitted by filter");
				}
			} catch (DiscovererException e) {
				addKickedOutPath(path, e.getMessage());
			}
		}

		return artifacts;
	}

	/**
	 * Returns an artifact object that is represented by the specified path in a
	 * repository
	 * 
	 * @param path
	 *            The path that is pointing to an artifact
	 * @param repository
	 *            The repository of the artifact
	 * @return Artifact
	 * @throws DiscovererException
	 *             when the specified path does correspond to an artifact
	 */
	public Artifact buildArtifactFromPath(String path,
			ArtifactRepository repository) throws DiscovererException {
		Artifact artifact = buildArtifact(path);

		if (artifact != null) {
			artifact.setRepository(repository);
			artifact.setFile(new File(repository.getBasedir(), path));
		}

		return artifact;
	}
}
