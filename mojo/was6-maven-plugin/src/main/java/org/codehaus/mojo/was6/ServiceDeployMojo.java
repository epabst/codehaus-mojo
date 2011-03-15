package org.codehaus.mojo.was6;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

/**
 * Executes the ServiceDeploy command against an archive file (Ear, Zip or Jar) to produce
 * an ear file that can be deployed on Process Server.
 * <P> 
 * Reference Documentation can be found <a href="http://publib.boulder.ibm.com/infocenter/dmndhelp/v6r1mx/index.jsp?topic=/com.ibm.websphere.wbpmcore.javadoc.610.doc/web/apidocs/com/ibm/websphere/ant/tasks/ServiceDeployTask.html">IBM WebSphere Enterprise Service Bus, Version 6.1, IBM WebSphere Process Server, Version 6.1</a>.
 * 
 * @goal servicedeploy
 * @phase package
 * 
 * @author <a href="mailto:chrisg@warpspeed.com.au">Chris Graham [WarpSpeed Computers]</a>
 */
public class ServiceDeployMojo extends AbstractWas6Mojo
{
    /**
     * The scaModule attribute is required and specifies the .jar, .zip or .ear file that
     * contains the application to be deployed. If the command is not issued from the path
     * in which the file resides, this must be the full path for the file.
     * The .zip file can be either a nested archive that contains jars of Libraries
     * or Modules or it can be an Eclipse Project Interchange format file.
     * 
     * @parameter
     * @required
     */
    private File scaModule;

    /**
     * The outputApplication attribute is optional and controls the name and location of
     * the generated J2EE ear file. If this attribute is not specified, then the ear file
     * will be named according to the sca module name and will be generated in the location
     * from which the ANT process was launched.
     * 
     * @parameter expression="${was6.outputApplication}" default-value="${project.artifact.file}"
     */
    private File outputApplication;

    /**
     * The classPath attribute is optional and controls which external archives (jar, rar, and zip)
     * should be appended to the classpath. By default, no external archives are used during deployment.
     * 
     * @parameter
     */
    private String classPath;

    /**
     * The noJ2EEDeploy attribute is optional and controls whether the J2EE deployers, including
     * ejbDeploy, should be skipped during deployment. The J2EE deployers will execute by default.
     * 
     * @parameter
     */
    private boolean noJ2EEDeploy;

    /**
     * The freeForm attribute is optional and controls whether jsp and html files will be copied from the
     * optional j2ee folder of an sca jar file and copied to the generated war file. By default, jsp and
     * html files are not copied into the war file.
     * 
     * @parameter
     */
    private boolean freeForm;

    /**
     * The cleanStagingModules attribute is optional and controls whether imported staging modules should
     * be deleted before running the deployer. By default, imported staging modules are not deleted.
     * 
     * @parameter
     */
    private boolean cleanStagingModules;

    /**
     * The keep attribute is optional and controls whether the generated eclipse workspace should be
     * preserved when the deployer task is complete. By default, the generated eclipse workspace is
     * deleted after deployment.
     * 
     * @parameter
     */
    private boolean keep;

    /**
     * The ignoreErrors attribute is optional and controls whether an ear file should be generated
     * despite validation errors. By default, an ear file will not be generated if validation errors
     * were flagged during deployment.
     * 
     * @parameter
     */
    private boolean ignoreErrors;

    /**
     * The progressMonitor attribute is optional and controls how progress should be displayed to
     * the console. Legal values include none, meter, or message. The default progressMonitor is none.
     * 
     * @parameter default-value="none"
     */
    private String progressMonitor;

    /**
     * The clean attribute attribute is optional and is passed to eclipse so that eclipse will
     * regenerate its stored plugin configuration.
     * 
     * @parameter
     */
    private boolean clean;

    /**
     * The file that contains plugin-specific trace enablement settings.
     * 
     * The debug attribute is optional and may be used to specify a trace .options file to
     * enable eclipse-based tracing.
     * 
     * @parameter
     */
    private File debug;

    /**
     * The file that contains plugin-specific trace enablement settings.
     * 
     * The javaDebug attribute is optional and may be used to indicate that all debug
     * information should be included in generated class files.
     * 
     * @parameter
     */
    private File javaDebug;

    /**
     * The vmArgs attribute is optional and may be used to specify jvm arguments for the deploy process.
     * They are space separated. 
     * 
     * @parameter
     */
    private String vmArgs;

    /**
     * The fileEncoding attribute is optional and specifies the default file encoding that should be used by eclipse. 
     * 
     * @parameter expression="${project.build.sourceEncoding}"
     */
    private String fileEncoding;

    /**
     * The skipXsdValidate attribute is optional and specifies that the XSD schema validation will be skipped.
     * <strong>
     * This parameter is not available in the base product, as it was introduced in a later fixpack as a workaround
     * for various issues.
     * </strong>
     * 
     * @parameter
     */
    private boolean skipXsdValidate;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document ) throws MojoExecutionException
    {
        super.configureTaskAttribute( document, "scaModule", scaModule );
        super.configureTaskAttribute( document, "workingDirectory", getWorkingDirectory() );
        super.configureTaskAttribute( document, "outputApplication", outputApplication );
        super.configureTaskAttribute( document, "noJ2EEDeploy", Boolean.toString( noJ2EEDeploy ) );
        super.configureTaskAttribute( document, "freeForm", Boolean.toString( freeForm ) );
        super.configureTaskAttribute( document, "cleanStagingModules", Boolean.toString( cleanStagingModules ) );
        super.configureTaskAttribute( document, "keep", Boolean.toString( keep ) );
        super.configureTaskAttribute( document, "ignoreErrors", Boolean.toString( ignoreErrors ) );
        super.configureTaskAttribute( document, "classPath", classPath );
        super.configureTaskAttribute( document, "progressMonitor", progressMonitor );
        super.configureTaskAttribute( document, "fileEncoding", fileEncoding );
        super.configureTaskAttribute( document, "vmArgs", vmArgs );
        super.configureTaskAttribute( document, "debug", debug );
        super.configureTaskAttribute( document, "javaDebug", javaDebug );
        super.configureTaskAttribute( document, "clean", Boolean.toString( clean ) );
        if ( skipXsdValidate ) //is only defined for newer releases
        {
            super.getTaskElement( document ).addAttribute( "skipXsdValidate", Boolean.toString( skipXsdValidate ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "servicedeploy";
    }

}
