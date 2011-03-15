package org.codehaus.mojo.cis.maven;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.codehaus.mojo.cis.core.CisUtils;


/**
 * Abstract base class for deriving CIS related mojos.
 */
public abstract class AbstractCisMojo extends AbstractMojo
{
    private static final String CIS_GROUP_ID = "com.softwareag.cis";
    private static final String CIS_ARTIFACT_ID = "cis";

    /**
     * Specifies a particular CIS webapp artifact to use.
     * By default, the CIS webapp is derived from the cis jar file.
     * The CIS webapp artifact is used to extract the {@code cisconfig.xml}
     * file.
     *
     * @parameter
     */
    private org.codehaus.mojo.cis.model.Artifact cisWebappArtifact;

    /**
     * The CIS home directory. This is the directory, where the
     * web application is being assembled.
     * @parameter expression="${cis.homeDir}"
     */
    private File cisHomeDir;

    /**
     * The CIS markers directory. This is the directory, where the
     * marker files are being created.
     *
     * @parameter expression="${cis.markersDir}" default-value="${project.build.directory}/cis-maven-plugin/markers"
     */
    private File cisMarkersDirectory;

    /**
     * The maven project. This is a component, which is set automatically by Maven and
     * must not be set by the user.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The dependency tree builder to use. This is a component,
     * which is set automatically by Maven and must not be set
     * by the user.
     * 
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * The artifact collector to use. This is a component,
     * which is set automatically by Maven and must not be set
     * by the user.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactCollector artifactCollector;

    /**
     * The artifact metadata source to use. This is a component,
     * which is set automatically by Maven and must not be set
     * by the user.
     * 
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * Returns the web application directory.
     */
    protected File getCisHomeDir()
    {
        return cisHomeDir;
    }

    /**
     * Returns the marker directory.
     */
    protected File getCisMarkersDir()
    {
        return cisMarkersDirectory;
    }
    
    /**
     * Returns a new instance of {@link CisUtils}.
     */
    protected CisUtils newCisUtils()
    {
        return new MavenCisUtils(this);
    }

    /**
     * Returns the Maven project, which is currently being built.
     */
    protected MavenProject getProject() {
        return project;
    }

    /**
     * The artifact factory to use. This is a component,
     * which is set automatically by Maven and must not be set
     * by the user.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * The artifact resolver to use. This is a component,
     * which is set automatically by Maven and must not be set
     * by the user.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * The local repository to use.  This is a parameter,
     * which is set automatically by Maven and must not be set
     * by the user.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;
    
    /**
     * The list of remote repositories to use.  This is a parameter,
     * which is set automatically by Maven and must not be set
     * by the user.
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List remoteRepositories;

    /**
     * Returns the location of the {@code cis.jar} artifact.
     */
    protected Artifact getCisJar( boolean pRequired )
        throws MojoFailureException, MojoExecutionException
    {
        return getDependency( CIS_GROUP_ID, CIS_ARTIFACT_ID, "jar", "", pRequired );
    }

    /**
     * Returns the location of the given artifact.
     */
    protected Artifact getDependency( final String pGroupId,
                                    final String pArtifactId, final String pType,
                                    final String pClassifier,
                                    boolean pRequired)
        throws MojoExecutionException, MojoFailureException
    {
        // Search for cis-x.y.jar, first in the direct dependencies
        Artifact a = getDirectDependency( pGroupId, pArtifactId, pType, pClassifier );
        if ( a == null )
        {
            a = getIndirectDependency( pGroupId, pArtifactId, pType, pClassifier );
            if ( a == null )
            {
                if ( pRequired )
                {
                    throw new MojoFailureException( "The project doesn't have a dependency "
                                                    + pGroupId + ":" + pArtifactId + ":"
                                                    + pClassifier + ":" + pType );
                }
            }
        }
        return a;
    }

    private Artifact getIndirectDependency( final String pGroupId,
                                            final String pArtifactId, final String pType,
                                            final String pClassifier )
        throws MojoExecutionException
    {
        // Not found, search for cis-x.y.jar in the transitive dependencies
        DependencyTree tree;
        try
        {
            tree = dependencyTreeBuilder.buildDependencyTree( getProject(), localRepository, artifactFactory,
                                                              artifactMetadataSource, artifactCollector );
        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        for ( Iterator iter = tree.getArtifacts().iterator();  iter.hasNext();  )
        {
            Artifact a = (Artifact) iter.next();
            final String cl = a.getClassifier() == null ? "" : a.getClassifier();
            if ( pGroupId.equals( a.getGroupId() )
                            &&  pArtifactId.equals( a.getArtifactId() )
                            &&  pClassifier.equals( cl )
                            &&  pType.equals( a.getType() ) )
            {
                return a;
            }
        }
        return null;
    }

    private Artifact getDirectDependency( final String pGroupId,
                                          final String pArtifactId, final String pType,
                                          final String pClassifier )
    {
        final Set artifacts = getProject().getDependencyArtifacts();
        if ( artifacts != null )
        {
            for ( Iterator iter = artifacts.iterator();  iter.hasNext();  )
            {
                Artifact a = (Artifact) iter.next();
                final String cl = a.getClassifier() == null ? "" : a.getClassifier();
                if ( pGroupId.equals( a.getGroupId() )
                                &&  pArtifactId.equals( a.getArtifactId() )
                                &&  pClassifier.equals( cl )
                                &&  pType.equals( a.getType() ) )
                {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * Returns the location of the {@code cis.war} artifact.
     */
    protected Artifact getCisWebapp()
        throws MojoExecutionException, MojoFailureException
    {
        if ( cisWebappArtifact == null )
        {
            final Artifact cisJarArtifact = getCisJar( true );
            if ( cisJarArtifact == null )
            {
                throw new MojoFailureException( "Unable to determine a source file. "
                                                + " Use either of the parameters "
                                                + " cisConfigSourceFile or"
                                                + " cisWebappArtifact to configure it." );
            }
            cisWebappArtifact = new org.codehaus.mojo.cis.model.Artifact();
            cisWebappArtifact.setArtifactId( cisJarArtifact.getArtifactId() + "-webapp" );
            cisWebappArtifact.setGroupId( cisJarArtifact.getGroupId() );
            cisWebappArtifact.setVersion( cisJarArtifact.getVersion() );
            cisWebappArtifact.setScope( cisJarArtifact.getScope() );
            cisWebappArtifact.setClassifier( cisJarArtifact.getClassifier() );
            cisWebappArtifact.setType( "war" );
        }
        return findCisWebapp();
    }

    private Artifact findCisWebapp() throws MojoExecutionException
    {
        final String groupId = cisWebappArtifact.getGroupId();
        final String artifactId = cisWebappArtifact.getArtifactId();
        final String version = cisWebappArtifact.getVersion();
        final String scope = cisWebappArtifact.getScope() == null ? Artifact.SCOPE_COMPILE : cisWebappArtifact.getScope();
        final String classifier = cisWebappArtifact.getClassifier();
        final String type = cisWebappArtifact.getType() == null ? "war" : cisWebappArtifact.getType();
        Artifact artifact = artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
        artifact.setScope( scope );

        /* Make sure, that the artifact is available in the local repository.
         * This ensures, that we can access it through
         * <code>localRepository.pathOf( artifact )</code> later on.
         */
        try
        {
            artifactResolver.resolve( artifact, remoteRepositories, localRepository );
        }
        catch ( AbstractArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Failed to resolve artifact "
                                              + artifact.getGroupId() + ":"
                                              + artifact.getArtifactId() + ":"
                                              + artifact.getVersion() + ": "
                                              + e.getMessage(), e );
        }
        return artifact;
    }

    /**
     * Returns the location of the {@code cis.war} file as an
     * instance of {@link java.io.File}.
     */
    protected File getCisWebappFile() throws MojoExecutionException, MojoFailureException
    {
        final Artifact artifact = getCisWebapp();
        return new File( localRepository.getBasedir(), localRepository.pathOf( artifact ) );
    }
}
