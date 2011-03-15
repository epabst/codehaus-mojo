package org.codehaus.mojo.jlint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.doxia.tools.SiteTool;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceLoader;

/** 
 * @goal jlint
 * @phase process-classes
 */
public class JlintCheckMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * <p>
     * Specifies the location of the XML configuration to use.
     * </p>
     * <p>
     * Potential values are a filesystem path, a URL, or a classpath resource. This parameter expects that the contents
     * of the location conform to the xml format (Jlint <a
     * href="http://checkstyle.sourceforge.net/config.html#Modules">Checker module</a>) configuration of rulesets.
     * </p>
     * <p>
     * This parameter is resolved as resource, URL, then file. If successfully resolved, the contents of the
     * configuration is copied into the <code>${project.build.directory}/jlint-config.xml</code> file before being
     * passed to Jlint as a configuration.
     * </p>
     * <p>
     * There is 1 predefined ruleset.
     * </p>
     * <ul>
     * <li><code>config/jlint_default_config.xml</code>: Sun Checks.</li>
     * </ul>
     * 
     * @parameter expression="${jlint.config.location}" default-value="config/jlint-default-config.xml"
     */
    private String configLocation;

    /**
     * SiteTool.
     * 
     * @since 2.2
     * @component role="org.apache.maven.doxia.tools.SiteTool"
     * @required
     * @readonly
     */
    protected SiteTool siteTool;

    /**
     * Specifies the path and filename to save the jlint output. The format of the output file is determined by the
     * <code>outputFileFormat</code> parameter.
     * 
     * @parameter expression="${jlint.output.file}" default-value="${project.build.directory}/jlint-violations.xml"
     */
    private File outputFile;

    /**
     * Specifies the format of the output to be used when writing to the output file. Valid values are "plain" and
     * "xml".
     * 
     * @parameter expression="${jlint.output.format}" default-value="xml"
     */
    private String outputFileFormat;

    /**
     * Specifies if the build should fail upon a violation.
     * 
     * @parameter default-value="false"
     */
    private boolean failsOnError;

    /**
     * Specifies the location of the source directory to be used for Jlint.
     * 
     * @parameter default-value="${project.build.sourceDirectory}"
     * @required
     */
    private File sourceDirectory;

    /**
     * Specifies the location of the test source directory to be used for Jlint.
     * 
     * @parameter default-value="${project.build.testSourceDirectory}"
     * @since 2.2
     */
    private File testSourceDirectory;

    /**
     * Include or not the test source directory to be used for Jlint.
     * 
     * @parameter default-value="${false}"
     * @since 2.2
     */
    private boolean includeTestSourceDirectory;

    /**
     * The Maven Project Object.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Output errors to console.
     * 
     * @parameter default-value="false"
     */
    private boolean consoleOutput;

    /**
     * Link the violation line numbers to the source xref. Will link automatically if Maven JXR plugin is being used.
     * 
     * @parameter expression="${linkXRef}" default-value="true"
     * @since 2.1
     */
    private boolean linkXRef;

    /**
     * Location of the Xrefs to link to.
     * 
     * @parameter default-value="${project.reporting.outputDirectory}/xref"
     */
    private File xrefLocation;

    /**
     * The file encoding to use when reading the source files. If the property <code>project.build.sourceEncoding</code>
     * is not set, the platform default encoding is used. <strong>Note:</strong> This parameter always overrides the
     * property <code>charset</code> from Checkstyle's <code>TreeWalker</code> module.
     * 
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     * @since 2.2
     */
    private String encoding;

    /**
     * @component
     * @required
     * @readonly
     */
    private SiteRenderer siteRenderer;

    /**
     * @component
     * @required
     * @readonly
     */
    private ResourceManager locator;

    public void execute()
        throws MojoExecutionException
    {
        getLog().debug( "Started maven-jlint-plugin: execute() method" );
        getLog().info( "Running Maven-Jlint-Plugin : " );

        // Setup ResourceManager
        locator.addSearchPath( FileResourceLoader.ID, project.getFile().getParentFile().getAbsolutePath() );
        locator.addSearchPath( "url", "" );
        locator.setOutputDirectory( new File( project.getBuild().getDirectory() ) );

        getLog().info( "Configuration File used: " + configLocation );

        // File f = outputDirectory;
        JlintViolationHandler violationHandler = new JlintViolationHandler( getLog() );
        violationHandler.setTargetDir( outputDirectory.toString() );
        violationHandler.setClassesDir( System.getProperty( "file.separator" ) + Constants.JLINT_CLASSES_DIR );

        JlintConfiguration jlintConfig = new JlintConfiguration( locator, configLocation, getLog() );

        String cmdDisableCategories = jlintConfig.getCategoriesToDisable( violationHandler.getDefaultMessageList() );

        String cmd =
            Constants.JLINT_CMD + cmdDisableCategories + outputDirectory.toString()
                + System.getProperty( "file.separator" ) + Constants.JLINT_CLASSES_DIR;

        getLog().info(
                       "Classes Directory : " + outputDirectory.toString() + System.getProperty( "file.separator" )
                           + Constants.JLINT_CLASSES_DIR );
        getLog().debug( "Disabled Categories: " + cmdDisableCategories );
        getLog().info( "Executing Jlint with command : [" + cmd + "]" );

        // Open output files for writing
        File xmlFile = new File( outputDirectory, Constants.JLINT_XML_OUTPUT_FILENAME );
        File txtFile = new File( outputDirectory, Constants.JLINT_TXT_OUTPUT_FILENAME );
        File errFile = new File( outputDirectory, Constants.JLINT_ERROR_FILENAME );

        String s = null;

        try
        {

            // run the Jlint command on the "classes" directory

            getLog().debug( "execute: About to execute jlint command" );

            Process p = Runtime.getRuntime().exec( cmd );

            /*
             * BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); BufferedReader
             * stdError = new BufferedReader(new InputStreamReader(p.getErrorStream())); outputXmlFile = new
             * BufferedWriter(new FileWriter(xmlFile)); outputTxtFile = new BufferedWriter(new FileWriter(txtFile));
             * errorFile = new BufferedWriter(new FileWriter(errFile));
             */

            FileOutputStream outputXmlFile = new FileOutputStream( xmlFile );
            FileOutputStream outputTxtFile = new FileOutputStream( txtFile );
            FileOutputStream errorFile = new FileOutputStream( errFile );

            // Create the output file
            /*
             * outputXmlFile.write(Constants.XML_HEADER); outputXmlFile.newLine(); outputXmlFile.newLine();
             * outputXmlFile.write(Constants.ROOT_START_TAG);
             */

            StreamGobbler inputGobbler = new StreamGobbler( p.getInputStream(), outputXmlFile, outputTxtFile, "INPUT" );

            inputGobbler.setViolationHandler( violationHandler );

            StreamGobbler errorGobbler = new StreamGobbler( p.getErrorStream(), errorFile, "ERROR" );

            getLog().info( "Reading the standard input and error for Jlint" );
            inputGobbler.start();
            errorGobbler.start();

            int exitVal = p.waitFor();

            getLog().info( "Jlint exited with status value: " + String.valueOf( exitVal ) );

            if ( exitVal != 0 )
            {
                getLog().error( "Jlint did not execute properly. Check " + Constants.JLINT_ERROR_FILENAME + " file." );
            }

            inputGobbler.join();
            errorGobbler.join();

            getLog().info( "** Jlint Report: **" );
            getLog().info( "-------------------" );
            getLog().info(
                           "JLint Violations (if any): " + String.valueOf( inputGobbler.getNofLines() )
                               + " violations reported." );
            getLog().info( "Jlint Error Messages (if any): " );
            for ( String msg : errorGobbler.getErrorMsgList() )
            {
                getLog().error( msg );
            }
            getLog().info( "----End Report-----" );

        }
        catch ( IOException e )
        {
            System.out.println( "JLint: Exception Occured: " );
            e.printStackTrace();
            // System.exit(-1);
        }
        catch ( InterruptedException e )
        {
            System.out.println( "JLint: Exception Occured: " );
            e.printStackTrace();
        }

    }
}
