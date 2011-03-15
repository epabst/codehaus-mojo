package org.codehaus.mojo.jpox;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Convenience base class for Jpox Mojo extensions.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public abstract class AbstractJpoxMojo extends AbstractMojo
{

    /**
     * @parameter expression="${jpox.classDir}"
     *            default-value="${project.build.outputDirectory}"
     * @required
     */
    protected File classes;

    /**
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     */
    private List classpathElements;

    /**
     * @parameter expression="${plugin.artifacts}"
     * @required
     */
    protected List pluginArtifacts;

    /**
     * @parameter expression="${mappingIncludes}" default-value="**\/*.jdo"
     */
    private String mappingIncludes;

    /**
     * @parameter expression="${mappingExcludes}" defaultValue="**\/*.jdo"
     */
    private String mappingExcludes;

    public void execute() throws MojoExecutionException
    {
        if ( !classes.exists() )
            throw new MojoExecutionException( "Specified classes directory '" + classes.getAbsolutePath()
                            + "' is not available." );

        List files = findMappingFiles();

        if ( files.size() == 0 )
        {
            getLog().warn( "No files to run Jpox tool '" + getToolName() + "'" );

            return;
        }

        getLog().debug( "Classes Dir is : " + classes.getAbsolutePath() );

        URL log4jProperties = this.getClass().getResource( "/log4j.configuration" );

        try
        {
            executeJpoxTool( pluginArtifacts, log4jProperties, files );
        }
        catch ( CommandLineException e )
        {
            throw new MojoExecutionException( "Error while executing the JPox tool '" + getToolName() + "'.", e );
        }
    }

    /**
     * Locates and builds a list of all JDO mapping (.jdo) files under the build
     * output directory.
     * 
     * @throws MojoExecutionException
     */
    protected List findMappingFiles() throws MojoExecutionException
    {
        List files;

        try
        {
            files = FileUtils.getFiles( classes, mappingIncludes, mappingExcludes );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error while scanning for mapping files in '"
                            + classes.getAbsolutePath() + "'.", e );
        }

        return files;
    }

    /**
     * <p>
     * Return the set of classpath elements, ensuring that {@link #classes}
     * location is first, and that no entry is duplicated in the classpath.
     * </p>
     * 
     * <p>
     * The ability of the user to specify an alternate {@link #classes} location
     * facilitates the need for this. <br>
     * Example: Users that want to JpoxEnhance their test classes.
     * </p>
     * 
     * @return the list of unique classpath elements.
     */
    protected List getUniqueClasspathElements()
    {
        List ret = new ArrayList();
        ret.add( this.classes.getAbsolutePath() );
        Iterator it = classpathElements.iterator();
        while ( it.hasNext() )
        {
            String pathelem = (String) it.next();
            if ( !ret.contains( new File( pathelem ).getAbsolutePath() ) )
            {
                ret.add( pathelem );
            }
        }
        return ret;
    }

    /**
     * Template method expected to be implemented by extensions. This acts as
     * hook to invoke custom Jpox tool.
     * 
     * @param pluginArtifacts
     * @param log4jProperties
     * @param files
     */
    protected abstract void executeJpoxTool( List pluginArtifacts, URL log4jProperties, List files )
        throws CommandLineException, MojoExecutionException;

    /**
     * Returns the Jpox tool name being invoked by this plugin's execution.
     * 
     * @return Jpox tool/utility name being invoked.
     */
    protected abstract String getToolName();

}
