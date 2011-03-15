package org.codehaus.mojo.ship;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 * Ships the {@link #shipVersion} of the project artifacts using the Continuous Deployment script.
 *
 * @author Stephen Connolly
 * @goal ship
 * @description Ships the {@link #shipVersion} of the project artifacts using the Continuous Deployment script.
 * @threadSafe
 * @since 0.1
 */
public class ShipMojo
        extends AbstractMojo {
    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    private org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    private org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected java.util.List remoteRepos;

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @readonly
     * @since 0.1
     */
    private MavenProject project;

    /**
     * The version of the project artifacts to ship.
     *
     * @parameter expression="${shipVersion}" default-value="${project.version}"
     * @since 0.1
     */
    private String shipVersion;

    /**
     * Whether to allow shipping -SNAPSHOT versions, if <code>true</code> and the {@link #shipVersion} is a -SNAPSHOT
     * version then the build will be failed unless {@link #shipSnapshotsSkipped} is <code>true</code>.
     *
     * @parameter expression="${shipSnapshotsAllowed}" default-value="false"
     * @since 0.1
     */
    private boolean shipSnapshotsAllowed;

    /**
     * Whether to try and ship -SNAPSHOT versions, if <code>true</code> and the {@link #shipVersion} is a -SNAPSHOT
     * version then an attempt will be made to ship the project artifacts .
     *
     * @parameter expression="${shipSnapshotsSkipped}" default-value="false"
     * @since 0.1
     */
    private boolean shipSnapshotsSkipped;

    /**
     * Whether to bother trying to ship anything at all.
     *
     * @parameter expression="${shipSkip}" default-value="false"
     * @since 0.1
     */
    private boolean shipSkip;

    /**
     * The project artifacts to ship, if undefined then it will default to the project artifact.
     *
     * @parameter
     * @since 0.1
     */
    private Selector[] selectors;

    /**
     * The directory containing the ship scripts.
     *
     * @parameter default-value="src/ship/script"
     */
    private String shipScriptDirectory;

    /**
     * The name of the ship script to execute, the selected artifact files will be passed as the global variable
     * <code>artifacts</code> which is a {@ling File[]} in the order of the selectors.
     *
     * @parameter expression="${shipScript}"
     * @required
     */
    private String shipScript;

    public void execute()
            throws MojoExecutionException, MojoFailureException {
        if (shipSkip) {
            getLog().info("Shipping skipped.");
            return;
        }
        getLog().info("Ship version: " + shipVersion);
        if (ArtifactUtils.isSnapshot(shipVersion)) {
            if (shipSnapshotsSkipped) {
                getLog().info("Shipping skipped as ship version is a -SNAPSHOT");
                return;
            }
            if (!shipSnapshotsAllowed) {
                throw new MojoExecutionException("Shipping -SNAPSHOT version is not allowed and the requested shipVersion (" + shipVersion + ") is a -SNAPSHOT");
            }
        }
        boolean searchReactor = StringUtils.equals(shipVersion, project.getVersion());
        if (searchReactor) {
            getLog().debug("Ship version is project version, will preferentially resolve from the reactor");
        }
        if (selectors == null) {
            selectors = new Selector[]{new Selector(project.getPackaging(), null)};
        }
        List artifacts = new ArrayList();
        if (project.getArtifact() != null) {
            artifacts.add(project.getArtifact());
        }
        if (project.getAttachedArtifacts() != null) {
            artifacts.addAll(project.getAttachedArtifacts());
        }
        List artifactFiles = new ArrayList(selectors.length);
        for (int i = 0; i < selectors.length; i++) {
            if (StringUtils.isEmpty(selectors[i].getType())) {
                selectors[i].setType(project.getPackaging());
            }
            if (StringUtils.isEmpty(selectors[i].getClassifier())) {
                selectors[i].setClassifier(null);
            }
            getLog().debug("Using selector " + selectors[i]);
            Artifact artifact = null;
            if (searchReactor) {
                artifact = select(artifacts, selectors[i]);
            }
            if (artifact == null || artifact.getFile() == null || !artifact.getFile().isFile()) {
                try {
                    Artifact tmp = factory.createArtifactWithClassifier(project.getGroupId(), project.getArtifactId(), shipVersion, selectors[i].getType(), selectors[i].getClassifier());
                    resolver.resolve(tmp, remoteRepos, local);
                    artifact = tmp;
                } catch (ArtifactResolutionException e) {
                    throw new MojoExecutionException(e.getLocalizedMessage(), e);
                } catch (ArtifactNotFoundException e) {
                    // ignore
                }
            }
            if (artifact == null) {
                throw new MojoExecutionException("Could not find required artifact " + project.getGroupId() + ":" +
                        project.getArtifactId() + ":" + shipVersion + ":" + selectors[i].getType() + ":" +
                        selectors[i].getClassifier());
            }
            if (artifact.getFile() == null) {
                throw new MojoExecutionException("Resolved artifact " + artifact + " does not have a resolved file.");
            }
            if (!artifact.getFile().isFile()) {
                throw new MojoExecutionException("Resolved artifact " + artifact + "'s resolved file does not exist.");
            }
            artifactFiles.add(artifact.getFile());
        }
        getLog().info("Ship: " + artifactFiles);
        ScriptEngineManager mgr = new ScriptEngineManager();
        File script = new File(new File(project.getBasedir(), shipScriptDirectory), shipScript);
        if (!script.isFile()) {
            throw new MojoExecutionException("Specified ship script (" + script + ") does not exist");
        }
        Map scriptVars = new LinkedHashMap();
        scriptVars.put("artifacts", artifactFiles.toArray(new File[artifactFiles.size()]));
        mgr.eval(script, scriptVars, getLog());
    }

    private Artifact select(List artifacts, Selector selector) {
        Iterator i = artifacts.iterator();
        while (i.hasNext()) {
            Artifact artifact = (Artifact) i.next();
            if (StringUtils.equals(project.getGroupId(), artifact.getGroupId()) &&
                    StringUtils.equals(project.getArtifactId(), artifact.getArtifactId()) &&
                    selector.matches(artifact)) {
                return artifact;
            }
        }
        return null;
    }
}
