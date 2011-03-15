package org.codehaus.mojo.buildinfo;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.buildinfo.util.BuildInfoGenerator;


/**
 * Write the environment information for the current build execution to an XML file.
 * 
 * @goal java-write
 * @phase package
 * @author jdcasey
 *
 */
public class WriteBuildInfoMojo
    extends AbstractMojo
{
    
    /**
     * Determines which system properties are added to the buildinfo file.
     * @parameter expression="${buildinfo.systemProperties}"
     */
    private String systemProperties;
    
    /**
     * The location to write the buildinfo file.
     * @parameter default-value="${project.build.directory}/${project.artifactId}-${project.version}-buildinfo.xml"
     * @required
     */
    private File outputFile;
    
    public void execute() throws MojoExecutionException
    {
        BuildInfoGenerator buildInfoGenerator = new BuildInfoGenerator();
        
        try
        {
            buildInfoGenerator.writeXml( systemProperties, outputFile );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error writing buildinfo XML file. Reason: " + e.getMessage(), e );
        }
    }

}
