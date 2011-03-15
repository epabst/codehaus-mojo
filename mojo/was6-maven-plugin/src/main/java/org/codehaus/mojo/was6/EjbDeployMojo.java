package org.codehaus.mojo.was6;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;

/**
 * Generates EJB RMIC stub sources.
 * <p />
 * This goal will fork a parallel life cycle up to package phase. This is required because an archive is required as
 * input to the underlying tasks.
 * 
 * @goal ejbdeploy
 * @phase generate-sources
 * @requiresDependencyResolution runtime
 * @execute phase="package"
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class EjbDeployMojo
    extends AbstractEjbMojo
{
    /**
     * Reference to project which was forked in parallel.
     * 
     * @parameter default-value="${executedProject}"
     * @required
     * @readonly
     */
    private MavenProject executedProject;

    /**
     * Set to true to disable validation messages.
     * 
     * @parameter expression="${was6.noValidate}" default-value="false"
     */
    private boolean noValidate;

    /**
     * Set to true to disable warning and informational messages.
     * 
     * @parameter expression="${was6.noWarnings}" default-value="false"
     */
    private boolean noWarnings;

    /**
     * Set to true to disable informational messages.
     * 
     * @parameter expression="${was6.noInform}" default-value="false"
     */
    private boolean noInform;

    /**
     * Set this to true if you've got an old rational SDP version (7.0.0.4/interimfix 001), or an old WAS base/ND
     * installation (lower than fixpack 007).
     * 
     * @parameter expression="${was6.legacyMode}" default-value="false"
     */
    private boolean legacyMode;

    /**
     * Specifies the name of the database to create.
     * 
     * @parameter expression="${was6.dbname}"
     */
    private String dbname;

    /**
     * Specifies the name of the database schema to create.
     * 
     * @parameter expression="${was6.dbschema}"
     */
    private String dbschema;

    /**
     * Specifies the type of database the EJBs will use.
     * 
     * @parameter expression="${was6.dbvendor}"
     */
    private String dbvendor;

    /**
     * Specifies to enable dynamic query support.
     * 
     * @parameter expression="${was6.dynamic}"
     */
    private boolean dynamic;

    /**
     * Set to true to use WebSphere 3.5 compatible mapping rules.
     * 
     * @parameter expression="${was6.compatible35}"
     */
    private boolean compatible35;

    /**
     * Set to true to generate SQL/J persistor code.
     * 
     * @parameter expression="${was6.sqlj}"
     */
    private boolean sqlj;

    /**
     * JDK compliance level. Valid values are: 1.4 or 5.0 This parameter will only be taken into consideration if
     * legacyMode is false. IBM didn't support this flag in earlier versions.
     * 
     * @parameter expression="${was6.jdkComplianceLevel}" default-value="${project.build.java.target}"
     */
    private String jdkComplianceLevel;

    protected File getOutputJarFile()
    {
        File outputJarFile = new File( getWorkingDirectory(), executedProject.getArtifact().getArtifactId() + "-deployed.jar" );
        return outputJarFile;
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsEjbDeploy";
    }

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        //hack to avoid IBM bug: http://jira.codehaus.org/browse/MWAS-7
        document.getRootElement().addElement( "property" ).addAttribute( "name", "user.install.root" ).addAttribute( "location", getWasHome().getAbsolutePath() );

        File inputFile = executedProject.getArtifact().getFile();
        if ( !inputFile.canRead() )
        {
            throw new MojoExecutionException( "Invalid archive: " + inputFile.getAbsolutePath() );
        }
        configureTaskAttribute( document, "inputJar", inputFile.getAbsolutePath() );
        configureTaskAttribute( document, "outputJar", getOutputJarFile() );
        configureTaskAttribute( document, "workingDirectory", getWorkingDirectory().getAbsolutePath() );
        configureTaskAttribute( document, "trace", Boolean.toString( isVerbose() ) );
        configureTaskAttribute( document, "noInform", Boolean.toString( noInform ) );
        configureTaskAttribute( document, "noWarnings", Boolean.toString( noWarnings ) );
        configureTaskAttribute( document, "noValidate", Boolean.toString( noValidate ) );
        configureTaskAttribute( document, "classpath", getRuntimeClasspath() );
        configureTaskAttribute( document, "dbname", dbname );
        configureTaskAttribute( document, "dbvendor", dbvendor );
        configureTaskAttribute( document, "dbschema", dbschema );
        configureTaskAttribute( document, "dynamic", Boolean.toString( dynamic ) );
        configureTaskAttribute( document, "compatible35", Boolean.toString( compatible35 ) );
        configureTaskAttribute( document, "sqlj", Boolean.toString( sqlj ) );

        if ( legacyMode )
        {
            getLog().warn( "Legacy mode - jdkComplianceLevel will NOT be taken into consideration (default will be used)" );
            configureTaskAttribute( document, "jdkComplianceLevel", null );
        }
        else
        {
            configureTaskAttribute( document, "jdkComplianceLevel", 
                                    "1.5".equals( jdkComplianceLevel ) ? "5.0" : jdkComplianceLevel );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !getMavenProject().getPackaging().equalsIgnoreCase( "ejb" ) )
        {
            throw new MojoExecutionException( "Invalid packaging type, this plugin can only be applied to ejb packaging type projects" );
        }

        super.execute();
        
        if ( !getOutputJarFile().exists() )  //TODO: Solve generically - MWAS-14 - why doesn't failOnError fail the build and ws_ant return a returncode != 0?
        {
            throw new MojoExecutionException( "Deployment failed - see previous errors" );
        }

        File[] workingDirectorySubdirs =
            getWorkingDirectory().listFiles( (java.io.FileFilter) DirectoryFileFilter.DIRECTORY );
        if ( workingDirectorySubdirs.length == 1 )
        {
            // copy sources
            File generatedSources = new File( workingDirectorySubdirs[0], getMavenProject().getBuild().getFinalName() + File.separator + "ejbModule" );
            try
            {
                FileUtils.copyDirectory( generatedSources, getGeneratedSourcesDirectory() );
                FileUtils.deleteDirectory( new File( getGeneratedSourcesDirectory(), "META-INF" ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error copying generated sources", e );
            }

            List compileSourceRoots = getMavenProject().getCompileSourceRoots();
            compileSourceRoots.add( getGeneratedSourcesDirectory().getPath() );

            // copy generated classes
            File generatedClasses =
                new File( workingDirectorySubdirs[0], getMavenProject().getBuild().getFinalName() + File.separator +
                    "build" + File.separator + "classes" );

            try
            {
                FileUtils.copyDirectory( generatedClasses, getGeneratedClassesDirectory() );
                Resource resource = new Resource();
                resource.setDirectory( getGeneratedClassesDirectory().getPath() );
                getMavenProject().getResources().add( resource );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error copying generated classes", e );
            }
        }
        else
        {
            getLog().warn( "No sources were generated" );
        }

        getLog().info( "ejbDeploy finished" );
    }

    /**
     * Computes the runtime classpath.
     * 
     * @return A representation of the computed runtime classpath.
     * @throws MojoExecutionException in case of dependency resolution failure
     */
    private String getRuntimeClasspath()
        throws MojoExecutionException
    {
        try
        {
            // get the union of compile- and runtime classpath elements
            Set dependencySet = new HashSet();
            dependencySet.addAll( executedProject.getCompileClasspathElements() );
            dependencySet.addAll( executedProject.getRuntimeClasspathElements() );
            String compileClasspath = StringUtils.join( dependencySet, File.pathSeparator );

            return compileClasspath;
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

}
