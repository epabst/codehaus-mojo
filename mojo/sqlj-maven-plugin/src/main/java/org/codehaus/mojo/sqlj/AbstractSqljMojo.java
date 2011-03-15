package org.codehaus.mojo.sqlj;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Common superclass for sharing configuration attributes.
 * 
 * @author karltdav
 *
 */
public abstract class AbstractSqljMojo
    extends AbstractMojo
{
    /**
     * Location for generated source files.
     * 
     * @parameter expression="${sqlj.generatedSourcesDirectory}"
     *            default-value="${project.build.directory}/generated-sources/sqlj"
     */
    private File generatedSourcesDirectory;
    
    /**
     * Location for generated .ser files.
     * @parameter expression="${sqlj.generatedResourcesDirectory}"
     *            default-value="${project.build.directory}/generated-resources/sqlj"
     */
    private File generatedResourcesDirectory;
    
   
    protected File getGeneratedSourcesDirectory()
    {
        return generatedSourcesDirectory;
    }
    
    protected File getGeneratedResourcesDirectory()
    {
        return generatedResourcesDirectory;
    }

}
