package org.codehaus.mojo.cis.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.cis.core.CisCoreErrorMessage;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.HTMLGeneratorBean;


/**
 * A Mojo for running the HtmlGenerator.
 *
 * @goal htmlGenerator
 * @phase generate-resources
 */
public class HtmlGeneratorMojo extends AbstractCisMojo
{
    /**
     * The optional project name. If present, this is used in
     * conjunction with the {@link #getCisHomeDir() CIS home directory}
     * to calculate the project directory.
     * @parameter
     */
    private String projectName;

    /**
     * The project directory. If the project directory is set,
     * then it is used to provide defaults for the {@link #getXmlDir()
     * XML directory}, {@link #getHtmlDir() HTML directory},
     * {@link #getLogDir() log directory}, and the {@link #getAccessPathDir()}
     * access path directory.
     * @parameter
     */
    private File projectDir;

    /**
     * The optional directory for reading the XML layouts.
     * If missing, the directory is derived from the project directory.
     * @parameter
     */
    private File xmlDir;

    /**
     * The HTML directory, where the generated files are being created.
     * This parameter is optional. If you don't specify it, then the
     * project directory is used.
     * @parameter
     */
    private File htmlDir;

    /**
     * The log directory, where the log files are being created.
     * This parameter is optional. If you don't specify it, then the
     * subdirectory "log" of the project directory is used.
     * @parameter
     */
    private File logDir;

    /**
     * Sets the access path directory, where the acesspath files are being
     * created. This parameter is optional. If you don't specify it, then
     * the subdirectory "access" of the project directory is used.
     * @parameter
     */
    private File accessPathDir;

    /**
     * Returns the optional project name. If present, this is used in
     * conjunction with the {@link #getCisHomeDir() CIS home directory}
     * to calculate the project directory.
     * @parameter
     */
    protected String getProjectName()
    {
        return projectName;
    }

    /**
     * Returns the optional project directory. If the project directory is set,
     * then it is used to provide defaults for the {@link #getXmlDir()
     * XML directory}, {@link #getHtmlDir() HTML directory},
     * {@link #getLogDir() log directory}, and the {@link #getAccessPathDir()}
     * access path directory.
     */
    protected File getProjectDir() {
        return projectDir;
    }

    /**
     * Returns the optional directory for reading the XML layouts.
     * If missing, the directory is derived from the project directory.
     */
    protected File getXmlDir()
    {
        return xmlDir;
    }

    /**
     * Returns the HTML directory, where the generated files are being
     * created. This parameter is optional. If you don't specify it, then
     * the project directory is used.
     */
    public File getHtmlDir()
    {
        return htmlDir;
    }

    /**
     * Returns the log directory, where the log files are being
     * created. This parameter is optional. If you don't specify it, then
     * the subdirectory "log" of the project directory is used.
     */
    public File getLogDir()
    {
        return logDir;
    }

    /**
     * Returns the access path directory, where the acesspath files are being
     * created. This parameter is optional. If you don't specify it, then
     * the subdirectory "access" of the project directory is used.
     */
    public File getAccessPathDir()
    {
        return accessPathDir;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        File projectDirectory = getProjectDir();
        File cisHomeDirectory = getCisHomeDir();
        if ( projectDirectory == null  &&  cisHomeDirectory != null)
        {
            final String projName = getProjectName();
            if ( projName != null )
            {
                projectDirectory = new File( cisHomeDirectory, projName );
            }
        }
        File xmlDirectory = getXmlDir();
        if ( xmlDirectory == null  &&  projectDirectory != null )
        {
            xmlDirectory = new File( projectDirectory, "xml" );
        }
        File htmlDirectory = getHtmlDir();
        if ( htmlDirectory == null  &&  projectDirectory != null ) {
            htmlDirectory = projectDirectory;
        }
        File logDirectory = getLogDir();
        if ( logDirectory == null  &&  projectDirectory != null ) {
            logDirectory = new File( projectDirectory, "log" );
        }
        File accessPathDirectory = getAccessPathDir();
        if ( accessPathDirectory == null  &&  projectDirectory != null )
        {
            accessPathDirectory = new File( projectDirectory, "accesspath" );
        }
        HTMLGeneratorBean htmlGenerator = new HTMLGeneratorBean();
        htmlGenerator.setAccessPathDir( accessPathDirectory );
        htmlGenerator.setHtmlDir( htmlDirectory );
        htmlGenerator.setLogDir( logDirectory );
        htmlGenerator.setXmlDir( xmlDirectory );
        htmlGenerator.setCisUtils( newCisUtils() );
        htmlGenerator.setCisHomeDir( cisHomeDirectory );
        try
        {
            htmlGenerator.execute();
        }
        catch ( CisCoreErrorMessage e )
        {
            throw new MojoFailureException( e.getMessage(), e.getCause() );
        }
        catch ( CisCoreException e )
        {
            throw new MojoExecutionException( e.getMessage(), e.getCause() );
        }
    }
}
