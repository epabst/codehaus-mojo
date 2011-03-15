package org.codehaus.mojo.was6;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dom4j.Document;

/**
 * Creates Java classes and deployment descriptor templates from a Web Services Description Language (WSDL) file.
 * <p>
 * See <a href="http://www.ibm.com/developerworks/websphere/library/techarticles/0606_ulbricht/0606_ulbricht.html">
 * Developerworks article for background</a>.
 * 
 * @goal wsdl2java
 * @phase generate-resources
 * @requiresDependencyResolution runtime
 * @author karltdav
 * @since 1.2
 */
public class Wsdl2java
    extends AbstractWas6Mojo
{

    /**
     * The WSDL file.
     * 
     * @parameter
     * @required
     */
    private File wsdlFile;

    /**
     * Where to place the generated resources.
     * 
     * @parameter default-value="${project.build.directory}/generated-resources/was6-maven-plugin/wsdl2java"
     * @required
     */
    private File output;

    /**
     * Enable the hashcode/equal generation.
     * 
     * @parameter default-value="true"
     * @optional
     */
    private Boolean generateHashCodeEquals;

    /**
     * HAS NO MEANING WHEN USING FILE AS WSDLURL: Timeout value for obtaining the {@linkplain #wsdlUrl}.
     * 
     * @parameter
     * @optional private Integer timeout;
     */

    /**
     * Undocumented by IBM. Among valid values are "EJB".
     * 
     * @parameter
     */
    private String container;

    /**
     * IBM hasn't documented this AFAIK.
     * 
     * @parameter
     */
    private String deployScope;

    /**
     * Set the resolver to use.
     * 
     * @parameter
     */
    private Boolean generateResolver;

    /**
     * Have generated beans implement java.io.Serializable.
     * 
     * @parameter default-value="true"
     */
    private Boolean implementSerializable;

    /**
     * The genJava option. Allowed values: "No", "IfNotExists" (default), or "Overwrite"
     * 
     * @parameter default-value="IfNotExists"
     */
    private String generateJavaCode;

    /**
     * Undocumented by IBM
     * 
     * @parameter
     */
    private String generateXml;

    /**
     * Input mapping file.
     * 
     * @parameter
     */
    private File inputMappingFile;

    /**
     * Setter for noDataBinding Force everything to be mapped to a generic object (i.e. SOAPElement).
     * 
     * @parameter default-value="true"
     */
    private Boolean noDataBinding;

    /**
     * Do not wrap arrays.
     * 
     * @parameter default-value="false"
     */
    private Boolean doNotWrapArrays;

    /**
     * Do not wrap Operations.
     * 
     * @parameter default-value="false"
     */
    private Boolean doNotWrapOperations;

    /**
     * Set the NStoPkg mappings filename.
     * 
     * @parameter
     */
    private File nsToPkgMappingFile;

    /**
     * Set the scenario option used with genJava. Specifies how to refine generation behaviour. Allowed values: "normal"
     * to get default behaviour, "wrdTopDown" to get WRD-specific action.
     * 
     * @parameter default-value="normal"
     */
    private String scenario;

    /**
     * The J2EE development role that identifies which files to generate. Among valid values are "develop-server".
     * 
     * @parameter
     */
    private String role;

    /**
     * What to include of the generated resources.
     * 
     * @parameter 
     */
    private List includes;

    /**
     * What to exclude from the generated resources.
     * 
     * @parameter
     */
    private List excludes;
    
    /**
     * Set to true to filter the generated resources
     * 
     * @parameter default-value="false"
     */
    private boolean useFiltering;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        try
        {
            configureTaskAttribute( document, "url", wsdlFile.toURL() );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        configureTaskAttribute( document, "genEquals", generateHashCodeEquals );
        // configureTaskAttribute( document, "timeout", timeout );
        configureTaskAttribute( document, "container", container );
        configureTaskAttribute( document, "deployScope", deployScope );
        configureTaskAttribute( document, "generateResolver", generateResolver );
        configureTaskAttribute( document, "genImplSer", implementSerializable );
        configureTaskAttribute( document, "genJava", generateJavaCode );
        configureTaskAttribute( document, "genXML", generateXml );
        configureTaskAttribute( document, "inputMappingFile", inputMappingFile );
        configureTaskAttribute( document, "noDataBinding", noDataBinding );
        configureTaskAttribute( document, "noWrappedArrays", doNotWrapArrays );
        configureTaskAttribute( document, "fileNStoPkg", nsToPkgMappingFile );
        configureTaskAttribute( document, "scenario", scenario );
        configureTaskAttribute( document, "noWrappedOperations", doNotWrapOperations );
        configureTaskAttribute( document, "role", role );
        configureTaskAttribute( document, "output", output );
        configureTaskAttribute( document, "classpath", getRuntimeClasspath() );
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        super.execute();
        addResources();
    }

    /**
     * Adds the selected generated resources to the project.
     * 
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void addResources()
        throws MojoExecutionException, MojoFailureException
    {
        Resource resource = new Resource();
        resource.setExcludes( excludes );
        resource.setIncludes( includes );
        resource.setDirectory( output.getAbsolutePath() );
        resource.setFiltering( useFiltering );
        
        getLog().debug( "Adding resource: " + resource );
        getMavenProject().getResources().add( resource );
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
            dependencySet.addAll( getMavenProject().getCompileClasspathElements() );
            dependencySet.addAll( getMavenProject().getRuntimeClasspathElements() );
            String compileClasspath = StringUtils.join( dependencySet, File.pathSeparator );

            return compileClasspath;
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsdl2java";
    }

}
