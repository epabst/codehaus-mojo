package org.apache.maven.diagrams.connector_api.context;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.logger.ConnectorLoggerToMavenEmbadderLoggerAdapter;
import org.apache.maven.embedder.Configuration;
import org.apache.maven.embedder.ConfigurationValidationResult;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.MavenProject;

/**
 * The ConnectorContext's implementation lazily runs the embadded maven (from mavenHomeDir) in given (baseDir)
 * directory. The resulting mavenProject is stored for the future calls to the getMavenProject() method.
 * 
 * @author Piotr Tabor (ptab@newitech.com)
 */
public class RunMavenConnectorContext extends AbstractConnectorContext
{
    private File baseDir;

    private String mavenPhase;

    private File mavenHomeDir;

    private MavenProject root;

    /* ----------------- Constructors --------------------------- */

    /**
     * Creates configuration for run maven's "package" phase in current directory
     */
    public RunMavenConnectorContext()
    {
        super();
        baseDir = null;
        mavenPhase = "package";
        mavenHomeDir = null;
    }

    /**
     * Creates configuration for run maven's "mavenPhase" phase in given baseDir directory
     */
    public RunMavenConnectorContext( File baseDir, String mavenPhase )
    {
        super();
        this.baseDir = baseDir;
        this.mavenPhase = mavenPhase;
        this.mavenHomeDir = null;
    }

    /**
     * Create configuration for run maven's "mavenPhase" phase in given baseDir directory
     * 
     * It uses mavenHomeDirectory to find maven's configuration files
     */
    public RunMavenConnectorContext( File baseDir, String mavenPhase, File mavenHomeDir )
    {
        super();
        this.baseDir = baseDir;
        this.mavenPhase = mavenPhase;
        this.mavenHomeDir = mavenHomeDir;
    }

    /*------------------------- Logic --------------------------- */

    public synchronized MavenProject getMavenProject() throws ConnectorException
    {
        if ( root == null )
        {
            root = prepareMavenProject();
        }
        return root;
    }

    /**
     * The method tries to path to Maven's global settings by first available options from:
     * 
     * <ol>
     * <li>mavenHomeDir/conf/settings.xml (if mavenHomeDir is not null)
     * <li>maven.home/conf/settings.xml (where maven.home is system property)</li>
     * <li>M2_HOME/conf/settings.xml (where M2_HOME is environment variable)</li>
     * <li>MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE</li>
     * </ol>
     * 
     * @return
     */
    protected File getGlobalSettingsFile()
    {
        if ( mavenHomeDir == null )
        {
            if ( System.getProperty( "maven.home" ) != null )
                return new File( System.getProperty( "maven.home" ), "conf/settings.xml" );

            if ( System.getenv( "M2_HOME" ) != null )
                return new File( System.getenv( "M2_HOME" ), "conf/settings.xml" );

            if ( getLogger() != null )
                getLogger().error(
                                   "RunMavenConnectorContext.mavenHomeDir nor maven.home variable nor M2_HOME environment variable is not set !!!" );
            return MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE;
        }
        else
        {
            return new File( mavenHomeDir, "conf/settings/xml" );
        }
    }

    /**
     * The method runs embedded maven and returns the mavenProject.
     * 
     * @return created mavenProject
     * @throws ConnectorException  
     */
    private synchronized MavenProject prepareMavenProject() throws ConnectorException
    {
        try
        {
            File projectDirectory = baseDir == null ? new File( "." ) : baseDir;

            Configuration configuration = new DefaultConfiguration();
            configuration.setUserSettingsFile( MavenEmbedder.DEFAULT_USER_SETTINGS_FILE );
            configuration.setGlobalSettingsFile( getGlobalSettingsFile() );

            ConfigurationValidationResult validationResult = MavenEmbedder.validateConfiguration( configuration );

            if ( validationResult.isValid() )
            {
                MavenEmbedder embedder;

                embedder = new MavenEmbedder( configuration );

                embedder.setLogger( new ConnectorLoggerToMavenEmbadderLoggerAdapter( getLogger() ) );

                MavenExecutionRequest request =
                    new DefaultMavenExecutionRequest().setBaseDirectory( projectDirectory ).setGoals(
                                                                                                      Arrays.asList( new String[] { mavenPhase } ) );

                MavenExecutionResult result = embedder.execute( request );

                if ( result.hasExceptions() )
                {
                    throw new ConnectorException( "Embadded maven exception",
                                                  (Exception) result.getExceptions().iterator().next() );
                }

                MavenProject project = result.getMavenProject();
                if ( getLogger() != null )
                    getLogger().info( project.getArtifactMap().toString() );
                return project;
            }
            else
            {
                throw new ConnectorException( "Embadded maven configuration is not valid" );
            }
        }
        catch ( MavenEmbedderException e )
        {
            throw new ConnectorException( e.getMessage(), e );
        }

    }

    /*------------------- Getters and Setters ----------------------*/

    /**
     * Gets the directory in which the embedded maven will be run in. 
     * The directory should contain the 'pom.xml' file.
     */
    public File getBaseDir()
    {
        return baseDir;
    }

    /**
     * Sets the directory in which the embedded maven will be run in. 
     * The directory should contain the 'pom.xml' file.
     */
    public void setBaseDir( File baseDir )
    {
        this.baseDir = baseDir;
    }

    
    /**
     * Gets the phase to which the project will be build to.  
     * @return the maven phase.
     */
    public String getMavenPhase()
    {
        return mavenPhase;
    }

    /**
     * Sets the phase to which the project will be build to.
     * 
     * @param mavenPhase that will be set up. 
     */
    public void setMavenPhase( String mavenPhase )
    {
        this.mavenPhase = mavenPhase;
    }

    /**
     * Returns the directory in which the maven's configuration file will be looked for. 
     * 
     * @return the installed maven home directory.
     */
    public File getMavenHomeDir()
    {
        return mavenHomeDir;
    }

    /**
     * Sets the directory in which the maven's configuration file will be looked for.
     * 
     * @param mavenHomeDir
     */
    public void setMavenHomeDir( File mavenHomeDir )
    {
        this.mavenHomeDir = mavenHomeDir;
    }
}
