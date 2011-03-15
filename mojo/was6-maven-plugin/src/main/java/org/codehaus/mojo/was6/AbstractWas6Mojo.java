
package org.codehaus.mojo.was6;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Abstract base class for running ws_ant.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public abstract class AbstractWas6Mojo
    extends AbstractMojo
{
    /**
     * Root install location of WebSphere 6.1
     * 
     * @parameter expression="${was6.wasHome}" default-value="${env.WAS_HOME}"
     * @required
     */
    private File wasHome;

    /**
     * Working directory for plugin.
     * 
     * @parameter expression="${project.build.directory}/was6-maven-plugin"
     * @required
     */
    private File workingDirectory;

    /**
     * Specifies a verbose execution to help debug.
     * 
     * @parameter expression="${was6.verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * Fail build on errors.
     * 
     * @parameter expression="${was6.failOnError}" default-value="true"
     */
    private boolean failOnError;

    /**
     * The enclosing project.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * Access to settings.
     * 
     * @parameter default-value="${settings}"
     * @readonly
     */
    private Settings settings;

    /**
     * Optional log file to log execution of ws_ant to.
     * 
     * @parameter expression="${was6.logFile}"
     */
    private File logFile;
    
    /**
     * Optional file to log trace to.
     * 
     * @parameter expression="${was6.traceFile}"
     */
    private File traceFile;
    
    /**
     * @component
     * @required
     * @readonly
     */
    private ResourceManager resourceManager;
    
    /**
     * Skip execution.
     * @since 1.1.1
     * @parameter expression="${was6.skip}" default-value="false" 
     */
    private boolean skip;

    /**
     * Plugin working directory.
     */
    protected File getWorkingDirectory()
    {
        return workingDirectory;
    }

    /**
     * Specifies if execution should be verbose.
     * 
     * @return true if verbose, else false.
     */
    protected boolean isVerbose()
    {
        return verbose;
    }

    /**
     * Maven {@link Settings}
     * 
     * @return the settings.
     */
    protected Settings getSettings()
    {
        return settings;
    }

    /**
     * The enclosing project.
     * 
     * @return enclosing project.
     */
    protected MavenProject getMavenProject()
    {
        return mavenProject;
    }
    
    /**
     * Returns the location of the was root install point.
     * @return the root location.
     */
    protected File getWasHome()
    {
        return wasHome;
    }

    /**
     * Locates the ws_ant.sh|bat executable.
     * 
     * @return a File pointing on the executable
     */
    protected File getWsAntExecutable()
        throws MojoExecutionException
    {
        File binDir = new File( wasHome, "bin" );
        File[] candidates = binDir.listFiles( new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                dir.equals( wasHome );
                return name.startsWith( "ws_ant" );
            }
        });

        if ( candidates.length != 1 ) {
            throw new MojoExecutionException( "Couldn't find ws_ant[.sh|.bat], candidates: " + 
                                              ArrayUtils.toString( candidates ) ); 
        }
        
        File wsAnt = candidates[0];
        getLog().info( "wsAnt location: " + wsAnt.getAbsolutePath() );
        
        return wsAnt;
    }

    private File getBuildScript()
        throws MojoExecutionException
    {
        try
        {
            InputStream inputStream = resourceManager.getResourceAsInputStream( "build.xml" );
            String scriptSource = IOUtils.toString( inputStream );
            Document buildScriptDocument = DocumentHelper.parseText( scriptSource );

            configureTaskAttribute( buildScriptDocument, "wasHome", wasHome );
            configureTaskAttribute( buildScriptDocument, "failonerror", Boolean.toString( failOnError ) );
            Element projectElement = buildScriptDocument.getRootElement();
            projectElement.addAttribute( "default", getTaskName() ); //old was versions use old ant versions which require default task to be set

            configureBuildScript( buildScriptDocument );
            getWorkingDirectory().mkdirs();

            File buildScriptFile = new File( getWorkingDirectory(), "was6plugin-build." + System.currentTimeMillis() + ".xml" );
            Writer writer = new FileWriter( buildScriptFile );
            XMLWriter xmlWriter = new XMLWriter( writer, OutputFormat.createPrettyPrint() );
            xmlWriter.write( buildScriptDocument );
            xmlWriter.flush();
            xmlWriter.close();

            return buildScriptFile;
        }
        catch ( DocumentException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }
    
    /**
     * Fetches the element describing the IBM task.
     * @param document build document.
     * @return element for this mojo's task.
     * @throws MojoExecutionException should normally never happen.
     */
    protected Element getTaskElement( Document document )
        throws MojoExecutionException
    {
        Element element = (Element) document.selectSingleNode( "//target[@name='" + getTaskName() + "']/" + getTaskName() );
        if ( element == null )
        {
            throw new MojoExecutionException( "BUG: Task is not defined: " + getTaskName() );
        }
        
        return element;
    }

    /**
     * Configures task attributes.
     * 
     * @param document document containing buildscript.
     * @param attributeName attribute to (un)define
     * @param value to set, if null attribute with name attributeName will be removed, else defined with this value
     * @throws MojoExecutionException if attribute wasn't defined in build script.
     */
    protected void configureTaskAttribute( Document document, String attributeName, Object value )
        throws MojoExecutionException
    {
        Element taskElement = getTaskElement( document );
        Attribute attribute = (org.dom4j.Attribute) taskElement.selectSingleNode( "@" + attributeName );

        if ( attribute == null )
        {
            getLog().warn( "Build script does not contain attribute: " + attributeName );
            return;
            // throw new MojoExecutionException( "BUG: Build script does not contain attribute: " + attributeName );
        }

        if ( value != null )
        {
            String valueToSet = value instanceof File ? ((File) value ).getAbsolutePath() : value.toString();
            attribute.setText( valueToSet );
        }
        else
        {
            taskElement.remove( attribute );
        }
    }

    /**
     * Method to be implemented by subclasses for configuring their task.
     * 
     * @param document the build document.
     * @throws MojoExecutionException if any failure occurs.
     */
    protected abstract void configureBuildScript( Document document )
        throws MojoExecutionException;

    /**
     * Generates the commandline to be executed.
     * 
     * @return the commandline created
     * @throws MojoExecutionException if any failure occurs
     */
    private Commandline getCommandline()
        throws MojoExecutionException
    {
        File buildScript = getBuildScript();
        Commandline commandLine = new Commandline();
        commandLine.setExecutable( getWsAntExecutable().getAbsolutePath() );
        commandLine.setWorkingDirectory( mavenProject.getBuild().getDirectory() );
        if ( traceFile != null ) {
            commandLine.createArg().setLine( "-traceFile " + "\"" + traceFile.getAbsolutePath() + "\"" );
        }
        commandLine.createArg().setLine( "-buildfile " + "\"" +  buildScript.getAbsolutePath() + "\"" );
        if ( !getSettings().isInteractiveMode() && !"servicedeploy".equals( getTaskName() ) )//WPS 6.0.x uses old ant versions (?)
        {
            commandLine.createArg().setValue( "-noinput" );
        }

        if ( logFile != null )
        {
            commandLine.createArg().setLine( "-logfile \"" + logFile.getAbsolutePath() + "\"" );
        }

        if ( isVerbose() )
        {
            commandLine.createArg().setValue( "-verbose" );
            commandLine.createArg().setValue( "-debug" );
        }

        commandLine.createArg().setValue( getTaskName() );

        return commandLine;
    }

    /**
     * Defines the task name in the build script.
     * 
     * @return name of the task
     */
    protected abstract String getTaskName();

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip ) {
            getLog().info( "Skipping execution" );
        }
        if ( wasHome == null )
        {
            throw new MojoExecutionException( "wasHome not defined" );
        }
        else if ( !( wasHome.exists() || wasHome.isDirectory() ) )
        {
            throw new MojoExecutionException( wasHome.getAbsolutePath() + " does not exist or is not a directory" );
        }

        Commandline commandLine = getCommandline();

        executeCmdLine( commandLine );
    }

    /**
     * Executes a commandLine
     * 
     * @param commandline to execute
     * @throws MojoExecutionException if any failure occurs.
     */
    private void executeCmdLine( Commandline commandline )
        throws MojoFailureException
    {
        try
        {
            StreamConsumer errConsumer = getStreamConsumer( "error" );
            StreamConsumer infoConsumer = getStreamConsumer( "info" );

            getLog().debug( "Executing command line: " + StringUtils.join( commandline.getShellCommandline(), " " ) );

            int returncode = CommandLineUtils.executeCommandLine( commandline, infoConsumer, errConsumer );

            String logmsg = "Return code: " + returncode;
            if ( returncode != 0 )
            {
                throw new MojoFailureException( logmsg );
            }
            else
            {
                getLog().info( logmsg );
            }
        }
        catch ( CommandLineException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
    }

    /**
     * Creates an streamconsumer which log's to maven log.
     * 
     * @param level the log level to log to (info or error)
     * @return a {@link StreamConsumer}
     */
    private StreamConsumer getStreamConsumer( final String level )
    {
        StreamConsumer consumer = new StreamConsumer()
        {
            /*
             * (non-Javadoc)
             * 
             * @see org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.lang.String)
             */
            public void consumeLine( String line )
            {
                if ( level.equalsIgnoreCase( "info" ) )
                {
                    getLog().info( line );
                }
                else
                {
                    getLog().error( line );
                }
            }
        };

        return consumer;
    }
}
